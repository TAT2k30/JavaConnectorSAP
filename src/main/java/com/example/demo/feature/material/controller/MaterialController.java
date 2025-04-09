package com.example.demo.feature.material.controller;

import com.example.demo.common.ResponseHandler;
import com.example.demo.feature.material.domain.dto.request.MaterialRequest;
import com.example.demo.feature.material.domain.entities.Material;
import com.example.demo.feature.material.services.impl.MaterialServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/material")
public class MaterialController extends ResponseHandler {
    private final MaterialServiceImpl materialService;

    public MaterialController(MaterialServiceImpl materialService) {
        this.materialService = materialService;
    }

    @PostMapping("/getMaterialData")
    public ResponseEntity<?> getMaterialData(@RequestBody MaterialRequest materialRequest) {
        try {
            List<Material> materialReturn = materialService.getMaterialByMatnr(materialRequest);

            if (materialReturn.isEmpty()) {
                return buildSuccessResponse(null, "No material data retrieved", HttpStatus.NOT_FOUND);
            }

            return buildSuccessResponse(materialReturn, "Get material data successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request parameters: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error: " + e.getMessage());
        }
    }
}
