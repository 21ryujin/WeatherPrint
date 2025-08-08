/**
 * ------------------------------------------------------------
 * 天気のシンボルマーク組み立て用のデータクラス
 * ------------------------------------------------------------
 */
package com.weatherprint

data class MakeSymbolData(
    var firstLetter: List<String>,
    var secondLetter: List<String>,
    var thirdLetter: List<String>
)
