package view

import controller.VerexManager
import model.*
import util.LoggerErrores
import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)
    val manager = VerexManager()

    println("********************************************************")
    println("*    BIENVENIDO A VEREX JEWELRY APP (SISTEMA BASE)     *")
    println("********************************************************")

    var usuarioActual: Usuario? = null

    // Autenticación por Consola
    while (usuarioActual == null) {
        print("\nIngrese ID de Usuario (U001 = Cliente, U002 = Admin): ")
        val idInput = scanner.nextLine().trim()
        val userFound = manager.obtenerUsuario(idInput)
        
        if (userFound != null) {
            usuarioActual = userFound
            println("Sesión iniciada como: ${usuarioActual.nombre} [Rol: ${usuarioActual.rol}]")
        } else {
            println("Usuario no encontrado. Intente nuevamente.")
            LoggerErrores.registrarError("AUTENTICACION", "Intento de inicio de sesión fallido con ID: $idInput")
        }
    }

    var salir = false
    while (!salir) {
        try {
            mostrarMenu(usuarioActual.rol)
            print("Seleccione una opción: ")
            val opcionInput = scanner.nextLine()
            val opcion = opcionInput.toIntOrNull() ?: throw NumberFormatException("Debe ingresar un número entero válido. Entrada recibida: '$opcionInput'")

            when (usuarioActual.rol) {
                RolUsuario.CLIENTE -> ejecutarMenuCliente(opcion, manager, usuarioActual, scanner)
                RolUsuario.ADMINISTRADOR -> ejecutarMenuAdmin(opcion, manager, scanner, { salir = true })
            }
        } catch (e: Exception) {
            println("\n ERROR: ${e.message}")
            LoggerErrores.registrarError("MENU_PRINCIPAL", e.message ?: "Error desconocido", e)
        }
    }

    println("\nGracias por utilizar el sistema de Joyería Verex.")
}

fun mostrarMenu(rol: RolUsuario) {
    println("\n--- MENÚ PRINCIPAL ---")
    if (rol == RolUsuario.CLIENTE) {
        println("1. Ver Catálogo de Joyas")
        println("2. Cotizar y Realizar Pedido")
        println("3. Salir")
    } else {
        println("1. Registrar Nueva Joya al Inventario")
        println("2. Ver Lista de Pedidos / Resumen")
        println("3. Cambiar Estado de Pedido (Con Evidencia Fotográfica)")
        println("4. Ver Dashboard de Reportes y Estadísticas")
        println("5. Salir")
    }
}

fun ejecutarMenuCliente(opcion: Int, manager: VerexManager, usuario: Usuario, scanner: Scanner) {
    when (opcion) {
        1 -> {
            println("\n--- CATÁLOGO DE JOYAS ---")
            manager.listarTodos().forEach { println(it.obtenerDetalle()) }
        }
        2 -> {
            println("\n--- COTIZADOR Y NUEVO PEDIDO ---")
            println("Seleccione tipo de joya a cotizar: 1. Anillo | 2. Cadena")
            val tipo = scanner.nextLine().toIntOrNull() ?: 1
            
            print("Nombre descriptivo: ")
            val nombre = scanner.nextLine()
            
            println("Seleccione Material: 1. Plata .925 | 2. Oro 10K | 3. Oro 14K")
            val matOpt = scanner.nextLine().toIntOrNull() ?: 1
            val material = when(matOpt) {
                2 -> Material.ORO_10K
                3 -> Material.ORO_14K
                else -> Material.PLATA_925
            }

            print("Peso estimado en gramos: ")
            val peso = scanner.nextLine().toDoubleOrNull() ?: 2.0

            val joyaCreada: Joya = if (tipo == 1) {
                print("Talla del anillo (ej. 7): ")
                val talla = scanner.nextLine().toIntOrNull() ?: 7
                Anillo("J${System.currentTimeMillis().toString().takeLast(4)}", nombre, material, peso, 25.0, talla)
            } else {
                print("Longitud en cm (ej. 50): ")
                val longitud = scanner.nextLine().toIntOrNull() ?: 45
                Cadena("J${System.currentTimeMillis().toString().takeLast(4)}", nombre, material, peso, 20.0, longitud)
            }

            println("\n Cotización realizada: Precio Final = $${"%.2f".format(joyaCreada.calcularPrecioFinal())}")
            
            print("¿Desea agregar un grabado personalizado? (s/n): ")
            val opGrabado = scanner.nextLine()
            var grabado = ""
            if (opGrabado.equals("s", ignoreCase = true)) {
                print("Texto para grabado: ")
                grabado = scanner.nextLine()
            }

            val nuevoPedido = Pedido(
                idPedido = "P${(100..999).random()}",
                cliente = usuario,
                listaJoyas = mutableListOf(joyaCreada),
                grabadoPersonalizado = grabado
            )

            manager.crearPedido(nuevoPedido)
            println("${nuevoPedido.obtenerResumen()}")
        }
        3 -> System.exit(0)
        else -> println("Opción no válida.")
    }
}

fun ejecutarMenuAdmin(opcion: Int, manager: VerexManager, scanner: Scanner, alSalir: () -> Unit) {
    when (opcion) {
        1 -> {
            println("\n--- REGISTRAR NUEVA JOYA ---")
            print("ID Único: ")
            val id = scanner.nextLine()
            print("Nombre: ")
            val nombre = scanner.nextLine()
            val joya = Anillo(id, nombre, Material.ORO_14K, 4.0, 30.0, 6)
            manager.agregarJoya(joya)
            println("Joya agregada al inventario.")
        }
        2 -> {
            println("\n--- LISTA DE PEDIDOS ---")
            manager.generarReportesConsola()
        }
        3 -> {
            println("\n--- CAMBIAR ESTADO DE PEDIDO ---")
            print("ID del Pedido: ")
            val idPed = scanner.nextLine()
            println("Seleccione Nuevo Estado: 1. RECIBIDO | 2. EN_FABRICACION | 3. EMPAQUETADO | 4. ENVIADO")
            val estOpt = scanner.nextLine().toIntOrNull() ?: 1
            val nuevoEstado = when(estOpt) {
                2 -> EstadoPedido.EN_FABRICACION
                3 -> EstadoPedido.EMPAQUETADO
                4 -> EstadoPedido.ENVIADO
                else -> EstadoPedido.RECIBIDO
            }

            print("Ingrese la ruta de la foto de evidencia (ej: foto_evidencia.jpg): ")
            val rutaFoto = scanner.nextLine()

            try {
                manager.actualizarEstadoPedidoConFoto(idPed, nuevoEstado, rutaFoto)
                println("Estado del pedido actualizado exitosamente a $nuevoEstado.")
            } catch (e: Exception) {
                println("ERROR AL ACTUALIZAR: ${e.message}")
                LoggerErrores.registrarError("CAMBIO_ESTADO", e.message ?: "Error al actualizar estado", e)
            }
        }
        4 -> manager.generarReportesConsola()
        5 -> alSalir()
        else -> println("Opción no válida.")
    }
}