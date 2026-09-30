package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.ToofanButton
import com.example.ui.components.ToofanInputField
import com.example.ui.theme.*
import com.example.ui.viewmodel.DashboardUiState
import com.example.ui.viewmodel.DashboardViewModel

@Composable
fun WithdrawalDialog(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CallReceived,
                    contentDescription = null,
                    tint = ToofanGreen,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Retrait d'espèces",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ToofanMainDark
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Stepper Indicator
                if (uiState.withdrawalStep < 5) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Étape ${uiState.withdrawalStep}/4",
                            fontFamily = MulishFontFamily,
                            fontSize = 11.sp,
                            color = ToofanGreen,
                            fontWeight = FontWeight.Bold
                        )
                        LinearProgressBar(
                            step = uiState.withdrawalStep,
                            maxSteps = 4,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp)
                        )
                    }
                }

                when (uiState.withdrawalStep) {
                    1 -> StepSelectTypeAndRecipient(uiState, viewModel)
                    2 -> StepEnterAmount(uiState, viewModel)
                    3 -> StepPreviewWithdrawal(uiState, viewModel)
                    4 -> StepConfirmPin(uiState, viewModel)
                    5 -> StepWithdrawalSuccess(uiState, viewModel, onDismiss)
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.withdrawalStep > 1 && uiState.withdrawalStep < 5) {
                    TextButton(
                        onClick = { viewModel.prevWithdrawalStep() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Précédent",
                            fontFamily = MulishFontFamily,
                            color = ToofanBodyText,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                when (uiState.withdrawalStep) {
                    1 -> {
                        ToofanButton(
                            title = if (uiState.withdrawalType == "agent_cash") "Rechercher" else "Suivant",
                            onClick = { viewModel.searchAgentAndProceed() },
                            isLoading = uiState.isSearchingAgent,
                            modifier = Modifier.width(130.dp)
                        )
                    }
                    2 -> {
                        ToofanButton(
                            title = "Suivant",
                            onClick = { viewModel.proceedToPreview() },
                            isLoading = uiState.isWithdrawalPreviewLoading,
                            enabled = uiState.withdrawalAmount.toDoubleOrNull() != null && uiState.withdrawalAmount.toDouble() > 0,
                            modifier = Modifier.width(120.dp)
                        )
                    }
                    3 -> {
                        val canConfirm = uiState.withdrawalPreview?.canConfirm == true
                        ToofanButton(
                            title = "Continuer",
                            onClick = { viewModel.proceedToPinEntry() },
                            enabled = canConfirm,
                            modifier = Modifier.width(120.dp)
                        )
                    }
                    4 -> {
                        ToofanButton(
                            title = "Confirmer",
                            onClick = { viewModel.confirmWithdrawal(userId = 1, pin = uiState.withdrawalRecipient) }, // Wait, PIN input handles PIN locally or recipient?
                            // Actually let's use a local state or text field for PIN inside confirmation step.
                            // Let's implement local PIN input inside StepConfirmPin and trigger confirmWithdrawal(1, localPin)
                            // We will handle it in the click lambda of StepConfirmPin directly.
                            modifier = Modifier.width(0.dp) // Hidden here, custom confirm button inside StepConfirmPin
                        )
                    }
                    5 -> {
                        ToofanButton(
                            title = "Fermer",
                            onClick = onDismiss,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }
            }
        },
        dismissButton = {
            if (uiState.withdrawalStep < 5) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Annuler",
                        fontFamily = MulishFontFamily,
                        color = ToofanBodyText
                    )
                }
            }
        }
    )
}

@Composable
fun StepSelectTypeAndRecipient(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Toggle Method (Agent cash vs Mobile Money)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ToofanGrey1.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (uiState.withdrawalType == "agent_cash") ToofanGreen else Color.Transparent)
                    .clickable { viewModel.setWithdrawalType("agent_cash") },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chez Agent",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (uiState.withdrawalType == "agent_cash") Color.White else ToofanMainDark
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (uiState.withdrawalType == "mobile_money") ToofanGreen else Color.Transparent)
                    .clickable { viewModel.setWithdrawalType("mobile_money") },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Mobile Money",
                    fontFamily = MulishFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (uiState.withdrawalType == "mobile_money") Color.White else ToofanMainDark
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (uiState.withdrawalType == "agent_cash") {
            Text(
                text = "Identifier le Portefeuille Agent",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = ToofanMainDark
            )
            Text(
                text = "Saisissez le Wallet ID ou le numéro de téléphone de l'agent agréé CashPay.",
                fontSize = 11.sp,
                color = ToofanBodyText
            )

            ToofanInputField(
                value = uiState.withdrawalRecipient,
                onValueChange = { viewModel.setWithdrawalRecipient(it) },
                placeholder = "Ex: CP789012 ou 0812...",
                modifier = Modifier.testTag("withdrawal_agent_input")
            )

            if (uiState.agentSearchError == "CLIENT_FOUND") {
                // Polished Error banner informing that this is a Client and orienting to TRANSFER
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECEF)),
                    border = BorderStroke(1.dp, Color(0xFFFF4868).copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFF4868),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Portefeuille Client Détecté",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFFF4868)
                            )
                        }

                        Text(
                            text = "Le portefeuille de ${uiState.searchedAgentProfile?.fullName ?: "l'utilisateur"} possède le rôle 'Client'. Un portefeuille client ne peut pas effectuer de retrait agent ou recevoir de commissions d'agent.",
                            fontSize = 11.sp,
                            color = ToofanMainDark
                        )

                        Button(
                            onClick = {
                                viewModel.closeWithdrawalDialog()
                                uiState.searchedAgentProfile?.let { profile ->
                                    viewModel.triggerPrefilledTransfer(profile.fullName, profile)
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF4868),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Faire un transfert d'argent",
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            } else if (uiState.agentSearchError != null) {
                Text(
                    text = uiState.agentSearchError,
                    color = Color(0xFFFF4868),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            // Mobile Money
            Text(
                text = "Opérateur & Numéro de Retrait",
                fontFamily = MulishFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = ToofanMainDark
            )

            // Operators selector grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val operators = listOf(
                    Triple("MPESA", R.drawable.logo_mpesa, "M-Pesa"),
                    Triple("ORANGE", R.drawable.logo_orange_money, "Orange"),
                    Triple("AIRTEL", R.drawable.logo_airtel_money, "Airtel"),
                    Triple("MTN", R.drawable.logo_mtn_money, "MTN")
                )
                operators.forEach { (code, logo, label) ->
                    val isSelected = uiState.withdrawalOperator == code
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) ToofanGreen else Color.LightGray.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.setWithdrawalOperator(code) },
                        color = if (isSelected) ToofanGreen.copy(alpha = 0.08f) else Color.White
                    ) {
                        Column(
                            modifier = Modifier.padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = logo),
                                contentDescription = label,
                                modifier = Modifier.height(24.dp),
                                contentScale = ContentScale.Inside
                            )
                            Text(
                                text = label,
                                fontFamily = MulishFontFamily,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) ToofanGreen else ToofanBodyText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Numéro de téléphone bénéficiaire",
                fontFamily = MulishFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ToofanMainDark
            )
            ToofanInputField(
                value = uiState.withdrawalRecipient,
                onValueChange = { viewModel.setWithdrawalRecipient(it) },
                placeholder = "Ex: +243XXXXXXXXX ou 09...",
                modifier = Modifier.testTag("withdrawal_phone_input")
            )
        }
    }
}

@Composable
fun StepEnterAmount(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (uiState.withdrawalType == "agent_cash") {
            // Show selected agent details
            uiState.searchedAgentProfile?.let { agent ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ToFocusBorderColor.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, ToFocusBorderColor.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ToFocusBorderColor.copy(alpha = 0.15f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = ToFocusBorderColor, modifier = Modifier.size(20.dp))
                            }
                        }
                        Column {
                            Text(
                                text = agent.fullName,
                                fontFamily = MulishFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = ToofanMainDark
                            )
                            val boutiqueName = agent.boutiques?.firstOrNull()?.name ?: "Boutique Agent"
                            Text(
                                text = "Agent Agrée • $boutiqueName",
                                fontSize = 11.sp,
                                color = ToofanBodyText
                            )
                        }
                    }
                }
            }
        } else {
            // Show selected Mobile Money Operator details
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ToofanBlue.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, ToofanBlue.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = ToofanBlue.copy(alpha = 0.15f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = ToofanBlue, modifier = Modifier.size(20.dp))
                        }
                    }
                    Column {
                        Text(
                            text = "Retrait Mobile Money (${uiState.withdrawalOperator})",
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = ToofanMainDark
                        )
                        Text(
                            text = "Numéro : ${uiState.withdrawalRecipient}",
                            fontSize = 11.sp,
                            color = ToofanBodyText
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Montant du Retrait",
            fontFamily = MulishFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = ToofanMainDark
        )

        ToofanInputField(
            value = uiState.withdrawalAmount,
            onValueChange = { viewModel.setWithdrawalAmount(it) },
            placeholder = "Saisir le montant",
            modifier = Modifier.testTag("withdrawal_amount_input")
        )

        Text(
            text = "Devise de facturation",
            fontFamily = MulishFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ToofanMainDark
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val currencies = listOf("USD", "CDF")
            currencies.forEach { curr ->
                val isSelected = uiState.withdrawalCurrency == curr
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) ToofanGreen else Color.LightGray.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { viewModel.setWithdrawalCurrency(curr) },
                    color = if (isSelected) ToofanGreen.copy(alpha = 0.08f) else Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = curr,
                            fontFamily = MulishFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isSelected) ToofanGreen else ToofanMainDark
                        )
                    }
                }
            }
        }

        if (uiState.withdrawalPreviewError != null) {
            Text(
                text = uiState.withdrawalPreviewError,
                color = Color(0xFFFF4868),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StepPreviewWithdrawal(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel
) {
    val preview = uiState.withdrawalPreview
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Récapitulatif & Prévisualisation",
            fontFamily = MulishFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = ToofanMainDark
        )

        preview?.let { p ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ToofanGrey1.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, ToofanGrey1.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RecapRow("Mode de Retrait :", if (uiState.withdrawalType == "agent_cash") "Retrait chez Agent" else "Retrait Mobile Money")
                    if (uiState.withdrawalType == "agent_cash") {
                        RecapRow("Agent :", uiState.searchedAgentProfile?.fullName ?: uiState.withdrawalRecipient)
                    } else {
                        RecapRow("Opérateur :", uiState.withdrawalOperator ?: "")
                        RecapRow("Numéro :", uiState.withdrawalRecipient)
                    }

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f), thickness = 1.dp)

                    RecapRow("Montant demandé :", "${String.format("%.2f", p.amount)} ${p.currency}")
                    RecapRow("Frais calculés :", "${String.format("%.2f", p.fee)} ${p.currency}")
                    RecapRow("Total à débiter :", "${String.format("%.2f", p.total)} ${p.currency}", isBold = true, customColor = ToofanGreen)

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f), thickness = 1.dp)

                    RecapRow("Solde disponible :", "${String.format("%.2f", p.balance)} ${p.currency}")
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (p.sufficient) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = ToofanGreen.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, ToofanGreen.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ToofanGreen, modifier = Modifier.size(16.dp))
                        Text(
                            text = "✓ Solde suffisant pour effectuer l'opération.",
                            fontFamily = MulishFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ToofanGreen
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECEF)),
                    border = BorderStroke(1.dp, Color(0xFFFF4868).copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFFF4868), modifier = Modifier.size(16.dp))
                            Text(
                                text = "⚠ Solde insuffisant !",
                                fontFamily = MulishFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF4868)
                            )
                        }
                        Text(
                            text = "Il vous manque : ${String.format("%.2f", p.missing)} ${p.currency}",
                            fontFamily = MulishFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF4868)
                        )
                        Text(
                            text = "Le bouton de confirmation est indisponible. Aucune transaction ne peut être exécutée.",
                            fontSize = 10.sp,
                            color = ToofanBodyText
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StepConfirmPin(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel
) {
    var pinValue by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Saisir votre Code PIN CashPay",
            fontFamily = MulishFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = ToofanMainDark,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Afin de valider le retrait de ${uiState.withdrawalAmount} ${uiState.withdrawalCurrency}, veuillez saisir votre code confidentiel à 4 chiffres.",
            fontSize = 11.sp,
            color = ToofanBodyText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Simple customized PIN Entry dots or Input field
        ToofanInputField(
            value = pinValue,
            onValueChange = { input ->
                pinValue = input.filter { it.isDigit() }.take(4)
            },
            placeholder = "PIN à 4 chiffres",
            modifier = Modifier
                .width(180.dp)
                .testTag("withdrawal_pin_input")
        )

        if (uiState.withdrawalConfirmError != null) {
            Text(
                text = uiState.withdrawalConfirmError,
                color = Color(0xFFFF4868),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Localized validation button within this step
        ToofanButton(
            title = "Confirmer le Retrait",
            onClick = { viewModel.confirmWithdrawal(userId = 1, pin = pinValue) },
            isLoading = uiState.isWithdrawalConfirmLoading,
            enabled = pinValue.length == 4,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
    }
}

@Composable
fun StepWithdrawalSuccess(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = ToFocusBorderColor, // Gold accent or Green
            modifier = Modifier.size(64.dp)
        )

        Text(
            text = "Retrait Réussi !",
            fontFamily = MulishFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = ToofanMainDark
        )

        Text(
            text = "Votre retrait de ${uiState.withdrawalAmount} ${uiState.withdrawalCurrency} a été traité avec succès par le moteur de transactions CashPay.",
            fontSize = 12.sp,
            color = ToofanBodyText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 14.dp)
        )

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = ToofanGreen.copy(alpha = 0.08f),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RecapRow("Type d'opération :", "Retrait (WD)")
                RecapRow("Montant retiré :", "${uiState.withdrawalAmount} ${uiState.withdrawalCurrency}")
                if (uiState.withdrawalType == "agent_cash") {
                    RecapRow("Point de retrait :", uiState.searchedAgentProfile?.fullName ?: "")
                } else {
                    RecapRow("Opérateur :", uiState.withdrawalOperator ?: "")
                }
                RecapRow("Statut :", "COMPLÉTÉ", isBold = true, customColor = ToofanGreen)
            }
        }
    }
}
