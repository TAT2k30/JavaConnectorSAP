package com.example.demo.feature.material.domain.dto.request;


import com.example.demo.feature.material.domain.entities.elements.Range;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialRequest {
    private Range Matnr;
    private Range Mtart;
    private Range Matkl;
    private Range Meins;
    private Range Werks;
    private Range Lgort;
}
