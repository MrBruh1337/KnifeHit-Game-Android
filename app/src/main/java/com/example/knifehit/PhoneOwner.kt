package com.example.knifehit

import android.content.Context


object PhoneOwner {
     var default_knife_skin_for_player1 : Int = R.drawable.blue_knife
     var default_knife_skin_for_player2 : Int = R.drawable.red_knife2
     fun loadPlayerScore(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("PlayerScores", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("PlayerScore", 0)// 0 is the default value if the key is not found
    }
    fun savePlayerKnifes(KnifeId: Int, context: Context) {
        val sharedPreferences = context.getSharedPreferences("OwnerKnifes", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Step 1: Retrieve the existing set
        val existingKnifes = getSavedKnifes(context).toMutableSet()

        // Step 2: Modify the set by adding the new KnifeId
        existingKnifes.add(KnifeId.toString())

        // Step 3: Save the updated set back to SharedPreferences
        editor.putStringSet("Knifes", existingKnifes)
        editor.apply()
    }
    fun getSavedKnifes(context: Context): MutableSet<String> {
        val sharedPreferences = context.getSharedPreferences("OwnerKnifes", Context.MODE_PRIVATE)
        return sharedPreferences.getStringSet("Knifes", mutableSetOf()) ?: mutableSetOf()
    }
    fun setDefaultKnife(resourceId: Int,context: Context,Player_choose : Boolean){
        if(Player_choose){
            default_knife_skin_for_player1 = resourceId
            val sharedPreferences = context.getSharedPreferences("Usingknifes",Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("player1knife",resourceId)
            editor.apply()
        }else{
            default_knife_skin_for_player2=resourceId
            val sharedPreferences = context.getSharedPreferences("Usingknifes",Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("playerknife2",resourceId)
            editor.apply()
        }
    }
}