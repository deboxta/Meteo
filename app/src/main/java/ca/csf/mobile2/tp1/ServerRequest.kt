package ca.csf.mobile2.tp1

import android.os.AsyncTask
import android.widget.ProgressBar
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.lang.Exception
import java.net.URL

data class RequestObject(val city: String, val type: String, val temp: Int)

class ServerRequest (var onSuccess : (RequestObject) -> Unit) : AsyncTask<String, Unit, RequestObject>(){

    private lateinit var weather : RequestObject

    override fun doInBackground(vararg city: String?): RequestObject {
        try {

        }catch (e: Exception){
            e.printStackTrace()
        }
        var json = URL("http://10.200.77.203:8080/api/v1/weather/$city").readText()
        val mapper = jacksonObjectMapper()
        weather = mapper.readValue(json)

        return weather
    }

    override fun onPostExecute(result: RequestObject?) {
        super.onPostExecute(result)
        if (result != null)
            onSuccess(result)
    }
}