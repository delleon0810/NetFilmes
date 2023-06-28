package com.example.netfilmes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netfilmes.model.Category
import com.example.netfilmes.model.Movie
import com.example.netfilmes.util.CategoryTask

class MainActivity : AppCompatActivity(), CategoryTask.Callback {
    val categories = mutableListOf<Category>()//Lista Vazia
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: CategoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progress_main)
        /*Criando CATEGORIAS. que abrigarão os filmes
                     LISTA HORIZONTAL */

        //ADAPTER lISTA VERTICAL
        adapter = CategoryAdapter(categories) { id -> //Quando clicar no filme abrirá outra tela
            val intent = Intent(this@MainActivity, MovieActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)

        } //Adapter da Lista Vertical que pegará as categorias criadas
        /* Adapter conecta a RV com os dados dinâmicos então ele irá pegar os dados dinâmicos da Lista
          de categorias na variável 'categories'
         */
        val rvMain: RecyclerView = findViewById(R.id.rv_main)
        rvMain.layoutManager = LinearLayoutManager(this)//Definindo o estilo da RV (LinearLayout)
        rvMain.adapter = adapter //Criação do Adapter

        CategoryTask(this).execute("https://api.tiagoaguiar.co/netflixapp/home?apiKey=dceec83c-a2ac-4165-971e-b61998cc9673")
    }

    override fun onPreExecute() {
        progressBar.visibility = View.VISIBLE
    }

    override fun onResult(categories: List<Category>) {
        //Aqui a CategoryTask irá fazer o CallBack e adicionar na tela Main Activity
        this.categories.clear()//Limpando a  lista
        this.categories.addAll(categories)//Adicionando a lista categories
        adapter.notifyDataSetChanged() //Força o adpater chamar de novo o OnbindViewHolder para construir de novo o layout
        progressBar.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        progressBar.visibility = View.GONE

    }


}