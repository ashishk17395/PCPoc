package com.ta.pcpoc.displayOverOTherApps

import android.database.Observable
import androidx.lifecycle.Observer
import androidx.room.*
import io.reactivex.Flowable
import java.util.*

@Dao
interface LockAppDao {

    @Query("SELECT * FROM apps")
    fun getAll(): List<LockAppEntity>

    @Query("SELECT * FROM apps WHERE appName LIKE :appName")
    fun findByAppName(appName: String): LockAppEntity

    @Insert
    fun insertAll(vararg lockAppEntity: LockAppEntity)

    @Query("SELECT * FROM apps")
    fun getLockedApps(): Flowable<List<LockAppEntity>>

    @Update
    fun updateAppInfo(vararg lockAppEntity: LockAppEntity)
}