package gui;
import java.io.File;
import java.io.IOException;

import main.ZipDirectory;
import main.CallBack;
import main.Cryption;
import main.CryptoException;
import main.GenKey;
import main.Md5;
public class Main implements CallBack {
    public static void main(String[] args) {

        //String fileDirectory = "E:/User/Desktop/Test";
        String inputPath = "E:/User/Desktop/b.EXE";
        String outputPath = "E:/User/Desktop/mm";

        Main main = new Main();
        
        Cryption cryption = new Cryption(main);
        File input = new File(inputPath);
        File output = new File(outputPath);
        try {
			cryption.decrypt("DES", "ECB", "E:/User/Desktop/key_des.txt", output, input, null);
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        GenKey genkey = new GenKey();
//        System.out.println(genkey.generate("AES"));
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
}
