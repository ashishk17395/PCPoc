package com.ta.pcpoc.displayOverOTherApps

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apps")
class LockAppEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "appName")
    var appName: String = ""

    @ColumnInfo(name = "appLock")
    var appLock: Boolean = false
}