
public class FftParser {
	private double[] real;
	private double[] imag;
	private double[] mag;
	private double[] fft;
	private int length;
	
	private boolean isComplex;
	
	public FftParser(double[] fft, boolean isComplex){
		this.fft = fft;
		this.isComplex = isComplex;
		length = fft.length;
		
		real = new double[length];
		imag = new double[length];
		mag  = new double[length];
		
		
		if (((fft.length/2) & 1) == 0)
			parseEven();
		else
			parseOdd();
	}

	public  double[] returnMagnitudeArray(){
		for (int i = 0; i < length; i++){
			mag[i] = Math.hypot(real[i], imag[i]);
		}
		return mag;
	}
	
	public  double[] returnMagnitudeLogArray(){
		for (int i = 0; i < length; i++){
			mag[i] = 20* Math.log10(Math.hypot(real[i], imag[i]));
		}
		return mag;
	}
	
	public  Complex[] returnComplexArray(){
		return Complex.arrayToComplexArray(real, imag);
	}
	
	private void parseEven(){
		for(int k = 0; k < length/2; k++){
			real[k] = fft[2*k];
			imag[k] = fft[2*k+1];	
		}
		if (!isComplex)
			real[length/2] = fft[1];
	}
	
	private void parseOdd(){
		System.out.println("ODD has yet to be developed");
	}
	

}
