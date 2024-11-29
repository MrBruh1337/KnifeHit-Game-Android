package com.example.knifehit

import android.content.Context
import android.media.MediaPlayer

class MediaPlayers(context : Context){
    private var _knifehitFail : MediaPlayer? = MediaPlayer.create(context,R.raw.knifehitfail)
    public val knifehitFail get() = _knifehitFail!!
    private var _startlevelSound : MediaPlayer? = MediaPlayer.create(context,R.raw.startlevel)
    public val startlevelSound get() = _startlevelSound!!
    private var _breakSound : MediaPlayer? = MediaPlayer.create(context,R.raw.breakwood)
    public val breakSound get() = _breakSound!!



    public fun createknifehitSound(context: Context):MediaPlayer{
        var knifehitSound = MediaPlayer.create(context,R.raw.knifehit)
        return knifehitSound
    }
}