package com.example.knifehit

import androidx.lifecycle.ViewModel

class GameViewModel: ViewModel() {
    //level 1 attributes is actually sets here
    var currentLevel : Int = 1
    var knifeCount : Int = 2
    var knife_spin : Int = 3
    var count_passed_levels : Int = 1


    fun setUp(level : Int ){
        when(level){//modify levels

            1->{//if we want to call setUp(1) those should be stay here
                 currentLevel = 1
                 knifeCount  = 2
                 knife_spin  = 3
            }
            2->{
                currentLevel = 2
                knifeCount = 3
                knife_spin = 3
            }
            3->{
                currentLevel = 3
                knifeCount = 2
                knife_spin = 4
            }
            4->{
                currentLevel= 4
                knifeCount = 2
                knife_spin = 4
            }
            5->{
                currentLevel= 5
                knifeCount = 1
                knife_spin = 4
            }
        }
    }


}