package log_parser.common;
import java.util.ArrayList;

public class LogEntry {
	
	private String _logLine;
	private String _redactedLogLine;
	private String _logDate;	
	private String _logTime;
	private long _account;	
	private String _action;	
	private long _recordNumber;	
	private boolean _hasRedactedData = false;
	private String _redactedString = "";	
	private ArrayList<LogField> _fields = new ArrayList<LogField>();
	/**
	 * LogEntry represents a single log line (row) in a log file.
	 * @param Log Entry Line
	 * @param Data that will be redacted, CSV format
	 */
	public LogEntry(String line, String redactedData){	
		_logLine = line;
		_redactedString = redactedData;
		ParseLine(line);
	}
	/**
	 * Parses a log line, and will check for redacted data
	 * If redacted data is found, the redacted data will be removed
	 * and placed into a redactedString for audit output.
	 * @param line
	 */
	private void ParseLine(String line) {
		String[] split = line.split(" ");
		_logDate = split[0].trim();
		_logTime = split[1].trim();
		_account = Long.parseLong(split[3].trim());
		_action = split[4].trim() + " " + split[5].replaceAll(":", "").trim();
		_recordNumber = Long.parseLong(split[6].trim());
		
		if (!line.contains("Fields:"))
			return;
		
		String fields = line.substring(line.indexOf("Fields:", 0) + "Fields:".length()).trim();
		String[] arrFields = fields.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		
		for (String s: arrFields) {
			LogField f = new LogField();
			String name = s.split("=")[0].trim();
			String val = s.split("=")[1].trim();
			
			// If log entry contains redacted data, the log entry will be 
			// flagged for further parsing
			if (_redactedString.contains(name)) {
				this._hasRedactedData = true;
				f.isRedacted = true;
			}
					
			f.Name = name;
			f.Value = val;		
			_fields.add(f);
		}			
		
		if (this._hasRedactedData)
			ParseRedactedLogLine();
		
	}	
	/**
	 * Removes redacted data from log line.
	 */
	private void ParseRedactedLogLine() {
		String logLine = this._logLine;
		String tmpLine = " ";
		
		for (LogField f : this._fields) 
			if (!f.isRedacted) 
				tmpLine = tmpLine.concat(f.Name + "=" + f.Value + ", ");
		
		if (tmpLine.trim() != "")
			tmpLine = tmpLine.substring(0, tmpLine.length()-2);
		
		logLine = logLine.substring(0, logLine.indexOf("Fields:") + 7);
		
		this._redactedLogLine = logLine + tmpLine;	
	}
	/**
	 * Checks if LogEntry contains redacted data.
	 * @return
	 */
	public boolean HasRedactedData() {
		return _hasRedactedData;
	}
	/**
	 * Returns raw log line
	 * @return
	 */
	public String GetLogLine(){
		return this._logLine;
	}
	
	/**
	 * Returns list of fields in the log line.
	 * @return
	 */
	public ArrayList<LogField> GetFieldsList() {
		return this._fields;
	}
	/**
	 * Returns redacted log line.
	 * @return
	 */
	public String GetRedactedLogLine(){
		return this._redactedLogLine;
	}
}
