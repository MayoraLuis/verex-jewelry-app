# verex-jewelry-app
# Joyería Verex — Sistema de Gestión y Cotización Móvil (Etapa 2: Desarrollo Base)

**Universidad Don Bosco**  
**Facultad de Estudios Tecnológicos | Escuela de Computación**  
**Materia:** Desarrollo de Software para Móviles  
**Ciclo:** 02-2026

---

## Integrante del Equipo
| Nombre Completo | Carnet / Usuario GitHub | Rol en el Proyecto |
| :--- | :--- | :--- |
| Luis Ernesto Mayora Claros | @MayoraLuis | Arquitectura Base, Dominio y Logging |

---

##  Descripción del Proyecto
Este proyecto constituye el núcleo lógico y funcional (Etapa 2) para el sistema integral de comercio electrónico y gestión operativa de **Joyería Verex**. Desarrollado en **Kotlin**, el sistema implementa la lógica de negocio central en modo consola para administrar el inventario de piezas en metales preciosos (Plata Ley .925, Oro 10K, Oro 14K), procesar cotizaciones dinámicas con algoritmos de tasación polimórfica, gestionar pedidos con control de estados y registrar anomalías operativas mediante persistencia en archivos de registro.

---

## Especificaciones Técnicas y Paradigma POO

El desarrollo se fundamenta en una separación estricta de responsabilidades bajo el patrón arquitectónico **MVC (Modelo - Vista - Controlador)** adaptado con utilidades de persistencia:

### 1. Modelo de Dominio (`package model`)
* **Abstracción y Herencia:** Implementación de la clase base abstracta `Joya`, la cual define las propiedades esenciales de cualquier artículo orfebre (`id`, `nombre`, `materialBase`, `pesoGramos`, `precioBaseElaboracion`) y el contrato del método de tasación.
* **Polimorfismo Dinámico:**
    * Las subclases `Anillo` y `Cadena` sobrescriben y especializan el método `calcularPrecioFinal()`.
    * `Anillo` calcula recargos por incremento de circunferencia en tallas superiores a 7 (mayor volumen de metal fundido).
    * `Cadena` procesa la longitud lineal en centímetros y peso para el cálculo estructural.
* **Tipado Estricto con Enumeraciones (`Enums.kt`):**
    * `Material`: Define el costo y multiplicador por gramo (`PLATA_925`, `ORO_10K`, `ORO_14K`).
    * `RolUsuario`: Restricción de perfiles de acceso (`CLIENTE` y `ADMINISTRADOR`).
    * `EstadoPedido`: Flujo de fabricación y logística (`RECIBIDO`, `EN_FABRICACION`, `EMPAQUETADO`, `ENVIADO`).

### 2. Capa de Control y Gestión (`package controller`)
* **Manejo Avanzado de Colecciones:** Uso de `MutableList` y operaciones funcionales (`filter`, `map`, `find`, `any`) para administrar inventarios, usuarios y pedidos en memoria.
* **Lógica Transaccional:** Registro, actualización y cálculo reactivo de cotizaciones y pedidos en tiempo de ejecución.

### 3. Sistema de Registro y Auditoría (`package util`)
* **Logging Físico en Disco (`LoggerErrores.kt`):**
    * Módulo `object` (Singleton) que intercepta errores y excepciones del sistema (`try-catch`).
    * Persistencia en tiempo real en el archivo `log_errores_verex.txt`.
    * Cada registro almacena: marca de tiempo estandarizada (`yyyy-MM-dd HH:mm:ss`), módulo origen, mensaje amigable de error y el detalle técnico de la excepción (`localizedMessage`).

### 4. Capa de Presentación (`package view`)
* **Interfaz de Consola (`Main.kt`):** Sistema de menús interactivos con validación estricta de tipos de datos, previniendo cierres inesperados por entradas numéricas erróneas.

---
## Estructura del Proyecto
verex-jewelry-app/
│
├── src/
│   ├── controller/
│   │   ├── JoyaController.kt       # Gestión del catálogo y operaciones CRUD
│   │   ├── PedidoController.kt     # Transacciones de pedidos y cambios de estado
│   │   └── UsuarioController.kt    # Autenticación y control de accesos
│   │
│   ├── model/
│   │   ├── Joya.kt                 # Clase base abstracta
│   │   ├── Anillo.kt               # Subclase especializada con lógica de tallas
│   │   ├── Cadena.kt               # Subclase especializada con longitud lineal
│   │   ├── Usuario.kt              # Entidad de usuarios del sistema
│   │   ├── Pedido.kt               # Entidad transaccional de órdenes
│   │   └── Enums.kt                # Enumeraciones (Material, Rol, Estado)
│   │
│   ├── util/
│   │   └── LoggerErrores.kt        # Gestor de persistencia de logs en .txt
│   │
│   └── view/
│       └── Main.kt                 # Interfaz de consola interactiva del sistema
│
├── log_errores_verex.txt           # Archivo de persistencia de errores y excepciones
├── .gitignore                      # Exclusión de binarios y archivos temporales
└── README.md                       # Documentación técnica y datos de entrega

---

## Guía de Ejecución Local

1. **Requisitos Previos:**
    * Java Development Kit (JDK) versión 17 o superior.
    * Kotlin SDK versión 1.9 o superior.
    * Entorno recomendado: IntelliJ IDEA o compilador `kotlinc`.

2. **Pasos para Compilar y Ejecutar:**
   ```bash
   # Clonar el repositorio
   git clone [https://github.com/](https://github.com/)[TU-ORGANIZACION-O-USUARIO]/verex-jewelry-app.git

   # Abrir el directorio del proyecto
   cd verex-jewelry-app

   # Ejecutar desde IntelliJ IDEA ejecutando src/view/Main.kt

##  Matriz de Casos de Prueba y Manejo de Errores

| Caso de Prueba | Entrada de Usuario | Comportamiento del Sistema | Registro en `log_errores_verex.txt` |
| :--- | :--- | :--- | :--- |
| **TC-01:** Entrada no numérica en talla | Cadena `"abc"` en cotizador | Emite advertencia y solicita reintentar sin cerrar la app. | `[MÓDULO: Cotizador] ERROR: Entrada no numérica en talla` |
| **TC-02:** Precio o peso negativo | Valor `-15.0` en inventario | Validación de rango deniega creación. | `[MÓDULO: Inventario] ERROR: Valor de peso o precio fuera de rango` |
| **TC-03:** Credenciales incorrectas | Contraseña vacía o inválida | Deniega acceso y emite aviso de autenticación. | `[MÓDULO: Auth] ERROR: Intento de inicio de sesión fallido` |
| **TC-04:** Cambio de estado de pedido | Transición secuencial de estado | Actualiza el estado y genera resumen en consola. | N/A (Operación exitosa) |