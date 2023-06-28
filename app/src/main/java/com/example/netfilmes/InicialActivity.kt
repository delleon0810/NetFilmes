package com.example.netfilmes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class InicialActivity : AppCompatActivity() {
    private val TEMPO_EXIBICAO: Long = 5000 // Tempo de exibição em milissegundos (7 segundos)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicial)

        // Inicia o Handler
        val handler = Handler()

        // Define um atraso para fechar a Activity
        handler.postDelayed({
            // Cria uma Intent para a próxima Activity
            val intent = Intent(this@InicialActivity, MainActivity::class.java)
            startActivity(intent)

            // Finaliza a Activity atual
            finish()
        }, TEMPO_EXIBICAO)
    }
}