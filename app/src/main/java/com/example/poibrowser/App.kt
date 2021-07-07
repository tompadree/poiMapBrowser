package com.example.poibrowser

import android.app.Application
import com.example.poibrowser.di.AppModule
import com.example.poibrowser.di.DataModule
import com.example.poibrowser.di.NetModule
import com.example.poibrowser.di.RepoModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * @author Tomislav Curis
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(AppModule, DataModule, RepoModule, NetModule))
        }
    }
}