/**
 * ------------------------------------------------------------
 * UI状態保持用の変数群
 * ------------------------------------------------------------
 */
package com.weatherprint

import com.weatherprint.ConstantParameters.Companion.NARROW_AREA_LIST

data class AppUiState(

    // 実行確認ダイアログ関連
    val exeVisible: Boolean = false,

    // 印刷ボタン制御
    val isPrintButton: Boolean = true,

    // UI状態復元判定用フラグ
    val isDataRecovery: Boolean = false,

    // 準正常系ダイアログ関連
    val alertVisible: Boolean = false,
    val dialogText: String = "",
    val dialogTitle: String = "",

    // コンテンツスイッチ
    val isToday: Boolean = true,
    val isTomorrow: Boolean = false,
    val isAfterTomorrow: Boolean = false,
    val isOverview: Boolean = false,

    // 地域 初期値
    val expanded: Boolean = false,
    val narrowAreaKey: String = "120010",
    val narrowAreaValue: String = NARROW_AREA_LIST[narrowAreaKey].toString(),

)