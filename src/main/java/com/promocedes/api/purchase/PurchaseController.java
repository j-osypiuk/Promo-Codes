package com.promocedes.api.purchase;

import com.promocedes.api.purchase.dto.CurrencySalesReport;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchases")
@AllArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping()
    public ResponseEntity<Void> addPurchase(@RequestParam("productId") UUID productId,
                                            @RequestParam(value = "code", required = false) String code) {
        purchaseService.addPurchase(productId, code);

        return new ResponseEntity<>(
                HttpStatus.CREATED
        );
    }

    @GetMapping("/report")
    public ResponseEntity<List<CurrencySalesReport>> getPurchaseReport() {
        return new ResponseEntity<>(
            purchaseService.getPurchaseReport(),
            HttpStatus.OK
        );
    }
}
