package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.myapplication.databinding.ActivityIntro2Binding


class IntroActivity2 : BaseActivity() {
    private lateinit var binding: ActivityIntro2Binding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityIntro2Binding.inflate(layoutInflater)
        setContentView(binding.root);

        // Set up edge-to-edge content
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        // Create a simple RequestListener implementation
        val gifListener = object : RequestListener<GifDrawable> {
            override fun onResourceReady(
                resource: GifDrawable,
                model: Any,
                target: Target<GifDrawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                resource.setLoopCount(1) // Chạy GIF 1 lần
                resource.start() // Bắt đầu chạy GIF

                handler.postDelayed({
                    resource.stop()
                }, 8000)

                return false
            }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<GifDrawable>,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        }

        // Load the GIF
        Glide.with(this)
            .asGif()
            .load(R.drawable.introaudi)
            .listener(gifListener)
            .into(binding.backgroundImage)

        // Chuyển sang IntroActivity sau 7 giây
        handler.postDelayed({
            val intent = Intent(this@IntroActivity2, IntroActivity::class.java)
            startActivity(intent)
            finish()
        }, 9000)

//        binding.startBtn.setOnClickListener {
//            startActivity(Intent(this@IntroActivity2, MainActivity::class.java))
//        }

    }

}