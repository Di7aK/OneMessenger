package com.github.di7ok.httprequest

data class Response(val code: Int, val headers: Map<String, List<String>>, val result: TextResponse)