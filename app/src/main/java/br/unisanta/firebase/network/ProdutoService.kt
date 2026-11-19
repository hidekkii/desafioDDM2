package br.unisanta.firebase.network

import br.unisanta.firebase.model.Produto
import retrofit2.http.GET

interface ProdutoService {
    @GET("products")
    suspend fun listarProdutos(): List<Produto>
}
