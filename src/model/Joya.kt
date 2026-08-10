package model

abstract class Joya(
    val id: String,
    val nombre: String,
    var materialBase: Material,
    var pesoGramos: Double,
    var precioBaseElaboracion: Double
) {
    abstract fun calcularPrecioFinal(): Double

    open fun obtenerDetalle(): String {
        return "[$id] $nombre - Material: ${materialBase.nombre} | Peso: ${pesoGramos}g"
    }
}