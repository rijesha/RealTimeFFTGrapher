import java.lang.Math;

public class RealFftParser {
	private double[] real;
	private double[] imag;
	private double[] mag;
	private double[] fft;
	private int n;
	
	public RealFftParser(double[] fft){
		this.fft = fft;
		n = fft.length;
		
		real = new double[n];
		imag = new double[n];
		mag  = new double[n];
		
		
		if (((fft.length/2) & 1) == 0)
			parseEven();
		else
			parseOdd();
	}

	public  double[] returnMagnitudeArray(){
		for (int i = 0; i < n; i++){
			mag[i] = Math.hypot(real[i], imag[i]);
		}
		return mag;
	}
	
	public  double[] returnMagnitudeLogArray(){
		for (int i = 0; i < n; i++){
			mag[i] = 20* Math.log10(Math.hypot(real[i], imag[i]));
		}
		return mag;
	}
	
	public  Complex[] returnComplexArray(){
		return Complex.arrayToComplexArray(real, imag);
	}
	
	private void parseEven(){
		for(int k = 0; k < n/2; k++){
			real[k] = fft[2*k];
			imag[k] = fft[2*k+1];	
		}
		real[n/2] = fft[1];
	}
	
	private void parseOdd(){
		//for(int k = 0; k < (n+1)/2; k++){
		//	real[k] = fft[2*k];
	//	}
		System.out.println("ODD has yet to be developed");

//		complex[k] = fft[2*k];	
	//	real[n/2] = fft[1];
	}
	

}
