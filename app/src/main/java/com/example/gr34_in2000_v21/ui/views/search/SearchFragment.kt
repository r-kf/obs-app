package com.example.gr34_in2000_v21.ui.views.search

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.gr34_in2000_v21.R
import com.example.gr34_in2000_v21.data.local.CacheDatabase
import com.example.gr34_in2000_v21.data.local.PersistentDatabase
import com.example.gr34_in2000_v21.data.models.DataResult
import com.example.gr34_in2000_v21.data.models.GeoNorgeModel
import com.example.gr34_in2000_v21.data.models.MetAlertsModel
import com.example.gr34_in2000_v21.databinding.SearchFragmentBinding
import com.example.gr34_in2000_v21.ui.SharedViewModel
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import okhttp3.internal.immutableListOf
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/*
    Resources:
    Google Maps
    https://www.youtube.com/watch?v=YCFPClPjDIQ
    https://developers.google.com/maps/documentation/android-sdk/marker
    https://www.geeksforgeeks.org/how-to-add-custom-marker-to-google-maps-in-android/

    Expandable RecyclerView Cards
    https://www.youtube.com/watch?v=HQX0WHJm7BI

    Searchable Spinner
    https://www.youtube.com/watch?v=JtAiHBbaxbw

    API for getting Norway's counties
    https://ws.geonorge.no/kommuneinfo/v1/#/default/get_kommuner
    https://ws.geonorge.no/kommuneinfo/v1/kommuner

    API for information about specific counties in Norway
    https://ws.geonorge.no/kommuneinfo/v1/#/default/get_kommuner__kommunenummer_

    API for finding a geographical point's county
    https://ws.geonorge.no/kommuneinfo/v1/#/default/get_punkt
 */

@AndroidEntryPoint
class SearchFragment : Fragment(), OnMapReadyCallback {

    @Inject
    lateinit var cache: CacheDatabase

    @Inject
    lateinit var persist: PersistentDatabase

    //Fragment
    private lateinit var binding: SearchFragmentBinding

    //ViewModel
    private val viewModel: SharedViewModel by activityViewModels()

    var darkMode = false

    /* Collection of lists */
    private var malAlertItems: List<MetAlertsModel.ItemCapJoin> = emptyList()
    var expList = mutableListOf<Boolean>()
    private var response = mutableListOf<GeoNorgeModel.County>()
    private lateinit var responseCountyCoordinates: GeoNorgeSearchResult
    private var responseCountyCoordinatesList = mutableListOf<GeoNorgeModel.CountyCoordinates>()
    private var fishingBank = mutableListOf<MetAlertsModel.ItemCapJoin>()
    val favoritesList = mutableListOf<GeoNorgeModel.CountyCoordinates>()

    /* Views */
    private lateinit var spinner: Spinner

    //Google maps
    private var mapMarker: Marker? = null
    private var map: SupportMapFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        darkMode = sharedPref.getBoolean("switchValue2", false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return if (::binding.isInitialized) {
            binding.root
        } else {
            binding = SearchFragmentBinding.inflate(inflater, container, false)
            with(binding) {
                map = childFragmentManager.findFragmentById(R.id.google_map) as? SupportMapFragment
                map?.getMapAsync(this@SearchFragment)

                //Checking if dark mode (night mode) is turned on to decide which color scheme to use
                if (darkMode) {
                    sheet.background = ResourcesCompat.getDrawable(resources, R.drawable.rounded_dark, null)
                    binding.searchSpinner.setBackgroundColor(Color.parseColor("#000000"))
                } else {
                    sheet.background = ResourcesCompat.getDrawable(resources, R.drawable.rounded, null)
                }

                root
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBarSearch.visibility = View.VISIBLE

        initializeMalAlertItems()
        checkIfNoAlerts()
        menuClickListener()
        bottomSheetInitialiser()
        sortingSpinnerListener()
        getAllCounties()

    }

    private fun bottomSheetInitialiser() {
        //Bottom-sheet that shows a recyclerView with alerts as cardViews
        val sheet = requireView().findViewById<View>(R.id.sheet)

        //Setting the state of the bottom sheet as collapsed and peekHeight to
        //300, meaning that the user only sees the top of the bottom sheet and has to
        //swipe up to see the rest as it is collapsed
        BottomSheetBehavior.from(sheet).apply {
            peekHeight = 330
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    /*
        Function that takes care of when the user clicks on an item
        in the toolbar at the top of the map, consisting of a favorite button
        and a spinner with filters for alerts
     */
    private fun menuClickListener() {
        binding.toolbar2.setOnMenuItemClickListener {
            when (it.itemId) {
                //If the county was favorited and the user now un-favorites it
                R.id.favoriteButton -> {
                    binding.toolbar2.menu.findItem(R.id.favoriteButton).isVisible = false
                    binding.toolbar2.menu.findItem(R.id.favoriteNotButton).isVisible = true

                    val itemAsString = spinner.selectedItem.toString()
                    Toast.makeText(requireContext(), "$itemAsString fjernet som favoritt i hjemmeside", Toast.LENGTH_SHORT).show()

                    if (spinner.selectedItem != null) {
                        removeFromFavorites(itemAsString)
                    }
                    true
                }
                //If the county was not favorites and the user now wants to favorite it
                R.id.favoriteNotButton -> {
                    val itemAsString = spinner.selectedItem.toString()

                    binding.toolbar2.menu.findItem(R.id.favoriteButton).isVisible = true
                    binding.toolbar2.menu.findItem(R.id.favoriteNotButton).isVisible = false

                    Toast.makeText(requireContext(), "$itemAsString lagt til som favoritt i hjemmeside", Toast.LENGTH_SHORT).show()

                    //Get current selected item in spinner and add to list of favorites
                    if (spinner.selectedItem != null) {
                        addToFavorites(itemAsString)
                    }
                    true
                }
                R.id.filterButton -> {
                    binding.sortMapSpinner.performClick()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun filterOutTheList(list: List<MetAlertsModel.ItemCapJoin>): List<MetAlertsModel.ItemCapJoin> {
        return list.toSet().toList()
    }

    fun sortingSpinnerListener() {
        /*
            Spinner that shows options for what kind of danger type
            you want to filter the map and bottom-sheet for
         */
        val sort = immutableListOf(
            "Vis alt",
            "Snøfokk",
            "Skogbrannfare",
            "Kuling",
            "Is",
            "Polart lavtrykk",
            "Nedbør",
            "Snø",
            "Stormflo",
            "Vind"
        )

        binding.sortMapSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireContext(), "Nothing selected", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                parent?.let {
                    val tv = it.getChildAt(0) as? TextView
                    tv?.text = ""
                    filterSpinnerOption(it.getItemAtPosition(position).toString())
                }
            }
        }

        val adapter = context?.let {
            ArrayAdapter(it.applicationContext, R.layout.spinner_layout_item, sort)
        }
        binding.sortMapSpinner.adapter = adapter
    }

    //Function that gets all alerts from the database and puts them in the list malAlertItems
    fun initializeMalAlertItems() {
        viewModel.allItemsLiveData.observe(viewLifecycleOwner) { res ->
            when (res.status) {
                DataResult.Status.SUCCESS -> {
                    binding.progressBarSearch.visibility = View.GONE
                    if (!res.data.isNullOrEmpty()) {
                        malAlertItems = filterOutTheList(res.data)
                        for (m in malAlertItems) {
                            m.expanded = false
                        }

                        //Default when opening app: show all alerts available on map and in the recyclerView
                        mapAsync("Vis alt")
                        //Updating the adapter and therefore the RecyclerView
                        updateAdapter(malAlertItems)
                    } else {
                        Toast.makeText(requireContext(), "Oops.", Toast.LENGTH_SHORT).show()
                    }
                }

                DataResult.Status.ERROR -> {
                    binding.progressBarSearch.visibility = View.GONE
                    Toast.makeText(requireContext(), res.message?.msg, Toast.LENGTH_SHORT).show()
                }

                DataResult.Status.LOADING -> {
                }
            }
        }
    }

    //Updates the adapter for the recyclerView that shows cards with danger alerts
    private fun updateAdapter(list: List<MetAlertsModel.ItemCapJoin>) {
        CoroutineScope(Dispatchers.IO).launch {
            activity?.runOnUiThread {
                val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)
                val adp = ListRecyclerViewAdapter(list)
                recyclerView?.adapter = adp
                adp.notifyDataSetChanged()
            }
        }
    }

    //Function that checks whether there are no alerts available at the time
    private fun checkIfNoAlerts() {
        viewModel.lastCode.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.progressBarSearch.visibility = View.GONE
                Toast.makeText(requireContext(), "Ingen farevarsler for i dag! Kode: $it", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*
        Function that according to the option chosen in the filtering
        spinner adds icons to the map and alerts to the recyclerView,
        only showing those relevant to the option chosen
     */
    private fun filterSpinnerOption(itemSelected: String) {
        //Clear the map of previous icons first
        map?.getMapAsync {
            it.clear()
        }

        mapAsync(itemSelected)
        //Showing alerts in the recyclerView according to
        //the option chosen in the spinner
        if (itemSelected == "Vis alt") {
            updateAdapter(malAlertItems)
        } else {
            val filteredAlerts = mutableListOf<MetAlertsModel.ItemCapJoin>()
            //Checking for each alert if its event matches the one chosen in
            //the sort spinner and adds accordingly to the new list that the
            //adapter for the recyclerView will receive
            for (alert in malAlertItems) {
                val event = alert.info?.get(0)?.event
                if (event == itemSelected) {
                    filteredAlerts.add(alert)
                }
            }
            updateAdapter(filteredAlerts)
        }

    }

    /*
        Function that adds icons to the map according to the danger type
        chosen in the filtering spinner
        This function will also be called to show all icons whenever a county,
        "Fiskebanker" or "Norge" is chosen from the locations spinner
     */
    private fun mapAsync(dangerType: String) {
        CoroutineScope(Dispatchers.IO).launch {
            activity?.runOnUiThread {
                map?.getMapAsync { googleMap ->
                    //Looping through all CAPAlerts and adding icons to map according to the alert's event
                    //and danger level
                    for ((position, alert) in malAlertItems.withIndex()) {
                        val event = alert.info?.get(0)?.event
                        //Will only add icons where the event equals the danger type given as argument
                        //or if the danger type equals "Vis alt" (show all) and therefore will add all icons
                        if (event == dangerType || dangerType == "Vis alt") {
                            val res = findMatchingIconResource(position)

                            //Getting a position for the icon to be placed based on the first latitude and longitude
                            //in the polygon variable for the alert
                            val splitSpace = alert.info?.get(0)?.area?.polygon?.split(" ")?.toTypedArray()
                            val splitComma = splitSpace?.get(0)?.split(",")?.toTypedArray()

                            val latitude = splitComma?.get(0)?.toDouble()
                            val longitude = splitComma?.get(1)?.toDouble()

                            if (res != null) {
                                //Creating a bitmap for the icon and setting its width and height
                                val bitmap: Bitmap = BitmapFactory.decodeResource(resources, res)
                                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false)

                                //Moving the camera to a position on the map that shows Norway
                                val norway = LatLng(64.5783, 17.8882)
                                map?.getMapAsync {
                                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(norway, 4F))
                                }

                                //Adding danger icon to map
                                if (latitude != null && longitude != null) {
                                    val pos = LatLng(latitude, longitude)
                                    googleMap.addMarker(
                                        MarkerOptions().position(pos)
                                            .title(alert.info?.get(0)?.event + " - " + alert.info?.get(0)?.area?.areaDesc)
                                            .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))
                                    )
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    /*
        Function that with the parameter position finds the resource int of the icon
        associated with the alert in malAlertItems on the position given
     */
    private fun findMatchingIconResource(position: Int): Int? {
        var res: Int? = null
        if ((!malAlertItems.isNullOrEmpty())) {
            var event =
                malAlertItems[position].info?.get(0)?.eventCode?.value?.lowercase(Locale.ROOT)
            var dangerLevel = malAlertItems[position].info?.get(0)?.severity

            //Certain events don't have an event name matching an icon, so if the event is "blowingsnow" or "icing",
            //their icon will be the one associated with the event "snow", and if the event is "gale", it will have
            //the "wind" icon
            if (event == "blowingsnow" || event == "icing") {
                event = "snow"
            } else if (event == "gale") {
                event = "wind"
            }

            //Change the danger level to the color associated with it
            dangerLevel = when (dangerLevel) {
                "Moderate" -> "yellow"
                "Severe" -> "red"
                else -> {
                    "orange"
                }
            }

            //String representing the resource with wanted icon
            val drawableRes = "icon_warning_" + event.toString() + "_" + dangerLevel.toString()
            //Get resource id
            res = resources.getIdentifier(drawableRes, "drawable", activity?.packageName)
        }

        return res
    }

    /*
        Function that zooms in on the map according to given
        latitude (lat) and longitude (lng)
     */
    private fun zoomInOnLocation(lat: Double, lng: Double) {
        val loc = LatLng(lat, lng)

        map?.getMapAsync {
            if (mapMarker != null) {
                mapMarker?.remove()
            }
            mapMarker = it.addMarker(
                MarkerOptions().position(loc)
            )
            it.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 10F))
        }
    }

    /*
        Function that gets the name and county number of all counties in
        Norway from an API
     */
    private fun getAllCounties() {
        spinner = requireView().findViewById(R.id.searchSpinner)
        val path = "https://ws.geonorge.no/kommuneinfo/v1/kommuner"
        val gson = Gson()

        suspend fun getAPI() {
            try {
                response = gson.fromJson(Fuel.get(path).awaitString(), Array<GeoNorgeModel.County>::class.java).toMutableList()
            } catch (exception: Exception) {
                exception.message?.let { Timber.e("getAllCountries: $it") }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val dbContent = persist.geonorgeDao().getAll()
            val spinnerResponse = mutableListOf<String?>()
            if (dbContent.isNullOrEmpty()) {
                getAPI()
                for (county in response) {
                    spinnerResponse.add(county.kommunenavn)
                    findCountyCoordinates(county)
                }
            } else {
                for (county in dbContent) {
                    spinnerResponse.add(county.kommunenavn)
                    responseCountyCoordinatesList.add(county)
                }
            }
            spinnerResponse.add(0, "Norge")
            spinnerResponse.add(1, "Fiskebanker")
            activity?.runOnUiThread {
                val adapter = context?.let {
                    ArrayAdapter(it.applicationContext, R.layout.spinner_layout_item, spinnerResponse)
                }
                spinner.adapter = adapter
            }
        }

        countySpinner()
    }

    /*
        Function that listens on whether a county has been chosen in the searchable spinner
     */
    private fun countySpinner() {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireContext(), "Vennligst velg en lokasjon!", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //Checking if night mode is turned on in order to change the text color to white
                if (darkMode) {
                    (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                }

                (parent!!.getChildAt(0) as TextView).typeface = Typeface.DEFAULT_BOLD

                //If the object chosen is "Norge"
                if (position == 0) {
                    alertsWithinArea(parent.getItemAtPosition(position).toString())
                    // Disable favorite button for "Norge", not a county
                    binding.toolbar2.menu.findItem(R.id.favoriteButton).isVisible = false
                    binding.toolbar2.menu.findItem(R.id.favoriteNotButton).isVisible = true
                    binding.toolbar2.menu.findItem(R.id.favoriteNotButton).isEnabled = false
                }
                else {
                    if (position == 1) {
                        // Disable favorite button for "Fiskebanker", not a county
                        binding.toolbar2.menu.findItem(R.id.favoriteButton).isVisible = false
                        binding.toolbar2.menu.findItem(R.id.favoriteNotButton).isVisible = true
                        binding.toolbar2.menu.findItem(R.id.favoriteNotButton).isEnabled = false
                    } else {
                        binding.toolbar2.menu.findItem(R.id.favoriteNotButton).isEnabled = true
                    }
                    val toast = "Du valgte: " + parent.getItemAtPosition(position).toString()
                    Toast.makeText(requireContext(), toast, Toast.LENGTH_SHORT).show()

                    val locationCoordinates = responseCountyCoordinatesList.find {
                        it.kommunenavn == parent.getItemAtPosition(position).toString()
                    }

                    //Checking whether the county chosen has been favorited or not and showing
                    //the right heart icon accordingly
                    if (locationCoordinates != null) {
                        if (locationCoordinates.isFavorite) {
                            binding.toolbar2.menu.findItem(R.id.favoriteButton).isVisible = true
                            binding.toolbar2.menu.findItem(R.id.favoriteNotButton).isVisible = false
                        } else {
                            binding.toolbar2.menu.findItem(R.id.favoriteButton).isVisible = false
                            binding.toolbar2.menu.findItem(R.id.favoriteNotButton).isVisible = true
                        }
                    }

                    //Showing alerts within area for the county chosen
                    alertsWithinArea(parent.getItemAtPosition(position).toString())
                }

            }
        }
    }

    data class GeoNorgeSearchResult(val kommuner: List<GeoNorgeModel.CountyCoordinates>)

    /*
        Function that finds the coordinates of a county from an API
     */
    private fun findCountyCoordinates(county: GeoNorgeModel.County) {
        //Not every county has a county number
        val countyName: String? = county.kommunenavn
        val path = "https://ws.geonorge.no/kommuneinfo/v1/sok?knavn=$countyName"
        val gson = Gson()

        suspend fun getAPI() {
            try {
                responseCountyCoordinates = gson.fromJson(Fuel.get(path).awaitString(), GeoNorgeSearchResult::class.java)
                responseCountyCoordinatesList.add(responseCountyCoordinates.kommuner[0])
            } catch (exception: Exception) {
                exception.message?.let { Timber.e("findCountyCoordinates: $it") }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            getAPI()
        }
    }

    /*
        Function that adds a CountyCoordinates object as a favorite
     */
    fun addToFavorites(countyName: String) {
        for (county in responseCountyCoordinatesList) {
            if (county.kommunenavn == countyName) {
                county.isFavorite = true
                lifecycleScope.launch { viewModel.updateFavorite(county) }
                favoritesList.add(county)
                viewModel.favoritesList.add(county)
                break
            }
        }
    }

    /*
        Function that removes a county object as a favorite
     */
    fun removeFromFavorites(countyName: String) {
        for (county in responseCountyCoordinatesList) {
            if (county.kommunenavn == countyName) {
                county.isFavorite = false
                lifecycleScope.launch { viewModel.updateFavorite(county) }
                favoritesList.remove(county)
                //HENRIK: Adding to list in SharedViewModel as well
                viewModel.favoritesList.remove(county)
                break
            }
        }
    }

    /*
        Function that finds all alerts in the area given as argument,
        so either all alerts related to a county, "Fiskebanker" or
        all alerts if the argument is "Norge"
     */
    fun alertsWithinArea(countyName: String) {
        if (countyName == "Norge") {
            mapAsync("Vis alt")
            updateAdapter(malAlertItems)
        } else if (countyName == "Fiskebanker") {
            findFishingBanks()

            //Zoom to show the ocean
            val loc = LatLng(65.2800, 6.450)

            map?.getMapAsync {
                if (mapMarker != null) {
                    mapMarker?.remove()
                }
                mapMarker = it.addMarker(MarkerOptions().position(loc))
                it.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 4F))
            }

            updateAdapter(fishingBank.toSet().toList().sortedByDescending { it.pubDate }.distinctBy { it.identifier })
        } else {
            var latitudeCounty: Double? = null
            var longitudeCounty: Double? = null

            //Find latitude and longitude of county
            for (county in responseCountyCoordinatesList) {
                if (county.kommunenavn == countyName) {
                    latitudeCounty = county.punktIOmrade.coordinates[1]
                    longitudeCounty = county.punktIOmrade.coordinates[0]
                    break
                }
            }

            //Zoom in on county
            if (latitudeCounty != null && longitudeCounty != null) {
                zoomInOnLocation(latitudeCounty, longitudeCounty)
            }

            val countyAlerts = mutableListOf<MetAlertsModel.ItemCapJoin>()
            var countyNumber = ""

            //Finding county number
            for (county in responseCountyCoordinatesList) {
                if (county.kommunenavnNorsk == countyName) {
                    countyNumber = county.kommunenummer ?: ""
                }
            }

            var geocodeList: MutableList<String>

            for (alert in malAlertItems) {
                if (alert.info?.get(0)?.area?.geocode != null) {
                    for (geocode in alert.info?.get(0)?.area?.geocode!!) {
                        if (geocode.valueName == "MunicipalityId") {
                            geocodeList =
                                geocode.value?.split(";")?.toTypedArray()!!.toMutableList()
                            if (geocodeList.contains(countyNumber)) {
                                countyAlerts.add(alert)
                            }
                        }

                    }
                }
            }

            updateAdapter(countyAlerts.toSet().toList().sortedByDescending { it.pubDate }.distinctBy { it.identifier })

        }

    }

    /*
        Function that finds all alerts whose geographic domain
        is marine and adds them to the list fishingBank
     */
    private fun findFishingBanks() {
        for (alert in malAlertItems) {
            for (p in alert.info?.get(0)?.parameter!!) {
                if (p.valueName == "geographicDomain" && p.value == "marine") {
                    fishingBank.add(alert)
                }
            }
        }
    }

    override fun onStop() {
        GlobalScope.launch(Dispatchers.IO) {
            persist.geonorgeDao().insertAll(responseCountyCoordinatesList)
        }
        super.onStop()
    }

    override fun onMapReady(p0: GoogleMap) {
        val style = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.dark_map)
        if (darkMode)
            p0.setMapStyle(style)
    }
}