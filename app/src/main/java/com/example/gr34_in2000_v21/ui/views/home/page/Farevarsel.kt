package com.example.gr34_in2000_v21.ui.views.home.page

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.example.gr34_in2000_v21.data.models.DataResult
import com.example.gr34_in2000_v21.databinding.FragmentFarevarselBinding
import com.example.gr34_in2000_v21.ui.SharedViewModel
import com.example.gr34_in2000_v21.utils.Helpers
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class Farevarsel : Fragment() {
    private val args: FarevarselArgs by navArgs()
    private val viewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentFarevarselBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        findNavController().currentDestination?.label = "Farevarsel"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFarevarselBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        binding.appbar.setExpanded(false)
        binding.toolbar.setupWithNavController(findNavController())

        viewModel.cap(args.guid).observe(viewLifecycleOwner) { result ->
            when (result.status) {
                DataResult.Status.SUCCESS -> {
                    binding.progressBar2.visibility = View.GONE
                    if (result.data != null) {
                        val alert = result.data
                        val info = alert.info!![0]
                        binding.vTitle.text = info.headline
                        binding.vSeverity.text =
                            Helpers.StringFix.norwegianizeSeverity(info.severity)
                        if (info.parameter.find { it.valueName.equals("awarenessSeriousness") } != null) {
                            binding.vSeriousness.text =
                                info.parameter.find { it.valueName.equals("awarenessSeriousness") }!!.value
                        }
                        binding.vDesc.text = info.description
                        binding.vInstructions.text = info.instruction
                        binding.toolbar.title = "Farevarsel: ${info.event}"
                        if (info.resource?.uri != null) {
                            Glide.with(binding.vDescImage.context).load(info.resource?.uri)
                                .into(binding.vDescImage)
                        }
                    }
                }
                DataResult.Status.ERROR -> {
                    Timber.e("${result.message?.code} ${result.message?.msg}")
                    Toast.makeText(
                        requireContext(),
                        "Oops! Klarte ikke å hente varselinformasjon! Prøv igjen!",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigateUp()
                }
                DataResult.Status.LOADING -> binding.progressBar2.visibility = View.VISIBLE
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val animator = ObjectAnimator.ofFloat(binding.farevarselPage, "alpha", 0f, 1f)
        animator.startDelay = 100
        animator.duration = 300
        animator.start()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}