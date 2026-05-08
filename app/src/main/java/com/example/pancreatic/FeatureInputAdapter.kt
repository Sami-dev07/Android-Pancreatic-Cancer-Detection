package com.example.pancreatic

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pancreatic.api.SchemaField

class FeatureInputAdapter(
    private val fields: List<SchemaField>,
    private val onLearn: (SchemaField) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val values: MutableMap<String, Any> = mutableMapOf()

    fun getValues(): Map<String, Any> = values.toMap()

    override fun getItemViewType(position: Int): Int {
        val f = fields[position]
        val hasOptions = !f.options.isNullOrEmpty()
        return if (hasOptions) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 1) {
            SpinnerVH(inflater.inflate(R.layout.item_feature_spinner, parent, false))
        } else {
            InputVH(inflater.inflate(R.layout.item_feature_input, parent, false))
        }
    }

    override fun getItemCount(): Int = fields.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val field = fields[position]
        if (holder is InputVH) holder.bind(field)
        if (holder is SpinnerVH) holder.bind(field)
    }

    inner class InputVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.featureName)
        private val value: EditText = itemView.findViewById(R.id.featureValue)
        private val learnButton: View = itemView.findViewById(R.id.featureInfoButton)
        private var watcher: TextWatcher? = null

        fun bind(field: SchemaField) {
            name.text = field.label
            learnButton.setOnClickListener { onLearn(field) }
            value.hint = when {
                field.min != null && field.max != null -> "Range: ${field.min} - ${field.max}"
                else -> "Enter value"
            }
            value.inputType = if (field.type == "number") {
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            } else {
                InputType.TYPE_CLASS_TEXT
            }
            value.setText(values[field.name]?.toString() ?: "")

            watcher?.let { value.removeTextChangedListener(it) }
            watcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val text = s?.toString()?.trim().orEmpty()
                    if (text.isBlank()) values.remove(field.name) else values[field.name] = text
                }
            }
            value.addTextChangedListener(watcher)
        }
    }

    inner class SpinnerVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.featureName)
        private val spinner: Spinner = itemView.findViewById(R.id.featureSpinner)
        private val learnButton: View = itemView.findViewById(R.id.featureInfoButton)
        private var isBinding = false

        fun bind(field: SchemaField) {
            name.text = field.label
            learnButton.setOnClickListener { onLearn(field) }
            val options = field.options ?: emptyList()
            spinner.onItemSelectedListener = null

            val displayOptions = options.map { it.toString() }
            val adapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, displayOptions)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            isBinding = true
            val current = values[field.name]?.toString()
            val idx = when {
                current != null -> displayOptions.indexOf(current).takeIf { it >= 0 } ?: 0
                else -> 0
            }
            if (displayOptions.isNotEmpty()) {
                spinner.setSelection(idx, false)
                if (current == null) {
                    values[field.name] = displayOptions[idx]
                }
            }
            isBinding = false

            spinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    if (isBinding) return
                    if (displayOptions.isNotEmpty()) values[field.name] = displayOptions[position]
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
        }
    }
}

