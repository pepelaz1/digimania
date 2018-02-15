package ru.pepelaz.clickomania

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import java.lang.System.currentTimeMillis
import android.content.Context.VIBRATOR_SERVICE
import android.os.Vibrator
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import java.util.*
import kotlin.concurrent.schedule


/**
 * Created by pepel on 25.01.2018.
 */
class GameView(context: Context) : View(context) {

    val paint: Paint
    val bmpEmpty: Bitmap
    val bmpNumbers = ArrayList<Bitmap>()
    var bmpTransform: Bitmap? = null
    var dx: Float = 0f
    var dy: Float  = 0f
    var blockWidth = 0
    var blockHeight = 0
    var game: Game? = null
    var backColor = ContextCompat.getColor(context, R.color.background)
    var aback: Int
    var rback: Int
    var gback: Int
    var bback: Int
    var avail = true
    var fallDy: Float = 0f
    var shiftDx: Float = 0f

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

        bback = backColor and 0xff
        gback = (backColor shr 8) and 0xff
        rback = (backColor shr 16) and 0xff
        aback = (backColor shr 24) and 0xff
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.drawColor(backColor)


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
                val num = game.brickNumber(i, j)

                if (game.brickIsShifting(i,j)) {
                    dstRect.left -= shiftDx.toInt()
                    dstRect.right -= shiftDx.toInt()
                    canvas?.drawBitmap(bmpNumbers[num - 1], srcRect, dstRect, paint)
                }
                else if (game.brickIsFalling(i,j)) {
                    dstRect.top += fallDy.toInt()
                    dstRect.bottom += fallDy.toInt()
                    canvas?.drawBitmap(bmpNumbers[num - 1], srcRect, dstRect, paint)
                } else {
                    if (num == game.Empty  ) {
                        if (j < 1 || !game.brickIsFalling(i,j - 1))
                             canvas?.drawBitmap(bmpEmpty, srcRect, dstRect, paint)
                    }
                    else {
                        if (num == game.Explode) {
                            canvas?.drawBitmap(bmpTransform, srcRect, dstRect, paint)
                        } else
                            canvas?.drawBitmap(bmpNumbers[num - 1], srcRect, dstRect, paint)
                    }
                }
            }
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val gameSafe = game ?: return true

        val i: Int = ((event?.x ?: 0f) / (blockWidth * dx)).toInt()
        val j: Int = ((event?.y ?: 0f) / (blockHeight * dy)).toInt()

        when (event?.action) {

            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_UP -> {


                if (gameSafe.state == GameState.Continue) {

                    if (avail && gameSafe.brickNumber(i,j) != gameSafe.Empty) {
                        avail = false
                        bmpTransform = bmpNumbers[gameSafe.brickNumber(i, j) - 1].copy(Bitmap.Config.ARGB_8888, true)

                        gameSafe.onShortClick(i, j)
                        if (gameSafe.brickNumber(i,j) == gameSafe.Explode) {
                            explode(i, j)
                        } else {
                            avail = true
                        }
                    }
                    invalidate()
                }
            }
        }
        return true
    }

    fun explode(i:Int, j:Int) {
        var counter = 0
        val total = 5
        Timer("explode", true).schedule(50,50) {

            fadeOut(counter, total)

            if(counter == total) {
                cancel()
                game!!.onExplodeEnd(i, j)
                postInvalidate()
                //Log.d("test_test","cancel explode timer")
                fall()
            }

            counter++
            postInvalidate()
        }
    }

    fun fadeOut(curr: Int, total: Int)  {
        if (bmpTransform == null) return

        val width = bmpTransform!!.width
        val height = bmpTransform!!.height
        var pixels = IntArray( width * height)
        bmpTransform!!.getPixels(pixels,0, width, 0,0, width, height )

        var a: Int
        var r: Int
        var g: Int
        var b: Int


        var kback: Float = curr.toFloat() / total.toFloat()
        var k = (total - curr).toFloat() / total.toFloat()
        var clr: Int
        for(i in 0..pixels.size-1) {

            clr = pixels[i]
            if (clr != backColor ) {

                b = (kback * bback + k * (clr and 0xff)).toInt()
                g = (kback * gback + k * ((clr ushr 8) and 0xff)).toInt()
                r = (kback * rback + k * ((clr ushr 16) and 0xff)).toInt()
                a = (kback * aback + k * ((clr ushr 24) and 0xff)).toInt()

                pixels[i] = (a and 0xff shl 24) or (r and 0xff shl 16) or (g and 0xff shl 8) or (b and 0xff)
            }
        }

        bmpTransform!!.setPixels(pixels,0, width, 0,0, width, height )
    }

    fun fall() {
        // Falling bricks after explosion
        game!!.markFalling()
        fallDy = 0f
        val total = 5
        var counter = 0
        val k = blockHeight * dy / total.toFloat()
        Timer("fall", true).schedule(30,30) {

            fallDy += k
            if(counter == total - 1) {
                game!!.fallRowDown()
                if (!game!!.markFalling()) {
                    cancel()
                    //Log.d("test_test","cancel fall timer")
                    shift()
                }
                counter = 0
                fallDy = 0f
            }
            else
                counter++
            postInvalidate()

        }
    }

    fun shift() {
        // Shift bricks after falling
        if (!game!!.markShifting()){
            game!!.checkIfFinished()
            if (game!!.state == GameState.Finish) {
                Log.d("test_test", "Finish")
            }
            avail = true
            return
        }

        shiftDx = 0f
        val total = 5
        var counter = 0
        val k = blockWidth * dx / total.toFloat()
        Timer("shift", true).schedule(30,30) {
            //Log.d("test_test", "counter:" + counter + ", total:" + total)
            shiftDx += k
            if(counter == total - 1) {
                game!!.shiftColLeft()
                if (!game!!.markShifting() ) {
                    cancel()
                    avail = true

                    game!!.checkIfFinished()
                    if (game!!.state == GameState.Finish)
                       Log.d("test_test","Finish")
                   // Log.d("test_test","cancel shift timer")
                }
                counter = 0
                shiftDx = 0f
            }
            else
                counter++
            postInvalidate()

        }
    }

}