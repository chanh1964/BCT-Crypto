package Assignment1;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;


public class Md5 {
	public String generate(String filePath){
		File file = new File(filePath);
		return this.generate(file);
	}
	public String generate(File file) {
		try {
			//File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			MessageDigest md = MessageDigest.getInstance("MD5");
			int nread = 0;
			byte[] dataBytes = new byte[1024];
			while((nread = fis.read(dataBytes)) != -1){
				md.update(dataBytes,0,nread);
			}
		    byte[] digest = md.digest();
		    
			StringBuffer result = new StringBuffer();
		    for (byte b : digest) {
		        //result.append(String.format("%02X ", b));
		    	result.append(Integer.toString((b & 0xff)+0x100,16).substring(1));
		    }
		    fis.close();
		    return result.toString();
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	public boolean match(File a, File b){
		String aa = generate(a);
		String bb = generate(b);
		if(aa.equals(bb)){
			return true;
		}
		return false;
	}
}
