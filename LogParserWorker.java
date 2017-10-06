package log_parser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Callable;

import log_parser.common.LogFile;
import log_parser.utils.FileUtils;

public class LogParserWorker implements Callable<String> {
	
	String _fileName = null;
	Properties _prop = null;
	
	public LogParserWorker(String fileName, Properties prop) {
		_fileName = fileName;
		_prop = prop;
	}
	
	@Override
	public String call() throws Exception {
		LogFile lf = new LogFile(FileUtils.GetCompressionType(_fileName), _fileName, _prop);	
		_prop.getProperty("outputDir");
		try {			
			lf.LoadLogEntries();
			lf.WriteToFile();
			
		} catch (IOException e) {
			return "Error Parsing Log File: " + _fileName;
		}
			
		return ComposeReturnString(lf);
	}
	
	private String ComposeReturnString(LogFile lf) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();		
		String fileName = lf.GetFileName();
		String totalEntries = String.valueOf(lf.GetLogEntriesCount());
		String redactedEntries = String.valueOf(lf.GetRedactedCount());
		HashMap<String, Integer> map = lf.GetRedactedFieldTypesCount();		
		String redactedFields = "";
		
		for(Entry<String, Integer> entry : map.entrySet()){
			String key = entry.getKey();
			int val = entry.getValue();
			redactedFields += key + "=" + String.valueOf(val) + ", ";
		}

		return String.format("%s Filename: %s Total Lines Processed: %s Lines With Redacted Data: %s Fields Redacted Count: %s", df.format(date), fileName, totalEntries, redactedEntries, redactedFields );
	}
	
	
}
