/**
 * ------------------------------------------------------------
 * サーマルプリンターへの印刷実行（MPT-II 向け）
 * ------------------------------------------------------------
 */
package com.weatherprint

import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.util.Log
import java.io.IOException
import java.io.OutputStream
import kotlin.text.Charsets.UTF_8
import com.weatherprint.ConstantParameters.Companion.APP_LOG_TAG
import com.weatherprint.ConstantParameters.Companion.ESC_ALIGN_CENTER
import com.weatherprint.ConstantParameters.Companion.ESC_BOLD_ON
import com.weatherprint.ConstantParameters.Companion.ESC_FONT_SIZE_1
import com.weatherprint.ConstantParameters.Companion.ESC_FONT_SIZE_2
import com.weatherprint.ConstantParameters.Companion.ESC_INITIALISE
import com.weatherprint.ConstantParameters.Companion.ESC_JP_CHARSET
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
import com.weatherprint.ConstantParameters.Companion.ESC_JP_DISABLE
import com.weatherprint.ConstantParameters.Companion.ESC_JP_ENABLE
import com.weatherprint.ConstantParameters.Companion.ESC_MULTI_FEED
import com.weatherprint.ConstantParameters.Companion.ESC_WAIT_MIDDLE
import com.weatherprint.ConstantParameters.Companion.APP_NAME
import com.weatherprint.ConstantParameters.Companion.APP_CREDIT
import com.weatherprint.ConstantParameters.Companion.ESC_WAIT_2SEC
import com.weatherprint.ConstantParameters.Companion.ESC_WAIT_3SEC
import com.weatherprint.ConstantParameters.Companion.ESC_WAIT_5SEC
import com.weatherprint.ConstantParameters.Companion.NARROW_AREA_LIST_EN

class ConnectedThreadMPTII(
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
                Log.i(APP_LOG_TAG, "ConnectedThreadMPTII START")

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

                    // スマホ側：バッファ排出
                    mmOutStream.flush()

                    // プリンタ側：初期化
                    mmOutStream.write(ESC_INITIALISE)

                    // バッファ放出＆スリープ（処理落ち防止）
                    mmOutStream.flush()
                    sleep(ESC_WAIT_LONG)

                    // 強調表示（UIでONにした場合は全文強調表示とする）
                    if (printData.isBold) {
                        mmOutStream.write(ESC_BOLD_ON)
                    }

                    // 初回の印刷出力のみ用紙フィード：３行
                    if (!isFirstPrint) {
                        mmOutStream.write(ESC_MULTI_FEED)
                        isFirstPrint = true
                    }

                    // タイトル：今日／明日／明後日の天気
                    mmOutStream.write(ESC_FONT_SIZE_2)
                    mmOutStream.write(ESC_WB_REVERS_ON)
                    mmOutStream.write(ESC_ALIGN_CENTER)
                    if (i == 0) {
                        singleText("     Today     ", true)
                    } else if ( i == 1 ) {
                        singleText("   Tomorrow    ", true)
                    } else {
                        singleText("   In 2 days   ", true)
                    }
                    mmOutStream.write(ESC_FONT_SIZE_1)
                    singleText("         (" + printData.forecasts[i].date + ")         ", true)
                    mmOutStream.write(ESC_WB_REVERS_OFF)

                    // バッファ放出＆スリープ（処理落ち防止）
                    mmOutStream.flush()
                    sleep(ESC_WAIT_MIDDLE)

                    // 発表日時
                    singleLF()
                    mmOutStream.write(ESC_FONT_SIZE_1)
                    mmOutStream.write(ESC_ALIGN_CENTER)
                    singleText("Announcement at", true)
                    singleText(printData.publicTimeFormatted, true)
                    singleLF()

                    // バッファ放出＆スリープ（処理落ち防止）
                    mmOutStream.flush()
                    sleep(ESC_WAIT_MIDDLE)

                    // 地域指定
                    mmOutStream.write(ESC_FONT_SIZE_2_H)
                    mmOutStream.write(ESC_ALIGN_CENTER)
                    mmOutStream.write(ESC_UNDER_LINE_ON)
                    val areaName = NARROW_AREA_LIST_EN.get(printData.narrowArea).toString()
                    singleText(areaName, true)
                    mmOutStream.write(ESC_FONT_SIZE_1)
                    singleText("Area Code : " + printData.narrowArea, true)
                    mmOutStream.write(ESC_UNDER_LINE_OFF)
                    singleLF()

                    // バッファ放出＆スリープ（処理落ち防止）
                    mmOutStream.flush()
                    sleep(ESC_WAIT_MIDDLE)

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

                    // 天気テキストを英文に変換
                    val weatherText = makeEnTelop(printData.forecasts[i].telop)

                    mmOutStream.write(ESC_ALIGN_CENTER)
                    mmOutStream.write(ESC_FONT_SIZE_2_H)
                    singleLF()
                    singleText(weatherText, true)
                    singleLF()

                    // バッファ放出＆スリープ（処理落ち防止）
                    mmOutStream.flush()
                    sleep(ESC_WAIT_MIDDLE)

                    // 気温
                    mmOutStream.write(ESC_ALIGN_CENTER)
                    mmOutStream.write(ESC_FONT_SIZE_2_H)
                    singleText("========= Temperature =========", true)

                    mmOutStream.write(ESC_FONT_SIZE_1)
                    singleText("Min temp : ", false)
                    mmOutStream.write(ESC_FONT_SIZE_2_W)
                    if(printData.forecasts[i].temperature.min.celsius == null) {
                        singleText("--", false)
                    } else {
                        if (printData.forecasts[i].temperature.min.celsius == "0") {
                            singleText(" ", false)
                        }
                        singleText(printData.forecasts[i].temperature.min.celsius, false)
                    }
                    singleText(" [C]", true)

                    mmOutStream.write(ESC_FONT_SIZE_1)
                    singleText("Max temp : ", false)
                    mmOutStream.write(ESC_FONT_SIZE_2_W)
                    if(printData.forecasts[i].temperature.max.celsius == null) {
                        singleText("--", false)
                    } else {
                        if (printData.forecasts[i].temperature.max.celsius == "0") {
                            singleText(" ", false)
                        }
                        singleText(printData.forecasts[i].temperature.max.celsius, false)
                    }
                    singleText(" [C]", true)
                    singleLF()

                    // バッファ放出＆スリープ（処理落ち防止）
                    mmOutStream.flush()
                    sleep(ESC_WAIT_MIDDLE)

                    // 降水確率
                    mmOutStream.write(ESC_ALIGN_CENTER)
                    mmOutStream.write(ESC_FONT_SIZE_2_H)
                    singleText("======= Chance of rain =======", true)

                    mmOutStream.write(ESC_FONT_SIZE_2_W)
                    singleText("00 - 06 : ", false)
                    if (printData.forecasts[i].chanceOfRain.T00_06 == "0%") {
                        singleText(" ", false)
                    }
                    singleText(printData.forecasts[i].chanceOfRain.T00_06, true)

                    mmOutStream.write(ESC_FONT_SIZE_2_W)
                    singleText("06 - 12 : ", false)
                    if (printData.forecasts[i].chanceOfRain.T06_12 == "0%") {
                        singleText(" ", false)
                    }
                    singleText(printData.forecasts[i].chanceOfRain.T06_12, true)

                    mmOutStream.write(ESC_FONT_SIZE_2_W)
                    singleText("12 - 18 : ", false)
                    if (printData.forecasts[i].chanceOfRain.T12_18 == "0%") {
                        singleText(" ", false)
                    }
                    singleText(printData.forecasts[i].chanceOfRain.T12_18, true)

                    mmOutStream.write(ESC_FONT_SIZE_2_W)
                    singleText("18 - 24 : ", false)
                    if (printData.forecasts[i].chanceOfRain.T18_24 == "0%") {
                        singleText(" ", false)
                    }
                    singleText(printData.forecasts[i].chanceOfRain.T18_24, true)
                    singleLF()

                    // バッファ放出＆スリープ（処理落ち防止）
                    mmOutStream.flush()
                    sleep(ESC_WAIT_MIDDLE)

                    // クレジット
                    singleLF()
                    mmOutStream.write(ESC_ALIGN_CENTER)
                    mmOutStream.write(ESC_FONT_SIZE_1)
                    singleText(APP_NAME, true)
                    singleText(APP_CREDIT, true)

                    // 用紙フィード：３行
                    mmOutStream.write(ESC_MULTI_FEED)

                    // 切り取り線
                    singleText("-------------------------------", true)

                    // 用紙フィード：３行
                    mmOutStream.write(ESC_MULTI_FEED)

                    // バッファ放出＆スリープ（処理落ち防止）
                    mmOutStream.flush()
                    sleep(ESC_WAIT_2SEC)
                }

                // 印刷終了
                Log.i(APP_LOG_TAG, "ConnectedThreadMPTII END")

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
