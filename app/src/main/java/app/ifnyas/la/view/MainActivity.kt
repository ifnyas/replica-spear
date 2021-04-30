package app.ifnyas.la.view

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View.*
import android.viewbinding.library.activity.viewBinding
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.ifnyas.la.App.Companion.fu
import app.ifnyas.la.R
import app.ifnyas.la.databinding.ActivityMainBinding
import app.ifnyas.la.util.FunUtils
import coil.load
import coil.transform.CircleCropTransformation
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.dasbikash.android_network_monitor.NetworkStateListener
import com.dasbikash.android_network_monitor.addNetworkStateListener
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.*


class MainActivity : AppCompatActivity() {

    // Activity
    private val TAG by lazy { javaClass.simpleName }
    private val bind: ActivityMainBinding by viewBinding()

    // Data
    private lateinit var list: MutableList<String>
    private lateinit var his: String
    private lateinit var her: String

    // Nearby
    private val message by lazy { Message(createMessageBytes()) }
    private val nClient by lazy { Nearby.getMessagesClient(this) }
    private val subscribeOptions by lazy {
        SubscribeOptions.Builder().setStrategy(Strategy.BLE_ONLY).build()
    }

    /*
    private val publishOptions by lazy {
        PublishOptions.Builder().setStrategy(Strategy.BLE_ONLY).build()
    }

    private val pendingIntent by lazy {
        PendingIntent.getBroadcast(
                this,
                0,
                Intent(this, BeaconMessageReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
     */

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


    // lifecycle
    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        initFun()
    }

    override fun onStart() {
        super.onStart()
        startNearby()
    }

    override fun onStop() {
        stopNearby()
        super.onStop()
    }

    // fun
    //
    private fun startNearby() {
        nClient.publish(message)
        nClient.subscribe(messageListener, subscribeOptions)
        resetCount()
    }

    private fun stopNearby() {
        nClient.unpublish(message)
        nClient.unsubscribe(messageListener)
    }

    private fun initFun() {
        initIds()
        initBtn()
        initImg()
        initNet()
        initDebug()
        // initWorker()
    }

    /*
    private fun initWorker() {
        val workRequest = OneTimeWorkRequestBuilder<ScanWorker>().build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }
     */

    private fun initNet() {
        addNetworkStateListener(
                NetworkStateListener.getInstance(
                        { navToSplash() },
                        { createNoNetDialog() },
                        this
                )
        )
    }

    private fun createNoNetDialog() {
        val title = "No Connection"
        val msg = "Please check your internet connection. " +
                "This alert will be closed automatically after you are connected."
        val btn = "Close ${getString(R.string.app_name)}"
        MaterialDialog(this).show {
            lifecycleOwner(this@MainActivity)
            cancelable(false)
            cancelOnTouchOutside(false)
            title(text = title)
            message(text = msg)
            positiveButton(text = btn) { finishAfterTransition() }
        }
    }

    private fun initBtn() {
        bind.imgHeart.setOnClickListener { navToAccount() }
        bind.imgHis.setOnClickListener { createHisToast() }
    }

    private fun createHisToast() {
        Toast.makeText(this, "Your Id is $his", Toast.LENGTH_LONG).show()
    }

    private fun initIds() {
        his = intent.getStringExtra("his").toString()
        her = intent.getStringExtra("her").toString()
        if (his.isBlank() || her.isBlank()) {
            navToAccount()
        }
        Log.d(TAG, "initIds: $his, $her")
    }

    private fun initImg() {
        val img = intent.getStringExtra("img").toString()
        bind.imgHis.load(img) {
            crossfade(true)
            transformations(CircleCropTransformation())
        }
    }

    private fun resetCount() {
        list = mutableListOf()
        bind.textCount.text = "${list.distinct().size}"
    }

    fun putCount(msg: Message?, isPlus: Boolean) {
        val content = msg?.content?.decodeToString()?.split("|")
        val target = content?.get(0) ?: ""
        val source = content?.get(1) ?: ""

        // update text
        if (target == his) {
            if (!isPlus) delSource(source)
            else {
                if (!list.contains(source)) playRing()
                list.add(source)
                putHeartView()
            }
        }
    }

    private fun delSource(source: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            val index = list.indexOf(source)
            list.removeAt(index)
            putHeartView()
        }, 10000)
    }

    private fun putHeartView() {
        // update count text
        val count = list.distinct().size
        bind.textCount.text = "$count"

        // update scanner anim
        bind.imgScanning.visibility = if (count > 0) INVISIBLE else VISIBLE
        bind.imgScanned.visibility = if (count > 0) VISIBLE else INVISIBLE

        // update heart img
        val draw = if (count > 0) R.drawable.png_full_heart else R.drawable.png_empty_heart
        bind.imgHeart.load(draw) { crossfade(true) }
        fu.beginTransition(bind.root)
    }

    private fun playRing() {
        MediaPlayer.create(this, R.raw.sound_heart).start()
    }

    private fun createMessageBytes(): ByteArray {
        return "$her|$his".toByteArray()
    }

    private fun navToAccount() {
        // init intent
        val intent = Intent(this, AccountActivity::class.java).apply {
            putExtra("last", her)
        }

        // start intent
        FunUtils().navToAct(this, intent)
    }

    private fun navToSplash() {
        val intent = Intent(this, SplashActivity::class.java)
        FunUtils().navToAct(this, intent)
    }

    private fun initDebug() {
        bind.textCount.setOnClickListener {
            val msg = Message("irfanyasiras@gmail.com|justinejuno@gmail.com".toByteArray())
            if (list.distinct().isEmpty()) putCount(msg, true)
            else putCount(msg, false)
        }
    }
}

/*
    TODO
    add bg mode
*/