/**
 * ------------------------------------------------------------
 * 天気予報取得API レスポンス
 * ------------------------------------------------------------
 */
package com.weatherprint

/**
 * 本体（共通部分）
 */
data class WeatherData(
    /** 予報の発表日時（ ISO8601 形式 / 例・2020-09-01T05:00:00+09:00 ） */
    val publicTime: String,
    /** 予報の発表日時（例・2020/09/01 05:00:00 ） */
    val publicTimeFormatted: String,
    /** 予報を発表した気象台（例・福岡管区気象台） */
    val publishingOffice: String,
    /** タイトル・見出し（例・福岡県 久留米 の天気） */
    val title: String,
    /** リクエストされたデータの地域に該当する気象庁 HP の天気予報の URL */
    val link: String,
    /** 天気概況文 */
    val description: Description,
    /** 都道府県天気予報の予報日毎の配列 */
    val forecasts: List<Forecasts>,
    /** 予報を発表した地域を定義 */
    val location: Location,
    /** コピーライト */
    val copyright: Copyright,
    /** 今日の天気の印刷判定 */
    var isToday: Boolean,
    /** 明日の天気の印刷判定 */
    var isTomorrow: Boolean,
    /** 明後日の天気の印刷判定 */
    var isAfterTomorrow: Boolean,
    /** 天気概況の印刷判定 */
    var isOverview: Boolean,
    /** 地域コードの印刷判定 */
    var narrowArea: String,
)

/**
 * 天気予報取得API レスポンス：天気概況
 */
data class Description(
    /** 天気概況文の発表時刻（ ISO8601 形式 / 例・2020-09-01T04:52:00+09:00 ） */
    val publicTime: String,
    /** 天気概況文の発表時刻（例・2020/09/01 04:52:00 ） */
    val publicTimeFormatted: String,
    /** 天気概況文（見出しのみ） */
    val headlineText: String,
    /** 天気概況文（本文のみ） */
    val bodyText: String,
    /** 天気概況文（全文） */
    val text: String,
)

/**
 * 天気予報取得API レスポンス：都道府県天気予報の予報日毎の配列
 */
data class Forecasts(
    /** 予報日 */
    val date: String,
    /** 予報日（今日・明日・明後日のいずれか） */
    val dateLabel: String,
    /** 天気（晴れ、曇り、雨など） */
    val telop: String,
    /** 天気詳細 */
    val detail: Detail,
    /** 最高気温・最低気温 */
    val temperature: Temperature,
    /** 降水確率 */
    val chanceOfRain: ChanceOfRain,
    /** 天気アイコン */
    val image: Image
)

/**
 * 天気予報取得API レスポンス：天気詳細
 */
data class Detail(
    /** 詳細な天気情報 */
    val weather: String,
    /** 風の強さ */
    val wind: String,
    /** 波の高さ（海に面している地域のみ） */
    val wave: String
)

/**
 * 天気予報取得API レスポンス：最高気温・最低気温
 */
data class Temperature(
    /** 最高気温 */
    val max: MaxMin,
    /** 最低気温 */
    val min: MaxMin
)

/**
 * 天気予報取得API レスポンス：気温
 */
data class MaxMin(
    /** 摂氏(C) */
    val celsius: String,
    /** 華氏(F) */
    val fahrenheit: String
)

/**
 * 天気予報取得API レスポンス：降水確率
 */
data class ChanceOfRain(
    /** 0 時から 6 時までの降水確率 */
    val T00_06: String,
    /** 6 時から 12 時までの降水確率 */
    val T06_12: String,
    /** 12 時から 18 時までの降水確率 */
    val T12_18: String,
    /** 18 時から 24 時までの降水確率 */
    val T18_24: String
)

/**
 * 天気予報取得API レスポンス：天気アイコン
 */
data class Image(
    /** 天気（晴れ、曇り、雨など） */
    val title: String,
    /** 天気アイコンの URL（SVG 画像） */
    val url: String,
    /** 天気アイコンの幅 */
    val width: String,
    /** 天気アイコンの高さ */
    val height: String
)

/**
 * 天気予報取得API レスポンス：予報を発表した地域を定義
 */
data class Location(
    /** 地方名（例・九州） */
    val area: String,
    /** 都道府県名（例・福岡県） */
    val prefecture: String,
    /** 一次細分区域名（例・北九州地方） */
    val district: String,
    /** 地域名（気象観測所名）（例・八幡） */
    val city: String
)

/**
 * 天気予報取得API レスポンス：コピーライト
 */
data class Copyright(
    /** コピーライトの文言 */
    val title: String,
    /** 天気予報 API（livedoor 天気互換）の URL */
    val link: String,
    /** 天気予報 API（livedoor 天気互換）のアイコン */
    //val image : String,
    /** 天気予報 API（livedoor 天気互換）で使用している気象データの配信元（気象庁）*/
    //val provider : String
)