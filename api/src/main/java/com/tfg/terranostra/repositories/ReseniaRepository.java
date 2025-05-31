package com.tfg.terranostra.repositories;

import com.tfg.terranostra.models.ProductoModel;
import com.tfg.terranostra.models.ReseniaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReseniaRepository extends JpaRepository<ReseniaModel, Long> {

    List<ReseniaModel> findByProductoOrderByFechaDesc(ProductoModel producto);
    void deleteByUsuarioId(Long usuarioId);
}
