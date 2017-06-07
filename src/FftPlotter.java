import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.jtransforms.fft.DoubleFFT_1D;

public class FftPlotter implements Runnable, ChangeListener{
	private int bufferSize;
	private boolean isComplex;
	private CircularFifoQueue<Double> buffer;
	private XYGraph graph;
	private DoubleFFT_1D fftDo;
	private double[] fft;
	private double SAMPLINGFREQ;
	
	public FftPlotter(String title, String yaxis, String xaxis, int samplingFreq, int fftSize, boolean isComplex){
		SAMPLINGFREQ = samplingFreq;
		this.isComplex = isComplex;
		graph = new XYGraph(title, yaxis, xaxis, fftSize, 3.90625, this);
		changeSampleSize(fftSize);
	}
	
	public void changeSampleSize(int fftSize){
		fftDo = new DoubleFFT_1D(fftSize);
		bufferSize = isComplex ? fftSize*2 : fftSize;
		buffer = new CircularFifoQueue<Double>(bufferSize);
		fft = new double[bufferSize*2];
		graph.updateSeries(fftSize, SAMPLINGFREQ/((double)fftSize));		
	}
	
	public void addDataPoint(double... dps){
		for (double dp : dps) {
			buffer.add(dp);
		}
	}

	@Override
	public void run() {
		while (!buffer.isAtFullCapacity()) {
			System.out.println("Not yet Full");
		}
		double[] temp = new double[bufferSize];
		int i;
		while (true){
			i = 0;
			for (Double d : buffer){
				temp[i] = (double) d;
				i++;
			}
			System.arraycopy(temp, 0, fft, 0, temp.length);
			if (isComplex)
				fftDo.complexForward(fft);
			else
				fftDo.realForwardFull(fft);
			
			//FftParser fp = new FftParser(fft, isComplex);
			//Complex[] cp = fp.returnComplexArray();
			graph.updateSeriesYaxis(new FftParser(fft, isComplex).returnMagnitudeArray());
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public void stateChanged(ChangeEvent e) {
	    JSlider source = (JSlider)e.getSource();
	    if (!source.getValueIsAdjusting()) {
	        int fps = (int)source.getValue();
	        System.out.println(fps);
	    }
	}
	
	public JPanel getGraph() {
		return graph;
	}
}