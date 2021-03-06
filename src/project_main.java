import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.cli.*;
import java.util.concurrent.Semaphore;


import java.util.Random;

public class project_main {
	private static Semaphore calendarLock = new Semaphore(1);
	
	private static Logger logger;
	private static boolean loggerStart;

	private static boolean disableGUI;
	private static boolean enablePipe = false;
	private static String comPort = "COM9";

	private static RealTimeGraph chart1;
	private static RealTimeGraph chart2;
	
	private static FftPlotter realFftPlotter; // = new RealFftPlotter(32);
	private static FftPlotter complexFftPlotter;
	
	private static int SAMPLINGFREQ = 125;
			
	private static InputStream in;
	private static byte[] dataPacket = new byte[10];
	private static int REALTIMEUPDATERATIO = 13;
	
	private static final double VREF = 4.033;
	private static final int MAXADC = 8388607;
	private static double interval = VREF/MAXADC; 

	public static void main(String[] args) {
		System.out.println(interval);
		parseCLI(args);
		logger = new Logger(loggerStart);

		SerialPortHandler s = new SerialPortHandler();	
		
		chart1 = new RealTimeGraph("In Phase Voltage Signal", "Voltage", "", calendarLock);
		chart2 = new RealTimeGraph("In Quadrature Voltage Signal", "Voltage", "", calendarLock);
		
		realFftPlotter = new FftPlotter("Real FFT", "power", "Frequency (Hz)", SAMPLINGFREQ, 4, false, calendarLock);
		complexFftPlotter = new FftPlotter("Complex FFT", "power", "Frequency (Hz)", SAMPLINGFREQ, 4, true, calendarLock);
		
		Thread fftreal = new Thread(realFftPlotter);
		fftreal.start();

		Thread fftcomplex = new Thread(complexFftPlotter);
		fftcomplex.start();

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (!disableGUI)
			newFrame();

		if (!enablePipe) {
			try {
				fakeData();
				System.out.println("OPENING SERIAL PORT");
				s.connect(comPort);
				in = s.getSerialInputStream();
				System.out.println(in);
				System.out.println(comPort);
				findHeaderStart();
				startSerialParsing();
			} catch (Exception e) {
				System.out.println("Failed to Open Serial Port");
				e.printStackTrace();
				fakeData();
			}
		}
		else {
			System.out.println("Piping Data");
			pipedData();			
		}	        
        
	}
	
	private static void parseCLI(String[] args) {
		Options options = new Options();

        Option input = new Option("c", "disable_gui", false, "disable the gui");
        options.addOption(input);

        Option output = new Option("l", "start_logging", false, "enabling logging on startup");
        options.addOption(output);

        Option serialport = new Option("d", "serial_device_port", false, "location of serial device port");
        options.addOption(serialport);

		Option piping = new Option("p", "enable_pipe", false, "Pipe information from System.in");
        options.addOption(piping);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Real Time FFT Grapher", options);

            System.exit(1);
            return;
        }

        disableGUI = cmd.hasOption("disable_gui");
		loggerStart = cmd.hasOption("start_logging");
		enablePipe = cmd.hasOption("enable_pipe");

		if (cmd.hasOption("serial_device_port"))
			comPort = cmd.getOptionValue("serial_device_port");
	}

	
	private static void newFrame(){
		JFrame frame = new JFrame("Fancy Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container pane = frame.getContentPane();
		
        frame.setSize(50, 75);
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.ipady = 25;      //make this component tall
		c.ipadx = 25;      //make this component tall
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;
		c.weighty = 1;

		c.gridx = 0;
		c.gridy = 0;
		pane.add(chart1, c);

		c.gridx = 1;
	 	pane.add(chart2, c);

		c.gridx = 0;
		c.gridy = 1;
		pane.add(realFftPlotter.getGraph(), c);

		c.gridx = 1;
		pane.add(complexFftPlotter.getGraph(), c);

				
		frame.pack();
		frame.setVisible(true);
	}
	
	private static void findHeaderStart() throws IOException, InterruptedException{
		byte[] one = new byte[1]; 
		byte[] two = new byte[1];
		boolean foundStart = false;
		
		System.out.println("LOOKING FOR HEADER");
		in.read(one);
		while (foundStart == false){

			
			one[0] = two[0];
			in.read(two);
			Thread.sleep(2);
			if ((one[0] == 21) && (two[0] == 22))
				foundStart = true;
			System.out.println("LOOKING FOR HEADER");
			System.out.println(one[0]);
			System.out.println(two[0]);
		}
	}
	
	private static void startSerialParsing() throws IOException, InterruptedException{
		int realTimeUpdateCounter = 0;
		
		while (true){
			if (in.available() > 10){
				in.read(dataPacket);
				if (dataPacket[0] == 19 && dataPacket[1] == 20 && dataPacket[8] == 21 && dataPacket[9] == 22 ){
					int chan1 = dataPacket[2] << 16 | (dataPacket[3] & 0xff) << 8 | (dataPacket[4] & 0xff);;
					int chan2 = dataPacket[5] << 16 | (dataPacket[6] & 0xff) << 8 | (dataPacket[7] & 0xff);;
					
					if (loggerStart)
						logger.writeLine(String.valueOf(System.currentTimeMillis()) + " " +  chan1 + " " + chan2);
					
		//			realFftPlotter.addDataPoint((double) chan1); 
		//			complexFftPlotter.addDataPoint((double) chan1, (double) chan2); 
					if (realTimeUpdateCounter == REALTIMEUPDATERATIO){
						chart1.update((float) chan1);
						chart2.update((float) chan2);
						realTimeUpdateCounter = 0;
					}
					realTimeUpdateCounter++;	
				}
				else
					findHeaderStart();
			}
			Thread.sleep(1);
		}
	}

	private static void pipedData() {
		int realTimeUpdateCounter = 0;
		InputStreamReader isReader = new InputStreamReader(System.in);
		BufferedReader bufReader = new BufferedReader(isReader);
		String text = null;
		double chan1 = 0;
		double chan2 = 0;
		
		while (true){
			try {
				text = bufReader.readLine();
				String[] t = text.split("\\s+");
				chan1 = Double.valueOf(t[1]); //*interval;
				chan2 = Double.valueOf(t[2]); //*interval;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("No input");
			}

			realFftPlotter.addDataPoint((double) chan1); 
			complexFftPlotter.addDataPoint((double) chan1, (double) chan2); 
		
			if (realTimeUpdateCounter == REALTIMEUPDATERATIO){
				chart1.update((float) chan1);
			    chart2.update((float) chan2);						
				realTimeUpdateCounter = 0;
			}
			realTimeUpdateCounter++;
			
			try {
				Thread.sleep(7);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	private static void fakeData() {
		int realTimeUpdateCounter = 0;
		Random rn = new Random();

		while (true){
			int chan1 = rn.nextInt();
			int chan2 = rn.nextInt();
					
			if (loggerStart)
				logger.writeLine(String.valueOf(System.currentTimeMillis()) + " " +  chan1 + " " + chan2);
					
	//		realFftPlotter.addDataPoint((double) chan1); 
	//`		complexFftPlotter.addDataPoint((double) chan1, (double) chan2); 
			
			try {
				Thread.sleep(8);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (realTimeUpdateCounter == REALTIMEUPDATERATIO){
				chart1.update((float) chan1);
				chart2.update((float) chan2);
				realTimeUpdateCounter = 0;
			}
			realTimeUpdateCounter++;
			try {
				Thread.sleep(8);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

/*	private static void testFFT(){
		realFftPlotter.addDataPoint(0); 
		realFftPlotter.addDataPoint(1); 
		realFftPlotter.addDataPoint(0); 
		realFftPlotter.addDataPoint(-1); 
		
		complexFftPlotter.addDataPoint(0); 
		complexFftPlotter.addDataPoint(0); 
		complexFftPlotter.addDataPoint(1); 
		complexFftPlotter.addDataPoint(1); 
		complexFftPlotter.addDataPoint(0); 
		complexFftPlotter.addDataPoint(0); 
		complexFftPlotter.addDataPoint(-1); 
		complexFftPlotter.addDataPoint(-1); 
		
	}*/

}
