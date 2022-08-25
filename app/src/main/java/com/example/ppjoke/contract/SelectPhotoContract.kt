package com.example.ppjoke.contract

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract

/**
 * 选择照片的协定
 */
class SelectPhotoContract : ActivityResultContract<Unit?, Uri?>() {
    companion object {
        private const val TAG = "SelectPhotoContract"
    }

    override fun createIntent(context: Context, input: Unit?): Intent {
        return Intent(Intent.ACTION_PICK).setType("image/*")
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        Log.d(TAG, "Select photo uri: ${intent?.data}")
        return intent?.data
    }
}
