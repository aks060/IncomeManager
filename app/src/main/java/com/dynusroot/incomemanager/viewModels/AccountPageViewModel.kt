package com.dynusroot.incomemanager.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dynusroot.incomemanager.database.db_dao
import com.dynusroot.incomemanager.database.models.accounts
import com.dynusroot.incomemanager.database.models.subaccounts
import kotlinx.coroutines.*
import java.sql.SQLException

class AccountPageViewModel(val db: db_dao,
                           application: Application,
                           val accountid:Long
) : AndroidViewModel(application) {

    private val job= Job()
    lateinit var toastmssg: MutableLiveData<String>
    lateinit var subaccountlist:MutableLiveData<ArrayList<subaccounts>>

    private val uiScope= CoroutineScope(Dispatchers.Main+job)

    init {
        toastmssg= MutableLiveData()
        subaccountlist= MutableLiveData(ArrayList())
        uiScope.launch {
            fetchsubacc()
            total()
        }
    }

    fun deleteaccount(id:String)
    {
        uiScope.launch {
            innerdeleteacc(id)
        }
    }

    private suspend fun innerdeleteacc(id:String)
    {
        try {
            withContext(Dispatchers.IO) {
                db.deleteaccount(id.toLong())
            }
        }
        catch (t:SQLException)
        {
            toastmssg.value="Error in Database"
        }
    }

    fun fetchsubaccount()
    {
        uiScope.launch { fetchsubacc() }
    }

    private suspend fun fetchsubacc()
    {
        withContext(Dispatchers.IO){
            subaccountlist.postValue(db.getSubAccount(accountid) as ArrayList<subaccounts>?)
        }
    }

    fun refresh()
    {
        uiScope.launch {
            subaccountlist.postValue(ArrayList())
            fetchsubacc()
            total()
            Log.e("AccountPageViewModel", "Completed DB")
            Log.e("AccountPageViewModel", subaccountlist.value.toString())
        }
    }

    private suspend fun total()
    {
        withContext(Dispatchers.IO){
            db.updateAccountBalance(accountid)
        }
    }
}