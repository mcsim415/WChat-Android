package com.mcsim415.wchat.ui.server

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mcsim415.wchat.ui.RegexInputFilter
import java.lang.Integer.parseInt
import com.mcsim415.wchat.R
import com.mcsim415.wchat.databinding.FragmentServerBinding
import android.graphics.Color
import android.view.WindowManager
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.mcsim415.wchat.socketHandler.ServerSocketHandler


class ServerFragment : Fragment() {

    private lateinit var serverViewModel: ServerViewModel
    private var _binding: FragmentServerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        navBar.visibility = View.VISIBLE

        serverViewModel =
            ViewModelProvider(
                this,
                ViewModelProvider.NewInstanceFactory()
            ).get(ServerViewModel::class.java)

        _binding = FragmentServerBinding.inflate(inflater, container, false)

        val confirmButton: Button = binding.serverOn
        val txt: TextView = binding.serverPort
        txt.filters = arrayOf(RegexInputFilter("^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])\$"))
        txt.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode and KeyEvent.KEYCODE_ENTER == 0 || actionId == EditorInfo.IME_ACTION_DONE) {
                openServer(txt)
            }
            false
        }
        confirmButton.setOnClickListener { openServer(txt) }

        return binding.root
    }

    private fun openServer(txt: TextView) {
        if (checkValidate(txt)) {
            val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            navBar.visibility = View.GONE
            val dialog = setProgressDialog(getString(R.string.wait))

            Thread {
                val socketHandler = ServerSocketHandler(txt.text.toString(), context!!)
                socketHandler.start()
                socketHandler.accept()
                val bundle = Bundle()
                bundle.putSerializable("socketHandler", socketHandler)
                findNavController().navigate(R.id.navigation_chat, bundle)
                dialog?.dismiss()
            }.start()
        } else {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Error")
            builder.setMessage("Provided port number isn't valid.")
            builder.setCancelable(true)
            builder.setPositiveButton(
                "Ok"
            ) { dialog, _ -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun checkValidate(txt: TextView): Boolean {
        return if (txt.text.isNotBlank()) {
            val value: Int = parseInt(txt.text.toString())
            value != 0 && value < 65536
        } else {
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setProgressDialog(text: String): AlertDialog? {
        val llPadding = 30
        val ll = LinearLayout(requireContext())
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam
        val progressBar = ProgressBar(requireContext())
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam
        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(requireContext())
        tvText.text = text
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20f
        tvText.layoutParams = llParam
        ll.addView(progressBar)
        ll.addView(tvText)
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setView(ll)
        val dialog = builder.create()
        dialog.show()
        val window = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window!!.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window!!.attributes = layoutParams
        }
        return dialog
    }
}