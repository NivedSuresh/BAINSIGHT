package org.bainsight.watchlist.Watchlist.Model.Entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.Set;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity public class Watchlist {
    @Id
    @Column(name = "watchlist_id")
    private Long watchlistId;

    private UUID ucc;

    @Column(name = "watchlist_name")
    private String watchlistName;

    @Column(name = "is_pinned")
    private boolean isPinned;

    @ElementCollection
    @CollectionTable(name = "symbols")
    private Set<String> symbols;
}
