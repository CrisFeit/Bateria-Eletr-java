package beatBox;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class BeatBox {
	JFrame theFrame;
	JPanel mainPanel;
	ArrayList<JCheckBox> checkboxList;
	Sequencer sequencer;
	Sequence sequence;
	Track  track;
	
	String[] instrumentNames = {"Bumbo","Side Kick","Hit-Hat","Hit-Hat Aberto","Crash",
			"Caixa Acustica","Caixa Eletrica","Low Tom","Mid Tom","Hi Bongo","Low Bongo","Maracas",
			"Claves","Mute Hi Conga","Low Conga","Open Hi Gonga"};
	int[] instruments = {35,37,42,46,49,38,40,45,47,60,61,70,75,62,64,63};
	
	
	/*String[] instrumentNames = {"Bumbo","Side Kick","Hit-Hat","Hit-Hat Aberto","Ride",
			"Caixa Acustica","Caixa Eletrica","Crash","Tom Médio","Tom Grave","Bongo Grave","Maracas","Low Conga",
			"Cowbell","high Agogo","Open Hi Gonga"};
	int[] instruments = {35,37,42,46,51,38,40,49,47,50,60,70,64,56,67,63};*/
	public static void main(String[] args) {
		new BeatBox().setUpGui();
		}// close main
	public void setUpGui() { // Cria a O JFrame e Interface Principal
		theFrame = new JFrame("Cyber BeatBox");
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout layout = new BorderLayout();
		JPanel background = new JPanel(layout);
		background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		checkboxList = new ArrayList<JCheckBox>();
		Box buttonBox = new Box(BoxLayout.Y_AXIS);
		
		JButton start = new JButton("Start");
		start.addActionListener(new MyStartListener());
		buttonBox.add(start);
		
		JButton stop = new JButton("Stop");
		stop.addActionListener(new MyStopListener());
		buttonBox.add(stop);
		
		JButton upTempo = new JButton("Tempo Up");
		upTempo.addActionListener(new MyUpTempoListener());
		buttonBox.add(upTempo);
		
		JButton downTempo = new JButton("Tempo Down");
		downTempo.addActionListener(new MyDownTempoListener());
		buttonBox.add(downTempo);
		
		Box nameBox = new Box(BoxLayout.Y_AXIS);
		for(int i = 0;i<instruments.length;i++) {
			nameBox.add(new Label(instrumentNames[i]));
		}
		background.add(BorderLayout.EAST,buttonBox);
		background.add(BorderLayout.WEST,nameBox);
		
		theFrame.getContentPane().add(background);
		
		GridLayout grid = new GridLayout(instruments.length,instruments.length);
		grid.setVgap(1);
		grid.setHgap(2);
		mainPanel = new JPanel(grid);
		background.add(BorderLayout.CENTER,mainPanel);
		
		for(int i =0;i<256;i++) {// cria as Checkbox
			JCheckBox c = new JCheckBox();
			c.setSelected(false);
			checkboxList.add(c);
			mainPanel.add(c);
		}
		setUpMidi();
		
		theFrame.setBounds(50,50,300,300);
		theFrame.pack();
		theFrame.setVisible(true);
	}//close SetUpGui
	
	public void setUpMidi() {
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();;
			sequence = new Sequence(Sequence.PPQ,4);
			track = sequence.createTrack();
			sequencer.setTempoInBPM(120);
			}catch(Exception ex) {
			ex.printStackTrace();
		}
	}//close setupMidi()
	public void buildTrackAndStart() {
		int[] trackList = null;
		
		sequence.deleteTrack(track);
		track = sequence.createTrack();
		
		for(int i = 0;i<instruments.length;i++) { // Congigura cada CheckBox para um tipo de Instrumento
			trackList = new int[instruments.length];
			
			int key = instruments[i];
			
			for(int j=0;j<instruments.length;j++) {
				JCheckBox jc =(JCheckBox) checkboxList.get(j+(instruments.length*i));
				if(jc.isSelected()) {
					trackList[j]=key;
				}else {
					trackList[j]=0;
				}
			}//close j
			makeTracks(trackList);
			track.add(makeEvent(176,1,127,0,instruments.length));
		}//close i
		track.add(makeEvent(192,9,1,0,15));
		try {
			sequencer.setSequence(sequence);
			sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
			sequencer.start();
			sequencer.setTempoInBPM(120);
			}catch(Exception ex) {
			ex.printStackTrace();
		}
	}//buildTrackAndStart()
	
	public void makeTracks(int[] list) {
		for(int i = 0; i<instruments.length;i++) {
			int key = list[i];
			
			if(key !=0) {
				track.add(makeEvent(144,9,key,100,i));
				track.add(makeEvent(128,9,key,100,i+1));
			}
		}
	}

	public class MyStartListener implements ActionListener{
		public void actionPerformed(ActionEvent a) {
			buildTrackAndStart();
		}
	}//close Start
	public class MyStopListener implements ActionListener{
		public void actionPerformed(ActionEvent a) {
			sequencer.stop();
		}
	}//close Stop
	public class MyUpTempoListener implements ActionListener{
		public void actionPerformed(ActionEvent a) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor*1.03));
		}
	}//close UpTempo
	public class MyDownTempoListener implements ActionListener{
		public void actionPerformed(ActionEvent a) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor*0.97));
		}
	}//close DownTempo
	public MidiEvent makeEvent(int comd,int chan,int one,int two,int tick) {	
	MidiEvent event = null;
	try {
		ShortMessage a = new ShortMessage();
		a.setMessage(comd,chan,one,two);
		event = new MidiEvent(a,tick);
		
	} catch (Exception e) {e.printStackTrace();}
		return event;
	}//Close  makeEvent
}//Close BeatBox