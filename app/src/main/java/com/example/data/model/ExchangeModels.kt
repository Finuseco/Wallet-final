package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExchangeRequest(
    @Json(name = "amount") val amount: Double,
    @Json(name = "fromCurrency") val fromCurrency: String,
    @Json(name = "toCurrency") val toCurrency: String,
    @Json(name = "pin") val pin: String
)

@JsonClass(generateAdapter = true)
data class ExchangeResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "transactionId") val transactionId: Long? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "fromCurrency") val fromCurrency: String? = null,
    @Json(name = "toCurrency") val toCurrency: String? = null,
    @Json(name = "rate") val rate: Double? = null,
    @Json(name = "toAmount") val toAmount: Double? = null,
    @Json(name = "fee") val fee: Double? = 0.0,
    @Json(name = "senderNewBalance") val senderNewBalance: Double? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)
