package com.banco.apitarjetas.service;

import com.banco.apitarjetas.entity.Card;
import com.banco.apitarjetas.repository.cardRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

@Service
public class CardService {

    private final cardRepository repository;

    public CardService(cardRepository repository) {
        this.repository = repository;
    }

    public Card emitirTarjeta(String productId, String holderName) {
        if (productId == null || !productId.matches("\\d{6}")) {
            throw new RuntimeException("El Product ID debe tener 6 dígitos numéricos");
        }

        Card card = new Card();
        card.setCardId(UUID.randomUUID().toString()); // ID único público
        card.setHolderName(holderName);
        card.setIssuedAt(LocalDate.now());
        card.setExpiresAt(LocalDate.now().plusYears(3)); // Regla PDF: +3 años
        card.setStatus("ISSUED"); // Estado inicial
        card.setBalance(BigDecimal.ZERO); // Regla PDF: Saldo 0.00
        card.setCurrency("USD"); // Regla PDF: Moneda USD

        // Regla PDF: CardNumber = productId (6) + 10 aleatorios
        String numeroGenerado = productId + generarDigitosAleatorios(10);
        card.setCardNumber(numeroGenerado);

        return repository.save(card);
    }

    // --- 2. ACTIVAR TARJETA ---
    public Card activarTarjeta(String cardId) {
        Card card = repository.findByCardId(cardId)
                .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada"));

        if ("ACTIVE".equals(card.getStatus())) {
            return card; // Idempotencia: Si ya está activa, devolvemos OK sin hacer nada
        }
        
        if ("BLOCKED".equals(card.getStatus())) {
            throw new RuntimeException("No se puede activar una tarjeta bloqueada");
        }

        if ("ISSUED".equals(card.getStatus())) {
            card.setStatus("ACTIVE");
            return repository.save(card);
        }
        
        return card;
    }

    public Card bloquearTarjeta(String cardId) {
        Card card = repository.findByCardId(cardId)
                .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada"));

        if ("BLOCKED".equals(card.getStatus())) {
            return card; // Idempotencia
        }

        card.setStatus("BLOCKED");
        card.setBlockedAt(LocalDate.now());
        return repository.save(card);
    }
    
    public Card consultarTarjeta(String cardId) {
        return repository.findByCardId(cardId)
                .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada"));
    }

    private String generarDigitosAleatorios(int longitud) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}