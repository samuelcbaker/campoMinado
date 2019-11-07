package com.sbaker.campominado.view

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.sbaker.campominado.R
import com.sbaker.campominado.model.Field
import com.sbaker.campominado.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var rows = 13
    var columns = 10
    var qtdBombs = 15

    lateinit var items: Array<Array<Field>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUI()
    }

    private fun setupUI() {
        /**
         * Estrutura de dados que permite definir valores para os atributos do objeto repetir o nome da variável
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
         * Inicializando a matriz e colocando um Field padrão para cada elemento
         */
        items = Array(rows){ Array(columns, { Field(this, displayMetrics, columns) })}

        for(row in 0..rows-1){
            for (column in 0..columns-1){
                board.addView(items[row][column].label)

                /**
                 * Listener nas labels para sortear as bombas
                 */
                items[row][column].label.setOnClickListener {
                    positionBombs(row, column)
                }
            }
        }
    }

    private fun positionBombs(clickedRow: Int, clickedColumn: Int){
        var bombs = qtdBombs

        while (bombs > 0){
            val row = (0..rows-1).random()
            val column = (0..columns-1).random()

            /**
             * Se o valor da celula já não for uma bomba e não for a celula clicada, adiciona a bomba
             */
            if(items[row][column].value == Constants.BLANK_VALUE && (row != clickedRow && column != clickedColumn)){
                items[row][column].value = Constants.BOMB_VALUE
                items[row][column].setLabelText("*")
                bombs--
            }
        }

        /**
         * Para nao posicionar mais bombas.
         */
        for(row in 0..rows-1){
            for (column in 0..columns-1){
                /**
                 * Listener nas labels para sortear as bombas
                 */
                items[row][column].label.setOnClickListener {
                    showValue(row, column)
                }
            }
        }
    }

    private fun showValue(clickedRow: Int, clickedColumn: Int){

        var count = 0
        for(row in clickedRow - 1 .. clickedRow + 1){
            for (column in clickedColumn - 1 .. clickedColumn + 1){
                if(row >= 0 && column >= 0 && row < rows && column < columns) {
                    val field = items[row][column]
                    if ( (row != clickedRow || column != clickedColumn) && field.value == Constants.BOMB_VALUE) {
                        count++
                    }
                }
            }
        }

        items[clickedRow][clickedColumn].value = count
        items[clickedRow][clickedColumn].setLabelText(count.toString())
    }
}
