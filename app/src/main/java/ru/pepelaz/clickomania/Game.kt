package ru.pepelaz.clickomania

import java.util.*


/**
 * Created by pepel on 31.01.2018.
 */
class Game {

    var state: GameState private set
    var countX: Int private set
    var countY: Int private set
    private val bricks: ArrayList<ArrayList<Brick>> = ArrayList()



    constructor(countX: Int, countY: Int) {
        this.state = GameState.Continue
        this.countX = countX
        this.countY = countY

        for (i in 0..countX - 1) {
            val row: ArrayList<Brick> = ArrayList()
            for(j in 0..countY - 1) {
                val brick = Brick()
                brick.number = Random().nextInt(5)
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



    fun onShortClick(i: Int, j :Int)  {


    }

}