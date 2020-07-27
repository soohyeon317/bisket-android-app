package com.bisket.dto.ReverseGeoCode

data class ReverseGeoCodeResult(
    var name: ReverseGeoCodeResultName? = null,
    var region: Region? = null,
    var land: Land? = null
)

enum class ReverseGeoCodeResultName {
    legalcode, admcode, addr, roadaddr
}