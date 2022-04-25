package fi.metropolia.movesense.component

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FlipCameraAndroid
import androidx.compose.material.icons.outlined.Polymer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import fi.metropolia.movesense.R
import fi.metropolia.movesense.extension.round
import fi.metropolia.movesense.type.MeasureType
import fi.metropolia.movesense.view.measure.MeasureViewModel

@ExperimentalMaterial3Api
@Composable
fun MovesenseGraph(
    measureViewModel: MeasureViewModel
) {
    val entriesX by measureViewModel.entriesX.observeAsState()
    val entriesY by measureViewModel.entriesY.observeAsState()
    val entriesZ by measureViewModel.entriesZ.observeAsState()

    val selectedData by measureViewModel.dataAvg.observeAsState()
    val measureType by measureViewModel.measureType.observeAsState()

    fun setData(): LineData {
        val xSet = LineDataSet(entriesX, "x")
        xSet.axisDependency = YAxis.AxisDependency.LEFT
        xSet.color = Color.Blue.hashCode()
        xSet.setDrawCircles(false)
        xSet.setDrawFilled(false)
        xSet.lineWidth = 2f

        if (!measureViewModel.combineAxis.value!!) {
            val ySet = LineDataSet(entriesY, "y")
            ySet.axisDependency = YAxis.AxisDependency.LEFT
            ySet.setDrawCircles(false)
            ySet.setDrawFilled(false)
            ySet.color = Color.Yellow.hashCode()
            ySet.lineWidth = 2f

            val zSet = LineDataSet(entriesZ, "z")
            zSet.axisDependency = YAxis.AxisDependency.LEFT
            zSet.setDrawCircles(false)
            zSet.setDrawFilled(false)
            zSet.color = Color.Green.hashCode()
            zSet.lineWidth = 2f

            val data = LineData(xSet, ySet, zSet)
            data.setValueTextColor(R.color.md_theme_light_background)
            data.setValueTextSize(9f)
            return data
        } else {
            val data = LineData(xSet)
            data.setValueTextColor(R.color.md_theme_light_background)
            data.setValueTextSize(9f)
            return data
        }
    }

    fun updateData(chart: LineChart) {
        val xSet = chart.data?.getDataSetByIndex(0) as LineDataSet?
        xSet?.values = entriesX

        val ySet = chart.data?.getDataSetByIndex(1) as LineDataSet?
        val zSet = chart.data?.getDataSetByIndex(2) as LineDataSet?

        if (measureViewModel.clearData.value == true) {
            xSet?.values = listOf()
            ySet?.clear()
            zSet?.clear()
            chart.clear()
            measureViewModel.toggleClearData()
        }
        if (!measureViewModel.combineAxis.value!!) {
            ySet?.values = entriesY
            zSet?.values = entriesZ
        }
        chart.data?.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.invalidate()
    }

    val selectedBtnColor = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.surface
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .fillMaxWidth()
                .weight(2F),
        ) {
            OutlinedButton(
                modifier = Modifier
                    .padding(8.dp)
                    .height(40.dp)
                    .weight(3F),
                onClick = {
                    measureViewModel.toggleClearData()
                    measureViewModel.changeMeasureType(MeasureType.Acceleration)
                },
                colors =
                if (measureType == MeasureType.Acceleration) {
                    selectedBtnColor
                } else {
                    ButtonDefaults.outlinedButtonColors()
                }
            ) {
                if (measureType == MeasureType.Acceleration) {
                    Icon(
                        modifier = Modifier.padding(end = 8.dp),
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                }
                Text(text = stringResource(id = R.string.acc))
            }
            OutlinedButton(
                modifier = Modifier
                    .padding(8.dp)
                    .height(40.dp)
                    .weight(2F),
                onClick = {
                    measureViewModel.toggleClearData()
                    measureViewModel.changeMeasureType(MeasureType.Gyro)
                },
                colors =
                if (measureType == MeasureType.Gyro) {
                    selectedBtnColor
                } else {
                    ButtonDefaults.outlinedButtonColors()
                }
            ) {
                if (measureType == MeasureType.Gyro) {
                    Icon(
                        modifier = Modifier.padding(end = 8.dp),
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                }
                Text(text = stringResource(id = R.string.gyro))
            }
        }
        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                .fillMaxWidth()
                .weight(2F),
            horizontalArrangement = Arrangement.Start
        ) {
            OutlinedButton(
                modifier = Modifier
                    .padding(8.dp)
                    .height(40.dp),
                onClick = {
                    measureViewModel.toggleClearData()
                    measureViewModel.changeMeasureType(MeasureType.Magnetic)
                },
                colors =
                if (measureType == MeasureType.Magnetic) {
                    selectedBtnColor
                } else {
                    ButtonDefaults.outlinedButtonColors()
                }
            ) {
                if (measureType == MeasureType.Magnetic) {
                    Icon(
                        modifier = Modifier.padding(end = 8.dp),
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                }
                Text(text = stringResource(id = R.string.magn))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(9F)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context: Context ->
                    val chart = LineChart(context)
                    chart.legend.isEnabled = true

                    val desc = Description()

                    desc.text = context.getString(R.string.live_graph)
                    chart.description = desc
                    chart.data = setData()
                    chart
                },
                update = { chart ->
                    if (chart.data != null &&
                        chart.data.dataSetCount > 0
                    ) {
                        updateData(chart)
                    } else {
                        chart.data = setData()
                        chart.invalidate()
                    }
                }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2F),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            OutlinedButton(
                onClick = {
                    measureViewModel.toggleClearData()
                    measureViewModel.toggleCombineAxis()
                },
                colors =
                if (measureViewModel.combineAxis.value == true) {
                    selectedBtnColor
                } else {
                    ButtonDefaults.outlinedButtonColors()
                }
            ) {
                if (measureViewModel.combineAxis.value == true) {
                    Icon(
                        modifier = Modifier.padding(end = 8.dp),
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                }
                Text(stringResource(id = R.string.combine_axis))
            }
            OutlinedButton(onClick = { measureViewModel.toggleClearData() }) {
                Text(stringResource(id = R.string.clear_graph))
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .weight(3F)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .fillMaxHeight()
                        .background(color = MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp),
                        imageVector = when (measureType) {
                            MeasureType.Acceleration -> Icons.Outlined.DirectionsRun
                            MeasureType.Gyro -> Icons.Outlined.FlipCameraAndroid
                            MeasureType.Magnetic -> Icons.Outlined.Polymer
                            else -> {
                                Icons.Outlined.ErrorOutline
                            }
                        }, contentDescription = null
                    )
                }
                Text(
                    text = measureType!!.name, modifier = Modifier
                        .padding(8.dp)
                        .align(
                            Alignment.CenterVertically
                        )
                )
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("x: ${selectedData!!.x.round(3)}")
                    Text("y: ${selectedData!!.y.round(3)}")
                    Text("z: ${selectedData!!.z.round(3)}")
                }
            }
        }
    }
}