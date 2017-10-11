package log_parser.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import log_parser.utils.enums.CompressionType;
/**
 * Can be built out in future for additional compression types.
 * @author s003637
 *
 */
public class LogParserUtils {

	public static CompressionType GetCompressionType(String fileName){
		String ext = fileName.substring(fileName.lastIndexOf("."));
		switch (ext.toLowerCase()){
			case ".gz":
				return CompressionType.GZIP;
			default:
				return null;					
		}
	}	
	
	public static String GetDate(){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();		
		return df.format(date);
	}
	
}
