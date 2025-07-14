package com.example.menuprincipal

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.os.CountDownTimer
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import kotlin.random.Random



class Juego : AppCompatActivity() {
    private lateinit var barraHerramientas: Toolbar
    private lateinit var tablero: Array<Button>
    private lateinit var grillaTablero: GridLayout
    private lateinit var textoMovimientos: TextView
    private lateinit var textoAciertos: TextView
    private lateinit var textoRestantes: TextView
    private lateinit var mensajefinjuego : String
    private lateinit var nombre: String

    private lateinit var txtContador: TextView
    private lateinit var countDownTimer: CountDownTimer


    private lateinit var mostrarNombre : TextView

    private var colorlocetas: Int = 0
    private var colorAgua: Int = 0
    private var colorBordo: Int = 0

    private var juegoFinalizado = false

    private var tamañoTablero: Int = 0
    private var totalCeldas: Int = 0

    private lateinit var hayBarco: BooleanArray

    private var totalBarcos = 0
    private var movimientos = 0
    private var aciertos = 0
    private var aciertosAgua = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)

        nombre = intent.getStringExtra("Nombre") ?: "No definodo"
        tamañoTablero = intent.getIntExtra("Tamaño", 0)
        totalCeldas = tamañoTablero * tamañoTablero


        barraHerramientas = findViewById(R.id.barraHerramientas)
        grillaTablero = findViewById(R.id.boardGrid)
        textoMovimientos = findViewById(R.id.movementsText)
        textoAciertos = findViewById(R.id.hitsText)
        textoRestantes = findViewById(R.id.remainingText)

        //MOSTRAR NOMBRE
        mostrarNombre=findViewById(R.id.textNombreJugador)
        val textoNombre=getString(R.string.nombre,nombre)
        mostrarNombre.text=textoNombre


        //Cuenta Regresiva
        txtContador = findViewById(R.id.txtContador)



        textoMovimientos.text =   getString(R.string.movimientos,movimientos)
        textoAciertos.text =   getString(R.string.aciertos, aciertos)
        textoRestantes.text = getString(R.string.barcos_restantes, (totalBarcos - aciertos))


        textoRestantes.setTextColor(Color.BLACK)
        colorlocetas = ContextCompat.getColor(this, R.color.gris_oscuro)
        colorAgua = ContextCompat.getColor(this, R.color.celeste_francia)
        colorBordo = ContextCompat.getColor(this, R.color.bordo_profundo)


        configurarTablero()
        setSupportActionBar(barraHerramientas)


        if (savedInstanceState != null) {
                // Restaurar estado guardado
                movimientos = savedInstanceState.getInt("movimientos", 0)
                aciertos = savedInstanceState.getInt("aciertos", 0)
                totalBarcos = savedInstanceState.getInt("totalBarcos", 0)
                hayBarco = savedInstanceState.getBooleanArray("hayBarco") ?: BooleanArray(totalCeldas) { false }
                restaurarEstadoTablero(savedInstanceState)
                actualizarEstadisticas()
            } else {
                IniciarJuego()
            }
    }

    // Guardar estado antes de que destruya la Activity
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("movimientos", movimientos)
        outState.putInt("aciertos", aciertos)
        outState.putInt("totalBarcos", totalBarcos)
        outState.putBooleanArray("hayBarco", hayBarco)

        for (i in tablero.indices) {
            outState.putBoolean("boton_enabled_$i", tablero[i].isEnabled)
            outState.putCharSequence("boton_text_$i", tablero[i].text)
            val color = (tablero[i].background as? ColorDrawable)?.color ?: Color.LTGRAY
            outState.putInt("boton_color_$i", color)
        }
    }


    private fun temporizador(tamaño: Int) {
        val duracion = when (tamaño) {
            6 -> 20_000L  // 20 segundos
            8 -> 25_000L  // 25 segundos
            10 -> 30_000L // 30 segundos
            else -> error(getString(R.string.error_tamaño_no_valido))
        }

        countDownTimer = object : CountDownTimer(duracion, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val segundosRestantes = millisUntilFinished / 1000
                txtContador.text = getString(R.string.tiempo_restante, segundosRestantes)
            }

            override fun onFinish() {
                txtContador.text =  getString(R.string.tiempo_agotado)
                mostrarFinDelJuego()
            }
        }

        countDownTimer.start()
    }


    private fun restaurarEstadoTablero(savedInstanceState: Bundle) {
        for (i in tablero.indices) {
            tablero[i].isEnabled = savedInstanceState.getBoolean("boton_enabled_$i", true)
            val color = savedInstanceState.getInt("boton_color_$i", Color.LTGRAY)
            tablero[i].setBackgroundColor(color)
        }
    }


    private fun configurarTablero() {
        tablero = Array(totalCeldas) { Button(this) }
        grillaTablero.removeAllViews()
        grillaTablero.columnCount = tamañoTablero

        for (i in tablero.indices) {
            val celda = Button(this)
            celda.layoutParams = GridLayout.LayoutParams().apply {
                width = 40
                height = 40
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            celda.setBackgroundColor(colorlocetas)
            celda.setOnClickListener { alTocarCelda(i) }
            tablero[i] = celda
            grillaTablero.addView(celda)
        }
    }

    private fun IniciarJuego() {
        temporizador(tamañoTablero)
        movimientos = 0
        aciertos = 0
        totalBarcos = (10..15).random()
        hayBarco = BooleanArray(totalCeldas) { false }

        val posicionesBarcos = mutableSetOf<Int>()
        while (posicionesBarcos.size < totalBarcos) {
            posicionesBarcos.add(Random.nextInt(0, totalCeldas))
        }

        for (pos in posicionesBarcos) {
            hayBarco[pos] = true
        }

        actualizarEstadisticas()

        for (i in tablero.indices) {
            tablero[i].isEnabled = true
            tablero[i].text = " "
            tablero[i].textSize = 10f
            tablero[i].setBackgroundColor(Color.DKGRAY)
        }
    }

    private fun alTocarCelda(indice: Int) {
        if (juegoFinalizado) return

        movimientos++
        tablero[indice].isEnabled = false

        if (hayBarco[indice]) {
            aciertos++
            tablero[indice].setBackgroundColor(colorBordo)
            tablero[indice].text = getString(R.string.barco)
            tablero[indice].setTextColor(Color.WHITE)
        } else {
            tablero[indice].setBackgroundColor(colorAgua)
            tablero[indice].text = getString(R.string.agua)
            tablero[indice].setTextColor(Color.WHITE)
        }

        actualizarEstadisticas()

        if (aciertos == totalBarcos) {
            countDownTimer.cancel()
            mostrarFinDelJuego()
        }
    }

    private fun actualizarEstadisticas() {
        textoMovimientos.text =   getString(R.string.movimientos,movimientos)
        textoAciertos.text =   getString(R.string.aciertos, aciertos)
        textoRestantes.text = getString(R.string.barcos_restantes, (totalBarcos - aciertos))
        textoRestantes.setTextColor(Color.BLACK)

    }
    fun guardarRanking(context: Context, nombre: String, puntos: Double): Boolean {
        val prefs = context.getSharedPreferences("ranking_prefs", Context.MODE_PRIVATE)
        val ranking = mutableListOf<Pair<String, Double>>()

        for (i in 0 until 5) {
            val n = prefs.getString("nombre_$i", null)
            val p = prefs.getString("puntos_$i", null)?.toDoubleOrNull()
            if (n != null && p != null) {
                ranking.add(Pair(n, p))
            }
        }

        // Agregamos el nuevo puntaje
        ranking.add(Pair(nombre, puntos))

        // Ordenamos y seleccionamos los mejores 5
        val top5 = ranking.sortedByDescending { it.second }.take(5)

        // Verificamos si el nuevo puntaje quedó en el top 5
        val entraEnRanking = top5.any { it.first == nombre && it.second == puntos }

        // Guardamos el nuevo ranking
        val editor = prefs.edit()
        for (i in top5.indices) {
            editor.putString("nombre_$i", top5[i].first)
            editor.putString("puntos_$i", top5[i].second.toString())
        }
        editor.apply()

        return entraEnRanking
    }




    private fun mostrarFinDelJuego() {
        if (juegoFinalizado) return
        juegoFinalizado = true
        val pts = ((aciertos.toDouble() / movimientos) * 100 )
        val ptsString = String.format("%.2f", pts)
        aciertosAgua = movimientos - aciertos
        for (i in tablero.indices) {
            tablero[i].isEnabled = false
        }
        if (txtContador.text == getString(R.string.tiempo_agotado)){
            val dialogo = AlertDialog.Builder(this)
                .setTitle(getString(R.string.fin_juego))
                .setMessage(getString(R.string.se_acabo_tiempo))
                .setPositiveButton(getString(R.string.volver_menu)) { _, _ ->
                    // Acción si elige "Sí"
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton(getString(R.string.jugar_nuevamente)) { _, _ ->
                    val intent=Intent(this,Juego::class.java)
                    startActivity(intent)
                    finish()
                }
                .create()

            dialogo.show()
        //EN CONSTRUCCION....
        }else{
            val entro = guardarRanking(this, nombre, pts)
            if (entro) {
                val dialogo = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.fin_juego))
                    .setMessage(getString(R.string.resultado_con_ranking, aciertos, movimientos, ptsString))
                    .setPositiveButton(getString(R.string.ver_ranking)) { _, _ ->
                        startActivity(Intent(this, RankingActivity::class.java))
                        finish()
                    }
                    .setNegativeButton(getString(R.string.jugar_nuevamente)) { _, _ ->
                        val intent = Intent(this, Juego::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .setNeutralButton(getString(R.string.compartir)) { _, _ ->
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.compartir))
                            putExtra(Intent.EXTRA_TEXT, getString(R.string.texto_compartir_ranking, ptsString))
                        }
                        startActivity(Intent.createChooser(intent, getString(R.string.menu_compartir)))
                    }
                    .create()

                dialogo.show()
            } else {
                val dialogo = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.fin_juego))
                    .setMessage(getString(R.string.resultado_sin_ranking, aciertos, movimientos, ptsString))
                    .setPositiveButton(getString(R.string.jugar_nuevamente)) { _, _ ->
                        val intent = Intent(this, Juego::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .setNegativeButton(getString(R.string.ver_ranking)) { _, _ ->
                        val intent = Intent(this, RankingActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .create()
                dialogo.show()

            }


        }
        //mensajefinjuego = getString(R.string.mensaje_fin_juego, nombre, aciertos, aciertosAgua)
        //Toast.makeText(this, mensajefinjuego, Toast.LENGTH_LONG).show()

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
