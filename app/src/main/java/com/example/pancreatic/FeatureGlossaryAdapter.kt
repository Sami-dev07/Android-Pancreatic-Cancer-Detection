package com.example.pancreatic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pancreatic.api.SchemaField

class FeatureGlossaryAdapter(
    private val fields: List<SchemaField>,
) : RecyclerView.Adapter<FeatureGlossaryAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_feature_glossary, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = fields.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(fields[position])
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val meta: TextView = itemView.findViewById(R.id.meta)
        private val description: TextView = itemView.findViewById(R.id.description)
        private val dataset: TextView = itemView.findViewById(R.id.dataset)
        private val effect: TextView = itemView.findViewById(R.id.effectNote)

        fun bind(f: SchemaField) {
            title.text = "${f.label} (${f.name})"

            val parts = mutableListOf<String>()
            f.units?.takeIf { it.isNotBlank() }?.let { parts.add("Units: $it") }
            if (f.type == "number" && (f.min != null || f.max != null)) {
                parts.add("Range: ${f.min ?: "—"}–${f.max ?: "—"}")
            }
            meta.text = if (parts.isEmpty()) " " else parts.joinToString(" · ")

            val desc = f.description?.trim().orEmpty()
            description.visibility = if (desc.isBlank()) View.GONE else View.VISIBLE
            description.text = desc

            val src = f.dataset_source?.trim().orEmpty()
            dataset.visibility = if (src.isBlank()) View.GONE else View.VISIBLE
            dataset.text = if (src.isBlank()) "" else "Dataset: $src"

            val note = f.effect_note?.trim().orEmpty()
            effect.visibility = if (note.isBlank()) View.GONE else View.VISIBLE
            effect.text = note
        }
    }
}

