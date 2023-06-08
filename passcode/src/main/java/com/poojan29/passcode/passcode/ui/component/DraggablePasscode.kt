package com.poojan29.passcode.passcode.ui.component

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poojan29.passcode.R
import com.poojan29.passcode.passcode.util.Direction
import com.poojan29.passcode.passcode.util.DragObserver
import com.poojan29.passcode.passcode.util.PasscodeListener
import com.poojan29.passcode.passcode.util.PasscodeManager
import com.poojan29.passcode.passcode.viewmodel.PasscodeViewModel
import kotlin.math.abs

@Composable
fun DraggablePasscode(
    dragViewModel: PasscodeViewModel,
    passcodeListener: PasscodeListener,
    passcodeManager: PasscodeManager,
) {

    val activeDragStep by dragViewModel.activeDragStep.collectAsState()
    val scrollState = rememberScrollState()
    dragViewModel.isDragPasscodeAlreadySet = passcodeManager.hasDragPasscode

    val dragObserver = remember {
        DragObserver()
    }

    LaunchedEffect(key1 = true) {
        dragViewModel.onDragPassCodeReject.collect {
            dragObserver.lockDirections.clear()
            passcodeListener.onPasscodeReject()
        }
    }

    LaunchedEffect(key1 = true) {
        dragViewModel.onDragPassCodeReceive.collect {
            dragObserver.lockDirections.clear()
            passcodeListener.onPassCodeReceive(it)
        }
    }

    LaunchedEffect(key1 = true) {
        dragViewModel.onDragPassCodeConfirm.collect {
            dragObserver.lockDirections.clear()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top
    ) {
        DragHeader(
            activeDragStep = activeDragStep,
            isDragPasscodeAlreadySet = passcodeManager.hasDragPasscode
        )
        Spacer(
            modifier = Modifier.height(30.dp)
        )
        if (!passcodeManager.hasDragPasscode) {
            DragStepIndicator(
                modifier = Modifier.fillMaxWidth(),
                activeDragStep = activeDragStep
            )
        }
        Spacer(
            modifier = Modifier.height(30.dp)
        )
        DragDotIndicator(
            modifier = Modifier.fillMaxWidth(),
            dragViewModel = dragViewModel,
            dragObserver = dragObserver,
        )
        Spacer(
            modifier = Modifier.height(100.dp)
        )
        DragBox(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            dragObserver.clearDirections()
                        },
                        onDragEnd = {
                            if (!dragObserver.listLockCompleted) {
                                dragViewModel.onDragEnd(dragObserver.directions.last())
                                dragObserver.addLockDirection(dragObserver.directions.last())
                            }
                        },
                    ) { _: PointerInputChange, dragAmount: Offset ->
                        run {
                            val dragDirection = getDragDirection(dragAmount)
                            dragObserver.addDirection(dragDirection)
                        }

                    }
                }
        )
    }
}

fun getDragDirection(dragAmount: Offset): Int {
    val absX = abs(dragAmount.x)
    val absY = abs(dragAmount.y)

    return if (absX > absY) {
        if (dragAmount.x > 0) Direction.RIGHT.id else Direction.LEFT.id
    } else {
        if (dragAmount.y > 0) Direction.DOWN.id else Direction.TOP.id
    }
}

@Composable
fun DragBox(
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .padding(15.dp, 15.dp)
            .clip(shape = RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.direction_64),
            contentDescription = "icon",
            modifier = Modifier
                .rotate(45f)
                .align(alignment = Alignment.Center),
            tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
        )
        Text(
            modifier = Modifier
                .align(alignment = Alignment.Center)
                .padding(25.dp),
            text = "Drag your finger here only in one direction.",
            style = TextStyle(
                fontSize = 18.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DragHeader(
    activeDragStep: PasscodeViewModel.DragStep,
    isDragPasscodeAlreadySet: Boolean
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 40.dp, 0.dp, 0.dp),
        text =
        if (isDragPasscodeAlreadySet) {
            "Drag your Passcode"
        } else {
            if (activeDragStep == PasscodeViewModel.DragStep.CREATE) {
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
fun DragStepIndicator(
    modifier: Modifier,
    activeDragStep: PasscodeViewModel.DragStep,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            space = 10.dp,
            alignment = Alignment.CenterHorizontally
        )
    ) {

        repeat(PasscodeViewModel.TOTAL_DRAG_STEPS) { step ->
            val isActiveStep = step <= activeDragStep.index
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
fun DragDotIndicator(
    modifier: Modifier,
    dragViewModel: PasscodeViewModel,
    dragObserver: DragObserver,
) {
    val filledDots by dragViewModel.filledDragDots.collectAsState()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            space = 20.dp,
            alignment = Alignment.CenterHorizontally
        )
    ) {
        repeat(PasscodeViewModel.DRAG_PASSCODE_LENGTH) { dot ->
            val isCurrentDot = dot + 1 <= filledDots
            val dotColor =
                if (isCurrentDot) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary

            Box(
                modifier = Modifier
                    .size(
                        width = 20.dp,
                        height = 20.dp
                    )
                    .background(
                        color = dotColor,
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                if (isCurrentDot) {
                    Icon(
                        painter = painterResource(id = R.drawable.up_arrow_24),
                        contentDescription = "arrow",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.rotate(
                            when (dragObserver.lockDirections[dot]) {
                                Direction.TOP.id -> {
                                    0f
                                }
                                Direction.RIGHT.id -> {
                                    90f
                                }

                                Direction.DOWN.id -> {
                                    180f
                                }

                                Direction.LEFT.id -> {
                                    270f
                                }
                                else -> {
                                    return
                                }
                            }
                        )
                    )
                }
            }
        }
    }
}
