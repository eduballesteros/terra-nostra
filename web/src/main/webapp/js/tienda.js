document.addEventListener("DOMContentLoaded", function () {
    const contenedorProductos = document.getElementById("contenedorProductos");
    const inputBuscador = document.getElementById("buscadorProductos");
    const checkboxDisponibles = document.getElementById("filtroStock");
    const enlacesCategorias = document.querySelectorAll("aside ul li a");
    const loader = document.getElementById("loaderProductos");

    if (!contenedorProductos) {
        console.error("⚠️ Error: No se encontró el contenedor con id 'contenedorProductos'");
        return;
    }

    let categoriaSeleccionada = null;

    function mostrarLoader(visible) {
        if (loader) loader.style.display = visible ? "block" : "none";
        contenedorProductos.classList.toggle("d-none", visible);
    }

    function cargarTodosLosProductos(filtros = {}) {
        mostrarLoader(true);
        let url = "/productos/listar";
        const query = new URLSearchParams(filtros).toString();
        if (query) url += `?${query}`;

        fetch(url)
            .then(response => {
                if (!response.ok) throw new Error(`Error al obtener productos: ${response.statusText}`);
                return response.json();
            })
            .then(productos => mostrarProductos(productos))
            .catch(error => console.error("Error al cargar productos:", error))
            .finally(() => mostrarLoader(false));
    }

    function generarSlug(nombre) {
        return nombre
            .toLowerCase()
            .normalize("NFD")                     // descompone tildes
            .replace(/[\u0300-\u036f]/g, "")     // elimina tildes
            .replace(/\s+/g, "-")                // espacios por guiones
            .replace(/[^a-z0-9\-]/g, "");        // elimina otros símbolos
    }

    function aplicarFiltros() {
        const filtros = {
            texto: inputBuscador.value.trim(),
            disponibles: checkboxDisponibles.checked,
            categoria: categoriaSeleccionada
        };

        if (!filtros.categoria) {
            delete filtros.categoria;
        }

        cargarTodosLosProductos(filtros);
    }

    inputBuscador.addEventListener("input", aplicarFiltros);
    checkboxDisponibles.addEventListener("change", aplicarFiltros);

    enlacesCategorias.forEach(enlace => {
        enlace.addEventListener("click", e => {
            e.preventDefault();
            categoriaSeleccionada = enlace.dataset.categoria || null;

            enlacesCategorias.forEach(el => el.classList.remove("active"));
            enlace.classList.add("active");

            aplicarFiltros();
        });
    });

    function mostrarProductos(productos) {
        contenedorProductos.innerHTML = "";

        if (!productos || productos.length === 0) {
            contenedorProductos.innerHTML = '<p class="text-muted">No se encontraron productos.</p>';
            return;
        }

        productos.forEach(producto => {
            const agotado = producto.stock <= 0;

            const productoHTML = `
                <div class="col">
                    <div class="producto-card d-flex flex-column h-100 ${agotado ? 'producto-agotado' : ''}">
                        <div class="imagen-wrapper ${agotado ? 'filtro-gris' : ''}">
                            ${producto.imagen
                                ? `<img src="data:image/png;base64,${producto.imagen}" class="producto-img" alt="${producto.nombre}">`
                                : '<img src="images/default.png" class="producto-img" alt="Imagen no disponible">'}
                        </div>

                        ${agotado ? '<span class="etiqueta-agotado">Agotado</span>' : ''}

                        <div class="producto-titulo">${producto.nombre}</div>
                        <div class="producto-descripcion">${producto.descripcion_breve || ''}</div>
                        <div class="producto-precio">
                            ${
                                producto.descuento && producto.descuento > 0
                                    ? `
                                        <span class="text-decoration-line-through text-muted">${producto.precio.toFixed(2)} €</span>
                                        <span class="fw-bold text-danger ms-2">
                                            ${(producto.precio * (1 - producto.descuento / 100)).toFixed(2)} €
                                        </span>
                                        <span class="badge bg-success ms-2">${producto.descuento}% dto</span>
                                      `
                                    : `${producto.precio ? producto.precio.toFixed(2) : "N/A"} €`
                            }
                        </div>

                        <div class="mt-auto d-flex justify-content-between align-items-center gap-2">
                            <a href="/producto/${generarSlug(producto.nombre)}"
                               class="btn ${agotado ? 'btn-outline-secondary' : 'btn-outline-primary'} w-100">
                                Ver más
                            </a>
                        </div>
                    </div>
                </div>
            `;

            contenedorProductos.innerHTML += productoHTML;
        });
    }

    cargarTodosLosProductos();
});
