package com.arinal

import android.app.Application
import androidx.biometric.BiometricManager
import com.arinal.common.preferences.PreferencesHelper
import com.arinal.common.preferences.PreferencesKey
import com.arinal.di.appModule
import com.arinal.di.viewModelModule
import com.google.android.gms.security.ProviderInstaller
import com.google.firebase.firestore.FirebaseFirestore
import im.crisp.sdk.Crisp
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.util.*

class StockbitMiniApp : Application() {

    private val prefHelper: PreferencesHelper by inject()

    override fun onCreate() {
        super.onCreate()
        ProviderInstaller.installIfNeeded(this)
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@StockbitMiniApp)
            androidFileProperties()
            modules(appModule, viewModelModule)
        }

        val biometric = BiometricManager.from(this).canAuthenticate()
        prefHelper.setInt(PreferencesKey.HAS_BIOMETRIC, biometric)

        if (prefHelper.getString(PreferencesKey.INSTALLATION_ID).isEmpty()) {
            val id: String = UUID.randomUUID().toString()
            prefHelper.setString(PreferencesKey.INSTALLATION_ID, id)
        }

        if (prefHelper.getString(PreferencesKey.CRISP_WEB_ID).isEmpty()) {
            FirebaseFirestore.getInstance().collection("data")
                .document("crisp").get().addOnSuccessListener {
                    val websiteId = it["website_id"].toString()
                    prefHelper.setString(PreferencesKey.CRISP_WEB_ID, websiteId)
                    Crisp.initialize(this)
                    Crisp.getInstance().websiteId = websiteId
                    Crisp.getInstance().locale = "in"
                }
        }
    }

}
