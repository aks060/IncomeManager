package com.dynusroot.incomemanager.worker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dynusroot.incomemanager.R
import com.dynusroot.incomemanager.database.incomemanager_db
import com.dynusroot.incomemanager.viewModels.AddTransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

class ScheduleTransactionWork (var context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    override fun doWork(): Result {
        val db = incomemanager_db.get(context as Application).dbDao
        var application = context as Application

        var getList = db.getschedules()

        if (!getList.isEmpty()) {
            for (i in getList) {
                val simpleDateFormat = SimpleDateFormat("EEE")
                val weekday: String = simpleDateFormat.format(Date())
                val monthdate = SimpleDateFormat("d").format(Date()).toLong()
                val month = SimpleDateFormat("M").format(Date()).toLong()
                var orderbyDate = SimpleDateFormat("dd/MM/yyyy").format(Date())
                var yearly = false
                if (i.interval == "Y") {
                    val date = SimpleDateFormat("d/M/yyyy").parse(i.specificTime)
                    if (monthdate == SimpleDateFormat("d").format(date)
                            .toLong() && month == SimpleDateFormat("M").format(date)
                            .toLong()
                    ) {
                        yearly = true
                    }
                }
                if (i.interval == "D" || (i.interval == "W" && weekday == i.specificTime) || (i.interval == "M" && monthdate == i.specificTime!!.toLong()) || yearly) {
                    var viewModel =
                        AddTransactionViewModel(db, application, i.account, context!!)
                    if (i.txntype == "C")
                        viewModel.creditmoney(
                            false,
                            i.amount,
                            i.desc,
                            SimpleDateFormat("d/M/yyyy").format(Date()),
                            orderbyDate
                        )
                    else if (i.txntype == "D")
                        viewModel.debitmoney(
                            false,
                            i.amount,
                            i.desc,
                            SimpleDateFormat("d/M/yyyy").format(Date()),
                            orderbyDate
                        )
                    else if (i.txntype == "T")
                        viewModel.transfermoney(
                            false,
                            i.amount,
                            i.desc,
                            SimpleDateFormat("d/M/yyyy").format(Date()),
                            i.transferto,
                            orderbyDate
                        )
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("IncomeManager", "IncomeManager Scheduler", NotificationManager.IMPORTANCE_HIGH)
            channel.description="Scheduled Transactions notifications"
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        var builder = NotificationCompat.Builder(context, "IncomeManager")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Scheduled Transaction")
            .setContentText("Scheduled Transactions")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        var compat = NotificationManagerCompat.from(context)
        compat.notify(123, builder.build())
        return Result.success()
    }
}