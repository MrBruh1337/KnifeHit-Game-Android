package com.example.knifehit.Activities

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix

import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.knifehit.GameViewModel
import com.example.knifehit.MediaPlayers
import com.example.knifehit.MenuFragment
import com.example.knifehit.MusicManager
import com.example.knifehit.PhoneOwner
import com.example.knifehit.Players
import com.example.knifehit.R
import com.example.knifehit.data.Fruit
import com.example.knifehit.data.Placedknifes
import com.example.knifehit.databinding.ActivityTwoPlayerBinding
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates
import kotlin.random.Random


class TwoPlayerActivity : AppCompatActivity() {
    private var knifeStartPositionX by Delegates.notNull<Float>()
    private var knifeStartPositionY by Delegates.notNull<Float>()
    private var redknifeStartPositionY by Delegates.notNull<Float>()
    private val phoneOwner = PhoneOwner
    private lateinit var binding : ActivityTwoPlayerBinding
    private var dummyknifeList  = mutableListOf<ImageView>()
    private var Main_Score by Delegates.notNull<Int>()

    private lateinit var broken1 : ImageView
    private lateinit var broken2 : ImageView
    private lateinit var broken3 : ImageView

    private lateinit var knife: ImageView
    private lateinit var fruit : ImageView
    private lateinit var ghostknife : ImageView
    private lateinit var target : ImageView
    private lateinit var layout : RelativeLayout
    private val knifeList = mutableListOf<Placedknifes>()
    private val fruitList = mutableListOf<Fruit>()
    private var isKnifeThrown = false
    private var isCollisionDetected = false
    private lateinit var konfeti : KonfettiView

    private lateinit var mediaController : MediaPlayers
    private lateinit var leveltext : TextView
    private lateinit var scoretext: TextView
    private val knifecountimages = mutableListOf<ImageView>()
    private lateinit var player1 : Players
    private lateinit var player2 : Players
    private lateinit var currentPlayer : Players
    private lateinit var gameViewModel : GameViewModel
    private var rotationwillhappen = false

    @SuppressLint("ClickableViewAccessibility", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("oncreate called ", "oncreate called")
        mediaController = MediaPlayers(applicationContext)
        mediaController.startlevelSound.start()
        gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]

        super.onCreate(savedInstanceState)
        binding = ActivityTwoPlayerBinding.inflate(layoutInflater)
        broken1 = binding.broken1
        broken2 = binding.broken2
        broken3 = binding.broken3
        layout = binding.layoutrelative2
        knife = binding.knife2
        target = binding.log2
        konfeti = binding.konfeti2
        leveltext = binding.leveltext2
        scoretext = binding.scoretext
        fruit = binding.fruit2
        ghostknife = binding.ghostknife2!!
        hideSystemUI()
        setContentView(binding.root)


        initLevel()

        val rootView = window.decorView.rootView
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // This method will be called when the layout is complete
                // Place your code here to perform actions after all views are placed
                // Don't forget to remove the listener if you only need it once
                rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                knife.setImageResource(phoneOwner.default_knife_skin_for_player1)
                // Call your function here after the layout is complete
                knifeStartPositionX = knife.x
                knifeStartPositionY = knife.y
                redknifeStartPositionY = knifeStartPositionY -target.height -knife.height*3
                updateCountKnifeImages()
                makeObstacle()
            }
        })





        val rotationHandler = Handler(Looper.getMainLooper())
        val rotationRunnable = object : Runnable {
            override fun run() {
                target.rotation = (target.rotation + gameViewModel.knife_spin) % 360
                rotationHandler.postDelayed(this, 5)
            }
        }
        rotationHandler.post(rotationRunnable)


        //click knife action
        layout.setOnTouchListener{_, event ->
            if(event.action == MotionEvent.ACTION_DOWN ){
                if(currentPlayer.throwsRemaining != 0) {
                    if (!isKnifeThrown) {
                        throwKnife()// where the fun begins
                        currentPlayer.decrementThrows()
                    }
                }
            }
            true
        }
    }
    private fun savePlayerScore(score: Int){
        val sharedPreferences = getSharedPreferences("PlayerScores", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("PlayerScore", score)  // Use a unique key for each player
        editor.apply()
    }
    private fun loadPlayerScore(): Int {
        val sharedPreferences = getSharedPreferences("PlayerScores", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("PlayerScore", 0)  // 0 is the default value if the key is not found
    }

    private fun hideSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }

    @SuppressLint("SuspiciousIndentation", "ResourceType")
    private fun throwKnife() {
        val knifehitSound = mediaController.createknifehitSound(applicationContext)
        isKnifeThrown = true

        val targetX = target.x + target.width / 2 - knife.width / 2
        val targetY = target.y
        val knifeAnimation = ObjectAnimator.ofPropertyValuesHolder(
            knife,
            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, targetX - knife.x),
            PropertyValuesHolder.ofFloat(View.TRANSLATION_Y,
            if (currentPlayer == player1) targetY + target.height - (knife.height / 2f) - knife.y
            else -targetY - target.height + (knife.height / 2f) + knife.y)
        )
        knifeAnimation.duration = 200
        knifeAnimation.start()
        knifeAnimation.addUpdateListener { _ ->
            if (!isCollisionDetected) {
                for (existingKnife in knifeList) {
                    if (intersects(existingKnife.imageView, knife)) {//define what will happen if you hit knife
                        // Set the collision flag to true to prevent multiple dialogs
                        isCollisionDetected = true
                        layout.removeView(knife)

                        val newKnife = ImageView(this@TwoPlayerActivity)
                        newKnife.setImageResource(if (currentPlayer == player1) phoneOwner.default_knife_skin_for_player1 else phoneOwner.default_knife_skin_for_player2)
                        if(currentPlayer == player2){
                            val originalBitmap = (newKnife.drawable as BitmapDrawable).bitmap
                            val matrix = Matrix()
                            matrix.postScale(1f,-1f,originalBitmap.width/2f , originalBitmap.height / 2f)
                            val flippedBitmap = Bitmap.createBitmap(originalBitmap,0,0,originalBitmap.width,originalBitmap.height,matrix,true)
                            newKnife.setImageBitmap(flippedBitmap)
                        }
                        val params = RelativeLayout.LayoutParams(knife.width, knife.height)
                        params.leftMargin = knife.x.toInt()
                        params.topMargin = if(currentPlayer == player1) (target.y + target.height - (knife.height / 2)+knife.height/2).toInt() else(target.y - (knife.height / 2)).toInt()
                        newKnife.layoutParams = params
                        layout.addView(newKnife)
                        if(currentPlayer==player2){
                            animationKnife(newKnife,2,0f,true)
                        }else if (currentPlayer == player1){
                            animationKnife(newKnife,1,0f)
                        }

                        Handler(Looper.getMainLooper()).postDelayed({
                            val fragmentManager = supportFragmentManager
                            val fragmentTransaction = fragmentManager.beginTransaction()
                            fragmentTransaction.replace(R.id.fragmentcontain, MenuFragment())
                            fragmentTransaction.commit()
                            //popupDialog.show(supportFragmentManager, "popup_dialog")
                            gameViewModel.setUp(gameViewModel.currentLevel)
                        },if(currentPlayer==player1)1800 else 2700)



                        break
                    }
                }
                if (!fruitList.isEmpty()) {//defines what will happens it you hit fruits
                    val iterator = fruitList.iterator()
                    while (iterator.hasNext()) {
                        val fruits = iterator.next()
                        if (intersects(fruits.imageview, knife)) {
                            if (fruits.id == R.drawable.speed_4_x) {
                                gameViewModel.knife_spin = if (gameViewModel.knife_spin < 0) -7 else 7
                                layout.removeView(fruits.imageview)
                            }
                            if (fruits.id == R.drawable.reverse) {
                                gameViewModel.knife_spin = gameViewModel.knife_spin * -1
                                iterator.remove() // Safely remove the element
                                layout.removeView(fruits.imageview)
                            }
                            if(fruits.id == R.drawable.speed_2_x){
                                gameViewModel.knife_spin = if (gameViewModel.knife_spin < 0) -5 else 5
                                iterator.remove()
                                layout.removeView(fruits.imageview)
                            }
                        }
                    }
                }

            }
        }
        knifeAnimation.addListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {// on animation end , add knifes and spin , change turns ,
                //checks that level complete or not , change levels and update side knifescount
                if(!isCollisionDetected) {
                    val newKnife = ImageView(this@TwoPlayerActivity)
                    newKnife.setImageResource(if (currentPlayer == player1) phoneOwner.default_knife_skin_for_player1 else phoneOwner.default_knife_skin_for_player2)
                    val params = RelativeLayout.LayoutParams(knife.width, knife.height)
                    params.leftMargin = knife.x.toInt()
                    params.topMargin = if(currentPlayer == player1) (target.y + target.height - (knife.height / 2)).toInt() else(target.y - (knife.height / 2)).toInt()
                    newKnife.layoutParams = params
                    newKnife.elevation = target.elevation - 1.0f

                    //make newknife to the target
                    if(currentPlayer == player2){
                        val originalBitmap = (newKnife.drawable as BitmapDrawable).bitmap
                        val matrix = Matrix()
                        matrix.postScale(1f,-1f,originalBitmap.width/2f , originalBitmap.height / 2f)
                        val flippedBitmap = Bitmap.createBitmap(originalBitmap,0,0,originalBitmap.width,originalBitmap.height,matrix,true)
                        newKnife.setImageBitmap(flippedBitmap)
                    }


                    val bounceAnimator = AnimatorInflater.loadAnimator(
                        applicationContext,
                        R.animator.bounce_animation
                    )
                    bounceAnimator.setTarget(target)
                    bounceAnimator.start()


                    val rotationHandler = Handler(Looper.getMainLooper())//looper


                    if(currentPlayer == player1){
                        val rotationRunnable = object : Runnable {
                            override fun run() {
                                //if(!rotationwillhappen) {
                                    newKnife.pivotY = -target.height / 2f + knife.height / 2
                                    newKnife.rotation = (newKnife.rotation + gameViewModel.knife_spin) % 360
                                //}else {
                                //    newKnife.pivotY = +target.height / 2f - knife.height / 2
                                //}
                                rotationHandler.postDelayed(this, 5)
                            }
                        }
                        rotationHandler.post(rotationRunnable)
                    }else {
                        val rotationRunnable = object : Runnable {
                            override fun run() {
                                //if(!rotationwillhappen){
                                    newKnife.pivotY = +target.height/2f + knife.height/2f
                                    newKnife.rotation = (newKnife.rotation + gameViewModel.knife_spin) % 360
                                //}else{
                                //newKnife.pivotY = knife.height/2f
                                //}

                                rotationHandler.postDelayed(this, 5)
                            }
                        }
                        rotationHandler.post(rotationRunnable)
                    }

                    // Add the new knife view to the layout

                    layout.addView(newKnife)

                    knifeList.add(Placedknifes(newKnife,if (currentPlayer==player1) 1 else 2))
                    isKnifeThrown = false
                    if(currentPlayer == player1) currentPlayer=player2 else currentPlayer=player1
                    knife.setImageResource(if (currentPlayer == player1) phoneOwner.default_knife_skin_for_player1 else phoneOwner.default_knife_skin_for_player2)
                    knife.x = knifeStartPositionX
                    knife.y = if(currentPlayer==player1) knifeStartPositionY else{ redknifeStartPositionY}
                    knife.rotation = (knife.rotation + 180f)

                    if(player1.throwsRemaining == 0 && player2.throwsRemaining==0){
                        Main_Score += gameViewModel.count_passed_levels
                        gameViewModel.count_passed_levels++
                        savePlayerScore(Main_Score)

                        knife.visibility = View.INVISIBLE

                        knifeList.forEach{
                                knife -> val rect = Rect()
                            knife.imageView.getHitRect(rect)
                            val brandnewKnife = ImageView(this@TwoPlayerActivity)
                            //burasıda öyle
                            brandnewKnife.setImageResource(if(knife.id == 1) phoneOwner.default_knife_skin_for_player1 else if (knife.id == 2) phoneOwner.default_knife_skin_for_player2 else R.drawable.knife_image)
                            val params = RelativeLayout.LayoutParams(knife.imageView.width, knife.imageView.height)
                            params.leftMargin = rect.left + knife.imageView.width
                            params.topMargin = rect.top
                            brandnewKnife.layoutParams = params
                            brandnewKnife.elevation = target.elevation - 1.0f
                            brandnewKnife.rotation = (knife.imageView.rotation + gameViewModel.knife_spin) % 360
                            layout.removeView(knife.imageView)
                            layout.addView(brandnewKnife)
                            animationKnife(brandnewKnife, knife.id,brandnewKnife.rotation)
                            dummyknifeList.add(brandnewKnife)
                        }
                        rotationwillhappen = true
                        mediaController.breakSound.start()
                        breakAnimation()
                        fruitList.forEach { apple ->
                            layout.removeView(apple.imageview)
                        }
                        fruitList.clear()
                        val handler = Handler()

                        val dummy = gameViewModel.currentLevel + 1
                        if(dummy >5 ) gameViewModel.currentLevel = 1
                        gameViewModel.setUp(dummy)

                        handler.postDelayed({
                            dummyknifeList.forEach{
                                    dummy -> layout.removeView(dummy)
                            }
                            dummyknifeList.clear()
                            knifeList.forEach{
                                    knife->layout.removeView(knife.imageView)
                            }
                            knifeList.clear()
                            initLevel()
                            updateCountKnifeImages()
                            makeObstacle()
                        }, 2000)

                    }
                    updateCountKnifeImages()

                    knifehitSound.start()
                }else{
                    mediaController.knifehitFail.start()
                }
            }


            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        })
    }
    private fun animationKnife(newKnife : ImageView,id : Int, rotation: Float){
        val translationYAnimator = ObjectAnimator.ofFloat(newKnife, "translationY", 0f,layout.height.toFloat()) // Adjust the values as needed
        translationYAnimator.duration = 3000
        translationYAnimator.interpolator = AccelerateDecelerateInterpolator()
        val randomint = Random.nextInt(360,500)

        val rotationAnimator = ObjectAnimator.ofFloat(newKnife, "rotation", rotation,if(Random.nextBoolean())randomint.toFloat() else -1 * randomint.toFloat()) // Adjust the values as needed
        rotationAnimator.duration = 2700
        rotationAnimator.interpolator = AccelerateDecelerateInterpolator()

        val translationXAnimator = ObjectAnimator.ofFloat(newKnife, "translationX", 0f,Random.nextInt(-820,820).toFloat()) // Adjust the values as needed
        translationXAnimator.duration = 2700
        translationXAnimator.interpolator = AccelerateDecelerateInterpolator()

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translationYAnimator,translationXAnimator,rotationAnimator)
        animatorSet.start()

    }
    private fun animationKnife(newKnife : ImageView,id : Int, rotation: Float, flag : Boolean){
        val translationYAnimator = ObjectAnimator.ofFloat(newKnife, "translationY", 0f,if(flag)-layout.height.toFloat()else layout.height.toFloat()) // Adjust the values as needed
        translationYAnimator.duration = 3000
        translationYAnimator.interpolator = AccelerateDecelerateInterpolator()
        val randomint = Random.nextInt(360,500)

        val rotationAnimator = ObjectAnimator.ofFloat(newKnife, "rotation", rotation,if(Random.nextBoolean())randomint.toFloat() else -1 * randomint.toFloat()) // Adjust the values as needed
        rotationAnimator.duration = 2700
        rotationAnimator.interpolator = AccelerateDecelerateInterpolator()

        val translationXAnimator = ObjectAnimator.ofFloat(newKnife, "translationX", 0f,Random.nextInt(-820,820).toFloat()) // Adjust the values as needed
        translationXAnimator.duration = 2700
        translationXAnimator.interpolator = AccelerateDecelerateInterpolator()

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translationYAnimator,translationXAnimator,rotationAnimator)
        animatorSet.start()

    }

    private fun breakAnimation(){
        target.visibility=View.INVISIBLE
        broken1.visibility = View.VISIBLE
        broken2.visibility = View.VISIBLE
        broken3.visibility = View.VISIBLE
        breakAnimationhelper(broken3,3)
        breakAnimationhelper(broken2,3)
        breakAnimationhelper(broken1,3)
    }
    private fun breakAnimationhelper(newKnife : ImageView,id :Int){
        val randomint = Random.nextInt(-600,600)
        val randomtime = Random.nextInt(2800, 3300)


        val randomrota = Random.nextInt(360, 720) * if(Random.nextBoolean()) 1 else -1
        val rotationAnimator = ObjectAnimator.ofFloat(newKnife, "rotation", 0f,randomrota.toFloat() ) // Adjust the values as needed
        rotationAnimator.duration = randomtime.toLong()
        rotationAnimator.interpolator = AccelerateDecelerateInterpolator()

        val translationXAnimator = ObjectAnimator.ofFloat(newKnife, "translationX", if(id == 4)-knife.height/2f else 0f,randomint.toFloat()) // Adjust the values as needed
        translationXAnimator.duration = randomtime.toLong()
        translationXAnimator.interpolator = AccelerateDecelerateInterpolator()

        val translationYAnimator = ObjectAnimator.ofFloat(newKnife, "translationY", 0f, layout.height.toFloat() ) // Adjust the values as needed
        translationYAnimator.duration = randomtime.toLong()
        translationYAnimator.interpolator = AccelerateDecelerateInterpolator()

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(rotationAnimator,translationXAnimator,translationYAnimator)
        animatorSet.start()

    }


    private fun intersects(view1: ImageView, view2: ImageView): Boolean { // collision detect function coulb be better and could be fix here
        val rect1 = Rect()
        val rect2 = Rect()

        view1.getGlobalVisibleRect(rect1)
        view2.getGlobalVisibleRect(rect2)


        val targetCenterX = rect2.centerX()
        val targetCenterY = rect2.centerY()



        val targetRadius = rect2.width() / 2


        val distance = Math.sqrt(
            Math.pow(targetCenterX - rect1.centerX().toDouble(), 2.0) +
                    Math.pow(targetCenterY - rect1.centerY().toDouble(), 2.0)
        ).toFloat()

        return distance < targetRadius
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaController.knifehitFail.release()
        mediaController.startlevelSound.release()
        mediaController.breakSound.release()
    }
    private fun updateCountKnifeImages(){
        knifecountimages.forEach{
                knifes->layout.removeView(knifes)
        }



        for(count in 1..player2.throwsRemaining){
            val countKnife2 = ImageView(this@TwoPlayerActivity)
            countKnife2.setImageResource(R.drawable.red_knife_count_indicator)
            val params2 = RelativeLayout.LayoutParams(ghostknife.width,ghostknife.height)
            params2.leftMargin = knife.x.toInt() -knife.width/2 -target.height/2 -knife.width
            params2.topMargin =  redknifeStartPositionY.toInt() +((knife.width/2)*count) -knife.height/2
            countKnife2.layoutParams = params2
            countKnife2.rotation = 180f
            layout.addView(countKnife2)
            knifecountimages.add(countKnife2)
        }
        for(count in 1..player2.throwalready){
            val countKnife2 = ImageView(this@TwoPlayerActivity)
            countKnife2.setImageResource(R.drawable.red_knife_count_indicator)
            val params2 = RelativeLayout.LayoutParams(ghostknife.width,ghostknife.height)
            params2.leftMargin = knife.x.toInt() -knife.width/2 -target.height/2 -knife.width
            params2.topMargin =  redknifeStartPositionY.toInt() +((knife.width/2)*(count+player2.throwsRemaining)) -knife.height/2
            countKnife2.alpha = 0.3f
            countKnife2.layoutParams = params2
            countKnife2.rotation=180f
            layout.addView(countKnife2)
            knifecountimages.add(countKnife2)
        }



        for(count in 1..player1.throwsRemaining) {
            val countKnife = ImageView(this@TwoPlayerActivity)
            countKnife.setImageResource(R.drawable.blue_knife_count_indicator)
            val params = RelativeLayout.LayoutParams(ghostknife.width,ghostknife.height)
            params.leftMargin = knife.x.toInt() + target.height / 2 + knife.width
            params.topMargin = knifeStartPositionY.toInt() -((knife.width/2)*count)+knife.height/2
            countKnife.layoutParams = params
            layout.addView(countKnife)
            knifecountimages.add(countKnife)
        }
        for(count in 1..player1.throwalready) {
            val countKnife = ImageView(this@TwoPlayerActivity)
            countKnife.setImageResource(R.drawable.blue_knife_count_indicator)
            val params = RelativeLayout.LayoutParams(ghostknife.width,ghostknife.height)
            params.leftMargin = knife.x.toInt() + target.height / 2 + knife.width
            params.topMargin = knifeStartPositionY.toInt() -((knife.width/2)*(count + player1.throwsRemaining)) +knife.height/2
            countKnife.layoutParams = params
            countKnife.alpha = 0.3f
            layout.addView(countKnife)
            knifecountimages.add(countKnife)
        }
    }
    private fun initLevel(){

        Main_Score = loadPlayerScore()
        scoretext.text="Score: "+Main_Score
        rotationwillhappen=false
        knife.visibility=View.VISIBLE
        target.visibility= View.VISIBLE
        broken3.visibility = View.INVISIBLE
        broken2.visibility = View.INVISIBLE
        broken1.visibility = View.INVISIBLE

        player1 = Players("BLUE", phoneOwner.default_knife_skin_for_player1,gameViewModel.knifeCount)
        player2 = Players("RED", phoneOwner.default_knife_skin_for_player2,gameViewModel.knifeCount)
        currentPlayer = player1
        leveltext.text = "LEVEL "+gameViewModel.currentLevel


        //spin the target target spin speed and rotation remains same, knifes spin and rotation changes
        // could be modified this for accurate spin with knifes

    }
    private fun makeObstacle() { //to modify levels
        when(gameViewModel.currentLevel){
            1->{//test new level here before implementation

            }
            2->{
                val dumknife:ImageView = makeknife()
                dumknife.rotation=90f
                layout.addView(dumknife)
                val orange : ImageView = makefruit()
                orange.rotation=180f
                fruitList.add(Fruit(orange, R.drawable.speed_4_x))
                layout.addView(orange)
                val pear : ImageView = makefruit()
                pear.setImageResource(R.drawable.reverse)
                fruitList.add(Fruit(pear, R.drawable.reverse))
                layout.addView(pear)
            }
            3->{
                for(i in 0..2) {
                    val dumknife : ImageView = makeknife()
                    dumknife.rotation = if(i == 0) 45f else if (i==1) 135f else 315f
                    layout.addView(dumknife)
                }
                for(i in 0..1){
                    val pear : ImageView = makefruit()
                    pear.setImageResource(R.drawable.reverse)
                    fruitList.add(Fruit(pear, R.drawable.reverse))
                    pear.rotation = if(i == 0) 0f else 95f
                    layout.addView(pear)
                }
                val apple : ImageView = makefruit()
                apple.setImageResource(R.drawable.speed_2_x)
                fruitList.add(Fruit(apple, R.drawable.speed_2_x))
                apple.rotation = 220f
                layout.addView(apple)
            }
            4->{
                val randomintarray = generateRandomIntArray(6)
                randomintarray.shuffle(Random)
                for(i in 1..4){
                    val dumknife = makeknife()
                    dumknife.rotation = (randomintarray[i-1] * 20).toFloat()
                    layout.addView(dumknife)
                }
                for(i in 5..6){
                    val randomfruitchoose = Random.nextInt(1,4)
                    val randomfruit : ImageView = makefruit()
                    randomfruit.setImageResource(if(randomfruitchoose == 1) R.drawable.speed_4_x else if(randomfruitchoose == 2) R.drawable.reverse else R.drawable.speed_2_x)
                    fruitList.add(Fruit(randomfruit,if(randomfruitchoose == 1) R.drawable.speed_4_x else if(randomfruitchoose == 2) R.drawable.reverse else R.drawable.speed_2_x ))
                    randomfruit.rotation = (randomintarray[i-1] * 20 ).toFloat()
                    layout.addView(randomfruit)
                }

            }
            5->{
                for(i in 1..17){
                    val dumknife = makeknife()
                    dumknife.rotation = (i*20).toFloat()
                    if (i == 9) continue
                    layout.addView(dumknife)
                }
                val apple : ImageView = makefruit()
                apple.setImageResource(R.drawable.speed_2_x)
                fruitList.add(Fruit(apple, R.drawable.speed_2_x))
                apple.rotation = 360f
                layout.addView(apple)
            }

        }
    }
    fun generateRandomIntArray(size: Int): IntArray {
        val numberSet = HashSet<Int>()
        val resultArray = IntArray(size)

        while (numberSet.size < size) {
            val randomNum = Random.nextInt(1,19) // Generates a random number between 1 and 18 (inclusive).
            numberSet.add(randomNum)
        }

        // Convert the HashSet to an array
        val iterator = numberSet.iterator()
        var index = 0
        while (iterator.hasNext()) {
            resultArray[index] = iterator.next()
            index++
        }

        return resultArray
    }
    private fun makeknife():ImageView{
        val dumknife = ImageView(this@TwoPlayerActivity)
        dumknife.setImageResource(R.drawable.knife_image)
        val params = RelativeLayout.LayoutParams(knife.width, knife.height)
        params.leftMargin = knife.x.toInt()
        params.topMargin = (target.y + target.height - (knife.height / 2)).toInt()
        dumknife.layoutParams = params
        dumknife.elevation = target.elevation - 1.0f
        knifeList.add(Placedknifes(dumknife,4))

        val rotationHandler = Handler(Looper.getMainLooper())
        val rotationRunnable = object : Runnable {
            override fun run() {
                dumknife.pivotY = -target.height / 2f + (knife.height / 2)
                dumknife.rotation = (dumknife.rotation + gameViewModel.knife_spin) % 360
                rotationHandler.postDelayed(this, 5)
            }
        }
        rotationHandler.post(rotationRunnable)
        return dumknife
    }
    private fun makefruit():ImageView{
        val apple = ImageView(this@TwoPlayerActivity)
        apple.setImageResource(R.drawable.speed_4_x)
        val params = RelativeLayout.LayoutParams(fruit.width,fruit.height)
        params.leftMargin = knife.x.toInt()
        params.topMargin = (target.y + target.height - (fruit.height/4)).toInt()
        apple.layoutParams = params
        apple.elevation = target.elevation - 1.0f
        val rotationHandler = Handler(Looper.getMainLooper())
        val rotationRunnable = object : Runnable {
            override fun run() {
                apple.pivotY = -target.height / 2f + (fruit.height/4f)
                apple.rotation = (apple.rotation + gameViewModel.knife_spin) % 360
                rotationHandler.postDelayed(this, 5)
            }
        }
        rotationHandler.post(rotationRunnable)

        return apple
    }
    override fun onPause() {
        super.onPause()
        MusicManager.pause()
    }

    override fun onResume() {
        super.onResume()
        MusicManager.resume()
    }
}