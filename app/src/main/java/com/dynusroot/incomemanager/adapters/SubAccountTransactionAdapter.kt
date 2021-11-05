package com.dynusroot.incomemanager.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.dynusroot.incomemanager.R
import com.dynusroot.incomemanager.database.models.transactions
import java.text.SimpleDateFormat
import java.time.ZoneId

class SubAccountTransactionAdapter(
    d: ArrayList<transactions>,
    val context: Context,
    private var popup: popupOption
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var data: ArrayList<transactions>
    var totalAmt=ArrayList<Double>()

    init {
        data= ArrayList()
        data=d
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return viewholder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_transaction,
                parent,
                false
            ),
                popup
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder)
        {
            is viewholder -> {
                var amt=0.0
                try {
                    if(position>0)
                        amt=totalAmt[position-1]
                    totalAmt[position]=holder.bind(data.get(position), position, amt)
                }
                catch (t: Throwable)
                {
                    if(position>0)
                        amt=totalAmt[position-1]
                    totalAmt.add(holder.bind(data.get(position), position, amt))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class viewholder(itemView: View, popupadap: popupOption) : RecyclerView.ViewHolder(itemView)
    {
        val main=itemView.findViewById<ConstraintLayout>(R.id.mainmain)
        var type=itemView.findViewById<TextView>(R.id.type)
        val amount=itemView.findViewById<TextView>(R.id.amount)
        var description=itemView.findViewById<TextView>(R.id.description)
        var amount_after=itemView.findViewById<TextView>(R.id.amount_after)
        val date=itemView.findViewById<TextView>(R.id.date)

        init {
           itemView.setOnLongClickListener {
               showmenu(itemView, popupadap)
               return@setOnLongClickListener true
           }

        }

        @SuppressLint("ResourceAsColor")
        fun bind(b: transactions, position: Int, totalAmt: Double): Double
        {
            var act=""
            var tot=totalAmt
            if(b.type=="D")
            {
                main.setBackgroundColor(R.color.c2)
                main.setBackgroundResource(R.color.c2)
                type.text="Debited"
                amount.text="Rs -"+b.amount.toString()
                tot-=b.amount
            }
            else
                if(b.type=="C")
            {
                main.setBackgroundColor(R.color.c5)
                main.setBackgroundResource(R.color.c5)
                type.text="Credited"
                amount.text="Rs +"+b.amount.toString()
                tot+=b.amount
            }
            else if(b.type=="T")
                {
                    main.setBackgroundColor(R.color.c7)
                    main.setBackgroundResource(R.color.c7)
                    type.text="Transfered"
                    amount.text="Rs -"+b.amount.toString()
                    tot-=b.amount
                }
            description.text=b.description
            date.text=b.date
            amount_after.text="%.2f".format(tot)
            return tot
        }

        private fun showmenu(v:View, popupadap: popupOption)
        {
            var popup=PopupMenu(v.context, v)
            popup.inflate(R.menu.transaction_long_click_options)
            popup.setOnMenuItemClickListener {
                if(it.itemId==R.id.delete)
                {
                    popupadap.delete(adapterPosition)
                    return@setOnMenuItemClickListener true
                }
                return@setOnMenuItemClickListener false
            }
            popup.show()
        }

    }

    interface popupOption{
        fun delete(position: Int)
    }

}
