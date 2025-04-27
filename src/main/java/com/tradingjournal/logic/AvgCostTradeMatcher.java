package com.tradingjournal.logic;

import com.tradingjournal.dto.TlgTradeDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

public class AvgCostTradeMatcher {

    @Data
    @Builder
    public static class TradeRecord {
        private String symbol;
        private String action; // BUYTOOPEN, SELLTOCLOSE, etc.
        private LocalDateTime dateTime;
        private double price;
        private int quantity;
        private int position;
        private int volume;
        private TlgTradeDTO original;
    }

    @Data
    @Builder
    public static class CompletedTrade {
        private String symbol;
        private LocalDateTime entryDateTime;
        private LocalDateTime exitDateTime;
        private double entryPrice;
        private double exitPrice;
        private int volume;
        private int position;
        private int quantity;
        private double pnl;
        private TlgTradeDTO original;
    }

    public static class MatchResult {
        public List<CompletedTrade> completedTrades;
        public List<TradeRecord> openPositions;

        public MatchResult(List<CompletedTrade> completedTrades, List<TradeRecord> openPositions) {
            this.completedTrades = completedTrades;
            this.openPositions = openPositions;
        }
    }


    public static MatchResult match(List<TradeRecord> records) {
        Map<String, Queue<TradeRecord>> openMap = new HashMap<>();
        List<CompletedTrade> completed = new ArrayList<>();

        records.sort(Comparator.comparing(r -> r.dateTime));

        for (TradeRecord record : records) {
            boolean isOption = record.original != null
                    ? "OPTION".equalsIgnoreCase(record.original.tradeType)
                    : true;

            String key = record.original != null
                    ? (isOption ? record.original.symbol + ":" + record.original.contract : record.original.symbol)
                    :record.symbol;
            record.symbol = key;

            openMap.putIfAbsent(key, new LinkedList<>());

            if (record.action.endsWith("OPEN")) {
                openMap.get(key).add(record);
            } else if (record.action.endsWith("CLOSE")) {
                Queue<TradeRecord> queue = openMap.get(key);
                int remainingToClose = record.quantity;

                while (remainingToClose > 0 && queue != null && !queue.isEmpty()) {
                    TradeRecord open = queue.peek();
                    int matchedQty = Math.min(open.quantity, remainingToClose);
                    double multiplier = isOption ? 100.0 : 1.0;
                    double pnl = (record.price - open.price) * (double) matchedQty; // * multiplier -> temporarily disable

                    LocalDateTime exitDateTime = record.original != null
                            && record.original.exitDateTime != null
                            && !record.original.exitDateTime.isBlank()
                            ? LocalDateTime.parse(record.original.exitDateTime)
                            : record.dateTime;

                    completed.add(CompletedTrade.builder()
                            .symbol(key)
                            .entryDateTime(open.dateTime)
                            .exitDateTime(exitDateTime)
                            .entryPrice(open.price)
                            .exitPrice(record.price)
                            .volume(Math.abs(record.position)*2)
                            .quantity(matchedQty)
                            .pnl(pnl)
                            .original(open.original)
                            .build());

                    open.quantity -= matchedQty;
                    remainingToClose -= matchedQty;

                    if (open.quantity == 0) {
                        queue.poll();
                    }
                }
            }
        }

        List<TradeRecord> remaining = new ArrayList<>();
        for (Queue<TradeRecord> queue : openMap.values()) {
            remaining.addAll(queue);
        }

        return new MatchResult(completed, remaining);
    }


}
