package ca.csf.mobile2.tp1

import android.os.AsyncTask
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.text.StringBuilder

data class RequestObject(val city: String, val type: String, val temperatureInCelsius: Int)

class FindWeatherAsyncTask (var onSuccess : (RequestObject) -> Unit, var onNoInternetError : () -> Unit,
                            var onServerError : (String) -> Unit) : AsyncTask<String, Unit, RequestObject>(){

    private lateinit var weather : RequestObject
    private lateinit var client : OkHttpClient
    private lateinit var response: Response

    private var isInternetError = false
    private var isServerError = false
    private var isRequestOk = false

    override fun doInBackground(vararg city: String?): RequestObject? {

        client = OkHttpClient()

        val http = StringBuilder("http://$SERVER_IP:8080/api/v1/weather/${city[0]}")
        val request = Request.Builder().url(http.toString()).build()

        try {
            response = client.newCall(request).execute()
            if (response.isSuccessful){
                isRequestOk = true

                val mapper = jacksonObjectMapper()
                weather = mapper.readValue(response.body?.string().toString())
                response.close()

            } else {
                isServerError = true
                return null
            }
        } catch (e : IOException){
            isInternetError = true
            return null
        }

        return weather
    }

    override fun onPostExecute(result: RequestObject?) {
        super.onPostExecute(result)
        if (isRequestOk && result != null)
            onSuccess(result)
        else if (isInternetError)
            onNoInternetError()
        else if (isServerError)
            onServerError( response.message )
    }
}

private const val SERVER_IP = "192.168.1.21"
