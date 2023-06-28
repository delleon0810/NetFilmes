package com.example.netfilmes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netfilmes.model.Category
import com.example.netfilmes.model.Movie

class CategoryAdapter(val categories : List<Category>,
                      private val onItemClickListener : (Int) -> Unit
                      ) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() { //Criação do Adapter
    /*Classe que só será vista pela class MainActivity, que herdará o que está na classe RecyclerView
  mais especificamente o objeto Adapter onde como parâmetro espera o tipo da classe da Célula (ViewHolder) */


    //LISTA VERTICAL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        //Método que espera o viewHolder e infle o layout
        //Devemos apontar qual layout será jogado para dentro da RV.
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent,false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]//Recebendo os dados de acordo com a posição declarada
        holder.bind(category)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

     inner class CategoryViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
         fun bind(category: Category) { /*Função criada para integrar o layout a cada campo*/
                val txtTitlle : TextView = itemView.findViewById(R.id.txt_tittle)
                txtTitlle.text = category.name
                val rvCategory : RecyclerView = itemView.findViewById(R.id.rv_category )
                rvCategory.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
             rvCategory.adapter = MovieAdapter(category.movies, R.layout.movie_item, onItemClickListener)

         }
     }
}