package com.example.gr34_in2000_v21.ui.views.search

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gr34_in2000_v21.R
import com.example.gr34_in2000_v21.data.models.MetAlertsModel

class ListRecyclerViewAdapter(private val alerts: List<MetAlertsModel.ItemCapJoin>) :
    RecyclerView.Adapter<ListRecyclerViewAdapter.ViewHolder>() {
    val distinct = alerts.distinctBy { it.identifier }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.danger_icon_placeholder)
        val danger: TextView = view.findViewById(R.id.danger_placeholder)
        val area: TextView = view.findViewById(R.id.area_placeholder)
        val headline: TextView = view.findViewById(R.id.headline_placeholder)
        val description: TextView = view.findViewById(R.id.description_placeholder)
        val instructions: TextView = view.findViewById(R.id.instructions_placeholder)
        val layout: LinearLayout = view.findViewById(R.id.cardview_top)
        val resImage: ImageView = view.findViewById(R.id.resImage)
        val resImageText: TextView = view.findViewById(R.id.resImageText)
        val expandableLayout: LinearLayout = view.findViewById(R.id.cardview_expandable)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_cardview, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.danger.text = alerts[position].info?.get(0)?.event
        holder.area.text = alerts[position].info?.get(0)?.area?.areaDesc
        holder.headline.text = alerts[position].info?.get(0)?.headline
        holder.description.text = alerts[position].info?.get(0)?.description
        holder.instructions.text = alerts[position].info?.get(0)?.instruction
        if (alerts[position].info?.get(0)?.resource?.uri != null) {
            "Deskriptivt bilde".also { holder.resImageText.text = it }
        }
        Glide.with(holder.resImage.context).load(alerts[position].info?.get(0)?.resource?.uri)
            .into(holder.resImage)

        val isExpandable: Boolean = alerts[position].expanded
        holder.expandableLayout.visibility = if (isExpandable) View.VISIBLE else View.GONE

        holder.layout.setOnClickListener {
            val a = alerts[position]
            a.expanded = !a.expanded
            notifyItemChanged(position)
        }

        var event: String? = alerts[position].info?.get(0)?.eventCode?.value?.lowercase()
        var dangerLevel: String? = alerts[position].info?.get(0)?.severity

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

        //Get resource id
        val res = holder.icon.resources.getIdentifier(
            drawableRes,
            "drawable",
            holder.icon.context.packageName
        )
        val bitmap: Bitmap = BitmapFactory.decodeResource(holder.icon.resources, res)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
        holder.icon.setImageBitmap(scaledBitmap)
    }

    override fun getItemCount() = distinct.size

}