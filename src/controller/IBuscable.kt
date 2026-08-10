package controller

interface IBuscable<T> {
    fun buscarPorId(id: String): T?
    fun listarTodos(): List<T>
}