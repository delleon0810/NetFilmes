package com.example.netfilmes.util

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.example.netfilmes.model.Category
import com.example.netfilmes.model.Movie
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection
import kotlin.math.log

class CategoryTask( private val callback : Callback) {
    //Classe que terá a tarefa de baixar os dados do servidor

    private val handler = Handler(Looper.getMainLooper()) //Entra na UI principal e faz a renderização

    interface Callback {
        fun onPreExecute() //Rodará antes de iniciar o Execute
        fun onResult (categories : List<Category>)
        fun onFailure(message: String)
    }


    fun execute(url: String) {  //Função que retornará uma URL
        //Estamos instanciando a classe EXECUTORS para ser possível a criação da Thread Pararela
        callback.onPreExecute()
        val executor = Executors.newSingleThreadExecutor()//Classe e método usado para criar a Thread 2

        executor.execute { //Tudo que estiver dentro desse bloco será aplicado na Thread 2 (Pararela)
            //Iniciando conexão HTTP, por padrão utilizamos a Classe URL que aceita uma String como parâmetro

            var urlConnection : HttpsURLConnection? = null
            var stream : InputStream? = null

            try {
                val requestUrl = URL(url) //1º Passo abrindo URL
                urlConnection = requestUrl.openConnection() as HttpsURLConnection // 2º Conexão aberta com o servidor
                urlConnection.readTimeout = 2000 //Se demorar mais que 2000mSegundos (2s) o código dará uma exceção (TEMPO DE LEITURA)
                urlConnection.connectTimeout = 2000 //TEMPO DE CONEXÃO
                val statusCode: Int = urlConnection.responseCode //Buscando o conteúdo do servidor

                //Validando a requisição de acordo com os Métodos HTTP
                if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor!")
                }

                //Se o bloco acima não for verdade irá fazer a leitura das Strings do servidor
                stream = urlConnection.inputStream //Sequência de Bytes convertida em String
                val jsonAsString = stream.bufferedReader()// BufferedReader é um Espaço de memória onde é anexado todos os bytes
                        .use { it.readText() } // Aqui pegamos os Bytes e o leitor irá transforma-los em String (bytes -> String)

                val categories = toCategories(jsonAsString)
                handler.post {
                    callback.onResult(categories) //Esse código roda dentro da UI-Thread
                }
            }

            catch (e: Exception) {
                val message = e.message?: "erro desconhecido"
                Log.e("Teste", message, e) //Se o bloco acima for TRUE será lançado essa exceção

                handler.post {
                    callback.onFailure(message)
                }
            } finally {  //Bloco de código disparado sempre que termina o TRY ou o CATCH
                urlConnection?.disconnect()//Fechando conexões
                stream?.close()//Fechando Conexões
            }

        }
    }

    //MAPEANDO CATEGORIAS
    private fun toCategories(jsonAsString : String) : List<Category>{
        val categories = mutableListOf<Category>() //Criando Lista Vazia de categorias

        val jsonRoot = JSONObject(jsonAsString) //Buscando o objeto em Json
        val jsonCategories = jsonRoot.getJSONArray("category") //Buscando a chave category e o que nela contém

        for (i in 0  until jsonCategories.length()){ //Laço que buscará todas as coisas presentes dentro de category
            val jsonCategory = jsonCategories.getJSONObject(i)//Buscando categorias por seus respectivos índices

            val title = jsonCategory.getString("title") //Buscando os titulos das categorias
            val jsonMovies = jsonCategory.getJSONArray("movie") // Buscando a lista de filmes

            val movies = mutableListOf<Movie>() //Criando lista vazia dos filmes
            for (j in 0 until jsonMovies.length()) {
                val jsonMovie = jsonMovies.getJSONObject(j)
                val id = jsonMovie.getInt("id")
                val coverUrl= jsonMovie.getString("cover_url") //Mapeando o que tem dentro da chave Cover Url
                movies.add(Movie(id, coverUrl)) //Criando a lista de filmes
            }

            categories.add(Category(title, movies)) //Criando lista de categorias
        }

        return categories
    }

}