package com.example.demo.util.SAP;

import com.example.demo.feature.material.domain.dto.request.MaterialRequest;
import com.example.demo.feature.material.domain.entities.Material;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SAPHelper {
    /**
     * 📌 Chuyển đổi `MATNR` thành định dạng SAP (18 ký tự, có số 0 phía trước)
     */
    public static String convertToMATNR(String materialNumber) {
        if (materialNumber == null || !materialNumber.matches("\\d+")) {
            throw new IllegalArgumentException("Invalid Material Number format.");
        }
        return String.format("%018d", Long.parseLong(materialNumber.trim()));
    }

    /**
     * 📌 Hàm này sử dụng regex ^0+ để tìm và loại bỏ tất cả các số 0 ở đầu chuỗi. 🚀
     */
    public static String removeLeadingZeros(String input) {
        return input.replaceFirst("^0+", "");
    }

    /**
     * 📌 Hàm này lấy các đầu vào của SAP function module. 🚀
     */
    public static Map<String, JCoStructure> mapStructures(JCoFunction function, MaterialRequest material) {
        Map<String, JCoStructure> structureMap = new HashMap<>();

        if (material == null || function == null) return structureMap;

        // Lấy class của Material
        Class<?> clazz = material.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            try {
                // Lấy tên trường (ví dụ: Matnr -> IN_SO_MATNR)
                String fieldName = field.getName().toUpperCase();
                String sapParamName = "IN_SO_" + fieldName;

                // Lấy structure từ function
                JCoStructure structure = function.getImportParameterList().getStructure(sapParamName);
                structureMap.put(fieldName, structure);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return structureMap;
    }

}
