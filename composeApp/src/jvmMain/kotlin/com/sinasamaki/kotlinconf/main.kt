package com.sinasamaki.kotlinconf

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KotlinConf",
    ) {
        App()
    }
}