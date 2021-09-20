package com.app.monitor.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.chart.common.listener.Event
import com.anychart.chart.common.listener.ListenersInterface
import com.anychart.enums.LegendLayout
import com.app.monitor.R
import org.koin.android.ext.android.inject


class FullscreenDialog : DialogFragment(), View.OnClickListener {

    private val homeViewModel: HomeViewModel by inject()

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_Monitor_DialogFragment)
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fullscreen_dialog, container, false)
        val close: ImageButton = view.findViewById(R.id.fullscreen_dialog_close)
        close.setOnClickListener(this)
        buildChart(view)

        return view
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fullscreen_dialog_close -> dismiss()
        }
    }


    companion object {
        fun newInstance(): FullscreenDialog {
            return FullscreenDialog()
        }
    }

    private fun buildChart(view: View) {
        val pie = AnyChart.pie()
        val list = homeViewModel.getViewMonitor()

        val data: MutableList<DataEntry> = ArrayList()
        data.add(ValueDataEntry("success", list.count { it.status == "success" }))
        data.add(ValueDataEntry("warning", list.count { it.status == "warning" }))
        data.add(ValueDataEntry("error", list.count { it.status == "error" }))

        pie.title("Общая статистика")
        pie.palette(arrayOf("#28a745", "#ffc107", "#dc3545"))
        pie.labels().position("outside")
        pie.legend()
            .position("center-bottom")
            .itemsLayout(LegendLayout.HORIZONTAL)
            .align("center")
        pie.setOnClickListener(object : ListenersInterface.OnClickListener(arrayOf("x", "value")) {
            override fun onClick(event: Event) {
                Toast.makeText(
                    context,
                    event.data["x"].toString() + ": " + event.data["value"],
                    Toast.LENGTH_LONG
                ).show()
            }
        })
        pie.data(data)

        val anyChartView = view.findViewById(R.id.any_chart_view) as AnyChartView
        anyChartView.setProgressBar(view.findViewById(R.id.chart_progress))
        anyChartView.setChart(pie)
    }
}