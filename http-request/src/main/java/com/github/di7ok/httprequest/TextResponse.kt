package com.github.di7ok.httprequest

import java.io.InputStream

open class TextResponse {
    var text = ""

    open fun handle(inputStream: InputStream) {
        text = inputStream.bufferedReader().readText()
    }
}