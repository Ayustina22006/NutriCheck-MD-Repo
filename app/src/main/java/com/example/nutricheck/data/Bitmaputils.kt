package com.example.nutricheck.data

import android.graphics.Bitmap

object BitmapUtils {
    fun cropToCenter(bitmap: Bitmap): Bitmap {
        val scanBoxSize = 200
        val centerX = bitmap.width / 2
        val centerY = bitmap.height / 2

        val left = (centerX - scanBoxSize / 2).coerceAtLeast(0)
        val top = (centerY - scanBoxSize / 2).coerceAtLeast(0)
        val right = (centerX + scanBoxSize / 2).coerceAtMost(bitmap.width)
        val bottom = (centerY + scanBoxSize / 2).coerceAtMost(bitmap.height)

        return Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)
    }
}
