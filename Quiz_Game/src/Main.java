import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

public class Main extends JFrame implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2385952899933096792L;
	
	static ArrayList<String> questions = new ArrayList<>();
	static ArrayList<String> answers = new ArrayList<>();
	static int numOfQuestion = 0;
	//static int tries = 5;
	AudioPlayer bgmusic;
	AudioPlayer winmusic;
	//AudioPlayer lostmusic;
	JButton next;
	JButton start;
	JButton exit;
	JTextArea questArea;
	JTextArea answerArea;
	JPanel mainPanel;
	JPanel commandPanel;
	JPanel answerPanel;
	JPanel questPanel;
	JLabel answerInfo;
	JLabel questInfo;
	Container cp;
	JFileChooser fc;
	
	public Main() {
		setTitle("QuizGame v1.0");
		setSize(600,400);

		cp = getContentPane();
		next = new JButton("Check Answer");
		next.addActionListener(this);
		next.setEnabled(false);
		start = new JButton("Start Game");
		start.addActionListener(this);
		exit = new JButton("Exit");
		exit.addActionListener(this);
		bgmusic = new AudioPlayer(this.getClass().getResource("bg.wav"));
		winmusic = new AudioPlayer(this.getClass().getResource("won.wav"));
		//lostmusic = new AudioPlayer(this.getClass().getResource("lost.wav"));
		
		questArea = new JTextArea("Welcome to QuizGame v1.0!");
		questArea.setLineWrap(true);
		questArea.setEditable(false);
		
		answerArea = new JTextArea("Press Start Game to begin the quiz!");
		answerArea.setLineWrap(true);
		answerArea.setEditable(false);
		
		answerInfo = new JLabel("Answer:");
		questInfo = new JLabel("Question:");
		
		mainPanel = new JPanel();
		
		commandPanel = new JPanel();
		commandPanel.setLayout(new FlowLayout());
		commandPanel.add(start);
		commandPanel.add(next);
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
	}
	
	public static void main(String[] args) {
		new Main();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//Next Button Pressed
		if(e.getSource()==next){
			String answer = answerArea.getText().trim();
			if(answer.equalsIgnoreCase(answers.get(numOfQuestion))){
				numOfQuestion++;
				if(numOfQuestion <= questions.size()-1){
					JOptionPane.showInternalMessageDialog(cp, "Your answer is correct! Let's move to the next question...", "Correct", JOptionPane.INFORMATION_MESSAGE);
					questArea.setText(questions.get(numOfQuestion));
					answerArea.setText("");
				}else{
					//End of Game
					bgmusic.stop();
					winmusic.play(false);
					JOptionPane.showInternalMessageDialog(cp, "Your answer is correct! You have won the Game!", "Winner", JOptionPane.INFORMATION_MESSAGE);
					next.setEnabled(false);
					start.setEnabled(true);
					start.setText("Restart Game");
				}
			}else{
				//Wrong Answer
				/*tries--;
				if(tries == 0){
					bgmusic.stop();
					lostmusic.play(false);
					JOptionPane.showInternalMessageDialog(cp, "Sorry but you are out of tries!\nYou have lost!", "Lost", JOptionPane.INFORMATION_MESSAGE);
					next.setEnabled(false);
					start.setEnabled(true);
					start.setText("Restart Game");*/
				//}else{
					JOptionPane.showInternalMessageDialog(cp, "Wrong answer, try again!", "Wrong", JOptionPane.INFORMATION_MESSAGE);
				//}
			}
		}else
		//Start Button Pressed
		if(e.getSource()==start){
			numOfQuestion = 0;
			//tries = 3;
			start.setText("Start Game");
			//Initializing Questions-Answers
			questions.clear();
			answers.clear();
			fc = new JFileChooser();
			fc.setCurrentDirectory(new File("."));
			fc.setDialogTitle("Open Quiz");
			fc.setSelectedFile(new File("data.qzd"));
			fc.setFileFilter(new FileNameExtensionFilter("qzd","QZD"));
			int val = fc.showOpenDialog(this);
			if(val == JFileChooser.APPROVE_OPTION){
				BufferedReader reader = null;
				File f2 = null;
				try{
					File f = fc.getSelectedFile();
					//encryption
					String key = "pstav1993";
					try {
						FileInputStream fis = new FileInputStream(f);
						FileOutputStream fos = new FileOutputStream(new File(f.getName()+".dec"));
						DESKeySpec dks = new DESKeySpec(key.getBytes());
						SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
						SecretKey desKey = skf.generateSecret(dks);
						Cipher cipher = Cipher.getInstance("DES");
						cipher.init(Cipher.DECRYPT_MODE, desKey);
						CipherInputStream cis = new CipherInputStream(fis, cipher);
						doCopy(cis, fos);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
					f2 = new File(f.getName()+".dec");
					reader = new BufferedReader(new FileReader(f2));
					String line;
					while((line = reader.readLine())!=null){
						if(line.startsWith("{QUESTION ")){
							String s = reader.readLine();
							StringBuilder question = new StringBuilder();
							while(!s.startsWith("{!QUESTION ")){
								question.append(s + "\n");
								s = reader.readLine();
							}
							questions.add(question.toString().trim());
						}else if(line.startsWith("{ANSWER ")){
							String s = reader.readLine();
							StringBuilder answer = new StringBuilder();
							while(!s.startsWith("{!ANSWER ")){
								answer.append(s + "\n");
								s = reader.readLine();
							}
							answers.add(answer.toString().trim());
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}finally{
					if(reader!=null){
						try {
							reader.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					if(f2!= null){
						f2.delete();
					}
				}
				//End
				if(!questions.isEmpty()){
					questArea.setText(questions.get(0));
					answerArea.setEditable(true);
					answerArea.setText("");
					next.setEnabled(true);
					start.setEnabled(false);
					winmusic.stop();
					//lostmusic.stop();
					bgmusic.play(true);
				}else{
					JOptionPane.showMessageDialog(this, "The file you selected has no data!", "Error", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}else 
		if(e.getSource() == exit){
			bgmusic.stop();
			winmusic.stop();
			//lostmusic.stop();
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
