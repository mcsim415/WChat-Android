package com.mcsim415.wchat.ui.client

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import com.mcsim415.wchat.databinding.FragmentClientBinding
import com.mcsim415.wchat.ui.RegexInputFilter
import java.util.regex.Pattern

class ClientFragment : Fragment() {

    private lateinit var clientViewModel: ClientViewModel
    private var _binding: FragmentClientBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        clientViewModel =
            ViewModelProvider(
                this,
                ViewModelProvider.NewInstanceFactory()
            ).get(ClientViewModel::class.java)

        _binding = FragmentClientBinding.inflate(inflater, container, false)

        val confirmButton: Button = binding.connect
        val ip: TextView = binding.clientIp
        val port: TextView = binding.clientPort
        port.filters = arrayOf(RegexInputFilter("^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])\$"))
        port.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode and KeyEvent.KEYCODE_ENTER == 0 || actionId == EditorInfo.IME_ACTION_DONE) {
                connect(ip, port)
            }
            false
        }
        confirmButton.setOnClickListener { connect(ip, port) }

        return binding.root
    }

    private fun connect(ip: TextView, port: TextView) {
        if (checkValidate(port)) {
            val pattern: Pattern = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)|(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?[0-9])?[0-9]).){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?[0-9])?[0-9]).){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9]))\$")
            if (pattern.matcher(ip.text.toString()).matches()) {
                Thread {
                    println("a")
                }.start()
            } else {
                showAlertDialog("Provided ip number isn't valid.")
            }
        } else {
            showAlertDialog("Provided port number isn't valid.")
        }
    }

    private fun showAlertDialog(message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setCancelable(true)
        builder.setPositiveButton(
            "Ok"
        ) { dialog, _ -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    private fun checkValidate(txt: TextView): Boolean {
        return if (txt.text.isNotBlank()) {
            val value: Int = Integer.parseInt(txt.text.toString())
            value != 0
        } else {
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}