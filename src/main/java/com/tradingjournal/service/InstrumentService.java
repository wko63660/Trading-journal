package com.tradingjournal.service;

import com.tradingjournal.model.Instrument;
import com.tradingjournal.repository.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;

    public Instrument createInstrument(Instrument instrument) {
        return instrumentRepository.save(instrument);
    }

    public Instrument getInstrumentBySymbol(String symbol) {
        return instrumentRepository.findById(symbol)
                .orElseThrow(() -> new RuntimeException("Instrument not found with symbol: " + symbol));
    }

    public List<Instrument> getAllInstruments() {
        return instrumentRepository.findAll();
    }

    public Instrument updateInstrument(String symbol, Instrument updatedInstrument) {
        Instrument existing = getInstrumentBySymbol(symbol);
        existing.setName(updatedInstrument.getName());
        existing.setType(updatedInstrument.getType());
        return instrumentRepository.save(existing);
    }

    public void deleteInstrument(String symbol) {
        instrumentRepository.deleteById(symbol);
    }
}
