package com.app.monitor.adapters.monitor

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.monitor.DetailActivity
import com.app.monitor.R
import com.app.monitor.models.ViewMonitor
import com.app.monitor.ui.home.HomeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MonitorAdapter : RecyclerView.Adapter<MonitorAdapter.MonitorHolder>() {

    var monitor: List<ViewMonitor> = ArrayList()
    private lateinit var activity: Activity
    private var column: Int = 3
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonitorHolder {
        val itemView: View = when(column) {
            3 -> {
                LayoutInflater.from(parent.context).inflate(R.layout.item_monitor, parent, false)
            }
            4 -> {
                LayoutInflater.from(parent.context).inflate(R.layout.item_small_monitor, parent, false)
            }
            else -> { LayoutInflater.from(parent.context).inflate(R.layout.item_very_small_monitor, parent, false) }
        }
        return MonitorHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MonitorHolder, position: Int) {
        val currentMonitor = monitor[position]
        holder.apply {
            textViewCode.text = currentMonitor.code
            textViewTitle.text =currentMonitor.title
            textViewCount.text = currentMonitor.count

            when (currentMonitor.status) {
                "warning" -> {
                    cardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.warning))
                }
                "error" -> {
                    cardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.error))
                }
                "success" -> {
                    cardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.success))
                }
                else -> {
                    cardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.secondary))
                }
            }

            itemView.setOnClickListener {
                val intent = Intent(activity, DetailActivity::class.java)
                intent.putExtra("code", currentMonitor.code)
                activity.startActivity(intent)
            }

            itemView.setOnLongClickListener {
                monitoring(currentMonitor.code, currentMonitor.row, currentMonitor.monitoring!!)
                return@setOnLongClickListener true
            }
        }
    }

    override fun getItemCount(): Int {
        return monitor.size
    }

    fun setMonitor(monitor: List<ViewMonitor>, activity: Activity, column: Int, homeViewModel: HomeViewModel) {
        val monitorDiffUtilCallback = DiffUtilCallback(this.monitor, monitor)
        val monitorDiffResult = DiffUtil.calculateDiff(monitorDiffUtilCallback)
        this.monitor = monitor
        this.activity = activity
        this.column = column
        this.homeViewModel = homeViewModel
        monitorDiffResult.dispatchUpdatesTo(this)
    }

    inner class MonitorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewCode: TextView = itemView.findViewById(R.id.monitor_code)
        var textViewTitle: TextView = itemView.findViewById(R.id.monitor_title)
        var cardView: CardView = itemView.findViewById(R.id.card_view)
        var textViewCount: TextView = itemView.findViewById(R.id.monitor_error_count)
    }

    @SuppressLint("SetTextI18n")
    private fun monitoring(code: String, row: Int, monitoring: Int) {
        val view: View = activity.layoutInflater.inflate(R.layout.dialog_change_view, null)
        val buttonSingle: Button = view.findViewById(R.id.single_change_view)
        val buttonGroup: Button = view.findViewById(R.id.group_change_view)
        val buttonSingleAnException: Button = view.findViewById(R.id.single_change_view_an)
        val buttonGroupAnException: Button = view.findViewById(R.id.group_change_view_an)
        var title = "Не отслеживать:"
        var status = 0

        if (monitoring == 0) {
            title = "Вернуть в отслеживание:"
            status = 1
        }

        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(activity)
                .setCancelable(false)
                .setTitle(title)
                .setView(view)
                .setNegativeButton("Отмена") { _, _ -> }

        val dialog: androidx.appcompat.app.AlertDialog = materialAlertDialogBuilder.show()

        buttonSingle.apply {
            text = "Только $code"
            setOnClickListener {
                homeViewModel.updateMonitoring(status, code)
                dialog.dismiss()
            }
        }

        buttonGroup.apply {
            text = "Всю группу - $row"
            setOnClickListener {
                homeViewModel.updateMonitoringRow(status, row)
                dialog.dismiss()
            }
        }

        buttonSingleAnException.apply {
            text = "Все кроме $code"
            setOnClickListener {
                homeViewModel.updateMonitoringAnException(status, code)
                dialog.dismiss()
            }
        }

        buttonGroupAnException.apply {
            text = "Все руппы кроме - $row"
            setOnClickListener {
                homeViewModel.updateMonitoringRowAnException(status, row)
                dialog.dismiss()
            }
        }
    }
}