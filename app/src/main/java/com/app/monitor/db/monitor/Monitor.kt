package com.app.monitor.db.monitor

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monitor")
data class Monitor(
        @PrimaryKey @ColumnInfo(name = "code") val code: String,
        @ColumnInfo(name = "index") val index: String,
        @ColumnInfo(name = "title") val title: String,
        @ColumnInfo(name = "status") val status: String,
        @ColumnInfo(name = "row") val row: Int,
        @ColumnInfo(name = "update") val update: Long,
        @ColumnInfo(name = "monitoring", defaultValue = "1") val monitoring: Int?
)