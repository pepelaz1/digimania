package ru.pepelaz.clickomania

import java.util.*


/**
 * Created by pepel on 31.01.2018.
 */
class Game {

    var state: GameState private set
    var countX: Int private set
    var countY: Int private set
    private val blocks: ArrayList<ArrayList<Block>> = ArrayList()

    var flagsRemained: Int private set
    private var bombsCount: Int = 0
    private var blockRemained: Int = 0


    constructor(countX: Int, countY: Int) {
        this.state = GameState.Continue
        this.countX = countX
        this.countY = countY
        flagsRemained = 0
        for (i in 0..countX - 1) {
            val row: ArrayList<Block> = ArrayList()
            for(j in 0..countY - 1) {
                val block = Block()
                row.add(block)
            }
            blocks.add(row)
        }
        blockRemained = countX * countY

        generateBombs()
    }

    init {

    }

    private fun generateBombs() {
        bombsCount = ((countX * countY) / 100f * 20f).toInt()
        flagsRemained = bombsCount
        val random = Random()
        for(n in 0..bombsCount - 1) {
            while(true){
                val i = random.nextInt(countX)
                val j = random.nextInt(countY)
                if (blocks[i][j].state != BlockState.Bomb) {
                    blocks[i][j].state = BlockState.Bomb
                    blockRemained--
                    break
                }
            }
        }
    }

    fun blockState(i: Int, j: Int) : BlockState {
        return  blocks[i][j].state
    }

    fun hasFlag(i: Int, j: Int): Boolean {
        return blocks[i][j].flag
    }

    fun onTime(time: String) {
        if (time == "60:00")
            state = GameState.Lose
    }

    fun onShortClick(i: Int, j :Int)  {

        if (blocks[i][j].flag) {
            blocks[i][j].flag = false
            flagsRemained++
        } else {

            if (blocks[i][j].state == BlockState.Bomb) {
                blocks[i][j].state = BlockState.BombClicked
                state = GameState.Lose
                return
            }

            calcBombsAround(i, j)

            if (blockRemained == 0)
                state = GameState.Win
        }
    }

    fun onLongClick(i: Int, j :Int) {
        if (blocks[i][j].flag) {
            blocks[i][j].flag = false
            flagsRemained++
        }  else {
            val state = blocks[i][j].state
            blocks[i][j].flag = if (state == BlockState.Unclicked || state == BlockState.Bomb) true else false
            flagsRemained--
        }
    }

    fun calcBombsAround(i: Int, j :Int) {

        if (blocks[i][j].state == BlockState.Unclicked)
          blockRemained--

        //Log.d("test_test","blocks remained: " + blockRemained)
        var cnt = 0
        if ((i - 1 >= 0) && (j - 1 >= 0)) {
            if (blocks[i - 1][j - 1].state == BlockState.Bomb)
                cnt++
        }

        if (i - 1 >= 0 ) {
            if (blocks[i - 1][j].state == BlockState.Bomb)
                cnt++
        }

        if (i - 1 >= 0 && (j + 1 <= countY - 1)) {
            if (blocks[i - 1][j + 1].state == BlockState.Bomb)
                cnt++
        }

        if (j - 1 >= 0) {
            if (blocks[i][j - 1].state == BlockState.Bomb)
                cnt++
        }

        if (j + 1 <= countY - 1) {
            if (blocks[i][j + 1].state == BlockState.Bomb)
                cnt++
        }

        if ((i + 1 <= countX - 1) && (j - 1 >= 0)) {
            if (blocks[i + 1][j - 1].state == BlockState.Bomb)
                cnt++
        }

        if (i + 1 <= countX - 1 ) {
            if (blocks[i + 1][j].state == BlockState.Bomb)
                cnt++
        }

        if ((i + 1 <= countX - 1) && (j + 1 <= countY - 1)) {
            if (blocks[i + 1][j + 1].state == BlockState.Bomb)
                cnt++
        }


        blocks[i][j].state = BlockState.values()[cnt]
        if (cnt == 0)
            exploreFreeSpace(i, j)

    }

    fun exploreFreeSpace(i: Int, j: Int) {
        if ((i - 1 >= 0) && (j - 1 >= 0)) {
            if (blocks[i-1][j-1].state == BlockState.Unclicked && !blocks[i-1][j-1].flag)
                calcBombsAround(i - 1, j - 1)
        }

        if (i - 1 >= 0 ) {
            if (blocks[i-1][j].state == BlockState.Unclicked && !blocks[i-1][j].flag)
                calcBombsAround(i - 1, j)
        }

        if (i - 1 >= 0 && (j + 1 <= countY - 1)) {
            if (blocks[i-1][j+1].state == BlockState.Unclicked && !blocks[i-1][j+1].flag)
                calcBombsAround(i - 1, j + 1)
        }

        if (j - 1 >= 0) {
            if (blocks[i][j-1].state == BlockState.Unclicked && !blocks[i][j-1].flag)
                calcBombsAround(i, j - 1)
        }

        if (j + 1 <= countY - 1) {
            if (blocks[i][j+1].state == BlockState.Unclicked && !blocks[i][j+1].flag)
                calcBombsAround(i, j + 1)
        }

        if ((i + 1 <= countX - 1) && (j - 1 >= 0)) {
            if (blocks[i + 1][j - 1].state == BlockState.Unclicked && !blocks[i + 1][j - 1].flag)
                calcBombsAround(i + 1, j - 1)
        }

        if (i + 1 <= countX - 1 ) {
            if (blocks[i + 1][j].state == BlockState.Unclicked && !blocks[i + 1][j].flag)
                calcBombsAround(i + 1, j )
        }

        if ((i + 1 <= countX - 1) && (j + 1 <= countY - 1)) {
            if (blocks[i + 1][j + 1].state == BlockState.Unclicked && !blocks[i + 1][j + 1].flag)
                calcBombsAround(i + 1, j + 1)
        }
    }

}