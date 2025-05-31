    <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page session="true" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <!DOCTYPE html>
    <html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Panel de Administración - Terra Nostra</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <!-- Bootstrap -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- CSS personalizado -->
        <link rel="stylesheet" href="css/admin.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="icon" href="images/favicon.ico" type="image/x-icon">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/carrito.css">

    </head>

    <body>

    <jsp:include page="/includes/header.jsp" />

    <div class="container-fluid mt-4">
        <div class="row">
            <!-- Menú lateral -->
            <aside class="col-md-3 border-end">
                <h5 class="fw-bold mb-4">Administración</h5>
                <ul class="nav flex-column">
                    <li class="nav-item mb-2">
                        <a id="linkUsuarios" class="nav-link active fw-bold text-primary" href="#">Usuarios</a>
                    </li>
                    <li class="nav-item mb-2">
                        <a id="linkProductos" class="nav-link" href="#">Productos</a>
                    </li>
                </ul>
            </aside>

            <!-- Sección de Usuarios -->
            <section id="seccionUsuarios" class="col-md-9">
                <h2 class="fw-bold mb-4">Gestión de Usuarios</h2>
                <div id="alertaUsuarios" class="alert d-none" role="alert"></div>
                <div class="table-responsive">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Nombre</th>
                                <th>Apellido</th>
                                <th>Email</th>
                                <th>Teléfono</th>
                                <th>Dirección</th>
                                <th>Rol</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="usuariosTableBody"></tbody>
                    </table>
                </div>
            </section>

            <!-- Sección de Productos -->
            <section id="seccionProductos" class="col-md-9 d-none">
                <h2 class="fw-bold mb-4">Gestión de Productos</h2>

                <!-- Formulario -->
                <div class="card mb-4">
                    <div class="card-header bg-success text-white">Añadir Nuevo Producto</div>
                    <div class="card-body">
                        <form id="formProducto" enctype="multipart/form-data">
                            <div class="mb-3"><label>Nombre:</label><input type="text" name="nombre" class="form-control" required></div>
                            <div class="mb-3"><label>Descripción:</label><textarea name="descripcion" class="form-control" required></textarea></div>
                            <div class="mb-3"><label>Descripción breve:</label><input type="text" name="descripcionBreve" class="form-control" required></div>
                            <div class="mb-3"><label>Precio:</label><input type="number" name="precio" class="form-control" step="0.01" required></div>
                            <div class="mb-3"><label>Descuento (%):</label><input type="number" name="descuento" class="form-control" step="0.01" min="0" max="100"></div>
                            <div class="mb-3"><label>Stock:</label><input type="number" name="stock" class="form-control" required></div>
                             <select id="categoria" name="categoria" class="form-control" required></select>
                            <div class="mb-3"><label>Imagen:</label><input type="file" name="imagen" class="form-control"></div>
                            <button type="submit" class="btn btn-success">Guardar Producto</button>
                        </form>
                    </div>
                </div>

                <!-- Tabla -->
                <div class="card">
                    <div class="card-header bg-success text-white">Lista de Productos</div>
                    <div class="card-body">
                        <div id="alertaProductos" class="alert d-none" role="alert"></div>
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover">
                                <thead class="table-success">
                                    <tr>
                                        <th>Nombre</th>
                                        <th>Descripción</th>
                                        <th>Descripción breve</th>
                                        <th>Precio</th>
                                        <th>Descuento</th>
                                        <th>Stock</th>
                                        <th>Categoría</th>
                                        <th>Imagen</th>
                                        <th>Editar</th>
                                        <th>Eliminar</th>
                                    </tr>
                                </thead>
                                <tbody id="productosTableBody"></tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    </div>

    <!-- Modal Editar Usuario (Panel Admin) -->
    <div class="modal fade" id="editarUsuarioModal" tabindex="-1" aria-labelledby="editarUsuarioModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editarUsuarioModalLabel">Editar Usuario (Admin)</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                </div>
                <div class="modal-body">
                    <!-- Alerta visual -->
                    <div id="alertaModalUsuario" class="alert d-none" role="alert"></div>

                    <form id="editarUsuarioForm">
                        <div class="mb-3">
                            <label for="editNombre" class="form-label">Nombre</label>
                            <input type="text" class="form-control" id="editNombre" required>
                        </div>
                        <div class="mb-3">
                            <label for="editApellido" class="form-label">Apellido</label>
                            <input type="text" class="form-control" id="editApellido" required>
                        </div>
                        <div class="mb-3">
                            <label for="editTelefono" class="form-label">Teléfono</label>
                            <input type="text" class="form-control" id="editTelefono">
                        </div>
                        <div class="mb-3">
                            <label for="editDireccion" class="form-label">Dirección</label>
                            <input type="text" class="form-control" id="editDireccion">
                        </div>
                        <div class="mb-3">
                            <label for="editRol" class="form-label">Rol</label>
                            <select id="editRol" class="form-select">
                                <option value="ROLE_USER">Usuario</option>
                                <option value="ROLE_ADMIN">Administrador</option>
                            </select>
                        </div>

                        <input type="hidden" id="editEmail">
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-primary" form="editarUsuarioForm">Guardar cambios</button>
                </div>
            </div>
        </div>
    </div>


    <!-- Modal Editar Producto -->
    <div class="modal fade" id="editarProductoModal" tabindex="-1" aria-labelledby="editarProductoModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <form class="modal-content" id="editarProductoForm">
                <div class="modal-header">
                    <h5 class="modal-title">Editar Producto</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" id="editProductoId" name="id">

                    <input type="text" id="editNombre" name="nombre" class="form-control mb-3" required>

                    <textarea id="editDescripcion" name="descripcion" class="form-control mb-3" required></textarea>

                    <input type="text" id="editDescripcionBreve" name="descripcionBreve" class="form-control mb-3" required>

                    <div class="input-group mb-3">
                        <input type="number" id="editPrecio" name="precio" class="form-control" step="0.01" required>
                        <span class="input-group-text">€</span>
                    </div>

                    <div class="input-group mb-3">
                        <input type="number" id="editDescuento" name="descuento" class="form-control" step="0.01" min="0" max="100" required>
                        <span class="input-group-text">%</span>
                    </div>

                    <input type="number" id="editStock" name="stock" class="form-control mb-3" required>

                    <select id="editCategoria" name="categoria" class="form-control" required>
                        <option value="" disabled selected>Selecciona una categoría</option>
                     </select>

                    <input type="file" id="editImagen" name="imagen" class="form-control mb-3">
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-success">Guardar Cambios</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Modal Eliminar Producto -->
    <div class="modal fade" id="modalConfirmarEliminarProducto" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 shadow">
                <div class="modal-header bg-danger text-white">
                    <h5 class="modal-title">Confirmar eliminación</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p>¿Estás seguro de que deseas eliminar el producto <strong id="nombreProductoAEliminar"></strong>?</p>
                </div>
                <div class="modal-footer justify-content-end">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="button" id="btnConfirmarEliminarProducto" class="btn btn-danger">Eliminar</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal Confirmar Eliminación de Usuario -->
    <div class="modal fade" id="modalConfirmarEliminarUsuario" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title">Confirmar eliminación</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <p>¿Estás seguro de que deseas eliminar al usuario <strong id="nombreUsuarioAEliminar"></strong>?</p>
            <input type="text" id="inputConfirmarEmail" class="form-control mt-3" placeholder="Confirma el correo del usuario">
          </div>
          <div class="modal-footer justify-content-end">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
            <button type="button" class="btn btn-danger" id="btnConfirmarEliminarUsuario">Eliminar</button>
          </div>
        </div>
      </div>
    </div>


    <footer style="text-align: center; padding: 1rem 0; font-size: 0.9rem; color: #666; background-color: #f8f8f8;">
        &copy; Terra Nostra 2025
    </footer>


    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="js/adminProductos.js"></script>
    <script src="js/adminUsuario.js"></script>
    <script src="js/adminPanel.js"></script>
    <script src="${pageContext.request.contextPath}/js/carrito.js"></script>


    </body>
    </html>
