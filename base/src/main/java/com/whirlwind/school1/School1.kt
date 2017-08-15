package com.whirlwind.school1

import android.app.Application
import android.support.v4.content.LocalBroadcastManager

class School1 :Application() {
    override fun onCreate() {
        super.onCreate()
        LocalBroadcastManager.getInstance(this)
    }
}