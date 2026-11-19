package br.unisanta.firebase.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.unisanta.firebase.databinding.ItemFavoritoBinding
import br.unisanta.firebase.model.Favorito
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class FavoritoAdapter(
    private val favoritos: MutableList<Favorito>,
    private val aoRemover: (Favorito) -> Unit
) : RecyclerView.Adapter<FavoritoAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemFavoritoBinding) : RecyclerView.ViewHolder(binding.root)

    private val moeda = NumberFormat.getCurrencyInstance(Locale.US)
    private val formatoData = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemFavoritoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fav = favoritos[position]
        holder.binding.txtTitulo.text = fav.titulo
        holder.binding.txtPreco.text = moeda.format(fav.preco)
        holder.binding.txtCategoria.text = fav.categoria
        holder.binding.txtData.text =
            fav.salvoEm?.let { "Salvo em ${formatoData.format(it)}" } ?: "Salvando..."
        holder.binding.btnRemover.setOnClickListener { aoRemover(fav) }
    }

    override fun getItemCount() = favoritos.size

    fun remover(favorito: Favorito) {
        val pos = favoritos.indexOf(favorito)
        if (pos >= 0) {
            favoritos.removeAt(pos)
            notifyItemRemoved(pos)
        }
    }
}
