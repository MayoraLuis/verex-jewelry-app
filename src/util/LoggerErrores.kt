package util

import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LoggerErrores {
    private const val ARCHIVO_LOG = "log_errores_verex.txt"

    fun registrarError(modulo: String, mensajeError: String, excepcion: Exception? = null) {
        try {
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            val logFile = File(ARCHIVO_LOG)
            val writer = FileWriter(logFile, true)
            
            writer.appendLine("[$timestamp] [MÓDULO: $modulo] ERROR: $mensajeError")
            excepcion?.let {
                writer.appendLine("  Detalle Técnico: ${it.localizedMessage}")
            }
            writer.appendLine("--------------------------------------------------------------------------------")
            writer.close()
        } catch (e: Exception) {
            println("Error crítico al escribir en el archivo log: ${e.message}")
        }
    }
}