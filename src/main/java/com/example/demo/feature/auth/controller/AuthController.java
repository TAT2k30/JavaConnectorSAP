package com.example.demo.feature.auth.controller;

import com.example.demo.common.ResponseHandler;
import com.example.demo.common.ResultResponse;
import com.example.demo.feature.account.domain.entities.Account;
import com.example.demo.feature.auth.domain.dto.request.LoginRequest;
import com.example.demo.feature.auth.domain.dto.request.RefreshTokenRequest;
import com.example.demo.feature.auth.domain.dto.response.JwtResponse;
import com.example.demo.feature.auth.services.impl.AuthServiceImpl;
import com.example.demo.feature.material.services.MaterialService;
import com.example.demo.handler.JcoDestinationFileHandler;
import com.example.demo.util.SAP.SAPHelper;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController extends ResponseHandler {
    private final JCoDestination jCoDestination;
    private final JcoDestinationFileHandler jcoDestinationFileHandler;
    private final AuthServiceImpl authService;
    public AuthController(JCoDestination jCoDestination, JcoDestinationFileHandler jcoDestinationFileHandler, AuthServiceImpl authService ) {
        this.jCoDestination = jCoDestination;
        this.jcoDestinationFileHandler = jcoDestinationFileHandler;
        this.authService = authService;
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        try{
            JwtResponse jwtResponse = authService.handleRefreshToken(refreshTokenRequest);
            return buildSuccessResponse(jwtResponse, "Login successful", HttpStatus.OK);
        }
        catch (IllegalArgumentException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request parameters: " + e.getMessage());
        }
        catch(Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody LoginRequest loginRequest) {
        try {
            // Tạo kết nối JCo
            JCoDestination jCoDestination_check = authService.handleCreateJcoConnection(loginRequest);
            if (jCoDestination_check == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to create JCo destination");
            }

            // Tạo đối tượng Account từ SAP
            Account account = new Account();
            account.setUser(jCoDestination_check.getUser());
            account.setLanguage(jCoDestination_check.getLanguage());
            account.setClient(jCoDestination_check.getClient());
            account.setSysId(jCoDestination_check.getSystemNumber());

            // Tạo JWT Token
            JwtResponse jwtResponse = authService.handleCreateJwtToken(account);
            return buildSuccessResponse(jwtResponse, "Login successful", HttpStatus.OK);
         } catch (IllegalArgumentException e) {

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request parameters: " + e.getMessage());
    } catch (Exception e) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e.getMessage());
    }
    }

    /**
     * 📌 Gọi SAP Function `ZFM_MATERIAL_DATA` để lấy thông tin vật tư
     */
    private Map<String, String> handleSapMaterialData(String materialNumber) {
        Map<String, String> materialData = new HashMap<>();
        try {
            JCoFunction function2 = jCoDestination.getRepository().getFunction("ZFM_MATERIAL_DATA");

            if (function2 == null) {
                throw new RuntimeException("SAP Function 'ZFM_MATERIAL_DATA' not found.");
            }

            // Chuyển đổi MATNR thành kiểu 18 ký tự
            function2.getImportParameterList().setValue("IN", SAPHelper.convertToMATNR(materialNumber));
            function2.execute(jCoDestination);

            // Lấy dữ liệu từ EXPORTING parameter `OUT`
            JCoStructure outStructure = function2.getExportParameterList().getStructure("OUT");

            materialData.put("MATNR", outStructure.getString("MATNR"));
            materialData.put("MTART", outStructure.getString("MTART"));
            materialData.put("MEINS", outStructure.getString("MEINS"));
            materialData.put("MAKTX", outStructure.getString("MAKTX"));
            System.out.print(materialData);

        } catch (Exception e) {
            System.err.println("Error fetching material data: " + e.getMessage());
        }

        return materialData;
    }




}
