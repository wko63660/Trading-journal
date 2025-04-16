// dto/DashboardDTO.java
package com.tradingjournal.dto;

import java.util.List;

public class DashboardDTO {
    public static class DailyStats {
        public String day;
        public double pnl;
        public int trades;

        public DailyStats(String day, double pnl, int trades) {
            this.day = day;
            this.pnl = pnl;
            this.trades = trades;
        }
    }

    public double profitFactor;
    public double winLossRatio;
    public double winVsLoss;
    public String avgHoldingTime;
    public List<DailyStats> days;

    public DashboardDTO(double profitFactor, double winLossRatio, double winVsLoss, List<DailyStats> days, String avgHoldingTime) {
        this.profitFactor = profitFactor;
        this.winLossRatio = winLossRatio;
        this.winVsLoss = winVsLoss;
        this.days = days;
        this.avgHoldingTime = avgHoldingTime;
    }
}
