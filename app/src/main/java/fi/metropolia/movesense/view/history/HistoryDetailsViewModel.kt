package fi.metropolia.movesense.view.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import fi.metropolia.movesense.database.MeasurementAccelerometer
import fi.metropolia.movesense.database.MeasurementGyroscope
import fi.metropolia.movesense.database.MeasurementInformation
import fi.metropolia.movesense.database.MeasurementMagnetometer
import fi.metropolia.movesense.model.MovesenseLogDataResponse
import fi.metropolia.movesense.repository.MeasurementRepository
import fi.metropolia.movesense.type.MeasureType
import fi.metropolia.movesense.util.DateUtil
import fi.metropolia.movesense.view.measure.MeasureViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sqrt

class HistoryDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val measurementRepository = MeasurementRepository(application.applicationContext)

    private var accelerationData: List<MeasurementAccelerometer>? = null
    private var gyroData: List<MeasurementGyroscope>? = null
    private var magnData: List<MeasurementMagnetometer>? = null

    private var _measureType = MutableLiveData(MeasureType.Acceleration)
    val measureType: LiveData<MeasureType>
        get() = _measureType

    private var _combineAxis = MutableLiveData(false)
    val combineAxis: LiveData<Boolean>
        get() = _combineAxis

    fun toggleCombineAxis() {
        _combineAxis.postValue(!combineAxis.value!!)
    }

    fun toggleClearData() {
        _entriesX.postValue(listOf())
        _entriesY.postValue(listOf())
        _entriesZ.postValue(listOf())
    }

    private var _entriesX = MutableLiveData<List<Entry>>()
    val entriesX: LiveData<List<Entry>>
        get() = _entriesX

    private var _entriesY = MutableLiveData<List<Entry>>()
    val entriesY: LiveData<List<Entry>>
        get() = _entriesY

    private var _entriesZ = MutableLiveData<List<Entry>>()
    val entriesZ: LiveData<List<Entry>>
        get() = _entriesZ

    fun changeMeasureType(measureType: MeasureType) {
        _measureType.postValue(measureType)
        getEntries()
    }

    fun getData(measurementInfo: MeasurementInformation) {
        viewModelScope.launch(Dispatchers.IO) {
            accelerationData = measurementInfo.id?.let {
                measurementRepository.getAccelerometerData(it)
            }
            gyroData = measurementInfo.id?.let {
                measurementRepository.getGyroscopeData(it)
            }
            magnData = measurementInfo.id?.let {
                measurementRepository.getMagnetometerData(it)
            }
            getEntries()
        }
    }

    private fun getEntries() {
        val data: List<MovesenseLogDataResponse.Data>? = when (measureType.value!!) {
            MeasureType.Acceleration -> accelerationData?.map { value ->
                MovesenseLogDataResponse.Data(value.x, value.y, value.z)
            }
            MeasureType.Gyro -> gyroData?.map { value ->
                MovesenseLogDataResponse.Data(value.x, value.y, value.z)
            }
            MeasureType.Magnetic -> magnData?.map { value ->
                MovesenseLogDataResponse.Data(value.x, value.y, value.z)
            }
        }
        if (combineAxis.value == true) {
            _entriesX.postValue(
                data?.mapIndexed { index, value ->
                    Entry(
                        index.toFloat(),
                        sqrt(
                            value.x.pow(2) +
                                    value.y.pow(2) +
                                    value.z.pow(2)
                        ).minus(if (measureType.value == MeasureType.Acceleration) G else 0.0) //subtract gravity if acceleration is selected
                            .toFloat()
                    )

                }
            )
            _entriesY.postValue(listOf())
            _entriesZ.postValue(listOf())
        } else {
            _entriesX.postValue(data?.mapIndexed { index, value ->
                Entry(index.toFloat(), value.x.toFloat())
            })
            _entriesY.postValue(data?.mapIndexed { index, value ->
                Entry(index.toFloat(), value.y.toFloat())
            })
            _entriesZ.postValue(data?.mapIndexed { index, value ->
                Entry(index.toFloat(), value.z.toFloat())
            })
        }
    }

    companion object {
        const val G = 9.81
    }
}