package com.dynusroot.incomemanager.database.models

import androidx.annotation.VisibleForTesting
import androidx.room.*

@Entity(tableName = "user", indices = [ Index(value = [ "username"], unique = true) ])
data class userDBModel(
    @PrimaryKey(autoGenerate = true)
    var id: Long=0L,
    var name: String,
    var username: String,
    var password: String,
    var loggedin: Int=0
)