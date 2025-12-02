package com.example.aikotlin.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    
    // 格式化日期为易读格式
    fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(date)
    }
    
    // 格式化日期为相对时间（如：3小时前）
    fun formatRelativeTime(date: Date): String {
        val now = Date()
        val diffInMillis = now.time - date.time
        val diffInSeconds = diffInMillis / 1000
        val diffInMinutes = diffInSeconds / 60
        val diffInHours = diffInMinutes / 60
        val diffInDays = diffInHours / 24
        
        return when {
            diffInDays > 0 -> "${diffInDays}天前"
            diffInHours > 0 -> "${diffInHours}小时前"
            diffInMinutes > 0 -> "${diffInMinutes}分钟前"
            else -> "刚刚"
        }
    }
}