package com.dynusroot.incomemanager.viewModels

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dynusroot.incomemanager.activities.Transactions
import com.dynusroot.incomemanager.database.db_dao
import com.dynusroot.incomemanager.database.models.subaccounts
import com.dynusroot.incomemanager.database.models.transactions
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.LocalDate

class AddSubAccountViewModel(val db: db_dao,
                             application: Application
) : AndroidViewModel(application) {

    private val job= Job()
    lateinit var toastmssg: MutableLiveData<String>
    private val uiScope= CoroutineScope(Dispatchers.Main+job)

    init {
        toastmssg=MutableLiveData()
    }

    fun addsubaccount(name: String, desc: String?, bal:Double, parent:Long)
    {
        uiScope.launch {
            inneraddsubaccount(name, desc, bal, parent)
        }
    }

    private suspend fun inneraddsubaccount(n: String, d: String?, b:Double, p:Long) {
        try {
            withContext(Dispatchers.IO) {
                var desc = ""
                if (d != null) {
                    desc = d
                }
                var subacc = subaccounts(name = n, description = desc, balance = b, parentaccount = p)
                var id=db.addSubAccount(subacc)
                if(b>0)
                {
                    addtransaction(b, id)
                }
                toastmssg.postValue("Sub Account Added successfully")
            }
        }
        catch (t: Throwable)
        {
            toastmssg.value=t.message.toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun addtransaction(amt: Double, subid:Long)
    {
        var date=LocalDate.now()
        var trans=transactions(type = "C", subaccountID = subid, amount = amt, amountafter = amt, description = "Initial Credit", orderBydate = "0/0/0", date =date.dayOfMonth.toString()+"/"+date.month+"/"+date.year)
        withContext(Dispatchers.IO){
            db.addTransaction(trans)
        }
    }
}