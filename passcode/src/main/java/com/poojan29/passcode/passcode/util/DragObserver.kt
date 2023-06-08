package com.poojan29.passcode.passcode.util

class DragObserver {
    private val _directionIds = mutableListOf<Int>()
    private val _lockDirectionIds = mutableListOf<Int>()

    val directions: MutableList<Int>
        get() = _directionIds

    val lockDirections: MutableList<Int>
        get() = _lockDirectionIds

    val listLockCompleted: Boolean
        get() = _lockDirectionIds.size == LOCK_PATTERN_SIZE

    fun addDirection(directionId: Int) {
        _directionIds.add(directionId)
    }

    fun addLockDirection(directionId: Int) {
        if (!listLockCompleted) {
            _lockDirectionIds.add(directionId)
        }
    }

    fun clearDirections() {
        _directionIds.clear()
    }

    companion object {
        private const val LOCK_PATTERN_SIZE = 12
    }
}