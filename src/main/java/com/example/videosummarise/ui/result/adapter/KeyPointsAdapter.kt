package com.example.videosummarise.ui.result.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.videosummarise.databinding.ItemKeyPointBinding

class KeyPointsAdapter(
    private val keyPoints: List<String>
) : RecyclerView.Adapter<KeyPointsAdapter.KeyPointViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyPointViewHolder {
        val binding = ItemKeyPointBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return KeyPointViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KeyPointViewHolder, position: Int) {
        holder.bind(keyPoints[position], position + 1)
    }

    override fun getItemCount(): Int = keyPoints.size

    class KeyPointViewHolder(
        private val binding: ItemKeyPointBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(keyPoint: String, number: Int) {
            binding.keyPointNumber.text = number.toString()
            binding.keyPointText.text = keyPoint
        }
    }
}
