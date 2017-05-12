import org.jtransforms.fft.DoubleFFT_1D;

public class onedimensionFFT {
	
	
	
	
	public double[] onedimensionFFT(double[] input){
		DoubleFFT_1D fftDo = new DoubleFFT_1D(input.length);
        double[] fft = new double[input.length * 2];
        System.arraycopy(input, 0, fft, 0, input.length);
        fftDo.realForward(fft);
        
		return fft;
	}

}
