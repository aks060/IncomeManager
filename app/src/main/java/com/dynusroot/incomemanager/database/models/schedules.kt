package com.dynusroot.incomemanager.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class schedules(
    @PrimaryKey(autoGenerate = true)
    var id: Long=0,
    var account: Long,
    var desc: String,
    var amount: Double,
    var interval: String,
    var txntype: String,
    var transferto: Long=0,
    var specificTime: String?=null
)
