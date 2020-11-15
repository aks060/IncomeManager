package com.dynusroot.incomemanager.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dynusroot.incomemanager.database.models.accounts
import com.dynusroot.incomemanager.database.models.subaccounts
import com.dynusroot.incomemanager.database.models.transactions
import com.dynusroot.incomemanager.database.models.userDBModel

@Database(version = 10, entities = [userDBModel::class, accounts::class, subaccounts::class, transactions::class])
abstract class incomemanager_db: RoomDatabase() {
    abstract val dbDao: db_dao

    companion object{

        @Volatile
        private var INSTANCE: incomemanager_db? = null

        fun get(application: Application) : incomemanager_db {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        application,
                        incomemanager_db::class.java,
                        "incomemanager_db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
                return instance
            }
        }
    }
}