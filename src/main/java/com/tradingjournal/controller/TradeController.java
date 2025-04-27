package com.tradingjournal.controller;

import com.tradingjournal.dto.TlgTradeDTO;
import com.tradingjournal.dto.TradeDTO;
import com.tradingjournal.model.Trade;
import com.tradingjournal.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping
    public ResponseEntity<Trade> createTrade(@Valid @RequestBody Trade trade) {
        return ResponseEntity.ok(tradeService.createTrade(trade));
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> uploadTrades(@RequestBody List<TradeDTO> trades) {
        tradeService.uploadTrades(trades);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/import-trades/{username}")
    public ResponseEntity<?> importTrades(
            @PathVariable String username,
            @RequestBody List<TlgTradeDTO> trades
    ) {
        tradeService.importFromTlg(trades, username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trade> getTradeById(@PathVariable Long id) {
        return ResponseEntity.ok(tradeService.getTradeById(id));
    }

    @GetMapping
    public ResponseEntity<List<Trade>> getAllTrades() {
        return ResponseEntity.ok(tradeService.getAllTrades());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Trade>> getTradesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(tradeService.getTradesByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trade> updateTrade(@PathVariable Long id, @Valid @RequestBody Trade trade) {
        return ResponseEntity.ok(tradeService.updateTrade(id, trade));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrade(@PathVariable Long id) {
        tradeService.deleteTrade(id);
        return ResponseEntity.noContent().build();
    }
}
