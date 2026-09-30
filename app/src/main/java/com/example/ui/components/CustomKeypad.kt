package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ToofanBodyText
import com.example.ui.theme.ToofanGreen
import com.example.ui.theme.ToofanMainDark
import com.example.ui.theme.ToofanWhite

@Composable
fun CustomKeypad(
    modifier: Modifier = Modifier,
    onDigitClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onBiometricClick: (() -> Unit)? = null
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                row.forEach { digit ->
                    ToofanKeypadKey(
                        text = digit,
                        onClick = { onDigitClick(digit) },
                        testTag = "keypad_digit_$digit"
                    )
                }
            }
        }

        // Bottom row: Biometric / 0 / Backspace
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Action Key (Biometric or Empty)
            if (onBiometricClick != null) {
                ToofanKeypadIconKey(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.toofan_fingerprint),
                            contentDescription = "Empreinte biométrique",
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    onClick = onBiometricClick,
                    testTag = "keypad_biometric_button"
                )
            } else {
                Box(
                    modifier = Modifier
                        .width(88.dp)
                        .height(68.dp)
                )
            }

            // Zero
            ToofanKeypadKey(
                text = "0",
                onClick = { onDigitClick("0") },
                testTag = "keypad_digit_0"
            )

            // Right Action Key (Backspace)
            ToofanKeypadIconKey(
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.toofan_backspace),
                        contentDescription = "Effacer",
                        modifier = Modifier.size(24.dp)
                    )
                },
                onClick = onBackspaceClick,
                testTag = "keypad_backspace_button"
            )
        }
    }
}

@Composable
private fun ToofanKeypadKey(
    text: String,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.94f else 1.0f, label = "key_scale")

    Surface(
        modifier = Modifier
            .width(88.dp)
            .height(68.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(bounded = true),
                onClick = onClick
            )
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        color = ToofanWhite,
        shadowElevation = 1.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ToofanMainDark
            )
        }
    }
}

@Composable
private fun ToofanKeypadIconKey(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.94f else 1.0f, label = "icon_key_scale")

    Surface(
        modifier = Modifier
            .width(88.dp)
            .height(68.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(bounded = true),
                onClick = onClick
            )
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        color = ToofanWhite,
        shadowElevation = 1.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            icon()
        }
    }
}
