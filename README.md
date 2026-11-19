# MinhaLoja

App Android (Kotlin + XML) com Retrofit (Fake Store API), Firebase Authentication e Cloud Firestore.

## Integrantes
- Renan Hideki  
- Osvaldo Vasconcellos
- Enzo Alonso

## Como rodar
1. Clone o repositório e abra no Android Studio.
2. No [Console do Firebase](https://console.firebase.google.com), crie o projeto **MinhaLoja** e adicione um app Android com o mesmo pacote do projeto (`br.unisanta.firebase`).
3. Baixe o `google-services.json` e coloque em `app/` (o arquivo está no `.gitignore`, não é versionado).
4. Em **Authentication > Método de login**, ative **E-mail/senha**.
5. Em **Firestore Database**, crie o banco em **modo de teste**.
6. Execute o app em um emulador ou dispositivo com internet.

## Estrutura
- `model/` data classes (`Produto`, `Rating`, `Favorito`)
- `network/` interface Retrofit (`ProdutoService`) e `RetrofitClient`
- `ui/` Activities e adapters (Login, Produtos, Favoritos)

## Firestore
`usuarios/{uid}/favoritos/{idDoProduto}` com `id`, `titulo`, `preco`, `categoria`, `salvoEm`.
