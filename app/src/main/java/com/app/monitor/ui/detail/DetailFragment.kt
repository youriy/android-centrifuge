package com.app.monitor.ui.detail

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.app.monitor.R
import com.app.monitor.core.Centrifuga
import com.app.monitor.core.Helper
import com.app.monitor.core.Helper.removeHTMLTags2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.centrifugal.centrifuge.PublishEvent
import io.github.centrifugal.centrifuge.Subscription
import io.github.centrifugal.centrifuge.SubscriptionEventListener
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.nio.charset.StandardCharsets
import java.util.*


class DetailFragment : Fragment() {

    private var code: String? = null
    private var centrifuga: Centrifuga? = null
    var stringJson: MutableLiveData<String> = MutableLiveData<String>()

    companion object {
        fun newInstance() = DetailFragment()
    }

    private val viewModel: DetailViewModel  by inject()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.detail_fragment, container, false)
        code = this.requireActivity().intent.extras?.getString("code")
        this.requireActivity().title = code
        setObserveStatus(root)
        readCentrifuga(code)
        setObserveTable(root)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail, menu)
        super.onCreateOptionsMenu(menu, inflater);
    }

    override fun onDestroy() {
        super.onDestroy()
        centrifuga?.disconnect()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_delete_logs) {
            deleteLogsDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    private fun setObserveStatus(root: View) {
        val frame = root.findViewById<FrameLayout>(R.id.frame_detail)
        val title = root.findViewById<TextView>(R.id.title)
        val status = root.findViewById<TextView>(R.id.status)
        val update = root.findViewById<TextView>(R.id.update)
        code?.let { code ->
            viewModel.getMonitorByCode(code).observe(viewLifecycleOwner, { list ->
                list?.let {
                    val currentTime = Calendar.getInstance()
                    title.text = removeHTMLTags2(it.title)
                    status.text = "Статус: " + it.status
                    update.text = "Обновлено: " + it.update.let { it1 -> Helper.convertLongToTime(it1) }

                    if (currentTime.timeInMillis - it.update > 5*60 * 1000) {
                        it.status = "secondary"
                    }

                    when (it.status) {
                        "warning" -> {
                            frame.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.warning))
                        }
                        "error" -> {
                            frame.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.error))
                        }
                        "success" -> {
                            frame.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.success))
                        }
                        else -> {
                            frame.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.secondary))
                        }
                    }
                }
            })
        }
    }

    @SuppressLint("InflateParams")
    private fun deleteLogsDialog() {
        MaterialAlertDialogBuilder(this.requireContext())
            .setCancelable(false)
            .setTitle(resources.getString(R.string.dialog_title))
            .setNegativeButton(resources.getString(R.string.dialog_cancel)) { _, _ -> }
            .setPositiveButton(resources.getString(R.string.dialog_ok)) { _, _ ->
                code?.let {
                    viewModel.deleteLogsByCode(it)
                    setHasOptionsMenu(false)
//                    recyclerView!!.visibility = View.INVISIBLE
                    Toast.makeText(this.requireContext(),
                            "Логи для $code - удалены",
                            Toast.LENGTH_LONG).show()
                }
            }.show()
    }

    private fun setObserveTable(root: View) {
        val tableLayout: TableLayout = root.findViewById(R.id.tableLayout)
        val layoutParams: TableRow.LayoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
        val colorZebra = ContextCompat.getColor(this.requireContext(), R.color.zebra)
        val colorBlack = ContextCompat.getColor(this.requireContext(), R.color.black)
        val colorWhite = ContextCompat.getColor(this.requireContext(), R.color.white)
        var countTable = 0

        stringJson.observe(viewLifecycleOwner, {

            try {
                val dataJsonObject = JSONObject(it)
                val header = dataJsonObject.getJSONArray("columns")
                val arrayTable = dataJsonObject.getJSONArray("table")

                if (countTable == arrayTable.length()) {
                    return@observe
                }

                tableLayout.removeAllViews()
                countTable = arrayTable.length()

                val tableRow = TableRow(context)
                tableRow.layoutParams = layoutParams
                tableRow.setBackgroundColor(colorBlack)

                for (i in 0 until header.length()) {
                    val textView = TextView(context)
                    textView.text = header.get(i) as CharSequence?
                    setTableHeader(textView, colorWhite)
                    tableRow.addView(textView)
                }

                tableLayout.addView(tableRow)

                for (row in 0 until arrayTable.length()) {
                    val tableRowData = TableRow(context)
                    tableRowData.layoutParams = layoutParams

                    if (row % 2 == 0) {
                        tableRowData.setBackgroundColor(colorZebra)
                    }

                    val tableData = arrayTable.getJSONArray(row)

                    for (column in 0 until tableData.length()) {
                        val text = TextView(context)
                        setTableData(text, colorBlack)
                        text.text = tableData.get(column) as CharSequence?
                        tableRowData.addView(text)
                    }

                    tableLayout.addView(tableRowData)
                }
            } catch (e: Exception) {
                println(e)
                tableLayout.removeAllViews()
                return@observe
            }
        })
    }

    private fun setTableHeader(textView: TextView, color: Int) {
        textView.apply {
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
            setTextColor(color)
            setPadding(20, 10, 20, 10)
        }
    }

    private fun setTableData(textView: TextView, color: Int) {
        textView.apply {
            setPadding(20, 0, 20, 0)
            setTextColor(color)
            maxWidth = 820
        }
    }

    private fun readCentrifuga(code: String?) {
        val subListener: SubscriptionEventListener = object : SubscriptionEventListener() {
            override fun onPublish(sub: Subscription?, event: PublishEvent?) {
                super.onPublish(sub, event)
                val data = String(event!!.data, StandardCharsets.UTF_8)

                if (data.isNotEmpty()) {
                    try {
                        stringJson.postValue(data)
                    } catch (e: Exception) {
                        println(e)
                    }
                }
            }
        }

        centrifuga = Centrifuga(subListener, Centrifuga.channel + "_" + code)
    }
}