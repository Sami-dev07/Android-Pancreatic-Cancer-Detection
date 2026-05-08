package com.example.pancreatic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pancreatic.api.PerformanceBlock
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class PerformanceBlocksAdapter(
    private val blocks: List<PerformanceBlock>,
) : RecyclerView.Adapter<PerformanceBlocksAdapter.VH>() {

    override fun getItemViewType(position: Int): Int {
        return if (blocks[position].id == "confusion_matrix") 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(
            if (viewType == 1) R.layout.item_confusion_matrix else R.layout.item_performance_block,
            parent,
            false
        )
        return VH(v)
    }

    override fun getItemCount(): Int = blocks.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(blocks[position])
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.blockTitle)
        private val body: TextView? = itemView.findViewById(R.id.blockBody)
        private val chips: ChipGroup? = itemView.findViewById(R.id.blockChips)

        private val tn: TextView? = itemView.findViewById(R.id.tn)
        private val fp: TextView? = itemView.findViewById(R.id.fp)
        private val fn: TextView? = itemView.findViewById(R.id.fn)
        private val tp: TextView? = itemView.findViewById(R.id.tp)

        fun bind(block: PerformanceBlock) {
            title.text = block.title
            if (tn != null && fp != null && fn != null && tp != null) {
                // Lines are like: "TN=69  FP=9", "FN=3  TP=37"
                val joined = block.lines.joinToString(" ")
                fun extract(key: String): String {
                    val idx = joined.indexOf("$key=")
                    if (idx < 0) return "-"
                    val start = idx + key.length + 1
                    val end = joined.indexOfAny(charArrayOf(' ', '\n', '\t'), startIndex = start).let { if (it < 0) joined.length else it }
                    return joined.substring(start, end)
                }
                tn.text = extract("TN")
                fp.text = extract("FP")
                fn.text = extract("FN")
                tp.text = extract("TP")
                return
            }

            body?.text = block.lines.joinToString("\n")

            chips?.removeAllViews()
            val chipItems = block.chips ?: emptyList()
            if (chipItems.isEmpty()) {
                chips?.visibility = View.GONE
            } else {
                chips?.visibility = View.VISIBLE
                chipItems.forEach { c ->
                    val chip = Chip(itemView.context).apply {
                        text = c.label
                        isClickable = false
                        isCheckable = false
                    }
                    chips?.addView(chip)
                }
            }
        }
    }
}

