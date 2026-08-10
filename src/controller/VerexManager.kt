package controller

import model.*

class VerexManager : IBuscable<Joya> {
    private val inventarioJoyas: MutableList<Joya> = mutableListOf()
    private val registroPedidos: MutableList<Pedido> = mutableListOf()
    private val usuarios: MutableList<Usuario> = mutableListOf()

    init {
        // Datos semilla iniciales
        usuarios.add(Usuario("U001", "Cliente Prueba", "cliente@verex.com", RolUsuario.CLIENTE))
        usuarios.add(Usuario("U002", "Administrador Verex", "admin@verex.com", RolUsuario.ADMINISTRADOR))

        inventarioJoyas.add(Anillo("J001", "Anillo Solitario Dama", Material.ORO_14K, 3.5, 40.0, 7))
        inventarioJoyas.add(Cadena("J002", "Cadena Estilo Cuerda", Material.PLATA_925, 12.0, 15.0, 50))
    }

    override fun buscarPorId(id: String): Joya? {
        return inventarioJoyas.firstOrNull { it.id.equals(id, ignoreCase = true) }
    }

    override fun listarTodos(): List<Joya> = inventarioJoyas

    fun agregarJoya(joya: Joya) {
        inventarioJoyas.add(joya)
    }

    fun crearPedido(pedido: Pedido) {
        registroPedidos.add(pedido)
    }

    fun buscarPedidoPorId(id: String): Pedido? {
        return registroPedidos.firstOrNull { it.idPedido.equals(id, ignoreCase = true) }
    }

    fun actualizarEstadoPedidoConFoto(idPedido: String, nuevoEstado: EstadoPedido, rutaFoto: String): Boolean {
        val pedido = buscarPedidoPorId(idPedido) ?: throw IllegalArgumentException("Pedido #$idPedido no encontrado.")
        
        if ((nuevoEstado == EstadoPedido.EMPAQUETADO || nuevoEstado == EstadoPedido.ENVIADO) && rutaFoto.isBlank()) {
            throw IllegalStateException("Requisito incumplido: Debe adjuntar una foto de evidencia para cambiar al estado $nuevoEstado.")
        }

        pedido.estado = nuevoEstado
        if (rutaFoto.isNotBlank()) {
            pedido.fotoEvidenciaRuta = rutaFoto
        }
        return true
    }

    fun generarReportesConsola() {
        println("\n========================================================")
        println("       RESUMEN Y REPORTES DEL SISTEMA - JOYERÍA VEREX   ")
        println("========================================================")
        
        val totalVentas = registroPedidos.sumOf { it.calcularTotalPedido() }
        val pedidosCompletados = registroPedidos.count { it.estado == EstadoPedido.ENVIADO }
        println("• Total de Ventas Acumuladas: $${"%.2f".format(totalVentas)}")
        println("• Pedidos Completados/Despachados: $pedidosCompletados de ${registroPedidos.size}")

        println("\n--- DEMANDA POR MATERIAL ---")
        val joyasVendidas = registroPedidos.flatMap { it.listaJoyas }
        if (joyasVendidas.isEmpty()) {
            println("No se han realizado ventas aún.")
        } else {
            val porMaterial = joyasVendidas.groupBy { it.materialBase }
            porMaterial.forEach { (material, lista) ->
                println(" -> ${material.nombre}: ${lista.size} piezas vendidas")
            }
        }
        println("========================================================\n")
    }

    fun obtenerUsuario(id: String): Usuario? = usuarios.firstOrNull { it.id == id }
}