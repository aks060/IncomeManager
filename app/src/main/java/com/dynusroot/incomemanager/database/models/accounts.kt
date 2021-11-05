package com.dynusroot.incomemanager.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*


@Entity
@Serializable
data class accounts (
        @PrimaryKey(autoGenerate = true)
        var id:Long,
        var name:String,
        var description:String="",
        var totalBalance:Double= 0.0,
        var createdAt: String=SimpleDateFormat("YYYY-MM-dd HH:MM:SS z").format(Date())
)