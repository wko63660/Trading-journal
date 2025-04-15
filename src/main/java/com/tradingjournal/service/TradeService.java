package com.tradingjournal.service;

import com.tradingjournal.model.Trade;
import com.tradingjournal.model.User;
import com.tradingjournal.repository.TradeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;
    private final UserService userService;

    public Trade createTrade(Trade trade) {
        return tradeRepository.save(trade);
    }

    public Trade getTradeById(Long id) {
        return tradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade not found with id: " + id));
    }

    public List<Trade> getTradesByUserId(Long userId) {
        User user = userService.getUserById(userId);
        return tradeRepository.findTradesByUser(user);
    }

    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }

    public Trade updateTrade(Long id, Trade updatedTrade) {
        Trade existing = getTradeById(id);
        existing.setEntryPrice(updatedTrade.getEntryPrice());
        existing.setExitPrice(updatedTrade.getExitPrice());
        existing.setQuantity(updatedTrade.getQuantity());
        existing.setTradeNotes(updatedTrade.getTradeNotes());
        existing.setStatus(updatedTrade.getStatus());
        existing.setStopLoss(updatedTrade.getStopLoss());
        existing.setTargetPrice(updatedTrade.getTargetPrice());
        existing.setSetup(updatedTrade.getSetup());
        existing.setTags(updatedTrade.getTags());
        existing.setExitDate(updatedTrade.getExitDate());
        existing.setExitTime(updatedTrade.getExitTime());
        return tradeRepository.save(existing);
    }

    public void deleteTrade(Long id) {
        if (!tradeRepository.existsById(id)) {
            throw new EntityNotFoundException("Trade not found with id: " + id);
        }
        tradeRepository.deleteById(id);
    }
}
