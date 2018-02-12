package ru.pepelaz.clickomania

import java.util.*
import kotlin.math.E


/**
 * Created by pepel on 31.01.2018.
 */
class Game {

    var state: GameState private set
    var countX: Int private set
    var countY: Int private set
    private val bricks: ArrayList<ArrayList<Brick>> = ArrayList()

    val Empty = 0
    val Explode = -1

    constructor(countX: Int, countY: Int) {
        this.state = GameState.Continue
        this.countX = countX
        this.countY = countY

        for (i in 0..countX - 1) {
            val row: ArrayList<Brick> = ArrayList()
            for(j in 0..countY - 1) {
                val brick = Brick()
                brick.number = Random().nextInt(5) + 1
                row.add(brick)
            }
            bricks.add(row)
        }
    }

    init {

    }


    fun brickNumber(i: Int, j: Int): Int {
        return bricks[i][j].number
    }

    fun makeEmpty(i: Int, j: Int) {
        bricks[i][j].number = 0
    }


    fun onShortClick(i: Int, j :Int)  {
        val value = bricks[i][j].number
        bricks[i][j].number = Explode
        exploreBricks(i,j, value, Explode)
    }

    fun onExplodeEnd(i: Int, j: Int) {
        bricks[i][j].number = Empty
        exploreBricks(i,j, Explode, Empty)
    }

    fun exploreBricks(i: Int, j: Int, src: Int, dst: Int) {

        if (i - 1 >= 0) {
            if (bricks[i - 1][j].number == src) {
                bricks[i - 1][j].number = dst
                exploreBricks(i - 1, j, src, dst)
            }
        }

        if (i + 1 <= countX - 1) {
            if (bricks[i + 1][j].number == src) {
                bricks[i + 1][j].number = dst
                exploreBricks(i + 1, j, src, dst)
            }
        }

        if (j - 1 >= 0) {
            if (bricks[i][j - 1].number == src) {
                bricks[i][j - 1].number = dst
                exploreBricks(i, j - 1, src, dst)
            }
        }

        if (j + 1 <= countY - 1) {
            if (bricks[i][j + 1].number == src) {
                bricks[i][j + 1].number = dst
                exploreBricks(i, j + 1, src, dst)
            }
        }
    }


}