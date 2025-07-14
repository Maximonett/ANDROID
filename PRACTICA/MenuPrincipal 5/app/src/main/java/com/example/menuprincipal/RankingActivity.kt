package com.example.menuprincipal

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class RankingActivity : AppCompatActivity() {

    private lateinit var listaRanking: ListView
    private lateinit var botonVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        listaRanking = findViewById(R.id.listaRanking)

        val prefs = getSharedPreferences("ranking_prefs", MODE_PRIVATE)
        val rankingList = mutableListOf<String>()

        for (i in 0 until 5) {
            val nombre = prefs.getString("nombre_$i", getString(R.string.Vacio))
            val puntosString = prefs.getString("puntos_$i", "0.0")
            val puntos = puntosString?.toDoubleOrNull()

            if (puntos != null) {
                rankingList.add("${i + 1}. $nombre - ${String.format("%.2f", puntos)} pts")
            } else {
                rankingList.add("${i + 1}. $nombre - 0.00 pts")
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, rankingList)
            listaRanking.adapter = adapter

            botonVolver = findViewById(R.id.volver)
            botonVolver.setOnClickListener {
                finish() // Vuelve a la actividad anterior
            }
        }
    }
}
