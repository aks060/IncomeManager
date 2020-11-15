package com.dynusroot.incomemanager.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dynusroot.incomemanager.database.db_dao
import com.dynusroot.incomemanager.database.models.subaccounts
import com.dynusroot.incomemanager.database.models.transactions
import kotlinx.coroutines.*

class TransactionsViewModel(val db: db_dao,
                            application: Application,
                            val accountid:Long,
                            val subaccountid: Long
) : AndroidViewModel(application) {

    private val job= Job()
    lateinit var total:MutableLiveData<Double>
    lateinit var toastmssg: MutableLiveData<String>
    lateinit var transactions: MutableLiveData<ArrayList<transactions>>
    private val uiScope= CoroutineScope(Dispatchers.Main+job)


    init {
        toastmssg= MutableLiveData()
        transactions= MutableLiveData(ArrayList())
        total= MutableLiveData(0.0)
        uiScope.launch {
            total()
            refreshTransactions()
        }
    }

    fun deletesubaccount() {
        uiScope.launch {
            innerdelete()
        }
    }

    private suspend fun innerdelete()
    {
        withContext(Dispatchers.IO){
            try {
                Log.i("Transaction", subaccountid.toString())
                db.deleteSubAccountDB(subaccountid)
                Log.e("Transaction", "Deleted")
            }
            catch (t:Throwable)
            {
                Log.e("Transaction", t.toString())
            }
        }
    }

    fun refresh()
    {
        uiScope.launch {
            total()
            refreshTransactions()
        }
    }

    private suspend fun total()
    {
        withContext(Dispatchers.IO){
            total.postValue(db.getTotalCredit(subaccountid.toLong())-db.getTotalDebit(subaccountid.toLong())-db.getTotalTransferred(subaccountid.toLong()))
            var subaccount=db.getSubAccountID(subaccountid.toLong())
            subaccount.balance= total.value!!
            db.updateSubAccount(subaccount)
        }
    }

    private suspend fun refreshTransactions()
    {
        withContext(Dispatchers.IO){
            transactions.postValue(db.getAllTransactions(subaccountid) as ArrayList<transactions>)
        }
    }
}