package com.example.trabalhocovid19

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_world.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, WorldActivity::class.java)
            ContextCompat.startActivity(this, intent, null)
        })
        btn_statesBrazil.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, StatesBrazilActivity::class.java)
            ContextCompat.startActivity(this, intent, null)
        })
        btn_countryWorld.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, CountryWorldActivity::class.java)
            ContextCompat.startActivity(this, intent, null)
        })




    }
}
