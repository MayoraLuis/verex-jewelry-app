package model

class Pedido(
    val idPedido: String,
    val cliente: Usuario,
    val listaJoyas: MutableList<Joya> = mutableListOf(),
    var grabadoPersonalizado: String = "",
    var fotoEvidenciaRuta: String = ""
) {
    var estado: EstadoPedido = EstadoPedido.RECIBIDO

    fun calcularTotalPedido(): Double {
        val subtotal = listaJoyas.sumOf { it.calcularPrecioFinal() }
        val costoGrabado = if (grabadoPersonalizado.isNotBlank()) 10.0 else 0.0
        return subtotal + costoGrabado
    }

    fun obtenerResumen(): String {
        val total = calcularTotalPedido()
        return "Pedido #$idPedido | Cliente: ${cliente.nombre} | Estado: $estado | Total: $${"%.2f".format(total)} | Evidencia Foto: ${if (fotoEvidenciaRuta.isBlank()) "Pendiente" else fotoEvidenciaRuta}"
    }
}