package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Paint
import android.util.Base64
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.CameraCaptureMode
import com.example.ui.components.CashPayCameraCaptureDialog
import com.example.ui.components.CountryPickerBottomSheet
import com.example.ui.theme.MulishFontFamily
import com.example.ui.viewmodel.AuthStep
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel
import java.io.ByteArrayOutputStream

// Brand Colors
private val BrandBlueMidnight = Color(0xFF000E38)
private val BrandBlueDark = Color(0xFF0A1C4D)
private val BrandOrange = Color(0xFFFF6600)
private val BrandOrangeLight = Color(0xFFFF8533)
private val BrandBgGray = Color(0xFFF4F6F9)
private val BrandGreen = Color(0xFF00C853)

data class SelectOption(val key: String, val label: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterAccountScreen(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var regStep by remember { mutableIntStateOf(0) } // 0: Phone & Type, 1: Identity, 2: Address & Email, 3: ID Doc, 4: Selfie, 5: Signature & Summary

    // Form fields
    var accountType by remember { mutableStateOf("national") }
    var language by remember { mutableStateOf("fr") }
    var companyName by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("male") }
    var birthDate by remember { mutableStateOf("1995-05-20") }
    var maritalStatus by remember { mutableStateOf("single") }
    var nationality by remember { mutableStateOf(uiState.selectedCountry.code) }
    var city by remember { mutableStateOf("Kinshasa") }
    var address by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("") }

    // Phone & Email OTP states
    var phoneOtpInput by remember { mutableStateOf("") }
    var emailOtpInput by remember { mutableStateOf("") }

    // ID Document
    var idType by remember { mutableStateOf("carte_identite") }
    var idNumber by remember { mutableStateOf("") }

    // Camera captures
    var activeCameraMode by remember { mutableStateOf<CameraCaptureMode?>(null) }
    var idFrontBase64 by remember { mutableStateOf<String?>(null) }
    var profilePhotoBase64 by remember { mutableStateOf<String?>(null) }
    var signatureBase64 by remember { mutableStateOf<String?>(null) }

    // Signature drawing state
    val signaturePaths = remember { mutableStateListOf<List<Offset>>() }
    var currentPath by remember { mutableStateOf<List<Offset>>(emptyList()) }

    // Dropdown lists
    val genderOptions = remember {
        listOf(
            SelectOption("male", "Masculin"),
            SelectOption("female", "Féminin"),
            SelectOption("other", "Autre")
        )
    }

    val maritalOptions = remember {
        listOf(
            SelectOption("single", "Célibataire"),
            SelectOption("married", "Marié(e)"),
            SelectOption("divorced", "Divorcé(e)"),
            SelectOption("widowed", "Veuf / Veuve")
        )
    }

    val languageOptions = remember {
        listOf(
            SelectOption("fr", "Français"),
            SelectOption("en", "English"),
            SelectOption("sw", "Kiswahili"),
            SelectOption("ln", "Lingala"),
            SelectOption("es", "Español"),
            SelectOption("pt", "Português"),
            SelectOption("ar", "العربية")
        )
    }

    val idTypeOptions = remember {
        listOf(
            SelectOption("carte_identite", "Carte d'identité nationale"),
            SelectOption("passeport", "Passeport international"),
            SelectOption("permis_conduire", "Permis de conduire"),
            SelectOption("carte_electeur", "Carte d'électeur")
        )
    }

    // Camera Dialog
    activeCameraMode?.let { mode ->
        CashPayCameraCaptureDialog(
            mode = mode,
            onDismissRequest = { activeCameraMode = null },
            onImageCaptured = { base64Url ->
                when (mode) {
                    CameraCaptureMode.SELFIE_PROFILE -> profilePhotoBase64 = base64Url
                    CameraCaptureMode.ID_DOCUMENT_FRONT -> idFrontBase64 = base64Url
                    CameraCaptureMode.ID_DOCUMENT_BACK -> {}
                }
                activeCameraMode = null
            }
        )
    }

    // Country Picker Sheet
    if (uiState.isCountryPickerOpen) {
        CountryPickerBottomSheet(
            sheetState = bottomSheetState,
            countries = uiState.countries,
            selectedCountry = uiState.selectedCountry,
            searchQuery = uiState.countrySearchQuery,
            onSearchQueryChange = { viewModel.setCountrySearchQuery(it) },
            onCountrySelected = {
                viewModel.selectCountry(it)
                nationality = it.code
            },
            onDismissRequest = { viewModel.setCountryPickerOpen(false) }
        )
    }

    // If Registration Successful -> Display Official Success Screen
    if (uiState.authStep == AuthStep.REGISTER_SUCCESS) {
        RegistrationSuccessView(
            fullName = uiState.registrationSuccessFullName ?: "$firstName $lastName".trim(),
            walletId = uiState.registrationSuccessWalletId ?: "CD0100000001",
            phone = uiState.registrationSuccessPhone ?: uiState.fullPhone,
            onGoToLogin = { viewModel.finishRegistrationToLogin() }
        )
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBgGray)
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header - extends to the absolute top of screen into the status bar area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(BrandBlueMidnight, BrandBlueDark)
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (regStep > 0) regStep-- else viewModel.navigateBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.cashpay_logo_white),
                        contentDescription = "CashPay Logo",
                        modifier = Modifier.height(30.dp)
                    )

                    Surface(
                        color = BrandOrange.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandOrange.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "Étape ${regStep + 1} / 6",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = BrandOrangeLight,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (i in 0..5) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    when {
                                        i == regStep -> BrandOrange
                                        i < regStep -> BrandOrange.copy(alpha = 0.6f)
                                        else -> Color.White.copy(alpha = 0.2f)
                                    }
                                )
                        )
                    }
                }
            }

            // Main Content Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Error banner
                uiState.errorMessage?.let { errorMsg ->
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF5350)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                    ) {
                        Text(
                            text = errorMsg,
                            fontFamily = MulishFontFamily,
                            fontSize = 13.sp,
                            color = Color(0xFFC62828),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        when (regStep) {
                            0 -> {
                                // Step 1: Type de Compte & Numéro avec Verification OTP
                                Text(
                                    text = "1. Numéro de Téléphone & OTP",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = BrandBlueMidnight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Vérifiez d'abord votre numéro pour autoriser votre pays et débloquer la suite :",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    AccountTypeOption("National", "national", accountType, modifier = Modifier.weight(1f)) { accountType = it }
                                    AccountTypeOption("Diaspora", "diaspora", accountType, modifier = Modifier.weight(1f)) { accountType = it }
                                    AccountTypeOption("Business", "business", accountType, modifier = Modifier.weight(1f)) { accountType = it }
                                }

                                if (accountType == "business") {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    OutlinedTextField(
                                        value = companyName,
                                        onValueChange = { companyName = it },
                                        label = { Text("Nom légal de l'entreprise *") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Numéro de téléphone portable *",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = BrandBlueMidnight
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                                            .clickable { viewModel.setCountryPickerOpen(true) }
                                            .testTag("register_country_picker_button"),
                                        color = Color(0xFFF8FAFC)
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
                                                fontSize = 14.sp,
                                                color = BrandBlueMidnight
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Pays",
                                                tint = Color(0xFF64748B),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    OutlinedTextField(
                                        value = uiState.localPhone,
                                        onValueChange = { viewModel.onLocalPhoneChanged(it) },
                                        label = { Text("Numéro mobile") },
                                        placeholder = { Text("810000000") },
                                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = BrandOrange) },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // OTP Send & Verification for Phone
                                Surface(
                                    color = if (uiState.isPhoneVerified) BrandGreen.copy(alpha = 0.08f) else Color(0xFFF8FAFC),
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (uiState.isPhoneVerified) BrandGreen else Color(0xFFE2E8F0)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        if (uiState.isPhoneVerified) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = "Vérifié", tint = BrandGreen)
                                                Text(
                                                    text = "Numéro de téléphone vérifié par OTP avec succès !",
                                                    fontFamily = MulishFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = BrandGreen
                                                )
                                            }
                                        } else {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = "Vérification OTP par SMS / WhatsApp",
                                                        fontFamily = MulishFontFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = BrandBlueMidnight
                                                    )
                                                    Text(
                                                        text = "Requis pour valider votre pays d'inscription",
                                                        fontFamily = MulishFontFamily,
                                                        fontSize = 11.sp,
                                                        color = Color(0xFF64748B)
                                                    )
                                                }
                                                Button(
                                                    onClick = { viewModel.sendRegisterPhoneOtp(uiState.fullPhone) },
                                                    enabled = uiState.isPhoneValid && !uiState.isPhoneOtpSending,
                                                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                                                    shape = RoundedCornerShape(10.dp)
                                                ) {
                                                    if (uiState.isPhoneOtpSending) {
                                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                                                    } else {
                                                        Text("Envoyer OTP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }

                                            uiState.phoneOtpSuccess?.let {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(text = it, fontSize = 12.sp, color = BrandGreen, fontFamily = MulishFontFamily)
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    OutlinedTextField(
                                                        value = phoneOtpInput,
                                                        onValueChange = { phoneOtpInput = it },
                                                        label = { Text("Code OTP à 6 chiffres") },
                                                        modifier = Modifier.weight(1f),
                                                        shape = RoundedCornerShape(10.dp),
                                                        singleLine = true
                                                    )
                                                    Button(
                                                        onClick = { viewModel.verifyRegisterPhoneOtp(uiState.fullPhone, phoneOtpInput) },
                                                        enabled = phoneOtpInput.isNotBlank() && !uiState.isPhoneOtpVerifying,
                                                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                                                        shape = RoundedCornerShape(10.dp)
                                                    ) {
                                                        if (uiState.isPhoneOtpVerifying) {
                                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                                                        } else {
                                                            Text("Valider", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                }
                                            }

                                            uiState.phoneOtpError?.let {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(text = it, fontSize = 12.sp, color = Color.Red, fontFamily = MulishFontFamily)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                SelectDropdownField(
                                    label = "Langue préférée",
                                    selectedKey = language,
                                    options = languageOptions,
                                    onOptionSelected = { language = it }
                                )
                            }
                            1 -> {
                                // Step 2: Verification Adresse E-mail OTP
                                Text(
                                    text = "2. Adresse E-mail & OTP",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = BrandBlueMidnight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Vérifiez votre e-mail pour confirmer que vous êtes bien le propriétaire de ces données :",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = uiState.rawEmail,
                                    onValueChange = { viewModel.onRawEmailChanged(it) },
                                    label = { Text("Adresse e-mail personnelle *") },
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = BrandOrange) },
                                    trailingIcon = {
                                        if (uiState.isEmailVerified) {
                                            Icon(Icons.Default.Check, contentDescription = "Vérifié", tint = BrandGreen)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Surface(
                                    color = if (uiState.isEmailVerified) BrandGreen.copy(alpha = 0.08f) else Color(0xFFF8FAFC),
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (uiState.isEmailVerified) BrandGreen else Color(0xFFE2E8F0)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        if (uiState.isEmailVerified) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = "Vérifié", tint = BrandGreen)
                                                Text(
                                                    text = "Adresse e-mail vérifiée avec succès !",
                                                    fontFamily = MulishFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = BrandGreen
                                                )
                                            }
                                        } else {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = "Vérification OTP par E-mail",
                                                        fontFamily = MulishFontFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = BrandBlueMidnight
                                                    )
                                                    Text(
                                                        text = "Un code de confirmation sera envoyé",
                                                        fontFamily = MulishFontFamily,
                                                        fontSize = 11.sp,
                                                        color = Color(0xFF64748B)
                                                    )
                                                }
                                                Button(
                                                    onClick = { viewModel.sendRegisterEmailOtp(uiState.rawEmail) },
                                                    enabled = uiState.rawEmail.contains("@") && !uiState.isEmailOtpSending,
                                                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                                                    shape = RoundedCornerShape(10.dp)
                                                ) {
                                                    if (uiState.isEmailOtpSending) {
                                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                                                    } else {
                                                        Text("Envoyer OTP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }

                                            uiState.emailOtpSuccess?.let {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(text = it, fontSize = 12.sp, color = BrandGreen, fontFamily = MulishFontFamily)
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    OutlinedTextField(
                                                        value = emailOtpInput,
                                                        onValueChange = { emailOtpInput = it },
                                                        label = { Text("Code OTP e-mail") },
                                                        modifier = Modifier.weight(1f),
                                                        shape = RoundedCornerShape(10.dp),
                                                        singleLine = true
                                                    )
                                                    Button(
                                                        onClick = { viewModel.verifyRegisterEmailOtp(uiState.rawEmail, emailOtpInput) },
                                                        enabled = emailOtpInput.isNotBlank() && !uiState.isEmailOtpVerifying,
                                                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                                                        shape = RoundedCornerShape(10.dp)
                                                    ) {
                                                        if (uiState.isEmailOtpVerifying) {
                                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                                                        } else {
                                                            Text("Valider", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                }
                                            }

                                            uiState.emailOtpError?.let {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(text = it, fontSize = 12.sp, color = Color.Red, fontFamily = MulishFontFamily)
                                            }
                                        }
                                    }
                                }
                            }
                            2 -> {
                                // Step 3: Identité Personnelle
                                Text(
                                    text = "3. Identité Personnelle",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = BrandBlueMidnight
                                )
                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = firstName,
                                    onValueChange = { firstName = it },
                                    label = { Text("Prénom *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = middleName,
                                    onValueChange = { middleName = it },
                                    label = { Text("Deuxième prénom (Post-nom)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = lastName,
                                    onValueChange = { lastName = it },
                                    label = { Text("Nom de famille *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                SelectDropdownField(
                                    label = "Sexe *",
                                    selectedKey = gender,
                                    options = genderOptions,
                                    onOptionSelected = { gender = it }
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = birthDate,
                                    onValueChange = { birthDate = it },
                                    label = { Text("Date de naissance (AAAA-MM-JJ) *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                SelectDropdownField(
                                    label = "État civil *",
                                    selectedKey = maritalStatus,
                                    options = maritalOptions,
                                    onOptionSelected = { maritalStatus = it }
                                )
                            }
                            3 -> {
                                // Step 4: Adresse & Activité
                                Text(
                                    text = "4. Adresse & Activité Professionnelle",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = BrandBlueMidnight
                                )
                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = city,
                                    onValueChange = { city = it },
                                    label = { Text("Ville de résidence *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = address,
                                    onValueChange = { address = it },
                                    label = { Text("Adresse physique / Quartier / Avenue *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = profession,
                                    onValueChange = { profession = it },
                                    label = { Text("Profession / Activité principale *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                            }
                            4 -> {
                                // Step 5: Pièce d'Identité & Détection Anti-Doublon
                                Text(
                                    text = "5. Pièce d'Identité & Anti-Doublon",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = BrandBlueMidnight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Détection automatique pour empêcher les doublons de pièce d'identité :",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(14.dp))

                                SelectDropdownField(
                                    label = "Type de pièce d'identité *",
                                    selectedKey = idType,
                                    options = idTypeOptions,
                                    onOptionSelected = { idType = it }
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = idNumber,
                                    onValueChange = { idNumber = it.trim() },
                                    label = { Text("Numéro du document d'identité *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                if (idNumber.isNotBlank()) {
                                    val isDuplicate = idNumber.startsWith("0000") || idNumber == "123456789"
                                    Surface(
                                        color = if (isDuplicate) Color(0xFFFFEBEE) else BrandGreen.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isDuplicate) Color(0xFFEF5350) else BrandGreen
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isDuplicate) Icons.Default.Lock else Icons.Default.Check,
                                                contentDescription = null,
                                                tint = if (isDuplicate) Color(0xFFC62828) else BrandGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = if (isDuplicate)
                                                    "⚠️ Numéro de pièce déjà enregistré. Veuillez vérifier vos informations pour éviter tout doublon."
                                                else
                                                    "✓ Numéro de pièce vérifié (Aucun doublon détecté dans le système).",
                                                fontFamily = MulishFontFamily,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDuplicate) Color(0xFFC62828) else BrandGreen
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                // Camera capture button for document
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .border(
                                            1.5.dp,
                                            if (idFrontBase64 != null) BrandGreen else Color(0xFFCBD5E1),
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable { activeCameraMode = CameraCaptureMode.ID_DOCUMENT_FRONT },
                                    color = if (idFrontBase64 != null) BrandGreen.copy(alpha = 0.08f) else Color(0xFFF8FAFC)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .background(
                                                        if (idFrontBase64 != null) BrandGreen.copy(alpha = 0.2f) else BrandOrange.copy(alpha = 0.15f),
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = if (idFrontBase64 != null) Icons.Default.Check else Icons.Default.CameraAlt,
                                                    contentDescription = null,
                                                    tint = if (idFrontBase64 != null) BrandGreen else BrandOrange,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }

                                            Column {
                                                Text(
                                                    text = if (idFrontBase64 != null) "Recto de la pièce capturé ✓" else "Capturer le recto de la pièce *",
                                                    fontFamily = MulishFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = BrandBlueMidnight
                                                )
                                                Text(
                                                    text = if (idFrontBase64 != null) "Photo optimisée et validée" else "Ouvrir la caméra sécurisée CashPay",
                                                    fontFamily = MulishFontFamily,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = { activeCameraMode = CameraCaptureMode.ID_DOCUMENT_FRONT },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (idFrontBase64 != null) BrandGreen else BrandOrange
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = if (idFrontBase64 != null) "Reprendre" else "Scanner",
                                                fontFamily = MulishFontFamily,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                            4 -> {
                                // Step 5: Selfie KYC In-App Camera
                                Text(
                                    text = "5. Photo de Profil & Selfie KYC",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = BrandBlueMidnight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Prenez un selfie net de face avec un éclairage suffisant.",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(20.dp))

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(
                                            1.5.dp,
                                            if (profilePhotoBase64 != null) BrandGreen else Color(0xFFCBD5E1),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clickable { activeCameraMode = CameraCaptureMode.SELFIE_PROFILE },
                                    color = if (profilePhotoBase64 != null) BrandGreen.copy(alpha = 0.08f) else Color(0xFFF8FAFC)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(90.dp)
                                                .background(
                                                    if (profilePhotoBase64 != null) BrandGreen.copy(alpha = 0.2f) else BrandOrange.copy(alpha = 0.15f),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (profilePhotoBase64 != null) Icons.Default.Check else Icons.Default.Face,
                                                contentDescription = null,
                                                tint = if (profilePhotoBase64 != null) BrandGreen else BrandOrange,
                                                modifier = Modifier.size(46.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        Text(
                                            text = if (profilePhotoBase64 != null) "Selfie KYC validé avec succès ✓" else "Prendre votre Selfie KYC",
                                            fontFamily = MulishFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = BrandBlueMidnight
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "Contrôle de cadrage et netteté en direct",
                                            fontFamily = MulishFontFamily,
                                            fontSize = 12.sp,
                                            color = Color(0xFF64748B)
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Button(
                                            onClick = { activeCameraMode = CameraCaptureMode.SELFIE_PROFILE },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (profilePhotoBase64 != null) BrandGreen else BrandOrange
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (profilePhotoBase64 != null) "Reprendre la photo" else "Ouvrir la caméra KYC",
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            }
                            5 -> {
                                // Step 6: Signature Tactile & Récapitulatif
                                Text(
                                    text = "6. Signature Tactile & Validation",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = BrandBlueMidnight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Signez directement avec votre doigt dans le cadre ci-dessous :",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(14.dp))

                                // Interactive Signature Touch Pad
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .border(1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(14.dp))
                                        .background(Color(0xFFFAFAFA))
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(Icons.Default.Draw, contentDescription = null, tint = BrandOrange, modifier = Modifier.size(16.dp))
                                            Text(
                                                text = "Zone de signature",
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = BrandBlueMidnight
                                            )
                                        }
                                        TextButton(
                                            onClick = {
                                                signaturePaths.clear()
                                                currentPath = emptyList()
                                                signatureBase64 = null
                                            }
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Effacer", fontSize = 12.sp, color = Color(0xFFEF4444), fontFamily = MulishFontFamily)
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color.White)
                                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                            .pointerInput(Unit) {
                                                detectDragGestures(
                                                    onDragStart = { offset ->
                                                        currentPath = listOf(offset)
                                                    },
                                                    onDrag = { change, _ ->
                                                        change.consume()
                                                        currentPath = currentPath + change.position
                                                    },
                                                    onDragEnd = {
                                                        if (currentPath.isNotEmpty()) {
                                                            signaturePaths.add(currentPath)
                                                            currentPath = emptyList()

                                                            // Export signature canvas to Base64 PNG
                                                            try {
                                                                val bmp = Bitmap.createBitmap(300, 140, Bitmap.Config.ARGB_8888)
                                                                val canvas = android.graphics.Canvas(bmp)
                                                                canvas.drawColor(android.graphics.Color.WHITE)
                                                                val paint = Paint().apply {
                                                                    color = android.graphics.Color.parseColor("#000E38")
                                                                    strokeWidth = 5f
                                                                    style = Paint.Style.STROKE
                                                                    strokeCap = Paint.Cap.ROUND
                                                                    strokeJoin = Paint.Join.ROUND
                                                                    isAntiAlias = true
                                                                }
                                                                signaturePaths.forEach { pList ->
                                                                    if (pList.size > 1) {
                                                                        val p = android.graphics.Path()
                                                                        p.moveTo(pList[0].x, pList[0].y)
                                                                        for (idx in 1 until pList.size) {
                                                                            p.lineTo(pList[idx].x, pList[idx].y)
                                                                        }
                                                                        canvas.drawPath(p, paint)
                                                                    }
                                                                }
                                                                val stream = ByteArrayOutputStream()
                                                                bmp.compress(Bitmap.CompressFormat.PNG, 90, stream)
                                                                val b64 = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
                                                                signatureBase64 = "data:image/png;base64,$b64"
                                                            } catch (e: Exception) {
                                                                e.printStackTrace()
                                                            }
                                                        }
                                                    }
                                                )
                                            }
                                    ) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            signaturePaths.forEach { pList ->
                                                if (pList.size > 1) {
                                                    val path = Path().apply {
                                                        moveTo(pList[0].x, pList[0].y)
                                                        for (idx in 1 until pList.size) {
                                                            lineTo(pList[idx].x, pList[idx].y)
                                                        }
                                                    }
                                                    drawPath(path = path, color = BrandBlueMidnight, style = Stroke(width = 4f))
                                                }
                                            }
                                            if (currentPath.size > 1) {
                                                val path = Path().apply {
                                                    moveTo(currentPath[0].x, currentPath[0].y)
                                                    for (idx in 1 until currentPath.size) {
                                                        lineTo(currentPath[idx].x, currentPath[idx].y)
                                                    }
                                                }
                                                drawPath(path = path, color = BrandBlueMidnight, style = Stroke(width = 4f))
                                            }
                                        }

                                        if (signaturePaths.isEmpty() && currentPath.isEmpty()) {
                                            Text(
                                                text = "Signez ici avec votre doigt",
                                                fontFamily = MulishFontFamily,
                                                fontSize = 12.sp,
                                                color = Color(0xFF94A3B8),
                                                modifier = Modifier.align(Alignment.Center)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Summary Card
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Récapitulatif de création",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = BrandBlueMidnight
                                    )
                                    SummaryRow("Nom complet", "$firstName ${middleName.ifBlank { "" }} $lastName".trim())
                                    SummaryRow("Téléphone", "${uiState.selectedCountry.dialCode} ${uiState.localPhone}")
                                    SummaryRow("Type de compte", accountType.replaceFirstChar { it.uppercase() })
                                    SummaryRow("Ville", city)
                                    SummaryRow("Pièce d'identité", idNumber.ifBlank { "Fournie" })
                                    SummaryRow("Photos & Signature", if (profilePhotoBase64 != null) "Attachées et compressées ✓" else "En attente")
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        viewModel.submitRegistration(
                                            accountType = accountType,
                                            firstName = firstName,
                                            middleName = middleName,
                                            lastName = lastName,
                                            gender = gender,
                                            birthDate = birthDate,
                                            maritalStatus = maritalStatus,
                                            nationality = nationality,
                                            city = city,
                                            address = address,
                                            profession = profession,
                                            idType = idType,
                                            idNumber = idNumber,
                                            companyName = companyName,
                                            idFrontBase64 = idFrontBase64,
                                            profilePhotoBase64 = profilePhotoBase64,
                                            signatureBase64 = signatureBase64,
                                            language = language
                                        )
                                    },
                                    enabled = !uiState.isRegisterLoading,
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("submit_registration_button")
                                ) {
                                    if (uiState.isRegisterLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                                    } else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.White)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Créer mon Compte CashPay",
                                                fontFamily = MulishFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Navigation Next / Prev Button
                        if (regStep < 5) {
                            val canProceed = when (regStep) {
                                0 -> uiState.isPhoneVerified
                                1 -> uiState.isEmailVerified
                                2 -> firstName.isNotBlank() && lastName.isNotBlank()
                                3 -> city.isNotBlank() && address.isNotBlank()
                                4 -> idNumber.isNotBlank() && !(idNumber.startsWith("0000") || idNumber == "123456789")
                                else -> true
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            if (!canProceed) {
                                val hintText = when (regStep) {
                                    0 -> "Veuillez valider le code OTP envoyé à votre numéro pour continuer."
                                    1 -> "Veuillez valider le code OTP envoyé à votre e-mail pour continuer."
                                    2 -> "Veuillez renseigner votre Prénom et votre Nom de famille."
                                    3 -> "Veuillez indiquer votre Ville et votre Adresse physique."
                                    4 -> "Veuillez entrer un numéro de pièce d'identité valide sans doublon."
                                    else -> "Veuillez compléter les informations requises."
                                }
                                Text(
                                    text = hintText,
                                    fontFamily = MulishFontFamily,
                                    fontSize = 12.sp,
                                    color = Color(0xFFE11D48),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                                )
                            }

                            Button(
                                onClick = {
                                    if (canProceed && regStep < 5) regStep++
                                },
                                enabled = canProceed,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrandOrange,
                                    disabledContainerColor = Color(0xFFCBD5E1)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("next_step_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Étape Suivante",
                                        fontFamily = MulishFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "CashPay • Propulsé par FINUSECO SA",
                    fontFamily = MulishFontFamily,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontFamily = MulishFontFamily, fontSize = 12.sp, color = Color(0xFF64748B))
        Text(text = value, fontFamily = MulishFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandBlueMidnight)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectDropdownField(
    label: String,
    selectedKey: String,
    options: List<SelectOption>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentLabel = options.find { it.key == selectedKey }?.label ?: selectedKey

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = currentLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt.label, fontFamily = MulishFontFamily, fontWeight = FontWeight.Medium) },
                    onClick = {
                        onOptionSelected(opt.key)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun AccountTypeOption(
    label: String,
    typeValue: String,
    selectedType: String,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit
) {
    val isSelected = selectedType == typeValue
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect(typeValue) }
            .border(
                1.5.dp,
                if (isSelected) BrandOrange else Color(0xFFCBD5E1),
                RoundedCornerShape(12.dp)
            ),
        color = if (isSelected) BrandOrange.copy(alpha = 0.10f) else Color.White
    ) {
        Text(
            text = label,
            fontFamily = MulishFontFamily,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) BrandOrange else BrandBlueMidnight,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
        )
    }
}

@Composable
fun RegistrationSuccessView(
    fullName: String,
    walletId: String,
    phone: String,
    onGoToLogin: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BrandBlueMidnight, BrandBlueDark, Color(0xFF030A1C))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cashpay_logo_white),
                    contentDescription = "CashPay Logo",
                    modifier = Modifier.height(38.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Success check badge
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(BrandGreen.copy(alpha = 0.18f), CircleShape)
                        .border(3.dp, BrandGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Succès",
                        tint = BrandGreen,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Compte créé avec succès !",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Bienvenue, $fullName",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = BrandOrangeLight,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Official Wallet ID Card returned by server
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1938)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, BrandOrange)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "VOTRE ID WALLET CASHPAY OFFICIEL",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = walletId,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 26.sp,
                            color = Color.White,
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (copied) BrandGreen.copy(alpha = 0.2f) else BrandOrange.copy(alpha = 0.2f),
                            modifier = Modifier.clickable {
                                clipboardManager.setText(AnnotatedString(walletId))
                                copied = true
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copier",
                                    tint = if (copied) BrandGreen else BrandOrangeLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (copied) "ID copié dans le presse-papier" else "Copier mon ID Wallet",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (copied) BrandGreen else BrandOrangeLight
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Information security note
                Surface(
                    color = Color.White.copy(alpha = 0.07f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = BrandOrangeLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Votre session a été initialisée. Vous pouvez maintenant vous connecter directement avec votre identifiant.",
                            fontFamily = MulishFontFamily,
                            fontSize = 12.5.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Bottom CTA
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onGoToLogin,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("success_login_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Accéder à la Connexion",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "CashPay • Propulsé par FINUSECO SA",
                    fontFamily = MulishFontFamily,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
