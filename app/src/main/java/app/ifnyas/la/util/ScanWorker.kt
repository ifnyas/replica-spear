package app.ifnyas.la.util

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.google.android.gms.nearby.messages.Strategy
import com.google.android.gms.nearby.messages.SubscribeOptions
import io.karn.notify.Notify


class ScanWorker(appContext: Context, workerParams: WorkerParameters) :
        Worker(appContext, workerParams) {

    private val TAG by lazy { javaClass.simpleName }
    private val sp by lazy { applicationContext.getSharedPreferences("SP", 0) }

    private val his by lazy { "${sp.getString("his", "")}" }
    private val her by lazy { "${sp.getString("her", "")}" }

    private val message by lazy { Message("$her|$his".toByteArray()) }
    private val nClient by lazy { Nearby.getMessagesClient(applicationContext) }
    private val subscribeOptions by lazy {
        SubscribeOptions.Builder().setStrategy(Strategy.BLE_ONLY).build()
    }
    private val messageListener = object : MessageListener() {
        override fun onFound(msg: Message?) {
            super.onFound(msg)
            putCount(msg, true)
        }

        override fun onLost(msg: Message?) {
            super.onLost(msg)
            putCount(msg, false)
        }
    }

    override fun doWork(): Result {
        // Do the work here--in this case, upload the images.
        startNearby()

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    private fun startNearby() {
        nClient.publish(message)
        nClient.subscribe(messageListener, subscribeOptions)
        createNotify()
    }

    private fun putCount(msg: Message?, isPlus: Boolean) {
        val content = msg?.content?.decodeToString()?.split("|")
        val target = content?.get(0) ?: ""
        val source = content?.get(1) ?: ""

        // update text
        if (target == his) {
            createNotify()
        }
    }

    private fun createNotify() {
        Notify.with(applicationContext)
                .content { title = "Someone Ring Yours" }
                .show()
    }
}
