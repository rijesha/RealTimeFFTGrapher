import javax.swing.JPanel;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.jtransforms.fft.DoubleFFT_1D;

public class FftPlotter implements Runnable {
	private int bufferSize;
	private boolean isComplex;
	private CircularFifoQueue<Double> buffer;
	private XYGraph graph;
	private DoubleFFT_1D fftDo;
	private double[] fft;
	
	public FftPlotter(String title, String yaxis, String xaxis, int fftSize, boolean isComplex){
		this.isComplex = isComplex;
		fftDo = new DoubleFFT_1D(fftSize);
		bufferSize = isComplex ? fftSize*2 : fftSize;
		buffer = new CircularFifoQueue<Double>(bufferSize);
		fft = new double[bufferSize];
		graph = new XYGraph(title, yaxis, xaxis, fftSize, 3.90625);
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
	        fftDo.complexForward(fft);
			
			graph.updateSeriesYaxis(new FftParser(fft, isComplex).returnMagnitudeArray());
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public JPanel getGraph() {
		return graph;
	}
}