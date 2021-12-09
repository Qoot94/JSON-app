package com.example.json_app

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import com.example.json_app.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.json.JSONObject
import java.math.RoundingMode
import java.net.URL
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class MainActivity : AppCompatActivity() {
    lateinit var currencyList: ArrayList<String>
    lateinit var binding: ActivityMainBinding
    var clickedAt: Int = 0
    var input = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currencyList = arrayListOf("inr", "aud", "sar", "krw", "kwd")

        val adapter = ArrayAdapter<String>(this, R.layout.simple_spinner_item, currencyList)

        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                //val item = parent?.getItemAtPosition(position).toString()
                clickedAt = position
                input = currencyList[clickedAt]
                Log.d("TAFFF", "$input")
            }
        }
        requestAPI()
    }

    //api request
    private fun fetchData(): String {
        var response = ""
        try {
            response =
                URL("https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/eur.json").readText()
        } catch (e: Exception) {
            Log.d("MAIN", "ISSUE: $e")
        }
        // our response is saved as a string and returned
        return response
    }

    private suspend fun populateSpinner(result: String) {
        withContext(Dispatchers.Main) {
            // we create a JSON object from the data
            val jsonObject = JSONObject(result)
            val date = jsonObject.getString("date").toString()
            val currencyList = jsonObject.getJSONObject("eur")
            val currencyKeys = jsonObject.getJSONObject("eur").names()

            binding.tvDate.text = date

            binding.btConvert.setOnClickListener {
                val exchangeRate =
                    currencyList[input].toString().toDouble() //amount retrieved from api
                Log.d("exchange", "$exchangeRate")
                val amount = binding.etConvertFrom.text.toString().toInt()
                var result = ""

                //multi by number we get from api
                binding.tvresult.text = "result: ${(amount / exchangeRate).toBigDecimal().setScale(3, RoundingMode.HALF_EVEN)}"
            }
        }
    }


    private fun requestAPI() {
        // we use Coroutines to fetch the data, then update the Recycler View if the data is valid
        CoroutineScope(Dispatchers.IO).launch {
            // we fetch the data
            val data = async { fetchData() }.await()
            // once the data comes back, we populate our Recycler View
            if (data.isNotEmpty()) {
                populateSpinner(data)
            } else {
                Log.d("MAIN", "Unable to get data")
            }
        }
    }

}