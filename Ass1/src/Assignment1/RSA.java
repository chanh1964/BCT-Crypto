package Assignment1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.Cipher;
import java.util.Base64;

public class RSA {
	public static PublicKey publicKey;
	public static PrivateKey privateKey;
	private static KeyPair _keyPair;
	public RSA() {
	}
	public RSA(String publicKeyFile, String privateKeyFile) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException{
		readPublicKeyFromFile(publicKeyFile);
		readPrivateKeyFromFile(privateKeyFile);
	}	
	public byte[] encrypt(String input, String publicKeyFile) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, IOException{
		readPublicKeyFromFile(publicKeyFile);
		return encrypt(input);
	}
	public byte[] encrypt(byte[] input, String publicKeyFile) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, IOException{
		readPublicKeyFromFile(publicKeyFile);
		return encrypt(input);
	}
	public byte[] encrypt(String input) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{		
		Cipher cipher;
		cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(input.getBytes());
	}
	public byte[] encrypt(byte[] input) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{		
		Cipher cipher;
		cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(input);		
	}
	public byte[] decrypt(String input, String privateKeyFile) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, IOException{
		readPrivateKeyFromFile(privateKeyFile);
		return decrypt(input);
	}
	public byte[] decrypt(byte[] input, String privateKeyFile) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, IOException{
		readPrivateKeyFromFile(privateKeyFile);
		return decrypt(input);
	}
	public byte[] decrypt(String input) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		return decrypt(decode64(input));
	}
	public byte[] decrypt(byte[] input) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{		
		Cipher cipher;
		cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(input);		
	}
	public String encode64(byte[] input){
		return Base64.getEncoder().encodeToString(input);
	}
	private byte[] decode64(String input){
		return Base64.getDecoder().decode(input);
	}
	public void generateKeyPair() throws NoSuchAlgorithmException{
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(1024);
		_keyPair = kpg.generateKeyPair();
		publicKey = _keyPair.getPublic();
		privateKey = _keyPair.getPrivate();
	}
	public void saveKeysToFile(String fileName, String dir) throws IOException{
		byte[] publicKeyBytes = publicKey.getEncoded();
		byte[] privateKeyBytes = privateKey.getEncoded();
		File publicKeyFile = new File(dir + "\\" + fileName + ".pubk");
		File privateKeyFile = new File(dir + "\\" + fileName + ".prik");
		
		FileOutputStream pubks = new FileOutputStream(publicKeyFile);
		pubks.write(publicKeyBytes);
		pubks.close();
		
		FileOutputStream priks = new FileOutputStream(privateKeyFile);
		priks.write(privateKeyBytes);
		priks.close();				
	}
	
	public void readPublicKeyFromFile(String fileDir) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException{		
		File f = new File(fileDir); 
		byte[] keyBytes = new byte[(int) f.length()];
		FileInputStream fis = new FileInputStream(f);
		fis.read(keyBytes);
		fis.close();
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);				
	}

	public void readPrivateKeyFromFile(String fileDir) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException{		
		File f = new File(fileDir); 
		byte[] keyBytes = new byte[(int) f.length()];
		FileInputStream fis = new FileInputStream(f);
		fis.read(keyBytes);
		fis.close();
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		privateKey = KeyFactory.getInstance("RSA").generatePrivate(spec);	
	}
}
