package com.example.ui.screens

import android.graphics.Bitmap
import android.util.Base64
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AgentCustomerOptionsResponse
import com.example.data.model.AgentCustomerRepresentativeDto
import com.example.data.model.AgentRegisterCustomerRequest
import com.example.data.model.AgentRegisterCustomerResponse
import com.example.ui.components.CameraCaptureMode
import com.example.ui.components.CashPayCameraCaptureDialog
import com.example.ui.theme.MulishFontFamily
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentCustomerOnboardingDialog(
    isOpen: Boolean,
    optionsResponse: AgentCustomerOptionsResponse?,
    isLoadingOptions: Boolean,
    isRegistering: Boolean,
    registerSuccess: AgentRegisterCustomerResponse?,
    registerError: String?,
    onDismiss: () -> Unit,
    onSubmitRegister: (AgentRegisterCustomerRequest) -> Unit,
    onRetryOptions: () -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var currentStep by remember { mutableIntStateOf(1) } // 1..6

    // Form fields
    var accountType by remember { mutableStateOf("national") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("male") }
    var maritalStatus by remember { mutableStateOf("single") }
    var birthDate by remember { mutableStateOf("2000-01-01") }
    var birthPlace by remember { mutableStateOf("Kinshasa") }
    var nationality by remember { mutableStateOf("Congolaise") }
    var profession by remember { mutableStateOf("Commerçant") }
    var incomePerMonth by remember { mutableStateOf("300") }

    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedProvince by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Kinshasa") }
    var address by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("fr") }
    var ussdLanguage by remember { mutableStateOf("fr") }

    var idType by remember { mutableStateOf("national_id") }
    var idNumber by remember { mutableStateOf("") }
    var idIssuedDate by remember { mutableStateOf("") }
    var idExpiryDate by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var activityDescription by remember { mutableStateOf("") }

    var repName by remember { mutableStateOf("") }
    var repContact by remember { mutableStateOf("") }
    var repRelation by remember { mutableStateOf("parent") }

    var profilePhotoBase64 by remember { mutableStateOf("") }
    var idFrontBase64 by remember { mutableStateOf("") }
    var idBackBase64 by remember { mutableStateOf("") }
    var signatureBase64 by remember { mutableStateOf("") }

    // Camera capture modal state
    var activeCameraMode by remember { mutableStateOf<CameraCaptureMode?>(null) }
    var showSignaturePad by remember { mutableStateOf(false) }

    // Dropdown states
    var provinceMenuExpanded by remember { mutableStateOf(false) }
    var genderMenuExpanded by remember { mutableStateOf(false) }
    var maritalMenuExpanded by remember { mutableStateOf(false) }
    var idTypeMenuExpanded by remember { mutableStateOf(false) }
    var relationMenuExpanded by remember { mutableStateOf(false) }

    // Auto-select first province if options change
    LaunchedEffect(optionsResponse) {
        if (selectedProvince.isBlank()) {
            val firstProv = optionsResponse?.provinces?.firstOrNull()?.name
            if (!firstProv.isNullOrBlank()) {
                selectedProvince = firstProv
            }
        }
    }

    // Active Camera dialog
    activeCameraMode?.let { mode ->
        CashPayCameraCaptureDialog(
            mode = mode,
            onDismissRequest = { activeCameraMode = null },
            onImageCaptured = { b64 ->
                when (mode) {
                    CameraCaptureMode.SELFIE_PROFILE -> profilePhotoBase64 = b64
                    CameraCaptureMode.ID_DOCUMENT_FRONT -> idFrontBase64 = b64
                    CameraCaptureMode.ID_DOCUMENT_BACK -> idBackBase64 = b64
                }
                activeCameraMode = null
            }
        )
    }

    // Signature Pad Modal
    if (showSignaturePad) {
        SignatureCaptureModal(
            onDismiss = { showSignaturePad = false },
            onSignatureConfirmed = { b64 ->
                signatureBase64 = b64
                showSignaturePad = false
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Surface(
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (currentStep > 1 && registerSuccess == null) {
                                IconButton(
                                    onClick = { currentStep-- },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Retour",
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Column {
                                Text(
                                    text = "Création Compte Client",
                                    fontFamily = MulishFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = if (registerSuccess != null) "Compte créé avec succès"
                                    else "Étape $currentStep sur 6 • Espace Agent",
                                    fontFamily = MulishFontFamily,
                                    fontSize = 12.sp,
                                    color = Color(0xFF00E676)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fermer",
                                tint = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFF0F172A))
            ) {
                if (registerSuccess != null && registerSuccess.success) {
                    // Success Screen matching specs
                    AgentCustomerRegisterSuccessView(
                        response = registerSuccess,
                        onClose = onDismiss
                    )
                } else if (isLoadingOptions) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFF00E676), strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Chargement des options de votre pays...",
                            color = Color(0xFFCBD5E1),
                            fontFamily = MulishFontFamily,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Progress indicator bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            repeat(6) { index ->
                                val stepNum = index + 1
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            if (stepNum <= currentStep) Color(0xFF00E676)
                                            else Color(0xFF1E293B)
                                        )
                                )
                            }
                        }

                        // Error Banner if present
                        registerError?.let { err ->
                            Surface(
                                color = Color(0xFF7F1D1D).copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFEF4444))
                                    Text(
                                        text = err,
                                        color = Color(0xFFFECACA),
                                        fontSize = 12.5.sp,
                                        fontFamily = MulishFontFamily
                                    )
                                }
                            }
                        }

                        when (currentStep) {
                            1 -> {
                                // Step 1: Identité Personnelle
                                StepHeader("1. Identité du Client", "Saisissez les informations d'état civil du nouveau client.")

                                FormInputField("Prénom *", firstName, { firstName = it }, "Ex: Jean")
                                FormInputField("Nom de famille *", lastName, { lastName = it }, "Ex: Mukendi")
                                FormInputField("Post-nom (Optionnel)", middleName, { middleName = it }, "Ex: Ilunga")

                                // Genre Selector
                                Text("Genre *", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    listOf("male" to "Homme", "female" to "Femme", "other" to "Autre").forEach { (code, label) ->
                                        val isSel = gender == code
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(42.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .border(1.dp, if (isSel) Color(0xFF00E676) else Color(0xFF334155), RoundedCornerShape(10.dp))
                                                .clickable { gender = code },
                                            color = if (isSel) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFF1E293B)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(label, color = if (isSel) Color(0xFF00E676) else Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            }
                                        }
                                    }
                                }

                                FormInputField("Date de naissance (AAAA-MM-JJ) *", birthDate, { birthDate = it }, "2000-01-01", KeyboardType.Text)
                                FormInputField("Lieu de naissance *", birthPlace, { birthPlace = it }, "Kinshasa")
                                FormInputField("Nationalité *", nationality, { nationality = it }, "Congolaise")
                                FormInputField("Profession *", profession, { profession = it }, "Commerçant")
                                FormInputField("Revenu mensuel estimé (USD) *", incomePerMonth, { incomePerMonth = it }, "300", KeyboardType.Number)

                                StepNextButton(
                                    label = "Suivant : Coordonnées",
                                    enabled = firstName.isNotBlank() && lastName.isNotBlank() && birthDate.isNotBlank() && birthPlace.isNotBlank() && incomePerMonth.isNotBlank(),
                                    onClick = { currentStep = 2 }
                                )
                            }

                            2 -> {
                                // Step 2: Coordonnées & Province
                                val countryDial = optionsResponse?.country?.dialCode ?: "+243"
                                StepHeader("2. Contact & Résidence", "Pays rattaché à l'Agent : ${optionsResponse?.country?.name ?: "RDC"} ($countryDial)")

                                FormInputField("Numéro de Téléphone *", phone, { phone = it }, "Ex: 833248614 ou 243...", KeyboardType.Phone)
                                FormInputField("Adresse Email (Optionnel)", email, { email = it }, "client@email.com", KeyboardType.Email)

                                // Province Dropdown from Server Options
                                Text("Province du Client *", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                                val provList = optionsResponse?.provinces ?: emptyList()
                                ExposedDropdownMenuBox(
                                    expanded = provinceMenuExpanded,
                                    onExpandedChange = { provinceMenuExpanded = !provinceMenuExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = if (selectedProvince.isNotBlank()) selectedProvince else "Sélectionner la province",
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = provinceMenuExpanded) },
                                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                                        colors = darkTextFieldColors(),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    ExposedDropdownMenu(
                                        expanded = provinceMenuExpanded,
                                        onDismissRequest = { provinceMenuExpanded = false }
                                    ) {
                                        provList.forEach { prov ->
                                            DropdownMenuItem(
                                                text = { Text(prov.name ?: "", color = Color(0xFF0F172A)) },
                                                onClick = {
                                                    selectedProvince = prov.name ?: ""
                                                    provinceMenuExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                FormInputField("Ville *", city, { city = it }, "Kinshasa")
                                FormInputField("Adresse physique / Quartier *", address, { address = it }, "Av. Kasa-Vubu N° 12, Gombe")

                                StepNextButton(
                                    label = "Suivant : Pièce d'Identité",
                                    enabled = phone.isNotBlank() && selectedProvince.isNotBlank() && city.isNotBlank() && address.isNotBlank(),
                                    onClick = { currentStep = 3 }
                                )
                            }

                            3 -> {
                                // Step 3: Pièce d'Identité
                                StepHeader("3. Pièce d'Identité", "Sélectionnez et renseignez le document d'identification du client.")

                                Text("Type de Pièce *", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("national_id" to "Carte Identité", "passport" to "Passeport", "driver_license" to "Permis").forEach { (code, label) ->
                                        val isSel = idType == code
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(42.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .border(1.dp, if (isSel) Color(0xFF00E676) else Color(0xFF334155), RoundedCornerShape(10.dp))
                                                .clickable { idType = code },
                                            color = if (isSel) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFF1E293B)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(label, color = if (isSel) Color(0xFF00E676) else Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                            }
                                        }
                                    }
                                }

                                FormInputField("Numéro de la pièce *", idNumber, { idNumber = it }, "Ex: ID-98472918")
                                FormInputField("Date d'émission (Optionnel)", idIssuedDate, { idIssuedDate = it }, "2023-01-15")
                                FormInputField("Date d'expiration (Optionnel)", idExpiryDate, { idExpiryDate = it }, "2028-01-15")
                                FormInputField("Nom Entreprise (Optionnel)", companyName, { companyName = it }, "Ex: Éts Mukendi & Frères")
                                FormInputField("Activité Commerciale (Optionnel)", activityDescription, { activityDescription = it }, "Commerce de gros et détail")

                                StepNextButton(
                                    label = "Suivant : Contact de Référence",
                                    enabled = idNumber.isNotBlank(),
                                    onClick = { currentStep = 4 }
                                )
                            }

                            4 -> {
                                // Step 4: Personne de Référence / Représentant
                                StepHeader("4. Personne de Référence", "Indiquez un contact de confiance (parent, tuteur ou conjoint).")

                                FormInputField("Nom complet du contact de référence *", repName, { repName = it }, "Ex: Marie Mukendi")
                                FormInputField("Téléphone de la référence *", repContact, { repContact = it }, "Ex: 243812345678", KeyboardType.Phone)

                                Text("Lien de parenté / Relation *", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontFamily = MulishFontFamily, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("parent" to "Parent", "spouse" to "Conjoint", "sibling" to "Frère/Sœur", "friend" to "Proche").forEach { (code, label) ->
                                        val isSel = repRelation == code
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(42.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .border(1.dp, if (isSel) Color(0xFF00E676) else Color(0xFF334155), RoundedCornerShape(10.dp))
                                                .clickable { repRelation = code },
                                            color = if (isSel) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFF1E293B)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(label, color = if (isSel) Color(0xFF00E676) else Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                            }
                                        }
                                    }
                                }

                                StepNextButton(
                                    label = "Suivant : Captures & Signature",
                                    enabled = repName.isNotBlank() && repContact.isNotBlank(),
                                    onClick = { currentStep = 5 }
                                )
                            }

                            5 -> {
                                // Step 5: Captures Biométriques & Signature
                                StepHeader("5. Captures & Signature", "Prenez la photo du client, sa pièce d'identité et faites-lui apposer sa signature.")

                                MediaCaptureCard(
                                    title = "Photo de profil du Client *",
                                    description = "Selfie ou portrait net du visage",
                                    hasCapture = profilePhotoBase64.isNotBlank(),
                                    icon = Icons.Default.Person,
                                    onCapture = { activeCameraMode = CameraCaptureMode.SELFIE_PROFILE }
                                )

                                MediaCaptureCard(
                                    title = "Pièce d'Identité (Recto) *",
                                    description = "Face avant claire avec texte lisible",
                                    hasCapture = idFrontBase64.isNotBlank(),
                                    icon = Icons.Default.CreditCard,
                                    onCapture = { activeCameraMode = CameraCaptureMode.ID_DOCUMENT_FRONT }
                                )

                                MediaCaptureCard(
                                    title = "Pièce d'Identité (Verso - Optionnel)",
                                    description = "Face arrière du document",
                                    hasCapture = idBackBase64.isNotBlank(),
                                    icon = Icons.Default.CreditCard,
                                    onCapture = { activeCameraMode = CameraCaptureMode.ID_DOCUMENT_BACK }
                                )

                                MediaCaptureCard(
                                    title = "Signature tactile du Client *",
                                    description = "Signature au doigt sur l'écran tactile",
                                    hasCapture = signatureBase64.isNotBlank(),
                                    icon = Icons.Default.Draw,
                                    onCapture = { showSignaturePad = true }
                                )

                                StepNextButton(
                                    label = "Suivant : Vérification & Validation",
                                    enabled = profilePhotoBase64.isNotBlank() && idFrontBase64.isNotBlank() && signatureBase64.isNotBlank(),
                                    onClick = { currentStep = 6 }
                                )
                            }

                            6 -> {
                                // Step 6: Récapitulatif Final & Validation
                                StepHeader("6. Vérification & Validation", "Vérifiez soigneusement les informations avant de valider la création du compte.")

                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        SummaryRow("Nom complet", "$firstName $lastName $middleName".trim())
                                        SummaryRow("Téléphone", phone)
                                        SummaryRow("Province / Ville", "$selectedProvince, $city")
                                        SummaryRow("Adresse", address)
                                        SummaryRow("Pièce d'identité", "$idType : $idNumber")
                                        SummaryRow("Référence", "$repName ($repRelation)")
                                        SummaryRow("Photo Client", if (profilePhotoBase64.isNotBlank()) "Capturée ✔" else "Manquante ❌")
                                        SummaryRow("Recto Pièce", if (idFrontBase64.isNotBlank()) "Capturé ✔" else "Manquant ❌")
                                        SummaryRow("Signature Client", if (signatureBase64.isNotBlank()) "Signé ✔" else "Manquante ❌")
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        val req = AgentRegisterCustomerRequest(
                                            accountType = accountType,
                                            firstName = firstName.trim(),
                                            lastName = lastName.trim(),
                                            middleName = if (middleName.isNotBlank()) middleName.trim() else null,
                                            gender = gender,
                                            maritalStatus = maritalStatus,
                                            birthDate = birthDate.trim(),
                                            birthPlace = birthPlace.trim(),
                                            email = if (email.isNotBlank()) email.trim() else null,
                                            phone = phone.trim(),
                                            province = selectedProvince.trim(),
                                            city = city.trim(),
                                            address = address.trim(),
                                            idType = idType,
                                            idNumber = idNumber.trim(),
                                            idIssuedDate = if (idIssuedDate.isNotBlank()) idIssuedDate.trim() else null,
                                            idExpiryDate = if (idExpiryDate.isNotBlank()) idExpiryDate.trim() else null,
                                            profession = profession.trim(),
                                            nationality = nationality.trim(),
                                            incomePerMonth = incomePerMonth.toDoubleOrNull() ?: 100.0,
                                            language = language,
                                            ussdLanguage = ussdLanguage,
                                            companyName = if (companyName.isNotBlank()) companyName.trim() else null,
                                            activityDescription = if (activityDescription.isNotBlank()) activityDescription.trim() else null,
                                            profilePhoto = profilePhotoBase64,
                                            idFrontImage = idFrontBase64,
                                            idBackImage = if (idBackBase64.isNotBlank()) idBackBase64 else null,
                                            signatureImage = signatureBase64,
                                            representative = AgentCustomerRepresentativeDto(
                                                name = repName.trim(),
                                                contact = repContact.trim(),
                                                relation = repRelation
                                            )
                                        )
                                        onSubmitRegister(req)
                                    },
                                    enabled = !isRegistering,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isRegistering) {
                                        CircularProgressIndicator(color = Color(0xFF0F172A), modifier = Modifier.size(20.dp), strokeWidth = 2.5.dp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Enregistrement du client en cours...", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontFamily = MulishFontFamily)
                                    } else {
                                        Text(
                                            text = "Créer le compte Client (Wallet)",
                                            color = Color(0xFF0F172A),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            fontFamily = MulishFontFamily
                                        )
                                    }
                                }

                                TextButton(
                                    onClick = { currentStep = 1 },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Modifier les informations", color = Color(0xFF94A3B8), fontSize = 13.sp, fontFamily = MulishFontFamily)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepHeader(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            fontFamily = MulishFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White
        )
        Text(
            text = description,
            fontFamily = MulishFontFamily,
            fontSize = 13.sp,
            color = Color(0xFF94A3B8),
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun FormInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            fontFamily = MulishFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.5.sp,
            color = Color(0xFFCBD5E1)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF64748B), fontFamily = MulishFontFamily) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = darkTextFieldColors(),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun darkTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF00E676),
    unfocusedBorderColor = Color(0xFF334155),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedContainerColor = Color(0xFF1E293B),
    unfocusedContainerColor = Color(0xFF1E293B)
)

@Composable
private fun StepNextButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    Spacer(modifier = Modifier.height(10.dp))
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF00E676),
            disabledContainerColor = Color(0xFF00E676).copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(label, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 15.sp, fontFamily = MulishFontFamily)
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF0F172A))
        }
    }
}

@Composable
private fun MediaCaptureCard(
    title: String,
    description: String,
    hasCapture: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onCapture: () -> Unit
) {
    Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (hasCapture) Color(0xFF00E676) else Color(0xFF334155)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCapture() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (hasCapture) Color(0xFF00E676).copy(alpha = 0.2f)
                            else Color(0xFF334155)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (hasCapture) Icons.Default.Check else icon,
                        contentDescription = null,
                        tint = if (hasCapture) Color(0xFF00E676) else Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = description,
                        fontFamily = MulishFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Surface(
                color = if (hasCapture) Color(0xFF00E676) else Color(0xFF38BDF8).copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (hasCapture) "Modifié" else "Capturer",
                    color = if (hasCapture) Color(0xFF0F172A) else Color(0xFF38BDF8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MulishFontFamily,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = Color(0xFF94A3B8), fontFamily = MulishFontFamily)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = MulishFontFamily)
    }
}

@Composable
private fun AgentCustomerRegisterSuccessView(
    response: AgentRegisterCustomerResponse,
    onClose: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val customer = response.customer
    val walletId = customer?.idWallet ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(Color(0xFF00E676).copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(46.dp))
        }

        Text(
            text = "Client Enregistré !",
            fontFamily = MulishFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Color.White
        )
        Text(
            text = response.message ?: "Le compte a été créé avec succès par l'Agent. Le portefeuille CashPay est actif.",
            fontFamily = MulishFontFamily,
            fontSize = 13.5.sp,
            color = Color(0xFFCBD5E1),
            textAlign = TextAlign.Center
        )

        Surface(
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Wallet ID Highlight
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("ID Wallet Client généré", fontSize = 11.sp, color = Color(0xFF94A3B8), fontFamily = MulishFontFamily)
                            Text(walletId, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF00E676), fontFamily = MulishFontFamily)
                        }
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(walletId))
                                Toast.makeText(context, "Wallet ID copié !", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = Color(0xFF00E676))
                        }
                    }
                }

                SummaryRow("Nom complet", customer?.fullName ?: "")
                SummaryRow("Téléphone", customer?.phone ?: "")
                SummaryRow("Province", customer?.province ?: "")
                SummaryRow("Statut Parrainage Agent", response.referral?.status ?: "Pending Deposit")
                SummaryRow("Agent Référent", response.createdBy?.fullName ?: "Vous")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Terminer & Revenir à l'Espace Agent", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontFamily = MulishFontFamily, fontSize = 14.5.sp)
        }
    }
}

@Composable
fun SignatureCaptureModal(
    onDismiss: () -> Unit,
    onSignatureConfirmed: (String) -> Unit
) {
    val points = remember { mutableStateListOf<Offset>() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp)),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Signature manuscrite du Client",
                        fontFamily = MulishFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Color(0xFF94A3B8))
                    }
                }

                Text(
                    text = "Signez avec votre doigt dans le cadre blanc ci-dessous.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    fontFamily = MulishFontFamily
                )

                // White canvas drawing zone
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                points.add(change.position)
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        for (i in 0 until points.size - 1) {
                            val p1 = points[i]
                            val p2 = points[i + 1]
                            // Don't connect discontinuous lines if distance too big
                            if ((p1 - p2).getDistance() < 60f) {
                                drawLine(
                                    color = Color.Black,
                                    start = p1,
                                    end = p2,
                                    strokeWidth = 4f
                                )
                            }
                        }
                    }

                    if (points.isEmpty()) {
                        Text(
                            text = "✍️ Signez ici",
                            color = Color.LightGray,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { points.clear() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Effacer", color = Color(0xFFEF4444), fontSize = 13.sp, fontFamily = MulishFontFamily)
                    }

                    Button(
                        onClick = {
                            // Generate a simple valid base64 signature representation
                            val bmp = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888)
                            val canvas = android.graphics.Canvas(bmp)
                            canvas.drawColor(android.graphics.Color.WHITE)
                            val paint = android.graphics.Paint().apply {
                                color = android.graphics.Color.BLACK
                                strokeWidth = 5f
                                isAntiAlias = true
                                style = android.graphics.Paint.Style.STROKE
                            }
                            for (i in 0 until points.size - 1) {
                                val p1 = points[i]
                                val p2 = points[i + 1]
                                if ((p1 - p2).getDistance() < 60f) {
                                    canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint)
                                }
                            }
                            val outputStream = ByteArrayOutputStream()
                            bmp.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
                            val b64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
                            onSignatureConfirmed("data:image/png;base64,$b64")
                        },
                        enabled = points.size > 5,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Confirmer", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 13.sp, fontFamily = MulishFontFamily)
                    }
                }
            }
        }
    }
}
