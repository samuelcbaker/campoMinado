package com.sbaker.campominado.view

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sbaker.campominado.R
import com.sbaker.campominado.model.Field
import com.sbaker.campominado.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var rows = 13
    var columns = 10
    var qtdBombs = 15
    var qtdSafeFields = rows * columns - qtdBombs
    var gameHidden = true

    lateinit var items: Array<Array<Field>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSetup()
        startGame()
    }

    private fun initSetup(){
        btnTryAgain.setOnClickListener {
            restartGame()
        }
    }

    private fun startGame() {
        setVisibilityEndItems(View.GONE)

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
                    showValue(row, column)
                }
            }
        }
    }

    private fun restartGame(){
        qtdSafeFields = rows * columns - qtdBombs
        board.removeAllViews()
        startGame()
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
                items[row][column].setLabelText("*", R.color.gray)
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
        val field = items[clickedRow][clickedColumn]

        if(field.value == Constants.BOMB_VALUE){
            showAllBombs()
            field.setLabelText("*", R.color.redLight)
            defeat()
            return
        }

        var count = 0

        /**
         * Loop que passa por um quadrado 3x3, com o item clicado no meio
         */
        for(row in clickedRow - 1 .. clickedRow + 1){
            for (column in clickedColumn - 1 .. clickedColumn + 1){
                // se a linha e coluna estão dentro do tamanho do tabuleiro inteiro
                if(row >= 0 && column >= 0 && row < rows && column < columns) {
                    // se não for o item clicado e se for uma bomba
                    if ( (row != clickedRow || column != clickedColumn) && items[row][column].value == Constants.BOMB_VALUE) {
                        count++
                    }
                }
            }
        }

        field.value = count
        field.setLabelText(count.toString(), R.color.grayLight)

        //Diminuir a quantidade de campos seguros somente quando aquele item não foi clicado
        if(!field.clicked) {
            qtdSafeFields--
            field.clicked = true
            field.visible = true
        }

        if(count == 0){
            for(row in clickedRow - 1 .. clickedRow + 1){
                for (column in clickedColumn - 1 .. clickedColumn + 1){
                    // se a linha e coluna estão dentro do tamanho do tabuleiro inteiro
                    if(row >= 0 && column >= 0 && row < rows && column < columns) {
                        // se não for o item clicado
                        if (row != clickedRow || column != clickedColumn) {
                            if(!items[row][column].clicked) {
                                showValue(row, column)
                            }
                        }
                    }
                }
            }
        }

        if(qtdSafeFields == 0){
            victory()
        }
    }

    private fun showAllBombs() {
        items.forEach {
            it.forEach {
                if(it.value == Constants.BOMB_VALUE){
                    it.setLabelText("*", R.color.grayLight)
                }
            }
        }
    }

    private fun victory(){
        labelResult.text = resources.getString(R.string.win)
        setVisibilityEndItems(View.VISIBLE)
        cleanListeners()
    }

    private fun defeat(){
        labelResult.text = resources.getString(R.string.lose)
        setVisibilityEndItems(View.VISIBLE)
        cleanListeners()
    }

    private fun cleanListeners(){
        items.forEach {
            it.forEach {
                it.label.setOnClickListener{}
            }
        }
    }

    private fun setVisibilityEndItems(visibility: Int){
        labelResult.visibility = visibility
        btnTryAgain.visibility = visibility
    }

    private fun hiddenOff(){
        items.forEach {
            it.forEach {
                it.setLabelText(it.value.toString(), R.color.grayLight)
            }
        }
    }

    private fun hiddenOn(){
        items.forEach {
            it.forEach {
                if(!it.visible) {
                    it.setLabelText("", R.color.gray)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_restart -> {
                restartGame()
                true
            }
            R.id.action_visibility -> {
                if(gameHidden){
                    item.setIcon(resources.getDrawable(R.drawable.ic_visibility_off))
                    hiddenOff()
                } else {
                    item.setIcon(resources.getDrawable(R.drawable.ic_visibility))
                    hiddenOn()
                }
                
                gameHidden = !gameHidden
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
