package com.gestion.stock.service.impl;

import com.gestion.stock.dto.response.CommandeResponseDTO;
import com.gestion.stock.dto.response.StockResponseDTO;
import com.gestion.stock.entity.Commande;
import com.gestion.stock.entity.DetailsCommande;
import com.gestion.stock.entity.Produit;
import com.gestion.stock.enums.StatutCommande;
import com.gestion.stock.mapper.*;
import com.gestion.stock.repository.*;
import com.gestion.stock.service.StockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Map;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommandeServiceImplTest {


    @Mock
    private  CommandeRepository commandeRepository;

    @Mock
    private  StockService stockService;
    @Mock
    private  CommandeMapper mapper;


    @InjectMocks
    private CommandeServiceImpl commandeService;




    @Test
    void updateCommandeStatusToLivree_CreationOFStockAndMouvement(){
        Long commandeId = 1L;


        Produit produit = new Produit();
        produit.setId(1L);
        produit.setStockActuel(0);

        DetailsCommande detailsCommande1 = new DetailsCommande();
        detailsCommande1.setProduit(produit);
        detailsCommande1.setQuantite(50);
        detailsCommande1.setPrix(100D);

        DetailsCommande detailsCommande2 = new DetailsCommande();
        detailsCommande2.setProduit(produit);
        detailsCommande2.setQuantite(30);
        detailsCommande2.setPrix(60D);

        Commande commande = new Commande();
        commande.setId(commandeId);
        commande.setStatutCommande(StatutCommande.EN_ATTENTE);
        commande.setDetailsCommandes(Arrays.asList(detailsCommande1,detailsCommande2));

        StockResponseDTO stockResponseDTO1 = new StockResponseDTO();
        stockResponseDTO1.setNumeroLot("LOT-001");
        stockResponseDTO1.setQuantiteActuel(detailsCommande1.getQuantite());
        stockResponseDTO1.setPrixAchat(detailsCommande1.getPrix());
        stockResponseDTO1.setDateEntre(LocalDateTime.now());

        StockResponseDTO stockResponseDTO2 = new StockResponseDTO();
        stockResponseDTO2.setNumeroLot("LOT-002");
        stockResponseDTO2.setQuantiteActuel(detailsCommande2.getQuantite());
        stockResponseDTO2.setPrixAchat(detailsCommande2.getPrix());
        stockResponseDTO2.setDateEntre(LocalDateTime.now());

        List<StockResponseDTO> mockStockResponse =  Arrays.asList(stockResponseDTO1,stockResponseDTO2);
        CommandeResponseDTO mockCommandeResponse = new CommandeResponseDTO();


        when(commandeRepository.findById(commandeId)).thenReturn(Optional.of(commande));
        when(stockService.createStockLotsAndMouvement(commande.getDetailsCommandes())).thenReturn(mockStockResponse);
        when(commandeRepository.save(commande)).thenReturn(commande);
        when(mapper.toResponseDto(commande)).thenReturn(mockCommandeResponse);


        Map<String,Object> result = commandeService.changeStatusToLivree(commandeId);

        assertThat(result).containsKey("Stock list");
        assertThat(result).containsKey("Commande updated");
        assertThat(result.get("Stock list")).isEqualTo(mockStockResponse);
        assertThat(result.get("Commande updated")).isEqualTo(mockCommandeResponse);

        @SuppressWarnings("unchecked")
        List<StockResponseDTO> stockList = (List<StockResponseDTO>) result.get("Stock list");

        assertThat(stockList).hasSize(2);

        StockResponseDTO firstStock = stockList.get(0);
        assertThat(firstStock.getNumeroLot()).isEqualTo("LOT-001");
        assertThat(firstStock.getQuantiteActuel()).isEqualTo(50);
        assertThat(firstStock.getPrixAchat()).isEqualTo(100D);

        StockResponseDTO secondStock = stockList.get(1);
        assertThat(secondStock.getNumeroLot()).isEqualTo("LOT-002");
        assertThat(secondStock.getQuantiteActuel()).isEqualTo(30);
        assertThat(secondStock.getPrixAchat()).isEqualTo(60D);

        assertThat(commande.getStatutCommande()).isEqualTo(StatutCommande.LIVREE);

        verify(stockService).createStockLotsAndMouvement(commande.getDetailsCommandes());
        verify(commandeRepository).save(commande);



    }


}
