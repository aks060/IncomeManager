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
import com.dynusroot.incomemanager.backupclass.subaccountsBackup
import com.dynusroot.incomemanager.backupclass.transactionsBackup
import com.dynusroot.incomemanager.database.incomemanager_db
import com.dynusroot.incomemanager.database.models.accounts
import com.dynusroot.incomemanager.database.models.schedules
import com.dynusroot.incomemanager.database.models.subaccounts
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class Backup (var context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
)
{
    val db= incomemanager_db.get(context as Application).dbDao
    override fun doWork(): Result {
        var filename="IncomeManager-backup.txt"
        try {
            Log.e("Backup", "Backup in progress")
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(path, filename)
            val fos: FileOutputStream = FileOutputStream(file)
            val os = ObjectOutputStream(fos)
            var temp= db.getaccounts()
            var accounts = ArrayList<accountsBackup>()
            for (i in temp){
                accounts.add(accountsBackup(i.id, i.name, i.description, i.totalBalance, i.createdAt))
            }

            var temp1= db.getsubaccounts()
            var subaccounts = ArrayList<subaccountsBackup>()
            for (i in temp1){
                subaccounts.add(subaccountsBackup(i.id, i.name, i.parentaccount, i.description, i.balance))
            }

            var temp2= db.gettransactions()
            var transactions = ArrayList<transactionsBackup>()
            for (i in temp2){
                transactions.add(transactionsBackup(i.id,
                i.type,
                i.subaccountID,
                i.amount,
                i.description,
                    i.amountafter!!,
                i.transferto,
                i.orderBydate,
                i.date))
            }

            var temp3= db.getschedules()
            var schedules = ArrayList<schedules>()
            for (i in temp3){
                schedules.add(schedules(i.id, i.account, i.desc, i.amount, i.interval, i.txntype, i.transferto, i.specificTime))
            }

            var obj = arrayListOf(accounts, subaccounts, transactions, schedules)
            os.writeObject(obj)
            os.close()
            fos.close()
        } catch (e: Exception) {
            Log.e("Backup", e.toString())
            return Result.failure()
        }
        Log.e("Backup", "Done")
        return Result.success()
    }

}