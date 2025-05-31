document.addEventListener("DOMContentLoaded", () => {
    console.log("📦 Módulo de gestión de productos cargado.");

    let idProductoPendienteEliminar = null;

    const $ = id => document.getElementById(id);
    const mostrarModal = id => new bootstrap.Modal($(id)).show();
    const ocultarModal = id => bootstrap.Modal.getInstance($(id))?.hide();

    const mostrarAlerta = (id, mensaje, tipo = "success") => {
        const alerta = $(id);
        if (!alerta) return;
        alerta.className = `alert alert-${tipo}`;
        alerta.textContent = mensaje;
        alerta.classList.remove("d-none");
        setTimeout(() => alerta.classList.add("d-none"), 4000);
    };

    const validarCamposProducto = ({ nombre, descripcion, descripcionBreve, categoria, precio, descuento, stock }) => {
        if (!nombre || !descripcion || !descripcionBreve || !categoria)
            return "❌ Todos los campos de texto son obligatorios";
        if (isNaN(precio) || precio <= 0)
            return "❌ El precio debe ser un número positivo";
        if (isNaN(descuento) || descuento < 0)
            return "❌ El descuento debe ser un número positivo o cero";
        if (isNaN(stock) || stock < 0)
            return "❌ La cantidad en stock no puede ser negativa";
        return null;
    };

    const renderProducto = producto => {
        const fila = document.createElement("tr");
        fila.dataset.id = producto.id;

        fila.innerHTML = `
            <td>${producto.nombre}</td>
            <td>${producto.descripcion}</td>
            <td>${producto.descripcion_breve}</td>
            <td>${producto.precio?.toFixed(2)} €</td>
            <td>${producto.descuento ?? 0}%</td>
            <td>${producto.stock}</td>
            <td>${producto.categoria}</td>
            <td>${producto.imagen ? `<img src="data:image/png;base64,${producto.imagen}" width="50">` : 'Sin imagen'}</td>
            <td>
                <button class="btn btn-success btn-sm editar-producto"
                    data-id="${producto.id}"
                    data-nombre="${encodeURIComponent(producto.nombre)}"
                    data-descripcion="${encodeURIComponent(producto.descripcion)}"
                    data-descripcionbreve="${encodeURIComponent(producto.descripcion_breve)}"
                    data-precio="${producto.precio}"
                    data-descuento="${producto.descuento}"
                    data-stock="${producto.stock}"
                    data-categoria="${encodeURIComponent(producto.categoria)}">
                    Editar
                </button>
            </td>
            <td>
                <button class="btn btn-danger btn-sm eliminar-producto"
                    data-id="${producto.id}" data-nombre="${producto.nombre}">
                    Eliminar
                </button>
            </td>
        `;

        fila.querySelector(".editar-producto").addEventListener("click", e =>
            cargarDatosEdicion(e.currentTarget)
        );

        fila.querySelector(".eliminar-producto").addEventListener("click", () => {
            idProductoPendienteEliminar = producto.id;
            $("nombreProductoAEliminar").textContent = producto.nombre;
            mostrarModal("modalConfirmarEliminarProducto");
        });

        return fila;
    };

    const cargarProductos = async () => {
        try {
            const res = await fetch("/productos/listar");
            if (!res.ok) throw new Error("No se pudo obtener la lista de productos");
            const productos = await res.json();
            const tbody = $("productosTableBody");
            tbody.innerHTML = "";
            productos.forEach(p => tbody.appendChild(renderProducto(p)));
        } catch (err) {
            console.error("❌ Error al cargar productos:", err);
        }
    };

    const cargarCategorias = async () => {
        try {
            const res = await fetch("/productos/categorias");
            if (!res.ok) throw new Error("No se pudo obtener la lista de categorías");
            const categorias = await res.json();
            ["categoria", "editCategoria"].forEach(id => {
                const select = $(id);
                if (!select) return;
                select.innerHTML = `<option value="">Selecciona una categoría</option>`;
                categorias.forEach(cat => {
                    const option = document.createElement("option");
                    option.value = cat;
                    option.textContent = cat;
                    select.appendChild(option);
                });
            });
        } catch (err) {
            console.error("❌ Error al cargar categorías:", err);
        }
    };

    const cargarDatosEdicion = btn => {
        const id = btn.dataset.id;
        if (!id || isNaN(id) || id <= 0) {
            mostrarAlerta("alertaProductos", "❌ ID no válido para edición", "danger");
            return;
        }

        $("editProductoId").value = id;
        $("editNombre").value = decodeURIComponent(btn.dataset.nombre);
        $("editDescripcion").value = decodeURIComponent(btn.dataset.descripcion);
        $("editDescripcionBreve").value = decodeURIComponent(btn.dataset.descripcionbreve);
        $("editPrecio").value = btn.dataset.precio;
        $("editDescuento").value = btn.dataset.descuento || 0;
        $("editStock").value = btn.dataset.stock;
        $("editCategoria").value = decodeURIComponent(btn.dataset.categoria);

        mostrarModal("editarProductoModal");
    };

    $("formProducto")?.addEventListener("submit", async e => {
        e.preventDefault();
        const form = e.currentTarget;
        const formData = new FormData(form);

        const datos = {
            nombre: formData.get("nombre")?.trim(),
            descripcion: formData.get("descripcion")?.trim(),
            descripcionBreve: formData.get("descripcionBreve")?.trim(),
            categoria: formData.get("categoria")?.trim(),
            precio: parseFloat(formData.get("precio")),
            descuento: parseFloat(formData.get("descuento")),
            stock: parseInt(formData.get("stock"))
        };

        const error = validarCamposProducto(datos);
        if (error) return mostrarAlerta("alertaProductos", error, "warning");

        try {
            const res = await fetch("/productos/guardar", { method: "POST", body: formData });
            const data = await res.json();
            if (!res.ok) throw data;
            mostrarAlerta("alertaProductos", "✅ Producto guardado con éxito");
            form.reset();
            cargarProductos();
        } catch (err) {
            mostrarAlerta("alertaProductos", err.mensaje || "❌ Error al guardar el producto", "danger");
        }
    });

    $("editarProductoForm")?.addEventListener("submit", async e => {
        e.preventDefault();
        const id = $("editProductoId").value;
        if (!id || isNaN(id) || id <= 0) {
            mostrarAlerta("alertaProductos", "❌ ID no válido", "danger");
            return;
        }

        const datos = {
            nombre: $("editNombre").value.trim(),
            descripcion: $("editDescripcion").value.trim(),
            descripcionBreve: $("editDescripcionBreve").value.trim(),
            categoria: $("editCategoria").value.trim(),
            precio: parseFloat($("editPrecio").value),
            descuento: parseFloat($("editDescuento").value),
            stock: parseInt($("editStock").value)
        };

        const formData = new FormData(e.currentTarget);
        const error = validarCamposProducto(datos);
        if (error) return mostrarAlerta("alertaProductos", error, "warning");

        try {
            const res = await fetch(`/productos/editar/${id}`, {
                method: "PUT",
                body: formData
            });
            const data = await res.json();
            if (!res.ok) throw data;

            mostrarAlerta("alertaProductos", data.mensaje || "✅ Producto actualizado correctamente");
            ocultarModal("editarProductoModal");
            cargarProductos();
        } catch (err) {
            mostrarAlerta("alertaProductos", err.mensaje || "❌ Error al actualizar producto", "danger");
        }
    });

    $("btnConfirmarEliminarProducto")?.addEventListener("click", () => {
        if (idProductoPendienteEliminar) {
            eliminarProducto(idProductoPendienteEliminar);
            ocultarModal("modalConfirmarEliminarProducto");
            idProductoPendienteEliminar = null;
        }
    });

    const eliminarProducto = async id => {
        try {
            const res = await fetch(`/productos/eliminar/${id}`, { method: "DELETE" });
            if (!res.ok) throw new Error("No se pudo eliminar el producto");
            $("productosTableBody").querySelector(`tr[data-id="${id}"]`)?.remove();
            mostrarAlerta("alertaProductos", "✅ Producto eliminado correctamente");
        } catch (err) {
            mostrarAlerta("alertaProductos", "❌ Error al eliminar producto", "danger");
        }
    };

    // Inicializar
    cargarProductos();
    cargarCategorias();
});
