package com.app.monitor.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import com.app.monitor.core.Centrifuga
import com.app.monitor.core.Helper
import com.app.monitor.db.monitor.Monitor
import com.app.monitor.models.DefaultMonitor
import com.app.monitor.repository.Repository
import io.github.centrifugal.centrifuge.PublishEvent
import io.github.centrifugal.centrifuge.Subscription
import io.github.centrifugal.centrifuge.SubscriptionEventListener
import org.json.JSONArray
import org.koin.android.ext.android.inject
import java.nio.charset.StandardCharsets


class ReadCentrifuga : Service() {

    private val repository: Repository by inject()

    override fun onCreate() {

        val subListener: SubscriptionEventListener = object : SubscriptionEventListener() {

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
                            val index = jsonObject.getString("index")
                            val monitor = repository.getByCode(code)

                            if (monitor == null) {
                                repository.insertMonitor(DefaultMonitor(code, index, title, status, i, Helper.getTimestamp()))
                            } else {
                                repository.updateMonitor(Monitor(code, index, title, status, i, Helper.getTimestamp(), monitor.monitoring))
                            }
                        }
                    }
                }
            }
        }

        Centrifuga(subListener, Centrifuga.channel)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}