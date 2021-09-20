package com.app.monitor.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.core.widget.doOnTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.app.monitor.R
import com.app.monitor.SettingsActivity
import com.app.monitor.adapters.monitor.MonitorAdapter
import com.app.monitor.core.PreferenceHelper
import com.app.monitor.core.PreferenceHelper.get
import com.app.monitor.core.PreferenceHelper.set
import com.app.monitor.service.ReadCentrifuga
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import org.koin.android.ext.android.inject


class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by inject()
    private lateinit var preference: SharedPreferences
    private var recyclerView: RecyclerView? = null
    private var drawerLayout: DrawerLayout? = null
    private var navView: NavigationView? = null
    private var countWarning: TextView? = null
    private var countError: TextView? = null
    private var countAll: TextView? = null
    private var countHidden: TextView? = null
    private var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)
        preference = PreferenceHelper.defaultPrefs(this.requireContext())
        setupRecyclerView()
        requireContext().startService(Intent(context, ReadCentrifuga::class.java))
        setObserves()
        navView = this.requireActivity().findViewById(R.id.nav_view)
        drawerLayout =  this.requireActivity().findViewById(R.id.drawer_layout)
        setNavigationItemSelectedListener()
        countAll = MenuItemCompat.getActionView(navView?.menu?.findItem(R.id.nav_home)) as TextView?
        countWarning =  MenuItemCompat.getActionView(navView?.menu?.findItem(R.id.nav_warning)) as TextView?
        countError = MenuItemCompat.getActionView(navView?.menu?.findItem(R.id.nav_error)) as TextView?
        countHidden = MenuItemCompat.getActionView(navView?.menu?.findItem(R.id.nav_hidden)) as TextView?
        initializeCountDrawer(countWarning)
        initializeCountDrawer(countError)
        initializeCountDrawer(countAll)
        initializeCountDrawer(countHidden)
        setHasOptionsMenu(true)

        return root
    }

    private fun setupRecyclerView() {
        val collColumn = preference["recycler_coll", 3]
        val adapterMonitor: MonitorAdapter by inject()
        recyclerView = root?.findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView?.apply {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            layoutManager = GridLayoutManager(context, collColumn)
            adapter = adapterMonitor
        }

        homeViewModel.all.observe(viewLifecycleOwner, { list ->
            list?.let {
                adapterMonitor.setMonitor(
                    homeViewModel.buildData(it),
                    this.requireActivity(),
                    collColumn,
                    homeViewModel
                )
            }
        })
    }

    private fun setObserves() {
        homeViewModel.count.observe(viewLifecycleOwner, { String ->
            String?.let {
                countAll?.text = it.all
                countWarning?.text = it.warning
                countError?.text = it.error
                countHidden?.text = it.hidden
            }
        })
    }

    private fun setNavigationItemSelectedListener() {
        navView?.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    homeViewModel.sort = ""
                    homeViewModel.monitoring = 1
                    homeViewModel.search()
                    this.requireActivity().title = "Все"
                }
                R.id.nav_warning -> {
                    homeViewModel.sort = "warning"
                    homeViewModel.monitoring = 1
                    homeViewModel.search()
                    this.requireActivity().title = "Warning"
                }
                R.id.nav_error -> {
                    homeViewModel.sort = "error"
                    homeViewModel.monitoring = 1
                    homeViewModel.search()
                    this.requireActivity().title = "Error"
                }
                R.id.nav_hidden -> {
                    homeViewModel.sort = ""
                    homeViewModel.monitoring = 0
                    homeViewModel.search()
                    this.requireActivity().title = "Неотслеживаемые"
                }
                R.id.nav_settings -> {
                    startActivity(Intent(activity, SettingsActivity::class.java))
                }
                R.id.nav_reset_monitor -> {
                    resetMonitor()
                }
                R.id.nav_total_stats -> {
                    val dialog: DialogFragment = FullscreenDialog.newInstance()
                    dialog.show(this.requireActivity().supportFragmentManager, "tag")
                }
            }

            drawerLayout?.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }
    }

    private fun initializeCountDrawer(textView: TextView?) {
        textView?.apply {
            gravity = Gravity.CENTER_VERTICAL
            setTypeface(null, Typeface.BOLD)
            setTextColor(resources.getColor(R.color.black))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_3_coll) {
            preference["recycler_coll"] = 3
            setupRecyclerView()
            return true
        }
        if (id == R.id.action_4_coll) {
            preference["recycler_coll"] = 4
            setupRecyclerView()
            return true
        }
        if (id == R.id.action_5_coll) {
            preference["recycler_coll"] = 5
            setupRecyclerView()
            return true
        }
        if (id == R.id.search) {
            initSearchInterface()
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("InflateParams")
    private fun resetMonitor() {
        MaterialAlertDialogBuilder(this.requireContext())
                .setCancelable(false)
                .setTitle(resources.getString(R.string.update_monitor))
                .setNegativeButton(resources.getString(R.string.dialog_cancel)) { _, _ -> }
                .setPositiveButton(resources.getString(R.string.dialog_ok)) { _, _ ->
                    homeViewModel.resetMonitor()
                    homeViewModel.all.value = null
                    Toast.makeText(
                        this.requireContext(),
                        "Монитор сброшен",
                        Toast.LENGTH_LONG
                    ).show()
                }.show()
    }

    @SuppressLint("InflateParams")
    private fun deleteAllLog() {
        MaterialAlertDialogBuilder(this.requireContext())
                .setCancelable(false)
                .setTitle(resources.getString(R.string.delete_all_log))
                .setNegativeButton(resources.getString(R.string.dialog_cancel)) { _, _ -> }
                .setPositiveButton(resources.getString(R.string.dialog_ok)) { _, _ ->
                    homeViewModel.deleteAllLog()
                    Toast.makeText(
                        this.requireContext(),
                        "Все логи удалены",
                        Toast.LENGTH_LONG
                    ).show()
                }.show()
    }

    /**
     * search interface
     */
    private fun initSearchInterface() {
        val root = this.requireActivity().findViewById<ConstraintLayout>(R.id.search_interface)
        val close = root.findViewById<ImageView>(R.id.close_search)
        val editText = root.findViewById<EditText>(R.id.query_text)
        val clearText = root.findViewById<ImageView>(R.id.clear_search)
        val appBarLayout = this.requireActivity().findViewById<AppBarLayout>(R.id.app_bar_layout)
        val inputMethodManager = this.requireActivity().applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val callback = this.requireActivity().onBackPressedDispatcher.addCallback(this) {
            appBarLayout.visibility = View.VISIBLE
            editText.clearFocus()
            editText.setText("")
            remove()
        }

        close.setOnClickListener {
            appBarLayout.visibility = View.VISIBLE
            editText.clearFocus()
            editText.setText("")
            inputMethodManager.hideSoftInputFromWindow(root.windowToken, 0)
            callback.remove()
        }

        clearText.setOnClickListener { editText.setText("") }

        editText.requestFocus()
        editText.doOnTextChanged { text, _, _, _ ->

            if (!text.isNullOrEmpty()) {
                homeViewModel.querySort = text.toString()
                homeViewModel.search()
                clearText.visibility = View.VISIBLE
            } else {
                homeViewModel.querySort = ""
                homeViewModel.search()
                clearText.visibility = View.INVISIBLE
            }
        }

        appBarLayout.visibility = View.INVISIBLE
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}