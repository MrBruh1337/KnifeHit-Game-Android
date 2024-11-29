package com.example.knifehit

class Players(val name: String, var knifeImageResource: Int, val starKnifeCount: Int) {
    var throwsRemaining: Int = starKnifeCount
    fun decrementThrows() {
        if (throwsRemaining > 0) {
            throwsRemaining--
            throwalready++
        }
    }

    var throwalready: Int = 0
    fun setKnifeImage(newsource: Int) {
        knifeImageResource = newsource
    }
}