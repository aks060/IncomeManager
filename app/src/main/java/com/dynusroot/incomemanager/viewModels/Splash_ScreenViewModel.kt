package com.dynusroot.incomemanager.viewModels

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteFullException
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.room.RoomDatabase
import androidx.room.RoomWarnings
import com.dynusroot.incomemanager.database.db_dao
import com.dynusroot.incomemanager.database.incomemanager_db
import com.dynusroot.incomemanager.database.models.userDBModel
import com.dynusroot.incomemanager.objects.user
import kotlinx.coroutines.*

class Splash_ScreenViewModel
    (val db: db_dao,
     application: Application) : AndroidViewModel(application) {

    lateinit var loggedin:MutableLiveData<Int>
    val job= Job()
    var ret: MutableLiveData<Int> = MutableLiveData(-1)
    lateinit var toastmssg:MutableLiveData<String>
    val uiScope= CoroutineScope(Dispatchers.Main+job)
    init {
        toastmssg=MutableLiveData()
        loggedin= MutableLiveData()
        uiScope.launch {
            updateLoggedinUser()
        }
    }

    private suspend fun updateLoggedinUser()
    {
        var userres:userDBModel?=null
        withContext(Dispatchers.IO)
        {
            userres=db.getloggedinuser()
        }
        if(userres!=null)
        {
            user.fullname= userres!!.name
            user.username= userres!!.username
            loggedin.value=1
        }
        else
        {
            loggedin.value=0
        }
    }

    fun signup(name:String, username:String, password: String)
    {
        val usermodel=userDBModel(name = name, username = username, password = password)
        uiScope.launch {
            insertuserdb(usermodel)
        }
    }

    private suspend fun insertuserdb(usermodel:userDBModel)
    {
        try {
            withContext(Dispatchers.IO)
            {
                db.insertuser(usermodel)
                ret.value=1
            }
        }
        catch (t: SQLiteConstraintException)
        {
            ret.value=0
            toastmssg.value="User already Register with this username"
            Log.e("SQLite Error", t.toString())
        }
        catch (t: Throwable)
        {
            ret.value=0
            toastmssg.value="Some error occured"
            Log.e("SplashVM", t.toString())
        }
    }
}