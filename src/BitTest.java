
public class BitTest {
	
	public static void main(String[] args) {
		byte byte0 = 0x00;
		byte byte1 = 0x09;
		byte byte2 = (byte) 0x81;
		
		int val = (byte0 & 0xff) << 16 | (byte1 & 0xff) << 8 | (byte2 & 0xff);
		System.out.println(val);

		
	}
	
	


	
}
