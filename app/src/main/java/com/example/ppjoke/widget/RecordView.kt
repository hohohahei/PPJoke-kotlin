package com.example.ppjoke.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.blankj.utilcode.util.ConvertUtils
import com.example.ppjoke.R

class RecordView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    View(context, attrs, defStyleAttr, defStyleRes), View.OnLongClickListener,
    View.OnClickListener {
    private val fillPaint: Paint
    private val progressPaint: Paint
    private var progressMaxValue = 0
    private val radius: Int
    private val progressWidth: Int
    private val progressColor: Int
    private val fillColor: Int
    private val maxDuration: Int
    private var progressValue = 0
    private var isRecording = false
    private var startRecordTime: Long = 0
    private var mListener: onRecordListener? = null

    private fun finishRecord() {
            mListener?.onFinish()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height
        if (isRecording) {
            canvas.drawCircle(
                (width / 2).toFloat(),
                (height / 2).toFloat(),
                (width / 2).toFloat(),
                fillPaint
            )
            val left = progressWidth / 2
            val top = progressWidth / 2
            val right = width - progressWidth / 2
            val bottom = height - progressWidth / 2
            val sweepAngle = progressValue * 1.0f / progressMaxValue * 360
            canvas.drawArc(
                left.toFloat(),
                top.toFloat(),
                right.toFloat(),
                bottom.toFloat(),
                -90f,
                sweepAngle,
                false,
                progressPaint
            )
        } else {
            canvas.drawCircle(
                (width / 2).toFloat(),
                (height / 2).toFloat(),
                radius.toFloat(),
                fillPaint
            )
        }
    }

    fun setMaxDuration(maxDuration: Int) {
        progressMaxValue = maxDuration * 1000 / PROGRESS_INTERVAL
    }

    fun setOnRecordListener(listener: onRecordListener?) {
        mListener = listener
    }

    override fun onLongClick(v: View): Boolean {
        if (mListener != null) {
            mListener!!.onLongClick()
        }
        return true
    }

    override fun onClick(v: View) {
        if (mListener != null) {
            mListener!!.onClick()
        }
    }

    interface onRecordListener {
        fun onClick()
        fun onLongClick()
        fun onFinish()
    }

    companion object {
        private const val PROGRESS_INTERVAL = 100
    }

    init {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.RecordView, defStyleAttr, defStyleRes)
        radius = typedArray.getDimensionPixelOffset(R.styleable.RecordView_radius, 0)
        progressWidth = typedArray.getDimensionPixelOffset(
            R.styleable.RecordView_progress_width,
            ConvertUtils.dp2px(3f)
        )
        progressColor = typedArray.getColor(R.styleable.RecordView_progress_color, Color.RED)
        fillColor = typedArray.getColor(R.styleable.RecordView_fill_color, Color.WHITE)
        maxDuration = typedArray.getInteger(R.styleable.RecordView_duration, 10)
        setMaxDuration(maxDuration)
        typedArray.recycle()
        fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        fillPaint.color = fillColor
        fillPaint.style = Paint.Style.FILL
        progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        progressPaint.color = progressColor
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeWidth = progressWidth.toFloat()
        val handler: Handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                progressValue++
                postInvalidate()
                if (progressValue <= progressMaxValue) {
                    sendEmptyMessageDelayed(0, PROGRESS_INTERVAL.toLong())
                } else {
                    finishRecord()
                }
            }
        }
        setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                isRecording = true
                startRecordTime = System.currentTimeMillis()
                handler.sendEmptyMessage(0)
            } else if (event.action == MotionEvent.ACTION_UP) {
                val now = System.currentTimeMillis()
                if (now - startRecordTime > ViewConfiguration.getLongPressTimeout()) {
                    finishRecord()
                }
                handler.removeCallbacksAndMessages(null)
                isRecording = false
                startRecordTime = 0
                progressValue = 0
                postInvalidate()
            }
            false
        }
        setOnClickListener(this)
        setOnLongClickListener(this)
    }
}