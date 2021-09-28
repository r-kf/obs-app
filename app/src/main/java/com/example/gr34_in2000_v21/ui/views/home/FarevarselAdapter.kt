package com.example.gr34_in2000_v21.ui.views.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gr34_in2000_v21.R
import com.example.gr34_in2000_v21.data.models.MetAlertsModel
import timber.log.Timber

class FarevarselAdapter :
    RecyclerView.Adapter<FarevarselAdapter.ViewHolder>() {

    private var items: List<MetAlertsModel.ItemCapJoin> = emptyList()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = itemView.findViewById(R.id.home_label)
        val description: TextView = itemView.findViewById(R.id.home_description)
        val icon: ImageView = itemView.findViewById(R.id.home_icon)
        val expandIcon: ImageView = itemView.findViewById(R.id.expand_icon)

        fun bind(card: MetAlertsModel.ItemCapJoin) {
            title.text = card.info?.get(0)?.event ?: card.title
            description.text = card.info?.get(0)?.area?.areaDesc ?: card.description

            if (card.info != null) {
                expandIcon.visibility = View.VISIBLE
                var event: String? = card.info?.get(0)?.eventCode?.value?.lowercase()
                var dangerLevel: String? = card.info?.get(0)?.severity

                if (event == "blowingsnow" || event == "icing") {
                    event = "snow"
                } else if (event == "gale") {
                    event = "wind"
                }

                dangerLevel = when (dangerLevel) {
                    "Moderate" -> "yellow"
                    "Severe" -> "red"
                    else -> {
                        "orange"
                    }
                }

                val drawableRes = "icon_warning_" + event.toString() + "_" + dangerLevel.toString()
                val res = icon.resources.getIdentifier(
                    drawableRes,
                    "drawable",
                    icon.context.packageName
                )
                val bitmap: Bitmap = BitmapFactory.decodeResource(icon.resources, res)
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
                icon.setImageBitmap(scaledBitmap)
            } else {
                icon.setImageDrawable(ResourcesCompat.getDrawable(icon.resources, R.drawable.no_warning_placeholder, null))
                expandIcon.visibility = View.GONE
            }


            itemView.setOnClickListener {
                varselCardSelectedListener.onCardSelected(card)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FarevarselAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FarevarselAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        Timber.d("Contains ${items.size} items")
        return items.distinctBy { it.identifier }.size
    }

    fun setItems(items: List<MetAlertsModel.ItemCapJoin>) {
        this.items = items
        notifyDataSetChanged()
    }

    interface VarselCardSelectedListener {
        fun onCardSelected(card: MetAlertsModel.ItemCapJoin)
    }

    lateinit var varselCardSelectedListener: VarselCardSelectedListener

}