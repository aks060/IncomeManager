package com.dynusroot.incomemanager.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dynusroot.incomemanager.database.db_dao
import com.dynusroot.incomemanager.database.models.accounts
import kotlinx.coroutines.*

class AddAccountViewModel(val db: db_dao,
                          application: Application
) : AndroidViewModel(application) {

    private val job= Job()
    lateinit var toastmssg:MutableLiveData<String>
    var issuccess=0
    private val uiScope= CoroutineScope(Dispatchers.Main+job)

    init {
            toastmssg=MutableLiveData()
    }

    fun addaccount(name:String, desc:String){
        uiScope.launch {
            inneraddacc(name, desc)
        }
    }

    private suspend fun inneraddacc(n:String, d:String)
    {
        try {
            withContext(Dispatchers.IO) {
                db.addacount(accounts(0, n, d))
                toastmssg.postValue("Account Added")
                issuccess=1
            }
        }
        catch (t: Throwable)
        {
            toastmssg.value="Error: "+t.message.toString()
            issuccess=0
        }
    }
}