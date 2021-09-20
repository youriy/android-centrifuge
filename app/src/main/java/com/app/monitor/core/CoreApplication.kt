package com.app.monitor.core

import android.app.Application
import com.app.monitor.di.*
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidContext

class CoreApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CoreApplication)
            modules(listOf(dbModule, repositoryModule, uiModuleMonitor, uiModuleDetail))
        }
    }
}