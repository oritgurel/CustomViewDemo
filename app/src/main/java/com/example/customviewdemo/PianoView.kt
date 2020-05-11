package com.example.customviewdemo

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.customviewdemo.R.dimen.black_key_height

class PianoView : View {

    private var whiteKeysColor = Color.WHITE
    private var blackKeysColor = Color.BLACK

    private lateinit var whiteKeys: Array<Key>
    private lateinit var blackKeys: Array<Key>

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttr(attrs)
        initKeys()
        initKeyNames()
    }


    var listener: IKeyboardListener? = null

    private val whiteKeyStrokePaint: Paint by lazy {
        Paint().apply {
            strokeWidth = 2f
            style = Paint.Style.STROKE

            color = ContextCompat.getColor(context, R.color.black)
        }
    }

    private val blackKeyPaint: Paint by lazy {
        Paint().apply {
            strokeWidth = 2f
            style = Paint.Style.FILL_AND_STROKE
            color = blackKeysColor
        }
    }

    var whiteKeyWidth = resources.getDimension(R.dimen.white_key_width)
    var whiteKeyHeight = resources.getDimension(R.dimen.white_key_height)
    val whiteKeyWidthHeightRatio = whiteKeyHeight.div(whiteKeyWidth)

    var blackKeyWidth = resources.getDimension(R.dimen.black_key_width)
    var blackKeyHeight = resources.getDimension(black_key_height)
    val blackKeysWidthHeightRatio = blackKeyHeight.div(blackKeyWidth)

    val blackKeyToWhiteKeyWidthRatio = blackKeyWidth.div(whiteKeyWidth)

    var keyRadius = 5f
    var keyRadiusToKeyWidthRatio = keyRadius.div(whiteKeyWidth)

    private val numOfWhiteKeys = 14


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val givenWidth =
            MeasureSpec.getSize(widthMeasureSpec) //this gives us the width assigned to this view.

        //we can adjust the key sizes to fit our given view width
        whiteKeyWidth = givenWidth.div(numOfWhiteKeys).toFloat()
        whiteKeyHeight = whiteKeyWidth.times(whiteKeyWidthHeightRatio)

        blackKeyWidth = whiteKeyWidth.times(blackKeyToWhiteKeyWidthRatio)
        blackKeyHeight = blackKeyWidth.times(blackKeysWidthHeightRatio)

        keyRadius = whiteKeyWidth.times(keyRadiusToKeyWidthRatio)

        //now calculate the desired result:
        val desiredWidth = whiteKeyWidth.times(numOfWhiteKeys)
        val desireHeight = whiteKeyHeight

        //must call
        setMeasuredDimension(desiredWidth.toInt(), desireHeight.toInt())

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawWhiteKeys(canvas)
        drawBlackKeys(canvas)
    }

    private fun drawWhiteKeys(canvas: Canvas?) {
        var left = 0f
        for (i in 0 until numOfWhiteKeys) {

            canvas?.drawRoundRect(
                left,
                0f,
                left + whiteKeyWidth,
                whiteKeyHeight,
                keyRadius,
                keyRadius,
                whiteKeys[i].paint
            )

            val rect = whiteKeys[i].rect.let {
                it.left = left; it.top = 0f; it.right = left + whiteKeyWidth; it.bottom =
                whiteKeyHeight; it
            }
            canvas?.drawRoundRect(rect, keyRadius, keyRadius, whiteKeyStrokePaint)

            left += whiteKeyWidth - whiteKeyStrokePaint.strokeWidth.div(2)
        }
    }

    private fun drawBlackKeys(canvas: Canvas?) {
        var left = whiteKeyWidth.div(2) + blackKeyPaint.strokeWidth
        for (i in 0 until numOfWhiteKeys) {
            val rect = blackKeys[i].rect.let {
                it.left = left; it.top = 0f; it.right = left + blackKeyWidth; it.bottom =
                blackKeyHeight; it
            }
            if (i == 2 || i == 6 || i == 9 || i == 13) {
                left += whiteKeyWidth
                continue
            }
            canvas?.drawRoundRect(rect, keyRadius, keyRadius, blackKeys[i].paint)
            left += whiteKeyWidth - blackKeyPaint.strokeWidth.div(2)
        }
    }

    private fun pressKey(key: Key) {
        key.paint.color = if (key.isBlack) Color.DKGRAY else Color.GRAY
        invalidate()
        listener?.keyOn(key)
    }

    private fun releaseKey(key: Key) {
        key.paint.color = if (key.isBlack) blackKeysColor else whiteKeysColor
        invalidate()
        listener?.keyOff(key)
    }

    private var prevKey: Key? = null

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return super.onTouchEvent(event)
        val key = findKeyForTouch(event.x, event.y) ?: return true
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                prevKey?.let { releaseKey(it) }
                prevKey = key
                pressKey(key)
            }
            MotionEvent.ACTION_UP -> releaseKey(key)
        }
        return true
    }

    private fun findKeyForTouch(x: Float, y: Float): Key? {
        //single touch
        blackKeys.forEach {
            if (it.rect.contains(x, y)) return it
        }
        whiteKeys.forEach {
            if (it.rect.contains(x, y)) return it
        }
        return null
    }

    private fun initKeys() {
        whiteKeys = Array(numOfWhiteKeys) {
            Key(
                RectF(),
                Paint().apply { style = Paint.Style.FILL; color = whiteKeysColor },
                false
            )
        }
        blackKeys = Array(numOfWhiteKeys) {
            Key(
                RectF(),
                Paint().apply {
                    strokeWidth = 2f; style = Paint.Style.FILL_AND_STROKE; color =
                    blackKeysColor
                },
                true
            )
        }
    }

    private fun initKeyNames() {
        //add key names
        var i = 0
        var j = 1
        var k = 0
        val s = "CDEFGAB"
        while (j < 3) {
            while (i < s.length) {
                blackKeys[k].name = "${s[i]}#$j"
                whiteKeys[k++].name = "${s[i++]}$j"
            }
            j++
            i = 0
        }
    }

    private fun initAttr(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.PianoView, 0, 0).apply {
            try {
                whiteKeysColor = getColor(R.styleable.PianoView_whiteKeysColor, whiteKeysColor)
                blackKeysColor = getColor(R.styleable.PianoView_blackKeysColor, blackKeysColor)
            } finally {
                recycle() //TypedArray is a shared resource and must be recycled, it is for one time use.
            }
        }
    }

    data class Key(
        val rect: RectF,
        val paint: Paint,
        val isBlack: Boolean,
        var name: String? = null
    )

    interface IKeyboardListener {
        fun keyOn(key: Key)
        fun keyOff(key: Key)
    }
}