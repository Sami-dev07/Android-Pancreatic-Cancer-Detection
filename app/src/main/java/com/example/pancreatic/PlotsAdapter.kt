package com.example.pancreatic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pancreatic.api.PlotItem

class PlotsAdapter(
    private val baseUrl: String,
    private val items: List<PlotItem>,
    private val onClick: (PlotItem) -> Unit,
) : RecyclerView.Adapter<PlotsAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_plot, parent, false)
        return VH(v, onClick)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(baseUrl, items[position])
    }

    class VH(itemView: View, private val onClick: (PlotItem) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.plotName)
        private val image: ImageView = itemView.findViewById(R.id.plotImage)

        private var current: PlotItem? = null

        init {
            itemView.setOnClickListener { current?.let(onClick) }
        }

        fun bind(baseUrl: String, item: PlotItem) {
            current = item
            name.text = item.filename
            val url = (if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl) + item.static_url
            image.load(url) { crossfade(true) }
        }
    }
}

