package app.ifnyas.la.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.ifnyas.la.util.FunUtils

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, AccountActivity::class.java)
        FunUtils().navToAct(this, intent)
    }
}