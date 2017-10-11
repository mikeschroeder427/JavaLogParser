package log_parser.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import log_parser.utils.enums.CompressionType;

public class LogFile {

	private ArrayList<LogEntry> _logEntries = new ArrayList<LogEntry>();
	private CompressionType _compressionType;
	private File _file;
	private Properties _prop;
	
	public LogFile(CompressionType type, String file, Properties prop) throws IOException {
		_compressionType = type;
		_file = new File(file);
		_prop = prop;		
	}
	/**
	 * Reads the log file, and stores log entries in local list.
	 * @throws IOException
	 */
	public void LoadLogEntries() throws IOException {
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(_file))));
	    String line = null;
	    
	    while((line = bufferedReader.readLine()) != null) {
	      this._logEntries.add(processLine(line));
	    }	    	    
	    bufferedReader.close();
	}

	/**
	 	Writes log data to new compressed gz file. If a log entry has redacted data,
	 	the outputted line will have redacted data removed.
	*/
	public void WriteToFile() throws IOException {
		String outputDir = _prop.getProperty("outputDir");
		FileOutputStream output = new FileOutputStream(outputDir + "/" +  _file.getName());
		Writer w = new OutputStreamWriter(new GZIPOutputStream(output));
		
		for (LogEntry l : this._logEntries){		
			
			if (l.HasRedactedData())
				w.write(l.GetRedactedLogLine());
			else
				w.write(l.GetLogLine());
	
			w.write((char)10);
		}	
		w.close();
	}
	/**
	 * 
	 * @return Returns total number of lines containing redacted entries.
	 */
	public int GetRedactedCount(){
		int counter = 0;
		
		for (LogEntry l : _logEntries)
			if (l.HasRedactedData())
				counter++;
		
		return counter;
	} 
	/**
	 * Reads in a log entry line, parses out property and determines 
	 * if the line has redacted data
	 * @param String line of one line.
	 * @return
	 */
	private LogEntry processLine(String line) {
		    return new LogEntry(line, _prop.getProperty("redactedFields"));
	  }
	/**
	 * Number of log entries in log file.
	 * @return
	 */
	public int GetLogEntriesCount(){
		return this._logEntries.size();
	}
	/**
	 * Returns log file name
	 * @return
	 */
	public String GetFileName() {
		return this._file.getName();
	}
	/**
	 * Returns a map of redacted fields and how many times they appeared
	 * in a log file.
	 * @return
	 */
	public HashMap<String, Integer> GetRedactedFieldTypesCount() {
		HashMap<String, Integer> fields = new HashMap<String, Integer>();
		
		for(LogEntry l : this._logEntries) {
			if (l.HasRedactedData()){
				for (LogField f: l.GetFieldsList()){
					if (f.isRedacted)
						fields.put(f.Name, fields.getOrDefault(f.Name, 0) + 1);
				}
			}
		}
		
		return fields;
	}
	
}
