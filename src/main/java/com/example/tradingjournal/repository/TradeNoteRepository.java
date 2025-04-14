package com.example.tradingjournal.repository;

import com.example.tradingjournal.model.Trade;
import  com.example.tradingjournal.model.TradeNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeNoteRepository extends JpaRepository<TradeNote, Long> {

    List<TradeNote> findByTrade(Trade trade);

}
