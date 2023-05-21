package com.example.qrscanner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BarcodeTextBottomSheet : BottomSheetDialogFragment(), BarcodeBottomSheet {
    private val infoKey = "info"
    private lateinit var infoView : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.text_data, container, false)
        infoView = view.findViewById(R.id.tv_description)
        if (savedInstanceState != null) {
            infoView.text = savedInstanceState.getString(infoKey)
        }

        view.findViewById<TextView>(R.id.tv_copy_link).setOnClickListener { _ ->
            try {
                val clipboard = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("text", infoView.text)
                clipboard.setPrimaryClip(clip)
                dismiss()
            }
            catch (ex : Exception){
                activity?.let {
                    Toast.makeText(it, getString(R.string.save_text_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(infoKey, infoView.text.toString())
    }

    override fun updateInfo(info: String) {
        view?.findViewById<TextView>(R.id.tv_description)?.text = info
    }

}