package com.gestion.stock.controller;
import com.gestion.stock.dto.request.BonSortieRequestDto;
import com.gestion.stock.dto.request.BonSortieUpdateRequestDTO;
import com.gestion.stock.dto.response.BonSortieResponseDTO;
import com.gestion.stock.dto.response.CommandeResponseDTO;
import com.gestion.stock.service.BonSortieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/bonsSortie")
@RequiredArgsConstructor
public class BonSortieController {

    private final BonSortieService bonSortieService;

    @PostMapping
    public ResponseEntity<BonSortieResponseDTO> createBonSortie(@Valid @RequestBody BonSortieRequestDto bonSortieRequestDto){
        return ResponseEntity.ok(bonSortieService.createBonSortie(bonSortieRequestDto));
    }
    @GetMapping
    public ResponseEntity<List<BonSortieResponseDTO>> getAllBonsSortie() {
        return ResponseEntity.ok(bonSortieService.getAllBonsSortie());
    }
    @GetMapping("/{id}")
    public ResponseEntity<BonSortieResponseDTO> getBonSortieById(@PathVariable Long id) {
        return ResponseEntity.ok(bonSortieService.getBonSortieById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<BonSortieResponseDTO> updateBonSortie(@PathVariable Long id, @Valid @RequestBody BonSortieUpdateRequestDTO bonSortieUpdateRequestDto) {

        return ResponseEntity.ok(bonSortieService.updateBonSortie(id, bonSortieUpdateRequestDto));
    }
    @PutMapping("/{id}/annuler")
    public ResponseEntity<BonSortieResponseDTO> updateBonSortieToAnnuler(@PathVariable Long id){
        return ResponseEntity.ok(bonSortieService.updateBonSortieToAnnuler(id));
    }

    @PutMapping("/{id}/valider")
    public ResponseEntity<Map<String , Object>> updateBonSortieToValider(@PathVariable Long id){
        return ResponseEntity.ok(bonSortieService.updateBonSortieToValider(id));
    }

}
