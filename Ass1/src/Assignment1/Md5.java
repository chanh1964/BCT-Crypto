package Assignment1;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
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
			 
			DigestInputStream dis = new DigestInputStream(fis, md);
		    byte[] digest = md.digest();
		    
			StringBuffer result = new StringBuffer();
		    for (byte b : digest) {
		        result.append(String.format("%02X ", b));
		    }
		    
		    dis.close();
		    fis = null;
		    file = null;
		    md = null;
		    dis = null;
		    digest = null;
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
