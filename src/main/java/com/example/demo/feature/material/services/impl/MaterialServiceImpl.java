package com.example.demo.feature.material.services.impl;

import com.example.demo.feature.material.domain.dto.request.MaterialRequest;
import com.example.demo.feature.material.domain.entities.Material;
import com.example.demo.feature.material.domain.entities.elements.Range;
import com.example.demo.feature.material.services.MaterialService;
import com.example.demo.util.SAP.SAPHelper;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class MaterialServiceImpl extends SAPHelper implements MaterialService {
    private static final Logger logger = LoggerFactory.getLogger(MaterialServiceImpl.class);
    private final JCoDestination jCoDestination;

    public MaterialServiceImpl(JCoDestination jCoDestination) {
        this.jCoDestination = jCoDestination;
    }

    private List<Material> handleSapMaterialData(MaterialRequest materialRequest) {
        List<Material> materialDataList = new ArrayList<>();
        try {
            JCoFunction function = jCoDestination.getRepository().getFunction("ZFM_PG_MM_249");
            if (function == null) {
                throw new RuntimeException("SAP Function 'ZFM_PG_MM_249' not found.");
            }

            // **Gán dữ liệu vào Input Structure**
            Map<String, JCoStructure> structures = SAPHelper.mapStructures(function, materialRequest);
            Map<String, Range> ranges = Map.of(
                    "MATNR", materialRequest.getMatnr(),
                    "MTART", materialRequest.getMtart(),
                    "MATKL", materialRequest.getMatkl(),
                    "MEINS", materialRequest.getMeins(),
                    "WERKS", materialRequest.getWerks(),
                    "LGORT", materialRequest.getLgort()
            );

            for (Map.Entry<String, Range> entry : ranges.entrySet()) {
                JCoStructure structure = structures.get(entry.getKey());
                if (structure != null && entry.getValue() != null) {
                    String key = entry.getKey();
                    Range range = entry.getValue();

                    String lowValue = range.getLow();
                    String highValue = range.getHigh();

                    if ("MATNR".equals(key)) {
                        if (lowValue != null && !lowValue.isEmpty()) {
                            lowValue = SAPHelper.convertToMATNR(lowValue);
                        }
                        if (highValue != null && !highValue.isEmpty()) {
                            highValue = SAPHelper.convertToMATNR(highValue);
                        }
                    }

                    structure.setValue(key + "_LOW", lowValue);
                    structure.setValue(key + "_HIGH", highValue);
                }
            }
            // **Thực thi RFC**
            function.execute(jCoDestination);

            // **Lấy dữ liệu từ EXPORTING parameter `OUT`**
            JCoTable outTable = function.getExportParameterList().getTable("OUT_DATA");
            System.out.println("total lines of data: " + outTable.getNumRows());
            for (int i = 0; i < outTable.getNumRows(); i++) {
                outTable.setRow(i);

                Material material = new Material();
                material.setMatnr(SAPHelper.removeLeadingZeros(outTable.getString("MATNR")));
                material.setMtart(outTable.getString("MTART"));
                material.setMatkl(outTable.getString("MATKL"));
                material.setMeins(outTable.getString("MEINS"));
                material.setWerks(outTable.getString("WERKS"));
                material.setLgort(outTable.getString("LGORT"));
                material.setMaktx(outTable.getString("MAKTX"));
                material.setUmrez(outTable.getString("UMREZ"));
                material.setUmren(outTable.getString("UMREN"));
                material.setStprs(outTable.getString("STPRS"));
                material.setVerpr(outTable.getString("VERPR"));
                material.setPeinh(outTable.getString("PEINH"));
                material.setWaers(outTable.getString("WAERS"));

                materialDataList.add(material);
            }

            logger.info("Fetched {} materials from SAP.", materialDataList.size());
        } catch (Exception e) {
            logger.error("Error fetching material data: {}", e.getMessage(), e);
        }
        return materialDataList;
    }


    @Override
    public List<Material> getMaterialByMatnr(MaterialRequest materialRequest) {
        List<Material> materialData = handleSapMaterialData(materialRequest);

        if (materialData.isEmpty()) {
            logger.warn("No material data found");
        }
        return materialData;
    }
}
