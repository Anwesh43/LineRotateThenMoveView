package com.anwesh.uiprojects.linerotatethenmoveview

/**
 * Created by anweshmishra on 11/01/20.
 */

import android.content.Context
import android.app.Activity
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.view.MotionEvent

val nodes : Int = 5
val steps : Int = 5
val scGap : Float = 0.02f / 5
val delay : Long = 20
val strokeFactor : Float = 90f
val foreColor : Int = Color.parseColor("#3F51B5")
val backColor : Int = Color.parseColor("#BDBDBD")
