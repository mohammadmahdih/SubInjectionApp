package com.unknown.subinjection

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.unknown.subinjection.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity(),Main {

    private lateinit var binding: ActivityMainBinding

    lateinit var prefrences: SharedPreferences

    lateinit var crypto: Crypto

    var sign: String = ""

    var localUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefrences = getSharedPreferences("config", MODE_PRIVATE)
        crypto = Crypto()
        setup()
    }

    fun setup() {
        prefrences.getString("sign", null)?.let {
            this.sign = it
            binding.sign.setText(it)
        }
        binding.login.setOnClickListener {
            sign = binding.sign.text.toString()
            config()
        }
        binding.clearCache.setOnClickListener {
            clearCache()
        }
        binding.addSub.setOnClickListener {
            AddDialog(this,localUrl,this).show()
        }

    }

    fun config() {
        val configApi = "$baseUrl/config"
        val params = JSONObject()
        params.put("sign", crypto.enc(sign))
        val request = object : JsonObjectRequest(
            Method.POST, configApi, params,
            Response.Listener {
                prefrences.edit().putString("sign", this.sign).apply()
                //Snackbar.make(binding.root, it.toString(), Snackbar.LENGTH_SHORT).show()
                binding.login.visibility = View.GONE
                binding.loginProgress.visibility = View.GONE
                binding.subList.visibility = View.VISIBLE
                binding.addSub.visibility = View.VISIBLE
                binding.clearCache.visibility = View.VISIBLE

                val result = JSONObject(it.toString())
                localUrl = result.getString("localUrl")
                val subList = result.getJSONArray("subs")
                setSubList(subList)

            },
            Response.ErrorListener { error ->
                Snackbar.make(binding.root, error.toString(), Snackbar.LENGTH_SHORT).show()
                binding.login.visibility = View.VISIBLE
                binding.loginProgress.visibility = View.GONE
            }
        ) {}

        request.retryPolicy = DefaultRetryPolicy(10000, 4, 4f)

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)

    }


    fun setSubList(data: JSONArray){
        binding.subList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.subList.adapter = SubAdapter(data,this)
    }

    fun clearCache(){
        Snackbar.make(binding.root, "Clearing...", Snackbar.LENGTH_SHORT).show()
        val configApi = "$baseUrl/cache"
        val params = JSONObject()
        params.put("sign", crypto.enc(sign))
        val request = object : JsonObjectRequest(
            Method.POST, configApi, params,
            Response.Listener {
                prefrences.edit().putString("sign", this.sign).apply()
                Snackbar.make(binding.root, "Cache was cleared.", Snackbar.LENGTH_SHORT).show()

            },
            Response.ErrorListener { error ->
                Snackbar.make(binding.root, error.toString(), Snackbar.LENGTH_SHORT).show()
            }
        ) {}

        request.retryPolicy = DefaultRetryPolicy(10000, 4, 4f)
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }

    override fun subEdited() {
        config()
    }

}