package com.example.campominado.view

import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.campominado.R
import com.example.campominado.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    var rows = 13
    var columns = 10
    var qtdBombs = 15

    var items = ArrayList<ArrayList<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupUI()
    }

    private fun setupUI() {
        /**
         * Estrutura de dados que permite setar os atributos do objeto repetir o nome da variável
         * Parecido com um builder
         */
        board.apply {
            rowCount = rows
            columnCount = columns
        }

        /**
         * Com o DisplayMetrics, consigo pegar o tamanho total da tela do celular em pixels
         */
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

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

        for (i in 1..rows * columns) {
            val label = TextView(this)
            board.addView(label.apply {
                background = resources.getDrawable(R.drawable.select_item) //Drawable para fazer a animação do clique
                layoutParams = lytParams
                isClickable = true
            })
        }

        positionBombs(qtdBombs)
        updateFields()
    }

    private fun positionBombs(qtdBombs: Int){
        if(qtdBombs == 0){
            return
        }

        val row = Random.nextInt(0, rows-1)
        val column = Random.nextInt(0, columns-1)

        if(items[row][column].isEmpty()){
            items[row][column] = "*"
            positionBombs(qtdBombs - 1)
        } else {
            positionBombs(qtdBombs)
        }
    }

    private fun updateFields() {
        board.removeAllViews()

        /**
         * Com o DisplayMetrics, consigo pegar o tamanho total da tela do celular em pixels
         */
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

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

        for (i in 1..rows) {
            for (j in 1..columns) {
                val label = TextView(this)
                board.addView(label.apply {
                    background = resources.getDrawable(R.drawable.select_item) //Drawable para fazer a animação do clique
                    layoutParams = lytParams
                    isClickable = true
                    text = items[i][j]
                })
            }
        }
    }
}
