package com.example.qrscanner

import android.annotation.SuppressLint
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@ExperimentalGetImage class ImageAnalyzer(
    private val fragmentManager: FragmentManager
) : ImageAnalysis.Analyzer {

    private var bottomSheet : BottomSheetDialogFragment = BarcodeLinkBottomSheet()
    private var isURLflag = true

    override fun analyze(imageProxy: ImageProxy) {
        scanBarcode(imageProxy)
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun scanBarcode(imageProxy: ImageProxy) {
        imageProxy.image?.let { image ->
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()
            scanner.process(inputImage)
                .addOnCompleteListener {
                    imageProxy.close()
                    if (it.isSuccessful) {
                        readBarcodeData(it.result as List<Barcode>)
                    } else {
                        it.exception?.printStackTrace()
                    }
                }
        }
    }

    private fun readBarcodeData(barcodes: List<Barcode>) {
        for (barcode in barcodes) {
            if ((barcode.valueType == Barcode.TYPE_URL && !isURLflag) || (barcode.valueType != Barcode.TYPE_URL && isURLflag)) {
                isURLflag = !isURLflag
                if (bottomSheet.isAdded)
                    bottomSheet.dismiss()
                bottomSheet = if (barcode.valueType == Barcode.TYPE_URL)  BarcodeLinkBottomSheet()
                              else BarcodeTextBottomSheet()
            }
            val infoStr = barcode.rawValue
            if (infoStr != null) {
                if (!bottomSheet.isAdded) {
                    bottomSheet.isCancelable = true
                    bottomSheet.show(fragmentManager, "")
                }
                (bottomSheet as BarcodeBottomSheet).updateInfo(infoStr)
            }
        }
    }
}