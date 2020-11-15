package com.dynusroot.incomemanager.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class transactions(
    @PrimaryKey(autoGenerate = true)
    var id:Long=0,
    var type:String,
    var subaccountID: Long,
    var amount: Double,
    var description:String,
    var amountafter: Double?,
    var transferto: Long=0,
    var orderBydate: String="",
    var date:String
)