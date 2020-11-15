package com.dynusroot.incomemanager.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dynusroot.incomemanager.database.db_dao
import com.dynusroot.incomemanager.database.models.accounts
import kotlinx.coroutines.*

class DashboardViewModel(val db: db_dao,
                         application: Application
) : AndroidViewModel(application) {

    private val job= Job()
    lateinit var toastmssg: MutableLiveData<String>
    lateinit var accountlist:MutableLiveData<ArrayList<accounts>>
    private val uiScope= CoroutineScope(Dispatchers.Main+job)

    init {
        toastmssg= MutableLiveData()
        accountlist= MutableLiveData(ArrayList())
        uiScope.launch {
            fetchacc()
        }
    }

    fun fetchaccount()
    {
        uiScope.launch { fetchacc() }
    }

    private suspend fun fetchacc()
    {
        withContext(Dispatchers.IO){
            accountlist.postValue(db.getaccounts() as ArrayList<accounts>?)
        }
    }

    fun refresh()
    {
        uiScope.launch {
            fetchacc()
        }
    }

}