package com.example.autokolk

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val lp = LessonProgress(this)
        HeartRefillJobService.scheduleNext(this, lp)
    }
}


