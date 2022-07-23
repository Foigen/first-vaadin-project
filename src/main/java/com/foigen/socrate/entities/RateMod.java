package com.foigen.socrate.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class RateMod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    RateTag tag;
    Double mod;

    @Override
    public String toString() {
        return String.format("%+.2f",mod)+" "+tag.getName();
    }
}
