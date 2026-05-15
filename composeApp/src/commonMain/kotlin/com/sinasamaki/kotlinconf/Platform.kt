package com.sinasamaki.kotlinconf

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform