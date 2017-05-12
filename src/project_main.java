import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JFrame;
import javax.swing.Timer;

import org.jtransforms.fft.DoubleFFT_1D;


public class project_main {
	
	private static RealTimeGraph chart;
	private static RealFftPlotter fftPlotter; // = new RealFftPlotter(32);

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		double[] real = new double[]{0,1,0,-1};
		double[] imag = new double[]{0,1,0,-1};
		
		//double[] fft = performRealFFT_1D(real);
		//Complex[] data = new RealFftParser(fft).returnComplexArray();
        
		
		//double[] cmplx = new double[]{0,1,1,0,0,-1,-1,0};
		double[] cmplx = new double[]{0,0,1,1,0,0,-1,-1};
		//double[] fft = performComplexFFT_1D(cmplx);
		//Complex[] data = new ComplexFftParser(fft).returnComplexArray();
        
		ComplexFftPlotter cfp = new ComplexFftPlotter(4);
		cfp.addDataPoint(0, 1);
		cfp.addDataPoint(1, 0);
		cfp.addDataPoint(0, -1);
		cfp.addDataPoint(-1, 0);
		Thread t = new Thread(cfp);
        t.start();

        /*
        
        for(Complex d: data) {
            System.out.println(d);
        }
        
        makeRealtimeGraph();
        Thread t = new Thread(fftPlotter);
        t.start();
        initSerial();
        */
        
	}
	
	private static double[] performComplexFFT_1D(double[] input){
		DoubleFFT_1D fftDo = new DoubleFFT_1D(input.length/2);
        double[] fft = new double[input.length * 2];
        System.arraycopy(input, 0, fft, 0, input.length);
        fftDo.complexForward(fft);

        return fft;
	}
	
	
	private static void initSerial(){
		SerialPortHandler s = new SerialPortHandler();
		try {
			s.connect("COM9");
			InputStream in = s.getSerialInputStream();
			OutputStream out = s.getSerialOutputStream();

			byte[] one = new byte[1]; 
			
			in.read(one);
			boolean foundStart = false;
			while (foundStart == false){
				byte[] two = new byte[1];
				in.read(two);
				Thread.sleep(10);
				System.out.print(Integer.toHexString(one[0]));
				System.out.print(" ");
				System.out.println(Integer.toHexString(two[0]));
				
				if ((one[0] == 21) && (two[0] == 22))
					foundStart = true;
				one = two;
					
			}
			int lp = 0;
			int i = 0;
			byte[] dataPacket = new byte[10];
			while (i < 100000){
				
				if (in.available() > 10){
					in.read(dataPacket);
					
					i++;
					if (dataPacket[0] == 19 && dataPacket[1] == 20 && dataPacket[8] == 21 && dataPacket[9] == 22 ){
						
						int chan1 = dataPacket[2] << 16 | (dataPacket[3] & 0xff) << 8 | (dataPacket[4] & 0xff);;
						fftPlotter.addDataPoint((double) chan1); 
						if (lp > 4){
							//System.out.println(chan1);
							chart.update((float) chan1);
							lp = 0;
						}
						lp++;
						
					}
				}
				
				Thread.sleep(1);
				
			}
			
			
			
		} catch (IOException | InterruptedException e) {
			System.out.println("Failed to Open");
			e.printStackTrace();
		}
	}
	
	private static void makeRealtimeGraph(){
		JFrame frame = new JFrame("testing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chart = new RealTimeGraph("In Phase Analog", " ","Voltage");
        frame.add(chart);
        frame.pack();
        frame.setVisible(true);
	}

	private static void startTimer(){
		Timer timer = new Timer(8, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        chart.update((float) (Math.random() * 10));
                    }
                });
            }
        });
        timer.start();
	}
	

	
	private static double[] performRealFFT_1D(double[] input){
		DoubleFFT_1D fftDo = new DoubleFFT_1D(input.length);
        double[] fft = new double[input.length * 2];
        System.arraycopy(input, 0, fft, 0, input.length);
        fftDo.realForwardFull(fft);

        return fft;
		
	}
	
}