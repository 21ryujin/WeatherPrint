/**
 * ------------------------------------------------------------
 * サーマルプリンターへの印刷実行（セイコーインスツル MP-B20 向け）
 * ------------------------------------------------------------
 */
package com.weatherprint

import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.util.Log
import java.io.IOException
import java.io.OutputStream
import com.weatherprint.ConstantParameters.Companion.APP_LOG_TAG
import com.weatherprint.ConstantParameters.Companion.ESC_ALIGN_CENTER
import com.weatherprint.ConstantParameters.Companion.ESC_BOLD_ON
import com.weatherprint.ConstantParameters.Companion.ESC_FONT_SIZE_1
import com.weatherprint.ConstantParameters.Companion.ESC_FONT_SIZE_2
import com.weatherprint.ConstantParameters.Companion.ESC_INITIALISE
import com.weatherprint.ConstantParameters.Companion.ESC_SYMBOL_PRINT
import com.weatherprint.ConstantParameters.Companion.ESC_UNDER_LINE_OFF
import com.weatherprint.ConstantParameters.Companion.ESC_UNDER_LINE_ON
import com.weatherprint.ConstantParameters.Companion.ESC_WAIT_LONG
import com.weatherprint.ConstantParameters.Companion.ESC_WAIT_SHORT
import com.weatherprint.ConstantParameters.Companion.ESC_WB_REVERS_OFF
import com.weatherprint.ConstantParameters.Companion.ESC_WB_REVERS_ON
import com.weatherprint.ConstantParameters.Companion.ESC_ALIGN_LEFT
import com.weatherprint.ConstantParameters.Companion.ESC_FONT_SIZE_2_H
import com.weatherprint.ConstantParameters.Companion.ESC_FONT_SIZE_2_W
import com.weatherprint.ConstantParameters.Companion.ESC_MULTI_FEED
import com.weatherprint.ConstantParameters.Companion.ESC_WAIT_MIDDLE
import com.weatherprint.ConstantParameters.Companion.APP_NAME_JP1
import com.weatherprint.ConstantParameters.Companion.APP_NAME_JP2
import com.weatherprint.ConstantParameters.Companion.APP_CREDIT
import com.weatherprint.ConstantParameters.Companion.ESC_FINE_FEED_N07
import com.weatherprint.ConstantParameters.Companion.ESC_FINE_FEED_N15
import com.weatherprint.ConstantParameters.Companion.ESC_WAIT_1SEC
import com.weatherprint.ConstantParameters.Companion.ESC_WAIT_3SEC
import com.weatherprint.ConstantParameters.Companion.ESC_WAIT_5SEC
import java.nio.charset.Charset

class ConnectedThreadMPB20(
    private val mmSocket: BluetoothSocket, weatherData: WeatherData) : Thread() {

        // 天気予報APIから取得したデータの格納
        val printData: WeatherData = weatherData

        // Bluetoothシリアルポートからプリンタに出力するストリーム定義
        private val mmOutStream: OutputStream = mmSocket.outputStream

        // 信号入力時の参考用
        // private val mmInStream: InputStream = mmSocket.inputStream
        // private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream


        /**
         * 印刷実行部
         */
        override fun run() {

            try {
                Log.i(APP_LOG_TAG, "ConnectedThreadMPB20 START")

                // 天気概況を印刷済みかを判定
                var isOverviewPrinted: Boolean = false

                // 初回の印刷かを判定
                var isFirstPrint: Boolean = false

                for (i in 0..2) {

                    if (i == 0 && !printData.isToday) {
                        continue
                    }
                    if (i == 1 && !printData.isTomorrow) {
                        continue
                    }
                    if (i == 2 && !printData.isAfterTomorrow) {
                        continue
                    }

                    // バッファ排出
                    mmOutStream.flush()

                    // プリンタ初期化
                    mmOutStream.write(ESC_INITIALISE)

                    // 強調表示（UIでONにした場合は全文強調表示とする）
                    if (printData.isBold) {
                        mmOutStream.write(ESC_BOLD_ON)
                    }

                    // バッファ放出＆スリープ（処理落ち防止）
                    mmOutStream.flush()
                    sleep(ESC_WAIT_SHORT)

                    // 初回の印刷出力のみ用紙フィード：３行
                    if (!isFirstPrint) {
                        mmOutStream.write(ESC_MULTI_FEED)
                        isFirstPrint = true
                    }

                    // タイトル：今日／明日／明後日の天気
                    mmOutStream.write(ESC_FONT_SIZE_2)
                    mmOutStream.write(ESC_WB_REVERS_ON)
                    mmOutStream.write(ESC_ALIGN_CENTER)
                    if (printData.forecasts[i].dateLabel == "明後日") {
                        multiText("　" + printData.forecasts[i].dateLabel + "の天気　", true)
                    } else {
                        singleText("   ", false)
                        multiText(printData.forecasts[i].dateLabel + "の天気", false)
                        singleText("   ", true)
                    }
                    mmOutStream.write(ESC_WB_REVERS_OFF)
                    mmOutStream.write(ESC_FINE_FEED_N15)

                    // 発表日時と管区気象台
                    mmOutStream.write(ESC_FONT_SIZE_1)
                    mmOutStream.write(ESC_ALIGN_CENTER)
                    singleText(printData.publicTimeFormatted + " ", false)
                    multiText("時点", true)
                    multiText(printData.publishingOffice + "発表", true)
                    singleLF()

                    // 地域指定
                    mmOutStream.write(ESC_FONT_SIZE_2_H)
                    mmOutStream.write(ESC_ALIGN_CENTER)
                    multiText(printData.title, true)
                    mmOutStream.write(ESC_FINE_FEED_N07)

                    mmOutStream.write(ESC_FONT_SIZE_1)
                    multiText("エリアコード：", false)
                    mmOutStream.write(ESC_UNDER_LINE_ON)
                    singleText(printData.narrowArea, true)
                    mmOutStream.write(ESC_UNDER_LINE_OFF)

                    // バッファ放出＆スリープ（処理落ち防止）
                    mmOutStream.flush()
                    sleep(ESC_WAIT_SHORT)

                    // 天気シンボル
                    val makeSymbolPrint: MakeSymbolData = makeSymbol(printData.forecasts[i].telop)

                    for (j in 0..59) {
                        mmOutStream.write(ESC_SYMBOL_PRINT +
                            symbolByteArray(
                                makeSymbolPrint.firstLetter[j],
                                makeSymbolPrint.secondLetter[j],
                                makeSymbolPrint.thirdLetter[j]
                            )
                        )
                        // バッファ放出＆スリープ（処理落ち防止）
                        mmOutStream.flush()
                        sleep(ESC_WAIT_SHORT)
                    }

                    // 天気テキスト
                    mmOutStream.write(ESC_ALIGN_CENTER)
                    mmOutStream.write(ESC_FONT_SIZE_2)
                    multiText(printData.forecasts[i].telop, true)
                    singleLF()

                    // 気温
                    mmOutStream.write(ESC_ALIGN_CENTER)
                    mmOutStream.write(ESC_FONT_SIZE_2_H)
                    multiText("＝＝＝＝＝＝＝気温＝＝＝＝＝＝＝", true)
                    mmOutStream.write(ESC_FINE_FEED_N15)

                    mmOutStream.write(ESC_FONT_SIZE_1)
                    multiText("最低気温：", false)
                    mmOutStream.write(ESC_FONT_SIZE_2_W)
                    if(printData.forecasts[i].temperature.min.celsius == null) {
                        singleText("--", false)
                    } else {
                        if (printData.forecasts[i].temperature.min.celsius == "0") {
                            singleText(" ", false)
                        }
                        singleText(printData.forecasts[i].temperature.min.celsius, false)
                    }
                    multiText("℃", true)

                    mmOutStream.write(ESC_FONT_SIZE_1)
                    multiText("最高気温：", false)
                    mmOutStream.write(ESC_FONT_SIZE_2_W)
                    if(printData.forecasts[i].temperature.max.celsius == null) {
                        singleText("--", false)
                    } else {
                        if (printData.forecasts[i].temperature.max.celsius == "0") {
                            singleText(" ", false)
                        }
                        singleText(printData.forecasts[i].temperature.max.celsius, false)
                    }
                    multiText("℃", true)
                    singleLF()

                    // バッファ放出＆スリープ（処理落ち防止）
                    mmOutStream.flush()
                    sleep(ESC_WAIT_SHORT)

                    // 降水確率
                    mmOutStream.write(ESC_ALIGN_CENTER)
                    mmOutStream.write(ESC_FONT_SIZE_2_H)
                    multiText("＝＝＝＝＝＝降水確率＝＝＝＝＝＝", true)
                    mmOutStream.write(ESC_FINE_FEED_N15)

                    mmOutStream.write(ESC_FONT_SIZE_1)
                    multiText("００時～０６時：", false)
                    mmOutStream.write(ESC_FONT_SIZE_2_W)
                    if (printData.forecasts[i].chanceOfRain.T00_06 == "0%") {
                        singleText(" ", false)
                    }
                    singleText(printData.forecasts[i].chanceOfRain.T00_06, true)

                    mmOutStream.write(ESC_FONT_SIZE_1)
                    multiText("０６時～１２時：", false)
                    mmOutStream.write(ESC_FONT_SIZE_2_W)
                    if (printData.forecasts[i].chanceOfRain.T06_12 == "0%") {
                        singleText(" ", false)
                    }
                    singleText(printData.forecasts[i].chanceOfRain.T06_12, true)

                    mmOutStream.write(ESC_FONT_SIZE_1)
                    multiText("１２時～１８時：", false)
                    mmOutStream.write(ESC_FONT_SIZE_2_W)
                    if (printData.forecasts[i].chanceOfRain.T12_18 == "0%") {
                        singleText(" ", false)
                    }
                    singleText(printData.forecasts[i].chanceOfRain.T12_18, true)

                    mmOutStream.write(ESC_FONT_SIZE_1)
                    multiText("１８時～２４時：", false)
                    mmOutStream.write(ESC_FONT_SIZE_2_W)
                    if (printData.forecasts[i].chanceOfRain.T18_24 == "0%") {
                        singleText(" ", false)
                    }
                    singleText(printData.forecasts[i].chanceOfRain.T18_24, true)
                    singleLF()

                    // バッファ放出＆スリープ（処理落ち防止）
                    mmOutStream.flush()
                    sleep(ESC_WAIT_SHORT)

                    // 風向き
                    mmOutStream.write(ESC_ALIGN_CENTER)
                    mmOutStream.write(ESC_FONT_SIZE_2_H)
                    multiText("＝＝＝＝＝＝風の状況＝＝＝＝＝＝", true)
                    mmOutStream.write(ESC_FINE_FEED_N15)
                    mmOutStream.write(ESC_FONT_SIZE_1)
                    multiText(printData.forecasts[i].detail.wind, true)
                    singleLF()

                    // 波の高さ
                    mmOutStream.write(ESC_ALIGN_CENTER)
                    mmOutStream.write(ESC_FONT_SIZE_2_H)
                    multiText("＝＝＝＝＝＝波の高さ＝＝＝＝＝＝", true)
                    mmOutStream.write(ESC_FINE_FEED_N15)
                    mmOutStream.write(ESC_FONT_SIZE_1)
                    if (printData.forecasts[i].detail.wave == null) {
                        multiText("（情報なし）", true)
                    } else {
                        multiText(printData.forecasts[i].detail.wave, true)
                    }
                    singleLF()

                    // バッファ放出＆スリープ（処理落ち防止）
                    mmOutStream.flush()
                    sleep(ESC_WAIT_1SEC)

                    // 天気概況
                    if (printData.isOverview && !isOverviewPrinted) {
                        mmOutStream.write(ESC_ALIGN_CENTER)
                        mmOutStream.write(ESC_FONT_SIZE_2_H)
                        multiText("＝＝＝＝＝＝天気概況＝＝＝＝＝＝", true)
                        mmOutStream.write(ESC_FINE_FEED_N15)
                        mmOutStream.write(ESC_ALIGN_LEFT)
                        mmOutStream.write(ESC_FONT_SIZE_1)
                        multiText(printData.description.text,true)
                        singleLF()

                        // バッファ放出＆スリープ（処理落ち防止）
                        mmOutStream.flush()
                        sleep(ESC_WAIT_SHORT)

                        // 次に天気概況を印刷しないようにするフラグ
                        isOverviewPrinted = true
                    }

                    // クレジット
                    singleLF()
                    mmOutStream.write(ESC_ALIGN_CENTER)
                    mmOutStream.write(ESC_FONT_SIZE_1)
                    multiText(APP_NAME_JP1, false)
                    singleText(APP_NAME_JP2, true)
                    singleText(APP_CREDIT, true)

                    // 用紙フィード：３行
                    mmOutStream.write(ESC_MULTI_FEED)

                    // 切り取り線
                    multiText("－－－－－－－－－－－－－－－－", true)

                    // 用紙フィード：３行
                    mmOutStream.write(ESC_MULTI_FEED)

                    // バッファ放出＆スリープ（処理落ち防止）
                    mmOutStream.flush()
                    if (printData.isOverview) {
                        // 天気概況がある場合はウェイト５秒
                        sleep(ESC_WAIT_5SEC)
                    } else {
                        // 天気概況がない場合はウェイト３秒
                        sleep(ESC_WAIT_3SEC)
                    }
                }

                // 印刷終了
                Log.i(APP_LOG_TAG, "ConnectedThreadMPB20 END")

            } catch (e: IOException) {
                Log.i(TAG, "Input stream was disconnected", e)
                cancel()
            }
    }

    /**
     * Bluetooth接続解除
     */
    fun cancel() {
        try {
            mmSocket.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the connect socket", e)
        }
    }

    /**
     * シングルバイト印刷
     */
    fun singleText(text: String, isLF: Boolean) {
        try {
            mmOutStream.write(text.toByteArray())
            if (isLF) {
                mmOutStream.write("\n".toByteArray())
            }
        } catch (e: IOException) {
            Log.d(TAG, "Input stream was disconnected", e)
            cancel()
        }
    }

    /**
     * マルチバイト印刷
     */
    fun multiText(text: String, isLF: Boolean) {
        try {
            // キャラセットをShiftJISに変換
            val charset = Charset.forName("SJIS")
            mmOutStream.write(text.toByteArray(charset))
            if (isLF) {
                mmOutStream.write("\n".toByteArray())
            }
        } catch (e: IOException) {
            Log.d(TAG, "Input stream was disconnected", e)
            cancel()
        }
    }

    /**
     * １行改行
     */
    fun singleLF() {
        try {
            mmOutStream.write("\n".toByteArray())
        } catch (e: IOException) {
            Log.d(TAG, "Input stream was disconnected", e)
            cancel()
        }
    }

}
