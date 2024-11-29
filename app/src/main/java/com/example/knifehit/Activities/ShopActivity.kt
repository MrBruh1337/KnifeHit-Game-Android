package com.example.knifehit.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.knifehit.ImageItem
import com.example.knifehit.ImageViewAdapter
import com.example.knifehit.MusicManager
import com.example.knifehit.PhoneOwner
import com.example.knifehit.R
import org.w3c.dom.Text
import kotlin.properties.Delegates

class ShopActivity : AppCompatActivity(), ImageViewAdapter.ItemClickListener {
    private val phoneOwner = PhoneOwner
    private lateinit var scoretext : TextView
    private lateinit var soundPool: SoundPool
    private lateinit var player1_button : TextView
    private lateinit var player2_button : TextView
    private var Player_choose = true //true for player1
    private var soundclick by Delegates.notNull<Int>()
    private lateinit var  imageList : List<ImageItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
        val recyclerView : RecyclerView = findViewById(R.id.rv_shop)
        val numberofcolumns = 3
        soundPool = SoundPool(1, AudioManager.STREAM_MUSIC,0)
        soundclick = soundPool.load(this,R.raw.click,1)
        scoretext = findViewById(R.id.Score_shop)
        scoretext.text = "Score: "+phoneOwner.loadPlayerScore(this)
        val gridLayoutManager = GridLayoutManager(this,numberofcolumns)
        recyclerView.layoutManager = gridLayoutManager

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        player1_button = findViewById(R.id.player1_shop_text)
        player2_button = findViewById(R.id.player2_shop_text)
        val back_button_shop : ImageView = findViewById(R.id.back_button_shop)
        back_button_shop.setOnClickListener{
            soundPool.play(soundclick,1f,1f,0,0,1f)
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        player2_button.setOnClickListener {
            soundPool.play(soundclick,1f,1f,0,0,1f)
            Player_choose = false
            player2_button.setTextColor(resources.getColor(R.color.redish))
            player1_button.setTextColor(resources.getColor(R.color.white))
        }
        player1_button.setOnClickListener{
            soundPool.play(soundclick,1f,1f,0,0,1f)
            Player_choose = true
            player1_button.setTextColor(resources.getColor(R.color.blueish))
            player2_button.setTextColor(resources.getColor(R.color.white))
        }


        val imageList= listOf(
            ImageItem(R.drawable.blue_knife,100),
            ImageItem(R.drawable.red_knife,100),
            ImageItem(R.drawable.blue_knife1,100),
            ImageItem(R.drawable.red_knife1,100),
            ImageItem(R.drawable.blue_knife2,500),
            ImageItem(R.drawable.blue_knife,100),
            ImageItem(R.drawable.red_knife,100),
            ImageItem(R.drawable.blue_knife1,100),
            ImageItem(R.drawable.red_knife1,100),
            ImageItem(R.drawable.blue_knife2,500),
            ImageItem(R.drawable.blue_knife,100),
            ImageItem(R.drawable.red_knife,100),
            ImageItem(R.drawable.blue_knife1,100),
            ImageItem(R.drawable.red_knife1,100),
            ImageItem(R.drawable.blue_knife2,500),
            ImageItem(R.drawable.blue_knife1,100),
            ImageItem(R.drawable.red_knife1,100),
            ImageItem(R.drawable.blue_knife2,500),
            ImageItem(R.drawable.blue_knife,100),
            ImageItem(R.drawable.red_knife,100),
            ImageItem(R.drawable.blue_knife1,100),
            ImageItem(R.drawable.red_knife1,100),
            ImageItem(R.drawable.blue_knife2,500),
            ImageItem(R.drawable.blue_knife,100),
            ImageItem(R.drawable.red_knife,100),
            ImageItem(R.drawable.blue_knife1,100),
            ImageItem(R.drawable.red_knife1,100),
            ImageItem(R.drawable.blue_knife2,500),
            ImageItem(R.drawable.red_knife2,500)
        )
        val adapter = ImageViewAdapter(imageList,this,this@ShopActivity)
        recyclerView.adapter=adapter
    }

    @SuppressLint("CommitPrefEdits", "MutatingSharedPrefs")
    override fun onItemClick(imageItem: ImageItem) {
        //if player already have the knife change skin and do nothing else
        if(phoneOwner.getSavedKnifes(this).contains(imageItem.imageResource.toString())){
            if(Player_choose){
                phoneOwner.setDefaultKnife(imageItem.imageResource,this,true)//set knife to the default
            }else{
                phoneOwner.setDefaultKnife(imageItem.imageResource,this,false)
            }

        }//check if player enough score to buy the knife
        else if(phoneOwner.loadPlayerScore(this) >= imageItem.costItem){
            phoneOwner.savePlayerKnifes(imageItem.imageResource,this)//add knife to the OwnedList
            val sharedPreferences = getSharedPreferences("PlayerScores",Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("PlayerScore",phoneOwner.loadPlayerScore(this)-imageItem.costItem)//decrease playerscore
            editor.apply()
            phoneOwner.setDefaultKnife(imageItem.imageResource,this,Player_choose)//set knife to the default
        }
        soundPool.play(soundclick,1f,1f,0,0,1f)
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        MusicManager.resume()
        scoretext.text = "Score: " + phoneOwner.loadPlayerScore(this)
    }
    override fun onPause() {
        super.onPause()
        MusicManager.pause()
    }

}