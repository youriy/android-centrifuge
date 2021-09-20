package com.app.monitor.models

data class DefaultMonitor(
        val code: String,
        val index: String,
        val title: String,
        var status: String,
        val row: Int,
        val update: Long
)