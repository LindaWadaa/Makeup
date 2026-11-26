package com.makeup.dto;

import java.time.LocalDate;

import com.makeup.entities.Marque;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MakeupDTO {
    private Long id;
    private String nom;
    private Double price;
    private String description;
    private LocalDate dateExpiration;
    private Marque marque;
    private String nomMarque;
}
