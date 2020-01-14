package ca.csf.mobile2.tp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.constraintlayout.widget.Group

class MainActivity : AppCompatActivity() {

    private lateinit var errorGroup : Group
    private lateinit var tempGroup : Group
    private lateinit var cityEdit : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var serverRequest = ServerRequest()

        errorGroup = findViewById(R.id.errorGroup)
        tempGroup = findViewById(R.id.tempGroup)

        cityEdit = findViewById(R.id.cityEditText)

        serverRequest.execute()
    }
}
