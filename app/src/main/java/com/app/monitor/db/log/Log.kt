package com.app.monitor.db.log

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "log")
data class Log(
        @PrimaryKey(autoGenerate = true) val id: Int?,
        @ColumnInfo(name = "code") val code: String,
        @ColumnInfo(name = "title") val title: String,
        @ColumnInfo(name = "status") val status: String,
        @ColumnInfo(name = "start") val start: Long,
        @ColumnInfo(name = "end") val end: Long?,
        @ColumnInfo(name = "view") val view: Int
        )