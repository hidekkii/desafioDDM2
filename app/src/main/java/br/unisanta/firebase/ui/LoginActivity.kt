package br.unisanta.firebase.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import br.unisanta.firebase.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Já logado? Vai direto para a lista.
        if (auth.currentUser != null) {
            irParaProdutos()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEntrar.setOnClickListener { entrar() }
        binding.btnCriarConta.setOnClickListener { criarConta() }
    }

    private fun entrar() {
        val (email, senha) = lerCampos() ?: return
        carregando(true)
        auth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener { irParaProdutos() }
            .addOnFailureListener { erro ->
                carregando(false)
                mostrarErro(traduzirErro(erro))
            }
    }

    private fun criarConta() {
        val (email, senha) = lerCampos() ?: return
        carregando(true)
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { irParaProdutos() }
            .addOnFailureListener { erro ->
                carregando(false)
                mostrarErro(traduzirErro(erro))
            }
    }

    /** Valida os campos e devolve (email, senha), ou null se houver erro. */
    private fun lerCampos(): Pair<String, String>? {
        val email = binding.edtEmail.text.toString().trim()
        val senha = binding.edtSenha.text.toString()
        when {
            email.isEmpty() -> mostrarErro("Informe o e-mail.")
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                mostrarErro("E-mail inválido. Confira o formato (ex.: nome@dominio.com).")
            senha.isEmpty() -> mostrarErro("Informe a senha.")
            senha.length < 6 -> mostrarErro("A senha deve ter pelo menos 6 caracteres.")
            else -> {
                binding.txtErro.visibility = View.GONE
                return email to senha
            }
        }
        return null
    }

    private fun traduzirErro(erro: Exception): String = when (erro) {
        is FirebaseAuthWeakPasswordException -> "A senha deve ter pelo menos 6 caracteres."
        is FirebaseAuthUserCollisionException -> "Já existe uma conta com esse e-mail."
        is FirebaseAuthInvalidUserException -> "Não existe conta com esse e-mail."
        is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha incorretos."
        is FirebaseNetworkException -> "Sem conexão com a internet."
        else -> "Não foi possível autenticar: ${erro.localizedMessage ?: "erro desconhecido"}"
    }

    private fun mostrarErro(mensagem: String) {
        binding.txtErro.text = mensagem
        binding.txtErro.visibility = View.VISIBLE
    }

    private fun carregando(ativo: Boolean) {
        binding.progress.visibility = if (ativo) View.VISIBLE else View.GONE
        binding.btnEntrar.isEnabled = !ativo
        binding.btnCriarConta.isEnabled = !ativo
    }

    private fun irParaProdutos() {
        startActivity(Intent(this, ProdutosActivity::class.java))
        finish()
    }
}
