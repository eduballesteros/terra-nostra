package com.tfg.terranostra.repositories;

import com.tfg.terranostra.models.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

/**
 * Repositorio para la entidad {@link UsuarioModel}.
 * Proporciona métodos para realizar operaciones CRUD en la base de datos.
 * Extiende {@link JpaRepository}, lo que permite el uso de métodos predefinidos como:
 *     Guardar un usuario ({@code save})
 *     Buscar un usuario por su ID ({@code findById})
 *     Obtener todos los usuarios ({@code findAll})
 *     Eliminar un usuario ({@code deleteById})
 * Además, contiene un método adicional para buscar usuarios por su correo electrónico.
 *
 * @author ebp
 * @version 1.0
 */

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    Optional<UsuarioModel> findByEmail(String email);

    long countByRol(String rol);
}
