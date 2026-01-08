package com.weatherprint

import android.util.Log
import com.weatherprint.ConstantParameters.Companion.APP_LOG_TAG
import java.io.ByteArrayOutputStream

class ImageByteConvertSML200 {
    // No Constructor
}

/**
 *  天気シンボルデータ生成
 *  晴：sunBits24
 *  曇：cloudBits24
 *  雨：rainBits24
 *  雪：snowBits24
 *  時々・一時：barBits24
 *  のち：slashBits24
 *  空白：spaceBits24
 */
fun makeSymbolSML200(telop: String) : MakeSymbolData {

    // シンボル印刷用data classの初期化（すべてspaceBitsで埋めておく）
    val makeSymbolData = MakeSymbolData(spaceBits24, spaceBits24, spaceBits24)

    // 正規表現：時々・一時・のち 判定用
    val regexSometimes = Regex(".+時々.+")
    val regexTemporary = Regex(".+一時.+")
    val regexAfter = Regex(".+のち.+")

    // 正規表現：晴 の複合判定用
    val regexHeadSun = Regex("晴.+")
    val regexLastSun = Regex(".+晴")

    // 正規表現：曇 の複合判定用
    val regexHeadCloud = Regex("曇.+")
    val regexLastCloud = Regex(".+曇")

    // 正規表現：雨 の複合判定用
    val regexHeadRain = Regex("雨.+")
    val regexLastRain = Regex(".+雨")

    // 正規表現：雪 の複合判定用
    val regexHeadSnow = Regex("雪.+")
    val regexLastSnow = Regex(".+雪")

    // 正規表現：止む の複合判定用
    val regexLastStop = Regex(".+止む")

    // 正規表現：雨か雪 の複合判定用
    val regexLastRainOrSnow = Regex(".+雨か雪")

    // 正規表現：雪か雨 の複合判定用
    val regexLastSnowOrRain = Regex(".+雪か雨")

    // ----------------------------------------
    // 単一シンボルのケース
    // ----------------------------------------

    // 「晴れ」のみ
    if (telop=="晴れ") {
        makeSymbolData.secondLetter = sunBits24
    }
    // 「曇り」のみ
    else if (telop=="曇り") {
        makeSymbolData.secondLetter = cloudBits24
    }
    // 「雨」のみ
    else if (telop=="雨") {
        makeSymbolData.secondLetter = rainBits24
    }
    // 「雨か雪」のみ
    else if (telop=="雨か雪") {
        makeSymbolData.secondLetter = rainBits24
    }
    // 「雪」のみ
    else if (telop=="雪") {
        makeSymbolData.secondLetter = snowBits24
    }
    // 「雪か雨」のみ
    else if (telop=="雪か雨") {
        makeSymbolData.secondLetter = snowBits24
    }
    // 「風雪強い」のみ
    else if (telop=="風雪強い") {
        makeSymbolData.secondLetter = snowBits24
    }
    // 「暴風雪」のみ
    else if (telop=="暴風雪") {
        makeSymbolData.secondLetter = snowBits24
    }

    // ----------------------------------------
    // 「時々」「一時」「のち」を含むケース
    // ----------------------------------------
    else if (telop.matches(regexSometimes) ||
             telop.matches(regexTemporary) ||
             telop.matches(regexAfter)) {

        // ----- セカンドトレターの判定 -----
        if (telop.matches(regexAfter)) {
            // 「のち」→「／」
            makeSymbolData.secondLetter = slashBits24
        } else if (telop.matches(regexSometimes) || telop.matches(regexTemporary)) {
            // 「時々」「一時」→「｜」
            makeSymbolData.secondLetter = barBits24
        }

        // ----- ファーストレターの判定 -----
        if (telop.matches(regexHeadSun)) {
            // 晴
            makeSymbolData.firstLetter = sunBits24
        }
        else if (telop.matches(regexHeadCloud)) {
            // 曇
            makeSymbolData.firstLetter = cloudBits24
        }
        else if (telop.matches(regexHeadRain)) {
            // 雨
            makeSymbolData.firstLetter = rainBits24
        }
        else if (telop.matches(regexHeadSnow)) {
            // 雪
            makeSymbolData.firstLetter = snowBits24
        }

        // ----- サードレターの判定 -----
        if (telop.matches(regexLastSun)) {
            // 晴
            makeSymbolData.thirdLetter = sunBits24
        }
        else if (telop.matches(regexLastCloud)) {
            // 曇
            makeSymbolData.thirdLetter = cloudBits24
        }
        else if (telop.matches(regexLastRainOrSnow)) {
            // 雨か雪（先に来る方のシンボルを割り当てる）
            makeSymbolData.thirdLetter = rainBits24
        }
        else if (telop.matches(regexLastSnowOrRain)) {
            // 雪か雨（先に来る方のシンボルを割り当てる）
            makeSymbolData.thirdLetter = snowBits24
        }
        else if (telop.matches(regexLastRain)) {
            // 雨
            makeSymbolData.thirdLetter = rainBits24
        }
        else if (telop.matches(regexLastSnow)) {
            // 雪
            makeSymbolData.thirdLetter = snowBits24
        }
        else if (telop.matches(regexLastStop)) {
            // 止む（「雨時々止む」「雪時々止む」という形で使用されるので「曇り」を割り当てる）
            makeSymbolData.thirdLetter = cloudBits24
        }
    }

    return makeSymbolData
}


/**
 *  天気シンボルデータからバイト列を生成
 */
fun symbolByteArraySML200(makeSymbolPrint: MakeSymbolData): ByteArray {

    // バイト列作成用ストリーム
    val outByteArray = ByteArrayOutputStream()

    // 連結用シンボル
    val allLetter: Array<String?> = arrayOfNulls(24)

    // シンボルをすべて連結
    for (i in 0..23) {
        allLetter[i] =
            makeSymbolPrint.firstLetter[i] + makeSymbolPrint.secondLetter[i] + makeSymbolPrint.thirdLetter[i]
    }

    // 縦8ドット×3区画を、横75ドット分ループしてバイナリデータを作成

    // ビット列連結用バッファー
    val dataBuild1 = StringBuilder()
    val dataBuild2 = StringBuilder()
    val dataBuild3 = StringBuilder()

    // 横の繰り返し
    for (i in 0..74) {

        // 縦の繰り返し：区画1
        dataBuild1.clear()
        for (j in 0..7) {
            dataBuild1.append(allLetter[j]?.substring(i, i + 1))
        }
        val section1 = dataBuild1.toString().toInt(2).toByte()

        // 縦の繰り返し：区画2
        dataBuild2.clear()
        for (j in 8..15) {
            dataBuild2.append(allLetter[j]?.substring(i, i + 1))
        }
        val section2 = dataBuild2.toString().toInt(2).toByte()

        // 縦の繰り返し：区画3
        dataBuild3.clear()
        for (j in 16..23) {
            dataBuild3.append(allLetter[j]?.substring(i, i + 1))
        }
        val section3 = dataBuild3.toString().toInt(2).toByte()

        // 読み出したデータをリスト化
        val lineByteArray = listOf(
            section1, section2, section3
        )

        // ストリームにByteArrayとして書き出し
        outByteArray.write(lineByteArray.toByteArray())

        // 幅を倍にするために再度同じデータを書き出し
        outByteArray.write(lineByteArray.toByteArray())
    }

    // バイトストリーム打ち止め
    outByteArray.close()

    // 戻り値として出力
    return outByteArray.toByteArray()
}

// 曇のビットイメージ
val cloudBits24 = listOf(
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000011110000000000000",
    "0000000100001100000000000",
    "0000011000000010000000000",
    "0000010000000001111000000",
    "0000100000000001000110000",
    "0000100000000001000010000",
    "0001111100000000000001000",
    "0010000000000000000001000",
    "0100000000000000000001000",
    "0100000000000000000001000",
    "0100000000000000000001100",
    "0100000000000000000010010",
    "0100001000000000000000010",
    "0100000100000000000000100",
    "0010000010000000000001000",
    "0001110111000000000010000",
    "0000001000111111111100000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000"
)

// 晴のビットイメージ
val sunBits24 = listOf(
    "0000000000000000000000000",
    "0000000000001000000000000",
    "0000000000001000000000000",
    "0000000000001000000000000",
    "0000100000001000000010000",
    "0000010000000000000100000",
    "0000001000000000001000000",
    "0000000001111111000000000",
    "0000000011111111100000000",
    "0000000111111111110000000",
    "0000000111111111110000000",
    "0000001111111111111000000",
    "0111101111111111111011110",
    "0000001111111111111000000",
    "0000000111111111110000000",
    "0000000111111111110000000",
    "0000000011111111100000000",
    "0000000001111111000000000",
    "0000001000001000001000000",
    "0000010000000000000100000",
    "0000100000001000000010000",
    "0000000000001000000000000",
    "0000000000001000000000000",
    "0000000000001000000000000"
)

// 雨のビットイメージ
val rainBits24 = listOf(
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000001111110001000000",
    "0000000111111111110000000",
    "0000011111111111111000000",
    "0000111111111111111100000",
    "0001000111111111111110000",
    "0010000011111111111110000",
    "0000000001111111111111000",
    "0000000001111111111111000",
    "0000000011000111111111100",
    "0000000000001001111111100",
    "0000000000001001111111100",
    "0000000000010001111111100",
    "0000000000010001100001100",
    "0000000000100000000000100",
    "0000000000100000000000100",
    "0000000001000000000000100",
    "0000000010000000000001000",
    "0001000010000000000000000",
    "0001000100000000000000000",
    "0001000100000000000000000",
    "0001111000000000000000000",
    "0000000000000000000000000"
)

// 雪のビットイメージ
val snowBits24 = listOf(
    "0000000000100000000000000",
    "0000000011110000000000000",
    "0000000111111000000000000",
    "0000000111111110000000000",
    "0000000011111111100000000",
    "0000000111111111110000000",
    "0000000111111111110000000",
    "0000000111111111111000000",
    "0000000111111111111000000",
    "0000000111111111111000000",
    "0000100111111111111001000",
    "0010100111111111111001100",
    "0001101111111111111001000",
    "0000111111111111111110000",
    "0000011111111111111100000",
    "0000111111111111111110000",
    "0000111111111111111110000",
    "0000111111111111111111000",
    "0000111111111111111111000",
    "0000111111111111111111000",
    "0000111111111111111111000",
    "0000111111111111111110000",
    "0000011111111111111110000",
    "0000001111111111111100000"
)

// 「のち」のビットイメージ
val barBits24 = listOf(
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000",
    "0000000000001100000000000"

)

// 「時々」「一時」のビットイメージ
val slashBits24 = listOf(
    "0000000000000000000000011",
    "0000000000000000000000110",
    "0000000000000000000001100",
    "0000000000000000000011000",
    "0000000000000000000110000",
    "0000000000000000001100000",
    "0000000000000000011000000",
    "0000000000000000110000000",
    "0000000000000001100000000",
    "0000000000000011000000000",
    "0000000000000110000000000",
    "0000000000001100000000000",
    "0000000000011000000000000",
    "0000000000110000000000000",
    "0000000001100000000000000",
    "0000000011000000000000000",
    "0000000110000000000000000",
    "0000001100000000000000000",
    "0000011000000000000000000",
    "0000110000000000000000000",
    "0001100000000000000000000",
    "0011000000000000000000000",
    "0110000000000000000000000",
    "1100000000000000000000000"
)

// 空白のビットイメージ
val spaceBits24 = listOf(
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000",
    "0000000000000000000000000"
)
