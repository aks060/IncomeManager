package com.dynusroot.incomemanager.database

import androidx.room.*
import com.dynusroot.incomemanager.database.models.*

@Dao
interface db_dao {

    @Query("SELECT * FROM user WHERE loggedin=1")
    fun getloggedinuser(): userDBModel

    @Insert
    fun insertuser(user: userDBModel)

    @Query("SELECT * FROM user WHERE username=:user AND password=:passw")
    fun login(user: String, passw:String):userDBModel

    @Query("UPDATE user SET loggedin=0")
    fun logout()

    @Query("UPDATE user SET loggedin=1 WHERE username=:u")
    fun setloggedin(u:String)

    @Insert
    fun addacount(account: accounts)

    @Query("DELETE FROM accounts WHERE id=:id")
    fun deleteaccount(id:Long)

    @Insert
    fun addSubAccount(a:subaccounts):Long

    @Update
    fun updateSubAccount(a: subaccounts)

    @Query("SELECT * FROM subaccounts WHERE id=:subid")
    fun getSubAccountID(subid: Long):subaccounts

    @Query("SELECT * FROM subaccounts WHERE parentaccount=:parentacc")
    fun getSubAccount(parentacc:Long):List<subaccounts>

    @Query("DELETE FROM transactions WHERE subaccountID=:subid")
    fun deleteTransactionsSub(subid: Long)

    @Query("DELETE FROM transactions WHERE id=:tid")
    fun deleteTransaction(tid: Long)

    @Query("DELETE FROM subaccounts WHERE id=:subid")
    fun deleteSubAccountDB(subid:Long)

    fun deleteSubAccount(subid: Long)
    {
        deleteTransactionsSub(subid)
        deleteSubAccountDB(subid)
    }

    @Insert
    fun addTransaction(a: transactions):Long

    @Query("SELECT * FROM transactions WHERE subaccountID=:subID ORDER BY orderBydate, id")
    fun getAllTransactions(subID: Long): List<transactions>

    @Query("SELECT SUM(amount) FROM transactions WHERE type='C' AND subaccountID=:sub")
    fun getTotalCredit(sub:Long): Double

    @Query("SELECT SUM(amount) FROM transactions WHERE type='D' AND subaccountID=:sub")
    fun getTotalDebit(sub:Long): Double

    @Query("SELECT SUM(amount) FROM transactions WHERE type='T' AND subaccountID=:sub")
    fun getTotalTransferred(sub:Long): Double

    @Query("SELECT * FROM subaccounts WHERE id!=:accid")
    fun getSubAccountExcept(accid:Long): List<subaccounts>

    @Query("UPDATE accounts SET totalBalance=(SELECT COALESCE(SUM(balance), 0) FROM subaccounts WHERE parentaccount=:accid) WHERE id=:accid")
    fun updateAccountBalance(accid: Long)

    @Update
    fun updateTransaction(tr: transactions)

    // Backup Functions

    @Query("SELECT * FROM accounts")
    fun getaccounts():List<accounts>

    @Query("SELECT * FROM subaccounts")
    fun getsubaccounts(): List<subaccounts>

    @Query("SELECT * FROM transactions")
    fun gettransactions(): List<transactions>


    // Schedule Functions
    @Insert
    fun scheduleTransaction(sctxn: schedules): Long
    @Delete
    fun deleteschedule(sctxn: schedules)
    @Update
    fun updateschedule(sctxn: schedules)
    @Query("SELECT * FROM schedules")
    fun getschedules(): List<schedules>
}