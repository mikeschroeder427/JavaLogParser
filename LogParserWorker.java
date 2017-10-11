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
import log_parser.utils.LogParserUtils;
import log_parser.utils.enums.LogReturnType;

public class LogParserWorker implements Callable<LogParserThreadResult> {
	
	String _fileName = null;
	Properties _prop = null;
	
	public LogParserWorker(String fileName, Properties prop) {
		_fileName = fileName;
		_prop = prop;
	}
	
	@Override
	public LogParserThreadResult call() throws Exception {
		
		LogFile lf;
		LogParserThreadResult res = new LogParserThreadResult();
		_prop.getProperty("outputDir");
		
		try {
			lf = new LogFile(LogParserUtils.GetCompressionType(_fileName), _fileName, _prop);
			lf.LoadLogEntries();
			lf.WriteToFile();			
		} catch (Exception e) {
			res.ReturnText = String.format("%s FileName %s Error parsing file: %s", LogParserUtils.GetDate(), _fileName,  e.getMessage());
			res.ReturnType = LogReturnType.ERROR;
			return res;
		}
					
		res.ReturnText = ComposeReturnString(lf);
		res.ReturnType = LogReturnType.SUCCESS;
		
		return res;
	}
	/**
	 * Generates an auditLog string with number of redacted fields, and a count of individual redacted fields.
	 * @param lf
	 * @return
	 */
	private String ComposeReturnString(LogFile lf) {
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

		return String.format("%s Filename: %s Total Lines Processed: %s Lines With Redacted Data: %s Redacted Fields Count: %s", LogParserUtils.GetDate(), fileName, totalEntries, redactedEntries, redactedFields );
	}
	
}
