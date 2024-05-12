package com.unknown.subinjection

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.unknown.subinjection.databinding.DialogAddBinding
import com.unknown.subinjection.databinding.DialogDeleteBinding
import com.unknown.subinjection.databinding.DialogEditBinding
import org.json.JSONObject


class DeleteDialog(context:Context, val sub:String, val main: Main):AlertDialog(context) {

    lateinit var binding: DialogDeleteBinding

    var sign:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        binding = DialogDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context.getSharedPreferences("config", AppCompatActivity.MODE_PRIVATE).getString("sign", null)?.let {
            this.sign = it
        }
        val crypto = Crypto()
        binding.sub.setText(sub)
        binding.delBtn.setOnClickListener {
            val configApi = "$baseUrl/editor/delete"
            val params = JSONObject()
            params.put("sign", crypto.enc(sign))
            params.put("sub", crypto.enc(binding.sub.text.toString()))
            val request = object : JsonObjectRequest(
                Method.POST, configApi, params,
                Response.Listener {
                    dismiss()
                    Toast.makeText(context, "Sub was deleted.", Toast.LENGTH_SHORT).show()
                    main.subEdited()
                },
                Response.ErrorListener { error ->
                    Toast.makeText(context, error.message.toString(), Toast.LENGTH_LONG).show()
                }
            ) {}

            request.retryPolicy = DefaultRetryPolicy(10000, 4, 4f)

            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(request)
        }
    }


}