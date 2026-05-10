# 🏗️ Sistema de Gestión de Materiales de Construcción (SDG_MDC)

<div align="center">
  <img src="ruta/a/tu/logo.png" alt="Logo del Sistema" width="250"/>
</div>

<br>

<div align="center">

![Java](https://img.shields.io/badge/Java-17.0.12-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-UI-4796DA?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Apache_Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Arquitectura MVC](https://img.shields.io/badge/Arquitectura-MVC-232F3E?style=for-the-badge)
![Persistencia](https://img.shields.io/badge/Persistencia-CSV-4CAF50?style=for-the-badge)

</div>

---

## 📑 Tabla de Contenidos
- [Acerca del Proyecto](#-acerca-del-proyecto)
- [Vistas Previas del Sistema](#-vistas-previas-del-sistema)
- [Stack Tecnológico](#️-stack-tecnológico)
- [Requerimientos Funcionales](#-requerimientos-funcionales-rf)
- [Requerimientos No Funcionales](#️-requerimientos-no-funcionales-rnf)
- [Instalación y Ejecución](#-instalación-y-ejecución)

---

## 📖 Acerca del Proyecto

Este proyecto consiste en una solución de software diseñada para optimizar las operaciones diarias de una constructora y su manejo de materiales. El sistema permite gestionar desde el catálogo de productos y el inventario hasta la generación de reportes financieros, garantizando precisión y eficiencia en la toma de decisiones comerciales mediante accesos controlados por roles (Gerente, Vendedor, Almacenista).

---

## 📸 Vistas Previas del Sistema

> **Nota:** Aquí puedes ver cómo luce el sistema en acción. 

<details>
  <summary><b>1. Dashboard del Gerente (Inteligencia de Negocio)</b> <i>[Clic para expandir]</i></summary>
  <br>
  <div align="center">
    <img src="ruta/a/tu/imagen_dashboard.png" alt="Dashboard Gerente" width="800"/>
    <p><i>Panel principal con KPIs de ventas, alertas de stock e inventario en tiempo real.</i></p>
  </div>
</details>

<details>
  <summary><b>2. Punto de Venta (Vendedor)</b> <i>[Clic para expandir]</i></summary>
  <br>
  <div align="center">
    <img src="ruta/a/tu/imagen_punto_venta.png" alt="Punto de Venta" width="800"/>
    <p><i>Interfaz fluida para registro de compras, carrito y canje de puntos de lealtad.</i></p>
  </div>
</details>

<details>
  <summary><b>3. Control de Almacén (Almacenista)</b> <i>[Clic para expandir]</i></summary>
  <br>
  <div align="center">
    <img src="ruta/a/tu/imagen_almacen.png" alt="Control de Almacén" width="800"/>
    <p><i>Módulo para la recepción de proveedores mediante órdenes de compra y despacho a clientes.</i></p>
  </div>
</details>

---

## 🛠️ Stack Tecnológico

| Tecnología | Descripción |
| :--- | :--- |
| **☕ Java** | Versión 17.0.12 (Oracle JDK) - Lógica core del sistema. |
| **🎨 JavaFX** | Framework para el diseño de interfaces gráficas (GUI) modernas. |
| **🏗️ MVC** | Patrón de arquitectura Modelo-Vista-Controlador. |
| **📂 DAO & CSV** | Patrón de Acceso a Datos utilizando archivos planos (.csv) para persistencia. |
| **📦 Maven** | Herramienta de gestión de dependencias y empaquetado del proyecto. |

---

## 📋 Requerimientos Funcionales (RF)

- **`RF-01` Gestión del Catálogo de Productos:** > El sistema debe permitir al Gerente la creación, actualización y mantenimiento general del catálogo de materiales de construcción. Cada registro deberá almacenar obligatoriamente el nombre, descripción, categoría, precio de venta y disponibilidad actual.

- **`RF-02` Búsqueda y Consulta de Materiales:** > Proporciona al Vendedor y al Gerente una herramienta de búsqueda rápida que permita localizar productos por nombre, número o categoría, desplegando información detallada, precio y existencias.

- **`RF-03` Registro de Pedidos y Ventas:** > Permite al Vendedor registrar pedidos. Seleccionará productos y cantidades, mientras el sistema calcula de manera automática y en tiempo real el precio total.

- **`RF-04` Generación de Recibos y Comprobantes:** > Al finalizar una transacción, genera automáticamente un recibo con desglose detallado: productos, precios unitarios, cantidades e importe total.

- **`RF-05` Gestión de Clientes y Lealtad:** > Permite registrar clientes habituales. Administra un programa de lealtad calculando y habilitando descuentos para clientes frecuentes al momento de la venta.

- **`RF-06` Aplicación de Descuentos y Promociones:** > Calcula automáticamente descuentos y permite aplicar promociones especiales sobre productos específicos o el total de la compra (configurado por Gerente, aplicado por Vendedor).

- **`RF-07` Gestión de Salidas y Entradas de Inventario:** > Interfaz simplificada para el Almacenista:
  > * **Salidas:** Ingresa ticket/pedido, confirma entrega y descuenta stock automáticamente.
  > * **Entradas:** Ingresa ID de Orden de Compra, verifica lista esperada vs recibida. Suma stock real y notifica discrepancias al Gerente.

- **`RF-08` Sistema de Alertas de Stock:** > Monitoreo constante del inventario con notificaciones visuales (Gerente/Vendedor) cuando un producto alcance su límite mínimo.

- **`RF-09` Gestión de Proveedores y Órdenes de Compra:** > Mantiene catálogo de proveedores. Permite exclusivamente al Gerente generar "Órdenes de Compra" (Pendientes) que servirán como base para la entrada física de mercancía.

- **`RF-10` Historial y Consulta de Ventas:** > Registro inmutable de todas las ventas (fecha, productos, precios, cliente). Búsquedas y consultas habilitadas para Vendedor y Gerente.

- **`RF-11` Reportes e Inteligencia de Negocio:** > Exclusivo para el Gerente. Generación de reportes financieros/rendimiento (diarios, semanales, mensuales, anuales) y análisis de tendencia con exportación a `.txt`.

- **`RF-12` Autorización de Operaciones Restringidas:** > Validaciones (Override) en el Punto de Venta. Acciones críticas (cancelaciones, precios manuales, descuentos extra) disparan una solicitud del PIN de Gerente para proceder.

---

## ⚙️ Requerimientos No Funcionales (RNF)

- **`RNF-01` Interfaz Intuitiva (UI/UX):** > GUI moderna en JavaFX con diseño limpio, minimizando clics y reduciendo la curva de aprendizaje del personal.
- **`RNF-02` Persistencia en Archivos Planos:** > Almacenamiento portátil mediante formato CSV, garantizando lectura/escritura rápida y consistencia transaccional.
- **`RNF-03` Modularidad (MVC):** > Separación estricta entre Lógica de Negocio, Interfaz Gráfica y Acceso a Datos (DAO) para facilitar mantenimiento.
- **`RNF-04` Compatibilidad (Java 17):** > Ejecución asegurada en Windows, macOS y Linux mediante el clonado del repositorio y Maven.
- **`RNF-05` Integridad de Datos:** > Validaciones estrictas pre-guardado para evitar corrupción de CSVs (ej. rechazar ventas sin stock, prevenir letras en campos numéricos).

---

## 🚀 Instalación y Ejecución

Sigue estos pasos para ejecutar el proyecto en tu entorno local:

### 1. Prerrequisitos
Asegúrate de tener instalados:
- [Java JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Apache Maven](https://maven.apache.org/download.cgi)
- Git

### 2. Clonar el repositorio
Abre tu terminal de preferencia y ejecuta:
```bash
git clone [https://github.com/tu-usuario/SDG_MDC.git](https://github.com/tu-usuario/SDG_MDC.git)
cd SDG_MDC
