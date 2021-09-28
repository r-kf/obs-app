package com.example.gr34_in2000_v21.ui.views.info.page

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.example.gr34_in2000_v21.R
import com.example.gr34_in2000_v21.databinding.FragmentInfoPageBinding
import io.noties.markwon.Markwon
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.network.OkHttpNetworkSchemeHandler


class InfoPage : Fragment() {
    private val args: InfoPageArgs by navArgs()
    private var _binding: FragmentInfoPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var markwon: Markwon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        findNavController().currentDestination?.label = args.title
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoPageBinding.inflate(inflater, container, false)
        Glide.with(requireContext()).load(args.icon).into(binding.infoPageImage)
        binding.collapsableToolbarLayout.setCollapsedTitleTextAppearance(R.style.Theme_InfoPageCollapsed)
        binding.collapsableToolbarLayout.setExpandedTitleTextAppearance(R.style.Theme_InfoPageExpanded)
        binding.collapsableToolbarLayout.setScrimsShown(false)
        binding.toolbar.setupWithNavController(findNavController())
        markwon = Markwon.builder(requireContext())
            .usePlugin(ImagesPlugin.create { plugin ->
                plugin.addSchemeHandler(
                    OkHttpNetworkSchemeHandler.create()
                )
            })
            .build()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.infoPageImage.transitionName = args.icon
        markwon.setMarkdown(binding.infoMarkdown, args.content)
        val animator = ObjectAnimator.ofFloat(binding.infoPage, "alpha", 0f, 1f)
        animator.startDelay = 100
        animator.duration = 300
        animator.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}