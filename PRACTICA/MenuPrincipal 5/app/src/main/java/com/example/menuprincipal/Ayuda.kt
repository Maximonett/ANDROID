package com.example.menuprincipal

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class Ayuda : AppCompatActivity() {
    private lateinit var  botonVolver: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ayuda)
        botonVolver  = findViewById(R.id.volver)
        botonVolver.setOnClickListener {
            finish() // Vuelve a la actividad anterior
        }
    }
}