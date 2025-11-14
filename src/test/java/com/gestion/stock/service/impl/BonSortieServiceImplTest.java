package com.gestion.stock.service.impl;

import com.gestion.stock.entity.*;
import com.gestion.stock.mapper.StockToMouvementMapper;
import com.gestion.stock.repository.BonSortieRepository;
import com.gestion.stock.repository.MouvementStockRepository;
import com.gestion.stock.repository.ProduitRepository;
import com.gestion.stock.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

import com.gestion.stock.entity.StatutBonSortie;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BonSortieServiceImplTest {

    @Mock
    private BonSortieRepository bonSortieRepository;
    @Mock
    private ProduitRepository produitRepository;
    @Mock
    private MouvementStockRepository mouvementStockRepository;
    @Mock
    private StockRepository stockRepository;
    @Mock
    private StockToMouvementMapper stockToMouvementMapper;

    @InjectMocks
    private BonSortieServiceImpl bonSortieService;

    private Long bonSortieId;
    private Produit produit;
    private MouvementStock mockMouvement;

    @BeforeEach
    void setUp() {
        bonSortieId = 1L;
        
        produit = new Produit();
        produit.setId(1L);
        produit.setStockActuel(40);

        mockMouvement = new MouvementStock();
        
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(stockToMouvementMapper.toMouvementSortie(any())).thenReturn(mockMouvement);
    }

    @Test
    void updateBonSortieToValide_WithOneProduit(){
        produit.setStockActuel(20);
        
        BonSortieItem item = new BonSortieItem();
        item.setQuantite(10);
        item.setProduit(produit);

        BonSortie bonSortie = new BonSortie();
        bonSortie.setId(bonSortieId);
        bonSortie.setStatut(StatutBonSortie.BROUILLON);
        bonSortie.setItems(List.of(item));

        Stock stock = new Stock();
        stock.setQuantiteActuel(20);
        stock.setProduit(produit);
        stock.setDateEntre(LocalDateTime.now());

        when(bonSortieRepository.findById(bonSortieId)).thenReturn(Optional.of(bonSortie));
        when(stockRepository.findAll()).thenReturn(List.of(stock));

        Map<String , Object> result = bonSortieService.updateBonSortieToValider(bonSortieId);

        assertThat(result.get("status")).isEqualTo("VALIDE");
        assertThat(bonSortie.getStatut()).isEqualTo(StatutBonSortie.VALIDE);
        assertThat(stock.getQuantiteActuel()).isEqualTo(10);
        verify(bonSortieRepository).save(bonSortie);
        verify(stockRepository).save(stock);
        verify(mouvementStockRepository).save(any());
    }

    @Test
    void updateBonSortieToValide_WithManyProduit() {
        Stock stock1 = new Stock();
        stock1.setNumeroLot("Lot-001");
        stock1.setQuantiteActuel(20);
        stock1.setPrixAchat(20D);
        stock1.setProduit(produit);
        stock1.setDateEntre(LocalDateTime.now().minusHours(2));

        Stock stock2 = new Stock();
        stock2.setNumeroLot("Lot-002");
        stock2.setQuantiteActuel(20);
        stock2.setPrixAchat(25D);
        stock2.setProduit(produit);
        stock2.setDateEntre(LocalDateTime.now());

        BonSortieItem item = new BonSortieItem();
        item.setQuantite(30);
        item.setProduit(produit);

        BonSortie bonSortie = new BonSortie();
        bonSortie.setId(bonSortieId);
        bonSortie.setStatut(StatutBonSortie.BROUILLON);
        bonSortie.setItems(List.of(item));

        when(bonSortieRepository.findById(bonSortieId)).thenReturn(Optional.of(bonSortie));
        when(stockRepository.findAll()).thenReturn(Arrays.asList(stock1, stock2));

        Map<String, Object> result = bonSortieService.updateBonSortieToValider(bonSortieId);

        assertThat(result.get("status")).isEqualTo("VALIDE");
        assertThat(bonSortie.getStatut()).isEqualTo(StatutBonSortie.VALIDE);
        assertThat(stock1.getQuantiteActuel()).isEqualTo(0);
        assertThat(stock2.getQuantiteActuel()).isEqualTo(10);
        assertThat(produit.getStockActuel()).isEqualTo(10);
        verify(bonSortieRepository).save(bonSortie);
        verify(stockRepository,times(2)).save(any(Stock.class));
        verify(mouvementStockRepository, times(2)).save(any(MouvementStock.class));
    }

    @Test
    void updateBonSortieToValide_WithInsufficientStock_ThrowsException(){
        Stock stock1 = new Stock();
        stock1.setNumeroLot("Lot-001");
        stock1.setQuantiteActuel(20);
        stock1.setPrixAchat(20D);
        stock1.setProduit(produit);
        stock1.setDateEntre(LocalDateTime.now().minusHours(2));

        Stock stock2 = new Stock();
        stock2.setNumeroLot("Lot-002");
        stock2.setQuantiteActuel(20);
        stock2.setPrixAchat(25D);
        stock2.setProduit(produit);
        stock2.setDateEntre(LocalDateTime.now());

        BonSortieItem item = new BonSortieItem();
        item.setQuantite(60);
        item.setProduit(produit);

        BonSortie bonSortie = new BonSortie();
        bonSortie.setId(bonSortieId);
        bonSortie.setStatut(StatutBonSortie.BROUILLON);
        bonSortie.setItems(List.of(item));

        when(bonSortieRepository.findById(bonSortieId)).thenReturn(Optional.of(bonSortie));
        when(stockRepository.findAll()).thenReturn(Arrays.asList(stock1, stock2));

        assertThatThrownBy(() -> bonSortieService.updateBonSortieToValider(bonSortieId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient stock for product: 1");
    }

    @Test
    void updateBonSortieToValide_WithSortieQuantiteEqualsStockQuantite(){
        Stock stock1 = new Stock();
        stock1.setNumeroLot("Lot-001");
        stock1.setQuantiteActuel(20);
        stock1.setPrixAchat(20D);
        stock1.setProduit(produit);
        stock1.setDateEntre(LocalDateTime.now().minusHours(2));

        Stock stock2 = new Stock();
        stock2.setNumeroLot("Lot-002");
        stock2.setQuantiteActuel(20);
        stock2.setPrixAchat(25D);
        stock2.setProduit(produit);
        stock2.setDateEntre(LocalDateTime.now());

        BonSortieItem item = new BonSortieItem();
        item.setQuantite(40);
        item.setProduit(produit);

        BonSortie bonSortie = new BonSortie();
        bonSortie.setId(bonSortieId);
        bonSortie.setStatut(StatutBonSortie.BROUILLON);
        bonSortie.setItems(List.of(item));

        when(bonSortieRepository.findById(bonSortieId)).thenReturn(Optional.of(bonSortie));
        when(stockRepository.findAll()).thenReturn(Arrays.asList(stock1, stock2));

        Map<String, Object> result = bonSortieService.updateBonSortieToValider(bonSortieId);

        assertThat(result.get("status")).isEqualTo("VALIDE");
        assertThat(bonSortie.getStatut()).isEqualTo(StatutBonSortie.VALIDE);
        assertThat(stock1.getQuantiteActuel()).isEqualTo(0);
        assertThat(stock2.getQuantiteActuel()).isEqualTo(0);
        assertThat(produit.getStockActuel()).isEqualTo(0);
        verify(bonSortieRepository).save(bonSortie);
        verify(stockRepository,times(2)).save(any(Stock.class));
        verify(mouvementStockRepository, times(2)).save(any(MouvementStock.class));
    }

    @Test
    void updateBonSortieToValider_ValidationWorkflow_ShouldTriggerAllAutomaticActions() {
        LocalDateTime beforeValidation = LocalDateTime.now().minusMinutes(1);
        produit.setStockActuel(50);

        BonSortieItem item = new BonSortieItem();
        item.setQuantite(15);
        item.setProduit(produit);

        BonSortie bonSortie = new BonSortie();
        bonSortie.setId(bonSortieId);
        bonSortie.setStatut(StatutBonSortie.BROUILLON);
        bonSortie.setItems(List.of(item));
        bonSortie.setDateSortie(beforeValidation);

        Stock stock = new Stock();
        stock.setQuantiteActuel(25);
        stock.setProduit(produit);
        stock.setDateEntre(LocalDateTime.now().minusDays(1));

        when(bonSortieRepository.findById(bonSortieId)).thenReturn(Optional.of(bonSortie));
        when(stockRepository.findAll()).thenReturn(List.of(stock));

        Map<String, Object> result = bonSortieService.updateBonSortieToValider(bonSortieId);

        assertThat(bonSortie.getStatut()).isEqualTo(StatutBonSortie.VALIDE);
        assertThat(result.get("status")).isEqualTo("VALIDE");
        assertThat(stock.getQuantiteActuel()).isEqualTo(10);
        assertThat(produit.getStockActuel()).isEqualTo(35);
        
        verify(mouvementStockRepository).save(any(MouvementStock.class));
        verify(stockToMouvementMapper).toMouvementSortie(stock);
        verify(stockRepository).save(stock);
        verify(produitRepository).save(produit);
        verify(bonSortieRepository).save(bonSortie);
    }
}