package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InternationalTransferClaimDto(
    @Json(name = "id") val id: Long,
    @Json(name = "provider") val provider: String,
    @Json(name = "trackingNumber") val trackingNumber: String,
    @Json(name = "expectedAmount") val expectedAmount: Double,
    @Json(name = "expectedCurrency") val expectedCurrency: String,
    @Json(name = "senderCountry") val senderCountry: String,
    @Json(name = "receiveInCurrency") val receiveInCurrency: String,
    @Json(name = "status") val status: String,
    @Json(name = "userDocumentId") val userDocumentId: Long? = null,
    @Json(name = "rejectionReason") val rejectionReason: String? = null,
    @Json(name = "createdAt") val createdAt: String? = null,
    @Json(name = "updatedAt") val updatedAt: String? = null
) {
    val providerDisplayName: String
        get() = when (provider.lowercase().trim()) {
            "western_union", "westernunion", "wu" -> "Western Union"
            "moneygram", "money_gram", "mg" -> "MoneyGram"
            "ria" -> "Ria"
            else -> provider.replace("_", " ").replaceFirstChar { it.uppercase() }
        }

    val statusDisplayName: String
        get() = when (status.lowercase().trim()) {
            "pending" -> "En attente"
            "processing" -> "En cours de traitement"
            "approved" -> "Approuvée"
            "processing_payment" -> "Paiement en cours"
            "completed" -> "Terminée"
            "rejected" -> "Rejetée"
            "failed" -> "Échec"
            else -> status.replace("_", " ").replaceFirstChar { it.uppercase() }
        }

    val isPendingOrProcessing: Boolean
        get() = status.lowercase().trim() in listOf("pending", "processing", "approved", "processing_payment")

    val isTerminal: Boolean
        get() = status.lowercase().trim() in listOf("completed", "rejected", "failed")
}

@JsonClass(generateAdapter = true)
data class CreateClaimRequest(
    @Json(name = "provider") val provider: String,
    @Json(name = "trackingNumber") val trackingNumber: String,
    @Json(name = "expectedAmount") val expectedAmount: Double,
    @Json(name = "expectedCurrency") val expectedCurrency: String,
    @Json(name = "senderCountry") val senderCountry: String,
    @Json(name = "receiveInCurrency") val receiveInCurrency: String
)

@JsonClass(generateAdapter = true)
data class CreateClaimResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String? = null,
    @Json(name = "claim") val claim: InternationalTransferClaimDto? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ClaimsListResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "count") val count: Int? = null,
    @Json(name = "claims") val claims: List<InternationalTransferClaimDto> = emptyList(),
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ClaimDetailResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "claim") val claim: InternationalTransferClaimDto? = null,
    @Json(name = "error") val error: String? = null
)
