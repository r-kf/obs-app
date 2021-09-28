package com.example.gr34_in2000_v21

import com.example.gr34_in2000_v21.data.models.MetAlertsModel
import com.example.gr34_in2000_v21.ui.views.search.ListRecyclerViewAdapter
import org.junit.Assert.assertEquals
import org.junit.Test

/*
    Resources:
    https://developer.android.com/training/testing/unit-testing/local-unit-tests
    https://junit.org/junit4/javadoc/latest/org/junit/Assert.html
*/

//@RunWith(AndroidJUnit4::class)
//Local unit tests
class SearchUnitTest {

    // mock alerts
    private val mocks = listOf(
        MetAlertsModel.ItemCapJoin("Varsel 1",
        "Desc 1",
        "",
        "",
        "marine",
        "1",
        "1",
        "1",
        "",
        "",
        "active",
        "",
        "",
        "",
            listOf(MetAlertsModel.CAPInfo(
                "", "", "", "", "", "", "", MetAlertsModel.EventCode(null, null),
                "", "", "", "", "", "", "", "", "", listOf(), null, MetAlertsModel.Area("","", listOf(MetAlertsModel.Geocode(null, null)), "")
            ))),
        MetAlertsModel.ItemCapJoin("Varsel 2",
            "Desc 2",
            "",
            "",
            "land",
            "2",
            "2",
            "2",
            "",
            "",
            "active",
            "",
            "",
            "",
            listOf(MetAlertsModel.CAPInfo(
                "", "", "", "", "", "", "", MetAlertsModel.EventCode(null, null),
                "", "", "", "", "", "", "", "", "", listOf(
                    MetAlertsModel.Parameter("geographicDomain", "marine")
                ), null, MetAlertsModel.Area("","", listOf(MetAlertsModel.Geocode(null, null)), "")
            ))),
        MetAlertsModel.ItemCapJoin("Varsel 3",
            "Desc 3",
            "",
            "",
            "???",
            "3",
            "3",
            "3",
            "",
            "",
            "active",
            "",
            "",
            "",
            listOf(MetAlertsModel.CAPInfo(
                "", "", "", "", "", "", "", MetAlertsModel.EventCode(null, null),
                "", "", "", "", "", "", "", "", "", listOf(
                    MetAlertsModel.Parameter("geographicDomain", "land")
                ), null, MetAlertsModel.Area("","", listOf(MetAlertsModel.Geocode(null, null)), "")
            )))
    )

    // test if recyclerview gets correct amount of alerts
    @Test
    fun recycleViewAmountTest() {
        val adapter = ListRecyclerViewAdapter(mocks)

        assertEquals(3, adapter.itemCount) // successful if same amount as mock
    }

    // test if recyclerview can decipher fishingbanks
    @Test
    fun recycleViewFishingBanksTest() {
        // replica of SearchFragment.findFishingBanks()
        val fishingBanks = mutableListOf<MetAlertsModel.ItemCapJoin>()
        for (alert in mocks) {
            for (p in alert.info?.get(0)?.parameter!!) {
                if (p.valueName == "geographicDomain" && p.value == "marine") {
                    fishingBanks.add(alert)
                }
            }
        }
        val adapter = ListRecyclerViewAdapter(fishingBanks)

        assertEquals(1, adapter.itemCount) // successful if there's only one
    }
}