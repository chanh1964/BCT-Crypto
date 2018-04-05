package BCTCrypto;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
//import com.jgoodies.forms.factories.DefaultComponentFactory;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
//import javax.swing.JLayeredPane;
//import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
//import java.awt.Dialog.ModalExclusionType;
import java.awt.SystemColor;
import javax.swing.JTextField;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;

//import java.awt.GridBagLayout;
//import java.awt.GridLayout;
import javax.swing.SwingConstants;
//import javax.swing.JMenu;
//import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
//import javax.swing.JMenuBar;
import javax.swing.JProgressBar;
import javax.swing.JComboBox;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import javax.print.DocFlavor.URL;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;

//import java.awt.Label;
import java.awt.TextField;
import javax.swing.JToggleButton;
import javax.swing.JButton;
//import javax.swing.JInternalFrame;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
//import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileNameExtensionFilter;



import Assignment1.*;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
//import javax.swing.JSplitPane;

public class BCTCryptoGUI{

	private JFrame frmBctcrypto;
	private TextField encryptionInput;
	private TextField encryptionOutput;
	private TextField decryptionInput;
	private TextField decryptionOutput;
	private TextField encRsaPrivateKey;
	private TextField decRsaPrivateKey;
	private JComboBox algorithm;
	private JComboBox mode;
	private TextField key;
	private TextField initVector;
	private JToggleButton advancedMode;
	private JCheckBox encRsaPrivateKeyDefault;
	private JCheckBox decRsaPrivateKeyDefault;
	private static JProgressBar encProgressBar;
	private static JProgressBar decProgressBar;
	private static JButton EncryptBtn;
	private static JButton DecryptBtn;
	private JLabel EncStatus;	
	private JLabel DecStatus;
	private Thread encryptThread;
	private Thread decryptThread;
	
	private static final String DEF_PUB_KEY = System.getProperty("user.dir")+"\\resources\\rsa\\def.pubk";
	private static final String DEF_PRI_KEY = System.getProperty("user.dir")+"\\resources\\rsa\\def.prik";
	private static final String ZIP_TEMP_DIR=System.getProperty("user.dir")+"\\temp\\";
	private static final String ZIP_TEMP_FILE = System.getProperty("user.dir")+"\\temp\\temp.zip";
	private static final BCTCryptoGUI window = new BCTCryptoGUI();
	private static final Cryption _crypt = new Cryption(new CallBack(){
		@Override
		public void onResultValueOfProgressBar(int value) {
			// TODO Auto-generated method stub					
		}
		@Override
		public void onResultComplete() {
			// TODO Auto-generated method stub
		}
	});
	private static Cryption _encrypt = new Cryption(new CallBack(){
		@Override
		public void onResultValueOfProgressBar(int value) {
			// TODO Auto-generated method stub
			if( value != encProgressBar.getValue()){
				encProgressBar.setValue(value);	
			}				
		}
		@Override
		public void onResultComplete() {
			// TODO Auto-generated method stub
			EncryptBtn.setEnabled(true);
		}
	});
	private static Cryption _decrypt = new Cryption(new CallBack(){
		@Override
		public void onResultValueOfProgressBar(int value) {
			// TODO Auto-generated method stub
			if( value != encProgressBar.getValue()){
				decProgressBar.setValue(value);	
			}
		}
		@Override
		public void onResultComplete() {
			// TODO Auto-generated method stub
			//DecryptBtn.setEnabled(true);
		}
		
	});
	private JTextField md5Result;
	private JTextField md5ChkInputStr;
	//private File encOutput;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {					
					window.frmBctcrypto.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @wbp.parser.entryPoint
	 */
	public BCTCryptoGUI() {
		initialize();
	}
	
	private boolean KeyIsOk(String algorithm, String key){		
		return (algorithm.equals("AES") && key.length() == 16)? true:
			   (algorithm.equals("DES") && key.length() == 8)? true:
			   (algorithm.equals("TripleDES") && key.length() == 24)? true:false;
	}
	
	private boolean EncParameterIsOk(String algorithm, String inputFile, String outputFolder, String key, String mode, String initVector, String RSAKeyFile){
		return ParameterIsOk(algorithm, inputFile, outputFolder, key, mode, initVector, RSAKeyFile)
				&& EncInputFileIsOk(inputFile, outputFolder, key, initVector, RSAKeyFile);
	}
	private boolean EncInputFileIsOk(String inputFile, String outputFolder, String key, String initVector, String RSAKeyFile){
		File input = new File(inputFile);
		File output = new File(outputFolder);		
		if(!input.exists()){
			JOptionPane.showMessageDialog(null,"Cannot find input file/folder!");
			return false;
		}
		else if(!output.exists()){
			JOptionPane.showMessageDialog(null,"Cannot find output folder!");
			return false;
		}
		else if(RSAKeyFile != null){
			File RSA = new File(RSAKeyFile);
			if(!RSA.exists() || RSA.isDirectory()){		
				JOptionPane.showMessageDialog(null,"Cannot find RSA Private Key!");
				return false;
			}
		}
		else if(advancedMode.isSelected() && !encRsaPrivateKeyDefault.isSelected()){
			if(RSAKeyFile.equals("")){
				JOptionPane.showMessageDialog(null,"Please insert RSA Private Key!");
				return false;
			}			
			else if(!_crypt.getExtension(RSAKeyFile).equals("prik")){
				JOptionPane.showMessageDialog(null,"Please insert supported RSA Private Key (.prik)!");
				return false;
			}
		}		
		return true;
	}
	private boolean DecParameterIsOk(String algorithm, String inputFile, String outputFolder, String key, String mode, String initVector, String RSAKeyFile){
		return ParameterIsOk(algorithm, inputFile, outputFolder, key, mode, initVector, RSAKeyFile)
				&& DecInputFileIsOk(inputFile, outputFolder, key, initVector, RSAKeyFile);
	}
	private boolean DecInputFileIsOk(String inputFile, String outputFolder, String key, String initVector, String RSAKeyFile){
		File input = new File(inputFile);
		File output = new File(outputFolder);		
		if(!input.exists()){
			JOptionPane.showMessageDialog(null,"Cannot find input file!");
			return false;
		}
		else if(!_crypt.getExtension(inputFile).toLowerCase().equals("encr")){
			JOptionPane.showMessageDialog(null,"Please insert the right encrypted file extension (.encr)!");
			return false;
		}
		else if(!output.exists()){
			JOptionPane.showMessageDialog(null,"Cannot find output folder!");
			return false;
		}
		else if(RSAKeyFile != null){
			File RSA = new File(RSAKeyFile);
			if(!RSA.exists() || RSA.isDirectory()){		
				JOptionPane.showMessageDialog(null,"Cannot find RSA Private Key!");
				return false;
			}
		}
		else if(advancedMode.isSelected() && !decRsaPrivateKeyDefault.isSelected()){
			if(RSAKeyFile.equals("")){
				JOptionPane.showMessageDialog(null,"Please insert RSA Private Key!");
				return false;
			}			
			else if(!_crypt.getExtension(RSAKeyFile).equals("prik")){
				JOptionPane.showMessageDialog(null,"Please insert supported RSA Private Key (.prik)!");
				return false;
			}
		}		
		return true;
	}
	private boolean ParameterIsOk(String algorithm, String inputFile, String outputFolder, String key, String mode, String initVector, String RSAKeyFile){
		if(inputFile.equals("")){
			JOptionPane.showMessageDialog(null,"Please choose your input!");
			return false;
		}
		else if(outputFolder.equals("")){
			JOptionPane.showMessageDialog(null,"Please choose output folder!");
			return false;
		}
		else if(key.equals("")){
			JOptionPane.showMessageDialog(null,"Please insert your key!");
			return false;
		}
		else if(!advancedMode.isSelected() && !KeyIsOk(algorithm, key)){
			JOptionPane.showMessageDialog(null,"Please insert the right key length for " + algorithm + " algorithm!");
			return false;
		}
		else if(advancedMode.isSelected()){
			if(!_crypt.getExtension(key).toUpperCase().equals(algorithm.toUpperCase())){
				JOptionPane.showMessageDialog(null,"Please insert the right key extension for " + algorithm + " algorithm!");
				return false;
			}
			else if(mode.equals("CBC")){
				if(initVector.equals("")){
					JOptionPane.showMessageDialog(null,"Please insert initVector for CBC mode!");
					return false;
				}		
				File ivF = new File(initVector);
				//if(ivF.exists())
				if(!ivF.exists() && !KeyIsOk(algorithm,initVector) && !algorithm.equals("TripleDES")){
					JOptionPane.showMessageDialog(null,"Please insert the right Init Vector length for " + algorithm + " algorithm!");
					return false;
				}
				if(!ivF.exists() && !KeyIsOk("DES",initVector) && algorithm.equals("TripleDES")){
					JOptionPane.showMessageDialog(null,"Please insert the right Init Vector length for " + algorithm + " algorithm!");
					return false;
				}
				else if(ivF.exists() && !_crypt.getExtension(initVector).toUpperCase().equals(algorithm.toUpperCase())){
					JOptionPane.showMessageDialog(null,"Please insert the right Init Vector extension for " + algorithm + " algorithm!");
					return false;
				}
			}				
		}						
		return true;
	}
	
	private void EncryptBtnHandle(){
		
		String _algorithm = algorithm.getSelectedItem().toString();
		String _mode = mode.getSelectedItem().toString();
		String _key = key.getText();
		String _inputFile = encryptionInput.getText();
		String _outputFolder = encryptionOutput.getText();
		String _RSAKeyFile = (!advancedMode.isSelected())? null :
							 (decRsaPrivateKeyDefault.isSelected())? DEF_PRI_KEY : decRsaPrivateKey.getText();
		String _initVector = initVector.getText();
		//_crypt = new Cryption(window);
		if(EncParameterIsOk(_algorithm,_inputFile,_outputFolder,_key,_mode,_initVector,_RSAKeyFile)){
			encryptThread = new Thread(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub		
					Boolean error = false;
					try {					
						File input = new File(_inputFile);
						String output = _outputFolder+"\\"+input.getName();
						if(input.isDirectory()){
							ZipDirectory zip = new ZipDirectory(_inputFile,ZIP_TEMP_FILE);
							EncStatus.setText("Compressing Folder...");
							if(zip.zip()){
								output += ".folder";
								input = new File(ZIP_TEMP_FILE);
							}
							EncStatus.setText("Encrypting...");
						}
						File encOutput = new File(output+".encr");
						encProgressBar.setValue(0);					
						_encrypt.encrypt(_algorithm, _mode, _key, input, encOutput, _RSAKeyFile, _initVector);
						
					} 
					catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
							| InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException
							| InvalidAlgorithmParameterException | IOException |NullPointerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						JOptionPane.showMessageDialog(null,"An Error Occurred!");
						//EncStatus.setText("An Error Occurred!");
						error = true;
						EncryptBtn.setEnabled(true);
					}
					finally{
						File t = new File(ZIP_TEMP_FILE);
						if(t.exists()){
							t.delete();
						}
						EncStatus.setText((error)? "An Error Occurred" : "Encryption Success!");
					}
				}				
			});	
			EncryptBtn.setEnabled(false);
			encryptThread.start();
		}
	}
	
	private void DecryptBtnHandle(){
		String _algorithm = algorithm.getSelectedItem().toString();
		String _mode = mode.getSelectedItem().toString();
		String _key = key.getText();
		String _inputFile = decryptionInput.getText();
		String _outputFolder = decryptionOutput.getText();
		String _RSAKeyFile = (!advancedMode.isSelected())? null :
							 (encRsaPrivateKeyDefault.isSelected())? DEF_PRI_KEY : encRsaPrivateKey.getText();
		String _initVector = initVector.getText();
		if(DecParameterIsOk(_algorithm,_inputFile,_outputFolder,_key,_mode,_initVector,_RSAKeyFile)){
			decryptThread = new Thread(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Boolean error = false;
					try {
						File input = new File(_inputFile);
						//String slash = (_outputFolder.endsWith("\\"))? "\\":
							//		   (_outputFolder.endsWith("/"))? "\\":"";
						File output = new File(_outputFolder + "\\" + _crypt.removeExtension(input.getName())); 
						DecStatus.setText("Decrypting...");
						_decrypt.decrypt(_algorithm, _mode, _key, input, output, _RSAKeyFile, _initVector);
						if(_crypt.getExtension(output.getName()).equals("folder")){
							File f = new File(_crypt.removeExtension(output.getAbsolutePath())+".zip");
							output.renameTo(f);
							ZipDirectory zip = new ZipDirectory(f.getAbsolutePath(),_outputFolder);
							DecStatus.setText("Decompressing Folder...");
							if(zip.unzip()){								
								f.delete();
							}
						}
						decProgressBar.setValue(100);
							
					} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
							| InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException
							| InvalidAlgorithmParameterException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						JOptionPane.showMessageDialog(null,"An Error Occurred!");
						error = true;
						DecryptBtn.setEnabled(true);
						decProgressBar.setValue(0);
					} finally{
						DecStatus.setText((error)? "An Error Occurred" : "Decryption Success!");
						DecryptBtn.setEnabled(true);						
					}
				}
				
			});
			DecryptBtn.setEnabled(false);
			decryptThread.start();
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmBctcrypto = new JFrame();		
		try {
			frmBctcrypto.setIconImage(ImageIO.read(new File(System.getProperty("user.dir")+"/resources/logo_s.png")));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		frmBctcrypto.setResizable(false);
		frmBctcrypto.setForeground(SystemColor.window);
		frmBctcrypto.setBackground(SystemColor.textHighlightText);
		frmBctcrypto.setTitle("BCT Crypto v1.2");
		frmBctcrypto.setBounds(100, 100, 545, 400);
		frmBctcrypto.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);			
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmBctcrypto.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel encryptDecryptPanel = new JPanel();
		encryptDecryptPanel.setBackground(SystemColor.control);
		tabbedPane.addTab("Encrypt/Decrypt", null, encryptDecryptPanel);
		tabbedPane.setEnabledAt(0, true);
		tabbedPane.setBackgroundAt(0, Color.WHITE);
		tabbedPane.setForegroundAt(0, new Color(0, 0, 0));
		encryptDecryptPanel.setLayout(null);
		
		JLabel AlgoLabel = new JLabel("Algorithm");
		AlgoLabel.setBounds(11, 47, 81, 20);
		encryptDecryptPanel.add(AlgoLabel);
		
		algorithm = new JComboBox();
		algorithm.setModel(new DefaultComboBoxModel(new String[] {"AES", "DES", "TripleDES"}));
		algorithm.setBounds(73, 47, 90, 20);
		encryptDecryptPanel.add(algorithm);
		
		JPanel advancedOnly = new JPanel();
		advancedOnly.setBounds(0, 69, 539, 37);
		encryptDecryptPanel.add(advancedOnly);
		advancedOnly.setLayout(null);
		advancedOnly.setVisible(false);
		
		JLabel ModeLabel = new JLabel("Mode of Op.");
		ModeLabel.setBounds(10, 7, 81, 20);
		advancedOnly.add(ModeLabel);
				
		
		JLabel InitVectorLabel = new JLabel("InitVector");
		InitVectorLabel.setBounds(172, 6, 61, 20);
		advancedOnly.add(InitVectorLabel);
		
		initVector = new TextField();
		initVector.setEnabled(false);
		initVector.setBounds(235, 7, 188, 20);
		advancedOnly.add(initVector);
		
		JButton InitVectorBrowse = new JButton("Browse");
		InitVectorBrowse.setEnabled(false);
		InitVectorBrowse.setBounds(429, 7, 102, 20);
		advancedOnly.add(InitVectorBrowse);
		//InitVectorBrowse.setVisible(false);
		InitVectorBrowse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				String algo = algorithm.getSelectedItem().toString();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.setDialogTitle("Choose "+ algo + " Init Vector File");
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.addChoosableFileFilter(new FileNameExtensionFilter(algo + " Init Vector File (." + algo.toLowerCase() + ")",
																	   algo.toLowerCase()));
				if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					initVector.setText(jfc.getSelectedFile().getAbsolutePath());
				}
			}
			
		});
		
		JLabel KeyLabel = new JLabel("Key");
		KeyLabel.setBounds(174, 47, 26, 20);
		encryptDecryptPanel.add(KeyLabel);
		
		key = new TextField();
		key.setBounds(204, 47, 220, 20);
		encryptDecryptPanel.add(key);
		
		mode = new JComboBox();
		mode.setModel(new DefaultComboBoxModel(new String[] {"ECB", "CBC"}));
		mode.setBounds(88, 7, 74, 20);
		advancedOnly.add(mode);
		mode.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if("CBC".equals(mode.getSelectedItem())){
					initVector.setEnabled(true);
					InitVectorBrowse.setEnabled(true);
				}
				else{
					initVector.setEnabled(false);
					InitVectorBrowse.setEnabled(false);
				}
			}
			
		});
		
		JButton KeyBrowse = new JButton("Browse");
		KeyBrowse.setEnabled(false);
		KeyBrowse.setBounds(430, 47, 102, 20);
		encryptDecryptPanel.add(KeyBrowse);
		KeyBrowse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				String algo = algorithm.getSelectedItem().toString();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.setDialogTitle("Choose "+ algo + " Key File");
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.addChoosableFileFilter(new FileNameExtensionFilter(algo + " Key File (." + algo.toLowerCase() + ")",
																	   algo.toLowerCase()));
				if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					key.setText(jfc.getSelectedFile().getAbsolutePath());
				}
			}
			
		});
		
		JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_1.setBounds(0, 115, 539, 230);
		encryptDecryptPanel.add(tabbedPane_1);
		
		JPanel EncryptPanel = new JPanel();
		tabbedPane_1.addTab("Encrypt", null, EncryptPanel, null);
		EncryptPanel.setLayout(null);
		
		JLabel EncryptionInput = new JLabel("Input File/Folder");
		EncryptionInput.setBounds(10, 38, 92, 20);
		EncryptPanel.add(EncryptionInput);
		
		encryptionInput = new TextField();
		encryptionInput.setBounds(108, 38, 314, 20);
		EncryptPanel.add(encryptionInput);
		encryptionInput.setColumns(10);
		
		JButton EncryptionInputBrowse = new JButton("Browse");
		EncryptionInputBrowse.setBounds(430, 38, 94, 20);
		EncryptPanel.add(EncryptionInputBrowse);
		EncryptionInputBrowse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				jfc.setDialogTitle("Choose file/folder that needs to be encrypted");
				if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					encryptionInput.setText(jfc.getSelectedFile().getAbsolutePath());
				}
				
			}
			
		});
		
		JLabel EncryptionOutput = new JLabel("Output Folder");
		EncryptionOutput.setBounds(10, 69, 92, 20);
		EncryptPanel.add(EncryptionOutput);
		
		encryptionOutput = new TextField();
		encryptionOutput.setColumns(10);
		encryptionOutput.setBounds(108, 69, 314, 20);
		EncryptPanel.add(encryptionOutput);

		EncStatus = new JLabel("");
		EncStatus.setForeground(new Color(255, 0, 0));
		EncStatus.setBounds(108, 135, 138, 23);
		EncryptPanel.add(EncStatus);
		
		JButton EncryptionOutputBrowse = new JButton("Browse");
		EncryptionOutputBrowse.setBounds(430, 69, 94, 20);
		EncryptPanel.add(EncryptionOutputBrowse);
		EncryptionOutputBrowse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				jfc.setDialogTitle("Choose output folder");
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					encryptionOutput.setText(jfc.getSelectedFile().getAbsolutePath());
				}
				
			}
			
		});
		
		EncryptBtn = new JButton("Encrypt");
		EncryptBtn.setBounds(8, 135, 89, 23);
		EncryptPanel.add(EncryptBtn);
		EncryptBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				EncryptBtnHandle();
			}
			
		});
		
		JLabel lblEncryptMode = new JLabel("Encrypt Mode");
		lblEncryptMode.setForeground(new Color(255, 0, 0));
		lblEncryptMode.setBounds(10, 11, 100, 14);
		EncryptPanel.add(lblEncryptMode);
		
		JPanel EncryptPublicKeyPanel = new JPanel();
		EncryptPublicKeyPanel.setBounds(10, 98, 524, 27);
		EncryptPanel.add(EncryptPublicKeyPanel);
		EncryptPublicKeyPanel.setLayout(null);
		EncryptPublicKeyPanel.setVisible(false);
		
		JLabel EncRSAPrivateKeyLabel = new JLabel("RSA Private Key");
		EncRSAPrivateKeyLabel.setBounds(0, 0, 92, 20);
		EncryptPublicKeyPanel.add(EncRSAPrivateKeyLabel);
		
		encRsaPrivateKey = new TextField();
		encRsaPrivateKey.setEnabled(false);
		encRsaPrivateKey.setColumns(10);
		encRsaPrivateKey.setBounds(98, 0, 156, 20);
		EncryptPublicKeyPanel.add(encRsaPrivateKey);
		
		JButton EncRSAPrivateKeyBrowse = new JButton("Browse");
		EncRSAPrivateKeyBrowse.setEnabled(false);
		EncRSAPrivateKeyBrowse.setBounds(262, 0, 94, 20);
		EncryptPublicKeyPanel.add(EncRSAPrivateKeyBrowse);
		EncRSAPrivateKeyBrowse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.setDialogTitle("Choose RSA Private Key File");
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.addChoosableFileFilter(new FileNameExtensionFilter("RSA Private Key File (.prik)","prik"));
				if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					encRsaPrivateKey.setText(jfc.getSelectedFile().getAbsolutePath());
				}
			}
			
		});
		
		encRsaPrivateKeyDefault = new JCheckBox("Use Default Private Key");
		encRsaPrivateKeyDefault.setSelected(true);
		encRsaPrivateKeyDefault.setBounds(363, 0, 160, 23);
		EncryptPublicKeyPanel.add(encRsaPrivateKeyDefault);		
		encRsaPrivateKeyDefault.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				boolean set = !encRsaPrivateKeyDefault.isSelected();
				EncRSAPrivateKeyBrowse.setEnabled(set);
				encRsaPrivateKey.setEnabled(set);
			}
			
		});
		
		encProgressBar = new JProgressBar(0,100);
		encProgressBar.setStringPainted(true);
		encProgressBar.setForeground(new Color(0, 255, 0));
		encProgressBar.setBounds(10, 165, 514, 23);
		EncryptPanel.add(encProgressBar);
		
		JButton EncPauBtn = new JButton("Pause");		
		EncPauBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
		EncPauBtn.setBounds(256, 135, 89, 23);
		EncryptPanel.add(EncPauBtn);
		EncPauBtn.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(encryptThread!=null && encryptThread.isAlive()){
					encryptThread.suspend();
					EncStatus.setText("Encryption Paused");
				}
				
			}			
		});
		
		JButton EncConBtn = new JButton("Continue");
		EncConBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
		EncConBtn.setBounds(346, 135, 89, 23);
		EncryptPanel.add(EncConBtn);
		EncConBtn.addActionListener(new ActionListener() {			
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(encryptThread!=null && encryptThread.isAlive()){
					encryptThread.resume();
					EncStatus.setText("Encrypting...");
				}
				
			}			
		});
		
		JButton EncStopBtn = new JButton("Cancel");
		EncStopBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
		EncStopBtn.setBounds(436, 135, 89, 23);
		EncryptPanel.add(EncStopBtn);		
		EncStopBtn.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(encryptThread!=null && encryptThread.isAlive()){
					encryptThread.stop();
					encProgressBar.setValue(0);
					EncryptBtn.setEnabled(true);
					EncStatus.setText("Encryption Cancelled");				
				}
				
			}			
		});
		
		JPanel DecryptPanel = new JPanel();
		tabbedPane_1.addTab("Decrypt", null, DecryptPanel, null);
		DecryptPanel.setLayout(null);
		
		JLabel DecryptionInputLabel = new JLabel("Input .ENCR File");
		DecryptionInputLabel.setBounds(10, 38, 92, 20);
		DecryptPanel.add(DecryptionInputLabel);
		
		decryptionInput = new TextField();
		decryptionInput.setColumns(10);
		decryptionInput.setBounds(108, 38, 314, 20);
		DecryptPanel.add(decryptionInput);
		
		JButton DecryptionInputBrowse = new JButton("Browse");
		DecryptionInputBrowse.setBounds(430, 38, 94, 20);
		DecryptPanel.add(DecryptionInputBrowse);
		DecryptionInputBrowse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.setDialogTitle("Choose file that needs to be decrypted");
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.addChoosableFileFilter(new FileNameExtensionFilter("Encrypted File (.encr)","encr"));
				if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					decryptionInput.setText(jfc.getSelectedFile().getAbsolutePath());
				}
			}
			
		});
		
		JLabel DecryptionOutputLabel = new JLabel("Output Folder");
		DecryptionOutputLabel.setBounds(10, 69, 92, 20);
		DecryptPanel.add(DecryptionOutputLabel);
		
		decryptionOutput = new TextField();
		decryptionOutput.setColumns(10);
		decryptionOutput.setBounds(108, 69, 314, 20);
		DecryptPanel.add(decryptionOutput);
		
		JButton DecryptionOutputBrowse = new JButton("Browse");
		DecryptionOutputBrowse.setBounds(430, 69, 94, 20);
		DecryptPanel.add(DecryptionOutputBrowse);
		DecryptionOutputBrowse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.setDialogTitle("Choose output folder");
				if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					decryptionOutput.setText(jfc.getSelectedFile().getAbsolutePath());
				}
			}
			
		});
		
		DecryptBtn = new JButton("Decrypt");
		DecryptBtn.setBounds(8, 135, 89, 23);
		DecryptPanel.add(DecryptBtn);
		DecryptBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				DecryptBtnHandle();
			}
			
		});
		
		JLabel lblDecryptMode = new JLabel("Decrypt Mode");
		lblDecryptMode.setForeground(new Color(255, 0, 0));
		lblDecryptMode.setBounds(10, 11, 100, 14);
		DecryptPanel.add(lblDecryptMode);
		
		JPanel DecryptPrivateKeyPanel = 	new JPanel();
		DecryptPrivateKeyPanel.setBounds(10, 98, 524, 29);
		DecryptPanel.add(DecryptPrivateKeyPanel);
		DecryptPrivateKeyPanel.setLayout(null);
		DecryptPrivateKeyPanel.setVisible(false);
		
		JLabel DecRsaPrivateKeyLabel = new JLabel("RSA Private Key");
		DecRsaPrivateKeyLabel.setBounds(0, 0, 92, 20);
		DecryptPrivateKeyPanel.add(DecRsaPrivateKeyLabel);
		
		decRsaPrivateKey = new TextField();
		decRsaPrivateKey.setEnabled(false);
		decRsaPrivateKey.setColumns(10);
		decRsaPrivateKey.setBounds(98, 0, 156, 20);
		DecryptPrivateKeyPanel.add(decRsaPrivateKey);
		
		JButton DecRsaPrivateKeyBrowse = new JButton("Browse");
		DecRsaPrivateKeyBrowse.setEnabled(false);
		DecRsaPrivateKeyBrowse.setBounds(262, 0, 94, 20);
		DecryptPrivateKeyPanel.add(DecRsaPrivateKeyBrowse);
		DecRsaPrivateKeyBrowse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.setDialogTitle("Choose RSA Private Key File");
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.addChoosableFileFilter(new FileNameExtensionFilter("RSA Private Key File (.prik)","prik"));
				if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					decRsaPrivateKey.setText(jfc.getSelectedFile().getAbsolutePath());
				}
			}
			
		});
		
		decRsaPrivateKeyDefault = new JCheckBox("Use Default Private Key");
		decRsaPrivateKeyDefault.setSelected(true);
		decRsaPrivateKeyDefault.setBounds(363, 0, 160, 23);
		DecryptPrivateKeyPanel.add(decRsaPrivateKeyDefault);
		
		DecStatus = new JLabel("");
		DecStatus.setForeground(new Color(255, 0, 0));
		DecStatus.setBounds(108, 135, 138, 23);
		DecryptPanel.add(DecStatus);
		
		JButton DecPauBtn = new JButton("Pause");
		DecPauBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
		DecPauBtn.setBounds(256, 135, 89, 23);
		DecryptPanel.add(DecPauBtn);
		DecPauBtn.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(decryptThread!=null && decryptThread.isAlive()){
					decryptThread.suspend();
					DecStatus.setText("Decryption Paused");
				}
				
			}			
		});
		
		JButton DecConBtn = new JButton("Continue");
		DecConBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
		DecConBtn.setBounds(346, 135, 89, 23);
		DecryptPanel.add(DecConBtn);
		DecConBtn.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(decryptThread!=null && decryptThread.isAlive()){
					decryptThread.resume();
					DecStatus.setText("Decrypting...");				
				}
				
			}			
		});
		
		JButton DecStopBtn = new JButton("Cancel");
		DecStopBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
		DecStopBtn.setBounds(436, 135, 89, 23);
		DecryptPanel.add(DecStopBtn);
		DecStopBtn.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(decryptThread!=null && decryptThread.isAlive()){
					decryptThread.stop();
					decProgressBar.setValue(0);
					DecryptBtn.setEnabled(true);
					DecStatus.setText("Decryption Cancelled");
				}
				
			}			
		});
		
		decProgressBar = new JProgressBar(0, 100);
		decProgressBar.setStringPainted(true);
		decProgressBar.setForeground(Color.GREEN);
		decProgressBar.setBounds(10, 165, 514, 23);
		DecryptPanel.add(decProgressBar);				
		decRsaPrivateKeyDefault.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				boolean set = !decRsaPrivateKeyDefault.isSelected();
				DecRsaPrivateKeyBrowse.setEnabled(set);
				decRsaPrivateKey.setEnabled(set);
			}
			
		});
		
		advancedMode = new JToggleButton("Advanced Mode");
		advancedMode.setBounds(11, 11, 130, 23);
		encryptDecryptPanel.add(advancedMode);
		advancedMode.addActionListener( new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					boolean set = advancedMode.isSelected();
					key.setEditable(!set);
					key.setText("");									
					initVector.setText("");
					advancedOnly.setVisible(set);
					KeyBrowse.setEnabled(set);
					EncryptPublicKeyPanel.setVisible(set);
					DecryptPrivateKeyPanel.setVisible(set);					
					if(!set){						
						mode.setSelectedItem("ECB");
					}
				}
		});
		
		JPanel KeyGenPanel = new JPanel();
		KeyGenPanel.setBackground(new Color(128, 128, 128));
		tabbedPane.addTab("Key Generator", null, KeyGenPanel, null);
		KeyGenPanel.setLayout(null);
		
		JPanel CryptoPanel = new JPanel();
		CryptoPanel.setBounds(0, 0, 534, 184);
		KeyGenPanel.add(CryptoPanel);
		CryptoPanel.setLayout(null);
		
		JLabel KeyGenAlgoLabel = new JLabel("Algorithm");
		KeyGenAlgoLabel.setBounds(10, 11, 81, 20);
		CryptoPanel.add(KeyGenAlgoLabel);
		
		JComboBox keyGenAlgo = new JComboBox();
		keyGenAlgo.setModel(new DefaultComboBoxModel(new String[] {"AES", "DES", "TripleDES"}));
		keyGenAlgo.setBounds(10, 37, 90, 20);
		CryptoPanel.add(keyGenAlgo);
		
		JLabel lblRsaPublicKey = new JLabel("RSA Public Key");
		lblRsaPublicKey.setBounds(10, 69, 92, 20);
		CryptoPanel.add(lblRsaPublicKey);
		
		TextField keyGenRsaPub = new TextField();
		keyGenRsaPub.setEnabled(false);
		keyGenRsaPub.setColumns(10);
		keyGenRsaPub.setBounds(120, 69, 144, 20);
		CryptoPanel.add(keyGenRsaPub);
		
		JButton KeyGenRsaPubBrowse = new JButton("Browse");
		KeyGenRsaPubBrowse.setEnabled(false);
		KeyGenRsaPubBrowse.setBounds(272, 69, 94, 20);
		CryptoPanel.add(KeyGenRsaPubBrowse);
		KeyGenRsaPubBrowse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.setDialogTitle("Choose RSA Public Key File");
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.addChoosableFileFilter(new FileNameExtensionFilter("RSA Public Key File (.pubk)","pubk"));
				if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					keyGenRsaPub.setText(jfc.getSelectedFile().getAbsolutePath());
				}
			}
			
		});
		
		JCheckBox KeyGenRsaPubDef = new JCheckBox("Use Default Public Key");
		KeyGenRsaPubDef.setSelected(true);
		KeyGenRsaPubDef.setBounds(373, 69, 160, 23);
		CryptoPanel.add(KeyGenRsaPubDef);
		KeyGenRsaPubDef.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				boolean set = !KeyGenRsaPubDef.isSelected();
				keyGenRsaPub.setEnabled(set);
				KeyGenRsaPubBrowse.setEnabled(set);
			}
			
		});
		
		JLabel KeyGenKEyLabel = new JLabel("Key");
		KeyGenKEyLabel.setBounds(168, 11, 26, 20);
		CryptoPanel.add(KeyGenKEyLabel);
		
		TextField keyGenKey = new TextField();
		keyGenKey.setBounds(198, 11, 220, 20);
		CryptoPanel.add(keyGenKey);
		
		JButton KeyGenKeyBtn = new JButton("Generate");
		KeyGenKeyBtn.setBounds(424, 10, 102, 20);
		CryptoPanel.add(KeyGenKeyBtn);
		KeyGenKeyBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Generate(keyGenKey,keyGenAlgo.getSelectedItem().toString());
			}
			
		});
		
		JLabel KeyGenInvLabel = new JLabel("Init Vector");
		KeyGenInvLabel.setBounds(131, 38, 63, 20);
		CryptoPanel.add(KeyGenInvLabel);
		
		TextField keyGenInv = new TextField();
		keyGenInv.setBounds(198, 38, 220, 20);
		CryptoPanel.add(keyGenInv);
		
		JButton KeyGenInvBtn = new JButton("Generate");
		KeyGenInvBtn.setBounds(424, 37, 102, 20);
		CryptoPanel.add(KeyGenInvBtn);
		KeyGenInvBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String algo = keyGenAlgo.getSelectedItem().toString();
				if(algo.equals("TripleDES")){
					Generate(keyGenInv,"DES");
				}
				else{
					Generate(keyGenInv,algo);
				}				
			}
			
		});
		
		JLabel label_2 = new JLabel("Output Folder");
		label_2.setBounds(10, 100, 92, 20);
		CryptoPanel.add(label_2);
		
		TextField keyGenOutput = new TextField();
		keyGenOutput.setColumns(10);
		keyGenOutput.setBounds(120, 100, 302, 20);
		CryptoPanel.add(keyGenOutput);
		
		JButton KeyGenOutputBrowse = new JButton("Browse");
		KeyGenOutputBrowse.setBounds(430, 100, 94, 20);
		CryptoPanel.add(KeyGenOutputBrowse);
		KeyGenOutputBrowse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				jfc.setDialogTitle("Choose output folder");
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					keyGenOutput.setText(jfc.getSelectedFile().getAbsolutePath());
				}
				
			}
			
		});
		
		JLabel label_3 = new JLabel("Key Name");
		label_3.setBounds(10, 127, 81, 20);
		CryptoPanel.add(label_3);
		
		TextField keyGenName = new TextField();
		keyGenName.setColumns(10);
		keyGenName.setBounds(120, 127, 144, 20);
		CryptoPanel.add(keyGenName);
		
		JLabel keyExpStatus = new JLabel("");
		keyExpStatus.setForeground(new Color(255, 0, 0));
		keyExpStatus.setBounds(373, 126, 151, 20);
		CryptoPanel.add(keyExpStatus);
		
		JLabel invExpStatus = new JLabel("");
		invExpStatus.setForeground(new Color(255, 0, 0));
		invExpStatus.setBounds(373, 153, 151, 20);
		CryptoPanel.add(invExpStatus);
		
		JButton KeyGenExportBtn = new JButton("Export");
		KeyGenExportBtn.setBounds(272, 126, 94, 20);
		CryptoPanel.add(KeyGenExportBtn);
		KeyGenExportBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String key = keyGenKey.getText();
				String algorithm = keyGenAlgo.getSelectedItem().toString();
				String fileName = keyGenName.getText();
				String outputFolder = keyGenOutput.getText();
				String RSAKeyFile = (!KeyGenRsaPubDef.isSelected())? keyGenRsaPub.getText() : DEF_PUB_KEY;				
				if(KeyGenParameterIsOk(algorithm,key,outputFolder,RSAKeyFile,fileName,false)){
					File output = new File(outputFolder + "\\" + fileName+"."+algorithm.toLowerCase());
					if(Export(key,output,RSAKeyFile)){
						keyExpStatus.setText("Key Exported");
					}
					else{
						keyExpStatus.setText("An error Occurred");
					}
				}				
			}
			
		});
		
		JLabel lblInitVectorName = new JLabel("Init Vector Name");
		lblInitVectorName.setBounds(10, 154, 104, 20);
		CryptoPanel.add(lblInitVectorName);
		
		TextField keyGenInvName = new TextField();
		keyGenInvName.setColumns(10);
		keyGenInvName.setBounds(120, 154, 144, 20);
		CryptoPanel.add(keyGenInvName);
		
		JButton KeyGenInvExportBtn = new JButton("Export");
		KeyGenInvExportBtn.setBounds(272, 153, 94, 20);
		CryptoPanel.add(KeyGenInvExportBtn);
		KeyGenInvExportBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String inv = keyGenInv.getText();
				String algorithm = keyGenAlgo.getSelectedItem().toString();
				String fileName = keyGenInvName.getText();
				String outputFolder = keyGenOutput.getText();
				String RSAKeyFile = (!KeyGenRsaPubDef.isSelected())? keyGenRsaPub.getText() : DEF_PUB_KEY;				
				if(KeyGenParameterIsOk(algorithm,inv,outputFolder,RSAKeyFile,fileName,true)){
					File output = new File(outputFolder + "\\" + fileName+".inv."+algorithm.toLowerCase());
					if(Export(inv,output,RSAKeyFile)){
						invExpStatus.setText("Init Vector Exported");
					}
					else{
						invExpStatus.setText("An error Occurred");					
					}
				}				
			}
			
		});
		
		JPanel RSAPanel = new JPanel();
		RSAPanel.setBounds(0, 187, 534, 156);
		KeyGenPanel.add(RSAPanel);
		RSAPanel.setLayout(null);
		
		JLabel rsaStatus = new JLabel("");
		rsaStatus.setForeground(Color.RED);
		rsaStatus.setBounds(383, 70, 141, 20);
		RSAPanel.add(rsaStatus);
		
		TextField rsaName = new TextField();
		rsaName.setColumns(10);
		rsaName.setBounds(120, 70, 144, 20);
		RSAPanel.add(rsaName);
		
		JLabel lblNewLabel = new JLabel("RSA Key Pair Generator");
		lblNewLabel.setForeground(new Color(255, 0, 0));
		lblNewLabel.setBounds(10, 11, 254, 20);
		RSAPanel.add(lblNewLabel);
		
		JLabel label = new JLabel("Output Folder");
		label.setBounds(10, 40, 92, 20);
		RSAPanel.add(label);
		
		TextField rsaOutput = new TextField();
		rsaOutput.setColumns(10);
		rsaOutput.setBounds(120, 40, 302, 20);
		RSAPanel.add(rsaOutput);
		
		JButton RsaOutputBrowse = new JButton("Browse");
		RsaOutputBrowse.setBounds(430, 40, 94, 20);
		RSAPanel.add(RsaOutputBrowse);
		RsaOutputBrowse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				jfc.setDialogTitle("Choose output folder");
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					rsaOutput.setText(jfc.getSelectedFile().getAbsolutePath());
				}
				
			}
			
		});
		
		JButton rsaBtn = new JButton("Generate");
		rsaBtn.setBounds(272, 70, 102, 20);
		RSAPanel.add(rsaBtn);
		
		JLabel label_1 = new JLabel("Name");
		label_1.setBounds(10, 70, 81, 20);
		RSAPanel.add(label_1);
		tabbedPane.setBackgroundAt(1, new Color(255, 255, 255));
		rsaBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String outputFolder = rsaOutput.getText();	
				String name = rsaName.getText();
				if(RsaParameterIsOk(outputFolder,name)){
					if(RsaExport(name,outputFolder)){
						rsaStatus.setText("Key Pair Generated");
					}
					else{
						rsaStatus.setText("An error occurred");
					}
				}
			}
			
		});
		
		JPanel Md5Panel = new JPanel();
		tabbedPane.addTab("MD5", null, Md5Panel, null);
		Md5Panel.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("Generate MD5 Hash String");
		lblNewLabel_1.setForeground(new Color(255, 0, 0));
		lblNewLabel_1.setBounds(10, 11, 220, 20);
		Md5Panel.add(lblNewLabel_1);
		
		md5Result = new JTextField();
		md5Result.setEditable(false);
		md5Result.setHorizontalAlignment(SwingConstants.LEFT);
		md5Result.setBounds(120, 70, 404, 20);
		Md5Panel.add(md5Result);
		md5Result.setColumns(10);
		
		JLabel lblInputFile = new JLabel("Input File");
		lblInputFile.setBounds(10, 39, 92, 20);
		Md5Panel.add(lblInputFile);
		
		TextField md5Input = new TextField();
		md5Input.setColumns(10);
		md5Input.setBounds(120, 39, 302, 20);
		Md5Panel.add(md5Input);
		
		JButton Md5Browse = new JButton("Browse");
		Md5Browse.setBounds(430, 39, 94, 20);
		Md5Panel.add(Md5Browse);
		Md5Browse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				jfc.setDialogTitle("Choose input file");
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					md5Input.setText(jfc.getSelectedFile().getAbsolutePath());
				}
				
			}
			
		});
		
		JButton Md5Gen = new JButton("Generate");
		Md5Gen.setBounds(10, 70, 94, 20);
		Md5Panel.add(Md5Gen);
		Md5Gen.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String inputFile = md5Input.getText();
				if(inputFile.equals("")){
					JOptionPane.showMessageDialog(null,"Please choose input file!");
					md5Result.setText("");
					return;
				}
				File f = new File(inputFile);
				if(!f.exists() || (f.exists() && f.isDirectory())){
					JOptionPane.showMessageDialog(null,"Can't find input file!");
					md5Result.setText("");
					return;
				}
				md5Result.setText(new Md5().generate(f));
			}
			
		});
		
		JLabel lblMdHashCheck = new JLabel("MD5 Hash Check");
		lblMdHashCheck.setForeground(new Color(255, 0, 0));
		lblMdHashCheck.setBounds(10, 119, 220, 20);
		Md5Panel.add(lblMdHashCheck);
		
		JLabel label_5 = new JLabel("Input File");
		label_5.setBounds(10, 147, 92, 20);
		Md5Panel.add(label_5);
		
		TextField md5ChkInput = new TextField();
		md5ChkInput.setColumns(10);
		md5ChkInput.setBounds(120, 147, 302, 20);
		Md5Panel.add(md5ChkInput);
		
		JButton Md5ChkBrowse = new JButton("Browse");
		Md5ChkBrowse.setBounds(430, 147, 94, 20);
		Md5Panel.add(Md5ChkBrowse);
		Md5ChkBrowse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				jfc.setDialogTitle("Choose input file");
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					md5ChkInput.setText(jfc.getSelectedFile().getAbsolutePath());
				}
				
			}
			
		});
		
		md5ChkInputStr = new JTextField();
		md5ChkInputStr.setHorizontalAlignment(SwingConstants.LEFT);
		md5ChkInputStr.setColumns(10);
		md5ChkInputStr.setBounds(120, 178, 404, 20);
		Md5Panel.add(md5ChkInputStr);
		
		JLabel lblHashString = new JLabel("MD5 Hash \nString");
		lblHashString.setBounds(10, 178, 100, 20);
		Md5Panel.add(lblHashString);
		
		JLabel md5ChkStatus = new JLabel("");
		md5ChkStatus.setForeground(new Color(255, 0, 0));
		md5ChkStatus.setBounds(120, 209, 173, 20);
		Md5Panel.add(md5ChkStatus);
		
		JButton Md5Chk = new JButton("Check");
		Md5Chk.setBounds(10, 209, 94, 20);
		Md5Panel.add(Md5Chk);
		
		JLabel label_4 = new JLabel("Input File A");
		label_4.setBounds(10, 250, 92, 20);
		Md5Panel.add(label_4);
		
		TextField md5InputA = new TextField();
		md5InputA.setColumns(10);
		md5InputA.setBounds(120, 250, 302, 20);
		Md5Panel.add(md5InputA);
		
		JButton Md5InputABrowse = new JButton("Browse");
		Md5InputABrowse.setBounds(430, 250, 94, 20);
		Md5Panel.add(Md5InputABrowse);
		Md5InputABrowse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				jfc.setDialogTitle("Choose input file");
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					md5InputA.setText(jfc.getSelectedFile().getAbsolutePath());
				}
				
			}
			
		});
		
		JLabel md5ChkStatus2 = new JLabel("");
		md5ChkStatus2.setForeground(Color.RED);
		md5ChkStatus2.setBounds(120, 312, 173, 20);
		Md5Panel.add(md5ChkStatus2);
		
		TextField md5InputB = new TextField();
		md5InputB.setColumns(10);
		md5InputB.setBounds(120, 281, 302, 20);
		Md5Panel.add(md5InputB);
		
		JButton Md5Chk2 = new JButton("Check");
		Md5Chk2.setBounds(10, 312, 94, 20);
		Md5Panel.add(Md5Chk2);
		Md5Chk2.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String inputFileA = md5InputA.getText();
				String inputFileB = md5InputB.getText();
				if(inputFileA.equals("")){
					JOptionPane.showMessageDialog(null,"Please choose input file A!");
					md5ChkStatus2.setText("");
					return;
				}
				else if(inputFileB.equals("")){
					JOptionPane.showMessageDialog(null,"Please choose input file B!");
					md5ChkStatus2.setText("");
					return;
				}
				File fA = new File(inputFileA);
				File fB = new File(inputFileB);
				if(!fA.exists() || (fA.exists() && fA.isDirectory())){
					JOptionPane.showMessageDialog(null,"Can't find input file A!");
					md5ChkStatus2.setText("");
					return;
				}
				else if(!fB.exists() || (fB.exists() && fB.isDirectory())){
					JOptionPane.showMessageDialog(null,"Can't find input file B!");
					md5ChkStatus2.setText("");
					return;
				}
				//String inputFileAHash = new Md5().generate(fA);
				//String inputFileBHash = new Md5().generate(fB);
				if(new Md5().match(fA, fB)){
					md5ChkStatus2.setText("Match!");
				}
				else{
					md5ChkStatus2.setText("Mismatch!");
				}
			}
			
		});
		
		JLabel label_6 = new JLabel("Input File B");
		label_6.setBounds(10, 281, 92, 20);
		Md5Panel.add(label_6);
				
		
		JButton Md5InputBBrowse = new JButton("Browse");
		Md5InputBBrowse.setBounds(430, 281, 94, 20);
		Md5Panel.add(Md5InputBBrowse);
		Md5InputBBrowse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				jfc.setDialogTitle("Choose input file");
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					md5InputB.setText(jfc.getSelectedFile().getAbsolutePath());
				}
				
			}
			
		});
		
		tabbedPane.setBackgroundAt(2, new Color(255, 255, 255));
		
		JPanel About = new JPanel();
		About.setBackground(SystemColor.menu);
		tabbedPane.addTab("About", null, About, null);
		tabbedPane.setBackgroundAt(3, new Color(255, 255, 255));
		tabbedPane.setEnabledAt(3, true);
		About.setLayout(null);
		try {
			BufferedImage aboutImg = ImageIO.read(new File(System.getProperty("user.dir")+"/resources/logo.png"));
			JLabel picLabel = new JLabel(new ImageIcon(aboutImg));
			picLabel.setLocation(50, 120);
			picLabel.setSize(115, 55);
			About.add(picLabel);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JTextPane txtpnDddd = new JTextPane();
		txtpnDddd.setFont(new Font("Tahoma", Font.BOLD, 15));
		txtpnDddd.setText(" BCT Crypto");
		txtpnDddd.setBackground(SystemColor.menu);
		txtpnDddd.setEditable(false);
		txtpnDddd.setBounds(60, 186, 97, 25);
		About.add(txtpnDddd);
		
		JTextPane Description = new JTextPane();
		Description.setForeground(SystemColor.textText);
		Description.setEditable(false);
		Description.setBackground(SystemColor.menu);
		Description.setAlignmentX(Component.RIGHT_ALIGNMENT);
		Description.setText("BCT Crypto is an application built for 'Cryptography & Network Security' course assignment.");
		Description.setBounds(252, 210, 282, 55);
		About.add(Description);
		
		JTextPane Authors = new JTextPane();
		Authors.setForeground(SystemColor.textText);
		Authors.setEditable(false);
		Authors.setText("Bao Vo - 1410289@hcmut.edu.vn \n"
				+ "Chanh Tran - 1410333@hcmut.edu.vn\n"
				+ "Triet Tran - 1414169@hcmut.edu.vn");
		Authors.setBackground(SystemColor.menu);
		Authors.setBounds(252, 40, 272, 48);
		About.add(Authors);
		
		JLabel lblNewLabel_2 = new JLabel("Authors:");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_2.setBounds(220, 25, 90, 14);
		About.add(lblNewLabel_2);
		
		JLabel lblDescriptions = new JLabel("Descriptions:");
		lblDescriptions.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDescriptions.setBounds(220, 195, 90, 14);
		About.add(lblDescriptions);
		
		JLabel lblUniversity = new JLabel("University:");
		lblUniversity.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblUniversity.setBounds(221, 100, 90, 14);
		About.add(lblUniversity);			
		
		JTextPane University = new JTextPane();
		University.setEditable(false);
		University.setForeground(SystemColor.textText);
		University.setText("Vietnam National University in HCMC \nBach Khoa University - BKU \n(HCMC University of Technology - HCMUT)");
		University.setBackground(SystemColor.menu);
		University.setBounds(253, 115, 272, 55);
		About.add(University);			
		University.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				try {
					java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://www.hcmut.edu.vn/"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				University.setForeground(SystemColor.textHighlight);
				frmBctcrypto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				University.setForeground(SystemColor.textText);
				frmBctcrypto.setCursor(Cursor.getDefaultCursor());
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		JTextPane txtpnVersion = new JTextPane();
		txtpnVersion.setText("version 1.2");
		txtpnVersion.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtpnVersion.setEditable(false);
		txtpnVersion.setBackground(SystemColor.menu);
		txtpnVersion.setBounds(74, 212, 69, 25);
		About.add(txtpnVersion);
		
		JLabel lblApplicationManual = new JLabel("Application Manual:");
		lblApplicationManual.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblApplicationManual.setBounds(220, 276, 153, 14);
		About.add(lblApplicationManual);
		
		JTextPane Manual = new JTextPane();
		Manual.setForeground(SystemColor.textText);
		Manual.setText("readme.txt");
		Manual.setEditable(false);
		Manual.setBackground(SystemColor.menu);
		Manual.setBounds(252, 291, 272, 25);
		About.add(Manual);
		Manual.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				try {
					java.awt.Desktop.getDesktop().open(new File(System.getProperty("user.dir") + "/readme.txt"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				Manual.setForeground(SystemColor.textHighlight);
				frmBctcrypto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				Manual.setForeground(SystemColor.textText);
				frmBctcrypto.setCursor(Cursor.getDefaultCursor());
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		
		
		JTextPane Faculty = new JTextPane();
		Faculty.setText("Faculty of Computer Science & Engineering");
		Faculty.setForeground(SystemColor.textText);
		Faculty.setEditable(false);
		Faculty.setBackground(SystemColor.menu);
		Faculty.setBounds(253, 165, 272, 25);
		About.add(Faculty);
		Faculty.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				try {
					java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://www.cse.hcmut.edu.vn/"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				Faculty.setForeground(SystemColor.textHighlight);
				frmBctcrypto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				Faculty.setForeground(SystemColor.textText);
				frmBctcrypto.setCursor(Cursor.getDefaultCursor());
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		Md5Chk.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String inputFile = md5ChkInput.getText();
				String inputString = md5ChkInputStr.getText();
				if(inputFile.equals("")){
					JOptionPane.showMessageDialog(null,"Please choose input file!");
					md5ChkStatus.setText("");
					return;
				}
				File f = new File(inputFile);
				if(!f.exists() || (f.exists() && f.isDirectory())){
					JOptionPane.showMessageDialog(null,"Can't find input file!");
					md5ChkStatus.setText("");
					return;
				}
				String inputFileHash = new Md5().generate(f);
				if(inputFileHash.equals(inputString)){
					md5ChkStatus.setText("Match!");
				}
				else{
					md5ChkStatus.setText("Mismatch!");
				}
			}
			
		});
	}	
	private void Generate(TextField t, String algorithm){
		t.setText(new GenKey().generate(algorithm));
	}
	private boolean Export(String content, File outputFile, String RSAKeyFile){		
		try {
			RSA rsa = new RSA();
			FileOutputStream fos = new FileOutputStream(outputFile);
			rsa.readPublicKeyFromFile(RSAKeyFile);
			fos.write(rsa.encrypt(content));
			fos.close();
			return true;
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException | InvalidKeyException | 
				NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"An Error Occurred!");
			return false;
		}		
	}
	private boolean RsaExport(String name, String outputFolder){
		RSA rsa = new RSA();
		try {
			rsa.generateKeyPair();
			rsa.saveKeysToFile(name, outputFolder);
			return true;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	private boolean KeyGenParameterIsOk(String algorithm, String content, String outputFolder, String RSAKeyFile, String keyName, boolean inv){
		File o = new File(outputFolder);
		if(outputFolder.equals("")){
			JOptionPane.showMessageDialog(null,"Please choose output folder!");
			return false;
		}
		else if(!o.exists() || (o.exists() && !o.isDirectory())){
			JOptionPane.showMessageDialog(null,"Can't find output folder!");
			return false;
		}
		else if(!KeyIsOk(algorithm,content) && !inv){
			JOptionPane.showMessageDialog(null,"Please input the right length of " + algorithm + " algorithm!");
			return false;
		}
		else if(inv){
			if((algorithm.equals("TripleDES") && !KeyIsOk("DES",content)) ||
				!algorithm.equals("TripleDES") && (!KeyIsOk(algorithm,content))){
				JOptionPane.showMessageDialog(null,"Please input the right length of " + algorithm + " algorithm!");
				return false;
			}
		}
		else if(RSAKeyFile.equals("")){
			JOptionPane.showMessageDialog(null,"Please input RSA Public Key!");
			return false;
		}
		else if(!_crypt.getExtension(RSAKeyFile).toLowerCase().equals("pubk")){
			JOptionPane.showMessageDialog(null,"Please input supported RSA Public Key (.pubk)!");
			return false;
		}
		else if(!new File(RSAKeyFile).exists()){
			JOptionPane.showMessageDialog(null,"Can't find RSA Public Key!");
			return false;
		}
		else if(keyName.equals("")){
			JOptionPane.showMessageDialog(null,"Please specify key name");
			return false;
		}
		return true;
	}
	private boolean RsaParameterIsOk(String outputFolder, String name){
		File o = new File(outputFolder);
		if(outputFolder.equals("")){
			JOptionPane.showMessageDialog(null,"Please choose output folder!");
			return false;
		}
		else if(!o.exists() || (o.exists() && !o.isDirectory())){
			JOptionPane.showMessageDialog(null,"Can't find output folder!");
			return false;
		}
		else if(name.equals("")){
			JOptionPane.showMessageDialog(null,"Please specify key name");
			return false;
		}
		return true;
	}
}
