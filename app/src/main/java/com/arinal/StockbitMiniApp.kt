package com.arinal

import android.app.Application
import com.arinal.di.appModule
import com.arinal.di.viewModelModule
import com.google.android.gms.security.ProviderInstaller
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class StockbitMiniApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ProviderInstaller.installIfNeeded(this)
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@StockbitMiniApp)
            androidFileProperties()
            modules(appModule, viewModelModule)
        }
    }

}
