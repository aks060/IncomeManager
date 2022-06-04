package com.dynusroot.incomemanager.worker

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi

class SMSReader: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        var bundle = p1!!.extras
        Log.e("SMS Reader", "Inside Reciever")
        var objArr = bundle!!.get("pdus") as Array<Any>
        var format = bundle!!.getString("format");
        for (obj in objArr){
            var sms = SmsMessage.createFromPdu(obj as ByteArray)
            Toast.makeText(p0, sms.toString(), Toast.LENGTH_LONG).show()
            Log.d("SMS Reader", sms.originatingAddress.toString())
        }
    }
}