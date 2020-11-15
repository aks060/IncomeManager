package com.dynusroot.incomemanager.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dynusroot.incomemanager.database.db_dao
import com.dynusroot.incomemanager.database.models.userDBModel
import com.dynusroot.incomemanager.objects.user
import kotlinx.coroutines.*

class LoginFragmentViewModel(val db: db_dao,
                             application: Application) : AndroidViewModel(application) {

    lateinit var login:MutableLiveData<Int>
    lateinit var toastmessage:MutableLiveData<String>
    private val job= Job()
    private val uiScope= CoroutineScope(Dispatchers.Main+job)
    init {
        login= MutableLiveData()
        toastmessage=MutableLiveData()
    }

    fun login(username: String, pass: String)
    {
        uiScope.launch {
            innerlogin(username, pass)
        }
    }

    private suspend fun innerlogin(u:String, p:String)
    {
        var loginuser:userDBModel?=null
        withContext(Dispatchers.IO)
        {
            loginuser=db.login(u, p)
            if(loginuser!=null)
            {
                user.username= loginuser!!.username
                user.fullname=loginuser!!.name
                db.setloggedin(user.username)
            }
        }
        if(loginuser!=null && loginuser!!.username==u && loginuser!!.password==p)
        {
            login.value=1
        }
        else
        {
            toastmessage.value="Incorrect Username or Password"
            login.value=0
        }
    }
}