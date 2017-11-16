package com.github.di7ok.httprequest

import java.io.InputStream

class GetRequest(url: String) : Request(url) {
    init {
        method = "GET"
    }

    constructor(url: String, init: GetRequest.() -> Unit) : this(url) {
        init()
    }

    override fun getContent(): InputStream? {
        return null
    }

}