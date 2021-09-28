package com.example.gr34_in2000_v21.data.repository

import com.example.gr34_in2000_v21.data.local.CacheDatabase
import com.example.gr34_in2000_v21.data.models.DataResult
import com.example.gr34_in2000_v21.data.models.MetAlertsModel
import com.example.gr34_in2000_v21.data.remote.MetAlertsDataSource
import com.example.gr34_in2000_v21.utils.Helpers
import com.example.gr34_in2000_v21.utils.Messages
import timber.log.Timber
import javax.inject.Inject

/**
 * Lag en repository klasse for hver APIer vi ønsker å bruke
 */

class MetAlertsRepository @Inject constructor(
    private val remote: MetAlertsDataSource,
    private val database: CacheDatabase
) : BaseRepository() {

    // new calls.
    fun getAllDbItems() = database.metalertsDao().getAllItems()

    //suspend fun getAllNetworkPureItems() = remote.getAllItems()
    //suspend fun getNetworkCap(guid: String) = remote.getCap(guid)
    suspend fun getAllNetworkItems() = chainItemsAndCaps(remote.getAllItems())
    fun getLatLonWithCap(lat: Double, lon: Double, show: String? = null) =
        makeRequest { chainItemsAndCaps(remote.getLatLon(lat, lon, show)) }

    suspend fun saveItems(items: List<MetAlertsModel.ItemCapJoin>) {
        val pureItems = mutableListOf<MetAlertsModel.Item>()
        val caps = mutableListOf<MetAlertsModel.CAPAlert>()
        for (i in items) {
            pureItems.add(
                MetAlertsModel.Item(
                    i.title,
                    i.description,
                    i.link,
                    i.author,
                    i.category,
                    i.guid,
                    i.pubDate
                )
            )
            caps.add(
                MetAlertsModel.CAPAlert(
                    i.identifier,
                    i.sender,
                    i.sent,
                    i.status,
                    i.msgType,
                    i.scope,
                    i.code,
                    i.info
                )
            )
        }
        Timber.d("PURE ITEMS IN REPO: ${pureItems.size}")
        Timber.d("PURE CAPS IN REPO: ${caps.size}")
        return database.metalertsDao().saveItems(pureItems, caps)
    }

    private suspend fun chainItemsAndCaps(items: DataResult<MetAlertsModel.RSS>): DataResult<List<MetAlertsModel.ItemCapJoin>> {
        return if (items.status == DataResult.Status.SUCCESS) {
            val rss = items.data!!
            val fromRSS = Helpers.MetAlerts.rss2List(rss)
            if (fromRSS != null) {
                Timber.d("TOTAL ITEM LENGTH: ${fromRSS.size}")
                val ret = mutableListOf<MetAlertsModel.ItemCapJoin>()
                for (i in fromRSS) {
                    checkCap(remote.getCap(i.guid)).let {
                        if (it.status == DataResult.Status.SUCCESS)
                            ret.add(
                                MetAlertsModel.ItemCapJoin(
                                    i.title,
                                    i.description,
                                    i.link,
                                    i.author,
                                    i.category,
                                    i.guid,
                                    i.pubDate,
                                    it.data!!.identifier,
                                    it.data.sender,
                                    it.data.sent,
                                    it.data.status,
                                    it.data.msgType,
                                    it.data.scope,
                                    it.data.code,
                                    it.data.info,
                                )
                            )
                    }
                }
                Timber.d("TOTAL ITEM LENGTH AFTER CAPPING: ${ret.size}")
                DataResult.success(ret as List<MetAlertsModel.ItemCapJoin>)
            } else {
                DataResult.error(Messages.Message(20, "No items"))
            }
        } else {
            DataResult.error(Messages.Message(10, "Oops."))
        }
    }

    private fun checkCap(cap: DataResult<MetAlertsModel.CAPAlert>): DataResult<MetAlertsModel.CAPAlert> {
        return if (cap.status == DataResult.Status.SUCCESS) {
            DataResult.success(cap.data!!)
        } else {
            DataResult.error(Messages.Message(30, "Oops."))
        }
    }

    private fun extractListFromRSS(items: DataResult<MetAlertsModel.RSS>): DataResult<List<MetAlertsModel.Item>?> {
        return if (items.status == DataResult.Status.SUCCESS) {
            DataResult.success(Helpers.MetAlerts.rss2List(items.data!!))
        } else {
            DataResult.error(Messages.Message(40, "Oops."))
        }
    }

    // old calls. kept for bakcompat
    fun getAllItems() = makeRequest { extractListFromRSS(remote.getAllItems()) }

    /*fun getCounty(county: String, show: String? = null) =
        makeRequest { remote.getCountyItems(county, show) }*/

    fun getLatLon(lat: Double, lon: Double, show: String? = null) =
        makeRequest { remote.getLatLon(lat, lon, show) }


    fun getCap(guid: String) = makeRequest { remote.getCap(guid) }
}