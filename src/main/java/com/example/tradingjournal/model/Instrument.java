package com.example.tradingjournal.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instrument {
    @Id
    private String symbol; // Use symbol as PK

    private String name;
    private String market;
    private String type;

    @OneToMany(mappedBy = "instrument", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Trade> trades;
}
