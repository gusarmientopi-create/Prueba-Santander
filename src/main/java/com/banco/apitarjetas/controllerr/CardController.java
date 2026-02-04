package com.banco.apitarjetas.controllerr;

import com.banco.apitarjetas.entity.Card;
import com.banco.apitarjetas.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService service;

    public CardController(CardService service) {
        this.service = service;
    }

    // 1. Endpoint: Emitir Tarjeta
    @PostMapping
    public ResponseEntity<?> emitir(@RequestBody Map<String, String> request) {
        try {
      
            String productId = request.get("productid");
            String holderName = request.get("holderName");
            
            Card tarjeta = service.emitirTarjeta(productId, holderName);
            return new ResponseEntity<>(tarjeta, HttpStatus.CREATED); // 201
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400
        }
    }


    @PostMapping("/{cardId}/activate")
    public ResponseEntity<?> activar(@PathVariable String cardId) {
        try {
            Card tarjeta = service.activarTarjeta(cardId);
            return ResponseEntity.ok(tarjeta); // 200
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
        }
    }

    @PostMapping("/{cardId}/block")
    public ResponseEntity<?> bloquear(@PathVariable String cardId) {
        try {
            Card tarjeta = service.bloquearTarjeta(cardId);
            return ResponseEntity.ok(tarjeta); // 200
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
        }
    }
    
    @GetMapping("/{cardId}")
    public ResponseEntity<?> consultar(@PathVariable String cardId) {
        try {
            Card tarjeta = service.consultarTarjeta(cardId);
            return ResponseEntity.ok(tarjeta); // 200
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}