package com.example.netfilmes.model

import androidx.annotation.DrawableRes

data class Movie(val id: Int,
                 val coverUrl : String,
                 val title : String = "",
                 val desc : String ="",
                 val cast: String = ""
) //Classe onde ficará as propriedades do filme, e seus dados





/* 1º O filme conterá uma imagem que é carregada da internet, onde pra obtermos a imagem necessitamos
    da sua URL, que é uma string.

    2º ---- Está na classe Category ---
 */