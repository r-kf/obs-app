package com.example.gr34_in2000_v21.ui.views.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gr34_in2000_v21.databinding.InfoFragmentBinding
import com.example.gr34_in2000_v21.ui.views.info.adapter.InfoCardAdapter
import com.example.gr34_in2000_v21.ui.views.info.model.InfoCardModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class InfoFragment : Fragment() {
    private val viewModel: InfoViewModel by viewModels()
    private lateinit var binding: InfoFragmentBinding

    private lateinit var infoCardAdapter: InfoCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return if (::binding.isInitialized) {
            binding.root
        } else {
            infoCardAdapter = InfoCardAdapter(viewModel.infoList)
            binding = InfoFragmentBinding.inflate(inflater, container, false)
            with(binding) {
                infoGrid.apply {
                    adapter = infoCardAdapter
                    layoutManager =
                        GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
                    postponeEnterTransition()
                    viewTreeObserver.addOnPreDrawListener {
                        startPostponedEnterTransition()
                        true
                    }
                }

                root
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        infoCardAdapter.infoCardSelectedListener =
            object : InfoCardAdapter.InfoCardSelectedListener {
                override fun onCardSelected(card: InfoCardModel, imageView: ImageView) {
                    val extras = FragmentNavigatorExtras(
                        imageView to card.icon
                    )
                    val action = InfoFragmentDirections.actionInfoListToInfoPage(
                        title = card.title,
                        icon = card.icon,
                        content = card.content
                    )
                    findNavController().navigate(action, extras)
                }
            }
    }

}