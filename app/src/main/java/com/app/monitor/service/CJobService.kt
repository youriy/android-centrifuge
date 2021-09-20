package com.app.monitor.service

/**
 * На данный момент не используется
 */
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import com.app.monitor.core.Centrifuga
import com.app.monitor.core.Helper
import com.app.monitor.core.PreferenceHelper.get
import com.app.monitor.models.DefaultMonitor
import com.app.monitor.repository.Repository
import io.github.centrifugal.centrifuge.*
import org.json.JSONArray
import org.koin.android.ext.android.inject
import java.nio.charset.StandardCharsets


class CJobService : JobService() {

    private lateinit var centrifuga: Centrifuga
    private val repository: Repository by inject()
    private val preference: SharedPreferences by inject()

    override fun onStartJob(params: JobParameters): Boolean {
        val subListener: SubscriptionEventListener = object : SubscriptionEventListener() {

            private val prefLog = preference["log", false]

            override fun onPublish(sub: Subscription?, event: PublishEvent?) {
                super.onPublish(sub, event)
                val data = String(event!!.data, StandardCharsets.UTF_8)
                val dataJsonArrayLv1 = JSONArray(data)
                for (i in 0 until dataJsonArrayLv1.length()) {
                    val dataJsonArrayLv2 = dataJsonArrayLv1.getJSONArray(i)

                    if (dataJsonArrayLv2.length() > 0) {
                        for (l in 0 until dataJsonArrayLv2.length()) {
                            val jsonObject = dataJsonArrayLv2.getJSONObject(l)
                            val status = jsonObject.getString("status")
                            val code = jsonObject.getString("code")
                            val title = jsonObject.getString("title")

                            if (prefLog) {
                                writeLog(status, code, title)
                            }

                            repository.insertMonitor(DefaultMonitor(code, jsonObject.getString("index"), title, status, i, Helper.getTimestamp()))
                        }
                    }
                }
            }
        }

        //centrifuga = Centrifuga(subListener)
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        centrifuga.disconnect()
        return true
    }

    private fun writeLog(status: String, code: String, title: String) {
        val currentStatus = repository.getStatusByCode(code)

        if (!currentStatus.isNullOrEmpty() && currentStatus != status) {
            when (status) {
                "warning" -> {
                    when (currentStatus) {
                        "success" -> {
                            repository.insertLog(com.app.monitor.db.log.Log(null, code, title, status, Helper.getTimestamp(), null, 0))
                        }
                        "error" -> {
                            val log = repository.getWarningOrError(code, currentStatus)
                            if (log != null) {
                                repository.insertLog(com.app.monitor.db.log.Log(log.id, log.code, log.title, log.status, log.start, Helper.getTimestamp(), log.view))
                            }
                            repository.insertLog(com.app.monitor.db.log.Log(null, code, title, status, Helper.getTimestamp(), null, 0))
                        }
                    }
                }
                "error" -> {
                    when (currentStatus) {
                        "success" -> {
                            repository.insertLog(com.app.monitor.db.log.Log(null, code, title, status, Helper.getTimestamp(), null, 0))
                        }
                        "warning" -> {
                            val log = repository.getWarningOrError(code, currentStatus)
                            if (log != null) {
                                repository.insertLog(com.app.monitor.db.log.Log(log.id, log.code, log.title, log.status, log.start, Helper.getTimestamp(), log.view))
                            }
                            repository.insertLog(com.app.monitor.db.log.Log(null, code, title, status, Helper.getTimestamp(), null, 0))
                        }
                    }
                }
                "success" -> {
                    if (currentStatus == "warning" || currentStatus == "error") {
                        val log = repository.getWarningOrError(code, currentStatus)
                        if (log != null) {
                            repository.insertLog(com.app.monitor.db.log.Log(log.id, log.code, log.title, log.status, log.start, Helper.getTimestamp(), log.view))
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun scheduleJob(context: Context) {
            val serviceComponent = ComponentName(context, CJobService::class.java)
            val builder = JobInfo.Builder(0, serviceComponent)
            builder.setRequiresCharging(false)
            builder.setRequiresDeviceIdle(false)
            builder.setRequiresBatteryNotLow(false)
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            builder.setPeriodic((30*60*1000).toLong())
            val jobScheduler: JobScheduler = context.getSystemService(JobScheduler::class.java)
            jobScheduler.schedule(builder.build())
        }

        private const val TAG = "JobService"
    }
}