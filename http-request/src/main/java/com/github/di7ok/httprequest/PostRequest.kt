package com.github.di7ok.httprequest

import java.io.InputStream
import java.net.URLEncoder

class PostRequest(url: String) : Request(url) {
    private val fields = mutableMapOf<String, String>()

    init {
        method = "POST"
        header("Content-Type", "application/x-www-form-urlencoded")
    }

    constructor(url: String, init: PostRequest.() -> Unit) : this(url) {
        init()
    }

    fun field(name: String, value: String): PostRequest {
        fields.put(name, value); return this
    }

    override fun getContent(): InputStream? {
        val sb = StringBuilder()
        for ((k, v) in fields) {
            if (sb.isNotEmpty()) sb.append("&")
            sb.append("${URLEncoder.encode(k, "UTF-8")}=${URLEncoder.encode(v, "UTF-8")}")
        }
        return sb.toString().byteInputStream()
    }
}