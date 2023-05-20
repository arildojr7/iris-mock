package dev.arildo.iris.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dev.arildo.iris.sample.data.RetrofitInitializer
import dev.arildo.iris.sample.data.model.DummyRequest
import dev.arildo.iris.sample.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchData()

        binding.btnReload.setOnClickListener {
            binding.tvTest.text = "loading..."
            fetchData()
        }
    }

    private fun fetchData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val dummyRequest = DummyRequest()
            val result = RetrofitInitializer.apiService().getUserProfile()
            withContext(Dispatchers.Main) {
                binding.tvTest.text = result.body()?.data
            }
        }
    }
}
