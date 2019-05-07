import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

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
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Editor extends JFrame implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3416945698496744790L;
	
	static int numOfQuestions = 1;
	static ArrayList<String> questions = new ArrayList<>();
	static ArrayList<String> answers = new ArrayList<>();
	static boolean focusChangeQ = true;
	static boolean focusChangeA = true;
	static boolean Qnext = false;
	static boolean Anext = false;
	
	Container cp;
	
	JButton next;
	JButton save;
	JButton reset;
	JButton exit;
	
	JTextArea questArea;
	JTextArea answerArea;
	JPanel mainPanel;
	JPanel commandPanel;
	JPanel answerPanel;
	JPanel questPanel;
	JLabel answerInfo;
	JLabel questInfo;
	JFileChooser fc;
	
	public Editor() {
		setTitle("QuizEditor v1.0");
		setSize(600,400);
		
		cp = getContentPane(); 
		next = new JButton("Next Question");
		next.addActionListener(this);
		next.setEnabled(false);
		save = new JButton("Save As");
		save.addActionListener(this);
		reset = new JButton("Reset");
		reset.addActionListener(this);
		exit = new JButton("Exit");
		exit.addActionListener(this);
		
		questArea = new JTextArea("Here you write your question.");
		questArea.setLineWrap(true);
		questArea.addFocusListener(new FocusListener(){
			
			@Override
			public void focusGained(FocusEvent e) {
				if(focusChangeQ){
					questArea.setText("");
					focusChangeQ = false;
					Qnext = true;
					if(Anext){
						next.setEnabled(true);
					}
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
			}
			
		});
		
		answerArea = new JTextArea("Here you write your answer.");
		answerArea.setLineWrap(true);
		answerArea.addFocusListener(new FocusListener(){
			
			@Override
			public void focusGained(FocusEvent e) {
				if(focusChangeA){
					answerArea.setText("");
					focusChangeA = false;
					Anext = true;
					if(Qnext){
						next.setEnabled(true);
					}
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
			}
			
		});
		
		answerInfo = new JLabel("Answer 1:");
		questInfo = new JLabel("Question 1:");
		
		mainPanel = new JPanel();
		
		commandPanel = new JPanel();
		commandPanel.setLayout(new FlowLayout());
		commandPanel.add(save);
		commandPanel.add(next);
		commandPanel.add(reset);
		commandPanel.add(exit);
		
		answerPanel = new JPanel();
		answerPanel.setLayout(new BorderLayout());
		answerPanel.add(answerInfo, BorderLayout.NORTH);
		answerPanel.add(answerArea,BorderLayout.CENTER);
		answerPanel.add(commandPanel, BorderLayout.SOUTH);
		
		questPanel = new JPanel();
		questPanel.setLayout(new BorderLayout());
		questPanel.add(questInfo,BorderLayout.NORTH);
		questPanel.add(questArea, BorderLayout.CENTER);
		
		mainPanel.setLayout(new GridLayout(2, 1));
		mainPanel.add(questPanel);
		mainPanel.add(answerPanel);
		cp.add(mainPanel);
				
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		requestFocusInWindow();
	}
	
	public static void main(String[] args) {
		new Editor();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == next){
			numOfQuestions++;
			questions.add(questArea.getText().trim());
			answers.add(answerArea.getText().trim());
			questArea.setText("Next Question");
			answerArea.setText("Next Answer");
			questInfo.setText("Question "+numOfQuestions+":");
			answerInfo.setText("Answer "+numOfQuestions+":");
			focusChangeQ = true;
			focusChangeA = true;
			Qnext = false;
			Anext = false;
			next.setEnabled(false);
		}else 
		if(e.getSource() == reset){
			numOfQuestions = 1;
			questInfo.setText("Question "+numOfQuestions+":");
			answerInfo.setText("Answer "+numOfQuestions+":");
			questArea.setText("Here you write your question.");
			answerArea.setText("Here you write your answer.");
			answers.clear();
			questions.clear();
			focusChangeQ = true;
			focusChangeA = true;
			Qnext = false;
			Anext = false;
			next.setEnabled(false);
		}else
		if(e.getSource() == save){
			StringBuilder questAns = new StringBuilder();
			int qs = 0;
			for(String q : questions){
				questAns.append("{QUESTION " + qs + "}\n");
				questAns.append(q);
				questAns.append("\n{!QUESTION " + qs + "}\n");
				qs++;
			}
			int as = 0;
			for(String a : answers){
				questAns.append("{ANSWER " + as + "}\n");
				questAns.append(a);
				questAns.append("\n{!ANSWER " + as + "}\n");
				as++;
			}
			fc = new JFileChooser();
			fc.setCurrentDirectory(new File("."));
			fc.setDialogTitle("Open Quiz");
			fc.setSelectedFile(new File("data.qzd"));
			fc.setFileFilter(new FileNameExtensionFilter("qzd","QZD"));
			int val = fc.showSaveDialog(this);
			if(val == JFileChooser.APPROVE_OPTION){
				BufferedWriter writer = null;
				File f = null;
				try {
					f = fc.getSelectedFile();
					writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
					writer.write(questAns.toString());
					writer.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}finally{
					if(writer!=null){
						try {
							writer.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				//encryption
				String key = "pstav1993";
				try {
					FileInputStream fis = new FileInputStream(f);
					String fname = f.getName();
					File f2 = new File(fname+".cpt");
					FileOutputStream fos = new FileOutputStream(f2);
					DESKeySpec dks = new DESKeySpec(key.getBytes());
					SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
					SecretKey desKey = skf.generateSecret(dks);
					Cipher cipher = Cipher.getInstance("DES");
					cipher.init(Cipher.ENCRYPT_MODE, desKey);
					CipherInputStream cis = new CipherInputStream(fis, cipher);
					doCopy(cis, fos);
					f.delete();
					f2.renameTo(new File(fname));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}else{
				JOptionPane.showMessageDialog(this, "File not saved!");
			}
		}else
		if(e.getSource() == exit){
			dispose();
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
}
