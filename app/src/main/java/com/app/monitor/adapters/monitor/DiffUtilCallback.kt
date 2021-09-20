package com.app.monitor.adapters.monitor

import androidx.recyclerview.widget.DiffUtil
import com.app.monitor.models.ViewMonitor


class DiffUtilCallback(oldList: List<ViewMonitor>, newList: List<ViewMonitor>) : DiffUtil.Callback() {

    private val oldList: List<ViewMonitor>
    private val newList: List<ViewMonitor>

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldMonitor: ViewMonitor = oldList[oldItemPosition]
        val newMonitor: ViewMonitor = newList[newItemPosition]
        return oldMonitor.code == newMonitor.code
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldMonitor: ViewMonitor = oldList[oldItemPosition]
        val newMonitor: ViewMonitor = newList[newItemPosition]
        return (oldMonitor.title == newMonitor.title &&
                oldMonitor.count == newMonitor.count &&
                oldMonitor.status == newMonitor.status)

    }

    init {
        this.oldList = oldList
        this.newList = newList
    }
}