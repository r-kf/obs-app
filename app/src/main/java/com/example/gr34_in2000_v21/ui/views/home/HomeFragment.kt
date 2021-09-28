package com.example.gr34_in2000_v21.ui.views.home

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.gr34_in2000_v21.R
import com.example.gr34_in2000_v21.data.models.DataResult
import com.example.gr34_in2000_v21.data.models.GeoNorgeModel
import com.example.gr34_in2000_v21.data.models.MetAlertsModel
import com.example.gr34_in2000_v21.databinding.HomeFragmentBinding
import com.example.gr34_in2000_v21.ui.MainActivity
import com.example.gr34_in2000_v21.ui.SharedViewModel
import com.example.gr34_in2000_v21.utils.Helpers
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.properties.Delegates

//The slide menu for locations is based on these three guides on ViewPager2 with modifications:
//https://developer.android.com/training/animation/screen-slide-2
//https://stackoverflow.com/questions/60957775/howto-nest-viewpager2-within-a-fragment
//https://codingwithmitch.com/blog/swiping-views-with-viewpager/

var currentLocation: Location? = null

@AndroidEntryPoint
class HomeFragment : Fragment() {

    var currentLocationLat by Delegates.notNull<Double>()
    var currentLocationLon by Delegates.notNull<Double>()

    private val viewModel: SharedViewModel by activityViewModels()
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var farevarselAdapter: FarevarselAdapter
    private lateinit var favoritesAdapter: ViewPagerAdapter

    private fun initFavorites() {
        viewModel.favorites.observe(viewLifecycleOwner) {
            Timber.d("favlist: $it")
            favoritesAdapter.setFavorites(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        farevarselAdapter = FarevarselAdapter()
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        with(binding) {
            farevarselRecyclerView.setHasFixedSize(true)
            farevarselRecyclerView.apply {
                adapter = farevarselAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCurrentLocation()
        initFavorites()

        val mViewPager: ViewPager2 = view.findViewById(R.id.pager)
        favoritesAdapter = ViewPagerAdapter(this)
        mViewPager.adapter = favoritesAdapter

        //Tabs
        val tabLayout: TabLayout = view.findViewById(R.id.tabs)

        TabLayoutMediator(tabLayout, mViewPager) { tab, _ ->
            tab.text =
                "" //Sets tabs names
        }.attach()

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.map_button -> {
                    findNavController().navigate(R.id.navigation_search)
                    true
                }
                else -> false
            }
        }

        //Updates recyclerview according to what tab is selected
        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                //Updating recyclerview for your gps position when first tab selected
                if (position == 0) {
                    if (currentLocation != null) {
                        getItemsFromLocation(currentLocationLat, currentLocationLon)
                    }
                    if (currentLocation == null) {
                        //Inserts location off card into recyclerview
                        val no = listOf(MetAlertsModel.ItemCapJoin("Sted-tjenester er av for Obs!"))
                        farevarselAdapter.setItems(no)
                        Timber.d("RecyclerView error: Location is turned off")
                    }
                }
                //Updating recyclerview acording to what favourite location is selected
                if (position > 0) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.noneText.visibility = View.GONE

                    getItemsFromLocation(
                        favoritesAdapter.getFavorite(position).punktIOmrade.coordinates[1],
                        favoritesAdapter.getFavorite(position).punktIOmrade.coordinates[0]
                    )
                    Timber.d("RecyclerView: will update")
                }
                super.onPageSelected(position)
            }
        })

        farevarselAdapter.varselCardSelectedListener =
            object : FarevarselAdapter.VarselCardSelectedListener {
                override fun onCardSelected(card: MetAlertsModel.ItemCapJoin) {
                    if (card.guid.isNotBlank()) {
                        val action = HomeFragmentDirections.actionNavigationHomeToFarevarsel(
                            guid = card.guid
                        )
                        findNavController().navigate(action)
                    }
                }
            }

    }

    private fun getCurrentLocation() {
        if (!Helpers.Location.isLocationEnabled(requireContext()) || !Helpers.Location.checkPermissions(
                requireContext()))
            Helpers.Location.requestPermissions(requireActivity())

        viewModel.getCurrentLocation().observe(viewLifecycleOwner) { location ->
            if (location != null) {
                Timber.d("lat: ${location.latitude} | lon: ${location.longitude}")
                getItemsFromLocation(location.latitude, location.longitude)
                currentLocation = location
                currentLocationLat = location.latitude
                currentLocationLon = location.longitude
                favoritesAdapter.setCurrentLocation(location.latitude, location.longitude)
                Timber.d("Location?: Location is not null")
            }
            if (location == null) {
                Timber.d("Location?: Location is null")
            }
        }
    }

    private fun getItemsFromLocation(lat: Double, lon: Double) {
        viewModel.getItemsFromLatLonWithCap(lat, lon).observe(viewLifecycleOwner) { data ->
            when (data.status) {
                DataResult.Status.SUCCESS -> {
                    if (data.data != null) {
                        handleRecyclerView(data.data)
                    }
                }
                DataResult.Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    val no = listOf(MetAlertsModel.ItemCapJoin("Ingen farevarsler for i dag!"))
                    farevarselAdapter.setItems(no)
                    Timber.d("${data.message?.code} ${data.message?.msg}")
                }
                DataResult.Status.LOADING -> binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun handleRecyclerView(data: List<MetAlertsModel.ItemCapJoin>?) {
        binding.progressBar.visibility = View.GONE
        if (data != null) {
            farevarselAdapter.setItems(data.sortedByDescending { it.pubDate })
        } else {
            val no = listOf(MetAlertsModel.ItemCapJoin("Ingen farevarsler for i dag!"))
            farevarselAdapter.setItems(no)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private var favorites: List<GeoNorgeModel.CountyCoordinates> = emptyList()
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    override fun getItemCount(): Int {
        //Numer of locations shown
        return favorites.size + 1
    }

    override fun createFragment(position: Int): Fragment {
        return if (position > 0) {
            val favorite = favorites[position - 1]
            val knavn = favorite.kommunenavn
            val fnavn = favorite.fylkesnavn!!
            FavoritePagerFrag(knavn, "$fnavn, Norge")
        } else {
            FirstPagerFrag()
        }
    }

    fun setCurrentLocation(lat: Double, lon: Double) {
        this.lat = lat
        this.lon = lon
        notifyItemChanged(0)
    }

    fun getFavorite(i: Int) = favorites[i - 1]

    fun setFavorites(list: List<GeoNorgeModel.CountyCoordinates>) {
        this.favorites = list
        notifyDataSetChanged()
    }
}

//Fragment classes
class FirstPagerFrag : Fragment() {

    private val viewModel: SharedViewModel by activityViewModels()
    private var stedTekst: String = "Ukjent kommune"
    private var byLandTekst: String = "Ukjent fylke"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.sted_slide_layout, container, false)
        val stedText: TextView = view.findViewById(R.id.stedTekst)
        val byLandText: TextView = view.findViewById(R.id.byLandTekst)

        if(!(activity as MainActivity).sharedPreferences.getBoolean("isDarkModeOn", false)) {
            stedText.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_baseline_place_30,
                0,
                0,
                0
            )
        }else{
            stedText.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_baseline_place_33,
                0,
                0,
                0
            )
        }

        stedText.text = stedTekst
        byLandText.text = byLandTekst

        viewModel.getCurrentLocation().observe(viewLifecycleOwner) { location ->
            if (location != null) {
                viewModel.getCountyFromCoordinates(location.latitude, location.longitude)
                    .observe(viewLifecycleOwner) { result ->
                        when (result.status) {
                            DataResult.Status.SUCCESS -> {
                                val county = result.data!!
                                stedTekst = county.kommunenavn ?: "..."
                                byLandTekst = "${county.fylkesnavn}, Norge"
                                stedText.text = stedTekst
                                byLandText.text = byLandTekst
                            }
                            DataResult.Status.ERROR -> {
                                stedTekst = "Det oppstod en feil!"
                                byLandTekst = "Klarte ikke å finne din kommune og fylke!"
                                stedText.text = stedTekst
                                byLandText.text = byLandTekst
                            }
                            DataResult.Status.LOADING -> {
                                stedTekst = "Laster inn"
                                byLandTekst = "..."
                                stedText.text = stedTekst
                                byLandText.text = byLandTekst
                            }
                        }
                    }
            }
        }
        return view
    }
}

class FavoritePagerFrag(private val stedTekst: String = "", private val byLandTekst: String = "") :
    Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.sted_slide_layout, container, false)
        val stedText: TextView = view.findViewById(R.id.stedTekst)
        if(!(activity as MainActivity).sharedPreferences.getBoolean("isDarkModeOn", false)) {
            stedText.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_baseline_favorite_30,
                0,
                0,
                0
            )
        }else{
            stedText.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_baseline_favorite_33,
                0,
                0,
                0
            )
        }
        stedText.text = stedTekst
        val byLandText: TextView = view.findViewById(R.id.byLandTekst)
        byLandText.text = byLandTekst
        return view
    }
}
