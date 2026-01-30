package ru.netology.statsview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.statsview.databinding.MainLayoutBinding
import ru.netology.statsview.ui.StatsView

class MainActivity : AppCompatActivity() {
    lateinit var binding: MainLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewById<StatsView>(R.id.statsView).data = listOf(500F, 500F, 500F, 500F)
    }
}