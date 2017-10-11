package log_parser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import log_parser.common.LogFile;
import log_parser.utils.LogParserUtils;

public class LogParser {

	public static void main(String[] args) {
		
		args = new String[]{"ouch", "C:/Users/s003637/workspace/ActOn/src/log_parser/log/Log.txt.gz"};
		if (args.length > 0)
			Init(args);
	}
	/**
	 * Filename of the log file 
	 * @param fileName - Absolute Path
	 */
	public static void ParseLogs(String fileName) {
		if (fileName != null)
			Init(new String[] {fileName});
	}
	/**
	 * Array of log files
	 * @param fileName - Absolute Path
	 */
	public static void ParseLogs(String[] fileNames) {
		if (fileNames.length > 0)
			Init(fileNames);
	}
	
	private static void Init(String[] fileNames) {
		
	    Properties prop = new Properties();
	    try{
	    	URL huh =LogParser.class.getResource("config.properties");
	    	InputStream input = new FileInputStream(new File(huh.getFile()));
	    	prop.load(input);
	    }
	    catch (Exception e) {
	    	System.out.println("Error Loading config.properties. Error: " + e.getMessage());
	    }
	    
	    if (!ValidateConfig(prop))
	    	return;
	    
	    LogParserThreadManager threadManager = new LogParserThreadManager(prop, fileNames);
	    try {
			threadManager.StartThreads();
		} catch (Exception e) {
			System.out.println("Error in Processing: " + e.getMessage());
		}
	}
	
	/**
	 * Performs validation on the properties file. If errors exist in prop file, returns false and ends execution.
	 * @param prop
	 * @return
	 */
	private static boolean ValidateConfig(Properties prop) {
		
		boolean isValidConfig = true;
		
		try {
			Integer.parseInt(prop.getProperty("threadPool"));
		}
		catch (Exception e) {
			System.out.println("threadPool property in config.properties file is invalid. " + e.getMessage());
			isValidConfig = false;
		}
		
		try{
			String outputDir = prop.getProperty("outputDir");
			
			if (outputDir == null){
				System.out.println("Required property outputDir in config.properties is null.");
				isValidConfig = false;
			}
		
			if(!new File(outputDir).isDirectory()){
				System.out.println("outputDir does not point to a valid directory" + outputDir);
				isValidConfig = false;
			}
			
		}
		catch(Exception e) {
			System.out.println("Error validating outputDir property: " + e.getMessage());
			isValidConfig = false;
		}
		
		try {
			String auditDir = prop.getProperty("auditDir");
			
			if (auditDir == null){
				System.out.println("Required property auditDir in config.properties is null.");
				isValidConfig = false;
			}
			
			if(!new File(auditDir).isDirectory()){
				System.out.println("outputDir does not point to a valid directory" + auditDir);
				isValidConfig = false;
			}
			
		}
		catch (Exception e) {
			System.out.println("Error validating auditDir property: " + e.getMessage());
			isValidConfig = false;
		}
		
		try {
			String dir = prop.getProperty("auditDir");
			String auditLogFileName = prop.getProperty("auditLog");
			
			if (auditLogFileName == null || auditLogFileName == "" ){
				System.out.println("Required property auditDir in config.properties is null.");
				isValidConfig = false;
			}
			
			if(!new File(dir + auditLogFileName).exists()){
				System.out.println("Audit Log file cannot be found.");
				isValidConfig= false;
			}
		}
		catch (Exception e) {
			System.out.println("Error validating auditDir property: " + e.getMessage());
			isValidConfig = false;
		}

		try {
			String errorDir = prop.getProperty("errorDir");
			
			if (errorDir == null){
				System.out.println("Required property errorDir in config.properties is null.");
				isValidConfig = false;
			}
			
			if(!new File(errorDir).isDirectory()){
				System.out.println("errorDir does not point to a valid directory" + errorDir);
				isValidConfig = false;
			}
			
		}
		catch (Exception e) {
			System.out.println("Error validating errorDir property: " + e.getMessage());
			isValidConfig = false;
		}
		
		try {
			String dir = prop.getProperty("errorDir");
			String errorLogFileName = prop.getProperty("errorLog");
			
			if (errorLogFileName == null || errorLogFileName == ""){
				System.out.println("Required property errorLog in config.properties is null.");
				isValidConfig = false;
			}
			
			if(!new File(dir + errorLogFileName).exists()){
				System.out.println("Error Log file cannot be found.");
				isValidConfig= false;
			}
		}
		catch (Exception e) {
			System.out.println("Error validating errorLogFileName property: " + e.getMessage());
			isValidConfig = false;
		}
		
		
		try {
			String redactedFields = prop.getProperty("redactedFields");
			
			if (redactedFields == null){
				System.out.println("Required property redactedFields in the config.properties can not be null. (Empty string is ok)");
				isValidConfig= false;
			}
		}
		catch (Exception e){
			System.out.println("Error validating redactedFields. " + e.getMessage());
			isValidConfig= false;
		}
		
		return isValidConfig;
	}
	
	
}
