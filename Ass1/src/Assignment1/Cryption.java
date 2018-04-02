package Assignment1;
 
import java.io.File;
import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.json.simple.JSONObject;

import Assignment1.GenKey;
import Assignment1.RSA;

public class Cryption {
	private CallBack callBack;
	private String _keyString;
	private RSA _rsa = new RSA();
	public Cryption(CallBack callBack){
		this.callBack = callBack;
	}	
    public  void encrypt(String algorithm,String mode,String key, File inputFile, File outputFile, String RSAKeyFile, String initVector) throws CryptoException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        doCrypto(algorithm, mode, Cipher.ENCRYPT_MODE, key, inputFile, outputFile, RSAKeyFile, initVector);
    }
 
    public  void decrypt(String algorithm,String mode,String key, File inputFile, File outputFile, String RSAKeyFile, String initVector)
            throws CryptoException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        doCrypto( algorithm, mode, Cipher.DECRYPT_MODE, key, inputFile, outputFile, RSAKeyFile, initVector);
    }
 
    private  void doCrypto(String algorithm,String mode,int cipherMode, String key, File inputFile,File outputFile,String RSAKeyFile, String initVector) throws CryptoException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
    	try {
    			if (mode.equals("CBC")){
	    			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes());
	    			boolean writeFlag = false;	    			
	    			Key secretKey;
	    			if (key == null && cipherMode == Cipher.ENCRYPT_MODE){
	    				GenKey g = new GenKey();
	    				_keyString = g.generate(algorithm);
	    				secretKey = new SecretKeySpec(_keyString.getBytes(), algorithm);
	    				writeFlag = true;
	    				if(RSAKeyFile != null){
	    					_rsa.readPublicKeyFromFile(RSAKeyFile);
	    				}
	    				else{
	    					_rsa.generateKeyPair();
	    					_rsa.saveKeysToFile(removeExtension(outputFile.getName()), outputFile.getParent());
	    				}
	    			}
	    			else{
						File keyFile = new File(key);
						if(keyFile.exists() && !keyFile.isDirectory()){
							FileInputStream fis = new FileInputStream(keyFile);
			    			byte[] keyBytes = new byte[(int) keyFile.length()];
			    			fis.read(keyBytes);			    			
			    			_keyString = new String(_rsa.decrypt(keyBytes, RSAKeyFile));
			    			secretKey = new SecretKeySpec(_keyString.getBytes(), algorithm);
			    			fis.close();			    			
						}	    			
						else{
							_keyString = key;
							secretKey = new SecretKeySpec(_keyString.getBytes(), algorithm);
							if(cipherMode == Cipher.ENCRYPT_MODE && RSAKeyFile != null){
								_rsa.readPublicKeyFromFile(RSAKeyFile);
							}
						}
	    			}
	    			String algo=algorithm+"/CBC/PKCS5Padding";
	    			Cipher cipher = Cipher.getInstance(algo);
	    			cipher.init(cipherMode, secretKey,iv);
	    			FileInputStream inputStream = new FileInputStream(inputFile);
	    			CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
	    			FileOutputStream outputStream = new FileOutputStream(outputFile);
	    			int temp = (int) (inputFile.length()/100);
	    			if(temp == 0) temp = 1;
	    			long count = 0;
	    		    byte[] buffer = new byte[1024];
	    		    int bytesRead = 0;
	    		    while((bytesRead = cipherInputStream.read(buffer))!=-1){
						outputStream.write(buffer, 0, bytesRead);
						count+=bytesRead;
						callBack.onResultValueOfProgressBar((int) (count/temp));
					}   
	    	
	    			callBack.onResultValueOfProgressBar(100);
	    			callBack.onResultComplete();
	    			outputStream.close();
	    			cipherInputStream.close();
	    			outputStream.close();
	    			if(writeFlag){
	    				exportKey(outputFile);
	    			}
             }
    		else {
    			boolean writeFlag = false;
    			RSA rsa = new RSA();
    			Key secretKey;
    			if (key == null && cipherMode == Cipher.ENCRYPT_MODE){
    				GenKey g = new GenKey();
    				_keyString = g.generate(algorithm);
    				secretKey = new SecretKeySpec(_keyString.getBytes(), algorithm);
    				writeFlag = true;
    				if(RSAKeyFile != null){
    					_rsa.readPublicKeyFromFile(RSAKeyFile);
    				}
    				else{
    					_rsa.generateKeyPair();
    					_rsa.saveKeysToFile(removeExtension(outputFile.getName()), outputFile.getParent());
    				}
    			}
    			else{
					File keyFile = new File(key);
					if(keyFile.exists() && !keyFile.isDirectory()){
						FileInputStream fis = new FileInputStream(keyFile);
		    			byte[] keyBytes = new byte[(int) keyFile.length()];
		    			fis.read(keyBytes);			    			
		    			_keyString = new String(rsa.decrypt(keyBytes, RSAKeyFile));
		    			secretKey = new SecretKeySpec(_keyString.getBytes(), algorithm);
		    			fis.close();			    			
					}	    			
					else{
						_keyString = key;
						secretKey = new SecretKeySpec(_keyString.getBytes(), algorithm);
						if(cipherMode == Cipher.ENCRYPT_MODE && RSAKeyFile != null){
							_rsa.readPublicKeyFromFile(RSAKeyFile);
						}
					}
    			}
    			String algo=algorithm+"/ECB/PKCS5Padding";
    			Cipher cipher = Cipher.getInstance(algo);
    			
    			cipher.init(cipherMode, secretKey);
             
    			FileInputStream inputStream = new FileInputStream(inputFile);
    			CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
    			FileOutputStream outputStream = new FileOutputStream(outputFile);
    			int temp = (int) (inputFile.length()/100);
    			if(temp == 0) temp = 1;
    			System.out.println(temp);
    			long count = 0;
    		    byte[] buffer = new byte[1024];
    		    int bytesRead;
    		    while((bytesRead = cipherInputStream.read(buffer))!=-1)
    		      {
    		        outputStream.write(buffer, 0, bytesRead);
    		        count+=bytesRead;
    		        callBack.onResultValueOfProgressBar((int) (count/temp));
    		      }   
    		    
    			callBack.onResultValueOfProgressBar(100);
    			callBack.onResultComplete();
    			outputStream.close();
    			cipherInputStream.close();
    			outputStream.close();
    			if(writeFlag){
    				exportKey(outputFile);
    			}
    		}
			
    		
    	} catch (NoSuchPaddingException | 
    			NoSuchAlgorithmException| InvalidKeyException 
    			 | IOException|InvalidAlgorithmParameterException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
        
    }
    private String removeExtension(String s){
    	if(s == null) return s;
    	int pos = s.lastIndexOf(".");
    	if (pos == -1) return s;
    	return s.substring(0, pos);
    }
    public void clearKey(){
    	_keyString = null;
    }
    private File getKeyFile(File outputFile){
    	return	new File(outputFile.getParent()+"\\"+removeExtension(outputFile.getName())+".key");
    }
    public void exportKey(File link) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException{
    	FileOutputStream fos = new FileOutputStream(getKeyFile(link));
    	fos.write(_rsa.encrypt(_keyString));
    	fos.close();
    }
    public void exportKey(String link) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException{
    	exportKey(new File(link));
    }
}