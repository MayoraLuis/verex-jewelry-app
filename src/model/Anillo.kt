package model

class Anillo(
    id: String,
    nombre: String,
    materialBase: Material,
    pesoGramos: Double,
    precioBaseElaboracion: Double,
    val talla: Int
) : Joya(id, nombre, materialBase, pesoGramos, precioBaseElaboracion) {

    override fun calcularPrecioFinal(): Double {
        val costoMaterial = pesoGramos * materialBase.multiplicadorGramo
        val costoTallaExtra = if (talla > 7) (talla - 7) * 5.0 else 0.0
        return precioBaseElaboracion + costoMaterial + costoTallaExtra
    }

    override fun obtenerDetalle(): String {
        return "${super.obtenerDetalle()} | Talla: $talla | Precio Total: $${"%.2f".format(calcularPrecioFinal())}"
    }
}