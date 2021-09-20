package com.app.monitor.di

import com.app.monitor.adapters.detail.DetailAdapter
import com.app.monitor.adapters.monitor.MonitorAdapter
import com.app.monitor.db.AppDatabase
import com.app.monitor.repository.Repository
import com.app.monitor.ui.detail.DetailViewModel
import com.app.monitor.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dbModule = module {
    single { AppDatabase.getAppDatabase(
        context = get()
    )}
    factory { get<AppDatabase>().monitorDao() }
}

val repositoryModule = module {
    single { Repository(get()) }
}

val uiModuleMonitor = module {
    factory { MonitorAdapter() }
    viewModel { HomeViewModel(get()) }
}

val uiModuleDetail = module {
    factory { DetailAdapter() }
    viewModel { DetailViewModel(get()) }
}