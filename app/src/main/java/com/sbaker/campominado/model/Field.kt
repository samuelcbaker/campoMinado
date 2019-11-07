package com.sbaker.campominado.model

import android.content.Context
import android.graphics.Typeface
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.sbaker.campominado.R
import com.sbaker.campominado.utils.Constants

class Field(context: Context, displayMetrics: DisplayMetrics, columns: Int) {
    var value = 0
    var label = TextView(context)
    var clicked = false

    init {
        /**
         * O tamanho total do board = tamanho da tela - (a soma das margins * a densidade) -> necessário multiplicar pela densidade pois o tamanho total da tela é em pixels
         */
        val boardWidth = displayMetrics.widthPixels - (Constants.MARGIN_GRID * displayMetrics.density)

        /**
         * Tamanho de cada campo = (tamanho da board / numero de colunas) - (o total de margin de cada item + margem de erro de 2dp)
         */
        val fieldSize = (Math.abs(boardWidth / columns) - (Constants.MARGIN_ITEM + 2)).toInt()

        val lytParams = LinearLayout.LayoutParams(fieldSize, fieldSize)
        lytParams.leftMargin = Constants.MARGIN_ITEM
        lytParams.bottomMargin = Constants.MARGIN_ITEM

        label.apply {
            background = resources.getDrawable(R.drawable.select_item) //Drawable para fazer a animação do clique
            layoutParams = lytParams
            isClickable = true
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
    }

    fun setLabelText(str: String, color: Int){
        label.apply {
            text = if (value == Constants.BLANK_VALUE) "" else str
            setBackgroundColor(resources.getColor(color))
            when(value){
                1 -> setTextColor(resources.getColor(R.color.blue))
                2 -> setTextColor(resources.getColor(R.color.colorPrimaryDark))
                Constants.BOMB_VALUE -> setTextColor(resources.getColor(R.color.black))
                else -> setTextColor(resources.getColor(R.color.red))
            }
        }
    }
}