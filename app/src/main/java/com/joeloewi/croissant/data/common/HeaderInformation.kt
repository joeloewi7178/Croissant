package com.joeloewi.croissant.data.common

enum class HeaderInformation(
    val saltKey: String = "",
    val xRpcAppVersion: String = "",
    val xRpcClientType: String = ""
) {
    OS(
        saltKey = "6cqshh5dhw73bzxn20oexa9k516chk7s",
        xRpcAppVersion = "1.5.0",
        xRpcClientType = "4"
    ),
    CN(
        saltKey = "xV8v4Qu54lUKrEYFZkJhB8cuOh9Asafs",
        xRpcAppVersion = "2.11.1",
        xRpcClientType = "5"
    );
}