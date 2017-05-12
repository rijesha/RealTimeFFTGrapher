import java.lang.Math;

public class Complex {
	double real;
	double imag;
	
	public Complex(double real, double imag){
		this.real = real;
		this.imag = imag;
	}
	
	public double getMagnitude(){
		return Math.hypot(real, imag);
	}
	
	public static Complex[] arrayToComplexArray(double[] real, double[] imag) {
		int arrlength = real.length;
		Complex[] complexarr = new Complex[arrlength];
		
		for (int n = 0; n < arrlength; n++){
			complexarr[n] = new Complex(real[n], imag[n]); 
		}
		return complexarr;
	}

	@Override
    public String toString()
    {
        String re = this.real+"";
        String im = "";
        if(this.imag < 0)
            im = this.imag+"i";
        else
            im = "+"+this.imag+"i";
        return re+im;
    }
	
}
