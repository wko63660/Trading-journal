package com.tradingjournal.repository;

import com.tradingjournal.model.Trade;
import com.tradingjournal.model.TradeNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeNoteRepository extends JpaRepository<TradeNote, Long> {

    List<TradeNote> findByTrade(Trade trade);

}
