package com.example.qrscanner

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.github.g0dkar.qrcode.QRCode
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class GeneratorFragment : Fragment() {
    private lateinit var codeText: EditText
    private lateinit var codeImage: ImageView
    private lateinit var btn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.generator, container, false)

        codeImage = view.findViewById(R.id.ivCode)
        codeText = view.findViewById(R.id.etConvert)
        btn = view.findViewById(R.id.btn)

        if (savedInstanceState != null) {
            codeText.setText(savedInstanceState.getString(textKey, getString(R.string.enter_string)))
            generate(codeText.text.toString())
        }

        btn.setOnClickListener {
            if (codeText.text.toString().trim { it <= ' ' }.isEmpty()) {
                makeToast(getString(R.string.empty_text))
            } else {
                val stream = generate(codeText.text.toString())
                try {
                    val wallpaperDirectory = File(IMAGE_DIRECTORY)
                    if (!wallpaperDirectory.exists())
                        wallpaperDirectory.mkdirs()
                    val file = File(wallpaperDirectory, codeText.text.toString() + "-" +
                            Calendar.getInstance().timeInMillis.toString() + ".jpg")
                    file.writeBytes(stream.toByteArray())
                    makeToast( "QRCode saved to -> ${file.absolutePath}")
                } catch (e: Exception) {
                    makeToast( getString(R.string.save_code_error))
                }
            }
        }
        return view
    }

    private fun generate(text: String) : ByteArrayOutputStream {
        val stream = ByteArrayOutputStream()
        QRCode(text).render().writeImage(stream, "JPEG")
        val bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.toByteArray().size)
        codeImage.setImageBitmap(bitmap)
        return stream
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(textKey, codeText.text.toString())
    }

    companion object {
        private const val IMAGE_DIRECTORY = "/storage/emulated/0/Pictures/QRcodeDemonuts"
        private const val textKey = "text"
    }

    private fun Fragment.makeToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
        activity?.let {
            Toast.makeText(it, text, duration).show()
        }
    }

}