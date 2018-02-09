package ru.pepelaz.clickomania

/**
 * Created by pepel on 06.02.2018.
 */

enum class SmileyState(val state: Int) {
    Unclicked(0),
    Clicked(1),
    Win(2),
    Lose(3)
}