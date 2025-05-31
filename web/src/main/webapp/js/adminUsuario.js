document.addEventListener("DOMContentLoaded", () => {
    let emailUsuarioPendienteEliminar = null;

    const mostrarAlerta = (id, mensaje, tipo = "success") => {
        const alerta = document.getElementById(id);
        if (!alerta) return;

        alerta.className = `alert alert-${tipo}`;
        alerta.innerText = mensaje;

        setTimeout(() => alerta.classList.add("d-none"), 4000);
    };

    const cargarUsuarios = () => {
        fetch("/usuario/listar")
            .then(res => res.ok ? res.json() : Promise.reject("Error al obtener usuarios"))
            .then(usuarios => {
                const tbody = document.getElementById("usuariosTableBody");
                tbody.innerHTML = "";

                usuarios.forEach(({ nombre, apellido, email, telefono, direccion, rol }) => {
                    const fila = document.createElement("tr");
                    fila.innerHTML = `
                        <td>${nombre}</td>
                        <td>${apellido}</td>
                        <td>${email}</td>
                        <td>${telefono || "No disponible"}</td>
                        <td>${direccion || "No especificada"}</td>
                        <td>${rol}</td>
                        <td>
                            <button class="btn btn-primary btn-sm editar-usuario"
                                data-email="${email}"
                                data-nombre="${nombre}"
                                data-apellido="${apellido}"
                                data-telefono="${telefono || ""}"
                                data-direccion="${direccion || ""}"
                                data-rol="${rol}">
                                Editar
                            </button>
                            <button class="btn btn-danger btn-sm eliminar-usuario"
                                data-email="${email}"
                                data-nombre="${nombre}">
                                Eliminar
                            </button>
                        </td>
                    `;
                    tbody.appendChild(fila);
                });

                agregarEventosUsuarios();
            })
            .catch(() => {
                mostrarAlerta("alertaUsuarios", "❌ Error al cargar usuarios.", "danger");
            });
    };

    const agregarEventosUsuarios = () => {
        document.querySelectorAll(".eliminar-usuario").forEach(btn => {
            btn.addEventListener("click", () => {
                emailUsuarioPendienteEliminar = btn.dataset.email;
                document.getElementById("nombreUsuarioAEliminar").textContent = btn.dataset.nombre;
                document.getElementById("inputConfirmarEmail").value = "";
                new bootstrap.Modal(document.getElementById("modalConfirmarEliminarUsuario")).show();
            });
        });

        document.querySelectorAll(".editar-usuario").forEach(btn => {
            btn.addEventListener("click", () => {
                document.getElementById("editEmail").value = btn.dataset.email || "";
                document.getElementById("editNombre").value = btn.dataset.nombre || "";
                document.getElementById("editApellido").value = btn.dataset.apellido || "";
                document.getElementById("editTelefono").value = btn.dataset.telefono || "";
                document.getElementById("editDireccion").value = btn.dataset.direccion || "";
                document.getElementById("editRol").value = btn.dataset.rol || "";

                new bootstrap.Modal(document.getElementById("editarUsuarioModal")).show();
            });
        });
    };

    document.getElementById("btnConfirmarEliminarUsuario")?.addEventListener("click", () => {
        const inputEmail = document.getElementById("inputConfirmarEmail").value.trim();
        if (inputEmail !== emailUsuarioPendienteEliminar) {
            mostrarAlerta("alertaUsuarios", "❌ El correo no coincide. Eliminación cancelada.", "danger");
            return;
        }

        fetch(`/admin/eliminarUsuario?email=${emailUsuarioPendienteEliminar}`, {
            method: "DELETE",
            headers: { "Accept": "application/json" }
        })
            .then(res => res.ok ? res.json() : Promise.reject(res))
            .then(data => {
                mostrarAlerta("alertaUsuarios", data.mensaje || "✅ Usuario eliminado correctamente.", "success");
                cargarUsuarios();
                bootstrap.Modal.getInstance(document.getElementById("modalConfirmarEliminarUsuario")).hide();
            })
            .catch(async (error) => {
                if (error instanceof Response && error.status === 403) {
                    const data = await error.json();
                    mostrarAlerta("alertaUsuarios", data.mensaje || "⛔ No se puede eliminar al último administrador.", "danger");
                } else {
                    mostrarAlerta("alertaUsuarios", "❌ Error al eliminar usuario.", "danger");
                }
            });
    });

    document.getElementById("editarUsuarioForm")?.addEventListener("submit", e => {
        e.preventDefault();

        const email = document.getElementById("editEmail").value.trim();
        const datosUsuario = {
            nombre: document.getElementById("editNombre").value.trim(),
            apellido: document.getElementById("editApellido").value.trim(),
            telefono: document.getElementById("editTelefono").value.trim(),
            direccion: document.getElementById("editDireccion").value.trim(),
            rol: document.getElementById("editRol").value
        };

        if (!datosUsuario.nombre || !datosUsuario.apellido || !datosUsuario.rol) {
            mostrarAlerta("alertaUsuarios", "❌ Nombre, apellido y rol son obligatorios.", "danger");
            return;
        }

        fetch(`/admin/editarUsuario?email=${email}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(datosUsuario)
        })
            .then(res => res.ok ? res.json() : Promise.reject("Error al actualizar usuario"))
            .then(data => {
                mostrarAlerta("alertaUsuarios", data.mensaje || "✅ Usuario actualizado correctamente.", "success");
                cargarUsuarios();
                bootstrap.Modal.getInstance(document.getElementById("editarUsuarioModal")).hide();
            })
            .catch(() => {
                mostrarAlerta("alertaUsuarios", "❌ Error al actualizar usuario.", "danger");
            });
    });

    cargarUsuarios();
});
