package com.example.gr34_in2000_v21

import com.example.gr34_in2000_v21.ui.views.info.InfoViewModel
import com.example.gr34_in2000_v21.ui.views.info.adapter.InfoCardAdapter
import org.junit.Test
import org.junit.Assert.*

/*
    https://developer.android.com/training/testing/unit-testing/local-unit-tests
    https://junit.org/junit4/javadoc/latest/org/junit/Assert.html
*/

class InfoUnitTest {
    @Test
    fun adapterTest() {
        val vm = InfoViewModel()
        val adapter = InfoCardAdapter(vm.infoList)

        assertEquals(3, adapter.itemCount) // check if 3 items show up in adapter
    }
}