package com.example.storyapp.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.content.res.Configuration
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.adapter.ListStoryAdapter
import com.example.storyapp.adapter.LoadingAdapter
import com.example.storyapp.viewmodel.ViewModelFactory
import com.example.storyapp.databinding.FragmentStoryBinding
import com.example.storyapp.viewmodel.StoriesViewModel

class StoryFragment : Fragment() {

    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var listStoryAdapter: ListStoryAdapter
    private lateinit var factory: ViewModelFactory
    private val homeViewModel: StoriesViewModel by viewModels { factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        factory = ViewModelFactory.getInstance(binding.root.context)

        val rvStory = binding.rvStory

        if (root.context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rvStory.layoutManager = GridLayoutManager(root.context, 2)
        } else {
            rvStory.layoutManager = LinearLayoutManager(root.context)
        }

        listStoryAdapter = ListStoryAdapter()
        rvStory.adapter = listStoryAdapter

        val dividerItemDecoration = DividerItemDecoration(root.context, LinearLayoutManager.VERTICAL)
        binding.rvStory.addItemDecoration(dividerItemDecoration)

        binding.addStoryButton.setOnClickListener {
            val intent = Intent(binding.root.context, CreateNewStoryActivity::class.java)
            startActivity(intent)
        }

        binding.rvStory.adapter = listStoryAdapter.withLoadStateFooter(
            footer = LoadingAdapter {
                listStoryAdapter.retry()
            }
        )

        homeViewModel.getListStory.observe(viewLifecycleOwner) {
            listStoryAdapter.submitData(lifecycle, it)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
