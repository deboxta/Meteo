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

    private var message : String? = null
    private var type : String? = null
    private var city : String? = null
    private var temp : String? = null


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

        cityEdit.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP && cityEdit.text.isNotEmpty()) {
                progressBar.visibility = ProgressBar.VISIBLE
                inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.SHOW_FORCED)
                resetDisplay()
                createAsync()

                return@OnKeyListener true
            }
            true
        })

    }

    private fun createAsync(){
        serverRequest = FindWeatherAsyncTask(this::onSuccess, this::onNoInternetError, this::onServerError)
        serverRequest.execute(cityEdit.text.toString())
    }

    private fun resetDisplay(){
        errorGroup.visibility = Group.GONE
        tempGroup.visibility = Group.GONE
    }

    private fun onServerError(message : String) {
        this.message = message
        city = null
        temp = null
        type = null
        updateView(message)
    }

    private fun onSuccess(requestObject: RequestObject) {
        city = requestObject.city
        temp = requestObject.temperatureInCelsius.toString()
        type = requestObject.type
        message = null
        updateView(null)
    }

    private fun onNoInternetError() {
        city = null
        temp = null
        type = null
        message = null
        updateView( null)
    }

    private fun onRetry(view:View){
        resetDisplay()
        progressBar.visibility = ProgressBar.VISIBLE
        createAsync()
    }
    
    private fun updateView( message: String?){
        progressBar.visibility = ProgressBar.INVISIBLE

        if (city != null && temp != null && type != null){
            errorGroup.visibility = Group.GONE
            tempGroup.visibility = Group.VISIBLE

            tempText.text = StringBuilder(temp+TEMP_SYMBOL)
            cityText.text = city

            when(WeatherTypes.valueOf(type.toString())){
                WeatherTypes.CLOUDY -> tempImg.setImageResource(R.drawable.ic_cloudy)
                WeatherTypes.SUNNY -> tempImg.setImageResource(R.drawable.ic_sunny)
                WeatherTypes.SNOW -> tempImg.setImageResource(R.drawable.ic_snow)
                WeatherTypes.RAIN -> tempImg.setImageResource(R.drawable.ic_rain)
                WeatherTypes.PARTLY_SUNNY -> tempImg.setImageResource(R.drawable.ic_partly_sunny)
            }
        }else{
            tempGroup.visibility = Group.GONE
            errorGroup.visibility = Group.VISIBLE

            if (message == null)
                errorText.text = INTERNET_ERROR_MESSAGE
            else
                errorText.text = message

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putCharSequence("CITY_EDIT", cityEdit.text)
        outState.putCharSequence("TEMP", temp)
        outState.putString("CITY_NAME", city)
        outState.putString("TYPE", type)
        outState.putString("MESSAGE", message)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        this.cityEdit.setText(savedInstanceState.getCharSequence(CITY_EDIT))
        city = savedInstanceState.getString(CITY_NAME)
        temp = savedInstanceState.getString(TEMP)
        type = savedInstanceState.getString(TYPE)

        message = if (savedInstanceState.getString(MESSAGE).toString() == "null")
            null
        else
            savedInstanceState.getString(MESSAGE).toString()
        updateView(message)
    }
}

private const val CITY_EDIT = "CITY_EDIT"
private const val CITY_NAME = "CITY_NAME"
private const val TEMP = "TEMP"
private const val TYPE = "TYPE"
private const val MESSAGE = "MESSAGE"

private const val INTERNET_ERROR_MESSAGE = "Oups...no internet connection."
private const val TEMP_SYMBOL = "°"

enum class WeatherTypes{
    CLOUDY,
    SUNNY,
    SNOW,
    RAIN,
    PARTLY_SUNNY
}
