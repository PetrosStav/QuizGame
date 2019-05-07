import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioPlayer {
	
	private Clip clip;
	
	public AudioPlayer(URL s){
		try{
			AudioInputStream ais = AudioSystem.getAudioInputStream(s);
			AudioFormat baseFormat = ais.getFormat();
			AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(), 
					baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
			AudioInputStream dais = AudioSystem.getAudioInputStream(decodedFormat,ais);
			clip = AudioSystem.getClip();
			clip.open(dais);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void play(boolean loop){
		if(clip == null) return;
		stop();
		clip.setFramePosition(0);
		if(!loop){
			clip.start();
		}else{
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
	public void stop(){
		if(clip.isRunning()) clip.stop();
	}
	
	public void close(){
		stop();
		clip.close();
	}
}
