package com.gestion.stock.repository;


import com.gestion.stock.entity.MouvementStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock,Long> {
}
