package br.unisanta.firebase.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.unisanta.firebase.databinding.ItemProdutoBinding
import br.unisanta.firebase.model.Produto
import java.text.NumberFormat
import java.util.Locale

class ProdutoAdapter(
    private val produtos: List<Produto>,
    private val aoTocar: (Produto) -> Unit
) : RecyclerView.Adapter<ProdutoAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemProdutoBinding) : RecyclerView.ViewHolder(binding.root)

    private val moeda = NumberFormat.getCurrencyInstance(Locale.US)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemProdutoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produto = produtos[position]
        holder.binding.txtTitulo.text = produto.title
        holder.binding.txtPreco.text = moeda.format(produto.price)
        holder.binding.txtCategoria.text = produto.category
        holder.itemView.setOnClickListener { aoTocar(produto) }
    }

    override fun getItemCount() = produtos.size
}
