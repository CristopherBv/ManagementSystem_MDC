# 🏗️ Sistema de Gestión de Materiales de Construcción

Este proyecto consiste en una solución de software diseñada para optimizar las operaciones diarias de una constructora y su manejo de materiales. El sistema permite gestionar desde el catálogo de productos y el inventario hasta la generación de reportes financieros, garantizando precisión y eficiencia en la toma de decisiones comerciales.


## 🛠️ Stack Tecnológico

* **Lenguaje:** Java 17.0.12 (Oracle JDK)
* **Interfaz Gráfica:** JavaFX
* **Arquitectura:** Modelo-Vista-Controlador (MVC)
* **Manejo de Datos:** Archivos planos (CSV) mediante el patrón DAO
* **Gestión de Dependencias:** Apache Maven


## 📋 Requerimientos Funcionales (RF)

**RF-01. Gestión del Catálogo de Productos** > El sistema debe permitir al Gerente la creación, actualización y mantenimiento general del catálogo de materiales de construcción. Cada registro deberá almacenar de manera obligatoria el nombre, descripción, categoría, precio de venta y la disponibilidad actual.

**RF-02. Búsqueda y Consulta de Materiales** > El sistema debe proporcionar al Vendedor y al Gerente una herramienta de búsqueda rápida que permita localizar productos por nombre, número de artículo o categoría, desplegando su información detallada, precio, existencias actuales y detalles técnicos.

**RF-03. Registro de Pedidos y Ventas** > El sistema debe permitir al Vendedor registrar los pedidos de los clientes. Durante este proceso, el Vendedor podrá seleccionar los productos deseados y la cantidad, mientras el sistema calcula de manera automática y en tiempo real el precio total del pedido o venta.

**RF-04. Generación de Recibos y Comprobantes** > Al finalizar una transacción, el sistema debe generar automáticamente un recibo para la venta, el cual incluirá el desglose detallado de la compra: lista de productos, precios unitarios, cantidades y el importe total a pagar.

**RF-05. Gestión de Clientes y Programas de Lealtad** > El sistema debe permitir al Vendedor registrar y mantener la información de clientes habituales (datos de contacto y preferencias). Asimismo, el sistema administrará un programa de lealtad, calculando y habilitando descuentos para clientes frecuentes al momento de la venta.

**RF-06. Aplicación de Descuentos y Promociones** > El sistema debe calcular automáticamente los descuentos aplicables y permitir al Gerente (para configuración) y al Vendedor (al momento del cobro) aplicar promociones especiales ya sea sobre productos específicos o sobre el total de la compra.

**RF-07. Gestión de Salidas y Entradas de Inventario** > El sistema proveerá una interfaz simplificada para el Almacenista, permitiéndole registrar exclusivamente la salida de productos terminados hacia el cliente y la entrada de nuevo material al almacén, actualizando automáticamente las existencias para garantizar la disponibilidad.

**RF-08. Sistema de Alertas de Stock** > El sistema debe monitorear constantemente los niveles de inventario y generar notificaciones visuales dirigidas al Gerente y al Vendedor cuando las existencias de un producto alcancen su límite mínimo preestablecido.

**RF-09. Gestión de Proveedores y Registro de Compras** > El sistema debe mantener un catálogo con la información de contacto de los proveedores. Además, debe permitir al Gerente y al Vendedor registrar las compras de reabastecimiento realizadas a dichos proveedores, enlazando estos registros al inventario.

**RF-10. Historial y Consulta de Ventas** > El sistema debe almacenar un registro histórico inmutable de todas las ventas realizadas (incluyendo fecha, productos, precios y cliente asociado). El Vendedor y el Gerente podrán realizar búsquedas y consultas sobre este registro en cualquier momento.

**RF-11. Generación de Reportes e Inteligencia de Negocio** > El sistema debe permitir exclusivamente al Gerente la generación de reportes financieros y de rendimiento. Esto incluye informes de ventas diarias, semanales, mensuales y anuales, así como análisis de ventas filtrados por producto, categoría y período de tiempo.


## ⚙️ Requerimientos No Funcionales (RNF)

**RNF-01. Interfaz de Usuario Intuitiva y Amigable** > El sistema debe contar con una interfaz gráfica moderna desarrollada en JavaFX. El diseño debe ser limpio y fácil de usar para el personal de la constructora, permitiendo la gestión de productos, ventas y reportes con un mínimo de clics y una curva de aprendizaje reducida.

**RNF-02. Persistencia de Datos mediante Archivos Planos** > Para facilitar la portabilidad, el sistema utilizará archivos en formato CSV como mecanismo principal de almacenamiento. El sistema debe garantizar que la lectura y escritura en estos archivos sea rápida y que los datos se mantengan consistentes tras cada operación.

**RNF-03. Modularidad y Mantenibilidad (Arquitectura MVC)** > El software se construirá bajo el patrón de diseño Modelo-Vista-Controlador. Esto asegura que la lógica de negocio, la interfaz gráfica y el acceso a datos (DAO) estén separados, facilitando futuras actualizaciones o el cambio de motor de persistencia sin afectar la estabilidad global.

**RNF-04. Compatibilidad y Entorno de Ejecución** > La aplicación debe ser totalmente compatible con Java Oracle OpenJDK 17.0.12. El código debe ser portable para que cualquier integrante del equipo pueda ejecutarlo en su entorno local (Windows, macOS o Linux) simplemente clonando el repositorio y cargando las dependencias.

**RNF-05. Integridad y Validación de Datos** > A pesar de usar archivos CSV, el sistema debe implementar validaciones lógicas estrictas. No se debe permitir el registro de ventas con stock insuficiente, ni la entrada de datos nulos o formatos incorrectos (como letras en campos de precio) para prevenir la corrupción de los archivos de almacenamiento.


## 🚀 Instalación y Ejecución

1. Clonar el repositorio:
