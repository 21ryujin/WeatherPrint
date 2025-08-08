/**
 * ------------------------------------------------------------
 * UIの状態を保持するデータベース
 * ------------------------------------------------------------
 */
package com.weatherprint

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.weatherprint.DatabaseProvider.getDatabase

// エンティティ（テーブル）定義
@Entity(tableName = "tbUiState")
data class UiState(
    @PrimaryKey val id: Int,
    val isToday: Boolean,           // 今日の天気 スイッチ
    val isTomorrow: Boolean,        // 明日の天気 スイッチ
    val isAfterTomorrow: Boolean,   // 明後日の天気 スイッチ
    val isOverview: Boolean,        // 天気概要 スイッチ
    val narrowAreaKey: String,      // 地域設定プルダウン（Key）
    val narrowAreaValue: String,    // 地域設定プルダウン（Value）
)

// DAO定義
@Dao
interface UiStateDao {
    // データの挿入
    @Insert
    suspend fun insert(uiState: UiState)

    // データの更新
    @Update
    suspend fun update(uiState: UiState)

    // データの削除
    @Delete
    suspend fun delete(uiState: UiState)

    // データカウント
    @Query("SELECT COUNT(*) FROM tbUiState WHERE id = :id")
    suspend fun getCount(id: Int): Int

    // データ全件取得
    @Query("SELECT * FROM tbUiState")
    suspend fun getAllUsers(): List<UiState>

    // ID指定でデータ取得
    @Query("SELECT * FROM tbUiState WHERE id = :id")
    suspend fun getUserById(id: Int): UiState?
}

// DB定義
@Database(entities = [UiState::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun uiStateDao(): UiStateDao
}

// DB準備
object DatabaseProvider {
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}

/**
 * ID条件付きSelect（カウント）の実行
 */
suspend fun selectCount(context: Context, id: Int) : Int {
    // DB接続
    val database = getDatabase(context)
    val uiStateDao = database.uiStateDao()

    // Select（カウント）実行
    val result: Int = uiStateDao.getCount(id)

    // Select（カウント）結果返却
    return result
}

/**
 * ID条件付きSelectの実行
 */
suspend fun selectIdUiState(context: Context, id: Int) : UiState? {
    // DB接続
    val database = getDatabase(context)
    val uiStateDao = database.uiStateDao()

    // Select実行
    val result: UiState? = uiStateDao.getUserById(id)

    // Select結果返却
    return result
}

/**
 * UIの状態保持（Insert）
 */
suspend fun insertUiState(
    context: Context,
    id: Int,
    isToday: Boolean,
    isTomorrow: Boolean,
    isAfterTomorrow: Boolean,
    isOverview: Boolean,
    narrowAreaKey: String,
    narrowAreaValue: String
) {
    // DB接続
    val database = getDatabase(context)
    val uiStateDao = database.uiStateDao()

    // Insertデータ準備
    val uiState = UiState(
        id,
        isToday,
        isTomorrow,
        isAfterTomorrow,
        isOverview,
        narrowAreaKey,
        narrowAreaValue
    )

    // Insert実行
    uiStateDao.insert(uiState)
}


/**
 * UIの状態保持（Update）
 */
suspend fun updateUiState(
    context: Context,
    id: Int,
    isToday: Boolean,
    isTomorrow: Boolean,
    isAfterTomorrow: Boolean,
    isOverview: Boolean,
    narrowAreaKey: String,
    narrowAreaValue: String
) {
    // DB接続
    val database = getDatabase(context)
    val uiStateDao = database.uiStateDao()

    // Updateデータ準備
    val uiState = UiState(
        id,
        isToday,
        isTomorrow,
        isAfterTomorrow,
        isOverview,
        narrowAreaKey,
        narrowAreaValue
    )

    // Update実行
    uiStateDao.update(uiState)
}