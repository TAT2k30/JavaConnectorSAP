package com.example.demo.feature.material.services;

import com.example.demo.feature.material.domain.dto.request.MaterialRequest;
import com.example.demo.feature.material.domain.entities.Material;

import java.util.List;

public interface MaterialService {
    List<Material> getMaterialByMatnr(MaterialRequest matnerialRequest);
}
