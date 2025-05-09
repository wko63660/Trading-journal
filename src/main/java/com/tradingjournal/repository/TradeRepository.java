package com.tradingjournal.repository;

import com.tradingjournal.model.Trade;
import com.tradingjournal.model.User;
import com.tradingjournal.model.enums.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findTradesByUser(User user);
    List<Trade> findByStatusAndUserId(TradeStatus tradeStatus, long UserId);
}