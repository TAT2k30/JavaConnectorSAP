package com.example.demo;

import com.sap.conn.jco.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) throws JCoException {
//
//		JCoDestination destination = JCoDestinationManager.getDestination("ABAP_AS_S19");
//
//		JCoFunction function = destination.getRepository().getFunction("ZFM_JAVA_CONNECT_249");
//
//		if (function == null) {
//			throw new RuntimeException("Function" + function.getName() + " not exits");
//		}
//
//		function.getImportParameterList().setValue("IN", "Hello from Spring");
//
//		try{
//			function.execute(destination);
//		}
//		catch (AbapException e){
//			e.printStackTrace();
//			return;
//		}
//
//		String out = function.getExportParameterList().getString("OUT");
//		System.out.println("out = " + out );
		SpringApplication.run(DemoApplication.class, args);
	}

}
