package com.github.di7ok.httprequest

import android.os.AsyncTask
import java.io.IOException
import java.net.HttpURLConnection
import java.util.zip.GZIPInputStream

class RequestManager() {
    private var timeout = 5000
    private val headers = mutableMapOf<String, String>()

    constructor(init: RequestManager.() -> Unit) : this() {
        init()
    }

    fun timeout(timeout: Int): RequestManager {
        this.timeout = timeout; return this
    }

    fun header(name: String, value: String): RequestManager {
        headers.put(name, value); return this
    }

    fun execute(init: () -> Request) = execute(init())

    fun execute(request: Request): RequestManager {
        request.headers = HashMap(this.headers + request.headers)
        request.timeout?.also {
            request.timeout = timeout
        }
        RequestTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request)
        return this
    }

    class RequestTask : AsyncTask<Request, Void, RequestTask.Result>() {
        override fun doInBackground(vararg args: Request?): Result? {
            val request = args.first()
            request?.let {
                return try {
                    with(request.url.openConnection() as HttpURLConnection) {
                        requestMethod = request.method
                        request.timeout?.let {
                            connectTimeout = it
                            readTimeout = it
                        }
                        for ((k, v) in request.headers) addRequestProperty(k, v)
                        request.getContent()?.let {
                            addRequestProperty("Content-Length", it.available().toString())
                            doInput = true
                            it.copyTo(outputStream)
                        }
                        connect()

                        if(responseCode == HttpURLConnection.HTTP_OK) {
                            val content = if (getHeaderField("Content-Encoding") == "gzip") {
                                GZIPInputStream(inputStream)
                            } else inputStream

                            request.responseHandler.handle(content)
                        }
                        Result(
                                request,
                                Response(responseCode, headerFields, request.responseHandler),
                                null
                        )
                    }
                } catch (e: IOException) {
                    Result(request, null, e)
                }
            }
            return null
        }

        override fun onPostExecute(result: Result?) {
            result?.let {
                it.response?.let { response ->
                    it.request.success?.let { it(response) }
                }
                it.exception?.let { exception ->
                    it.request.error?.let { it(exception) }
                }
            }
        }

        data class Result(val request: Request, val response: Response?, val exception: Exception?)
    }
}