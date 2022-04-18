package fi.metropolia.movesense.component

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import fi.metropolia.movesense.R
import fi.metropolia.movesense.model.MovesenseDataResponse

@Composable
fun MovesenseGraph(
    graphData: List<MovesenseDataResponse.Array?>,
    combineAxis: Boolean,
    combinedData: List<Double>
) {
    val entriesX = mutableListOf<Entry>()
    val entriesY = mutableListOf<Entry>()
    val entriesZ = mutableListOf<Entry>()

    if (!combineAxis) {
        graphData.forEachIndexed { index, value ->
            if (value != null) {
                entriesX.add(Entry(index.toFloat(), value.x.toFloat()))
                entriesY.add(Entry(index.toFloat(), value.y.toFloat()))
                entriesZ.add(Entry(index.toFloat(), value.z.toFloat()))
            }
        }
    } else {
        combinedData.forEachIndexed { index, value ->
            entriesX.add(Entry(index.toFloat(), value.toFloat()))
        }
    }

    fun setData(): LineData {
        val xSet = LineDataSet(entriesX, "x")
        xSet.axisDependency = YAxis.AxisDependency.LEFT
        xSet.color = Color.Blue.hashCode()
        xSet.setDrawCircles(false)
        xSet.setDrawFilled(false)
        xSet.lineWidth = 2f

        if (!combineAxis) {
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
        val xSet = chart.data.getDataSetByIndex(0) as LineDataSet
        xSet.values = entriesX

        if (!combineAxis) {
            val ySet = chart.data.getDataSetByIndex(1) as LineDataSet
            val zSet = chart.data.getDataSetByIndex(2) as LineDataSet

            ySet.values = entriesY
            zSet.values = entriesZ
        }

        chart.data.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.invalidate()
    }

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