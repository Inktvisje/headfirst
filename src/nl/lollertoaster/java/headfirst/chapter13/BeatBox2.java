package nl.lollertoaster.java.headfirst.chapter13;


import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;


public class BeatBox2 {
    private JFrame theFrame;
    private ArrayList<JCheckBox> checkboxList;
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;

    private String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Accoustic Snare", "Crash Cymbal",
            "Hand Clap", "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell", "Vibraslap",
            "Low-mid Tom", "High Agogo", "Open Hi Conga"};
    private int[] instruments = {35, 42, 46, 38, 49, 39, 50, 50, 70, 72, 64, 56, 58, 47, 67, 63};

    public static void main(String[] args) {
        new BeatBox2().buildGui();
    }

    private void buildGui() {
        theFrame = new JFrame("Cyber Beatbox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel backGround = new JPanel(layout);
        backGround.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        checkboxList = new ArrayList<>();
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

        JButton save = new JButton("Save");
        save.addActionListener(new MySendListener());
        buttonBox.add(save);

        JButton read = new JButton("Read");
        read.addActionListener(new MyReadInListener());
        buttonBox.add(read);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i =0; i < 16; i++) {
            nameBox.add(new Label(instrumentNames[i]));
        }

        backGround.add(BorderLayout.EAST, buttonBox);
        backGround.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(backGround);

        GridLayout grid = new GridLayout(16,16);
        grid.setVgap(1);
        grid.setHgap(2);

        JPanel mainPanel = new JPanel(grid);
        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            mainPanel.add(c);
        }
        backGround.add(BorderLayout.CENTER, mainPanel);

        setupMidi();

        theFrame.setBounds(50,50,300,300);
        theFrame.pack();
        theFrame.setVisible(true);
    }

    private void setupMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {e.printStackTrace();}
    }

    private class MyStartListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            buildTrackAndStart();
        }
    }

    private void buildTrackAndStart() {

        sequence.deleteTrack(track);
        track = sequence.createTrack();

        int[] trackList;

        for (int i = 0; i < 16; i++) {
            trackList = new int[16];

            int key = instruments[i];

            for (int j = 0; j < 16; j++) {
                JCheckBox jc = checkboxList.get((16 * i) + j);
                if (jc.isSelected()) {
                    trackList[j] = key;
                } else {
                    trackList[j] = 0;
                }
            }

            makeTracks(trackList);
            track.add(makeEvent(176,1,127,0,16));
        }

        track.add(makeEvent(176,1,127,0,16));
        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {e.printStackTrace();}
    }

    private void makeTracks(int[] list) {
        for (int i = 0; i < 16; i++) {
            int key = list[i];

            if (key != 0) {
                track.add(makeEvent(144,9,key,100,i));
                track.add(makeEvent(128,9,key,100,i+1));
            }
        }
    }

    private MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
        try {
            ShortMessage msg = new ShortMessage(comd,chan,one,two);
            return new MidiEvent(msg, tick);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private File selectFile(String mode) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Beats", "ser"));
        chooser.setCurrentDirectory(new File("."));

        int result;
        if (mode.equals("Save")) {
            result = chooser.showSaveDialog(theFrame);
        } else {
            result = chooser.showOpenDialog(theFrame);
        }

        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            return null;
        }
    }

    private class MyStopListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
        }
    }

    private class MyUpTempoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor*1.03));
        }
    }

    private class MyDownTempoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor*0.97));
        }
    }

    private class MySendListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean[] checkBoxState = new boolean[256];

            File file = selectFile("Save");

            if (file != null) {
                for (int i = 0; i < 256; i++) {
                    JCheckBox check = checkboxList.get(i);

                    if (check.isSelected()) {
                        checkBoxState[i] = true;
                    }
                }
                try {
                    FileOutputStream fileStream = new FileOutputStream(file);
                    ObjectOutputStream os = new ObjectOutputStream(fileStream);
                    os.writeObject(checkBoxState);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private class MyReadInListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean[] checkBoxState = new boolean[256];
            File file = selectFile("Open");

            if (file != null) {
                try {
                    FileInputStream fileIn = new FileInputStream(file);
                    ObjectInputStream is = new ObjectInputStream(fileIn);
                    checkBoxState = (boolean[]) is.readObject();
                } catch (IOException | ClassNotFoundException e1) {
                    e1.printStackTrace();
                }

                for (int i = 0; i < 256; i++) {
                    JCheckBox check = checkboxList.get(i);
                    check.setSelected(checkBoxState[i]);
                }

                sequencer.stop();
            }
        }
    }
}
