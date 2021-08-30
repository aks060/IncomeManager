package com.dynusroot.incomemanager.adapters

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.dynusroot.incomemanager.R
import com.dynusroot.incomemanager.database.models.subaccounts
import kotlin.random.Random

class SubAccountsAdapter(var context: Context, var data:ArrayList<subaccounts>): BaseAdapter() {

    private lateinit var colors:ArrayList<Int>

    init {
        colors= ArrayList()
        colors.add(R.color.c1)
        colors.add(R.color.c2)
        colors.add(R.color.c3)
        //,R.color.c4,R.color.c5,R.color.c6,R.color.c7,R.color.c8,R.color.c9,R.color.c10,R.color.c11,R.color.c12,R.color.c13,R.color.c14)
    }
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View = View.inflate(context, R.layout.accounts_grid_view, null)
        var balance=view.findViewById<TextView>(R.id.main_accountbalance)
        var addacc= Dialog(context, )
        if(position!=this.count-1)
        {
            var rand= Random.nextInt(0, colors.size)
            view.findViewById<CardView>(R.id.card).setCardBackgroundColor(colors.get(rand))
        }
        var acc_name=view.findViewById<TextView>(R.id.account_name)
        var acc_id=view.findViewById<TextView>(R.id.accountid)
        acc_name.text=data.get(position).name
        acc_id.text=data.get(position).id.toString()
        balance.text="Total: Rs Sub "+"%.2f".format(data.get(position).balance)

        return view
    }
}
