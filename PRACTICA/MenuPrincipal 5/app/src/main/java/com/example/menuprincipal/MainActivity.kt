package com.example.menuprincipal

import android.annotation.SuppressLint
import android.content.Intent

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

    private lateinit var barraHerramientas: Toolbar
    private lateinit var ayudaInformacion: Button
    private lateinit var editName: EditText
    private lateinit var Mapa: Button
    private lateinit var boton6x6: Button
    private lateinit var boton8x8: Button
    private lateinit var boton10x10: Button
    private lateinit var botonEmpezar: Button
    private lateinit var botonTop5 : Button

    private var tamaño = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        barraHerramientas = findViewById(R.id.barraHerramientas)
        editName = findViewById(R.id.Nombre_jugador)
        ayudaInformacion = findViewById(R.id.Ayuda_Informacion)
        Mapa = findViewById(R.id.Tamaño_mapa)
        boton6x6 = findViewById(R.id.tamaño_6x6)
        boton8x8 = findViewById(R.id.tamaño_8x8)
        boton10x10 = findViewById(R.id.tamaño_10x10)
        botonEmpezar = findViewById(R.id.botonEmpezar)
        botonTop5 = findViewById(R.id.top5)

        setSupportActionBar(barraHerramientas)

        ayudaInformacion.setOnClickListener {
            intent = Intent(this, Ayuda::class.java)
            startActivity(intent)}
        // Listeners para botones de tamaño
        boton6x6.setOnClickListener {
            Mapa.text = getString(R.string.tamaño_seleccionado, "6x6")
            tamaño = 6
        }
        boton8x8.setOnClickListener {
            Mapa.text = getString(R.string.tamaño_seleccionado, "8x8")
            tamaño = 8
        }
        boton10x10.setOnClickListener {
            Mapa.text = getString(R.string.tamaño_seleccionado, "10x10")
            tamaño = 10
        }
        botonTop5.setOnClickListener {
            val intent = Intent(this, RankingActivity::class.java)
            startActivity(intent)
            true
        }
        botonEmpezar.setOnClickListener {
            val nombreJugador = editName.text.toString().trim()

            if (tamaño == 0 || nombreJugador.isEmpty()) {
                Toast.makeText(this, "Falta seleccionar tamaño o nombre", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, Juego::class.java)
                intent.putExtra("Tamaño", tamaño)
                intent.putExtra("Nombre", nombreJugador)
                startActivity(intent)
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_pal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuNuevo -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                true
            }
            R.id.menuAyuda -> {
                val intent = Intent(this, Ayuda::class.java)
                startActivity(intent)
                true
            }
            R.id.menuRanking -> {
                val intent= Intent(this,RankingActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menuSalir -> {
                finishAffinity() // Cierra la app
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
