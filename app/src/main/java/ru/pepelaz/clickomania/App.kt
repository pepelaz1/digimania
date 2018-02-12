package ru.pepelaz.clickomania

import android.app.Application
import com.rollbar.android.Rollbar

/**
 * Created by pepel on 02.02.2018.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Rollbar.init(this, "8bcf05357cfb4591a85652c71f9713c9", "production")
    }
}