package ru.netology.statsview

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import ru.netology.statsview.databinding.MainLayoutBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: MainLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.statsView.data = listOf(500F, 500F, 500F, 500F)
        binding.statsView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animation)
            .apply {
                setAnimationListener(object :Animation.AnimationListener{
                    override fun onAnimationEnd(animation: Animation?) {
                        binding.label.text = "onAnimationEnd"
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                        binding.label.text = "onAnimationRepeat"
                    }

                    override fun onAnimationStart(animation: Animation?) {
                        binding.label.text = "onAnimationStart"
                    }

                })
            })
    }
}