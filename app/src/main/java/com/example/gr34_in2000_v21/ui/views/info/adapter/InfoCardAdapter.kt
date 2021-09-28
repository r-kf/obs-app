package com.example.gr34_in2000_v21.ui.views.info.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gr34_in2000_v21.R
import com.example.gr34_in2000_v21.ui.views.info.model.InfoCardModel

class InfoCardAdapter(private var infoList: List<InfoCardModel>) :
    RecyclerView.Adapter<InfoCardAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.textView)
        val icon: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(card: InfoCardModel) {
            title.text = card.title
            Glide.with(itemView).load(card.icon).into(icon)
            icon.transitionName = card.icon
            itemView.setOnClickListener {
                infoCardSelectedListener.onCardSelected(card, icon)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoCardAdapter.ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.info_card, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: InfoCardAdapter.ViewHolder, position: Int) {
        holder.bind(infoList[position])
    }

    override fun getItemCount() = infoList.size

    interface InfoCardSelectedListener {
        fun onCardSelected(card: InfoCardModel, imageView: ImageView)
    }

    lateinit var infoCardSelectedListener: InfoCardSelectedListener

}