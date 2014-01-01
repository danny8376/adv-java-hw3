/**
 * Advance JAVA Assignment
 * 
 * Student Name : ½²´Q¥à
 * Student No.  : 101403022
 * Class : Information Management - 2A
 * 
 * Filename : MainFrame.java
 * 
 * The main frame
 * 
 * Changelog:
 */
package advjava.hw.s101403022;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.LinkedHashSet;
import com.deitel.ch17.AccountRecordSerializable;

public class MainFrame extends JFrame {

	JPanel topPanel, middlePanel, bottomPanel;
	JButton load, insert, delete, save, clear;
	JLabel filename, path, insertData, deleteData;
	JTextField insertField, deleteField;
	JTextArea display;
	
	LinkedHashSet<AccountRecordSerializable> data;
	
	File loadedFile, lastFile;
	
	public MainFrame() {
		super("HW3 - File & Collection");
		setLayout(new BorderLayout());
		
		// data store
		data = new LinkedHashSet<AccountRecordSerializable>();
		
		// top part panel - GridLayout
		filename = new JLabel("File name:");
		path = new JLabel("Path:");
		load = new JButton("Load");
		load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				readFile();
			}
		});
		insertData = new JLabel("Insert data:"); 
		insertField = new JTextField();
		insertField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_ENTER && loadedFile != null) {
					insertRecord();
					refreshDisp();
				}
			}
		});
		insert = new JButton("Insert");
		insert.setEnabled(false);
		insert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (loadedFile == null) {
					JOptionPane.showMessageDialog(null,"No data, but how do you do this?");
				} else {
					insertRecord();
					refreshDisp();
				}
			}
		});
		deleteData = new JLabel("Delete data:");
		deleteField = new JTextField();
		deleteField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_ENTER && loadedFile != null) {
					deleteRecord();
					refreshDisp();
				}
			}
		});
		delete = new JButton("Delete");
		delete.setEnabled(false);
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (loadedFile == null) {
					JOptionPane.showMessageDialog(null,"No data, but how do you do this?");
				} else {
					deleteRecord();
					refreshDisp();
				}
			}
		});
		
		topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(3, 3));
		topPanel.add(filename);
		topPanel.add(path);
		topPanel.add(load);
		topPanel.add(insertData);
		topPanel.add(insertField);
		topPanel.add(insert);
		topPanel.add(deleteData);
		topPanel.add(deleteField);
		topPanel.add(delete);
		
		
		// middle part panel
		display = new JTextArea("Please load a file.");
		display.setEditable(false);
		display.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		middlePanel = new JPanel();
		middlePanel.setLayout(new BorderLayout());
		middlePanel.add(display);
		
		
		// bottom part panel
		save = new JButton("Save");
		save.setEnabled(false);
		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				if (loadedFile == null) {
					JOptionPane.showMessageDialog(null,"No data, but how do you do this?");
				} else {
					saveFile();
				}
			}
		});
		clear = new JButton("Clear");
		clear.setEnabled(false);
		clear.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				if (loadedFile == null) {
					JOptionPane.showMessageDialog(null,"No data, but how do you do this?");
				} else {
					clear();
				}
			}
		});
		
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(0,2));
		bottomPanel.add(save);
		bottomPanel.add(clear);
		
		
		
		this.add(topPanel,BorderLayout.NORTH);
		this.add(middlePanel,BorderLayout.CENTER);
		this.add(bottomPanel,BorderLayout.PAGE_END);
	}
	
	
	public void readFile() {
		// open with the last loaded folder - runtime live
		JFileChooser chooser = new JFileChooser(lastFile);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			loadedFile = chooser.getSelectedFile(); 
			
			// info updated, start loading file
			ObjectInputStream input = null;
			try {
				input = new ObjectInputStream(new FileInputStream(loadedFile));
				// clear data before reading
				data.clear();
				try {
					while(true) data.add((AccountRecordSerializable) input.readObject());
				} catch( EOFException endOfFileException ) {
					// end of reading
					filename.setText("File name:" + loadedFile.getName());
					path.setText("Path:" + loadedFile.getParent());
					insert.setEnabled(true);
					delete.setEnabled(true);
					save.setEnabled(true);
					clear.setEnabled(true);
					refreshDisp();
				} catch ( ClassNotFoundException classNotFoundException ) {
					JOptionPane.showMessageDialog(null, "Error reading object.");
				}
				input.close();
			} catch(IOException e) {
				JOptionPane.showMessageDialog(null, "Error opening the file.");
			}
		}
	}
	
	public void saveFile() {
		// save - defaults to loaded file (path)
		JFileChooser chooser = new JFileChooser(loadedFile);
		File file2Save;
		if(chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION &&
				(file2Save = chooser.getSelectedFile()) != null) {
			try {
				ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file2Save));
				for(AccountRecordSerializable value : data) output.writeObject(value);
				output.close();
				JOptionPane.showMessageDialog(null, "Sucessfully saved.");
			} catch (FileNotFoundException e) {		
				JOptionPane.showMessageDialog(null, "Error writing the file.");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "IO Error.");
			}
		}
	}
	
	public void insertRecord() {
		String input = insertField.getText();
		// check format
		if (input.matches("^[^:]*:[0-9.]*,[0-9.]*$")) {
			// split values
			String[] values = input.split("[:,]");
			AccountRecordSerializable record = new AccountRecordSerializable();
			record.setName(values[0]);
			record.setScore1(Double.parseDouble(values[1]));
			record.setScore2(Double.parseDouble(values[2]));
			data.add(record);
			insertField.setText("");
		} else {
			JOptionPane.showMessageDialog(null,"Wrong format -- Name:Score1,Score2.");
		}
	}
	
	public void deleteRecord() {
		String data2Remove = deleteField.getText();
		if (data2Remove.isEmpty()) return;
		for(AccountRecordSerializable value : data) {
			// found and remove
			if(data2Remove.equals(value.getName())){
				data.remove(value);
				break;
			}
		}
		deleteField.setText("");
	}
	
	public void refreshDisp(){
		String cont = "";
		for(AccountRecordSerializable value : data) {
			cont += String.format("%-30s\t%.1f\t%.1f%n", value.getName(), value.getScore1(), value.getScore2());
		}
		display.setText(cont);
	}
	
	public void clear(){
		data.clear();
		display.setText("Please load a file.");
		path.setText("Path:");
		filename.setText("File name:");
		if (loadedFile != null) lastFile = loadedFile;
		loadedFile = null;
		insert.setEnabled(false);
		delete.setEnabled(false);
		save.setEnabled(false);
		clear.setEnabled(false);
	}
}
