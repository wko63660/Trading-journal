package com.tradingjournal.service;

import com.tradingjournal.dto.TlgTradeDTO;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        User user = userService.getUserByName(username);
        //get open trades from db
        List<Trade> dbOpenTrades = tradeRepository.findByStatusAndUserId(TradeStatus.OPEN, user.getId());
        List<TradeRecord> dbRecords = dbOpenTrades.stream().map(trade -> TradeRecord.builder()
                .symbol(trade.getSymbol())
                .action(trade.getTradeAction().name())
                .dateTime(trade.getEntryDateTime())
                .price(trade.getEntryPrice())
                .quantity(trade.getQuantity())
                .position(trade.getPosition())
                .original(null) // no TLG origin
                .build()
        ).collect(Collectors.toList());


        List<TradeRecord> rawRecords = tlgList.stream()
                .map(dto ->{
                    LocalDateTime entry = (dto.entryDateTime != null && !dto.entryDateTime.isBlank())
                            ? LocalDateTime.parse(dto.entryDateTime)
                            : LocalDateTime.now();

                    LocalDateTime exit = (dto.exitDateTime != null && !dto.exitDateTime.isBlank())
                            ? LocalDateTime.parse(dto.exitDateTime)
                            : null;
                    return TradeRecord.builder()
                            .symbol(dto.symbol + ":" + dto.contract)
                            .action(dto.action)
                            .dateTime(entry)
                            .price(dto.price)
                            .quantity(dto.qty)
                            .position(dto.position)
                            .original(dto)
                            .build();}
        ).collect(Collectors.toList());

        List<TradeRecord> allRecords = new ArrayList<>();
        allRecords.addAll(dbRecords);
        allRecords.addAll(rawRecords);

        MatchResult result = AvgCostTradeMatcher.match(allRecords);
        List<Trade> matchedTrades = result.completedTrades.stream().map(c ->
                Trade.builder()
                        .symbol(c.getSymbol().split(":" )[0])
                        .contract(c.getSymbol())
                        .entryPrice(c.getEntryPrice())
                        .exitPrice(c.getExitPrice())
                        .entryDateTime(c.getEntryDateTime())
                        .exitDateTime(c.getExitDateTime())
                        .quantity(c.getQuantity())
                        .volume(c.getVolume())
                        .position(c.getPosition())
                        .pnl(c.getPnl())
                        .status(TradeStatus.CLOSED)
                        .tradeAction(TradeAction.BUYTOOPEN) // default/fallback
                        .tradeType(TradeType.OPTION)
                        .side(TradeSide.LONG)
                        .user(user)
                        .build()
        ).collect(Collectors.toList());

        List<Trade> openTrades = result.openPositions.stream().map(o ->
                Trade.builder()
                        .symbol(o.getSymbol().split(":" )[0])
                        .contract(o.getSymbol())
                        .entryPrice(o.getPrice())
                        .entryDateTime(o.getDateTime())
                        .quantity(o.getQuantity())
                        .position(o.getPosition())
                        .volume(Math.abs(o.getPosition()))
                        .status(TradeStatus.OPEN)
                        .tradeAction(TradeAction.valueOf(o.getAction()))
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
            if (!Objects.equals(dto.getEntryDateTime(), ""))
                t.setEntryDateTime(LocalDateTime.parse(dto.getEntryDateTime()));
            if (!Objects.equals(dto.getExitDateTime(), ""))
                t.setExitDateTime(LocalDateTime.parse(dto.getExitDateTime()));
            t.setEntryPrice(dto.getEntry());
            t.setExitPrice(dto.getExit());
            t.setTradeNote(dto.getNotes());
            t.setTags(dto.getTags());
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
        existing.setExitDateTime(updatedTrade.getExitDateTime());
        return tradeRepository.save(existing);
    }

    public void deleteTrade(Long id) {
        if (!tradeRepository.existsById(id)) {
            throw new EntityNotFoundException("Trade not found with id: " + id);
        }
        tradeRepository.deleteById(id);
    }
}
