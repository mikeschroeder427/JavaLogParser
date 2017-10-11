package log_parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import log_parser.utils.LogParserUtils;
import log_parser.utils.enums.LogReturnType;

public class LogParserThreadManager {
	private String[] _fNames;
	Properties _prop = null;
	
	public LogParserThreadManager(Properties prop, String[] fNames) {
		_fNames = fNames;
		_prop = prop;
	}
	/**
	 * Creates thread workers. One thread for one log file. 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void StartThreads() throws InterruptedException, ExecutionException  {	
		ExecutorService executor = Executors.newFixedThreadPool(Integer.parseInt(_prop.getProperty("threadPool")));
		List<String> auditList = new ArrayList<String>();
		List<String> errorList = new ArrayList<String>();
		List<Future<LogParserThreadResult>> lstFutures = new ArrayList<Future<LogParserThreadResult>>();
		
		for (String s : _fNames) {
			
			if (new File(s).exists()) {
				Future<LogParserThreadResult> f = executor.submit(new LogParserWorker(s, _prop));
				lstFutures.add(f);
			} else {
				errorList.add(String.format("%s File Does Not Exist: %s", LogParserUtils.GetDate(), s));
			}
		}
		
			// Once a log is finished parsing, it will return an audit log string detailing the results.
			for (Future<LogParserThreadResult> future: lstFutures) {
				if (future.get().ReturnType == LogReturnType.SUCCESS)
					auditList.add(future.get().ReturnText);
				else
					errorList.add(future.get().ReturnText);
			}
	
		executor.shutdown();
		
		while (!executor.isTerminated()){};
		
		WriteToAuditLog(auditList);
		WriteToErrorLog(errorList);
		System.out.println(String.format("Finished Execution, Check the log at %s for details.", _prop.getProperty("auditDir") + _prop.getProperty("auditLog")));
	}
	
	private void WriteToAuditLog(List<String> lst){
		String dir = _prop.getProperty("auditDir");
		String auditLog = _prop.getProperty("auditLog");
		
		FileWriter fw;
		try {
			fw = new FileWriter(dir + auditLog, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			
			for (String s : lst)
				out.println(s);
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void WriteToErrorLog(List<String> lst){
		String dir = _prop.getProperty("errorDir");
		String auditLog = _prop.getProperty("errorLog");
		
		FileWriter fw;
		try {
			fw = new FileWriter(dir + auditLog, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			
			for (String s : lst)
				out.println(s);
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
}
