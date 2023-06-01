package com.example.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.databinding.ItemLoadingBinding

class LoadingAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadingAdapter.LoadingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class LoadingViewHolder(private val binding: ItemLoadingBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.cobaLagiButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            when (loadState) {
                is LoadState.Error -> {
                    binding.pesanError.text = loadState.error.message
                    binding.pesanError.isVisible = true
                    binding.progressBar.isVisible = false
                    binding.cobaLagiButton.isVisible = true
                }
                LoadState.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.pesanError.isVisible = false
                    binding.cobaLagiButton.isVisible = false
                }
                else -> {
                    binding.progressBar.isVisible = false
                    binding.pesanError.isVisible = false
                    binding.cobaLagiButton.isVisible = false
                }
            }
        }
    }
}