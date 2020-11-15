package com.dynusroot.incomemanager.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*


@Entity
data class accounts(
        @PrimaryKey(autoGenerate = true)
        var id:Long,
        var name:String,
        var description:String="",
        var totalBalance:Double= 0.0,
        var createdAt: String=SimpleDateFormat("YYYY-MM-dd HH:MM:SS z").format(Date())
)