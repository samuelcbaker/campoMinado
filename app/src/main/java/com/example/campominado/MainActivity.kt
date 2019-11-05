package com.example.campominado

import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var level = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        board.apply {
            rowCount = level
            columnCount = level
        }
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val boardWidth = displayMetrics.widthPixels - 40 * displayMetrics.density
        val fieldSize = (Math.abs(boardWidth/level) - 12).toInt()

        val lytParams = LinearLayout.LayoutParams( fieldSize, fieldSize)
        lytParams.leftMargin = 10
        lytParams.bottomMargin = 10

        for(i in 1 .. level*level){
            val label = TextView(this)
            board.addView(label.apply {
                background = resources.getDrawable(R.drawable.select_item)
                layoutParams = lytParams
                isClickable = true
            })
        }
    }
}
