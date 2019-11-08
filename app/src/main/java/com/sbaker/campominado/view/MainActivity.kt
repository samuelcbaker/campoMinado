package com.sbaker.campominado.view

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sbaker.campominado.R
import com.sbaker.campominado.enums.ModeEnum.EASY
import com.sbaker.campominado.enums.ModeEnum.MEDIUM
import com.sbaker.campominado.enums.ModeEnum.HARD
import com.sbaker.campominado.model.Field
import com.sbaker.campominado.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var rows = 13
    var columns = 10
    var qtdBombs = 15
    var qtdSafeFields = rows * columns - qtdBombs
    var gameHidden = true
    var mode = EASY

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

        // Estrutura de dados que permite definir valores para os atributos do objeto repetir o nome da variável
        board.apply {
            rowCount = rows
            columnCount = columns
        }

        // Com o DisplayMetrics, consigo pegar o tamanho total da tela do celular em pixels
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)


        // Inicializando a matriz e colocando um Field padrão para cada elemento
        items = Array(rows){ Array(columns, { Field(this, displayMetrics, columns) })}

        // Expressão Lambda -> ótima opção para melhora de performance
        items.forEachIndexed{ row, array ->
            array.forEachIndexed{ column, field ->
                board.addView(field.label)
                field.setListener {
                    positionBombs(row, column)
                    configSafeFields()
                    showValue(row, column)
                }
            }
        }
    }

    private fun restartGame(){
        when(mode){
            EASY -> {
                rows = 13
                columns = 10
                qtdBombs = 15
            }
            MEDIUM -> {
                rows = 10
                columns = 7
                qtdBombs = 18
            }
            HARD -> {
                rows = 7
                columns = 5
                qtdBombs = 21
            }
        }

        qtdSafeFields = rows * columns - qtdBombs
        board.removeAllViews()
        hiddenOn()
        gameHidden = true
        startGame()
    }

    private fun positionBombs(clickedRow: Int, clickedColumn: Int){
        var bombs = qtdBombs

        while (bombs > 0){
            val row = (0..rows-1).random()
            val column = (0..columns-1).random()

            // Se o valor da celula já não for uma bomba e não for a celula clicada, adiciona a bomba
            if(items[row][column].value == Constants.BLANK_VALUE && (row != clickedRow && column != clickedColumn)){
                items[row][column].value = Constants.BOMB_VALUE
                bombs--
            }
        }
    }

    private fun configSafeFields(){
        items.forEachIndexed{ row, array ->
            array.forEachIndexed{ column, field ->
                field.setListener { showValue(row, column) }
                if(field.value != Constants.BOMB_VALUE) {
                    evaluateField(row, column)
                }
            }
        }
    }

    private fun evaluateField(clickedRow: Int, clickedColumn: Int){
        var count = 0

        for(row in clickedRow - 1 .. clickedRow + 1){
            for (column in clickedColumn - 1 .. clickedColumn + 1){
                /**
                 * SE a linha e coluna estão dentro do tamanho do tabuleiro
                 * E o item não foi o clicado inicialmente
                 * E o item ser um bomba
                 * ENTÃO incrementa o count
                 */
                if( (row >= 0 && column >= 0 && row < rows && column < columns) && (row != clickedRow || column != clickedColumn) && (items[row][column].value == Constants.BOMB_VALUE)) {
                    count++
                }
            }
        }

        items[clickedRow][clickedColumn].value = count
    }

    private fun showValue(clickedRow: Int, clickedColumn: Int){
        val field = items[clickedRow][clickedColumn]

        if(field.value == Constants.BOMB_VALUE){
            showAllBombs()
            field.setLabelText("*", R.color.redLight)
            defeat()
            return
        }

        field.setLabelText(field.value.toString(), R.color.grayLight)
        if(!field.clicked) {
            qtdSafeFields--
            field.clicked = true
            field.visible = true
        }

        if(field.value == Constants.BLANK_VALUE){
            for(row in clickedRow - 1 .. clickedRow + 1){
                for (column in clickedColumn - 1 .. clickedColumn + 1){
                    /**
                     * SE a linha e coluna estão dentro do tamanho do tabuleiro
                     * E o item não é o clicado inicialmente
                     * E o item nunca foi clicado
                     */
                    if((row >= 0 && column >= 0 && row < rows && column < columns) && (row != clickedRow || column != clickedColumn) && (!items[row][column].clicked) ) {
                        showValue(row, column)
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
        setListeners{}
    }

    private fun defeat(){
        labelResult.text = resources.getString(R.string.lose)
        setVisibilityEndItems(View.VISIBLE)
        setListeners{}
    }

    private fun setListeners(function: () -> Unit){
        items.forEach {
            it.forEach {
                it.setListener { function() }
            }
        }
    }

    private fun setVisibilityEndItems(visibility: Int){
        labelResult.visibility = visibility
        btnTryAgain.visibility = visibility
    }

    private fun hiddenOff(){
        setListeners{}

        items.forEach {
            it.forEach {
                val str = if(it.value == Constants.BOMB_VALUE) "*" else it.value.toString()
                it.setLabelText(str, R.color.grayLight)
            }
        }
    }

    private fun hiddenOn(){
        items.forEachIndexed { row, array ->
            array.forEachIndexed{ column, field ->
                field.setListener { showValue(row, column) }
                if(!field.visible) {
                    field.setLabelText("", R.color.gray)
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
            R.id.easy_mode -> {
                mode = EASY
                restartGame()
                true
            }
            R.id.medium_mode -> {
                mode = MEDIUM
                restartGame()
                true
            }
            R.id.hard_mode -> {
                mode = HARD
                restartGame()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
