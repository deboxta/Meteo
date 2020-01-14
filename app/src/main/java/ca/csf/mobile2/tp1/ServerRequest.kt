package ca.csf.mobile2.tp1

import android.os.AsyncTask
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.net.URL

data class Task(val city: String, val type: String, val temp: Int)

class ServerRequest (var OnSucces : (Int) -> Unit) : AsyncTask<Unit, Unit, Int>(){


    override fun doInBackground(vararg params: Unit?): Int {
        var json = URL("http://localhost:8080/api/v1/weather/Laval").readText()
        val mapper = jacksonObjectMapper()
        val state = mapper.readValue<Task>(json)

        return state.hashCode()
    }


}