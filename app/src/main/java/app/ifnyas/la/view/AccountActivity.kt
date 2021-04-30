package app.ifnyas.la.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.viewbinding.library.activity.viewBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import app.ifnyas.la.R
import app.ifnyas.la.databinding.ActivityAccountBinding
import app.ifnyas.la.util.FunUtils
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.dasbikash.android_network_monitor.initNetworkMonitor
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status


@Suppress("PrivatePropertyName")
class AccountActivity : AppCompatActivity() {

    // Activity
    private val TAG by lazy { javaClass.simpleName }
    private val LOGIN by lazy { 6 }
    private val sp by lazy { getSharedPreferences("SP", 0) }
    private val bind: ActivityAccountBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        initFun()
    }

    private fun initFun() {
        initNetworkMonitor()
        checkHis()
    }

    private fun checkHis() {
        val his = "${sp.getString("his", "")}"
        if (his.isBlank()) login() else checkHer()
    }

    private fun checkHer() {
        val last = "${intent.getStringExtra("last")}"
        val her = if (last != "null") "" else "${sp.getString("her", "")}"
        if (her.isBlank()) createHerDialog() else navToMain()
    }

    private fun login() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) putHis(account) else {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, LOGIN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOGIN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) putHis(account)
                else throw ApiException(Status.RESULT_INTERNAL_ERROR)
            } catch (e: ApiException) {
                e.printStackTrace()
                createExDialog()
            }
        }
    }


    private fun createExDialog() {
        MaterialDialog(this).show {
            lifecycleOwner(this@AccountActivity)
            cancelable(false)
            cancelOnTouchOutside(false)
            title(text = "Sign In Failed")
            message(text = "Please identify yourself using google account")
            positiveButton(text = "Try Again") { navToSplash() }
            negativeButton(text = "Close") { finishAfterTransition() }
        }
    }

    private fun createHerDialog() {
        // init preFill
        var preFill = "${intent.getStringExtra("last")}"
        val isPreFillNull = preFill == "null"
        if (isPreFillNull) preFill = ""

        // init negative text
        val negText = if (isPreFillNull) "Close" else "Back"

        // show dialog
        MaterialDialog(this).show {
            lifecycleOwner(this@AccountActivity)
            cornerRadius(8f)
            cancelable(false)
            cancelOnTouchOutside(false)
            title(text = "Other Identity")
            message(text = "Please identify your significant other")
            input(
                    hint = "Type his/her email...",
                    prefill = preFill
            ) { _, input ->
                putHer("${input.trim()}")
            }
            getInputField().apply {
                gravity = Gravity.CENTER
                inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                post { selectAll() }
                setBackgroundColor(
                        ContextCompat.getColor(
                                this@AccountActivity,
                                android.R.color.transparent
                        )
                )
            }
            negativeButton(text = negText) {
                if (preFill.isBlank()) finishAfterTransition()
                else navToSplash()
            }
            positiveButton(text = "OK")
        }
    }

    private fun navToMain() {
        // init intent
        val intent = Intent(this, MainActivity::class.java).apply {
            listOf("his", "her", "img").forEach {
                putExtra(it, "${sp.getString(it, "")}")
            }
        }

        // start intent
        FunUtils().navToAct(this, intent)
    }

    private fun putHis(account: GoogleSignInAccount) {
        // save session
        putSession("img", "${account.photoUrl}")
        putSession("his", "${account.email}")

        // next fun
        checkHer()
    }

    private fun putHer(input: String) {
        // save session
        putSession("her", input)

        // next fun
        navToMain()
    }

    private fun putSession(key: String, value: String) {
        sp.edit().putString(key, value).apply()
    }

    private fun navToSplash() {
        val intent = Intent(this, SplashActivity::class.java)
        FunUtils().navToAct(this, intent)
    }
}