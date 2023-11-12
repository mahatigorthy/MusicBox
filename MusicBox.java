import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;

public class MusicBox extends JPanel implements ActionListener, AdjustmentListener, Runnable{

    
    JToggleButton arr [][] = new JToggleButton[37][100]; 
    JScrollPane pane; 
    JScrollBar tempoScrollBar; 
    JMenuBar menuBar; 
    JMenu file, isntrumentMenu, addRemoveMenu; 
    JMenuItem save, load, addCol, addNCol, removeCol, removeNCol; 
    JMenuItem[] instrumentItems; 
    JFileChooser fileChooser; 
    JButton playStopButton, clearButton, restButton; 
    JLabel[] labels = new JLabel[arr.length]; 
    JPanel gridPanel, otherPanel, labelPanel, tempoPanel, menuGridPanel; 
    JLabel tempoLabel; 
    FileNameExtensionFilter filter; 
    boolean notStopped = true; 
    JFrame frame = new JFrame(); 
    //String [] clipNames; 
    int tempo;
    boolean playing = false; 
    int row = 0, col = 0; 
    Thread timing;
    String notes [] = new String [] {
        "C4", 
        "B3", "ASharp3", "A3", "GSharp3", "G3", "FSharp3", "F3", "E3", "DSharp3", "D3", "CSharp3", "C3",
        "B2", "ASharp2", "A2", "GSharp2", "G2", "FSharp2", "F2", "E2", "DSharp2", "D2", "CSharp2", "C2",
        "B1", "ASharp1", "A1", "GSharp1", "G1", "FSharp1", "F1", "E1", "DSharp1", "D1", "CSharp1", "C1"
    };  

    Clip clip [] = new Clip [notes.length]; 

    String instrumentNames [] = new String [] {
        "Bell", "Glockenspiel", "Marimba", "Oboe", "Oh_Ah", "Piano"
    };

    public MusicBox() {
        frame = new JFrame();
        frame.add(this); 
        frame.setSize(1800, 1800);
        
        setGrid(37, 100); 
        loadTunes(instrumentNames[2]); 

        String currentDirectory = System.getProperty("user.dir");
        fileChooser = new JFileChooser(currentDirectory); 


        //build menu
        menuBar = new JMenuBar();
        file = new JMenu("file");
        save = new JMenuItem("save");
        load = new JMenuItem("load"); 
        save.addActionListener(this);
        load.addActionListener(this);
        file.add(save);
        file.add(load);

        addRemoveMenu = new JMenu("add/remove column");
        addCol = new JMenuItem("add column");
        addCol.addActionListener(this);
        addNCol = new JMenuItem("add n columns");
        addNCol.addActionListener(this);

        removeCol = new JMenuItem("remove column");
        removeCol.addActionListener(this);
        removeNCol = new JMenuItem("remove n column");
        removeNCol.addActionListener(this);

        addRemoveMenu.add(addCol);
        addRemoveMenu.add(addNCol); 
        addRemoveMenu.add(removeCol);
        addRemoveMenu.add(removeNCol);

        isntrumentMenu = new JMenu("instruments");
        instrumentItems = new JMenuItem[instrumentNames.length];
        for(int x=0; x<instrumentNames.length; x++) {
            String name = instrumentNames[x]; 
            instrumentItems[x] = new JMenuItem(name); 
            instrumentItems[x].putClientProperty("isntrument", name);
            instrumentItems[x].addActionListener(this);
            isntrumentMenu.add(instrumentItems[x]); 
        }
        menuBar.add(file);
        menuBar.add(addRemoveMenu);
        menuBar.add(isntrumentMenu);
        
        //build tempo bar
        tempoScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 200, 0, 50, 500);
        tempoScrollBar.addAdjustmentListener(this);

        tempo = tempoScrollBar.getValue();
        tempoLabel = new JLabel(String.format("%s%6s", "Tempo: ", tempo));
        tempoPanel = new JPanel(new BorderLayout());
        tempoPanel.add(tempoLabel, BorderLayout.WEST);
        tempoPanel.add(tempoScrollBar, BorderLayout.CENTER); 

        //build control buttons
        menuGridPanel = new JPanel();
        menuGridPanel.setLayout(new GridLayout(1, 3));
        playStopButton = new JButton("play");
        playStopButton.addActionListener(this);
        menuGridPanel.add(playStopButton);

        clearButton = new JButton("clear");
        clearButton.addActionListener(this);
        menuGridPanel.add(clearButton);

        restButton = new JButton("reset");
        restButton.addActionListener(this);

        menuGridPanel.add(restButton);
        menuBar.add(menuGridPanel, BorderLayout.EAST);

        //frame.add(this); 

        frame.add(tempoPanel, BorderLayout.SOUTH); 
        frame.add(menuBar, BorderLayout.NORTH);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        timing = new Thread(this);
        timing.start(); 
       
    }
    public static void main(String[]args) {
        MusicBox app = new MusicBox();  
    }

    public void loadTunes(String initInstrument) {
        try {
        for(int x=0;x<notes.length;x++)
        {
            String temp = "/Users/Mahati/Data/Code/Data Structures 22-23/Minesweeper Images/Music Box Tones - Courtesy of Dr Neg/"+initInstrument+"/"+initInstrument+" - "+notes[x]+".wav"; 
            File file = new File(temp);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            clip[x] = AudioSystem.getClip();
            clip[x].open(audioIn);
        }
        } catch (UnsupportedAudioFileException|IOException|LineUnavailableException e) {
        }
    }

    public void setGrid(int dimR, int dimC) {
        if(pane != null) {
            frame.remove(pane);
        }
        arr = new JToggleButton[dimR][dimC];
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(dimR, dimC));
        otherPanel = new JPanel();
        otherPanel.setLayout(new FlowLayout());
        for(int r=0; r<dimR; r++) {
            String name = notes[r].replaceAll("Sharp", "#");
            for(int c=0; c<dimC; c++) {
                arr[r][c] = new JToggleButton();
                arr[r][c].setText(name);
                arr[r][c].setPreferredSize(new Dimension(29, 28));
                arr[r][c].setMargin(new Insets(0, 0, 0, 0)); 
                arr[r][c].setForeground(Color.DARK_GRAY);
                gridPanel.add(arr[r][c]);
            }
        }
        otherPanel.add(gridPanel);
        pane = new JScrollPane(otherPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 
        frame.add(pane, BorderLayout.CENTER);
        frame.revalidate();
    }

    public void setNotes(Character [][] notes) {
        setGrid(notes.length, notes[0].length);
        for(int r=0; r<arr.length; r++) {
            for(int c=0; c<arr[0].length; c++) {
                try {
                    if(notes[r][c]=='x')    
                        arr[r][c].setSelected(true);
                } catch(NullPointerException|ArrayIndexOutOfBoundsException e) {}
            }
        }
    }
 
    public void reset() {
        col = -1;
        playing = false;
        playStopButton.setText("play");
    }

    public void saveSong() {
        filter = new FileNameExtensionFilter("*.txt", ".txt"); 
        fileChooser.setFileFilter(filter); //didnt do step 12
        if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                String st = file.getAbsolutePath();
                if(st.indexOf(".txt") >= 0)
                    st = st.substring(0, st.length()-4); 
                String currSong = tempo+" "+arr[0].length+"\n";
                String [] noteNames = {"c ", "b ", "a-", "a ", "g-", "g ", "f-", "f ", "e ", "d-", "d ", "c-", "c ", 
                                        "c ", "b ", "a-", "a ", "g-", "g ", "f-", "f ", "e ", "d-", "d ", "c-", "c ",
                                        "c ", "b ", "a-", "a ", "g-", "g ", "f-", "f ", "e ", "d-", "d ", "c-", "c ",
                                        "c "};
                for(int r=0; r<arr.length; r++) {
                    currSong += noteNames[r];
                    for(int c=0; c<arr[0].length; c++) {
                        if(arr[r][c].isSelected())
                            currSong += 'x';
                        else   
                            currSong += '-';
                    }

                    currSong += "\n"; 
                }

                BufferedWriter outputStream = new BufferedWriter(new FileWriter(st+".txt")); 
                outputStream.write(currSong.substring(0, currSong.length()-1));
                outputStream.close(); 
            } catch(Exception e) {
                
            }
        }
        
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        // TODO Auto-generated method stub
        tempo = tempoScrollBar.getValue(); 
        tempoLabel.setText(String.format("%s%6s", "Tempo: ", tempo));

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if(e.getSource() == playStopButton) {
            playing = !playing;
            if(!playing) {
                playStopButton.setText("play");
            }
            else {
                playStopButton.setText("stop"); 
            }
         
        }
        else if(e.getSource() == load) {
            reset();
            loadFile(); 
        }
        else if(e.getSource() == save) {
            reset();
            saveSong();
        }
        else if(e.getSource() == clearButton) {
            for(int i=0; i<arr.length; i++) {
                for(int j=0; j<arr[0].length; j++) {
                    arr[i][j].setSelected(false);
                }
            }
            reset();
        }
        else if(e.getSource() == restButton){
            for(int i=0; i<arr.length; i++) {
                for(int j=col-1; j<col+1; j++) {
                    if(j >= 0 && j<arr[0].length)
                    if(arr[i][col].isSelected()) {
                        clip[i].stop();
                        clip[i].setFramePosition(0);
                        arr[i][col].setForeground(Color.DARK_GRAY);
                    }
                }
            }
            pane.getHorizontalScrollBar().setValue(0);
            col = -1; 
        }
        else if(e.getSource() == addCol) {
            addColumn(1);
        }
        else if (e.getSource() == addNCol) {
            String num = JOptionPane.showInputDialog("enter number of columns to add: ");
            try {
                int n = Integer.parseInt(num);
                addColumn(n);
            } catch (NumberFormatException ee) {

            }

        }
        else if(e.getSource() == removeCol) {
            if(arr[0].length > 25) {
                removeColumn(1); 
            }
        }
        else if(e.getSource() == removeNCol) {
            String num = JOptionPane.showInputDialog("enter number of columns to remove: ");
            try {
                int n = Integer.parseInt(num);
                if(arr[0].length - n > 25) {
                    removeColumn(n);  //8:23
                }
            }
            catch (NumberFormatException ee) {

            }
            
        }
        else {
            String name = ((JMenuItem)e.getSource()).getText();
            loadTunes(name); 
        }
    }

    public void removeColumn(int n) {
        frame.remove(pane);
        gridPanel = new JPanel();
        JToggleButton [][] temp = new JToggleButton[37][arr[0].length - n];
        gridPanel.setLayout(new GridLayout(temp.length, temp[0].length));
        for(int r=0; r<temp.length; r++) {
            String noteName;
            for(int c=0; c<temp[0].length; c++) {
                if(c<arr[r].length) {
                    noteName = arr[r][c].getText();
                    temp[r][c] = new JToggleButton(noteName);
                    if(arr[r][c].isSelected())
                        temp[r][c].setSelected(true);
                    temp[r][c].setPreferredSize(new Dimension(29, 28));
                    temp[r][c].setMargin(new Insets(0, 0, 0, 0));
                    temp[r][c].setForeground(Color.DARK_GRAY);

                    gridPanel.add(temp[r][c]);

                }
            }
        }

        arr = temp;
        otherPanel = new JPanel();
        otherPanel.setLayout(new FlowLayout());
        otherPanel.add(gridPanel);

        pane = new JScrollPane(otherPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 
        frame.add(pane, BorderLayout.CENTER);
        frame.revalidate();
    }

    public void addColumn(int n) {
        frame.remove(pane);
        gridPanel = new JPanel();
        JToggleButton [][] temp = new JToggleButton[37][arr[0].length + n];
        gridPanel.setLayout(new GridLayout(temp.length, temp[0].length));
        for(int r=0; r<temp.length; r++) {
            String noteName = notes[r];
            noteName = noteName.replaceAll("Sharp", "#"); 
            for(int c=0; c<temp[0].length; c++) {
                if(c<arr[r].length) {
                    noteName = arr[r][c].getText();
                    temp[r][c] = new JToggleButton(noteName);
                    if(arr[r][c].isSelected())
                        temp[r][c].setSelected(true);
                    temp[r][c].setPreferredSize(new Dimension(29, 28));
                    temp[r][c].setMargin(new Insets(0, 0, 0, 0));
                    temp[r][c].setForeground(Color.DARK_GRAY);

                    gridPanel.add(temp[r][c]);

                }
            }
        }

        arr = temp;
        otherPanel = new JPanel();
        otherPanel.setLayout(new FlowLayout());
        otherPanel.add(gridPanel);

        pane = new JScrollPane(otherPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 
        frame.add(pane, BorderLayout.CENTER);
        frame.revalidate();
    }

    public void loadNotes(String name) {
        String selectedInstrument = name+"\\"+name; 
        try {
            for(int x = 0; x<notes.length; x++) {
                File file = new File(selectedInstrument+" - "+notes[x]+".wav");
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
                clip[x] = AudioSystem.getClip();
                clip[x].open(audioIn); 
            }
        } catch(Exception e) {

        }
    }
    public void loadFile() {
        if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File loadFile = fileChooser.getSelectedFile();
                BufferedReader input = new BufferedReader(new FileReader(loadFile));
                String temp = input.readLine();
                String [] tempoCol = temp.split(" ");
                tempo = Integer.parseInt(tempoCol[0]);
                tempoScrollBar.setValue(tempo);
                int numCols = Integer.parseInt(tempoCol[1]);
                Character [][] song = new Character[arr.length][numCols];
                int r = 0;
                while((temp = input.readLine()) != null) {
                    for(int c=2; c<numCols+2; c++) {
                        song[r][c-2] = temp.charAt(c);
                    }
                    r++;
                }
                setNotes(song);

                    

            } catch(IOException ee) {

            }
        }
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        do {
            try {
                if(!playing) {
                    timing.sleep(0);
                } else {
                    for(int r=0; r<arr.length; r++) {
                        if(col >=0)
                        if(arr[r][col].isSelected()) {
                            clip[r].start();
                            arr[r][col].setForeground(Color.magenta);
                            //pane.getHorizontalScrollBar().setValue(col+30);
                        }
                    }
                    timing.sleep(tempo); 
                    for(int i=0; i<arr.length; i++) {
                        if(col >=0)
                        if(arr[i][col].isSelected()) {
                            clip[i].stop();
                            clip[i].setFramePosition(0);
                            arr[i][col].setForeground(Color.DARK_GRAY);
                        }
                    }
                    col++; 
                    if(col == arr[0].length)
                        col = 0; 
                }
            }
            catch (InterruptedException e) {

            }
        } while (notStopped); 

    }
}
