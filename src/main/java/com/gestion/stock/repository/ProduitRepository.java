package com.gestion.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gestion.stock.entity.Produit;
@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

}
