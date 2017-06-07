import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class project_main {
	private static Logger logger;
	private static boolean loggerStart;
	
	private static RealTimeGraph chart1;
	private static RealTimeGraph chart2;
	
	private static FftPlotter realFftPlotter; // = new RealFftPlotter(32);
	private static FftPlotter complexFftPlotter;
	
	private static int SAMPLINGFREQ = 125;
			
	private static InputStream in;
	private static byte[] dataPacket = new byte[10];
	private static int REALTIMEUPDATERATIO = 4;

	private static JButton startButton = new JButton("Start Logging");
	private static JButton stopButton = new JButton("Stop Logging");
	
	public static void main(String[] args) {
		logger = new Logger();
		
		SerialPortHandler s = new SerialPortHandler();	
		in = s.getSerialInputStream();

		chart1 = new RealTimeGraph("In Phase Voltage Signal", "Voltage", "");
		chart2 = new RealTimeGraph("In Quadrature Voltage Signal", "Voltage", "");
		
		realFftPlotter = new FftPlotter("Real FFT", "power", "Frequency (Hz)", SAMPLINGFREQ, 4, false);
		complexFftPlotter = new FftPlotter("Complex FFT", "power", "Frequency (Hz)", SAMPLINGFREQ, 4, true);
		
		Thread fftreal = new Thread(realFftPlotter);
		fftreal.start();

		Thread fftcomplex = new Thread(complexFftPlotter);
		fftcomplex.start();


		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//testFFT();
		
		
		newFrame();
		try {
			s.connect("COM9");
			findHeaderStart();
			startSerialParsing();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		        
        
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

		
		JPanel panel = new JPanel();
		
		startButton.addActionListener(logger);
		panel.add(startButton);
		
		stopButton.addActionListener(logger);
		panel.add(stopButton);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 1;       //reset to default
		c.weighty = 1;   //request any extra vertical space
		c.anchor = GridBagConstraints.PAGE_END; //bottom of space
		c.gridx = 0;
		c.gridy = 2;       //third row
		pane.add(panel, c);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	private static void findHeaderStart() throws IOException, InterruptedException{
		byte[] one = new byte[1]; 
		byte[] two = new byte[1];
		boolean foundStart = false;
		
		in.read(one);
		while (foundStart == false){
			one = two;
			in.read(two);
			Thread.sleep(2);
			if ((one[0] == 21) && (two[0] == 22))
				foundStart = true;
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
					
					realFftPlotter.addDataPoint((double) chan1); 
					complexFftPlotter.addDataPoint((double) chan1, (double) chan2); 
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

	private static void testFFT(){
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
		
	}

}
