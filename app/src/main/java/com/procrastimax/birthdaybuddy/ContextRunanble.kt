package com.procrastimax.birthdaybuddy

import android.content.Context

class ContextRunanble(val context: Context, function: () -> Unit) : Runnable {
    override fun run() {

    }
}