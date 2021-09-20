package com.app.monitor.core

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*


object Helper {

    @JvmStatic
    fun removeHTMLTags(str: String): String {
        return str.replace("<br>".toRegex(), "\n")
                .replace("<.*?>".toRegex(), "")
                .replace(" %".toRegex(), "%")
    }

    @JvmStatic
    fun removeHTMLTags2(str: String): String {
        return str.replace("<br>".toRegex(), " ")
                .replace("<.*?>".toRegex(), "")
                .replace(" %".toRegex(), "%")
    }

    @JvmStatic
    fun getTimestamp(): Long {
        return Date().time
    }

    @JvmStatic
    @SuppressLint("SimpleDateFormat")
    fun convertLongToTime(time: Long): String {
        val currentDay = Calendar.getInstance()
        val day = Calendar.getInstance()
        var format = SimpleDateFormat("HH:mm:ss")
        val timeMinSec = format.format(Date(time))
        day.timeInMillis = time

        return when (true) {
            currentDay.get(Calendar.DATE) == day.get(Calendar.DATE) -> {
                "сегодня, $timeMinSec"
            }
            currentDay.get(Calendar.DATE) - day.get(Calendar.DATE) == 1 -> {
                "вчера, $timeMinSec"
            }
            else -> {
                format = SimpleDateFormat("d MMM HH:mm:ss", Locale("ru"))
                format.format(Date(time))
            }
        }
    }

    @JvmStatic
    fun getTimeError(start: Long, end: Long?): String {
        var result: String?
        result = "Начало: " + convertLongToTime(start)
        result += ". Завершено: " + end?.let { convertLongToTime(it) }
        return result
    }
}