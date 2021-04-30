package app.ifnyas.la

import android.app.Application
import android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import app.ifnyas.la.util.FunUtils

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initFun()
    }

    private fun initFun() {
        setDefaultNightMode(MODE_NIGHT_NO)
        if (applicationInfo.flags and FLAG_DEBUGGABLE != 0) initDebug()
    }

    private fun initDebug() {
        //
    }

    companion object {
        val fu by lazy { FunUtils() }
    }
}