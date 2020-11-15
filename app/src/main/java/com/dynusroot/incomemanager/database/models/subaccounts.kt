package com.dynusroot.incomemanager.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = arrayOf(ForeignKey(entity = accounts::class, parentColumns = arrayOf("id"),
    childColumns = arrayOf("parentaccount"),
    onDelete = ForeignKey.CASCADE)))
data class subaccounts(
    @PrimaryKey(autoGenerate = true)
    var id:Long=0,
    var name: String,
    var parentaccount: Long,
    var description:String,
    var balance:Double=0.0
)