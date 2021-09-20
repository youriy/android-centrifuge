package com.app.monitor.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.monitor.MainActivity
import com.app.monitor.core.PreferenceHelper.get
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONArray
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val serverKey: String = "key=AAAAG5VxiT0:APA91bE1PIvEdmy8Z-IodUPUU1s"
    private val notificationKeyName: String = "monitor"
    private val notificationKey: String = "APA91bHbrgbsgKhGoikuuVTrI3NtY0B"
    private val senderId: String = "1"
    private val googleUrl: String ="https://fcm.googleapis.com/fcm/notification"

    private val preference: SharedPreferences by inject()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty() && preference["notify", true]) {
            remoteMessage.data["body"]?.let { centrifugaOffNotify(it) }
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
        PostToGoogleAsyncTask().execute(token)
    }

    @SuppressLint("StaticFieldLeak")
    private inner class PostToGoogleAsyncTask() : AsyncTask<String, Unit, Unit>() {
        override fun doInBackground(vararg params: String) {

            val jsonParam = JSONObject()
            jsonParam.put("operation", "add")
            jsonParam.put("notification_key_name", notificationKeyName)
            jsonParam.put("notification_key", notificationKey)
            jsonParam.put("registration_ids", JSONArray(arrayOf<Any>(params[0])))
            try {
                val url = URL(googleUrl)
                val conn = url.openConnection() as HttpsURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setRequestProperty("Authorization", serverKey)
                conn.setRequestProperty("project_id", senderId)
                conn.doOutput = true
                conn.doInput = true
                conn.connect()
                val bw = BufferedWriter(OutputStreamWriter(conn.outputStream, "UTF-8"))
                bw.write(jsonParam.toString())
                bw.flush()
                bw.close()
                val inputStream = conn.inputStream
                val buffer = StringBuilder()
                val reader = BufferedReader(InputStreamReader(inputStream))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    buffer.append(line)
                }
                conn.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    private fun centrifugaOffNotify(message: String) {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "monitor_channel_error"
        val channelName = "Канал для ошибок"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "monitor"
        channel.enableLights(true)
        channel.lightColor = Color.BLUE
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(1000, 1000)
        notificationManager.createNotificationChannel(channel)

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(com.app.monitor.R.drawable.baseline_monitor_24)
            .setContentTitle("Монитор")
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setLights(Color.BLUE, 3000, 3000)
            .setVibrate(longArrayOf(1000, 1000))
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}