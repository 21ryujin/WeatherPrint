/**
 * ------------------------------------------------------------
 * 天気予報印刷 for SUNMI V series ( and MPT-II )
 * Developed by 21ryujin
 * ------------------------------------------------------------
*/
package com.weatherprint

import android.Manifest
import android.R
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.checkSelfPermission
import com.weatherprint.ui.theme.WeatherPrintTheme
import com.weatherprint.ConstantParameters.Companion.APP_LOG_TAG
import com.weatherprint.ConstantParameters.Companion.APP_VERSION
import com.weatherprint.ConstantParameters.Companion.MPT_II_PRINTER
import com.weatherprint.ConstantParameters.Companion.NARROW_AREA_LIST
import com.weatherprint.ConstantParameters.Companion.V2_PRO_PRINTER
import com.weatherprint.DatabaseProvider.getDatabase
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

/**
 * UI：実行部
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherPrintTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BluetoothPermission()
                    BluetoothAdminPermission()
                    WeatherPrint(
                        name = APP_VERSION,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

}

/**
 * UI：メイン表示
 */
@Composable
fun WeatherPrint(name: String, modifier: Modifier = Modifier) {
    // コンテキスト取得
    val context = LocalContext.current

    // 実行確認ダイアログ表示用
    val exeVisible = remember { mutableStateOf(AppUiState().exeVisible) }

    // 準正常系ダイアログ表示用
    val alertVisible = remember { mutableStateOf(AppUiState().alertVisible) }
    val dialogText = remember { mutableStateOf(AppUiState().dialogText) }
    val dialogTitle = remember { mutableStateOf(AppUiState().dialogTitle) }

    // 印刷ボタン活性・非活性用
    val isPrintButton = remember { mutableStateOf(AppUiState().isPrintButton) }

    // 今日の天気スイッチ
    val isToday = remember { mutableStateOf(AppUiState().isToday) }
    // 明日の天気スイッチ
    val isTomorrow = remember { mutableStateOf(AppUiState().isTomorrow) }
    // 明後日の天気スイッチ
    val isAfterTomorrow = remember { mutableStateOf(AppUiState().isAfterTomorrow) }
    // 天気概況スイッチ
    val isOverview = remember { mutableStateOf(AppUiState().isOverview) }

    // 地域選択プルダウン
    val expanded = remember { mutableStateOf(AppUiState().expanded) }
    val narrowAreaKey = remember { mutableStateOf(AppUiState().narrowAreaKey) }
    val narrowAreaValue = remember { mutableStateOf(AppUiState().narrowAreaValue) }

    // 天気予報APIの取得成否確認用
    val isGetWeather = remember { mutableStateOf(false) }

    // UI状態復元判定用フラグ
    val isDataRecovery = remember { mutableStateOf(AppUiState().isDataRecovery) }

    // アプリ終了後の起動、あるいは新規インストール後の起動の場合
    //　アプリ終了後の起動→UIの状態を復元、新規インストール→DataBase
    if (!isDataRecovery.value) {
        // UIの状態をDataBaseから取得
        runBlocking {
            // Select count(*) してデータがあるかを確認
            val count = selectCount(context, 0)

            // データがあればSelectしてUI状態を復元、データがなければデフォルト値でInsert
            if (count > 0) {
                Log.i(APP_LOG_TAG, "Select UiState Databese")

                // 保存データをSelect
                val getUiState: UiState? = selectIdUiState(context, 0)

                // 今日の天気 スイッチの保存値を反映
                if (getUiState?.isToday != null) {
                    if (getUiState.isToday == true) {
                        isToday.value = true
                    } else {
                        isToday.value = false
                    }
                }

                // 明日の天気 スイッチの保存値を反映
                if (getUiState?.isTomorrow != null) {
                    if (getUiState.isTomorrow == true) {
                        isTomorrow.value = true
                    } else {
                        isTomorrow.value = false
                    }
                }

                // 明後日の天気 スイッチの保存値を反映
                if (getUiState?.isAfterTomorrow != null) {
                    if (getUiState.isAfterTomorrow == true) {
                        isAfterTomorrow.value = true
                    } else {
                        isAfterTomorrow.value = false
                    }
                }

                // 天気概要 スイッチの保存値を反映
                if (getUiState?.isOverview != null) {
                    if (getUiState.isOverview == true) {
                        isOverview.value = true
                    } else {
                        isOverview.value = false
                    }
                }

                // 地域設定 プルダウンの保存値（Key）を反映
                if (getUiState?.narrowAreaKey != null) {
                    narrowAreaKey.value = getUiState.narrowAreaKey.toString()
                }

                // 地域設定 プルダウンの保存値（Value）を反映
                if (getUiState?.narrowAreaValue != null) {
                    narrowAreaValue.value = getUiState.narrowAreaValue.toString()
                }

                // 復元フラグを復元済みにセット
                isDataRecovery.value = true
            } else {
                Log.i(APP_LOG_TAG, "Insert UiState Databese")

                // AppUiStateのデフォルト値でデータをInsert
                insertUiState(
                    context,
                    0,
                    AppUiState().isToday,
                    AppUiState().isTomorrow,
                    AppUiState().isAfterTomorrow,
                    AppUiState().isOverview,
                    AppUiState().narrowAreaKey,
                    AppUiState().narrowAreaValue
                )

                // 復元フラグを復元済みにセット（AppUiStateのデフォルト値で復元するため）
                isDataRecovery.value = true
            }
        }
    }

    // コンテンツ表示
    Column(
        modifier = modifier
            .padding(
                vertical = 0.dp,
                horizontal = 30.dp
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(
            Modifier.size(7.dp)
        )
        Text(
            text = "[ $name ]  Developed by 21ryujin",
            Modifier.align(Alignment.CenterHorizontally),
            fontSize = 18.sp
        )
        Spacer(
            Modifier.size(20.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth() // 全幅を占める
                .background(Color.LightGray) // 背景色を薄いグレーに設定
                .padding(0.dp) // パディングを追加
        ) {
            Text(
                text = "天気予報印刷",
                modifier = Modifier.align(alignment = Alignment.Center),
                fontSize = 20.sp,
                lineHeight = 40.sp
            )
        }
        Spacer(
            Modifier.size(30.dp)
        )
        // 印刷ボタン
        Button(
            onClick = {
                // 印刷ボタン非活性
                isPrintButton.value = false

                // 実行確認ダイアログ表示
                exeVisible.value = true
            },
            enabled = isPrintButton.value,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .size(150.dp, 48.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "印刷する",
                fontSize = 20.sp
            )
        }
        Spacer(
            Modifier.size(40.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth() // 全幅を占める
                .background(Color.LightGray) // 背景色を薄いグレーに設定
                .padding(0.dp) // パディングを追加
        ) {
            Text(
                text = "印刷設定",
                modifier = Modifier.align(alignment = Alignment.Center),
                fontSize = 20.sp,
                lineHeight = 40.sp
            )
        }
        Spacer(
            Modifier.size(10.dp)
        )
        // 今日の天気
        TodayWeather (
            checked = isToday.value,
            onCheckChanged = { isToday.value = it }
        )
        Spacer(
            Modifier.size(5.dp)
        )
        // 明日の天気
        TomorrowWeather (
            checked = isTomorrow.value,
            onCheckChanged = { isTomorrow.value = it }
        )
        Spacer(
            Modifier.size(5.dp)
        )
        // 明後日の天気
        AfterTomorrowWeather (
            checked = isAfterTomorrow.value,
            onCheckChanged = { isAfterTomorrow.value = it }
        )
        Spacer(
            Modifier.size(5.dp)
        )
        // 天気概況
        WeatherOverview (
            checked = isOverview.value,
            onCheckChanged = { isOverview.value = it }
        )
        Spacer(
            Modifier.size(30.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth() // 全幅を占める
                .background(Color.LightGray) // 背景色を薄いグレーに設定
                .padding(0.dp) // パディングを追加
        ) {
            Text(
                text = "地域設定",
                modifier = Modifier.align(alignment = Alignment.Center),
                fontSize = 20.sp,
                lineHeight = 40.sp
            )
        }
        Spacer(
            Modifier.size(20.dp)
        )
        // 地域選択
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "地域選択",
                fontSize = 19.sp
            )
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .size(190.dp, 40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .border(BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(4.dp))
                    .clickable { expanded.value = !expanded.value },
            ) {
                Text(
                    text = narrowAreaKey.value + narrowAreaValue.value,
                    modifier = Modifier.padding(start = 10.dp)
                )
                Icon(
                    Icons.Filled.ArrowDropDown, "contentDescription",
                    Modifier.align(Alignment.CenterEnd)
                )
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    NARROW_AREA_LIST.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Text("${item.key}${item.value}")
                            },
                            onClick = {
                                narrowAreaKey.value = item.key
                                narrowAreaValue.value = item.value
                                expanded.value = false
                            }
                        )
                    }
                }
            }
        }
        Spacer(
            Modifier.size(30.dp)
        )
    }

    // 実行確認ダイアログ表示
    if (exeVisible.value) {
        AlertDialog(
            icon = {
                Icon(Icons.Default.Info, contentDescription = "Info Icon")
            },
            onDismissRequest = {
                exeVisible.value = false
                isPrintButton.value = true
                return@AlertDialog
            },
            title = {
                Text("印刷を実行します")
            },
            text = {
                Text(
                    text = dialogText(
                        isToday.value,
                        isTomorrow.value,
                        isAfterTomorrow.value,
                        isOverview.value,
                        narrowAreaKey.value,
                        narrowAreaValue.value
                        ),
                    fontSize = 18.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // UI関連のチェック
                        if(!isToday.value && !isTomorrow.value && !isAfterTomorrow.value) {
                            dialogTitle.value = "印刷設定"
                            dialogText.value = "今日の天気・明日の天気・明後日の天気のうち、１つ以上をONにしてください"
                            alertVisible.value = true
                            return@TextButton
                        } else if (narrowAreaKey.value.length != 6) {
                            dialogTitle.value = "地域設定"
                            dialogText.value = "都道府県内の地域を選択してください"
                            alertVisible.value = true
                            return@TextButton
                        }

                        // ネットワーク接続チェック
                        val manager =
                            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)

                        if (capabilities != null) {
                            if ( capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                                Log.i(APP_LOG_TAG, "ネットワーク状態：Wi-Fi接続中")
                            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                                Log.i(APP_LOG_TAG, "ネットワーク状態：モバイル回線接続中")
                            } else {
                                Log.i(APP_LOG_TAG, "ネットワーク状態：その他の回線接続中")
                            }
                        } else {
                            dialogTitle.value = "インターネット接続"
                            dialogText.value = "モバイル回線、もしくはWi-Fiの接続状態を確認してください"
                            alertVisible.value = true
                            return@TextButton
                        }

                        // Bluetoothの状態判定用
                        var isBluetoothPermission: Boolean = true
                        var isLocationPermission: Boolean = true
                        var isBluetoothSupport: Boolean = true
                        var isBluetoothEnable: Boolean = true
                        var findDevice: Boolean = false

                        // Bluetoothの権限チェック
                        if (checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                            ||
                            checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                        ) {
                            isBluetoothPermission = false
                        }

                        // 位置情報の権限チェック
                        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            ||
                            checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        ) {
                            isLocationPermission = false
                        }

                        val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
                        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

                        // Bluetooth対応チェック
                        if (bluetoothAdapter == null) {
                            isBluetoothSupport = false
                        }

                        // Bluetooth On/Off チェック
                        if (bluetoothAdapter?.isEnabled == false) {
                            isBluetoothEnable = false
                        }

                        // チェック結果に合わせてダイアログ表示
                        if (!isBluetoothPermission) {
                            dialogTitle.value = "Bluetooth接続"
                            dialogText.value = "Bluetooth接続を許可してください"
                            alertVisible.value = true
                            return@TextButton
                        } else if(!isLocationPermission) {
                            dialogTitle.value = "位置情報取得"
                            dialogText.value = "位置情報取得を許可してください"
                            alertVisible.value = true
                            return@TextButton
                        } else if(!isBluetoothSupport) {
                            dialogTitle.value = "Bluetooth接続"
                            dialogText.value = "Bluetoothに対応していません"
                            alertVisible.value = true
                            return@TextButton
                        } else if(!isBluetoothEnable) {
                            dialogTitle.value = "Bluetooth接続"
                            dialogText.value = "BluetoothをONにしてください"
                            alertVisible.value = true
                            return@TextButton
                        }

                        // UIの状態を保存（Databese Update）
                        runBlocking {
                            Log.i(APP_LOG_TAG, "Updaste UiState Databese")
                            updateUiState(
                                context,
                                0,
                                isToday.value,
                                isTomorrow.value,
                                isAfterTomorrow.value,
                                isOverview.value,
                                narrowAreaKey.value,
                                narrowAreaValue.value
                            )
                        }

                        // 天気予報取得実行
                        Log.i(APP_LOG_TAG, "天気予報API リクエスト")
                        var weatherData = requestWeatherData(narrowAreaKey.value)

                        // UIの選択状態をdata classに代入
                        weatherData?.isToday = isToday.value
                        weatherData?.isTomorrow = isTomorrow.value
                        weatherData?.isAfterTomorrow = isAfterTomorrow.value
                        weatherData?.isOverview = isOverview.value
                        weatherData?.narrowArea = narrowAreaKey.value

                        // 天気予報取得に失敗した場合
                        if (weatherData == null) {
                            dialogTitle.value = "天気予報取得エラー"
                            dialogText.value = "天気予報を取得できませんでした"
                            alertVisible.value = true
                            return@TextButton
                        }

                        // 内蔵プリンタをサーチして接続＆印刷処理
                        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
                        pairedDevices?.forEach { device ->
                            val deviceName = device.name
                            val deviceHardwareAddress = device.address // MAC address
                            Log.i(APP_LOG_TAG, "検知デバイス：$deviceName -> $deviceHardwareAddress")

                            if (deviceName == V2_PRO_PRINTER || deviceName == MPT_II_PRINTER) {
                                // 内蔵プリンタを検知した場合
                                findDevice = true
                                Log.i(APP_LOG_TAG, "接続デバイス：$deviceName -> $deviceHardwareAddress")

                                // 印刷ボタン活性・ダイアログ消去
                                isPrintButton.value = true
                                exeVisible.value = false

                                // Bluetooth接続処理＆印刷処理
                                try {
                                    val connectThread = ConnectThread(device, weatherData, deviceName)
                                    connectThread.start()
                                } catch(e: Exception) {
                                    Log.e(APP_LOG_TAG, e.toString())
                                    dialogTitle.value = "プリンタエラー"
                                    dialogText.value = "内蔵プリンタが見つかりません"
                                    alertVisible.value = true
                                    return@TextButton
                                }
                            }
                        }
                        // 内蔵プリンタが見つからない場合
                        if (!findDevice) {
                            dialogTitle.value = "プリンタエラー"
                            dialogText.value = "内蔵プリンタが見つかりません"
                            alertVisible.value = true
                            return@TextButton
                        }

                        // --------------------------
                        // ここで印刷ボタン押下後の処理終了
                        // --------------------------
                        return@TextButton
                    }
                ) {
                    Text(
                        stringResource(R.string.ok),
                        fontSize = 20.sp
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        exeVisible.value = false
                        isPrintButton.value = true
                        return@TextButton
                    }
                ) {
                    Text(
                        stringResource(R.string.cancel),
                        fontSize = 20.sp
                    )
                }
            },
        )
    }


    // 準正常系ダイアログ表示
    if (alertVisible.value) {
        // 印刷ボタンを活性に戻す
        isPrintButton.value = true

        // 実行確認ダイアログを非表示
        exeVisible.value = false

        AlertDialog(
            icon = {
                Icon(Icons.Default.Warning, contentDescription = "Warning Icon")
            },
            title = {
                Text(dialogTitle.value)
            },
            text = {
                Text(
                    text = dialogText.value,
                    fontSize = 18.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // ダイアログを非表示
                        alertVisible.value = false
                    }
                ) {
                    Text(
                        stringResource(R.string.ok),
                        fontSize = 20.sp
                    )
                }
            },
            onDismissRequest = {
                // ダイアログを非表示
                alertVisible.value = false
            },
        )
    }
}


/**
 * UI：【スイッチ】今日の天気
 */
@Composable
fun TodayWeather (checked: Boolean, onCheckChanged: (Boolean) -> Unit) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "今日の天気",
            fontSize = 19.sp
        )
        Switch(
            checked = checked,
            onCheckedChange = {
                onCheckChanged(it)
            },
        )
    }
}

/**
 * UI：【スイッチ】明日の天気
 */
@Composable
fun TomorrowWeather (checked: Boolean, onCheckChanged: (Boolean) -> Unit) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "明日の天気",
            fontSize = 19.sp
        )
        Switch(
            checked = checked,
            onCheckedChange = {
                onCheckChanged(it)
            },
        )
    }
}

/**
 * UI：【スイッチ】明後日の天気
 */
@Composable
fun AfterTomorrowWeather (checked: Boolean, onCheckChanged: (Boolean) -> Unit) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "明後日の天気",
            fontSize = 19.sp
        )
        Switch(
            checked = checked,
            onCheckedChange = {
                onCheckChanged(it)
            },
        )
    }
}

/**
 * UI：【スイッチ】天気概況
 */
@Composable
fun WeatherOverview (checked: Boolean, onCheckChanged: (Boolean) -> Unit) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "天気概況の印刷",
            fontSize = 19.sp
        )
        Switch(
            checked = checked,
            onCheckedChange = {
                onCheckChanged(it)
            },
        )
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Main UI"
)
@Composable
fun WeatherPrintPreview() {
    WeatherPrintTheme {
        WeatherPrint("build 20250619_1")
    }
}

/**
 * UI：実行確認ダイアログ テキスト組み立て
 */
fun dialogText(
    isToday: Boolean,
    isTomorrow: Boolean,
    isAfterTomorrow: Boolean,
    isOverview: Boolean,
    narrowAreaKey: String,
    narrowAreaValue: String
): String {
    var textToday: String = ""
    var textTomorrow: String = ""
    var textAfterTomorrow: String = ""
    var textOverview: String = ""

    if (isToday) {
        textToday = "✔ 印刷する"
    } else {
        textToday = "✖ 印刷しない"
    }

    if (isTomorrow) {
        textTomorrow = "✔ 印刷する"
    } else {
        textTomorrow = "✖ 印刷しない"
    }

    if (isAfterTomorrow) {
        textAfterTomorrow = "✔ 印刷する"
    } else {
        textAfterTomorrow = "✖ 印刷しない"
    }

    if (isOverview) {
        textOverview = "✔ 印刷する"
    } else {
        textOverview = "✖ 印刷しない"
    }

    val buildText: String =
        "【対象日】\n" +
        "　今日の天気　：$textToday\n" +
        "　明日の天気　：$textTomorrow\n" +
        "　明後日の天気：$textAfterTomorrow\n" +
        "\n" +
        "【天気概況】\n" +
        "　$textOverview\n" +
        "\n" +
        "【対象地域】\n" +
        "　$narrowAreaKey $narrowAreaValue"

    return buildText
}

/**
 * 共通：トースト表示（ショート）
 */
fun showToastShort(context: Context, message: String) {
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_SHORT
    ).show()
}

/**
 * 共通：トースト表示（ロング）
 */
fun showToastLong(context: Context, message: String) {
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_LONG
    ).show()
}

/**
 * 天気予報APIのリクエストを実行（同期処理）
 */
fun requestWeatherData(narrowAreaKey : String) : WeatherData? {

    // data classを初期化
    var weatherData : WeatherData? = null

    // UIとは別にスレッドを立て、join()指定で処理終了を待つことで同期処理にする
    thread {
        // 天気予報APIサービス生成
        var retroClient: RetrofitClient = RetrofitClient()

        // 天気予報取得
        val response = retroClient.getService().getWeather(narrowAreaKey).execute()

        // 結果をdata classに格納
        if (response.isSuccessful) {
            weatherData = response.body()
        }
    }.join()

    return weatherData
}
