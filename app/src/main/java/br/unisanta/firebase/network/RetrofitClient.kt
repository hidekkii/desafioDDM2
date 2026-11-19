package br.unisanta.firebase.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://fakestoreapi.com/"

    // A Fake Store API pode recusar requisições sem User-Agent (erro 403)
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("User-Agent", "MinhaLoja/1.0 (Android)")
                    .build()
            )
        }
        .build()

    val produtoService: ProdutoService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProdutoService::class.java)
    }
}
