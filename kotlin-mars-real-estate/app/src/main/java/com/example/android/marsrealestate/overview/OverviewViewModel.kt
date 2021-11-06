
package com.example.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsApiFilter
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class MarsApiStatus { LOADING, ERROR, DONE }
/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {
    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<MarsApiStatus>()
    // The external immutable LiveData for the request status String
    val status: LiveData<MarsApiStatus>
        get() = _status

    private val _properties = MutableLiveData<List<MarsProperty>>()
    val property: LiveData<List<MarsProperty>>
        get() = _properties

    // Add _navigateToSelectedProperty MutableLiveData externalized as LiveData
    private val _navigateToSelectedProperty = MutableLiveData<MarsProperty>()
    val navigateToSelectedProperty: LiveData<MarsProperty>
        get() = _navigateToSelectedProperty

    // Create a Coroutines scope using a Job to be cancel when needed
    private val viewModelJob = Job()
    // Coroutines runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    //Call getMarsRealEstateProperties() on init so we can display status immediately.
    init {
        getMarsRealEstateProperties(MarsApiFilter.SHOW_ALL)
    }

    //Sets the value of the status LiveData to the Mars API status.
    private fun getMarsRealEstateProperties(filter:MarsApiFilter) {
        coroutineScope.launch {
            // Get deferred object
            var getPropertiesDeferred = MarsApi.retrofitService.getProperties(filter.value)
            try {
                _status.value = MarsApiStatus.LOADING
                // A waite the completion of our Retrofit request
                var listResult = getPropertiesDeferred.await()
                _status.value = MarsApiStatus.DONE
                //_status.value = "Success: ${listResult.size} Mars properties retrived "
                if (listResult.size > 0) {
                    _properties.value = listResult
                }
            } catch (e: Exception) {
                //_status.value = "Failure: ${e.message}"
                _status.value = MarsApiStatus.ERROR
                _properties.value = ArrayList()
            }
        }
    }
    /* When the ViewModel is finished , we cancel coroutine viewModelJob
       which tells the Retrofit service to stop */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    // ADD displayPropertyDetails
    fun displayPropertyDetails(marsProperty: MarsProperty) {
        _navigateToSelectedProperty.value = marsProperty
    }

    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }

    fun updateFilter(filter: MarsApiFilter){
        getMarsRealEstateProperties(filter)
    }

}
