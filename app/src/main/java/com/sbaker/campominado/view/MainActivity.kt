package com.sbaker.campominado.view

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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
    var bombsPositioned = false

    lateinit var items: Array<Array<Field>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTryAgain.setOnClickListener {
            restartGame()
        }

        startGame()
    }

    private fun startGame() {
        setVisibilityItemsEndGame(View.GONE)

        // Estrutura de dados que permite definir valores para os atributos do objeto repetir o nome da variável
        board.apply {
            rowCount = rows
            columnCount = columns
        }

        // Com o DisplayMetrics, consigo pegar o tamanho total da tela do celular em pixels
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        /**
         * Tamanho total do board = tamanho da tela - (soma das margens * densidade)
         * obs: necessário multiplicar pela densidade pois o tamanho total da tela é em pixels
         */
        val boardWidth = displayMetrics.widthPixels - (Constants.MARGIN_GRID * displayMetrics.density)

        // Inicializando a matriz e colocando um campo padrão para cada elemento
        items = Array(rows){ Array(columns, { Field(this, columns, boardWidth) })}

        // Expressão Lambda -> ótima opção para melhora de performance
        items.forEachIndexed{ row, array ->
            array.forEachIndexed{ column, field ->
                board.addView(field.label)

                // Somente no primeiro clique as bombas serão espalhadas e os campos seguros serão calculados
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
        bombsPositioned = false
        startGame()
    }

    private fun positionBombs(clickedRow: Int, clickedColumn: Int){
        var bombs = qtdBombs

        while (bombs > 0){
            val randomRow = (0..rows-1).random()
            val randomColumn = (0..columns-1).random()
            val field = items[randomRow][randomColumn]

            // SE o valor do campo não for uma bomba E não for o campo clicado ENTÃO adiciona a bomba
            if(!field.isBomb() && (randomRow != clickedRow || randomColumn != clickedColumn)){
                field.value = Constants.BOMB_VALUE
                bombs--
            }
        }

        bombsPositioned = true
    }

    private fun configSafeFields(){
        items.forEachIndexed{ row, array ->
            array.forEachIndexed{ column, field ->
                field.setListener { showValue(row, column) }
                if(!field.isBomb()) {
                    evaluateField(row, column)
                }
            }
        }
    }

    private fun evaluateField(fieldRow: Int, fieldColumn: Int){
        var count = 0

        for(row in fieldRow - 1 .. fieldRow + 1){
            for (column in fieldColumn - 1 .. fieldColumn + 1){
                /**
                 * SE a linha e coluna estão dentro do tamanho do tabuleiro
                 * E o item é um bomba
                 * ENTÃO incrementa o count
                 */
                if( isInsideBoard(row, column) && (items[row][column].isBomb())) {
                    count++
                }
            }
        }

        items[fieldRow][fieldColumn].value = count
    }

    private fun showValue(clickedRow: Int, clickedColumn: Int){
        val field = items[clickedRow][clickedColumn]

        // Conceito de early return
        if(field.clicked){
            return
        }

        if(field.isBomb()){
            showAllBombs()
            // A bomba clicada terá uma cor vermelha
            field.setLabelText("*", R.color.redLight)
            endGame(false)
            return
        }

        field.setLabelText(field.value.toString(), R.color.grayLight)
        qtdSafeFields--
        field.clicked = true
        field.visible = true

        if(field.isBlank()){
            for(row in clickedRow - 1 .. clickedRow + 1){
                for (column in clickedColumn - 1 .. clickedColumn + 1){
                    /**
                     * SE a linha e coluna estão dentro do tabuleiro
                     * E o item nunca foi clicado
                     */
                    if( isInsideBoard(row, column) && (!items[row][column].clicked) ) {
                        // Recursividade para chamar todos os campos em branco
                        showValue(row, column)
                    }
                }
            }
        }

        if(qtdSafeFields == 0){
            endGame(true)
        }
    }

    private fun isInsideBoard(row: Int, column: Int) = (row >= 0 && column >= 0 && row < rows && column < columns)

    private fun showAllBombs() {
        items.forEach {
            // Filter para alterar somente os campos que são bombas
            it.filter { it.isBomb() }.forEach {
                it.setLabelText("*", R.color.grayLight)
                it.visible = true
            }
        }
    }

    private fun endGame(win: Boolean){
        labelResult.text = if (win) resources.getString(R.string.win) else resources.getString(R.string.lose)
        setVisibilityItemsEndGame(View.VISIBLE)
        cleanListeners()
    }

    private fun cleanListeners(){
        items.forEach {
            it.forEach {
                it.setListener {}
            }
        }
    }

    private fun setVisibilityItemsEndGame(visibility: Int){
        labelResult.visibility = visibility
        btnTryAgain.visibility = visibility
    }

    private fun hiddenOff(){
        gameHidden = false

        cleanListeners()

        items.forEach {
            it.forEach {
                val str = if(it.isBomb()) "*" else it.value.toString()
                it.setLabelText(str, R.color.grayLight)
            }
        }
    }

    private fun hiddenOn(){
        gameHidden = true

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
                if(bombsPositioned) {
                    if (gameHidden) {
                        item.icon = resources.getDrawable(R.drawable.ic_visibility_off)
                        hiddenOff()
                    } else {
                        item.icon = resources.getDrawable(R.drawable.ic_visibility)
                        hiddenOn()
                    }
                } else {
                    Toast.makeText(this, resources.getString(R.string.visible_denied), Toast.LENGTH_LONG).show()
                }
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
