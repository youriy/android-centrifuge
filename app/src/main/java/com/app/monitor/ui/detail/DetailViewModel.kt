package com.app.monitor.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.monitor.db.log.Log
import com.app.monitor.models.DefaultMonitor
import com.app.monitor.repository.Repository

class DetailViewModel(private var repository: Repository) : ViewModel() {

    fun getMonitorByCode(code: String): LiveData<DefaultMonitor> {
        return repository.getMonitorByCode(code)
    }

    /**
     * На данный момент не используется
     */
    fun getLogsByCode(code: String): List<Log> {
        val logList: List<Log> = repository.getLogsByCode(code)
        logList.forEach{
            if (it.view == 0) {
                it.id?.let { it1 -> repository.updateLogViewById(it1) }
            }
        }
        return logList
    }

    fun deleteLogsByCode(code: String) {
        repository.deleteLogsByCode(code)
    }

    fun deleteLogById(id: Int) {
        repository.deleteLogById(id)
    }
}