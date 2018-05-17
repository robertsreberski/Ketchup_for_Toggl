package pl.robertsreberski.ketchup.pojos

import android.arch.lifecycle.MutableLiveData

/**
 * Author: Robert Sreberski
 * Creation time: 13:00 13/05/2018
 * Package name: pl.robertsreberski.ketchup.pojos
 */
data class Timer(
        var state: MutableLiveData<TimeEntry.Type> = MutableLiveData(),
        var start: MutableLiveData<Long> = MutableLiveData(),
        var elapsed: MutableLiveData<Long> = MutableLiveData(),
        var remaining: MutableLiveData<Long> = MutableLiveData(),
        var estimatedEnd: MutableLiveData<Long> = MutableLiveData()
) {
    companion object {
        const val NOT_SET: Long = 0
    }

    var _state: TimeEntry.Type = TimeEntry.Type.INACTIVE
        set(value) {
            state.postValue(value)
            field = value
        }
    var _start: Long = NOT_SET
        set(value) {
            start.postValue(value)
            field = value
        }
    var _elapsed: Long = 0L
        set(value) {
            elapsed.postValue(value)
            field = value
        }
    var _remaining: Long = NOT_SET
        set(value) {
            remaining.postValue(value)
            field = value
        }
    var _estimatedEnd: Long = NOT_SET
        set(value) {
            estimatedEnd.postValue(value)
            field = value
        }

    var _plannedDuration: Long = 0
}