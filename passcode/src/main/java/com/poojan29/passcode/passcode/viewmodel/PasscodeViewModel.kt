package com.poojan29.passcode.passcode.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poojan29.passcode.passcode.util.PasscodeManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.StringBuilder

class PasscodeViewModel(
    private val passcodeManager: PasscodeManager
) : ViewModel() {

    private var _isPasscodeAlreadySet = mutableStateOf(passcodeManager.hasPasscode)

    private val _onPassCodeConfirm = MutableSharedFlow<String>()
    private val _onPassCodeReject = MutableSharedFlow<Unit>()
    private val _onPassCodeReceive = MutableSharedFlow<String>()

    private val _activeStep = MutableStateFlow(Step.CREATE)
    private val _currentDot = MutableStateFlow(0)

    private val createPassCode = StringBuilder()
    private val confirmPassCode = StringBuilder()

    val onPassCodeConfirm = _onPassCodeConfirm.asSharedFlow()
    val onPassCodeReject = _onPassCodeReject.asSharedFlow()
    val onPassCodeReceive = _onPassCodeReceive.asSharedFlow()

    val activeStep = _activeStep.asStateFlow()
    val filledDots = _currentDot.asStateFlow()

    var isPasscodeAlreadySet = _isPasscodeAlreadySet.value

    private val _isDragPasscodeAlreadySet = mutableStateOf(passcodeManager.hasDragPasscode)

    private val _onDragPassCodeConfirm = MutableSharedFlow<String>()
    private val _onDragPassCodeReject = MutableSharedFlow<Unit>()
    private val _onDragPassCodeReceive = MutableSharedFlow<String>()

    private val _activeDragStep = MutableStateFlow(DragStep.CREATE)
    private val _currentDragDot = MutableStateFlow(0)

    private val createDragPassCode = mutableListOf<Int>()
    private val confirmDragPassCode = mutableListOf<Int>()

    val onDragPassCodeConfirm = _onDragPassCodeConfirm.asSharedFlow()
    val onDragPassCodeReject = _onDragPassCodeReject.asSharedFlow()
    val onDragPassCodeReceive = _onDragPassCodeReceive.asSharedFlow()

    val activeDragStep = _activeDragStep.asStateFlow()
    val filledDragDots = _currentDragDot.asStateFlow()

    var isDragPasscodeAlreadySet = _isDragPasscodeAlreadySet.value

    private fun emitActiveStep(activeStep: Step) = viewModelScope.launch {
        _activeStep.emit(activeStep)
    }

    private fun emitFilledDots(filledDots: Int) = viewModelScope.launch {
        _currentDot.emit(filledDots)
    }

    private fun emitOnPassCodeConfirm(confirmPasscode: String) = viewModelScope.launch {
        _onPassCodeConfirm.emit(confirmPasscode)
    }

    private fun emitOnPassCodeReject() = viewModelScope.launch {
        _onPassCodeReject.emit(Unit)
    }

    private fun emitOnPasscodeReceive(receivedPasscode: String) = viewModelScope.launch {
        _onPassCodeReceive.emit(receivedPasscode)
    }

    private fun emitActiveDragStep(activeDragStep: DragStep) = viewModelScope.launch {
        _activeDragStep.emit(activeDragStep)
    }

    //Drag
    private fun emitFilledDragDots(filledDragDots: Int) = viewModelScope.launch {
        _currentDragDot.emit(filledDragDots)
    }

    private fun emitOnDragPassCodeConfirm(confirmDragPasscode: String) = viewModelScope.launch {
        _onDragPassCodeConfirm.emit(confirmDragPasscode)
    }

    private fun emitOnDragPassCodeReject() = viewModelScope.launch {
        _onDragPassCodeReject.emit(Unit)
    }

    private fun emitOnDragPasscodeReceive(receivedDragPasscode: String) = viewModelScope.launch {
        _onDragPassCodeReceive.emit(receivedDragPasscode)
    }

    init {
        resetData()
        resetDragData()
    }

    private fun resetDragData() {
        emitActiveDragStep(DragStep.CREATE)
        emitFilledDragDots(0)

        createDragPassCode.clear()
        confirmDragPassCode.clear()
    }

    private fun resetData() {
        emitActiveStep(Step.CREATE)
        emitFilledDots(0)

        createPassCode.clear()
        confirmPassCode.clear()
    }

    fun onDragEnd(directionId: Int) {
        if (_currentDragDot.value >= DRAG_PASSCODE_LENGTH) {
            return
        }

        emitFilledDragDots(
            if (_activeDragStep.value == DragStep.CREATE) {
                createDragPassCode.add(directionId)
                createDragPassCode.size
            } else {
                confirmDragPassCode.add(directionId)
                confirmDragPassCode.size
            }
        )

        if (_currentDragDot.value == DRAG_PASSCODE_LENGTH) {
            if (_isDragPasscodeAlreadySet.value) {
                emitOnDragPasscodeReceive(createDragPassCode.toString())
                resetDragData()
            } else if (_activeDragStep.value == DragStep.CREATE) {
                emitActiveDragStep(DragStep.CONFIRM)
                emitFilledDragDots(0)
            } else {
                if (createDragPassCode == confirmDragPassCode) {
                    emitOnDragPassCodeConfirm(confirmDragPassCode.toString())
                    passcodeManager.saveDragPasscode(confirmDragPassCode.toString())
                    _isDragPasscodeAlreadySet.value = true
                    resetDragData()
                } else {
                    emitOnDragPassCodeReject()
                    resetDragData()
                }
            }
        }

    }

    fun enterKey(key: String) {
        if (_currentDot.value >= PASSCODE_LENGTH) {
            return
        }

        emitFilledDots(
            if (_activeStep.value == Step.CREATE) {
                createPassCode.append(key)
                createPassCode.length
            } else {
                confirmPassCode.append(key)
                confirmPassCode.length
            }
        )

        if (_currentDot.value == PASSCODE_LENGTH) {
            if (_isPasscodeAlreadySet.value) {
                emitOnPasscodeReceive(createPassCode.toString())
                resetData()
            } else if (_activeStep.value == Step.CREATE) {
                emitActiveStep(Step.CONFIRM)
                emitFilledDots(0)
            } else {
                if (createPassCode.toString() == confirmPassCode.toString()) {
                    emitOnPassCodeConfirm(confirmPasscode = confirmPassCode.toString())
                    passcodeManager.savePasscode(confirmPassCode.toString())
                    _isPasscodeAlreadySet.value = true
                    resetData()
                } else {
                    emitOnPassCodeReject()
                    resetData()
                }
            }
        }
    }

    fun deleteKey() {
        _currentDot.tryEmit(
            if (_activeStep.value == Step.CREATE) {
                if (createPassCode.isNotEmpty()) {
                    createPassCode.deleteAt(createPassCode.length - 1)
                }

                createPassCode.length
            } else {
                if (confirmPassCode.isNotEmpty()) {
                    confirmPassCode.deleteAt(confirmPassCode.length - 1)
                }
                confirmPassCode.length
            }
        )
    }

    fun deleteAllKey() {
        if (_activeStep.value == Step.CREATE) {
            createPassCode.clear()
        } else {
            confirmPassCode.clear()
        }
        resetData()
        emitFilledDots(0)
    }

    enum class Step(var index: Int) {
        CREATE(0),
        CONFIRM(1)
    }

    enum class DragStep(var index: Int) {
        CREATE(0),
        CONFIRM(1)
    }

    companion object {
        const val TOTAL_STEPS = 2
        const val PASSCODE_LENGTH = 6

        const val TOTAL_DRAG_STEPS = 2
        const val DRAG_PASSCODE_LENGTH = 6
    }

}