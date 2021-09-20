package com.app.monitor.ui.home

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.monitor.core.Helper
import com.app.monitor.models.ViewMenu
import com.app.monitor.models.ViewMonitor
import com.app.monitor.repository.Repository
import java.util.*


class HomeViewModel(private var repository: Repository) : ViewModel() {

    var all: MutableLiveData<List<ViewMonitor>> = MutableLiveData()
    var count: MutableLiveData<ViewMenu> = MutableLiveData()
    var sort: String = ""
    var querySort: String = ""
    var monitoring: Int = 1

    private val mainHandler = Handler()
    private var list = listOf<ViewMonitor>()

    init {
        mainHandler.post(object : Runnable {
            override fun run() {
                list = repository.getAll()
                count.value = ViewMenu(
                        list.count { it.monitoring == 1 }.toString(),
                        list.count { it.status == "warning" && it.monitoring == 1 }.toString(),
                        list.count { it.status == "error" && it.monitoring == 1 }.toString(),
                        list.count { it.monitoring == 0 }.toString()
                )

                search()
                mainHandler.postDelayed(this, 1500)
            }
        })
    }

    fun search() {
        all.value = list.filter { it.monitoring == monitoring }

        if (sort.isNotEmpty()) {
            all.value = all.value?.filter { it.status == sort }
        }

        if (querySort.isNotEmpty()) {

            if (querySort.toIntOrNull() === null){
                all.value = all.value?.filter { it.code.startsWith(querySort.toUpperCase()) }
            } else {
                all.value = all.value?.filter { it.row == querySort.toInt() }
            }
        }
    }

    fun resetMonitor() {
        repository.resetMonitor()
    }

    fun deleteAllLog() {
        repository.deleteAllLog()
    }

    fun buildData(list: List<ViewMonitor>): List<ViewMonitor> {
        val currentTime = Calendar.getInstance()
        list.forEach {
            it.title = Helper.removeHTMLTags(it.title)

            if (it.count == "0") {
                it.count = ""
            }

            if (currentTime.timeInMillis - it.update > 5*60*1000) {
                it.status = "secondary"
            }
        }

        return list
    }

    fun updateMonitoring(monitoring: Int, code: String) {
        repository.updateMonitoring(monitoring, code)
        search()
    }

    fun updateMonitoringRow(monitoring: Int, row: Int) {
        repository.updateMonitoringRow(monitoring, row)
        search()
    }

    fun updateMonitoringAnException(monitoring: Int, code: String) {
        repository.updateMonitoringAnException(monitoring, code)
        search()
    }

    fun updateMonitoringRowAnException(monitoring: Int, row: Int) {
        repository.updateMonitoringRowAnException(monitoring, row)
        search()
    }

    fun getViewMonitor(): List<ViewMonitor> {
        return repository.getAll().filter { it.monitoring == 1 }
    }
}
