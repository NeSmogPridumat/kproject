package com.dteam.kproject.database

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@InstallIn(ApplicationComponent::class)
@Module
object DBModule {
    @Provides
    fun provideMyTimetable(@ApplicationContext appContext: Context): MyTimetableDao {
        return AppDataBase.getAppDataBase(appContext)!!.myTimetableDao()
    }
}