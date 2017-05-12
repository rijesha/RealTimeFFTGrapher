import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.jtransforms.fft.DoubleFFT_1D;

public class ComplexFftPlotter implements Runnable {
	private int bufferSize;
	private int fftSize;
	private CircularFifoQueue<Double> buffer;
	private Graph graph;
	
	public ComplexFftPlotter(int fftSize){
		this.fftSize = fftSize;
		bufferSize = fftSize*2;
		buffer = new CircularFifoQueue<Double>(bufferSize);
		graph = new Graph(fftSize, 3.90625);
	}
	
	public void addDataPoint(double dp1, double dp2){
		buffer.add(dp1);
		buffer.add(dp2);
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
			double[] fft = performComplexFFT_1D(temp);
			graph.updateSeries(new ComplexFftParser(fft).returnMagnitudeArray());
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private double[] performComplexFFT_1D(double[] input){
		DoubleFFT_1D fftDo = new DoubleFFT_1D(fftSize);
        double[] fft = new double[bufferSize];
        System.arraycopy(input, 0, fft, 0, input.length);
        fftDo.complexForward(fft);

        return fft;
	}
	
}