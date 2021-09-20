package com.app.monitor.repository

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.app.monitor.db.AppDatabase
import com.app.monitor.db.log.Log
import com.app.monitor.db.log.LogDao
import com.app.monitor.db.monitor.Monitor
import com.app.monitor.db.monitor.MonitorDao
import com.app.monitor.models.*


class Repository(
    private val appDatabase: AppDatabase
) {

    fun getAll(): List<ViewMonitor> {
        return appDatabase.monitorDao().getAll()
    }

    fun getByCode(code: String): Monitor? {
        return appDatabase.monitorDao().getByCode(code)
    }

    fun insertMonitor(defaultMonitor: DefaultMonitor) {
        InsertMonitorAsyncTask(appDatabase.monitorDao()).execute(defaultMonitor)
    }

    private class InsertMonitorAsyncTask(val monitorDao: MonitorDao) : AsyncTask<DefaultMonitor, Unit, Unit>() {
        override fun doInBackground(vararg params: DefaultMonitor?) {
            monitorDao.insert(params[0]!!)
        }
    }

    fun updateMonitor(monitor: Monitor) {
        UpdateMonitorAsyncTask(appDatabase.monitorDao()).execute(monitor)
    }

    private class UpdateMonitorAsyncTask(val monitorDao: MonitorDao) : AsyncTask<Monitor, Unit, Unit>() {
        override fun doInBackground(vararg params: Monitor?) {
            monitorDao.update(params[0]!!)
        }
    }

    fun insertLog(log: Log) {
        InsertLogAsyncTask(appDatabase.logDao()).execute(log)
    }

    private class InsertLogAsyncTask(val logDao: LogDao) : AsyncTask<Log, Unit, Unit>() {
        override fun doInBackground(vararg params: Log?) {
            logDao.insert(params[0]!!)
        }
    }

    fun getAllByStatus(status: String): LiveData<List<ViewMonitor>> {
        return appDatabase.monitorDao().getAllByStatus(status)
    }

    fun getMonitorByCode(code: String): LiveData<DefaultMonitor> {
        return appDatabase.monitorDao().getMonitorByCode(code)
    }

    fun getStatusByCode(code: String): String? {
        return appDatabase.monitorDao().getStatusByCode(code)
    }

    fun getWarningOrError(code: String, status: String): Log? {
        return appDatabase.logDao().getWarningOrError(code, status)
    }

    fun getLogsByCode(code: String): List<Log> {
        return appDatabase.logDao().getAllByCode(code)
    }

    fun deleteLogsByCode(code: String) {
        DeleteLogsByCodeAsyncTask(appDatabase.logDao()).execute(code)
    }

    private class DeleteLogsByCodeAsyncTask(val logDao: LogDao) :  AsyncTask<String, Unit, Unit>() {
        override fun doInBackground(vararg params: String?) {
            logDao.deleteLogsByCode(params[0]!!)
        }
    }

    fun updateLogViewById(id: Int) {
        UpdateLogViewByIdAsyncTask(appDatabase.logDao()).execute(id)
    }

    private class UpdateLogViewByIdAsyncTask(val logDao: LogDao) :  AsyncTask<Int, Unit, Unit>() {
        override fun doInBackground(vararg params: Int?) {
            logDao.updateLogViewById(params[0]!!)
        }
    }

    fun resetMonitor() {
        ResetMonitorAsyncTask(appDatabase.monitorDao()).execute()
    }

    private class ResetMonitorAsyncTask(val monitorDao: MonitorDao) : AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg params: Unit?) {
            monitorDao.resetMonitor()
        }
    }

    fun deleteAllLog() {
        DeleteAllLogAsyncTask(appDatabase.logDao()).execute()
    }

    private class DeleteAllLogAsyncTask(val logDao: LogDao) : AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg params: Unit?) {
            logDao.deleteAllLog()
        }
    }

    fun deleteLogById(id: Int) {
        DeleteLogByIdAsyncTask(appDatabase.logDao()).execute(id)
    }

    private class DeleteLogByIdAsyncTask(val logDao: LogDao) : AsyncTask<Int, Unit, Unit>() {
        override fun doInBackground(vararg params: Int?) {
            logDao.deleteLogById(params[0]!!)
        }
    }

    fun updateMonitoring(monitoring: Int, code: String) {
        UpdateMonitoringAsyncTask(appDatabase.monitorDao()).execute(mapOf(1 to monitoring.toString(), 2 to code))
    }

    private class UpdateMonitoringAsyncTask(val monitorDao: MonitorDao) : AsyncTask<Map<Int, String>, Unit, Unit>() {
        override fun doInBackground(vararg params: Map<Int, String>) {
            monitorDao.updateMonitoring(params[0][1]!!.toInt(), params[0][2]!!)
        }
    }

    fun updateMonitoringRow(monitoring: Int, row: Int) {
        UpdateMonitoringRowAsyncTask(appDatabase.monitorDao()).execute(mapOf(1 to monitoring, 2 to row))
    }

    private class UpdateMonitoringRowAsyncTask(val monitorDao: MonitorDao) : AsyncTask<Map<Int, Int>, Unit, Unit>() {
        override fun doInBackground(vararg params: Map<Int, Int>) {
            monitorDao.updateMonitoringRow(params[0][1]!!, params[0][2]!!)
        }
    }

    fun updateMonitoringAnException(monitoring: Int, code: String) {
        UpdateMonitoringAsyncTaskAnException(appDatabase.monitorDao()).execute(mapOf(1 to monitoring.toString(), 2 to code))
    }

    private class UpdateMonitoringAsyncTaskAnException(val monitorDao: MonitorDao) : AsyncTask<Map<Int, String>, Unit, Unit>() {
        override fun doInBackground(vararg params: Map<Int, String>) {
            monitorDao.updateMonitoringAnException(params[0][1]!!.toInt(), params[0][2]!!)
        }
    }

    fun updateMonitoringRowAnException(monitoring: Int, row: Int) {
        UpdateMonitoringRowAsyncTaskAnException(appDatabase.monitorDao()).execute(mapOf(1 to monitoring, 2 to row))
    }

    private class UpdateMonitoringRowAsyncTaskAnException(val monitorDao: MonitorDao) : AsyncTask<Map<Int, Int>, Unit, Unit>() {
        override fun doInBackground(vararg params: Map<Int, Int>) {
            monitorDao.updateMonitoringRowAnException(params[0][1]!!, params[0][2]!!)
        }
    }

}