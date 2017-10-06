package log_parser.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import log_parser.utils.enums.CompressionType;

public abstract class LogReader {

	private File _fileToBeRead = null;
	private CompressionType _compressionType;
	
	public LogReader(String fileName, CompressionType cType) {
		_fileToBeRead = new File(fileName);	
		_compressionType = cType;
	}
	
	public LogFile ParseLogFile() throws IOException { 
	    return null;
	}
	
	
	
}
