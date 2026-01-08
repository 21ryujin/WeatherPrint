package com.weatherprint

class EnTelopConvert {
    // No Constructor
}

/**
 *  天気テキストを英文に変換（英字サーマルプリンター向け）
 */
fun makeEnTelop(JpTelop: String) : String {

    // テキスト組み立て用変数
    var firstText: String = ""
    var secondText: String = " "
    var thirdText: String = ""

    // 正規表現：時々・一時・のち 判定用
    val regexOccasional = Regex(".+時々.+")
    val regexSometimes = Regex(".+一時.+")
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
    if (JpTelop == "晴れ") {
        secondText = "Sunny"
    } else if (JpTelop == "曇り") {
        secondText = "Cloudy"
    } else if (JpTelop == "雨か雪") {
        secondText = "Rainy or Snowy"
    } else if (JpTelop == "雪か雨") {
        secondText = "Snowy or Rainy"
    } else if (JpTelop == "雨") {
        secondText = "Rainy"
    } else if (JpTelop == "雪") {
        secondText = "Snowy"
    } else if (JpTelop == "風雪強い") {
        secondText = "Snowstorm"
    } else if (JpTelop == "暴風雪") {
        secondText = "Blizzard"
    }

    // ----------------------------------------
    // 「時々」「一時」「のち」を含むケース
    // ----------------------------------------
    else if (JpTelop.matches(regexSometimes) ||
        JpTelop.matches(regexOccasional) ||
        JpTelop.matches(regexAfter)) {

        // ----- セカンドトレターの判定 -----
        if (JpTelop.matches(regexAfter)) {
            // 「のち」→「later」
            secondText = ", and later "
        } else if (JpTelop.matches(regexSometimes)) {
            // 「一時」→「sometimes」
            secondText = " and sometimes "
        } else if (JpTelop.matches(regexOccasional)) {
            // 「時々」→「occasionally」
            secondText = " and occasionally "
        }

        // ----- ファーストレターの判定 -----
        if (JpTelop.matches(regexHeadSun)) {
            // 晴
            firstText = "Sunny"
        }
        else if (JpTelop.matches(regexHeadCloud)) {
            // 曇
            firstText = "Cloudy"
        }
        else if (JpTelop.matches(regexHeadRain)) {
            // 雨
            firstText = "Rainy"
        }
        else if (JpTelop.matches(regexHeadSnow)) {
            // 雪
            firstText = "Snowy"
        }

        // ----- サードレターの判定 -----
        if (JpTelop.matches(regexLastSun)) {
            // 晴
            thirdText = "Sunny"
        }
        else if (JpTelop.matches(regexLastCloud)) {
            // 曇
            thirdText = "Cloudy"
        }
        else if (JpTelop.matches(regexLastRainOrSnow)) {
            // 雨か雪
            thirdText = "Rainy or Snowy"
        }
        else if (JpTelop.matches(regexLastSnowOrRain)) {
            // 雪か雨
            thirdText = "Snowy or Rainy"
        }
        else if (JpTelop.matches(regexLastRain)) {
            // 雨
            thirdText = "Rainy"
        }
        else if (JpTelop.matches(regexLastSnow)) {
            // 雪
            thirdText = "Snowy"
        }
        else if (JpTelop.matches(regexLastStop)) {
            // 止む
            thirdText = "Stop"
        }
    }

    return firstText + secondText + thirdText
}