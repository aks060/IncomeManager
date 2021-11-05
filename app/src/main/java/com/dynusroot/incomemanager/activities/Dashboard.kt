package com.dynusroot.incomemanager.activities

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.work.*
import com.dynusroot.incomemanager.R
import com.dynusroot.incomemanager.adapters.AccountsAdapter
import com.dynusroot.incomemanager.database.incomemanager_db
import com.dynusroot.incomemanager.database.models.accounts
import com.dynusroot.incomemanager.viewModels.DashboardViewModel
import com.dynusroot.incomemanager.worker.Backup
import com.dynusroot.incomemanager.worker.Restore
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.w3c.dom.Text
import java.util.jar.Manifest

class Dashboard : AppCompatActivity() {
    private lateinit var adapter: AccountsAdapter
    private lateinit var accounts:ArrayList<accounts>
    private lateinit var viewModel: DashboardViewModel
    private lateinit var gridView: GridView
    companion object {
        private const val STORAGE_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val db=incomemanager_db.get(application).dbDao
        viewModel= DashboardViewModel(db, application)
        accounts= ArrayList()
        accounts= viewModel.accountlist.value!!
        gridView=findViewById<GridView>(R.id.accounts)

        viewModel.accountlist.observe(this, Observer {
            accounts=it
            adapter= AccountsAdapter(this, accounts)
            gridView.adapter=adapter
        })
//        adapter= AccountsAdapter(this, accounts)
//        var gridView=findViewById<GridView>(R.id.accounts)
//        gridView.adapter=adapter


        gridView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            var intent= Intent(this, AccountPage::class.java)
            var bundle=Bundle()
            var name=view.findViewById<TextView>(R.id.account_name).text.toString()
            var accountid=view.findViewById<TextView>(R.id.accountid).text.toString()
            bundle.putString("account", name)
            bundle.putString("accountid", accountid)
            Log.i("Dashboard", accountid)
            intent.putExtras(bundle)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
        viewModel.accountlist.observe(this, Observer {
            accounts=it
            adapter= AccountsAdapter(this, accounts)
            gridView.adapter=adapter
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = MenuInflater(this)
        inflater.inflate(R.menu.dashboard_menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.createAccount){
            startActivity(Intent(this, AddAccount::class.java))
        }
        else if (item.itemId==R.id.backup){
            //Backup
            checkPermission(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE, true)
        }
        else if(item.itemId==R.id.restore){
            //Restore
            checkPermission(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkPermission(permission: String, requestCode: Int, isbackup: Boolean =false) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
        else {
            if(isbackup)
                startBackup()
            else
                startRestore()
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBackup()
                Toast.makeText(this, "Backup started. Will notify when complete", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Storage Permission Required for Backup", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startBackup(){
        var workmanager = WorkManager.getInstance(this)

        var workReq = OneTimeWorkRequest.Builder(Backup::class.java)
            .build()

        workmanager.enqueue(workReq)
        Log.e("Backup", "Work enqued")
        workmanager.getWorkInfoByIdLiveData(workReq.id).observe(this, Observer {
            Log.e("Backup", it.state.name)
        })
        Log.e("Backup", workmanager.getWorkInfoById(workReq.id).get().state.toString())
    }

    private fun startRestore(){
        var workmanager = WorkManager.getInstance(this)

        var workReq = OneTimeWorkRequest.Builder(Restore::class.java)
            .build()

        workmanager.enqueue(workReq)
        Log.e("Restore", "Work enqued")
        workmanager.getWorkInfoByIdLiveData(workReq.id).observe(this, Observer {
            Log.e("Restore", it.state.name)
        })
        Log.e("Restore", workmanager.getWorkInfoById(workReq.id).get().state.toString())
    }
}

