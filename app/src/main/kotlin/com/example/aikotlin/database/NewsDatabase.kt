package com.example.aikotlin.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.model.Settings
import com.example.aikotlin.model.UserPreference
import com.example.aikotlin.BuildConfig // 确认导入路径

@Database(
    entities = [NewsArticle::class, UserPreference::class, Settings::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(DateTypeConverter::class)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
    abstract fun userPreferenceDao(): UserPreferenceDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: NewsDatabase? = null

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS user_preferences " +
                            "(`key` TEXT NOT NULL, `value` TEXT NOT NULL, PRIMARY KEY(`key`))"
                )
            }
        }

        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `settings` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL)"
                )
            }
        }

        fun getDatabase(context: Context): NewsDatabase {
            return INSTANCE ?: synchronized(this) {
                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java,
                    "news_database"
                )

                if (BuildConfig.DEBUG) {
                    // 开发环境：允许破坏性迁移，方便快速迭代和测试
                    builder.fallbackToDestructiveMigration()
                } else {
                    // 生产环境：务必提供 Migration 路径，保证用户数据不丢失
                    builder.addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                }

                val instance = builder.build()
                INSTANCE = instance
                instance
            }
        }
    }
}