import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Assignment1.*;
import FileTransceiver.*;

public class Main implements CallBack{
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, CryptoException {   
    	testDES();
    }
    @Override
	public void onResultValueOfProgressBar(int value) {
		System.out.println("% = " + value);
	}

	@Override
	public void onResultComplete() {
		// TODO Auto-generated method stub
		System.out.println("Done");
	}
	
	public static void testDES() throws CryptoException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IOException{
		String inputFile = "C:/Users/Chanh KC Tran/Desktop/test/Lab01.pdf";
        String outputFile = "C:/Users/Chanh KC Tran/Desktop/test/Lab01.encr";
        String decryptedOutput = "C:/Users/Chanh KC Tran/Desktop/test/decrypted/Lab01.pdf";
        //String key = "C:/Users/Chanh KC Tran/Desktop/test/key/DES.key";
        //GenKey genkey = new GenKey();
        //String key = genkey.generate(8);
        
        Main main= new Main();        
        Cryption crypt = new Cryption(main);
        
        crypt.encrypt("DES", "EBC", "C:/Users/Chanh KC Tran/Desktop/test/Lab01.key", new File(inputFile), new File(outputFile), "C:\\Users\\Chanh KC Tran\\Desktop\\test\\rsa\\fileName.prik", "ahiahiah");
        //crypt.exportKey(outputFile);
        //crypt.decrypt("DES", "CBC", key, new File(outputFile), new File(decryptedOutput), null, "ahiahiah");
        System.out.println("Done!");
	}
	
	public static void testRSA() throws NoSuchAlgorithmException{
		RSA rsa = new RSA();
		rsa.generateKeyPair();
		System.out.println(rsa.privateKey);
		System.out.println(rsa.publicKey);
		try {
			rsa.saveKeysToFile("ahihi", "C:\\Users\\Chanh KC Tran\\Desktop\\test\\rsa");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void testRsaReadKeyFile(){
		RSA rsa;
		try {
			rsa = new RSA("C:\\Users\\Chanh KC Tran\\Desktop\\test\\rsa\\fileName.pubk","C:\\Users\\Chanh KC Tran\\Desktop\\test\\rsa\\fileName.prik");
			System.out.println(rsa.publicKey);
			System.out.println(rsa.privateKey);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//rsa.readPublicKeyFromFile("C:\\Users\\Chanh KC Tran\\Desktop\\test\\rsa\\fileName.pubk");
		
	}
	
	public static void testRsaEncrypt() throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IOException{
		RSA rsa = new RSA();
		byte[] a = rsa.encrypt("Chánh đẹp trai","C:\\Users\\Chanh KC Tran\\Desktop\\test\\rsa\\fileName.prik");
		//byte[] c = rsa.decrypt("fvVONRnus6wSW6mqT6XDC1JhQiiCOjZjufPSn9mgOF3yskHZKmXFSoNY8bNfFR9TUn1VYAhRLrbXzuKIePurnuIzRZO2qqRAShgCbiUC+Z+LKYpoxOuPuim8+RNsE9Oeu8aefzPXWVv933BGlWTvFnJ/FhH3MCL3E1p/Z/cATiI=","C:\\Users\\Chanh KC Tran\\Desktop\\test\\rsa\\fileName.pubk");
		System.out.println(rsa.encode64(a));
	}
	public static void testJson() throws UnsupportedEncodingException{
		String b = new Md5().generate("C:/Users/Chanh KC Tran/Desktop/test/Lab01.pdf") + ".";
		System.out.println(b);
		String s = new JSONHandle().toJsonKey("123456781234567812345678", new Md5().generate("C:/Users/Chanh KC Tran/Desktop/test/Lab01.pdf"));
		//s = String.format("%040x", new BigInteger(1, s.getBytes("UTF-8")));
		RSA rsa = new RSA();
		System.out.println(s);
		try {
			String a = rsa.encode64(rsa.encrypt(s, "C:\\Users\\Chanh KC Tran\\Desktop\\test\\rsa\\fileName.pubk"));
			System.out.println(a);
			String c = new String(rsa.decrypt(a, "C:\\Users\\Chanh KC Tran\\Desktop\\test\\rsa\\fileName.prik"));
			System.out.println(c);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | InvalidKeySpecException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public static void testFileTransceiver(){
		ServerSocketGenerator serverThread = ServerSocketGenerator.createSocket(27896,"C:\\Users\\Chanh KC Tran\\Desktop\\test\\file");
		serverThread
		
	}
}
