package com.example.tradingjournal.repository;
import com.example.tradingjournal.model.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstrumentRepository extends JpaRepository<Instrument, String> {
}
