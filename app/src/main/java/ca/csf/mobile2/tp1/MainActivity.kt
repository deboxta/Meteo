package ca.csf.mobile2.tp1

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group

class MainActivity : AppCompatActivity() {

    private lateinit var cityEdit : EditText

    private lateinit var errorGroup : Group

    private lateinit var cityText : TextView
    private lateinit var tempImg : ImageView
    private lateinit var tempText : TextView

    private lateinit var tempGroup : Group

    private lateinit var retryButton : Button
    private lateinit var errorText : TextView

    private lateinit var inputManager: InputMethodManager
    private lateinit var serverRequest : FindWeatherAsyncTask
    private lateinit var progressBar : ProgressBar

    private val TEMP_SYMBOL = "°"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        progressBar = findViewById(R.id.progressBar)

        errorGroup = findViewById(R.id.errorGroup)
        retryButton = findViewById(R.id.retryButton)
        errorText = findViewById(R.id.errorText)

        tempGroup = findViewById(R.id.tempGroup)
        cityText = findViewById(R.id.cityText)
        tempImg = findViewById(R.id.tempImg)
        tempText = findViewById(R.id.tempText)

        cityEdit = findViewById(R.id.cityEditText)

        retryButton.setOnClickListener(this::onRetry)

        //TODO: maybe change this
        cityEdit.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                hideKeyboard()
                progressBar.visibility = ProgressBar.VISIBLE
                resetDisplay()
                createAsync()

                return@OnKeyListener true
            }
            false
        })

    }

    private fun createAsync(){
        serverRequest = FindWeatherAsyncTask(this::onSucces, this::onNoInternetError)
        serverRequest.execute(cityEdit.text.toString())
    }

    private fun resetDisplay(){
        errorGroup.visibility = Group.GONE
        tempGroup.visibility = Group.GONE
    }

    private fun onSucces(requestObject: RequestObject?) {
        progressBar.visibility = ProgressBar.INVISIBLE
        updateDisplay(requestObject, true)
    }

    private fun onNoInternetError() {
        progressBar.visibility = ProgressBar.INVISIBLE
        updateDisplay(null , false)
    }

    private fun onRetry(view:View){
        resetDisplay()
        progressBar.visibility = ProgressBar.VISIBLE
        createAsync()
    }

    private fun hideKeyboard(){
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.SHOW_FORCED)
    }

    private fun updateDisplay(requestObject : RequestObject?, response : Boolean){
        if (response && requestObject != null){
            errorGroup.visibility = Group.GONE
            tempGroup.visibility = Group.VISIBLE
            tempText.text = StringBuilder(requestObject.temperatureInCelsius.toString()+TEMP_SYMBOL)
            cityText.text = requestObject.city
            when(WeatherTypes.valueOf(requestObject.type)){
                WeatherTypes.CLOUDY -> tempImg.setImageResource(R.drawable.ic_cloudy)
                WeatherTypes.SUNNY -> tempImg.setImageResource(R.drawable.ic_sunny)
                WeatherTypes.SNOW -> tempImg.setImageResource(R.drawable.ic_snow)
                WeatherTypes.RAIN -> tempImg.setImageResource(R.drawable.ic_rain)
                WeatherTypes.PARTLY_SUNNY -> tempImg.setImageResource(R.drawable.ic_partly_sunny)
            }
        }else{
            tempGroup.visibility = Group.GONE
            errorGroup.visibility = Group.VISIBLE
            //when(ResponseType.valueOf(response))
                //ResponseType.Unauthorized -> errorText.text =

        }

    }
}

enum class WeatherTypes{
    CLOUDY,
    SUNNY,
    SNOW,
    RAIN,
    PARTLY_SUNNY
}

enum class ResponseType{
    Unauthorized,
    Forbidden,
    NotFound
}
