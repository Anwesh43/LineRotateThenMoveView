package com.anwesh.uiprojects.linerotatethenmoveview

/**
 * Created by anweshmishra on 11/01/20.
 */

import android.content.Context
import android.app.Activity
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.view.MotionEvent

val nodes : Int = 5
val steps : Int = 5
val scGap : Float = 0.02f / 5
val delay : Long = 20
val strokeFactor : Float = 90f
val foreColor : Int = Color.parseColor("#3F51B5")
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawLineRotateThenMove(w : Float, scale : Float, paint : Paint) {
    val gap : Float = w / (steps + 1)
    val sf : Float = scale.sinify()
    val scGapFloor : Float = sf / steps
    val i : Int = Math.floor(sf.toDouble() / scGapFloor).toInt()
    val sfi : Float = sf.divideScale(i, steps)
    save()
    translate(gap * (i + 1), paint.strokeWidth / 2)
    rotate(-180f * sfi)
    drawLine(0f, 0f, -gap / 2, 0f, paint)
    restore()
}

fun Canvas.drawLRTMNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(0f, gap * (i + 1))
    drawLineRotateThenMove(w, scale, paint)
    restore()
}

class LineRotateThenMoveView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
                view.postInvalidate()
            }
        }
    }

    data class LRTMNode(var i : Int, val state : State = State()) {

        private var next : LRTMNode? = null
        private var prev : LRTMNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = LRTMNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawLRTMNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : LRTMNode {
            var curr : LRTMNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LineRotateThenMove(var i : Int) {

        private val root : LRTMNode = LRTMNode(0)
        private var curr : LRTMNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) : LRTMNode {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : LineRotateThenMoveView) {

        private val animator : Animator = Animator(view)
        private val lrtm : LineRotateThenMove = LineRotateThenMove(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            lrtm.draw(canvas, paint)
            animator.animate {
                lrtm.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            lrtm.startUpdating {
                animator.start()
            }
        }
    }
}