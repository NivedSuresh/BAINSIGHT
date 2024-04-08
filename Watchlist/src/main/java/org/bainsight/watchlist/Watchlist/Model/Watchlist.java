package org.bainsight.watchlist.Watchlist.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(uniqueConstraints = @UniqueConstraint(name = "ucc_watchlist", columnNames = {"ucc", "watchlist_name"}))
@Entity public class Watchlist {
    @Id
    @Column(name = "watchlist_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long watchlistId;

    private UUID ucc;

    @Column(name = "watchlist_name")
    private String watchlistName;

    @Column(name = "is_pinned")
    private boolean pinned;

    @ElementCollection
    @CollectionTable(name = "watchlist_symbols", joinColumns = @JoinColumn(name = "watchlist_id"))
    @Column(name = "symbol")
    private List<String> symbols;
}
