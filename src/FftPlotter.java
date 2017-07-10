import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.jtransforms.fft.DoubleFFT_1D;

import java.util.concurrent.Semaphore;

public class FftPlotter implements Runnable, ChangeListener{
	private Semaphore bufferLock;
	private int bufferSize;
	private boolean isComplex;
	private CircularFifoQueue<Double> buffer;
	private XYGraph graph;
	private DoubleFFT_1D fftDo;
	private double[] fft;
	private double SAMPLINGFREQ;
	private boolean changedSampleSize = false;
	private double[] temp;
	private int paddedSize = 128;
	private int paddedFFTSize;
	
	public FftPlotter(String title, String yaxis, String xaxis, int samplingFreq, int fftSize, boolean isComplex, Semaphore calendarLock){
		bufferLock = new Semaphore(1);
		SAMPLINGFREQ = samplingFreq;
		this.isComplex = isComplex;
		graph = new XYGraph(title, yaxis, xaxis, fftSize, 3.90625, this, calendarLock);
		changeSampleSize(fftSize);
	}
	
	public void changeSampleSize(int fftSize){
		try {
			bufferLock.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fftDo = new DoubleFFT_1D(paddedSize);
		paddedFFTSize = isComplex ? paddedSize*2 : paddedSize;

		bufferSize = isComplex ? fftSize*2 : fftSize;
		buffer = new CircularFifoQueue<Double>(bufferSize);
		
		fft = new double[paddedFFTSize*2];
		graph.updateSeries(fftSize, SAMPLINGFREQ/((double)fftSize));
		changedSampleSize = true;
		bufferLock.release();
	}
	
	public void addDataPoint(double... dps){
		for (double dp : dps) {
			buffer.add(dp);
		}
	}

	@Override
	public void run() {
		while (!buffer.isAtFullCapacity()) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Buffer Not yet Full");
		}
		while (true){
			try {
				bufferLock.acquire();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			temp = new double[bufferSize];
			int i;
			double avg = 0;
			
			for (i = 0; i < buffer.size(); i++) {
				try { 
					temp[i] = (double) buffer.get(i);
					//avg = avg + temp[i];
				} catch (Exception e) {
					System.out.println("Indexing Error");
					System.out.println(temp.length);
					System.out.println(buffer.size());
					System.out.println(i);
				}
			}
			
			for (; i < paddedFFTSize; i++) {
				try { 
					temp[i] = 0;
					//avg = avg + temp[i];
				} catch (Exception e) {
					System.out.println("Indexing Error");
					System.out.println(temp.length);
					System.out.println(buffer.size());
					System.out.println(i);
				}
			}
			
			
			/*avg = avg/temp.length;

			for (int j=0; j<temp.length; j++) {
				temp[j] = temp[j] - avg;
			}*/
			
			bufferLock.release();
			System.arraycopy(temp, 0, fft, 0, temp.length);
			if (isComplex)
				fftDo.complexForward(fft);
			else
				fftDo.realForwardFull(fft);
			
			/*
			for (int j=0; j<fft.length; j++) {
				fft[j] = 20 * java.lang.Math.log10(fft[j]);
			}*/

			//FftParser fp = new FftParser(fft, isComplex);
			//Complex[] cp = fp.returnComplexArray();
			graph.updateSeriesYaxis(new FftParser(fft, isComplex).returnMagnitudeArray());
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (changedSampleSize){
				temp = new double[bufferSize];				
				changedSampleSize = false;
			}
		}
		
	}

	public void stateChanged(ChangeEvent e) {
	    JSlider source = (JSlider)e.getSource();
	    if (!source.getValueIsAdjusting()) {
	        int fps = (int)source.getValue();
	        System.out.println(fps);
	        if (fps > 0) {
				changeSampleSize(fps);
	        	System.out.println("Changed FPS");
	        }
	    }
	}
	
	public JPanel getGraph() {
		return graph;
	}
}
