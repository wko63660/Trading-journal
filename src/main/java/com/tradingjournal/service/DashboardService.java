// service/DashboardService.java
package com.tradingjournal.service;

import com.tradingjournal.dto.DashboardDTO;
import com.tradingjournal.model.Trade;
import com.tradingjournal.repository.TradeRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private final TradeRepository tradeRepository;

    public DashboardService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    public DashboardDTO getDashboard() {
        List<Trade> trades = tradeRepository.findAll();

        double totalProfit = 0;
        double totalLoss = 0;
        int wins = 0;
        int losses = 0;
        Duration totalHoldingDuration = Duration.ZERO;
        int closedTrades = 0;

        Map<String, DashboardDTO.DailyStats> dayStatsMap = new LinkedHashMap<>();
        List<String> daysOrder = List.of("Mon", "Tue", "Wed", "Thu", "Fri");

        for (String day : daysOrder) {
            dayStatsMap.put(day, new DashboardDTO.DailyStats(day, 0, 0));
        }

        for (Trade trade : trades) {
            double pnl = trade.getExitPrice() - trade.getEntryPrice();
            boolean isWin = pnl > 0;

            // Day label
            String dayLabel = trade.getEntryDate().getDayOfWeek().name().substring(0, 3);

            dayStatsMap.computeIfPresent(dayLabel, (k, v) -> {
                v.pnl += pnl;
                v.trades += 1;
                return v;
            });
            if (trade.getExitDate() != null) {
                Duration holding = Duration.between(trade.getEntryDate(), trade.getExitDate());
                totalHoldingDuration = totalHoldingDuration.plus(holding);
                closedTrades++;
            }
            if (isWin) {
                totalProfit += pnl;
                wins++;
            } else {
                totalLoss += Math.abs(pnl);
                losses++;
            }
        }

        double profitFactor = totalLoss > 0 ? totalProfit / totalLoss : 0;
        double winLossRatio = losses > 0 ? (double) wins / losses : wins;
        double avgRR = winLossRatio; // Placeholder, can calculate avg RR based on R units if available
        String avgHoldingTime = "N/A";
        if (closedTrades > 0) {
            Duration avgDuration = totalHoldingDuration.dividedBy(closedTrades);
            long hours = avgDuration.toHours();
            long minutes = avgDuration.toMinutesPart();
            avgHoldingTime = hours + "h " + minutes + "m";
        }

        return new DashboardDTO(
                round(profitFactor),
                round(avgRR),
                round(winLossRatio),
                new ArrayList<>(dayStatsMap.values()),
                avgHoldingTime
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
