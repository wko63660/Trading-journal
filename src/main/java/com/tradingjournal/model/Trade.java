package com.tradingjournal.model;

import com.tradingjournal.model.enums.TradeSide;
import com.tradingjournal.model.enums.TradeStatus;
import com.tradingjournal.model.enums.TradeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
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


    @ManyToOne
    @NotBlank(message = "Symbol is required")
    @JoinColumn(name = "instrument_symbol", referencedColumnName = "symbol", nullable = false)
    private Instrument instrument;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Trade type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeType tradeType;

    @NotNull(message = "Trade side is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeSide side;

    @NotNull(message = "Entry price is required")
    @Positive
    @Column(nullable = false)
    private Double entryPrice;

    @Positive
    private Double exitPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @PastOrPresent
    @Column(nullable = false)
    private LocalDate entryDate;

    private LocalDate exitDate;

    @Column(nullable = false)
    private LocalTime entryTime;

    private LocalTime exitTime;

    private String setup;

    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TradeNote> tradeNotes;

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
