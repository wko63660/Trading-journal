package com.tradingjournal.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tradingjournal.model.enums.TradeAction;
import com.tradingjournal.model.enums.TradeSide;
import com.tradingjournal.model.enums.TradeStatus;
import com.tradingjournal.model.enums.TradeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


//    @ManyToOne
//    @NotBlank(message = "Symbol is required")
//    @JoinColumn(name = "instrument_symbol", referencedColumnName = "symbol", nullable = false)
//    private Instrument instrument;


    @NotBlank(message = "Symbol is required")
    @Column(nullable = false)
    private String symbol;

    private String contract;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Trade type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeType tradeType;

    @NotNull(message = "Trade action is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeAction tradeAction;

    @NotNull(message = "Trade side is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeSide side;

//    @NotNull(message = "Entry price is required")
    @Positive
//    @Column(nullable = false)
    private Double entryPrice;

    @Positive
    private Double exitPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private Integer position;

    private Integer volume;

    private String exchange;

    private Double pnl;

    @PastOrPresent
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime entryDateTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime exitDateTime;

    private String setup;

    @Column(length = 2000)
    private String tradeNote;

    private String screenshotUrl;

    @Positive
    private Double stopLoss;

    @Positive
    private Double targetPrice;

    @ElementCollection
    private List<String> tags;

    @NotNull(message = "Trade status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeStatus status;

}
