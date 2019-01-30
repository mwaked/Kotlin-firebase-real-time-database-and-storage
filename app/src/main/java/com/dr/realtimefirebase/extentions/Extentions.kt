package com.dr.realtimefirebase.extentions

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore

fun Activity.makeProgressDialog(title: Int): ProgressDialog {
    val progressDialog = ProgressDialog(this)
    progressDialog.isIndeterminate = false
    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
    progressDialog.setCancelable(false)
    progressDialog.setTitle(title)
    progressDialog.max = 100
    return progressDialog
}

const val PICK_IMAGE_REQUEST: Int = 100

fun Activity.chooseImageFromGallery() {
    if (isPermissionsGranted(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PICK_IMAGE_REQUEST)
        }
    }
}

fun Activity.isPermissionsGranted(vararg permissions: String): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
    return permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
}
