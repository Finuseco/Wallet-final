package com.example.ui.screens

import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.CountryPickerBottomSheet
import com.example.ui.components.ToofanButton
import com.example.ui.components.ToofanInputField
import com.example.ui.theme.MulishFontFamily
import com.example.ui.theme.ToofanBgColor
import com.example.ui.theme.ToofanBodyText
import com.example.ui.theme.ToofanGreen
import com.example.ui.theme.ToofanGrey1
import com.example.ui.theme.ToofanMainDark
import com.example.ui.theme.ToofanWhite
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterAccountScreen(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    modifier: Modifier = Modifier
) {
    var regStep by remember { mutableIntStateOf(0) } // 0: Type & Phone, 1: Personal Info, 2: Address & Activity, 3: ID Document, 4: Selfie, 5: Signature & Summary
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (uiState.isCountryPickerOpen) {
        CountryPickerBottomSheet(
            sheetState = bottomSheetState,
            countries = uiState.countries,
            selectedCountry = uiState.selectedCountry,
            searchQuery = uiState.countrySearchQuery,
            onSearchQueryChange = { viewModel.setCountrySearchQuery(it) },
            onCountrySelected = { viewModel.selectCountry(it) },
            onDismissRequest = { viewModel.setCountryPickerOpen(false) }
        )
    }

    // Local form state
    var accountType by remember { mutableStateOf("national") }
    var firstName by remember { mutableStateOf("Patrick") }
    var middleName by remember { mutableStateOf("Jean") }
    var lastName by remember { mutableStateOf("Lubanda") }
    var birthDate by remember { mutableStateOf("1995-05-20") }
    var birthPlace by remember { mutableStateOf("Kinshasa") }
    var gender by remember { mutableStateOf("male") }
    var nationality by remember { mutableStateOf("Congolaise") }
    var province by remember { mutableStateOf("Kinshasa") }
    var city by remember { mutableStateOf("Kinshasa") }
    var address by remember { mutableStateOf("Avenue de la Paix No. 12") }
    var profession by remember { mutableStateOf("Développeur / Entrepreneur") }
    var activityDesc by remember { mutableStateOf("") }
    var income by remember { mutableStateOf("") }
    var idType by remember { mutableStateOf("passport") }
    var idNumber by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }

    var idFrontBase64 by remember { mutableStateOf<String?>(null) }
    var idBackBase64 by remember { mutableStateOf<String?>(null) }
    var profilePhotoBase64 by remember { mutableStateOf<String?>(null) }
    var signatureBase64 by remember { mutableStateOf<String?>(null) }

    val frontCameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val stream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            idFrontBase64 = "data:image/jpeg;base64," + Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
        }
    }

    val backCameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val stream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            idBackBase64 = "data:image/jpeg;base64," + Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
        }
    }

    val selfieCameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val stream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            profilePhotoBase64 = "data:image/jpeg;base64," + Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ToofanBgColor)
    ) {
        // Full screen vivid fintech background image with professional contrast
        Image(
            painter = painterResource(id = R.drawable.fintech_bg_person_1790609860029),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.50f
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC070F22),
                            Color(0xAA0F1E3D),
                            Color(0xDD050B17)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    if (regStep > 0) regStep-- else viewModel.navigateBack()
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ToofanGreen.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Étape ${regStep + 1} / 6",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ToofanGreen,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // CashPay Brand Logo centered and comfortably lowered
            Image(
                painter = painterResource(id = R.drawable.cashpay_logo_white),
                contentDescription = "CashPay Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(48.dp)
                    .testTag("cashpay_register_logo")
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ToofanWhite),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    when (regStep) {
                        0 -> {
                            Text(text = "1. Type de Compte & Phone", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ToofanMainDark)
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(text = "Sélectionnez votre profil :", fontSize = 14.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                AccountTypeOption("National", "national", accountType) { accountType = it }
                                AccountTypeOption("Diaspora", "diaspora", accountType) { accountType = it }
                                AccountTypeOption("Business", "business", accountType) { accountType = it }
                            }

                            if (accountType == "business") {
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = companyName,
                                    onValueChange = { companyName = it },
                                    label = { Text("Nom de l'entreprise *") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Numéro de téléphone *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ToofanMainDark)
                            Spacer(modifier = Modifier.height(6.dp))

                            // Exact Country Picker + Phone input as LoginScreen
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, ToofanGrey1.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                        .clickable { viewModel.setCountryPickerOpen(true) }
                                        .testTag("register_country_picker_button"),
                                    color = ToofanWhite
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = uiState.selectedCountry.flagEmoji, fontSize = 18.sp)
                                        Text(
                                            text = uiState.selectedCountry.dialCode,
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = ToofanMainDark
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Pays",
                                            tint = ToofanBodyText,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = uiState.localPhone,
                                    onValueChange = { viewModel.onLocalPhoneChanged(it) },
                                    label = { Text("Numéro mobile") },
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = ToofanGreen) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = uiState.rawEmail,
                                onValueChange = { viewModel.onRawEmailChanged(it) },
                                label = { Text("Adresse E-mail (Facultatif)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                        1 -> {
                            Text(text = "2. Informations Personnelles", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ToofanMainDark)
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Prénom *") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(value = middleName, onValueChange = { middleName = it }, label = { Text("Deuxième prénom") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Nom *") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(value = birthDate, onValueChange = { birthDate = it }, label = { Text("Date de naissance (AAAA-MM-JJ) *") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(value = birthPlace, onValueChange = { birthPlace = it }, label = { Text("Lieu de naissance *") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(value = nationality, onValueChange = { nationality = it }, label = { Text("Nationalité *") }, modifier = Modifier.fillMaxWidth())
                        }
                        2 -> {
                            Text(text = "3. Adresse & Activité", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ToofanMainDark)
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(value = province, onValueChange = { province = it }, label = { Text("Province / État *") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("Ville *") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Adresse physique *") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(value = profession, onValueChange = { profession = it }, label = { Text("Profession / Fonction *") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(value = income, onValueChange = { income = it }, label = { Text("Revenu mensuel estimé (USD) *") }, modifier = Modifier.fillMaxWidth())
                        }
                        3 -> {
                            Text(text = "4. Pièce d'Identité & OCR IA", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ToofanMainDark)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "L'application analyse automatiquement votre document officiel.", fontSize = 13.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(value = idType, onValueChange = { idType = it }, label = { Text("Type de pièce (passport / national_id) *") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(value = idNumber, onValueChange = { idNumber = it }, label = { Text("N° de la pièce *") }, modifier = Modifier.fillMaxWidth())

                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Button(onClick = { frontCameraLauncher.launch(null) }, colors = ButtonDefaults.buttonColors(containerColor = ToofanGreen)) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Recto", fontSize = 12.sp)
                                }
                                Button(onClick = { backCameraLauncher.launch(null) }, colors = ButtonDefaults.buttonColors(containerColor = ToofanGreen)) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Verso", fontSize = 12.sp)
                                }
                            }
                        }
                        4 -> {
                            Text(text = "5. Selfie & Biométrie Faciale", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ToofanMainDark)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Prenez une photo nette de votre visage pour valider votre identité KYC.", fontSize = 13.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(20.dp))

                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Button(onClick = { selfieCameraLauncher.launch(null) }, colors = ButtonDefaults.buttonColors(containerColor = ToofanGreen)) {
                                    Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Prendre Selfie KYC")
                                }
                            }
                        }
                        5 -> {
                            Text(text = "6. Confirmation & Inscription", fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ToofanMainDark)
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(text = "Nom : $firstName $lastName", fontSize = 14.sp)
                            Text(text = "Téléphone : ${uiState.selectedCountry.dialCode} ${uiState.localPhone}", fontSize = 14.sp)
                            Text(text = "Profil : ${accountType.uppercase()}", fontSize = 14.sp)

                            Spacer(modifier = Modifier.height(20.dp))
                            ToofanButton(
                                title = "Soumettre l'ouverture de compte",
                                onClick = {
                                    viewModel.submitRegistration(
                                        accountType = accountType,
                                        firstName = firstName,
                                        middleName = middleName,
                                        lastName = lastName,
                                        gender = gender,
                                        birthDate = birthDate,
                                        birthPlace = birthPlace,
                                        nationality = nationality,
                                        province = province,
                                        city = city,
                                        address = address,
                                        profession = profession,
                                        activityDescription = activityDesc,
                                        incomePerMonth = income,
                                        idType = idType,
                                        idNumber = idNumber,
                                        companyName = companyName,
                                        idFrontBase64 = idFrontBase64,
                                        idBackBase64 = idBackBase64,
                                        profilePhotoBase64 = profilePhotoBase64,
                                        signatureBase64 = signatureBase64
                                    )
                                },
                                isLoading = uiState.isRegisterLoading,
                                testTag = "submit_registration_button"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (regStep < 5) {
                        ToofanButton(
                            title = "Suivant",
                            onClick = { regStep++ },
                            testTag = "next_step_button"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "CashPay • Powered by FINUSECO SA",
                fontFamily = MulishFontFamily,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AccountTypeOption(
    label: String,
    typeValue: String,
    selectedType: String,
    onSelect: (String) -> Unit
) {
    val isSelected = selectedType == typeValue
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect(typeValue) }
            .border(1.dp, if (isSelected) ToofanGreen else ToofanGrey1, RoundedCornerShape(12.dp)),
        color = if (isSelected) ToofanGreen.copy(alpha = 0.12f) else ToofanWhite
    ) {
        Text(
            text = label,
            fontFamily = MulishFontFamily,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) ToofanGreen else ToofanMainDark,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}
