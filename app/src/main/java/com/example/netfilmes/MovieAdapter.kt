package com.example.netfilmes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.netfilmes.model.Movie
import com.squareup.picasso.Picasso

class MovieAdapter( val movies : List<Movie>,
                   @LayoutRes private val layoutId : Int,
                   private val onItemClickListener : ( (Int) -> Unit )? = null //Função para responder aos clicks
                   ) : RecyclerView.Adapter<MovieAdapter.MoveViewHolder>() { //Criação do Adapter
    /*Classe que só será vista pela class MainActivity, que herdará o que está na classe RecyclerView
  mais especificamente o objeto Adapter onde como parâmetro espera o tipo da classe da Célula (ViewHolder) */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoveViewHolder {
        //Método que espera o viewHolder e infle o layout
        //Devemos apontar qual layout será jogado para dentro da RV.
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent,false)
        return MoveViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoveViewHolder, position: Int) {
        val movie = movies[position]//Recebendo os dados de acordo com a posição declarada
        holder.bind(movie)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

     inner class MoveViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
         fun bind(movie: Movie) { /*Função criada para integrar o layout a cada campo*/
             val imgCover: ImageView = itemView.findViewById(R.id.img_cover)
             imgCover.setOnClickListener {
                 onItemClickListener?.invoke(movie.id)
             }

             Picasso.get().load(movie.coverUrl).into(imgCover) //Download das imagens
         }
     }
}