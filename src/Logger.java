import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

public class Logger implements Runnable, ActionListener{

	private PrintWriter writer;
	private LinkedBlockingQueue<String> writeQueue;
	private boolean shutdown = false;
	private boolean logging = false;
	private Thread thread;
	
	public Logger(boolean start){
		writeQueue = new LinkedBlockingQueue<String>();
		toggleLogging(start);
	}
	
	public void makeNewLog(){
		try{
		    writer = new PrintWriter(new SimpleDateFormat("yyyyMMdd_HHmmss'.radarlog'").format(new Date()), "UTF-8");
		} catch (IOException e) {
		   System.out.println("Failed to make new File");
		}
	}
	
	
	void writeLine(String line){
		try {
			if (logging)
				writeQueue.put(line);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeLogger(){
		shutdown = true;
		while(writeQueue.size() != 0){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		writer.close();
		
	}

	public void toggleLogging(boolean start){
		if (start){
			if (!logging)
				makeNewLog();
				shutdown = false;
				thread = new Thread(this);
		    	thread.start();
			logging = true;
		}
		else{
			logging = false;
			shutdown = true;
		}
	}
	
	@Override
	public void run() {
		while (!shutdown) {
			try {
				writer.println(writeQueue.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while(writeQueue.size() != 0){
			try {
				writer.println(writeQueue.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		toggleLogging(command == "Start Logging");
		System.out.println(e);
	}
	
}
