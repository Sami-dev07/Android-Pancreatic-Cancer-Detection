package com.example.pancreatic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pancreatic.api.ModelInfo
import com.google.android.material.button.MaterialButton

/**
 * Renders one [ModelInfo] per card with active badge, load warning, and select action.
 */
class ModelCardAdapter(
    private val onSelect: (String) -> Unit,
) : RecyclerView.Adapter<ModelCardAdapter.ModelCardViewHolder>() {

    private var items: List<ModelInfo> = emptyList()
    private var selectInProgress: Boolean = false

    /** Replaces the displayed list (typically after a refresh from the server). */
    fun submitList(models: List<ModelInfo>) {
        items = models
        notifyDataSetChanged()
    }

    /** When true, select buttons are disabled to avoid duplicate taps during POST /select-model. */
    fun setSelectInProgress(inProgress: Boolean) {
        selectInProgress = inProgress
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_model_card, parent, false)
        return ModelCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModelCardViewHolder, position: Int) {
        holder.bind(items[position], selectInProgress, onSelect)
    }

    override fun getItemCount(): Int = items.size

    class ModelCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val modelName: TextView = itemView.findViewById(R.id.modelName)
        private val modelDescription: TextView = itemView.findViewById(R.id.modelDescription)
        private val activeBadge: TextView = itemView.findViewById(R.id.activeBadge)
        private val notLoadedWarning: TextView = itemView.findViewById(R.id.notLoadedWarning)
        private val selectButton: MaterialButton = itemView.findViewById(R.id.selectButton)

        fun bind(info: ModelInfo, selectInProgress: Boolean, onSelect: (String) -> Unit) {
            modelName.text = info.name
            modelDescription.text = info.description

            activeBadge.visibility = if (info.active) View.VISIBLE else View.GONE
            notLoadedWarning.visibility = if (info.loaded) View.GONE else View.VISIBLE

            val isActive = info.active
            selectButton.isEnabled = !isActive && info.loaded && !selectInProgress
            selectButton.text = itemView.context.getString(
                if (isActive) R.string.model_card_button_active else R.string.model_card_button_select,
            )
            selectButton.setOnClickListener {
                if (!isActive && info.loaded && !selectInProgress) {
                    onSelect(info.key)
                }
            }
        }
    }
}
