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
                val num = game.brickNumber(i, j)

                if (num == 0)
                    canvas?.drawBitmap(bmpEmpty, srcRect, dstRect, paint)
                else {

                    if (num == game.Explode) {
                        canvas?.drawBitmap(bmpTransform, srcRect, dstRect, paint)
                    } else
                        canvas?.drawBitmap(bmpNumbers[num - 1], srcRect, dstRect, paint)
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

                        bmpTransform = bmpNumbers[gameSafe.brickNumber(i,j)-1].copy(Bitmap.Config.ARGB_8888 , true)

                        gameSafe.onShortClick(i, j)
                        explode(i,j)
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

    fun explode(i:Int, j:Int) {

        var counter = 0
        Timer("explode", true).schedule(50,50) {

            fadeOut()

            if(counter == 5) {
                cancel()
                if (game != null)
                    game!!.onExplodeEnd(i,j)
            }

            counter++
            postInvalidate()
        }
    }

    fun fadeOut()  {
        if (bmpTransform == null) return

        val width = bmpTransform!!.width
        val height = bmpTransform!!.height
        var pixels = IntArray( width * height)
        bmpTransform!!.getPixels(pixels,0, width, 0,0, width, height )

        var a: Int
        var r: Int
        var g: Int
        var b: Int

        val d = (255f / 5).toInt()
        for(i in 0..pixels.size-1) {

            b = pixels[i] and 0xff
            g = (pixels[i] shr 8) and 0xff
            r = (pixels[i] shr 16) and 0xff
            a = (pixels[i] shr 24) and 0xff


            a -= d
            if (a < 0)
                a = 0

            pixels[i] = a and 0xff shl 24 or (r and 0xff shl 16) or (g and 0xff shl 8) or (b and 0xff)
        }

        bmpTransform!!.setPixels(pixels,0, width, 0,0, width, height )
    }

}