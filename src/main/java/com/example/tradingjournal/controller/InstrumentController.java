package com.example.tradingjournal.controller;

import com.example.tradingjournal.model.Instrument;
import com.example.tradingjournal.service.InstrumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "Instruments", description = "Endpoints for managing trading instruments")
@RestController
@RequestMapping("/api/instruments")
@RequiredArgsConstructor
public class InstrumentController {

    private final InstrumentService instrumentService;

    @Operation(summary = "Create a new instrument")
    @PostMapping
    public ResponseEntity<Instrument> createInstrument(@Valid @RequestBody Instrument instrument) {
        return ResponseEntity.ok(instrumentService.createInstrument(instrument));
    }

    @Operation(summary = "Get instrument by symbol")
    @GetMapping("/{symbol}")
    public ResponseEntity<Instrument> getInstrumentBySymbol(@PathVariable String symbol) {
        return ResponseEntity.ok(instrumentService.getInstrumentBySymbol(symbol));
    }

    @Operation(summary = "List all instruments")
    @GetMapping
    public ResponseEntity<List<Instrument>> getAllInstruments() {
        return ResponseEntity.ok(instrumentService.getAllInstruments());
    }

    @Operation(summary = "Update an existing instrument")
    @PutMapping("/{symbol}")
    public ResponseEntity<Instrument> updateInstrument(@PathVariable String symbol,
                                                       @Valid @RequestBody Instrument updatedInstrument) {
        return ResponseEntity.ok(instrumentService.updateInstrument(symbol, updatedInstrument));
    }

    @Operation(summary = "Delete an instrument by symbol")
    @DeleteMapping("/{symbol}")
    public ResponseEntity<Void> deleteInstrument(@PathVariable String symbol) {
        instrumentService.deleteInstrument(symbol);
        return ResponseEntity.ok().build();
    }
}

