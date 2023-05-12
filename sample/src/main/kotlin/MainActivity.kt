package dev.arildo.iris.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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

        lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                val dummyRequest = DummyRequest()
                val result = RetrofitInitializer.apiService().getUserProfile(dummyRequest)
                withContext(Dispatchers.Main) {
                    binding.tvTest.text = result.body()?.data
                }
            }
        }
    }
}
