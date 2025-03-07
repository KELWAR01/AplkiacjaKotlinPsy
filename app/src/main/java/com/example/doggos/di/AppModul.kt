package com.example.doggos.di

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModul(private val aplikacja: Context) {

    @Provides
    @Singleton
    fun zapewnijContext(): Context {
        return aplikacja
    }
}