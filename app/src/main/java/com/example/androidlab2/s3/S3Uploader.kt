package com.example.androidlab2.s3

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.*
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import java.io.File

class S3Uploader(private val context: Context) {

    fun uploadFile(file: File, onComplete: (String) -> Unit, onError: (Exception) -> Unit) {
        try {
            // val awsAccessKey = ""
           // val awsSecretKey = ""

            val credentials = BasicAWSCredentials(awsAccessKey, awsSecretKey)
            val s3Client = AmazonS3Client(credentials)
            s3Client.setRegion(com.amazonaws.regions.Region.getRegion(Regions.AP_NORTHEAST_2))

            val transferUtility = TransferUtility.builder()
                .context(context)
                .s3Client(s3Client)
                .defaultBucket("app-request-bucket")
                .build()

            val key = file.name
            val observer = transferUtility.upload(key, file)

            observer.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState?) {
                    when (state) {
                        TransferState.COMPLETED -> onComplete(key)
                        TransferState.FAILED -> onError(Exception("S3 업로드 실패"))
                        else -> {}
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    val percentDone = (bytesCurrent.toDouble() / bytesTotal * 100).toInt()
                    Log.d("AWS", "진행률: $percentDone%")
                }

                override fun onError(id: Int, ex: Exception?) {
                    ex?.let { onError(it) }
                }
            })

        } catch (e: Exception) {
            onError(e)
        }
    }
}