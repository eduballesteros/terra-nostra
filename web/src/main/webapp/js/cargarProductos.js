document.addEventListener("DOMContentLoaded", function () {
    const mejoresProductosContainer = document.getElementById("mejoresProductos");

    if (!mejoresProductosContainer) {
        console.error("⚠️ Error: No se encontró el contenedor con id 'mejoresProductos'");
        return;
    }

    function cargarProductos() {
        fetch("/productos/listar")
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Error al obtener productos: ${response.statusText}`);
                }
                return response.json();
            })
            .then(productos => {
                mostrarMejoresProductos(productos.slice(0, 3));
            })
            .catch(error => console.error("❌ Error al cargar productos:", error));
    }

    function mostrarMejoresProductos(productos) {
        mejoresProductosContainer.innerHTML = "";

        productos.forEach(producto => {
            const precioOriginal = producto.precio?.toFixed(2) ?? "N/A";
            const tieneDescuento = producto.descuento && producto.descuento > 0;
            const precioFinal = tieneDescuento
                ? (producto.precio - (producto.precio * producto.descuento / 100)).toFixed(2)
                : precioOriginal;
            const agotado = producto.stock === 0;

            const productoHTML = `
                <div class="col-md-4 d-flex">
                    <div class="card shadow-sm h-100 w-100 d-flex flex-column">
                        ${producto.imagen
                            ? `<img src="data:image/png;base64,${producto.imagen}" class="card-img-top" alt="${producto.nombre}">`
                            : '<img src="images/default.png" class="card-img-top" alt="Imagen no disponible">'}
                        <div class="card-body d-flex flex-column">
                            <div>
                                <h5 class="card-title">${producto.nombre}</h5>
                                <p class="card-text">${producto.descripcion_breve}</p>
                                <p class="text-muted">
                                    Precio:
                                    ${tieneDescuento
                                        ? `<span class="text-decoration-line-through">€${precioOriginal}</span>
                                           <strong class="text-danger">€${precioFinal}</strong>
                                           <span class="badge badge-terranostra">${producto.descuento}% dto</span>`
                                        : `<strong>€${precioOriginal}</strong>`}
                                </p>
                            </div>
                            <a href="/producto/${generarSlug(producto.nombre)}"
                               class="btn ${agotado ? 'btn-outline-secondary disabled' : 'btn-outline-primary'} w-100">
                               ${agotado ? 'Agotado' : 'Ver más'}
                            </a>
                        </div>
                    </div>
                </div>
            `;

            mejoresProductosContainer.innerHTML += productoHTML;
        });
    }

    function generarSlug(nombre) {
        return nombre.toLowerCase()
            .normalize("NFD").replace(/[\u0300-\u036f]/g, "")
            .replace(/[^a-z0-9]+/g, "-")
            .replace(/^-+|-+$/g, "");
    }

    cargarProductos();
});
