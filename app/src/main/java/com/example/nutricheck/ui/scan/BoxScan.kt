package com.example.nutricheck.ui.scan

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class BoxScan @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = 0xFFFFFFFF.toInt() // Warna putih
        strokeWidth = 8f // Ketebalan garis
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Ukuran sudut
        val cornerLength = 100f

        // Koordinat batas
        val left = 50f
        val top = 50f
        val right = width - 50f
        val bottom = height - 50f

        // Gambar sudut-sudut
        // Kiri atas
        canvas.drawLine(left, top, left + cornerLength, top, paint)
        canvas.drawLine(left, top, left, top + cornerLength, paint)

        // Kanan atas
        canvas.drawLine(right, top, right - cornerLength, top, paint)
        canvas.drawLine(right, top, right, top + cornerLength, paint)

        // Kiri bawah
        canvas.drawLine(left, bottom, left + cornerLength, bottom, paint)
        canvas.drawLine(left, bottom, left, bottom - cornerLength, paint)

        // Kanan bawah
        canvas.drawLine(right, bottom, right - cornerLength, bottom, paint)
        canvas.drawLine(right, bottom, right, bottom - cornerLength, paint)
    }
}
