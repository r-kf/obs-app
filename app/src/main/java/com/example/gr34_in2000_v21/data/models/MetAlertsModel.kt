package com.example.gr34_in2000_v21.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/***
 * TENK ALPACAPARTY
 * Hvis det er XML vi henter fra, sørg for at @Xml er med.
 *
 * For enhver element som skal inn i databasen, sørg for at @Entity(tablename="navn på elementet") er med
 * XML dokumentasjon: https://github.com/Tickaroo/tikxml/blob/master/docs/AnnotatingModelClasses.md
 */


object MetAlertsModel {

    data class ItemCapJoin(
        var title: String = "",
        var description: String = "",
        var link: String = "",
        var author: String = "",
        var category: String = "",
        @PrimaryKey var guid: String = "",
        var pubDate: String = "",
        var identifier: String = "",
        var sender: String = "",
        var sent: String = "",
        var status: String = "",
        var msgType: String = "",
        var scope: String = "",
        var code: String = "",
        var info: List<CAPInfo>? = null,
        @Ignore var expanded: Boolean = false
    )


    @Xml
    data class RSS(
        @Attribute
        var version: String? = null,
        @Element
        var channel: Channel? = null
    )

    @Xml
    data class Channel(
        @PropertyElement
        var title: String = "",
        @Element
        var items: List<Item>? = null
    )

    @Entity(tableName = "items", indices = [Index(value = ["link", "pubDate"], unique = true)])
    @Xml
    data class Item(
        @PropertyElement
        var title: String = "",
        @PropertyElement
        var description: String = "",
        @PropertyElement
        var link: String = "",
        @PropertyElement
        var author: String = "",
        @PropertyElement
        var category: String = "",
        @PropertyElement
        @PrimaryKey var guid: String = "",
        @PropertyElement
        var pubDate: String = "",
    )

    @Entity(tableName = "cap", indices = [Index(value = ["identifier", "sender"], unique = true)])
    @Xml(name = "alert")
    data class CAPAlert(
        @PropertyElement
        @field:PrimaryKey var identifier: String = "",
        @PropertyElement
        var sender: String = "",
        @PropertyElement
        var sent: String = "",
        @PropertyElement
        var status: String = "",
        @PropertyElement
        var msgType: String = "",
        @PropertyElement
        var scope: String = "",
        @PropertyElement
        var code: String = "",
        @Element
        var info: List<CAPInfo>? = null,
    )

    @Xml(name = "info")
    data class CAPInfo(
        @PropertyElement
        var language: String = "",
        @PropertyElement
        var category: String = "",
        @PropertyElement
        var event: String = "",
        @PropertyElement
        var responseType: String = "",
        @PropertyElement
        var urgency: String = "",
        @PropertyElement
        var severity: String = "",
        @PropertyElement
        var certainty: String = "",
        @Element
        var eventCode: EventCode,
        @PropertyElement
        var effective: String = "",
        @PropertyElement
        var onset: String = "",
        @PropertyElement
        var expires: String = "",
        @PropertyElement
        var senderName: String = "",
        @PropertyElement
        var headline: String = "",
        @PropertyElement
        var description: String = "",
        @PropertyElement
        var instruction: String = "",
        @PropertyElement
        var web: String = "",
        @PropertyElement
        var contact: String = "",
        @Element
        var parameter: List<Parameter>,
        @Element
        var resource: Resource?,
        @Element
        var area: Area
    )

    @Xml(name = "area")
    data class Area(
        @PropertyElement
        var areaDesc: String = "",
        @PropertyElement
        var polygon: String = "",
        @Element
        var geocode: List<Geocode>?,
        @PropertyElement
        var altitude: String = "",
        @PropertyElement
        var ceiling: String = ""
    )

    @Xml(name = "geocode")
    data class Geocode(
        @PropertyElement
        var valueName: String? = null,
        @PropertyElement
        var value: String? = null
    )

    @Xml(name = "parameter")
    data class Parameter(
        @PropertyElement
        var valueName: String? = null,
        @PropertyElement
        var value: String? = null
    )

    @Xml(name = "resource")
    data class Resource(
        @PropertyElement
        var resourceDesc: String? = null,
        @PropertyElement
        var mimeType: String? = null,
        @PropertyElement
        var uri: String? = null
    )

    @Xml(name = "eventCode")
    data class EventCode(
        @PropertyElement
        var valueName: String? = null,
        @PropertyElement
        var value: String? = null,
    )
}