package com.example.demo.config;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JcoConfig {
    private static final String DESTINATION_NAME = "ABAP_AS_S19";

    @Bean
    public JCoDestination jCoDestination() throws JCoException {
        return JCoDestinationManager.getDestination(DESTINATION_NAME);
    }
}
