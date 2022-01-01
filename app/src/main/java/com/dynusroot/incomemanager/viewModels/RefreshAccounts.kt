package com.dynusroot.incomemanager.viewModels

import android.util.Log
import com.dynusroot.incomemanager.database.db_dao
import com.dynusroot.incomemanager.database.models.accounts
import com.dynusroot.incomemanager.database.models.subaccounts
import com.dynusroot.incomemanager.database.models.transactions
import kotlinx.coroutines.*

open class RefreshAccounts(val db: db_dao) {
    private val job= Job()
    private val uiScope= CoroutineScope(Dispatchers.Main+job)

    fun refreshAmount(){
        uiScope.launch {
            withContext(Dispatchers.IO){
                var acc=db.getaccounts() as ArrayList<accounts>
                for(i in acc){
                    var subacc=db.getSubAccount(i.id) as ArrayList<subaccounts>
                    Log.e("subacc", subacc.toString())
                    for(j in subacc){
                        var txns=db.getAllTransactions(j.id) as ArrayList<transactions>
                        var subtotal=0.0
                        for(t in txns){
                            Log.e("subacc", subtotal.toString())
                            if(t.type=="C")
                                subtotal+=t.amount
                            else
                                subtotal-=t.amount
                        }
                        j.balance=subtotal
                        db.updateSubAccount(j)
                    }
                    Log.e("subacc", "Updating Acc Bal")
                    db.updateAccountBalance(i.id)
                }
            }
        }
    }
}