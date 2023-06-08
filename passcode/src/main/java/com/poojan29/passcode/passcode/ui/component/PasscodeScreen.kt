package com.poojan29.passcode.passcode.ui.component

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poojan29.passcode.R
import com.poojan29.passcode.passcode.ui.theme.PasscodeKeyButtonStyle
import com.poojan29.passcode.passcode.util.PasscodeListener
import com.poojan29.passcode.passcode.util.PasscodeManager
import com.poojan29.passcode.passcode.viewmodel.PasscodeViewModel

@Composable
fun PasscodeScreen(
    viewModel: PasscodeViewModel,
    passcodeListener: PasscodeListener,
    passcodeManager: PasscodeManager,
) {

    val activeStep by viewModel.activeStep.collectAsState()
    val scrollState = rememberScrollState()
    viewModel.isPasscodeAlreadySet = passcodeManager.hasDragPasscode

    LaunchedEffect(key1 = true) {
        viewModel.onPassCodeReceive.collect {
            passcodeListener.onPassCodeReceive(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Header(
            activeStep = activeStep,
            isPasscodeAlreadySet = passcodeManager.hasPasscode
        )
        if (!passcodeManager.hasPasscode) {
            StepIndicator(
                modifier = Modifier.fillMaxWidth(),
                activeStep = activeStep,
            )
        }
        Spacer(
            modifier = Modifier.height(30.dp)
        )
        DotIndicator(
            modifier = Modifier.fillMaxWidth(),
            viewModel = viewModel,
            passcodeListener = passcodeListener
        )
        Spacer(
            modifier = Modifier.height(100.dp)
        )
        PassCodeKeyView(
            modifier = Modifier.padding(0.dp, 50.dp),
            viewModel = viewModel
        )
        if (passcodeManager.isPasscodeWrong) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp),
                text = "Forgot Passcode",
                style = TextStyle(
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun Header(
    activeStep: PasscodeViewModel.Step,
    isPasscodeAlreadySet: Boolean,
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 40.dp, 0.dp, 0.dp),
        text = if (isPasscodeAlreadySet) {
            "Enter your Passcode"
        } else {
            if (activeStep == PasscodeViewModel.Step.CREATE) {
                "Create Passcode"
            } else {
                "Confirm Passcode"
            }
        },
        style = TextStyle(
            fontSize = 26.sp
        ),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun StepIndicator(
    modifier: Modifier,
    activeStep: PasscodeViewModel.Step,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            space = 10.dp,
            alignment = Alignment.CenterHorizontally
        )
    ) {

        repeat(PasscodeViewModel.TOTAL_STEPS) { step ->
            val isActiveStep = step <= activeStep.index
            val stepColor =
                if (isActiveStep) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary

            Box(
                modifier = Modifier
                    .size(
                        width = 70.dp,
                        height = 6.dp
                    )
                    .background(
                        color = stepColor,
                        shape = MaterialTheme.shapes.medium
                    )
            )
        }

    }
}

@Composable
fun DotIndicator(
    modifier: Modifier,
    viewModel: PasscodeViewModel,
    passcodeListener: PasscodeListener,
) {
    val view = LocalView.current
    val filledDots by viewModel.filledDots.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.onPassCodeReject.collect {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                view.performHapticFeedback(HapticFeedbackConstants.REJECT)
            }
            passcodeListener.onPasscodeReject()
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            space = 20.dp,
            alignment = Alignment.CenterHorizontally
        )
    ) {
        repeat(PasscodeViewModel.PASSCODE_LENGTH) { dot ->
            val isCurrentDot = dot + 1 <= filledDots
            val dotColor =
                if (isCurrentDot) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary

            Box(
                modifier = Modifier
                    .size(
                        width = 15.dp,
                        height = 15.dp
                    )
                    .background(
                        color = dotColor,
                        shape = MaterialTheme.shapes.medium
                    )
            )
        }
    }
}


@Composable
fun PassCodeKeyView(
    modifier: Modifier,
    viewModel: PasscodeViewModel
) {
    val onEnterKeyClick = { keyTitle: String ->
        viewModel.enterKey(keyTitle)
    }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp, 0.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    10.dp
                )
            ) {
                PassCodeKey(
                    modifier = Modifier.weight(weight = 1.0f),
                    keyTitle = "1",
                    onClick = onEnterKeyClick
                )
                PassCodeKey(
                    modifier = Modifier.weight(weight = 1.0f),
                    keyTitle = "2",
                    onClick = onEnterKeyClick
                )
                PassCodeKey(
                    modifier = Modifier.weight(weight = 1.0f),
                    keyTitle = "3",
                    onClick = onEnterKeyClick
                )
            }
            Spacer(modifier = Modifier.height(height = 12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp, 0.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    10.dp
                )
            ) {
                PassCodeKey(
                    modifier = Modifier.weight(weight = 1.0f),
                    keyTitle = "4",
                    onClick = onEnterKeyClick
                )
                PassCodeKey(
                    modifier = Modifier.weight(weight = 1.0f),
                    keyTitle = "5",
                    onClick = onEnterKeyClick
                )
                PassCodeKey(
                    modifier = Modifier.weight(weight = 1.0f),
                    keyTitle = "6",
                    onClick = onEnterKeyClick
                )
            }
            Spacer(modifier = Modifier.height(height = 12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp, 0.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    10.dp
                )
            ) {
                PassCodeKey(
                    modifier = Modifier.weight(weight = 1.0f),
                    keyTitle = "7",
                    onClick = onEnterKeyClick
                )
                PassCodeKey(
                    modifier = Modifier.weight(weight = 1.0f),
                    keyTitle = "8",
                    onClick = onEnterKeyClick
                )
                PassCodeKey(
                    modifier = Modifier.weight(weight = 1.0f),
                    keyTitle = "9",
                    onClick = onEnterKeyClick
                )
            }
            Spacer(modifier = Modifier.height(height = 12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp, 0.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    10.dp
                )
            ) {
                PassCodeKey(
                    modifier = Modifier.weight(weight = 1.0F),
                    keyIcon = ImageVector.vectorResource(id = R.drawable.ic_delete),
                    keyIconContentDescription = "Delete Passcode Key Button",
                    onClick = {
                        viewModel.deleteAllKey()
                    }
                )
                PassCodeKey(
                    modifier = Modifier.weight(weight = 1.0F),
                    keyTitle = "0",
                    onClick = onEnterKeyClick
                )
                PassCodeKey(
                    modifier = Modifier.weight(weight = 1.0F),
                    keyIcon = ImageVector.vectorResource(id = R.drawable.ic_clear),
                    keyIconContentDescription = "Delete Passcode Key Button",
                    onClick = {
                        viewModel.deleteKey()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PassCodeKey(
    modifier: Modifier,
    keyTitle: String = "",
    keyIcon: ImageVector? = null,
    keyIconContentDescription: String? = null,
    onClick: ((String) -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    Box(
        modifier = modifier
            .aspectRatio(2f)
            .combinedClickable(
                onClick = { onClick?.invoke(keyTitle) },
                role = Role.Button,
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = false,
                    radius = 50.dp,
                    color = if (isSystemInDarkTheme()) {
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            ),
    ) {
        if (keyIcon == null) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = keyTitle,
                style = PasscodeKeyButtonStyle,
            )
        } else {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                imageVector = keyIcon,
                contentDescription = keyIconContentDescription
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PassCode(@PreviewParameter(MyInterfacePreviewProvider::class) passcodeListener: PasscodeListener) {
    PasscodeScreen(
        viewModel = PasscodeViewModel(PasscodeManager(LocalContext.current)),
        passcodeListener,
        PasscodeManager(LocalContext.current)
    )
}

class MyInterfacePreviewProvider : PreviewParameterProvider<PasscodeListener> {
    override val values: Sequence<PasscodeListener>
        get() = sequenceOf(object : PasscodeListener {

            override fun onPasscodeReject() {

            }

            override fun onPassCodeReceive(passcode: String) {

            }

        })
}