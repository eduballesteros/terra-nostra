package com.tfg.terranostra.repositories;

import com.tfg.terranostra.models.PedidoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoModel, Long> {

    List<PedidoModel> findByUsuarioId(Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);
}
