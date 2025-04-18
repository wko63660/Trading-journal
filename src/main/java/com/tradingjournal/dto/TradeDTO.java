package com.tradingjournal.dto;

import com.tradingjournal.model.enums.TradeType;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeDTO {
    private String symbol;
    private String date;      // or LocalDate if parsed
    private String side;
    private String Status;
    private String action;
    private int qty;
    private String entryTime;
    private String exitTime;
    private double entry;
    private double exit;
    private double pnl;
    private String notes;
    private List<String> tags;
    private String tradeType;
}
