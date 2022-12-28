
// LionRay: wav to DFPWM converter
// by Gamax92

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

@SuppressWarnings("serial")
public class LionRay extends JFrame {
	public static int sampleRate = 48000;
	public static void main(String[] args) throws Exception {

			try {
				convert(true);
			} catch (UnsupportedAudioFileException e) {
				System.err.println("Audio format unsupported");
				return;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

	}

	public static void convert(boolean dfpwmNew) throws UnsupportedAudioFileException, IOException {
		AudioFormat convertFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, 8, 1, 1, sampleRate, false);
		AudioInputStream unconverted = AudioSystem.getAudioInputStream(new BufferedInputStream(System.in));
		AudioInputStream inFile = AudioSystem.getAudioInputStream(convertFormat, unconverted);
		BufferedOutputStream outFile = new BufferedOutputStream(System.out);

		byte[] readBuffer = new byte[1024];
		byte[] outBuffer = new byte[readBuffer.length / 8];
		DFPWM converter = new DFPWM(dfpwmNew);

		int read;
		do {
			for(read = 0; read < readBuffer.length;) {
				int amt = inFile.read(readBuffer, read, readBuffer.length - read);
				if(amt == -1) break;
				read += amt;
			}
			read &= ~0x07;
			converter.compress(outBuffer, readBuffer, 0, 0, read / 8);
			outFile.write(outBuffer, 0, read / 8);
		} while(read == readBuffer.length);
		outFile.flush();
	}
}
