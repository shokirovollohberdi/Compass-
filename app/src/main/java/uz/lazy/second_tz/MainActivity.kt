package uz.lazy.second_tz

import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.compass.helpers.Compass
import com.example.compass.helpers.SOTWFormatter

class MainActivity : AppCompatActivity() {
    private val TAG = "CompassActivity"

    private var compass: Compass? = null
    private var arrowView: ImageView? = null
    private var sotwLabel // SOTW is for "side of the world"
            : TextView? = null

    private var currentAzimuth = 0f
    private var sotwFormatter: SOTWFormatter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        sotwFormatter = SOTWFormatter(this);

        arrowView = findViewById(R.id.main_image_hands);
        sotwLabel = findViewById(R.id.sotw_label);
        setupCompass()
    }
    private fun setupCompass() {
        compass = Compass(this)
        val cl = getCompassListener()
        compass!!.setListener(cl)
    }
    private fun getCompassListener(): Compass.CompassListener {
        return object : Compass.CompassListener {
            override fun onNewAzimuth(azimuth: Float) {
                // UI updates only in UI thread
                // https://stackoverflow.com/q/11140285/444966
                runOnUiThread {
                    adjustArrow(azimuth)
                    adjustSotwLabel(azimuth)
                }
            }
        }
    }
    private fun adjustArrow(azimuth: Float) {
        Log.d(TAG, "will set rotation from " + currentAzimuth + " to "
                + azimuth)
        val an: Animation = RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f)
        currentAzimuth = azimuth
        an.duration = 300
        an.repeatCount = 0
        an.fillAfter = true
        arrowView!!.startAnimation(an)
    }

    private fun adjustSotwLabel(azimuth: Float) {
        sotwLabel!!.text = sotwFormatter!!.format(azimuth)
    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "start compass")
        compass!!.start()
    }

    override fun onPause() {
        super.onPause()
        compass!!.stop()
    }

    override fun onResume() {
        super.onResume()
        compass!!.start()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "stop compass")
        compass!!.stop()
    }
}