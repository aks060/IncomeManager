package com.dynusroot.incomemanager.worker

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dynusroot.incomemanager.backupclass.accountsBackup
import com.dynusroot.incomemanager.backupclass.schedulesBackup
import com.dynusroot.incomemanager.backupclass.subaccountsBackup
import com.dynusroot.incomemanager.backupclass.transactionsBackup
import com.dynusroot.incomemanager.database.incomemanager_db
import com.dynusroot.incomemanager.database.models.accounts
import com.dynusroot.incomemanager.database.models.schedules
import com.dynusroot.incomemanager.database.models.subaccounts
import com.dynusroot.incomemanager.database.models.transactions
import java.io.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class Restore (var context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
)
{
    val db= incomemanager_db.get(context as Application).dbDao
    override fun doWork(): Result {
        var filename="IncomeManager-backup.txt"
        try {
            Log.e("Restore", "Restore in progress")
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(path, filename)
            val fos: FileInputStream = FileInputStream(file)
            val os = ObjectInputStream(fos)
            var obj=os.readObject() as ArrayList<ArrayList<Any>>
            var accounts = obj[0]
            for(i in accounts){
                var ia = i as accountsBackup
                db.addacount(accounts(ia.id, ia.name, ia.description, ia.totalBalance, ia.createdAt))
            }

            var subaccounts = obj[1]
            for(i in subaccounts){
                var ia = i as subaccountsBackup
                db.addSubAccount(subaccounts(ia.id, ia.name, ia.parentaccount, ia.description, ia.balance))
            }

            var transactions = obj[2]
            for(i in transactions){
                var ia = i as transactionsBackup
                db.addTransaction(transactions(ia.id, ia.type, ia.subaccountID, ia.amount, ia.description, ia.amountafter, ia.transferto, ia.orderBydate, ia.date))
            }

            var schedules = obj[3]
            for(i in schedules){
                var ia = i as schedulesBackup
                db.scheduleTransaction(schedules(ia.id, ia.account, ia.desc, ia.amount, ia.interval, ia.txntype, ia.transferto, ia.specificTime))
            }

            Log.e("Restore-Any", accounts.toString())
            os.close()
            fos.close()
        } catch (e: Exception) {
            Log.e("Restore", e.toString())
            return Result.failure()
        }
        Log.e("Restore", "Done")
        return Result.success()
    }

}
