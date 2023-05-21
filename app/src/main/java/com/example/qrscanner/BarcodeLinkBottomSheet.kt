package com.example.qrscanner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.jsoup.Jsoup
import java.util.concurrent.Executors


class BarcodeLinkBottomSheet : BottomSheetDialogFragment(), BarcodeBottomSheet {
    private val titleKey = "title"
    private val descKey = "desc"
    private val infoKey = "info"

    private lateinit var titleView: TextView
    private lateinit var descView: TextView
    private lateinit var infoView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.link_data, container, false)
        titleView = view.findViewById(R.id.text_view_title)
        descView = view.findViewById(R.id.text_view_description)
        infoView = view.findViewById(R.id.text_view_link)

        if (savedInstanceState != null) {
            titleView.text = savedInstanceState.getString(titleKey)
            descView.text = savedInstanceState.getString(descKey)
            infoView.text = savedInstanceState.getString(infoKey)
        }

        view.findViewById<TextView>(R.id.text_view_copy_link).setOnClickListener {
            try {
                val clipboard =
                    activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("text", infoView.text)
                clipboard.setPrimaryClip(clip)
                makeToast(getString(R.string.save_link))
            } catch (ex: Exception) {
                makeToast(getString(R.string.save_link_error))
            }
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(titleKey, titleView.text.toString())
        outState.putString(descKey, descView.text.toString())
        outState.putString(infoKey, infoView.text.toString())
    }

    override fun updateInfo(info: String) {
        fetchUrlMetaData(info) { title, desc ->
            view?.apply {
                titleView.text = title
                descView.text = desc
                infoView.text = info

                findViewById<TextView>(R.id.text_view_visit_link).setOnClickListener { _ ->
                    Intent(Intent.ACTION_VIEW).also {
                        it.data = Uri.parse(info)
                        startActivity(it)
                    }
                }
            }
        }
    }

    private fun fetchUrlMetaData(
        url: String,
        callback: (title: String, desc: String) -> Unit
    ) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            val doc = Jsoup.connect(url).get()
            val descAttr = if (doc.select("meta[name=description]").isEmpty()) ""
                           else doc.select("meta[name=description]")[0].attr("content")
            handler.post {
                callback(doc.title(), descAttr)
            }
        }
    }

    private fun Fragment.makeToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
        activity?.let {
            Toast.makeText(it, text, duration).show()
        }
    }

}