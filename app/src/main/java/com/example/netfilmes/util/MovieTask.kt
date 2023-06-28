package com.example.netfilmes.util

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.example.netfilmes.model.Category
import com.example.netfilmes.model.Movie
import com.example.netfilmes.model.MovieDetail
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection
import kotlin.math.log

class MovieTask(private val callback : Callback) {
    //Classe que terá a tarefa de baixar os dados do servidor

    private val handler = Handler(Looper.getMainLooper()) //Entra na UI principal e faz a renderização

    interface Callback {
        fun onPreExecute() //Rodará antes de iniciar o Execute
        fun onResult (movieDetail: MovieDetail)
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

                val movieDetail = toMovieDetail(jsonAsString)
                handler.post {
                    callback.onResult(movieDetail) //Esse código roda dentro da UI-Thread
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

    //MAPEANDO FILMES
    private fun toMovieDetail(jsonAsString : String) : MovieDetail{
        val json = JSONObject(jsonAsString)
        val id = json.getInt("id")
        val title = json.getString("title")
        val desc = json.getString("desc")
        val cast = json.getString("cast")
        val coverUrl = json.getString("cover_url")
        val jsonMovies = json.getJSONArray("movie")

        val similars = mutableListOf<Movie>()//Criando a lista de filmes similares
        for (i in 0 until jsonMovies.length()){
            val jsonMovie = jsonMovies.getJSONObject(i)

            val similiarId = jsonMovie.getInt("id")
            var similarCoverUrl = jsonMovie.getString("cover_url")

            val m = Movie(similiarId, similarCoverUrl)
            similars.add(m)
        }
        val movie = Movie(id,coverUrl, title, desc, cast)

        return MovieDetail(movie, similars)
    }


}