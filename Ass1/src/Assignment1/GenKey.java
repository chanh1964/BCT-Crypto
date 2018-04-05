package Assignment1;

import java.nio.charset.Charset;
import java.util.Random;

public class GenKey {
	public String generate(int length) {
	    byte[] array = new byte[length];
	    for (int i=0; i<length; ++i){
	    	int j = new Random().nextInt(94) + 33;
	    	array[i] = (byte) j;
	    }
	    String generatedString = new String(array, Charset.forName("UTF-8"));
	    return generatedString;
	}
	public String generate(String algorithm){
		if(algorithm.equals("DES")){
			return generate(8);
		}
		else if(algorithm.equals("TripleDES")){
			return generate(24);
		}
		else if(algorithm.equals("AES")){
			return generate(16);
		}
		return null;
	}
	public boolean Export(String content){
		return true;
	}
}
