package com.tradingjournal.controller;

import com.tradingjournal.model.Trade;
import com.tradingjournal.model.TradeNote;
import com.tradingjournal.service.TradeNoteService;
import com.tradingjournal.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tradenote")
@RequiredArgsConstructor
public class TradeNodeController {
    private final TradeNoteService tradeNoteService;
    private final TradeService tradeService;

    @PostMapping
    public ResponseEntity<TradeNote> createNote(@Valid @RequestBody TradeNote tradeNote) {
        return ResponseEntity.ok(tradeNoteService.saveTradeNote(tradeNote));
    }

    @GetMapping
    public ResponseEntity<List<TradeNote>> getAllNotes() {
        return ResponseEntity.ok(tradeNoteService.getAllTradeNotes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TradeNote> getNoteById(@PathVariable Long id) {
        return ResponseEntity.ok(tradeNoteService.getTradeNote(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TradeNote> updateNote(@PathVariable Long id, @Valid @RequestBody TradeNote updatedNote) {
        return ResponseEntity.ok(tradeNoteService.updateTradeNote(id, updatedNote));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        tradeNoteService.deleteTradeNote(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-trade/{tradeId}")
    public ResponseEntity<List<TradeNote>> getNotesByTrade(@PathVariable Long tradeId) {
        Trade trade = tradeService.getTradeById(tradeId);
        return ResponseEntity.ok(tradeNoteService.getNotesByTrade(trade));
    }

}
