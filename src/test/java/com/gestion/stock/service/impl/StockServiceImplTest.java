package com.gestion.stock.service.impl;


import com.gestion.stock.entity.Stock;
import com.gestion.stock.mapper.*;
import com.gestion.stock.repository.MouvementStockRepository;
import com.gestion.stock.repository.ProduitRepository;
import com.gestion.stock.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StockServiceImplTest {

    @Mock
    private  StockRepository stockRepository;
    @Mock
    private  MouvementStockRepository mouvementStockRepository;
    @Mock
    private  ProduitRepository produitRepository;
    @Mock
    private  DetailsCommandetoStockMapper detailsToStockMapper;
    @Mock
    private  StockToMouvementMapper stockToMouvementMapper;
    @Mock
    private  MouvementStockMapper mouvementStockMapper;
    @Mock
    private  ProduitMapper produitMapper;
    @Mock
    private  StockMapper stockMapper;
    @InjectMocks
    private StockServiceImpl stockService;


    @Test
    void valorisationStock_WithManyStocks(){

        Stock stock1 = new Stock();
        stock1.setPrixAchat(50D);
        stock1.setQuantiteActuel(20);

        Stock stock2 = new Stock();
        stock2.setPrixAchat(20D);
        stock2.setQuantiteActuel(20);

        Stock stock3 = new Stock();
        stock3.setPrixAchat(100D);
        stock3.setQuantiteActuel(5);


        when(stockRepository.findAll()).thenReturn(Arrays.asList(stock1,stock2,stock3));

        double result = stockService.valorisationStock();



        assertThat(result).isEqualTo(1900D);

    }
}
