package com.example.poibrowser

import android.app.Application
import com.example.poibrowser.di.AppModule
import com.example.poibrowser.di.DataModule
import com.example.poibrowser.di.NetModule
import com.example.poibrowser.di.RepoModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module

/**
 * @author Tomislav Curis
 */
class TestApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@TestApp)
            modules(listOf(AppModule, DataModule, NetModule, RepoModule))
        }
    }

    internal fun injectModule(module: Module) {
        loadKoinModules(module)
    }
}