package log_parser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import log_parser.common.LogFile;
import log_parser.utils.FileUtils;

public class LogParser {

	public static void main(String[] args) throws IOException {
		args = new String[] { System.getProperty("user.dir") +"/src/log_parser/log/Log.txt.gz", 
				System.getProperty("user.dir") +"/src/log_parser/log/Log.txt - Copy.gz",
				System.getProperty("user.dir") +"/src/log_parser/log/Log.txt - Copy (2).gz", 
				System.getProperty("user.dir") +"/src/log_parser/log/Log.txt - Copy (3).gz", 
				System.getProperty("user.dir") +"/src/log_parser/log/Log.txt - Copy (4).gz", 
				System.getProperty("user.dir") +"/src/log_parser/log/Log.txt - Copy (5).gz", 
				System.getProperty("user.dir") +"/src/log_parser/log/Log.txt - Copy (6).gz", 
				System.getProperty("user.dir") +"/src/log_parser/log/Log.txt - Copy (7).gz" };
	      	
		
		//args = new String[] { System.getProperty("user.dir") +"/src/gzip/log/Log.txt.gz"};
	
	    Properties prop = new Properties();
	    InputStream input = new FileInputStream(System.getProperty("user.dir") +"/src/log_parser/config.properties");
	    prop.load(input);
	    
	    if (!ValidateConfig(prop))
	    	return;
	    
	    LogParserThreadManager threadManager = new LogParserThreadManager(prop, args);
	    try {
			threadManager.StartThreads();
		} catch (Exception e) {
			System.out.println("Error in Processing " + e.getMessage());
		}
	}
		
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
			
			if (auditLogFileName == null){
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
