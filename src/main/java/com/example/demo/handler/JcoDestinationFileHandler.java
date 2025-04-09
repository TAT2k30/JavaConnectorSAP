package com.example.demo.handler;

import com.sap.conn.jco.ext.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

@Component
public class JcoDestinationFileHandler {
    private static final String DESTINATION_FILE_PATH = "ABAP_AS_S19.jcoDestination";

    public JcoDestinationFileHandler() {

    }

    public String createDestinationFile(String userName, String password, String clientCode) throws Exception {
        Properties properties = new Properties();
        properties.setProperty("jco.client.ashost", "10.1.1.19");
        properties.setProperty("jco.client.sysnr", "00");
        properties.setProperty("jco.client.client", clientCode);
        properties.setProperty("jco.client.user", userName);
        properties.setProperty("jco.client.passwd", password);
        properties.setProperty("jco.client.lang", "EN");

        File file = new File(DESTINATION_FILE_PATH);

        if (file.exists()) {
            file.delete();
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            properties.store(fos, "SAP JCo Configuration");
        }
        return DESTINATION_FILE_PATH;
    }
}
