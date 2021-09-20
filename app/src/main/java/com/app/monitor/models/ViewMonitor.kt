package com.app.monitor.models

data class ViewMonitor(
        val code: String,
        val index: String,
        var title: String,
        var status: String,
        val row: Int,
        val update: Long,
        val monitoring: Int?,
        var count: String
)