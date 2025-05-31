package com.tfg.terranostra.repositories;

import com.tfg.terranostra.models.ProductoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

/**
 * Repositorio para la entidad {@link ProductoModel}.
 * Proporciona métodos para realizar operaciones CRUD en la base de datos.
 * Extiende {@link JpaRepository}, lo que permite el uso de métodos predefinidos como:
 *
 *     Guardar un producto ({@code save})
 *     Buscar un producto por su ID ({@code findById})
 *     Obtener todos los productos ({@code findAll})
 *     Eliminar un producto ({@code deleteById})<
 *
 * @author ebp
 * @version 1.0
 */


public interface ProductoRepository extends JpaRepository <ProductoModel, Long> {
}
