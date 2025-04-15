package com.tradingjournal.service;

import com.tradingjournal.model.Trade;
import com.tradingjournal.model.TradeNote;
import com.tradingjournal.repository.TradeNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeNoteService {

    private final TradeNoteRepository tradeNoteRepository;

    public TradeNote saveTradeNote(TradeNote tradeNote) {
        return tradeNoteRepository.save(tradeNote);
    }

    public List<TradeNote> getAllTradeNotes() {
        return tradeNoteRepository.findAll();
    }

    public TradeNote getTradeNote(Long id) {
        return tradeNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade note not found with id: " + id));
    }

    public void deleteTradeNote(Long id) {
        tradeNoteRepository.deleteById(id);
    }

    public TradeNote updateTradeNote(Long id, TradeNote updatedNote) {
        TradeNote existing = getTradeNote(id);
        existing.setContent(updatedNote.getContent());
        return tradeNoteRepository.save(existing);
    }

    public List<TradeNote> getNotesByTrade(Trade trade) {
        return tradeNoteRepository.findByTrade(trade);
    }
}
