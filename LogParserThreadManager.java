package log_parser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LogParserThreadManager {
	private String[] _fNames;
	Properties _prop = null;
	
	public LogParserThreadManager(Properties prop, String[] fNames) {
		_fNames = fNames;
		_prop = prop;
	}
	
	public void StartThreads() throws InterruptedException, ExecutionException {	
		ExecutorService executor = Executors.newFixedThreadPool(Integer.parseInt(_prop.getProperty("threadPool")));
		List<String> auditList = new ArrayList<String>();
		List<Future<String>> lstFutures = new ArrayList<Future<String>>();
		
		for (String s : _fNames) {
			Future<String> f = executor.submit(new LogParserWorker(s, _prop));
			lstFutures.add(f);
		}
		
		for (Future<String> future: lstFutures)
			auditList.add(future.get());
				
		executor.shutdown();
		
		while (!executor.isTerminated()){};
		
		WriteToAuditLog(auditList);
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
	
}
