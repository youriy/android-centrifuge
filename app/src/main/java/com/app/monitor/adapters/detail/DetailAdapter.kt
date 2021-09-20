package com.app.monitor.adapters.detail

/**
 * На данные момент не используется
 * Пример вызова:
 *  val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(detailAdapter)
    val touchHelper = ItemTouchHelper(callback)
    touchHelper.attachToRecyclerView(recyclerView)
 */
import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.monitor.R
import com.app.monitor.core.Helper
import com.app.monitor.db.log.Log
import com.app.monitor.ui.detail.DetailViewModel

class DetailAdapter : RecyclerView.Adapter<DetailAdapter.DetailHolder>() {

    private var log:  MutableList<Log> = mutableListOf()
    private lateinit var activity: Activity
    private lateinit var detailViewModel: DetailViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_detail, parent, false)
        return DetailHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DetailHolder, position: Int) {
        val currentLog = log[position]
        holder.textViewStatus.text = currentLog.status
        holder.textViewTitle.text = Helper.removeHTMLTags2(currentLog.title)
        holder.textViewStartEnd.text = Helper.getTimeError(currentLog.start, currentLog.end)

        when(currentLog.view) {
            0 -> {
                holder.textViewNew.visibility = View.VISIBLE
            }
            else -> { holder.textViewNew.visibility = View.INVISIBLE }
        }

        when (currentLog.status) {
            "warning" -> {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.warning))
            }
            else -> { holder.cardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.error)) }
        }
    }

    override fun getItemCount(): Int {
        return log.size
    }

    fun setLog(log: List<Log>, activity: Activity, detailViewModel: DetailViewModel) {
        this.log.addAll(log)
        this.activity = activity
        this.detailViewModel = detailViewModel
    }

    inner class DetailHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewStatus: TextView = itemView.findViewById(R.id.log_status)
        var textViewTitle: TextView = itemView.findViewById(R.id.log_title)
        var textViewStartEnd: TextView = itemView.findViewById(R.id.log_start_end)
        var cardView: CardView = itemView.findViewById(R.id.card_view_detail)
        var textViewNew: TextView = itemView.findViewById(R.id.new_error)
    }

    fun onItemDismiss(position: Int) {
        log[position].id?.let { detailViewModel.deleteLogById(it) }
        log.removeAt(position)
        notifyItemRemoved(position)
    }
}