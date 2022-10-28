package com.jusqre.greenpath.util

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skt.Tmap.TMapPoint

object LocationStore {
    private val _lastLocation = MutableLiveData<Location>()
    val lastLocation: LiveData<Location>
        get() = _lastLocation
    val lastTmapPoint: TMapPoint
        get() = TMapPoint(lastLocation.value?.latitude ?: 0.0, lastLocation.value?.longitude ?: 0.0)

    fun updateLocation(location: Location) {
        _lastLocation.postValue(location)
    }
}
