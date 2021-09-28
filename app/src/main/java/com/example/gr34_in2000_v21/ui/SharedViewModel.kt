package com.example.gr34_in2000_v21.ui

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.*
import com.example.gr34_in2000_v21.data.local.PersistentDatabase
import com.example.gr34_in2000_v21.data.models.DataResult
import com.example.gr34_in2000_v21.data.models.GeoNorgeModel
import com.example.gr34_in2000_v21.data.models.MetAlertsModel
import com.example.gr34_in2000_v21.data.repository.GeoNorgeRepository
import com.example.gr34_in2000_v21.data.repository.MetAlertsRepository
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val metAlertsRepository: MetAlertsRepository,
    private val locationProviderClient: FusedLocationProviderClient,
    private val geoNorgeRepository: GeoNorgeRepository,
    persistentDatabase: PersistentDatabase,
    app: Application
) : AndroidViewModel(app) {
    private val _db = metAlertsRepository.getAllDbItems()
    private val _geoNorge = persistentDatabase.geonorgeDao()
    val allItemsLiveData = MediatorLiveData<DataResult<List<MetAlertsModel.ItemCapJoin>>>()

    private val areAllLoaded = MutableLiveData(false)
    var lastCode = MutableLiveData<Int?>(null)

    private val counties: LiveData<List<GeoNorgeModel.CountyCoordinates>>
    val favorites: LiveData<List<GeoNorgeModel.CountyCoordinates>>

    //Shared Favourites list
    val favoritesList = mutableListOf<GeoNorgeModel.CountyCoordinates>()

    fun getAllItems() = viewModelScope.launch(Dispatchers.IO) {
        val result = metAlertsRepository.getAllNetworkItems()
        when (result.status) {
            DataResult.Status.SUCCESS -> {
                result.data!!.let {
                    Timber.d("SharedViewModel: $it")
                    Timber.d("SharedViewModel length: ${it.size}")
                    metAlertsRepository.saveItems(it)
                }
                areAllLoaded.postValue(true)
                lastCode.postValue(null)
            }
            DataResult.Status.ERROR -> {
                Timber.d("${result.message?.code} ${result.message?.code}")
                lastCode.postValue(result.message!!.code)
            }
            else -> {
                Timber.d("Getting all items...")
            }
        }
    }

    init {
        allItemsLiveData.addSource(_db) {
            allItemsLiveData.value =
                if (it.isEmpty()) DataResult.loading() else DataResult.success(it)
        }
        favorites = _geoNorge.allFavorites
        counties = _geoNorge.all
    }

    fun updateFavorite(county: GeoNorgeModel.CountyCoordinates) = viewModelScope.launch {
        Timber.d("UPDATING FAVORITE $county")
        _geoNorge.update(county)
    }

    fun getCountyFromCoordinates(lat: Double, lon: Double) =
        geoNorgeRepository.getNetworkPunkt(lat, lon)


    fun getItemsFromLatLonWithCap(lat: Double, lon: Double, show: String? = null) =
        metAlertsRepository.getLatLonWithCap(lat, lon, show)

    // old code. kept for bakcompat
    val items: LiveData<DataResult<List<MetAlertsModel.Item>?>> = metAlertsRepository.getAllItems()
    /*
    fun getItemsFromLatLon(lat: Double, lon: Double, show: String? = null) =
        metAlertsRepository.getLatLon(lat, lon, show)
    */
    fun cap(guid: String): LiveData<DataResult<MetAlertsModel.CAPAlert>> =
        metAlertsRepository.getCap(guid)


    private val _userLocation: MutableLiveData<Location?> =
        MutableLiveData<Location?>(null)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(): MutableLiveData<Location?> {
        viewModelScope.launch {
            locationProviderClient.lastLocation.addOnSuccessListener {
                _userLocation.postValue(it)
            }
        }
        return _userLocation
    }
}