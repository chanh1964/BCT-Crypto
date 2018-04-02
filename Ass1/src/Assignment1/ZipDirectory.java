/**
* @author Vo Cong Gia Bao
*/

package Assignment1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class ZipDirectory {
	private String inputPath = null;
	private String outputPath = null;
    private ArrayList<String> fileList = null;
    private String parentPath = null;
    

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public ZipDirectory() {}
	
	public ZipDirectory(String inputPath, String outputPath) {
		this.inputPath = inputPath;
		this.outputPath=outputPath;
	}
	
	public boolean zip() throws IOException {
		if (!checkInputPathZip())
			return false;
		if(!checkOutputPathZip())
			return false;
		
		setParentPath(inputPath);
		System.out.println(parentPath);
		
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        ZipOutputStream zos = null;

        fileList = new ArrayList<String>();
		try {
			zos = new ZipOutputStream(new FileOutputStream(this.outputPath));
			genFileAndFolderList(new File(this.inputPath));
			
			for(String unzipped: fileList) {
				System.out.println(parentPath + unzipped);
				ZipEntry entry = new ZipEntry(unzipped);
                zos.putNextEntry(entry);
                // only zip file, not folder
                if(!unzipped.substring(unzipped.length() - 1).equals(File.separator)) {
            		fis = new FileInputStream(parentPath + unzipped);
            		int length;
            		while ((length = fis.read(buffer)) >= 0) {
            			zos.write(buffer, 0, length);
            		}
            		if (fis!=null)
            			fis.close();
                }
			}
		}catch(IOException e) {
			e.printStackTrace();
			return false;
		}finally {
			if(zos != null)
				zos.close();
		}
		return true;
	}
	
	
	public boolean unzip() throws IOException {
        byte[] buffer = new byte[1024];

        ZipInputStream zis = null;
        ZipEntry entry = null;
        FileOutputStream fos = null;

        fileList = new ArrayList<String>();
		
		if(!checkInputPathUnzip())
			return false;
		if(!checkOutputPathUnzip())
			return false;
		updateInOutPaths();
		
		try {
	        File file = new File(this.inputPath);
	        zis = new ZipInputStream(new FileInputStream(file));
	        while((entry = zis.getNextEntry())!= null){
	        	String fileName = entry.getName();
	        	System.out.println(fileName);
	        	if(fileName.substring(fileName.length() - 1).equals(File.separator)) {
	        		String dir = outputPath + fileName;
	        		System.out.println(dir);
	        		new File (dir).mkdirs();
	        	}else {
		            File newFile = new File(outputPath+ File.separator + fileName);
		            fos = new FileOutputStream(newFile);
		            int length;
		            while ((length = zis.read(buffer)) > 0) {
		                fos.write(buffer, 0, length);
		            }
		            fos.close();
	        	}
	        }
		}catch(IOException e) {
			e.printStackTrace();
			return false;
		}finally {
			if (zis != null)
			zis.close();
		}
		return true;
	}
	
	private void genFileAndFolderList(File node) {
		if(node.isFile()) {
			String path = node.getAbsolutePath().toString();
			fileList.add(path.substring(parentPath.length(), path.length()));
		}else if(node.isDirectory()) {
			String dirPath = node.getAbsoluteFile().toString();
			fileList.add(dirPath.substring(parentPath.length(), dirPath.length()) + File.separator);
			String[] subNode = node.list();
			// Travel all node in subNode
			for (String eachNode : subNode){
				System.out.println("File name = " + eachNode);
				genFileAndFolderList(new File(node, eachNode));
			}
		}
		
	}
	

	
	private boolean checkInputPathZip() {
		return Files.exists(Paths.get(inputPath), LinkOption.NOFOLLOW_LINKS);
	}
	
	private boolean checkOutputPathZip() {
		return Files.exists(Paths.get(outputPath).getParent(), LinkOption.NOFOLLOW_LINKS);
	}
	private void setParentPath(String path) {
		parentPath = Paths.get(path).getParent().toString() + File.separator;
	}
	
	
	//----- UNZIP----------------
	private boolean checkInputPathUnzip() {
		return Files.exists(Paths.get(inputPath), LinkOption.NOFOLLOW_LINKS);
	}
	
	private boolean checkOutputPathUnzip() {
		return Files.exists(Paths.get(outputPath), LinkOption.NOFOLLOW_LINKS);
	}
	// update input and output paths to correct format (unzip)
	private void updateInOutPaths() {
		this.inputPath = Paths.get(this.inputPath).toString();
		this.outputPath = Paths.get(this.outputPath).toString() + File.separator;
	}
	
	
    
	
}