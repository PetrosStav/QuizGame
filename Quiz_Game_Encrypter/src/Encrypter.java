import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Encrypter extends JFrame implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8245172597308500388L;
	
	static boolean hasFile = false;
	static boolean decrypt = false;
	static String fileName;
	Container cp;
	
	JPanel commandPanel;
	JPanel mainPanel;
	JPanel upperPanel;
	JLabel fileLb;
	JTextArea textAr;
	JScrollPane scroll;
	JButton exit;
	JButton open;
	JButton convert;
	
	JFileChooser fc;
	
	public Encrypter() {
		setTitle("QuizEditor v1.0");
		setSize(600,400);
		
		cp = getContentPane();
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		commandPanel = new JPanel();
		commandPanel.setLayout(new FlowLayout());
		upperPanel = new JPanel();
		upperPanel.setLayout(new FlowLayout());
		textAr = new JTextArea();
		textAr.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent e) {
				hasFile = true;
				convert.setEnabled(true);
			}

			@Override
			public void focusLost(FocusEvent e) {
				
			}
			
		});
		scroll = new JScrollPane(textAr, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		fileLb = new JLabel("File Name: ");
		upperPanel.add(fileLb);
		
		open = new JButton("Open");
		open.addActionListener(this);
		convert = new JButton("Convert");
		convert.addActionListener(this);
		convert.setEnabled(false);
		exit = new JButton("Exit");
		exit.addActionListener(this);
		commandPanel.add(open);
		commandPanel.add(convert);
		commandPanel.add(exit);
		
		mainPanel.add(scroll,BorderLayout.CENTER);
		mainPanel.add(upperPanel, BorderLayout.NORTH);
		mainPanel.add(commandPanel,BorderLayout.SOUTH);
		cp.add(mainPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		requestFocusInWindow();
	}
	
	public static void main(String[] args) {
		new Encrypter();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==exit){
			dispose();
		}else if(e.getSource() == open){
			fc = new JFileChooser();
			fc.setCurrentDirectory(new File("."));
			fc.setDialogTitle("Open file");
			int val = fc.showOpenDialog(this);
			if(val == JFileChooser.APPROVE_OPTION){
				BufferedReader reader = null;
				File f = null;
				decrypt = false;
				try{
					f = fc.getSelectedFile();
					if(f.getName().endsWith(".qzd")){
						decrypt=true;
					}
					byte[] bytes = readFromStream(new FileInputStream(f));
					String text = new String(bytes);
					if(decrypt){
						//decrypt
						String key = "pstav1993";
						byte[] dectext = null;
						FileInputStream fis = null;
						CipherInputStream cis = null;
						try {
							fis = new FileInputStream(f);
							DESKeySpec dks = new DESKeySpec(key.getBytes());
							SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
							SecretKey desKey = skf.generateSecret(dks);
							Cipher cipher = Cipher.getInstance("DES");
							cipher.init(Cipher.DECRYPT_MODE, desKey);
							cis = new CipherInputStream(fis, cipher);
							dectext = readFromStream(cis);
						} catch (Exception e1) {
							e1.printStackTrace();
						}finally{
							if(fis!=null){
								fis.close();
							}
							if(cis!=null){
								cis.close();
							}
						}
						textAr.setText(new String(dectext));
					}else{
						textAr.setText(text.toString());
					}
					fileLb.setText("File Name: "+f.getName());
					fileName = f.getName();
				}catch (Exception ex){
					ex.printStackTrace();
				}finally{
					if(reader!=null){
						try {
							reader.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				hasFile = true;
				convert.setEnabled(true);
			}else{
				JOptionPane.showMessageDialog(this, "File not opened!", "Warning", JOptionPane.INFORMATION_MESSAGE);
			}
			
		}else if(e.getSource() == convert){
			if(hasFile){
				if(fileName==null || fileName.trim().equals("")){
					fileName = "default";
				}
				//encrypt -- default
				String key = "pstav1993";
				try {
					InputStream is = new ByteArrayInputStream(textAr.getText().getBytes());
					File f2 = null;
					if(decrypt){
						f2 = new File("new_"+fileName);
					}else{
						f2 = new File(fileName+".qzd");
					}
					FileOutputStream fos = new FileOutputStream(f2);
					DESKeySpec dks = new DESKeySpec(key.getBytes());
					SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
					SecretKey desKey = skf.generateSecret(dks);
					Cipher cipher = Cipher.getInstance("DES");
					cipher.init(Cipher.ENCRYPT_MODE, desKey);
					CipherInputStream cis = new CipherInputStream(is, cipher);
					doCopy(cis, fos);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void doCopy(InputStream is, OutputStream os) throws IOException {
		byte[] bytes = new byte[64];
		int numBytes;
		while ((numBytes = is.read(bytes)) != -1) {
			os.write(bytes, 0, numBytes);
		}
		os.flush();
		os.close();
		is.close();
	}
	
	private byte[] readFromStream(InputStream inputStream) throws Exception
	{
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    DataOutputStream dos = new DataOutputStream(baos);
	    byte[] data = new byte[4096];
	    int count = inputStream.read(data);
	    while(count != -1)
	    {
	        dos.write(data, 0, count);
	        count = inputStream.read(data);
	    }

	    return baos.toByteArray();
	}
	
}
