package com.example.aikotlin.database

import androidx.room.TypeConverter
import java.util.Date

/**
 * TypeConverter类，用于处理Room数据库中的Date类型
 * 将Date转换为Long存储，从Long恢复为Date
 */
class DateTypeConverter {
    
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let { Date(it) }
    }
}