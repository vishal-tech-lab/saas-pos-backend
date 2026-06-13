package com.example.Backend.Controller;

import com.example.Backend.Dto.KitchenProductionDto;
import com.example.Backend.Entity.KitchenProduction;
import com.example.Backend.Service.KitchenProductionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kitchenproduction")
@AllArgsConstructor
public class KitchenProductionController {

    private final KitchenProductionService kitchenProductionService;

   @PostMapping("/register")
public ResponseEntity<KitchenProduction> registerKitchenProduction(
        @RequestBody KitchenProductionDto productionDto
) {

    System.out.println(
        "RECEIVED QTY = " +
        productionDto.getQty()
    );

    KitchenProduction createdProduction =
            kitchenProductionService.registerKitchenProduction(
                    productionDto
            );

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdProduction);
}

    @GetMapping("/all")
    public ResponseEntity<List<KitchenProduction>> getAllKitchenProductions() {
        return ResponseEntity.ok(kitchenProductionService.getAllKitchenProductions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<KitchenProduction> getKitchenProductionById(@PathVariable Long id) {
        return kitchenProductionService.getKitchenProductionById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<KitchenProduction> updateKitchenProduction(
            @PathVariable Long id,
            @RequestBody KitchenProductionDto productionDto
    ) {
        KitchenProduction updatedProduction = kitchenProductionService.updateKitchenProduction(id, productionDto);
        return ResponseEntity.ok(updatedProduction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKitchenProduction(@PathVariable Long id) {
        if (kitchenProductionService.deleteKitchenProduction(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
