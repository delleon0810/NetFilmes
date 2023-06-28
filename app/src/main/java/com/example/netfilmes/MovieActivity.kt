package com.example.netfilmes

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netfilmes.model.Movie
import com.example.netfilmes.model.MovieDetail
import com.example.netfilmes.util.MovieTask
import com.squareup.picasso.Picasso
import java.lang.IllegalStateException

class MovieActivity : AppCompatActivity(), MovieTask.Callback {

    private val movies = mutableListOf<Movie>() // Lista Criada


    private lateinit var txtTitle : TextView
    private lateinit var txtDesc : TextView
    private lateinit var txtCast : TextView
    private lateinit var progress : ProgressBar
    private lateinit var adapter : MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

         txtTitle = findViewById(R.id.movie_txt_tittle)
         txtDesc  = findViewById(R.id.movie_txt_desc)
         txtCast  = findViewById(R.id.movie_txt_cast)
         val rv : RecyclerView = findViewById(R.id.rv_movie_similar)
         progress = findViewById(R.id.movie_progress)

        val id = intent?.getIntExtra("id", 0) ?: throw IllegalStateException("ID NÃO ENCONTRADO")

        val url = "https://api.tiagoaguiar.co/netflixapp/movie/$id?apiKey=dceec83c-a2ac-4165-971e-b61998cc9673"
        MovieTask(this).execute(url)

        adapter = MovieAdapter(movies, R.layout.movie_item_similar)
        rv.layoutManager = GridLayoutManager(this, 3)
        rv.adapter =  adapter

        val toolbar : Toolbar = findViewById(R.id.movie_toolbar)
        setSupportActionBar(toolbar) //Usado para fazer a Toolbar aparecer na tela

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //Código para fazer a seta de voltar
        supportActionBar?.title = null
    }

    override fun onPreExecute() {

        progress.visibility = View.VISIBLE
    }

    override fun onFailure(message: String) {
        progress.visibility = View.GONE
    }

    override fun onResult(movieDetail: MovieDetail) {
        progress.visibility = View.GONE
        txtTitle.text =movieDetail.movie.title
        txtDesc.text = movieDetail.movie.desc
        txtCast.text = getString(R.string.cast, movieDetail.movie.cast)
        movies.clear()
        movies.addAll(movieDetail.similars)
        adapter.notifyDataSetChanged()

        val coverImg: ImageView = findViewById(R.id.movie)

        Picasso.get().load(movieDetail.movie.coverUrl).into(object: com.squareup.picasso.Target {
            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {

                Log.e("Error Bitmap", e?.message, e)

                val errorMessage: String = "Não foi possível carregar a imagem do filme!"
                Toast.makeText(this@MovieActivity, errorMessage, Toast.LENGTH_LONG).show()
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                val layerDrawable: LayerDrawable = ContextCompat.getDrawable(this@MovieActivity, R.drawable.shadows) as LayerDrawable
                val movieCover = BitmapDrawable(resources, bitmap)
                layerDrawable.setDrawableByLayerId(R.id.cover_drawable, movieCover)

                coverImg.setImageDrawable(layerDrawable)
            }
        })


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {  //Fazendo o botão de voltar funcionar 
        if (item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}