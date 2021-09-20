package com.app.monitor.core

import android.util.Log
import io.github.centrifugal.centrifuge.*
import java.nio.charset.StandardCharsets


class Centrifuga(
        subListener: SubscriptionEventListener,
        channel: String
) {
    private val wss: String = "wss://secret"
    private val jwt: String = "secret"
    private val tag: String = "CENTRIFUGA"
    private val client: Client

    companion object {
        const val channel: String = "secret"
    }

    private val listener: EventListener = object : EventListener() {
        override fun onConnect(client: Client?, event: ConnectEvent?) {
            Log.d(tag, "connected")
        }

        override fun onDisconnect(client: Client?, event: DisconnectEvent) {
            Log.d(tag, event.reason + ',' + event.reconnect)
        }

        override fun onError(client: Client?, event: ErrorEvent?) {
            super.onError(client, event)
            Log.d(tag, event.toString())
        }
    }

    private val defaultSubListener: SubscriptionEventListener = object : SubscriptionEventListener() {
        override fun onUnsubscribe(sub: Subscription?, event: UnsubscribeEvent?) {
            super.onUnsubscribe(sub, event)
        }

        override fun onPublish(sub: Subscription?, event: PublishEvent?) {
            super.onPublish(sub, event)
            val data = String(event!!.data, StandardCharsets.UTF_8)
            Log.d(tag,"message from " + sub!!.channel.toString() + " " + data)
        }

        override fun onSubscribeError(sub: Subscription?, event: SubscribeErrorEvent?) {
            Log.d(tag, "subscribe error " + sub?.channel.toString() + " " + event?.message)
        }
    }

    init {
        client = Client(this.wss, Options(), listener)
        client.setToken(this.jwt)
        client.connect()

        val sub: Subscription = client.newSubscription(channel, subListener)
        sub.subscribe()
    }

    fun disconnect() {
        client.disconnect()
    }
}