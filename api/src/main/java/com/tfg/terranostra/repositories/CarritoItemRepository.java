package com.tfg.terranostra.repositories;

import com.tfg.terranostra.models.CarritoItemModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoItemRepository extends JpaRepository<CarritoItemModel, Long> {
}