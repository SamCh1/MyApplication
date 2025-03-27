package com.example.myapplication.ui.theme
import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


class GradientTextView(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val paint = paint
        val textColor = currentTextColor

        // Sử dụng alpha để làm mờ dần nửa dưới
        val shader = LinearGradient(
            0f, 0f, 0f, h.toFloat(),
            intArrayOf(textColor, textColor and 0x00FFFFFF), // Từ màu trắng đến trong suốt
            floatArrayOf(0.4f, 1f), // 70% trên giữ nguyên màu, 30% dưới mờ dần
            Shader.TileMode.CLAMP
        )

        paint.shader = shader
        invalidate() // Vẽ lại TextView với shader mới
    }
}