package com.example.knifehit.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.knifehit.MusicManager
import com.example.knifehit.MusicManager.isMusicMuted
import com.example.knifehit.PhoneOwner
import com.example.knifehit.PhoneOwner.default_knife_skin_for_player1
import com.example.knifehit.PhoneOwner.default_knife_skin_for_player2
import com.example.knifehit.R
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private lateinit var  image : ImageView
    private lateinit var scoretext : TextView
    private lateinit var soundPool: SoundPool
    private var soundclick by Delegates.notNull<Int>()
    private val rotationHandler = Handler(Looper.getMainLooper())
    private val rotationRunnable = object : Runnable{
        override fun run() {
            image.rotation = (image.rotation +1) %360
            rotationHandler.postDelayed(this,10)
        }
    }
    private var phoneOwner = PhoneOwner
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scoretext = findViewById(R.id.scoremain)
        scoretext.text = "Score: "+phoneOwner.loadPlayerScore(this)
        soundPool = SoundPool(1,AudioManager.STREAM_MUSIC,0)
        soundclick = soundPool.load(this,R.raw.click,1)
        val start_button : TextView = findViewById(R.id.button_start)
        val twoplayer_button : TextView = findViewById(R.id.button_twoplayer)
        val shop_button : TextView = findViewById(R.id.button_shop)
        hideSystemUI()
        val muteButton : ImageView = findViewById(R.id.mute_button)
        muteButton.setImageResource(if (MusicManager.isMuted())R.drawable.low else R.drawable.high)
        muteButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    muteButton.setColorFilter(resources.getColor(R.color.Tint))
                    true // Return 'true' to consume the event
                }
                MotionEvent.ACTION_UP -> {
                    MusicManager.toggleMute()
                    updateImageView()
                    muteButton.setColorFilter(resources.getColor(R.color.white))
                    true // Return 'true' to consume the event
                }
                else -> false
            }
        }

        image = findViewById(R.id.imageView)
        loadDefaultKnife(this)
        twoplayer_button.setOnClickListener{
            soundPool.play(soundclick,1f,1f,0,0,1f)
            val intent = Intent(this, TwoPlayerActivity::class.java)
            startActivity(intent)
        }
        start_button.setOnClickListener{
            soundPool.play(soundclick,1f,1f,0,0,1f)
            val intent = Intent(this, GameBoard::class.java)
            startActivity(intent)
        }
        shop_button.setOnClickListener{
            soundPool.play(soundclick,1f,1f,0,0,1f)
            val intent = Intent(this, ShopActivity::class.java)
            startActivity(intent)
        }
    }
    private fun updateImageView() {
        val imageView: ImageView = findViewById(R.id.mute_button)
        val imageResource = if (MusicManager.isMusicMuted) {
            R.drawable.low
        } else {
            R.drawable.high
        }
        imageView.setImageResource(imageResource)
    }

    override fun onResume() {
        super.onResume()
        MusicManager.resume()
        rotationHandler.post(rotationRunnable)
        scoretext.text = "Score: " + phoneOwner.loadPlayerScore(this)
    }
    override fun onPause() {
        super.onPause()
        MusicManager.pause()
        rotationHandler.removeCallbacks(rotationRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        rotationHandler.removeCallbacks(rotationRunnable)
    }

    override fun onStop() {
        super.onStop()
    }
    private fun hideSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }
    fun loadDefaultKnife(context: Context) {
        val sharedPreferences = context.getSharedPreferences("Usingknifes", Context.MODE_PRIVATE)
        default_knife_skin_for_player1 = sharedPreferences.getInt("player1knife", R.drawable.blue_knife) // 0 is the default value if the key is not found
        phoneOwner.default_knife_skin_for_player2 = sharedPreferences.getInt("playerknife2",R.drawable.red_knife)
    }
}
