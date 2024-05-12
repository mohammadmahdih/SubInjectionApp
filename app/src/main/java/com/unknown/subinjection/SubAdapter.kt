package com.unknown.subinjection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray

class SubAdapter(val data: JSONArray, val main: Main) :
    RecyclerView.Adapter<SubAdapter.SubAdapterHolder>() {

    inner class SubAdapterHolder(item: View) : RecyclerView.ViewHolder(item) {
        val card = item.findViewById<CardView>(R.id.item_sub)
        val subText = item.findViewById<TextView>(R.id.sub_text)
        val editBtn = item.findViewById<ImageView>(R.id.edit_btn)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubAdapterHolder {
        return SubAdapterHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_sub, parent, false)
        )
    }

    override fun getItemCount(): Int = data.length()

    override fun onBindViewHolder(holder: SubAdapterHolder, position: Int) {
        val crypto = Crypto()
        val sub = crypto.dec(data.getString(position))
        holder.subText.text = sub

        holder.editBtn.setOnClickListener {
            EditDialog(holder.itemView.context, sub, main).show()
        }

        holder.card.setOnLongClickListener {
            DeleteDialog(holder.itemView.context, sub, main).show()
            return@setOnLongClickListener true
        }

    }

}