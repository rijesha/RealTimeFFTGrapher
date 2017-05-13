import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class project_main {
	
	private static RealTimeGraph chart1;
	private static RealTimeGraph chart2;
	
	private static FftPlotter realFftPlotter; // = new RealFftPlotter(32);
	private static FftPlotter complexFftPlotter;
	
	private static InputStream in;
	private static byte[] dataPacket = new byte[10];
	private static int realTimeUpdateRatio = 4;

	public static void main(String[] args) {
		SerialPortHandler s = new SerialPortHandler();	
		in = s.getSerialInputStream();

		chart1 = new RealTimeGraph("In Phase Voltage Signal", "Voltage", "");
		chart2 = new RealTimeGraph("In Quadrature Voltage Signal", "Voltage", "");
		
		realFftPlotter = new FftPlotter("Real FFT", "power", "Frequency (Hz)", 32, false);
		complexFftPlotter = new FftPlotter("Real FFT", "power", "Frequency (Hz)", 32, true);
				
		displayGraph(chart1);
		displayGraph(chart2);
		
		displayGraph(realFftPlotter.getGraph());
		displayGraph(complexFftPlotter.getGraph());
		
		
		try {
			s.connect("COM9");
			findHeaderStart();
			startSerialParsing();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
        
	}
	
	private static void displayGraph(JPanel graph){
		JFrame frame = new JFrame("testing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(graph);
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
					
					realFftPlotter.addDataPoint((double) chan1); 
					complexFftPlotter.addDataPoint((double) chan1, (double) chan2); 
					if (realTimeUpdateCounter == realTimeUpdateRatio){
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
}