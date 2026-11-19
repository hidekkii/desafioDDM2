package br.unisanta.firebase.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import br.unisanta.firebase.databinding.ActivityFavoritosBinding
import br.unisanta.firebase.model.Favorito

class FavoritosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritosBinding
    private val db = FirebaseFirestore.getInstance()
    private var adapter: FavoritoAdapter? = null
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val usuario = FirebaseAuth.getInstance().currentUser
        if (usuario == null) {
            finish()
            return
        }
        uid = usuario.uid

        binding = ActivityFavoritosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.btnVoltar.setOnClickListener { finish() }

        carregarFavoritos()
    }

    private fun favoritosRef() =
        db.collection("usuarios").document(uid).collection("favoritos")

    private fun carregarFavoritos() {
        binding.progress.visibility = View.VISIBLE
        binding.txtVazio.visibility = View.GONE

        favoritosRef()
            .orderBy("salvoEm", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { resultado ->
                binding.progress.visibility = View.GONE

                val lista = resultado.documents.map { doc ->
                    Favorito(
                        id = doc.getLong("id") ?: doc.id.toLongOrNull() ?: 0L,
                        titulo = doc.getString("titulo") ?: "",
                        preco = doc.getDouble("preco") ?: 0.0,
                        categoria = doc.getString("categoria") ?: "",
                        salvoEm = doc.getTimestamp("salvoEm")?.toDate()
                    )
                }.toMutableList()

                adapter = FavoritoAdapter(lista) { removerFavorito(it) }
                binding.recycler.adapter = adapter
                atualizarVazio(lista.isEmpty())
            }
            .addOnFailureListener {
                binding.progress.visibility = View.GONE
                Toast.makeText(this, "Erro ao carregar: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    private fun removerFavorito(favorito: Favorito) {
        favoritosRef().document(favorito.id.toString())
            .delete()
            .addOnSuccessListener {
                adapter?.remover(favorito)
                atualizarVazio((adapter?.itemCount ?: 0) == 0)
                Toast.makeText(this, "Removido dos favoritos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao remover: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    private fun atualizarVazio(vazio: Boolean) {
        binding.txtVazio.visibility = if (vazio) View.VISIBLE else View.GONE
    }
}
