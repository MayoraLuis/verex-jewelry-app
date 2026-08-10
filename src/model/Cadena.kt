package model

class Cadena(
    id: String,
    nombre: String,
    materialBase: Material,
    pesoGramos: Double,
    precioBaseElaboracion: Double,
    val longitudCm: Int
) : Joya(id, nombre, materialBase, pesoGramos, precioBaseElaboracion) {

    override fun calcularPrecioFinal(): Double {
        val costoMaterial = pesoGramos * materialBase.multiplicadorGramo
        val costoLongitudExtra = if (longitudCm > 45) (longitudCm - 45) * 1.5 else 0.0
        return precioBaseElaboracion + costoMaterial + costoLongitudExtra
    }

    override fun obtenerDetalle(): String {
        return "${super.obtenerDetalle()} | Longitud: ${longitudCm}cm | Precio Total: $${"%.2f".format(calcularPrecioFinal())}"
    }
}