package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// --- BOUTIQUE MODELS ---

@JsonClass(generateAdapter = true)
data class BoutiqueDto(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "address") val address: String? = null,
    @Json(name = "whatsapp_public_number") val whatsappPublicNumber: String? = null
)

@JsonClass(generateAdapter = true)
data class BoutiquesResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "boutiques") val boutiques: List<BoutiqueDto> = emptyList(),
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class BoutiqueResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "boutique") val boutique: BoutiqueDto? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class CreateBoutiqueRequest(
    @Json(name = "name") val name: String,
    @Json(name = "address") val address: String,
    @Json(name = "whatsapp_public_number") val whatsappPublicNumber: String
)

// --- PRODUCT MODELS ---

@JsonClass(generateAdapter = true)
data class ProductVariationDto(
    @Json(name = "color") val color: String = "",
    @Json(name = "stock") val stock: Int = 0
)

@JsonClass(generateAdapter = true)
data class ShippingRateDto(
    @Json(name = "country") val country: String = "CD",
    @Json(name = "province") val province: String = "",
    @Json(name = "rate") val rate: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class PromotionalPackageDto(
    @Json(name = "quantity") val quantity: Int = 0,
    @Json(name = "price") val price: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class ProductDetailsDto(
    @Json(name = "isbn10") val isbn10: String? = null,
    @Json(name = "isbn13") val isbn13: String? = null,
    @Json(name = "pdfUrl") val pdfUrl: String? = null,
    @Json(name = "sourceUrl") val sourceUrl: String? = null,
    @Json(name = "audioUrl") val audioUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class ProductSellerDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "name") val name: String? = null
)

@JsonClass(generateAdapter = true)
data class ProductDto(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "image") val image: String? = null,
    @Json(name = "images") val images: List<String> = emptyList(),
    @Json(name = "purchasePrice") val purchasePrice: Double? = null,
    @Json(name = "sellingPrice") val sellingPrice: Double = 0.0,
    @Json(name = "currency") val currency: String = "USD",
    @Json(name = "weight") val weight: Double = 1.0,
    @Json(name = "shippingRates") val shippingRates: List<ShippingRateDto> = emptyList(),
    @Json(name = "promotionalPackages") val promotionalPackages: List<PromotionalPackageDto> = emptyList(),
    @Json(name = "variations") val variations: List<ProductVariationDto> = emptyList(),
    @Json(name = "barcode") val barcode: String? = null,
    @Json(name = "stock") val stock: Int = 0,
    @Json(name = "categoryId") val categoryId: String = "cat-vetements",
    @Json(name = "boutiqueId") val boutiqueId: String? = null,
    @Json(name = "boutiqueName") val boutiqueName: String? = null,
    @Json(name = "isPublic") val isPublic: Boolean = true,
    @Json(name = "details") val details: ProductDetailsDto? = null,
    @Json(name = "seller") val seller: ProductSellerDto? = null
)

@JsonClass(generateAdapter = true)
data class ShoppingContextResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "country") val country: String? = null,
    @Json(name = "countryCode") val countryCode: String? = null,
    @Json(name = "boutiques") val boutiques: List<BoutiqueDto> = emptyList(),
    @Json(name = "products") val products: List<ProductDto> = emptyList(),
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ProductResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "product") val product: ProductDto? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ProductReferenceResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "reference") val reference: String = "",
    @Json(name = "productId") val productId: String = "",
    @Json(name = "name") val name: String? = null,
    @Json(name = "barcode") val barcode: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class PublishProductRequest(
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "categoryId") val categoryId: String,
    @Json(name = "boutiqueId") val boutiqueId: String,
    @Json(name = "purchasePrice") val purchasePrice: Double? = null,
    @Json(name = "sellingPrice") val sellingPrice: Double,
    @Json(name = "barcode") val barcode: String? = null,
    @Json(name = "images") val images: List<String> = emptyList(),
    @Json(name = "isPublic") val isPublic: Boolean = true,
    @Json(name = "weight") val weight: Double = 1.0,
    @Json(name = "stock") val stock: Int = 1,
    @Json(name = "variations") val variations: List<ProductVariationDto> = emptyList(),
    @Json(name = "shippingRates") val shippingRates: List<ShippingRateDto> = emptyList(),
    @Json(name = "promotionalPackages") val promotionalPackages: List<PromotionalPackageDto> = emptyList(),
    @Json(name = "details") val details: ProductDetailsDto? = null
)

@JsonClass(generateAdapter = true)
data class GenericShoppingResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error") val error: String? = null
)

// Categories catalog
data class ProductCategoryItem(
    val id: String,
    val nameFr: String,
    val iconName: String
)

val SHOPPING_CATEGORIES = listOf(
    ProductCategoryItem("cat-vetements", "Vêtements & Mode", "Checkroom"),
    ProductCategoryItem("cat-electronique", "Électronique & Téléphonie", "Devices"),
    ProductCategoryItem("cat-alimentaire", "Alimentation & Boissons", "Fastfood"),
    ProductCategoryItem("cat-beaute", "Beauté & Cosmétiques", "Spa"),
    ProductCategoryItem("cat-maison", "Maison & Décoration", "Home"),
    ProductCategoryItem("cat-livre", "Livres & Médias", "MenuBook"),
    ProductCategoryItem("cat-divers", "Divers & Accessoires", "Category")
)
