import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.jtransforms.fft.DoubleFFT_1D;

public class RealFftPlotter implements Runnable {
	private double[] data;
	private int size;
	private boolean dataReady = false;
	private CircularFifoQueue<Double> buffer;
	private Graph graph;
	
	public RealFftPlotter(int size){
		this.size = size;
		buffer = new CircularFifoQueue<Double>(size);
		graph = new Graph(size, 3.90625);
	}
	
	public void addDataPoint(double dp){
		buffer.add(dp);
	}

	@Override
	public void run() {
		while (!buffer.isAtFullCapacity()) {
			System.out.println("Not yet Full");
		}
		double[] temp = new double[size];
		int i;
		while (true){
			i = 0;
			for (Double d : buffer){
				temp[i] = (double) d;
				i++;
			}
			double[] fft = performRealFFT_1D(temp);
			graph.updateSeries(new RealFftParser(fft).returnMagnitudeArray());
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private static double[] performRealFFT_1D(double[] input){
		DoubleFFT_1D fftDo = new DoubleFFT_1D(input.length);
        double[] fft = new double[input.length * 2];
        System.arraycopy(input, 0, fft, 0, input.length);
        fftDo.realForwardFull(fft);

        return fft;
		
	}
}