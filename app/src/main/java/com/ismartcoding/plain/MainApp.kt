package com.ismartcoding.plain

import android.app.Application
import android.os.Build
import com.ismartcoding.lib.brv.utils.BRV
import com.ismartcoding.lib.helpers.CoroutinesHelper.coIO
import com.ismartcoding.plain.features.AppEvents
import com.ismartcoding.plain.features.bluetooth.BluetoothEvents
import com.ismartcoding.plain.features.box.BoxEvents
import com.ismartcoding.plain.features.theme.AppThemeHelper
import com.ismartcoding.plain.ui.helpers.PageHelper
import com.ismartcoding.plain.web.HttpServerManager
import com.ismartcoding.plain.workers.FeedFetchWorker
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKVLogLevel
import io.ktor.server.netty.*

class MainApp : Application() {
    var httpServer: NettyApplicationEngine? = null

    override fun onCreate() {
        super.onCreate()

        instance = this

        MMKV.initialize(this, MMKVLogLevel.LevelNone)

        AppThemeHelper.init()
        LocalStorage.init()

        BRV.modelId = BR.m

        PageHelper.init()

        BluetoothEvents.register()
        AppEvents.register()
        BoxEvents.register()

        coIO {
            HttpServerManager.loadTokenCache()
            if (LocalStorage.feedAutoRefresh) {
                FeedFetchWorker.startRepeatWorker()
            }
            HttpServerManager.clientTsInterval()
        }
    }

    companion object {
        lateinit var instance: MainApp

        fun getAppVersion(): String {
            return BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")"
        }

        fun getAndroidVersion(): String {
            return Build.VERSION.RELEASE + " (" + Build.VERSION.SDK_INT + ")"
        }
    }
}
