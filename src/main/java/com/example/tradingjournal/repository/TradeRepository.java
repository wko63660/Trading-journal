package com.example.tradingjournal.repository;

import com.example.tradingjournal.model.Trade;
import com.example.tradingjournal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findTradesByUser(User user);
}
