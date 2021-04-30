package app.ifnyas.la.util

import android.content.Intent
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Fade
import androidx.transition.TransitionManager

class FunUtils {

    fun navToAct(cxt: AppCompatActivity, intent: Intent) {
        // next act
        cxt.apply {
            startActivity(intent)
            finishAfterTransition()

            // override transition
            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            )
        }
    }

    fun beginTransition(view: ViewGroup) {
        TransitionManager.beginDelayedTransition(view, Fade())
    }
}