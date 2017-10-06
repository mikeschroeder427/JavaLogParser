package log_parser.utils;

import log_parser.utils.enums.CompressionType;

public class FileUtils {

	public static CompressionType GetCompressionType(String fileName){
		String ext = fileName.substring(fileName.lastIndexOf("."));
		switch (ext.toLowerCase()){
			case ".gz":
				return CompressionType.GZIP;
			default:
				return null;					
		}
	}	
}
