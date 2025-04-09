package com.example.demo.feature.material.domain.entities;


import com.example.demo.feature.material.domain.entities.elements.Range;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Material {
    private String matnr;
    private String mtart;
    private String matkl;
    private String meins;
    private String werks;
    private String lgort;
    private String maktx;
    private String umrez;
    private String umren;
    private String stprs;
    private String verpr;
    private String peinh;
    private String waers;
}
