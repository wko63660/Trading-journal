package com.tradingjournal.logic;

import java.time.LocalDateTime;
import java.util.*;

public class AvgCostTradeMatcher {

    public static class TradeRecord {
        public String symbol;
        public String action; // BUYTOOPEN, SELLTOCLOSE, etc.
        public LocalDateTime dateTime;
        public double price;
        public int quantity;

        public TradeRecord(String symbol, String action, LocalDateTime dateTime, double price, int quantity) {
            this.symbol = symbol;
            this.action = action;
            this.dateTime = dateTime;
            this.price = price;
            this.quantity = quantity;
        }
    }

    public static class CompletedTrade {
        public String symbol;
        public LocalDateTime entryTime;
        public LocalDateTime exitTime;
        public double entryPrice;
        public double exitPrice;
        public int quantity;
        public double pnl;

        public CompletedTrade(String symbol, LocalDateTime entryTime, LocalDateTime exitTime, double entryPrice, double exitPrice, int quantity, double pnl) {
            this.symbol = symbol;
            this.entryTime = entryTime;
            this.exitTime = exitTime;
            this.entryPrice = entryPrice;
            this.exitPrice = exitPrice;
            this.quantity = quantity;
            this.pnl = pnl;
        }
    }

    public static class MatchResult {
        public List<CompletedTrade> completedTrades;
        public List<TradeRecord> openPositions;

        public MatchResult(List<CompletedTrade> completedTrades, List<TradeRecord> openPositions) {
            this.completedTrades = completedTrades;
            this.openPositions = openPositions;
        }
    }

    public static class PositionTracker {
        private double totalCost = 0.0;
        private int totalQuantity = 0;
        private final Queue<TradeRecord> entryQueue = new LinkedList<>();

        public void addEntry(TradeRecord entry) {
            totalCost += entry.price * entry.quantity;
            totalQuantity += entry.quantity;
            entryQueue.add(entry);
        }

        public List<CompletedTrade> closeOut(TradeRecord exit) {
            List<CompletedTrade> completed = new ArrayList<>();
            int qtyToClose = exit.quantity;
            double avgEntryPrice = getAvgPrice();

            while (qtyToClose > 0 && !entryQueue.isEmpty()) {
                TradeRecord entry = entryQueue.peek();
                int matchQty = Math.min(entry.quantity, qtyToClose);

                double pnl = (exit.price - avgEntryPrice) * matchQty * 100; // Assume options

                completed.add(new CompletedTrade(
                        entry.symbol,
                        entry.dateTime,
                        exit.dateTime,
                        avgEntryPrice,
                        exit.price,
                        matchQty,
                        pnl
                ));

                // Update position
                totalCost -= avgEntryPrice * matchQty;
                totalQuantity -= matchQty;
                entry.quantity -= matchQty;
                qtyToClose -= matchQty;

                if (entry.quantity == 0) {
                    entryQueue.poll();
                }
            }

            return completed;
        }

        public double getAvgPrice() {
            return totalQuantity == 0 ? 0.0 : totalCost / totalQuantity;
        }

        public List<TradeRecord> getOpenPositions() {
            return new ArrayList<>(entryQueue);
        }
    }

    public static MatchResult match(List<TradeRecord> records) {
        List<CompletedTrade> results = new ArrayList<>();
        Map<String, PositionTracker> trackers = new HashMap<>();

        records.sort(Comparator.comparing(r -> r.dateTime));

        for (TradeRecord record : records) {
            String key = record.symbol;
            trackers.putIfAbsent(key, new PositionTracker());
            PositionTracker tracker = trackers.get(key);

            if (record.action.equals("BUYTOOPEN") || record.action.equals("SELLTOOPEN")) {
                tracker.addEntry(record);
            } else if (record.action.equals("BUYTOCLOSE") || record.action.equals("SELLTOCLOSE")) {
                results.addAll(tracker.closeOut(record));
            }
        }

        List<TradeRecord> remainingOpen = new ArrayList<>();
        for (PositionTracker tracker : trackers.values()) {
            remainingOpen.addAll(tracker.getOpenPositions());
        }

        return new MatchResult(results, remainingOpen);
    }
}
