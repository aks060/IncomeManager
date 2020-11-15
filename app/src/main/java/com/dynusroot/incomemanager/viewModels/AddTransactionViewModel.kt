package com.dynusroot.incomemanager.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dynusroot.incomemanager.database.db_dao
import com.dynusroot.incomemanager.database.models.accounts
import com.dynusroot.incomemanager.database.models.subaccounts
import com.dynusroot.incomemanager.database.models.transactions
import kotlinx.coroutines.*

class AddTransactionViewModel(val db: db_dao,
                              application: Application,
                              val subaccountid:Long
) : AndroidViewModel(application) {
    private val job= Job()
    lateinit var toastmssg: MutableLiveData<String>
    lateinit var totalamount: MutableLiveData<Double>
    lateinit var accountList: MutableLiveData<ArrayList<subaccounts>>
    private lateinit var subaccount: subaccounts
    private val uiScope= CoroutineScope(Dispatchers.Main+job)

    init {
        toastmssg= MutableLiveData()
        totalamount= MutableLiveData(0.0)
        accountList= MutableLiveData(ArrayList())
        getSubAccountList()
        uiScope.launch {
            total()
        }
    }

    fun getSubAccountList()
    {
        uiScope.launch {
            withContext(Dispatchers.IO){
                accountList.postValue(db.getSubAccountExcept(subaccountid) as ArrayList)
            }
        }
    }

    fun creditmoney(amount:Double, desc:String, date:String, orderByDate:String)
    {
        var trans=transactions(type = "C", orderBydate = orderByDate, subaccountID = subaccountid.toLong(), amount = amount, date = date, description = desc, amountafter = (totalamount.value?.plus(amount)))
        uiScope.launch {
            withContext(Dispatchers.IO){
                try {
                    db.addTransaction(trans)
                    total()
                }
                catch (t:Throwable)
                {
                    toastmssg.value=t.message.toString()
                }
            }
        }
    }

    fun debitmoney(amount:Double, desc:String, date:String, orderByDate: String)
    {
        var trans=transactions(type = "D", orderBydate = orderByDate, subaccountID = subaccountid.toLong(), amount = amount, date = date, description = desc, amountafter = (totalamount.value?.minus(amount)))
        uiScope.launch {
            withContext(Dispatchers.IO){
                try {
                    db.addTransaction(trans)
                    total()
                }
                catch (t:Throwable)
                {
                    toastmssg.value=t.message.toString()
                }
            }
        }
    }

    fun transfermoney(amount:Double, desc:String, date:String, transferto:Long, orderByDate: String)
    {
        var trans=transactions(type = "T", orderBydate = orderByDate, subaccountID = subaccountid.toLong(), amount = amount, date = date, description = desc, amountafter = (totalamount.value?.minus(amount)), transferto = transferto)
        var transto=transactions(type = "C", orderBydate = orderByDate, subaccountID = transferto, amount = amount, date = date, description = desc+" From "+subaccount.name, amountafter = (totalamount.value?.minus(amount)), transferto = transferto)

        uiScope.launch {
            withContext(Dispatchers.IO){
                try {
                    db.addTransaction(trans)
                    db.addTransaction(transto)
                    total()
                }
                catch (t:Throwable)
                {
                    toastmssg.value=t.message.toString()
                }
            }
        }
    }

    fun refresh()
    {
        uiScope.launch {
            total()
            getSubAccountList()
        }
    }

    private suspend fun total()
    {
        withContext(Dispatchers.IO){
            totalamount.postValue(db.getTotalCredit(subaccountid.toLong())-db.getTotalDebit(subaccountid.toLong())-db.getTotalTransferred(subaccountid.toLong()))
            subaccount=db.getSubAccountID(subaccountid.toLong())
            subaccount.balance= totalamount.value!!
            db.updateSubAccount(subaccount)
        }
    }
}

