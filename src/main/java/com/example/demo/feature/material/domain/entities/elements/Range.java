package com.example.demo.feature.material.domain.entities.elements;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Range {
    private String low;
    private String high;
}
