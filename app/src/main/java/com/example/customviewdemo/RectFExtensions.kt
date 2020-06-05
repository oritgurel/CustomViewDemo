package com.example.customviewdemo

import android.graphics.RectF
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun RectF.containedInRounded(touchX: Float, touchY: Float, cornerRadius: Float): Boolean {
    val topLeftCornerRect = RectF(left, top, left + cornerRadius, top + cornerRadius).inCircle(touchX, touchY, cornerRadius)
    val bottomLeftCornerRect = RectF(left, bottom - cornerRadius, left + cornerRadius, bottom).inCircle(touchX, touchY, cornerRadius)
    val rightTopCornerRect = RectF(right - cornerRadius, top, right, top + cornerRadius).inCircle(touchX, touchY, cornerRadius)
    val rightBottomCornerRect = RectF(right - cornerRadius, bottom - cornerRadius, right, bottom).inCircle(touchX, touchY, cornerRadius)

//    val cornerList = listOf<RectF>(topLeftCornerRect, bottomLeftCornerRect, rightTopCornerRect, rightBottomCornerRect)

    val leftSubRect = RectF(left, top + cornerRadius, left + cornerRadius, bottom - cornerRadius).contains(touchX, touchY)
    val rightSubRect = RectF(right - cornerRadius, top + cornerRadius, right, bottom - cornerRadius).contains(touchX, touchY)



    //1. check if inside inner rect (trim left and right squares) &&
    //2. check if inside corner circles
    //3. check if inside side rects

    val trimmedRect = RectF(left + cornerRadius, top, right - cornerRadius, bottom)
    val containedInTrimmedRect = trimmedRect.contains(touchX, touchY)

//    var containedInCorners = false
//    cornerList.forEach {
//        if ((touchX - it.centerX()).pow(2) + (touchY - it.centerX()).pow(2) < cornerRadius.pow(2)) containedInCorners = true
//    }

    return topLeftCornerRect || bottomLeftCornerRect || rightTopCornerRect || rightBottomCornerRect
//    containedInTrimmedRect || leftSubRect || rightSubRect ||
}

fun RectF.inCircle(touchX: Float, touchY: Float, radius: Float): Boolean {
    return sqrt(((touchX - this.centerX()).pow(2)) + ((touchY - this.centerY()).pow(2))) < radius
}

fun RectF.roundedArea(cornerRadius: Int): Double {
    //rounded area
    val fullArea = (right - left).times(bottom - top)
    val cornerSquareArea = cornerRadius * cornerRadius
    val areaMinusCornerSquares = fullArea - 4 * cornerSquareArea
    return areaMinusCornerSquares + kotlin.math.PI * cornerSquareArea
}