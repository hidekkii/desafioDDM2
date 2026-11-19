package br.unisanta.firebase.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import br.unisanta.firebase.databinding.ActivityProdutosBinding
import br.unisanta.firebase.model.Produto
import br.unisanta.firebase.network.RetrofitClient
import kotlinx.coroutines.launch

class ProdutosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProdutosBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (auth.currentUser == null) {
            voltarParaLogin()
            return
        }

        binding = ActivityProdutosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.layoutManager = LinearLayoutManager(this)

        binding.btnFavoritos.setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }
        binding.btnSair.setOnClickListener {
            auth.signOut()
            voltarParaLogin()
        }
        binding.btnTentarNovamente.setOnClickListener { carregarProdutos() }

        carregarProdutos()
    }

    private fun carregarProdutos() {
        binding.progress.visibility = View.VISIBLE
        binding.grupoErro.visibility = View.GONE
        binding.recycler.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val produtos = RetrofitClient.produtoService.listarProdutos()
                binding.recycler.adapter = ProdutoAdapter(produtos) { salvarFavorito(it) }
                binding.recycler.visibility = View.VISIBLE
            } catch (e: Exception) {
                binding.txtErro.text = "Erro ao carregar os produtos. Verifique sua conexão."
                binding.grupoErro.visibility = View.VISIBLE
            } finally {
                binding.progress.visibility = View.GONE
            }
        }
    }

    private fun salvarFavorito(produto: Produto) {
        val uid = auth.currentUser?.uid ?: return voltarParaLogin()

        val dados = hashMapOf(
            "id" to produto.id,
            "titulo" to produto.title,
            "preco" to produto.price,
            "categoria" to produto.category,
            "salvoEm" to FieldValue.serverTimestamp()
        )

        FirebaseFirestore.getInstance()
            .collection("usuarios").document(uid)
            .collection("favoritos").document(produto.id.toString())
            .set(dados)
            .addOnSuccessListener {
                Toast.makeText(this, "Salvo nos favoritos!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    private fun voltarParaLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
