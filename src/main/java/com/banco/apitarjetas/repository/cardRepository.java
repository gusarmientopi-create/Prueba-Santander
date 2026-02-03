package com.banco.apitarjetas.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banco.apitarjetas.entity.Card;

public interface cardRepository  extends JpaRepository<Card,Long>{


boolean existsByCardNumber(String CardNumber);

Optional<Card> findByCardId(String cardId);
}
