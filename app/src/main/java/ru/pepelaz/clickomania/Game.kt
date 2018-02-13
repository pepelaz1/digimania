package ru.pepelaz.clickomania

import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.E


/**
 * Created by pepel on 31.01.2018.
 */
class Game {

    var state: GameState private set
    var countX: Int private set
    var countY: Int private set
    private val bricks: ArrayList<ArrayList<Brick>> = ArrayList()
    private var fallingCols: ArrayList<Int> = ArrayList()

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


    fun brickIsFalling(i: Int, j: Int): Boolean {
        return bricks[i][j].falling
    }



    fun onShortClick(i: Int, j :Int)  {
        val value = bricks[i][j].number
        if (exploreBricks(i,j, value, Explode))
            bricks[i][j].number = Explode
    }

    fun onExplodeEnd(i: Int, j: Int) {
        bricks[i][j].number = Empty
        exploreBricks(i,j, Explode, Empty)
    }

    fun exploreBricks(i: Int, j: Int, src: Int, dst: Int): Boolean {

        var found = false
        if (i - 1 >= 0) {
            if (bricks[i - 1][j].number == src) {
                found = true
                bricks[i - 1][j].number = dst
                exploreBricks(i - 1, j, src, dst)
            }
        }

        if (i + 1 <= countX - 1) {
            if (bricks[i + 1][j].number == src) {
                found = true
                bricks[i + 1][j].number = dst
                exploreBricks(i + 1, j, src, dst)
            }
        }

        if (j - 1 >= 0) {
            if (bricks[i][j - 1].number == src) {
                found = true
                bricks[i][j - 1].number = dst
                exploreBricks(i, j - 1, src, dst)
            }
        }

        if (j + 1 <= countY - 1) {
            if (bricks[i][j + 1].number == src) {
                found = true
                bricks[i][j + 1].number = dst
                exploreBricks(i, j + 1, src, dst)
            }
        }
        return found
    }

    fun markFalling(): Boolean {
        // Find colums to fall
        fallingCols = findFallingCols()
        for (i in 0..fallingCols.size-1) {
            for(j in countY-2 downTo 0) {

                if ((bricks[fallingCols[i]][j+1].number == Empty || bricks[fallingCols[i]][j+1].falling)
                        && bricks[fallingCols[i]][j].number != Empty) {
                    bricks[fallingCols[i]][j].falling = true
                }
            }
        }
        return fallingCols.size > 0
    }

    fun findFallingCols() : ArrayList<Int> {
        val res = ArrayList<Int>()
        for (i in 0..countX - 1) {
            for (j in 1..countY - 1) {
                if (bricks[i][j].number == Empty && bricks[i][j-1].number != Empty) {
                    res.add(i)
                    break
                }
            }
        }
        return res
    }

    fun fallRowDown() {
        for (i in 0..fallingCols.size-1) {
            for(j in countY - 1 downTo 1) {
                if (bricks[fallingCols[i]][j-1].falling){
                    bricks[fallingCols[i]][j].number = bricks[fallingCols[i]][j-1].number
                    bricks[fallingCols[i]][j-1].falling = false
                    bricks[fallingCols[i]][j-1].number = Empty
                }
            }
        }
    }

}