package com.tfg.terranostra.repositories;

import com.tfg.terranostra.models.CarritoModel;
import com.tfg.terranostra.models.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<CarritoModel, Long> {

    Optional<CarritoModel> findByUsuario(UsuarioModel usuario);

    void deleteByUsuarioId(Long usuarioId);
}
