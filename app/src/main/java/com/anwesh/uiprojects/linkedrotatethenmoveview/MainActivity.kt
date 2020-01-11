package com.anwesh.uiprojects.linkedrotatethenmoveview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.linerotatethenmoveview.LineRotateThenMoveView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LineRotateThenMoveView.create(this)
    }
}
