package ru.pepelaz.clickomania

/**
 * Created by pepel on 04.02.2018.
 */
enum class BlockState(val state: Int) {

    Clicked(0),
    One(1),
    Two(2),
    Three(3),
    Four(4),
    Five(5),
    Six(6),
    Seven(7),
    Eight(8),
    Unclicked(10),
    Bomb(11),
    BombClicked(12)
}