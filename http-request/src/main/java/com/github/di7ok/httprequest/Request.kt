package com.github.di7ok.httprequest

import java.io.InputStream
import java.net.URL

abstract class Request(url : String) {
    var url = URL(url)
    var headers = mutableMapOf<String, String>()
    var error: ((Exception) -> Unit)? = null
    var success: ((Response) -> Unit)? = null
    var method = "GET"
    var timeout: Int? = null
    var responseHandler = TextResponse()

    fun success(success: (Response) -> Unit) : Request {
        this.success = success; return this
    }

    fun error(error: (Exception) -> Unit) : Request {
        this.error = error; return this
    }

    fun header(name: String, value: String) : Request {
        headers.put(name, value); return this
    }

    fun handler(handler: TextResponse) : Request {
        responseHandler = handler; return this
    }

    abstract fun getContent() : InputStream?
}
