/**
 * ------------------------------------------------------------
 * 定数の定義
 * ------------------------------------------------------------
 */
package com.weatherprint

class ConstantParameters {

    companion object {
        // アプリバージョン
        const val APP_VERSION: String = "Ver 1.0.0"

        // ログ出力関連
        const val APP_LOG_TAG: String = "WeatherPrintLog"

        // 天気用法APIベースURL
        const val BASE_URL: String = "https://weather.tsukumijima.net/api/"

        // Bluetooth関連
        const val SPP_UUID: String = "00001101-0000-1000-8000-00805F9B34FB"

        // プリンター関連
        const val V2_PRO_PRINTER: String = "InnerPrinter"
        const val MPT_II_PRINTER: String = "MPT-II"

        // 印刷ウェイト
        const val ESC_WAIT_LONG: Long = 20
        const val ESC_WAIT_MIDDLE: Long = 10
        const val ESC_WAIT_SHORT: Long = 5

        // -------------------------
        // ESC/POS コマンド関連
        // -------------------------

        // プリンタ初期化 "ESC @"
        val ESC_INITIALISE = byteArrayOf(0x1B.toByte(), 0x40.toByte())

        // フォントサイズ定義 "GS ! n"
        val ESC_FONT_SIZE_1 = byteArrayOf(0x1D.toByte(), 0x21.toByte(), 0b00000000)
        val ESC_FONT_SIZE_2_H = byteArrayOf(0x1D.toByte(), 0x21.toByte(), 0b00000001)
        val ESC_FONT_SIZE_2_W = byteArrayOf(0x1D.toByte(), 0x21.toByte(), 0b00010000)
        val ESC_FONT_SIZE_2 = byteArrayOf(0x1D.toByte(), 0x21.toByte(), 0b00010001)
        val ESC_FONT_SIZE_3 = byteArrayOf(0x1D.toByte(), 0x21.toByte(), 0b00100010)
        val ESC_FONT_SIZE_4 = byteArrayOf(0x1D.toByte(), 0x21.toByte(), 0b00110011)
        val ESC_FONT_SIZE_5 = byteArrayOf(0x1D.toByte(), 0x21.toByte(), 0b01000100)
        val ESC_FONT_SIZE_6 = byteArrayOf(0x1D.toByte(), 0x21.toByte(), 0b01010101)
        val ESC_FONT_SIZE_7 = byteArrayOf(0x1D.toByte(), 0x21.toByte(), 0b01100110)
        val ESC_FONT_SIZE_8 = byteArrayOf(0x1D.toByte(), 0x21.toByte(), 0b01110111)

        // マルチバイト文字関連 "FS &"：マルチバイトON　"FS C 0xFF"：UTF-8に設定　"FS ."：マルチバイトOFF
        val ESC_JP_ENABLE  = byteArrayOf(0x1C.toByte(), 0x26.toByte())
        val ESC_JP_CHARSET = byteArrayOf(0x1C.toByte(), 0x43.toByte(), 0xFF.toByte())
        val ESC_JP_DISABLE = byteArrayOf(0x1C.toByte(), 0x2E.toByte())

        // 白黒反転 "GS b n"
        val ESC_WB_REVERS_ON  = byteArrayOf(0x1D.toByte(), 0x42.toByte(), 0x01.toByte())
        val ESC_WB_REVERS_OFF = byteArrayOf(0x1D.toByte(), 0x42.toByte(), 0x00.toByte())

        // 強調表示　"ESC E n"
        val ESC_BOLD_ON  = byteArrayOf(0x1D.toByte(), 0x42.toByte(), 0x01.toByte())
        val ESC_BOLD_OFF = byteArrayOf(0x1D.toByte(), 0x42.toByte(), 0x00.toByte())

        // 表示位置 "ESC a n"
        val ESC_ALIGN_LEFT   = byteArrayOf(0x1B.toByte(), 0x61.toByte(), 0x00.toByte())
        val ESC_ALIGN_CENTER = byteArrayOf(0x1B.toByte(), 0x61.toByte(), 0x01.toByte())
        val ESC_ALIGN_RIGHT  = byteArrayOf(0x1B.toByte(), 0x61.toByte(), 0x02.toByte())

        // 下線表示 "ESC - n"
        val ESC_UNDER_LINE_ON  = byteArrayOf(0x1B.toByte(), 0x2D.toByte(), 0x01.toByte())
        val ESC_UNDER_LINE_OFF = byteArrayOf(0x1B.toByte(), 0x2D.toByte(), 0x00.toByte())

        // 複数行改行 "ESC d n"
        val ESC_MULTI_FEED = byteArrayOf(0x1B.toByte(), 0x64.toByte(), 0x03.toByte())

        // 画像表示 "GS v 0 NUL wL wH hL hH 画像のバイト列"
        // 画像の横幅wと高さhを表す「wL wH hL hH」の横幅wの指定は８で割る必要あり
        // シンボル出力　横幅：384px　縦：2ライン
        val ESC_SYMBOL_PRINT = byteArrayOf(
            0x1D.toByte(), 0x76.toByte(), 0x30.toByte(), 0x00.toByte(),
            48.toByte(), 0x00.toByte(),
            2.toByte(), 0x00.toByte()
        )

        const val SEPARATOR: String = " - "

        // 地域 リスト値
        val NARROW_AREA_LIST: Map<String, String> = mapOf(
            // 北海道
            "01" to " ■■■ 北海道 ■■■",
            "011000" to SEPARATOR + "道北：稚内",

            "013010" to SEPARATOR + "道東：網走",
            "013020" to SEPARATOR + "道東：北見",
            "013030" to SEPARATOR + "道東：紋別",

            "014010" to SEPARATOR + "道東：根室",
            "014020" to SEPARATOR + "道東：釧路",
            "014030" to SEPARATOR + "道東：帯広",

            "015010" to SEPARATOR + "道南：室蘭",
            "015020" to SEPARATOR + "道南：浦河",

            "016010" to SEPARATOR + "道央：札幌",
            "016020" to SEPARATOR + "道央：岩見沢",
            "016030" to SEPARATOR + "道央：倶知安",

            "017010" to SEPARATOR + "道南：函館",
            "017020" to SEPARATOR + "道南：江差",

            // 青森県
            "02" to " ■■■ 青森県 ■■■",
            "020010" to SEPARATOR + "青森",
            "020020" to SEPARATOR + "むつ",
            "020030" to SEPARATOR + "八戸",

            // 岩手県
            "03" to " ■■■ 岩手県 ■■■",
            "030010" to SEPARATOR + "盛岡",
            "030020" to SEPARATOR + "宮古",
            "030030" to SEPARATOR + "大船渡",

            // 宮城県
            "04" to " ■■■ 宮城県 ■■■",
            "040010" to SEPARATOR + "仙台",
            "040020" to SEPARATOR + "白石",

            // 秋田県
            "05" to " ■■■ 秋田県 ■■■",
            "050010" to SEPARATOR + "秋田",
            "050020" to SEPARATOR + "横手",

            // 山形県
            "06" to " ■■■ 山形県 ■■■",
            "060010" to SEPARATOR + "山形",
            "060020" to SEPARATOR + "米沢",
            "060030" to SEPARATOR + "酒田",
            "060040" to SEPARATOR + "新庄",

            // 福島県
            "07" to " ■■■ 福島県 ■■■",
            "070010" to SEPARATOR + "福島",
            "070020" to SEPARATOR + "小名浜",
            "070030" to SEPARATOR + "若松",

            // 茨城県
            "08" to " ■■■ 茨城県 ■■■",
            "080010" to SEPARATOR + "水戸",
            "080020" to SEPARATOR + "土浦",

            // 栃木県
            "09" to " ■■■ 栃木県 ■■■",
            "090010" to SEPARATOR + "宇都宮",
            "090020" to SEPARATOR + "大田原",

            // 群馬県
            "10" to " ■■■ 群馬県 ■■■",
            "100010" to SEPARATOR + "前橋",
            "100020" to SEPARATOR + "みなかみ",

            // 埼玉県
            "11" to " ■■■ 埼玉県 ■■■",
            "110010" to SEPARATOR + "さいたま",
            "110020" to SEPARATOR + "熊谷",
            "110030" to SEPARATOR + "秩父",

            // 千葉県
            "12" to " ■■■ 千葉県 ■■■",
            "120010" to SEPARATOR + "千葉",
            "120020" to SEPARATOR + "銚子",
            "120030" to SEPARATOR + "館山",

            // 東京都
            "13" to " ■■■ 東京都 ■■■",
            "130010" to SEPARATOR + "東京",
            "130020" to SEPARATOR + "大島",
            "130030" to SEPARATOR + "八丈島",
            "130040" to SEPARATOR + "父島",

            // 神奈川県
            "14" to " ■■■神奈川県■■■",
            "140010" to SEPARATOR + "横浜",
            "140020" to SEPARATOR + "小田原",

            // 新潟県
            "15" to " ■■■ 新潟県 ■■■",
            "150010" to SEPARATOR + "新潟",
            "150020" to SEPARATOR + "長岡",
            "150030" to SEPARATOR + "高田",
            "150040" to SEPARATOR + "相川",

            // 富山県
            "16" to " ■■■ 富山県 ■■■",
            "160010" to SEPARATOR + "富山",
            "160020" to SEPARATOR + "伏木",

            // 石川県
            "17" to " ■■■ 石川県 ■■■",
            "170010" to SEPARATOR + "金沢",
            "170020" to SEPARATOR + "輪島",

            // 福井県
            "18" to " ■■■ 福井県 ■■■",
            "180010" to SEPARATOR + "福井",
            "180020" to SEPARATOR + "敦賀",

            // 山梨県
            "19" to " ■■■ 山梨県 ■■■",
            "190010" to SEPARATOR + "甲府",
            "190020" to SEPARATOR + "河口湖",

            // 長野県
            "20" to " ■■■ 長野県 ■■■",
            "200010" to SEPARATOR + "長野",
            "200020" to SEPARATOR + "松本",
            "200030" to SEPARATOR + "飯田",

            // 岐阜県
            "21" to " ■■■ 岐阜県 ■■■",
            "210010" to SEPARATOR + "岐阜",
            "210020" to SEPARATOR + "高山",

            // 静岡県
            "22" to " ■■■ 静岡県 ■■■",
            "220010" to SEPARATOR + "静岡",
            "220020" to SEPARATOR + "網代",
            "220030" to SEPARATOR + "三島",
            "220040" to SEPARATOR + "浜松",

            // 愛知県
            "23" to " ■■■ 愛知県 ■■■",
            "230010" to SEPARATOR + "名古屋",
            "230020" to SEPARATOR + "豊橋",

            // 三重県
            "24" to " ■■■ 三重県 ■■■",
            "240010" to SEPARATOR + "津",
            "240020" to SEPARATOR + "尾鷲",

            // 滋賀県
            "25" to " ■■■ 滋賀県 ■■■",
            "250010" to SEPARATOR + "大津",
            "250020" to SEPARATOR + "彦根",

            // 京都府
            "26" to " ■■■ 京都府 ■■■",
            "260010" to SEPARATOR + "京都",
            "260020" to SEPARATOR + "舞鶴",

            // 大阪府
            "27" to " ■■■ 大阪府 ■■■",
            "270000" to SEPARATOR + "大阪",

            // 兵庫県
            "28" to " ■■■ 兵庫県 ■■■",
            "280010" to SEPARATOR + "神戸",
            "280020" to SEPARATOR + "豊岡",

            // 奈良県
            "29" to " ■■■ 奈良県 ■■■",
            "290010" to SEPARATOR + "奈良",
            "290020" to SEPARATOR + "風屋",

            // 和歌山県
            "30" to " ■■■和歌山県■■■",
            "300010" to SEPARATOR + "和歌山",
            "300020" to SEPARATOR + "潮岬",

            // 鳥取県
            "31" to " ■■■ 鳥取県 ■■■",
            "310010" to SEPARATOR + "鳥取",
            "310020" to SEPARATOR + "米子",

            // 島根県
            "32" to " ■■■ 島根県 ■■■",
            "320010" to SEPARATOR + "松江",
            "320020" to SEPARATOR + "浜田",
            "320030" to SEPARATOR + "西郷",

            // 岡山県
            "33" to " ■■■ 岡山県 ■■■",
            "330010" to SEPARATOR + "岡山",
            "330020" to SEPARATOR + "津山",

            // 広島県
            "34" to " ■■■ 広島県 ■■■",
            "340010" to SEPARATOR + "広島",
            "340020" to SEPARATOR + "庄原",

            // 山口県
            "35" to " ■■■ 山口県 ■■■",
            "350010" to SEPARATOR + "下関",
            "350020" to SEPARATOR + "山口",
            "350030" to SEPARATOR + "柳井",
            "350040" to SEPARATOR + "萩",

            // 徳島県
            "36" to " ■■■ 徳島県 ■■■",
            "360010" to SEPARATOR + "徳島",
            "360020" to SEPARATOR + "日和佐",

            // 香川県
            "37" to " ■■■ 香川県 ■■■",
            "370000" to SEPARATOR + "高松",

            // 愛媛県
            "38" to " ■■■ 愛媛県 ■■■",
            "380010" to SEPARATOR + "松山",
            "380020" to SEPARATOR + "新居浜",
            "380030" to SEPARATOR + "宇和島",

            // 高知県
            "39" to " ■■■ 高知県 ■■■",
            "390010" to SEPARATOR + "高知",
            "390020" to SEPARATOR + "室戸岬",
            "390030" to SEPARATOR + "清水",

            // 福岡県
            "40" to " ■■■ 福岡県 ■■■",
            "400010" to SEPARATOR + "福岡",
            "400020" to SEPARATOR + "八幡",
            "400030" to SEPARATOR + "飯塚",
            "400040" to SEPARATOR + "久留米",

            // 佐賀県
            "41" to " ■■■ 佐賀県 ■■■",
            "410010" to SEPARATOR + "佐賀",
            "410020" to SEPARATOR + "伊万里",

            // 長崎県
            "42" to " ■■■ 長崎県 ■■■",
            "420010" to SEPARATOR + "長崎",
            "420020" to SEPARATOR + "佐世保",
            "420030" to SEPARATOR + "厳原",
            "420040" to SEPARATOR + "福江",

            // 熊本県
            "43" to " ■■■ 熊本県 ■■■",
            "430010" to SEPARATOR + "熊本",
            "430020" to SEPARATOR + "阿蘇乙姫",
            "430030" to SEPARATOR + "牛深",
            "430040" to SEPARATOR + "人吉",

            // 大分県
            "44" to " ■■■ 大分県 ■■■",
            "440010" to SEPARATOR + "大分",
            "440020" to SEPARATOR + "中津",
            "440030" to SEPARATOR + "日田",
            "440040" to SEPARATOR + "佐伯",

            // 宮城県
            "45" to " ■■■ 宮城県 ■■■",
            "450010" to SEPARATOR + "宮崎",
            "450020" to SEPARATOR + "延岡",
            "450030" to SEPARATOR + "都城",
            "450040" to SEPARATOR + "高千穂",

            // 鹿児島県
            "46" to " ■■■ 鹿児島県 ■■■",
            "460010" to SEPARATOR + "鹿児島",
            "460020" to SEPARATOR + "鹿屋",
            "460030" to SEPARATOR + "種子島",
            "460040" to SEPARATOR + "名瀬",

            // 沖縄県
            "47" to " ■■■ 沖縄県 ■■■",
            "471010" to SEPARATOR + "那覇",
            "471020" to SEPARATOR + "名護",
            "471030" to SEPARATOR + "久米島",
            "472000" to SEPARATOR + "南大東",
            "473000" to SEPARATOR + "宮古島",
            "474010" to SEPARATOR + "石垣島",
            "474020" to SEPARATOR + "与那国島",
        )


        // 地域 リスト値（英文）
        val NARROW_AREA_LIST_EN: Map<String, String> = mapOf(
            // 北海道
            "011000" to "Hokkaido - Wakkanai",

            "013010" to "Hokkaido - Abashiri",
            "013020" to "Hokkaido - Kitami",
            "013030" to "Hokkaido - Mombetsu",

            "014010" to "Hokkaido - Nemuro",
            "014020" to "Hokkaido - Kushiro",
            "014030" to "Hokkaido - Obihiro",

            "015010" to "Hokkaido - Muroran",
            "015020" to "Hokkaido - Urakawa",

            "016010" to "Hokkaido - Sapporo",
            "016020" to "Hokkaido - Iwamizawa",
            "016030" to "Hokkaido - Kutchancho",

            "017010" to "Hokkaido - Hakodate",
            "017020" to "Hokkaido - Esashi",

            // 青森県
            "020010" to "Aomori",
            "020020" to "Aomori- Mutsu",
            "020030" to "Aomori- Hachinohe",

            // 岩手県
            "030010" to "Iwate - Morioka",
            "030020" to "Iwate - Miyako",
            "030030" to "Iwate - Ofunato",

            // 宮城県
            "040010" to "Miyagi - Sendai",
            "040020" to "Miyagi - Shiroishi",

            // 秋田県
            "050010" to "Akita",
            "050020" to "Akita - Yokote",

            // 山形県
            "060010" to "Yamagata",
            "060020" to "Yamagata - Yonezawa",
            "060030" to "Yamagata - Sakata",
            "060040" to "Yamagata - Shinjo",

            // 福島県
            "070010" to "Fukushima",
            "070020" to "Fukushima - Onahama",
            "070030" to "Fukushima - Wakamatsu",

            // 茨城県
            "080010" to "Ibaraki - Mito",
            "080020" to "Ibaraki -Tsuchiura",

            // 栃木県
            "090010" to "Tochigi - Utsunomiya",
            "090020" to "Tochigi - Otawara",

            // 群馬県
            "100010" to "Gunma - Maebashi",
            "100020" to "Gunma - Minakami",

            // 埼玉県
            "110010" to "Saitama",
            "110020" to "Saitama - Kumagaya",
            "110030" to "Saitama - Chichibu",

            // 千葉県
            "120010" to "Chiba",
            "120020" to "Choshi",
            "120030" to "Tateyama",

            // 東京都
            "130010" to "Tokyo",
            "130020" to "Tokyo -Oshima",
            "130030" to "Tokyo - Hachijo-jima",
            "130040" to "Tokyo - Chichi-jima",

            // 神奈川県
            "140010" to "Kanagawa - Yokohama",
            "140020" to "Kanagawa - Odawara",

            // 新潟県
            "150010" to "Niigata",
            "150020" to "Niigata - Nagaoka",
            "150030" to "Niigata - Takada",
            "150040" to "Niigata - Aikawa",

            // 富山県
            "160010" to "Toyama",
            "160020" to "Toyama - Fushiki",

            // 石川県
            "170010" to "Ishikawa - Kanazawa",
            "170020" to "Ishikawa - Wajima",

            // 福井県
            "180010" to "Fukui",
            "180020" to "Fukui - Tsuruga",

            // 山梨県
            "190010" to "Yamanashi - Kofu",
            "190020" to "Yamanashi - Kawaguchiko",

            // 長野県
            "200010" to "Nagano",
            "200020" to "Nagano - Matsumoto",
            "200030" to "Nagano - Iida",

            // 岐阜県
            "210010" to "Gifu",
            "210020" to "Gifu - Takayama",

            // 静岡県
            "220010" to "Shizuoka",
            "220020" to "Shizuoka - Ajiro",
            "220030" to "Shizuoka - Mishima",
            "220040" to "Shizuoka - Hamamatsu",

            // 愛知県
            "230010" to "Aichi - Nagoya",
            "230020" to "Aichi - Toyohashi",

            // 三重県
            "240010" to "Mie - Tsu",
            "240020" to "Mie - Owase",

            // 滋賀県
            "250010" to "Shiga - Otsu",
            "250020" to "Shiga - Hikone",

            // 京都府
            "260010" to "Kyoto",
            "260020" to "Kyoto - Maizuru",

            // 大阪府
            "270000" to "Osaka",

            // 兵庫県
            "280010" to "Hyogo - Kobe",
            "280020" to "Hyogo - Toyooka",

            // 奈良県
            "290010" to "Nara",
            "290020" to "Nara - Kazeya",

            // 和歌山県
            "300010" to "Wakayama",
            "300020" to "Wakayama - Shiono-misaki",

            // 鳥取県
            "310010" to "Tottori",
            "310020" to "Tottori - Yonago",

            // 島根県
            "320010" to "Shimane - Matsue",
            "320020" to "Shimane - Hamada",
            "320030" to "Shimane - Saigo",

            // 岡山県
            "330010" to "Okayama",
            "330020" to "Okayama - Tsuyama",

            // 広島県
            "340010" to "Hiroshima",
            "340020" to "Hiroshima - Syoubara",

            // 山口県
            "350010" to "Yamaguchi - Shimonoseki",
            "350020" to "Yamaguchi",
            "350030" to "Yamaguchi - Yanai",
            "350040" to "Yamaguchi - Hagi",

            // 徳島県
            "360010" to "Tokushima",
            "360020" to "Tokushima - Hiwasa",

            // 香川県
            "370000" to "Kagawa - Takamatsu",

            // 愛媛県
            "380010" to "Ehime - Matsuyama",
            "380020" to "Ehime - Niihama",
            "380030" to "Ehime - Uwajima",

            // 高知県
            "390010" to "Kochi",
            "390020" to "Kochi - Muroto-misaki",
            "390030" to "Kochi - Shimizu",

            // 福岡県
            "400010" to "Fukuoka",
            "400020" to "Fukuoka - Yahata",
            "400030" to "Fukuoka - Iizuka",
            "400040" to "Fukuoka - Kurume",

            // 佐賀県
            "410010" to "Saga",
            "410020" to "Saga - Imari",

            // 長崎県
            "420010" to "Nagasaki",
            "420020" to "Nagasaki - Sasebo",
            "420030" to "Nagasaki - Izuhara",
            "420040" to "Nagasaki - Fukue",

            // 熊本県
            "430010" to "Kumamoto",
            "430020" to "Kumamoto - Aso-OtoHime",
            "430030" to "Kumamoto - Ushibuka",
            "430040" to "Kumamoto - Hitoyoshi",

            // 大分県
            "440010" to "Oita",
            "440020" to "Oita - Nakatsu",
            "440030" to "Oita - Hita",
            "440040" to "Oita - Saiki",

            // 宮城県
            "450010" to "Miyazaki",
            "450020" to "Miyazaki - Nobeoka",
            "450030" to "Miyazaki - Miyakonojo",
            "450040" to "Miyazaki - Takachiho",

            // 鹿児島県
            "460010" to "Kagoshima",
            "460020" to "Kagoshima - Kanoya",
            "460030" to "Kagoshima - Tanega-shima",
            "460040" to "Kagoshima - Naze",

            // 沖縄県
            "471010" to "Okinawa - Naha",
            "471020" to "Okinawa - Nago",
            "471030" to "Okinawa - Kume-jima",
            "472000" to "Okinawa - Minamidaito",
            "473000" to "Okinawa - Miyako-jima",
            "474010" to "Okinawa - Ishigaki-jima",
            "474020" to "Okinawa - Yonaguni-jima",
        )
    }
}