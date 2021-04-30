package app.ifnyas.la.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import app.ifnyas.la.view.MainActivity
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import io.karn.notify.Notify


class BeaconMessageReceiver : BroadcastReceiver() {

    private val TAG by lazy { javaClass.simpleName }

    override fun onReceive(cxt: Context, intent: Intent) {
        Nearby.getMessagesClient(cxt).handleIntent(intent, object : MessageListener() {
            override fun onFound(msg: Message) {
                Log.d(TAG, "onFound: $msg")
                //(cxt as MainActivity).putCount(msg, true)
                Notify.with(cxt)
                        .content { title = "Someone Ring Yours" }
                        .show()
            }

            override fun onLost(msg: Message) {
                Log.d(TAG, "onLost: $msg")
                (cxt as MainActivity).putCount(msg, false)
            }
        })
    }
}
