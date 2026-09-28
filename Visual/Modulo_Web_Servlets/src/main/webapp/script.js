/**
 * Lógica de la pantalla de inventario
 * Sistema de Inventario - Evidencia SENA
 */
const formulario = document.getElementById("formInventario");
const campoId = document.getElementById("idProducto");
const campoNombre = document.getElementById("nombreProducto");
const campoPrecio = document.getElementById("precioProducto");
const campoCantidad = document.getElementById("cantidadProducto");
const botonGuardar = document.getElementById("btnGuardar");
const botonCancelar = document.getElementById("btnCancelar");
const tabla = document.getElementById("tablaInventario");
const mensaje = document.getElementById("mensaje");

// Muestro un mensaje debajo del formulario (verde si salió bien, rojo si hubo un error)
function mostrarMensaje(texto, ok) {
    mensaje.textContent = texto;
    mensaje.className = ok ? "mensaje-ok" : "mensaje-error";
}

// Mando los datos al servlet. Si la sesión se venció, me devuelve al login
async function enviar(datos) {
    const respuesta = await fetch("ProductoServlet", {
        method: "POST",
        body: new URLSearchParams(datos)
    });
    if (respuesta.status === 401) {
        window.location.href = "login.html";
        return null;
    }
    return respuesta.json();
}

// Pido los productos del usuario y los dibujo en la tabla
async function cargarProductos() {
    try {
        const respuesta = await fetch("ProductoServlet");
        if (respuesta.status === 401) {
            window.location.href = "login.html";
            return;
        }
        const productos = await respuesta.json();
        pintarTabla(productos);
    } catch (error) {
        console.error("Error al cargar los productos:", error);
        mostrarMensaje("No se pudieron cargar los productos", false);
    }
}

// Dibujo las filas de la tabla (uso textContent para que un nombre raro no dañe la página)
function pintarTabla(productos) {
    tabla.innerHTML = "";

    if (productos.length === 0) {
        const fila = document.createElement("tr");
        const celda = document.createElement("td");
        celda.colSpan = 4;
        celda.textContent = "No hay productos registrados";
        fila.appendChild(celda);
        tabla.appendChild(fila);
        return;
    }

    productos.forEach(function (p) {
        const fila = document.createElement("tr");

        const celdaNombre = document.createElement("td");
        celdaNombre.textContent = p.nombre;

        const celdaPrecio = document.createElement("td");
        celdaPrecio.textContent = "$ " + Number(p.precio).toLocaleString("es-CO");

        const celdaCantidad = document.createElement("td");
        celdaCantidad.textContent = Number(p.cantidad).toLocaleString("es-CO");

        const celdaAcciones = document.createElement("td");

        const botonEditar = document.createElement("button");
        botonEditar.type = "button";
        botonEditar.textContent = "Editar";
        botonEditar.className = "btn-editar";
        botonEditar.addEventListener("click", function () {
            editarProducto(p);
        });

        const botonEliminar = document.createElement("button");
        botonEliminar.type = "button";
        botonEliminar.textContent = "Eliminar";
        botonEliminar.className = "btn-eliminar";
        botonEliminar.addEventListener("click", function () {
            eliminarProducto(p);
        });

        celdaAcciones.appendChild(botonEditar);
        celdaAcciones.appendChild(botonEliminar);

        fila.appendChild(celdaNombre);
        fila.appendChild(celdaPrecio);
        fila.appendChild(celdaCantidad);
        fila.appendChild(celdaAcciones);
        tabla.appendChild(fila);
    });
}

// Paso los datos del producto al formulario para poder editarlo
function editarProducto(p) {
    campoId.value = p.id;
    campoNombre.value = p.nombre;
    campoPrecio.value = p.precio;
    campoCantidad.value = p.cantidad;
    botonGuardar.textContent = "Actualizar";
    botonCancelar.style.display = "inline-block";
    campoNombre.focus();
}

// Dejo el formulario como estaba al principio (modo agregar)
function limpiarFormulario() {
    formulario.reset();
    campoId.value = "";
    botonGuardar.textContent = "Guardar";
    botonCancelar.style.display = "none";
}

// Elimino un producto, pero antes pido confirmación
async function eliminarProducto(p) {
    if (!confirm("¿Seguro que quieres eliminar \"" + p.nombre + "\"?")) {
        return;
    }
    try {
        const resultado = await enviar({ accion: "eliminar", id: p.id });
        if (!resultado) {
            return;
        }
        mostrarMensaje(resultado.mensaje, resultado.ok);
        if (resultado.ok) {
            // Si justo estaba editando ese producto, limpio el formulario
            if (campoId.value === String(p.id)) {
                limpiarFormulario();
            }
            cargarProductos();
        }
    } catch (error) {
        console.error("Error al eliminar:", error);
        mostrarMensaje("Error de comunicación con el servidor", false);
    }
}

// Al enviar el formulario: si hay un id estoy editando, si no, estoy agregando
formulario.addEventListener("submit", async function (e) {
    e.preventDefault();

    const editando = campoId.value !== "";
    const datos = {
        accion: editando ? "actualizar" : "agregar",
        nombre: campoNombre.value,
        precio: campoPrecio.value,
        cantidad: campoCantidad.value
    };
    if (editando) {
        datos.id = campoId.value;
    }

    try {
        const resultado = await enviar(datos);
        if (!resultado) {
            return;
        }
        mostrarMensaje(resultado.mensaje, resultado.ok);
        if (resultado.ok) {
            limpiarFormulario();
            cargarProductos();
        }
    } catch (error) {
        console.error("Error al guardar:", error);
        mostrarMensaje("Error de comunicación con el servidor", false);
    }
});

botonCancelar.addEventListener("click", function () {
    limpiarFormulario();
    mensaje.textContent = "";
});

// Cerrar sesión: le aviso al servidor y vuelvo al login
document.getElementById("btnCerrar").addEventListener("click", async function (e) {
    e.preventDefault();
    try {
        await enviar({ accion: "cerrar" });
    } catch (error) {
        console.error("Error al cerrar sesión:", error);
    }
    window.location.href = "login.html";
});

// Apenas carga la pantalla, traigo los productos
cargarProductos();