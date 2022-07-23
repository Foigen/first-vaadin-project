package com.foigen.socrate.entities;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@ToString
@Entity
@Table
public class RateCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true)
    String title;
    @OneToMany(fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "con_id")
    @Builder.Default
    List<RateMod> mods = new ArrayList<>();

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RateCondition that = (RateCondition) o;

        return Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return title != null ? title.hashCode() : 0;
    }
}
