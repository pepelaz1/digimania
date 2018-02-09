package ru.pepelaz.clickomania

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import java.lang.System.currentTimeMillis
import android.content.Context.VIBRATOR_SERVICE
import android.os.Vibrator
import android.support.v4.content.ContextCompat
import java.util.*
import kotlin.concurrent.schedule


/**
 * Created by pepel on 25.01.2018.
 */
class GameView(context: Context) : View(context) {

    val paint: Paint
    val bmpEmpty: Bitmap
    val bmpNumbers = ArrayList<Bitmap>()
    val longClickDuration = 300
    var dx: Float = 0f
    var dy: Float  = 0f
    var blockWidth = 0
    var blockHeight = 0
    var lastTime: Long = 0
    var game: Game? = null
    var vibrateTimer: Timer? = null

    init {

        paint = Paint()
        paint.isFilterBitmap = true
        paint.isAntiAlias = true
        paint.color = Color.RED

        bmpEmpty = BitmapFactory.decodeResource(resources, R.drawable.empty)
        for (n in 1..5) {
            val resId = resources.getIdentifier("n" + n.toString(), "drawable", context.packageName)
            bmpNumbers.add(BitmapFactory.decodeResource(resources, resId))
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.drawColor(ContextCompat.getColor(context, R.color.background))


        if (game == null) {
            initGame(canvas)
         }

        drawBricks(canvas)
    }


    fun initGame(canvas: Canvas?) {

        val canvasWidth = canvas?.width?:0;
        val canvasHeight = canvas?.height?:0;

        val anchorSide = if (canvasWidth < canvasHeight) canvasWidth else canvasHeight
        blockWidth = anchorSide / 10
        blockHeight = blockWidth


        val cntX = canvasWidth / blockWidth
        val cntY = canvasHeight / blockHeight
        game = Game(cntX, cntY)

        val actualWidth = cntX * blockWidth
        val actualHeight = cntY * blockHeight

        dx = canvasWidth.toFloat() / actualWidth
        dy = canvasHeight.toFloat() / actualHeight
    }



    fun drawBricks(canvas: Canvas?) {
        val game = game?:return

        val srcRect = Rect(0, 0, bmpEmpty.width, bmpEmpty.height)
        for (i in 0..game.countX - 1) {
            for(j in 0..game.countY - 1) {

                val x1 = (i * blockWidth * dx).toInt()
                val y1 = (j * blockHeight * dy).toInt()
                val x2 = ((i+1) * blockWidth * dx).toInt()
                val y2 = ((j+1) *  blockHeight * dy).toInt()

                val dstRect = Rect(x1, y1, x2, y2)
                canvas?.drawBitmap(bmpNumbers[game.brickNumber(i, j)], srcRect, dstRect, paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val gameSafe = game ?: return true


        val i: Int = ((event?.x ?: 0f) / (blockWidth * dx)).toInt()
        val j: Int = ((event?.y ?: 0f) / (blockHeight * dy)).toInt()


        when (event?.action) {

            MotionEvent.ACTION_DOWN -> {
                lastTime = currentTimeMillis()




                vibrateTimer = Timer("vibrator", true)
                vibrateTimer!!.schedule(300) {
                    val v: Vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
                    v.vibrate(100)
                }

            }
            MotionEvent.ACTION_UP -> {
                if (vibrateTimer != null) {
                    vibrateTimer!!.cancel()
                    vibrateTimer = null
                }


                if (gameSafe.state == GameState.Continue) {
                    if (currentTimeMillis() - lastTime < longClickDuration) {
                        gameSafe.onShortClick(i, j)
                        when (gameSafe.state) {
                            GameState.Win -> {

                            }
                            GameState.Lose -> {

                            }
                            else -> {  }
                        }
                    } else {

                    }
                    invalidate()
                }
            }
        }
        return true
    }

}