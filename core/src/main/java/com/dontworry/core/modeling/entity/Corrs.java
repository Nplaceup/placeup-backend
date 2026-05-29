package com.dontworry.core.modeling.entity;

import com.dontworry.core.modeling.enums.CorrClass;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "corrs")
public class Corrs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;

    private Double correlation;

    @Enumerated(EnumType.STRING)
    private CorrClass corrClass;

    private LocalDateTime eventDate;
}
