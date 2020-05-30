package com.pebbles.core

import android.os.Handler


class Run {
    companion object {
        fun after(delay: Long, process: () -> Unit) {
            Handler().postDelayed({
                process()
            }, delay)
        }
    }
}