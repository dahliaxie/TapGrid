package com.example.numbergrid

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.lang.Integer.min

class NumberGridView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val numCols = 9
    private val numRows = 9
    private val borderWidth = 5f
    private var cellSize = 0f
    private var numberSize = 0f
    private var numberPaint: Paint
    private var borderPaint: Paint
    private val numbers = Array(numRows) { IntArray(numCols) }

    init {
        // Initialize the paints
        numberPaint = Paint().apply {
            textSize = 1f
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL_AND_STROKE
            color = Color.BLACK
        }
        borderPaint = Paint().apply {
            strokeWidth = borderWidth
            style = Paint.Style.STROKE
            color = Color.BLACK
        }
        setOnTouchListener(CellTouchListener())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val viewSize = min(measuredWidth, measuredHeight)
        cellSize = (viewSize - (borderWidth * 2)) / numCols
        numberSize = cellSize * 0.8f
        setMeasuredDimension(viewSize, viewSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the border
        val borderRect = RectF(borderWidth / 2, borderWidth / 2, width.toFloat() - borderWidth / 2, height.toFloat() - borderWidth / 2)

        canvas.drawRect(borderRect, borderPaint)

        // Draw the cells
        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                val cellRect = RectF(col * cellSize + borderWidth, row * cellSize + borderWidth, (col + 1) * cellSize + borderWidth, (row + 1) * cellSize + borderWidth)
                canvas.drawRect(cellRect, borderPaint)

                // Draw the number
                val number = numbers[row][col].toString()
                numberPaint.textSize = calculateTextSize(number, numberSize, cellSize)
                canvas.drawText(number, cellRect.centerX(), cellRect.centerY() - (numberPaint.descent() + numberPaint.ascent()) / 2, numberPaint)
            }
        }
    }

    private fun calculateTextSize(text: String, targetTextSize: Float, targetWidth: Float): Float {
        var low = 0f
        var high = targetTextSize
        var mid = (low + high) / 2f
        val bounds = Rect()
        while (low <= high) {
            numberPaint.textSize = mid
            numberPaint.getTextBounds(text, 0, text.length, bounds)
            if (bounds.width() >= targetWidth || bounds.height() >= targetWidth) {
                high = mid - 1f
            } else {
                low = mid + 1f
            }
            mid = (low + high) / 2f
        }
        return mid
    }
    private inner class CellTouchListener : OnTouchListener {
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val col = ((event.x - borderWidth) / cellSize).toInt()
                    val row = ((event.y - borderWidth) / cellSize).toInt()

                    if (col in 0 until numCols && row in 0 until numRows) {
                        // incrememnt cell, use mod 10 so no numbers exceed 9
                        numbers[row][col] = (numbers[row][col] + 1) % 10

                        val cellRect = RectF(
                            col * cellSize + borderWidth,
                            row * cellSize + borderWidth,
                            (col + 1) * cellSize + borderWidth,
                            (row + 1) * cellSize + borderWidth
                        )
                        view.invalidate(
                            cellRect.left.toInt(),
                            cellRect.top.toInt(),
                            cellRect.right.toInt(),
                            cellRect.bottom.toInt()
                        )
                    }
                }
            }
            return true
        }
    }

}

