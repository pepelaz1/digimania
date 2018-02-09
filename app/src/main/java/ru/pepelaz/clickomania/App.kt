package ru.pepelaz.clickomania

import android.app.Application
import com.rollbar.android.Rollbar

/**
 * Created by pepel on 02.02.2018.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Rollbar.init(this, "541d9bf19dcc4872975ad4fb1266de54", "production")
    }
}