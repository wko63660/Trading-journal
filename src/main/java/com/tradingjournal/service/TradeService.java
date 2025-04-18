package com.tradingjournal.service;

import com.tradingjournal.controller.TlgTradeDTO;
import com.tradingjournal.dto.TradeDTO;
import com.tradingjournal.logic.AvgCostTradeMatcher;
import com.tradingjournal.logic.AvgCostTradeMatcher.*;
import com.tradingjournal.model.Trade;
import com.tradingjournal.model.User;
import com.tradingjournal.model.enums.TradeAction;
import com.tradingjournal.model.enums.TradeSide;
import com.tradingjournal.model.enums.TradeStatus;
import com.tradingjournal.model.enums.TradeType;
import com.tradingjournal.repository.TradeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;
    private final UserService userService;

    public Trade createTrade(Trade trade) {
        return tradeRepository.save(trade);
    }

    public void importFromTlg(List<TlgTradeDTO> tlgList, String username) {
        List<TradeRecord> rawRecords = tlgList.stream().map(dto ->
                new TradeRecord(
                        dto.symbol + ":" + dto.contract,
                        dto.action,
                        LocalDateTime.parse(dto.dateTime),
                        dto.price,
                        dto.qty
                )
        ).collect(Collectors.toList());

        MatchResult result = AvgCostTradeMatcher.match(rawRecords);
        User user   = userService.getUserByName(username);
        List<Trade> matchedTrades = result.completedTrades.stream().map(c ->
                Trade.builder()
                        .symbol(c.symbol.split(":" )[0])
                        .entryPrice(c.entryPrice)
                        .exitPrice(c.exitPrice)
                        .entryDate(c.entryTime.toLocalDate())
                        .entryTime(c.entryTime.toLocalTime())
                        .exitDate(c.exitTime.toLocalDate())
                        .exitTime(c.exitTime.toLocalTime())
                        .quantity(c.quantity)
                        .pnl(c.pnl)
                        .status(TradeStatus.CLOSED)
                        .tradeAction(TradeAction.BUYTOOPEN) // default/fallback
                        .tradeType(TradeType.OPTION)
                        .side(TradeSide.LONG)
                        .user(user)
                        .build()
        ).collect(Collectors.toList());

        List<Trade> openTrades = result.openPositions.stream().map(o ->
                Trade.builder()
                        .symbol(o.symbol.split(":" )[0])
                        .entryPrice(o.price)
                        .entryDate(o.dateTime.toLocalDate())
                        .entryTime(o.dateTime.toLocalTime())
                        .quantity(o.quantity)
                        .status(TradeStatus.OPEN)
                        .tradeAction(TradeAction.valueOf(o.action))
                        .tradeType(TradeType.OPTION)
                        .side(TradeSide.LONG)
                        .user(user)
                        .build()
        ).collect(Collectors.toList());

        tradeRepository.saveAll(matchedTrades);
        tradeRepository.saveAll(openTrades);
    }


    public void uploadTrades(List<TradeDTO> trades) {

        List<Trade> entities = trades.stream().map(dto -> {
            Trade t = new Trade();
            t.setSymbol(dto.getSymbol());
            t.setSide(TradeSide.valueOf(dto.getSide().toUpperCase()));
            t.setTradeAction(TradeAction.valueOf(dto.getAction().toUpperCase()));
            t.setQuantity(dto.getQty());
            if (!Objects.equals(dto.getEntryTime(), ""))
                t.setEntryTime(LocalTime.parse(dto.getEntryTime()));
            if (!Objects.equals(dto.getExitTime(), ""))
                t.setExitTime(LocalTime.parse(dto.getExitTime()));
            t.setEntryPrice(dto.getEntry());
            t.setExitPrice(dto.getExit());
            t.setTradeNote(dto.getNotes());
            t.setTags(dto.getTags());
            t.setEntryDate(LocalDate.parse(dto.getDate())); // Optional: convert date
            t.setStatus(TradeStatus.valueOf(dto.getStatus().toUpperCase()));
            t.setTradeType(TradeType.valueOf(dto.getTradeType()));
            return t;
        }).collect(Collectors.toList());

        tradeRepository.saveAll(entities);
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
