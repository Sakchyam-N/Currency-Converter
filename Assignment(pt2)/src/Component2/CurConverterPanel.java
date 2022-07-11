package Component2;

import javax.swing.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;
import java.awt.*;

@SuppressWarnings("serial")
public class CurConverterPanel extends JPanel{
	//declaring all the java components and variables needed here
	private JPanel insidePanel;
	private JTextField inputField;
	private JLabel inputLabel;
	private JLabel count;
	private int countConv =0;
	private JLabel result;
	private JComboBox<String> currList;
	private JButton convert;
	private JButton reset;
	private JCheckBox reverse;
	
	private String[] defCurr = {"Japanese yen (JPY)","Euro (EUR)","US Dollars (USD) ","Australian Dollars (AUD)","Canadian Dollars (CAD)","South Korean Won (KRW)","Thai Baht (THB)","United Arab Emirates Dirham (AED)"};
	private String symbolList[] = {"\u00a5","\u20AC","\u0024","A\u0024","C\u0024","\u20A9","\u0E3F","\u062F"}; 
	//arrays lists to store all data loaded from the file
	private ArrayList <String> currencies = new ArrayList<String>();
	private ArrayList <Double> convRate = new ArrayList<Double>();
	private ArrayList <String> symbols = new ArrayList<String>();
	private File file = null;
	private String errors;
	private boolean fileLoaded = false;
	
	//method for menu bar
	JMenuBar setUpMenu() {
		//creating a menu bar
		JMenuBar menuBar = new JMenuBar();
		
		//creating file menu in the menu bar
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F); //using alt + f as mnemonic for file menu
		fileMenu.setIcon(new ImageIcon("fileLogo.png")); // setting image icon for file menu
		
		JMenu help = new JMenu("Help");
		help.setMnemonic(KeyEvent.VK_H);
		help.setIcon(new ImageIcon("helpLogo.png"));
		
		//creating a sub menu named exit ,setting it's mnemonic and image icon
		JMenuItem exit = new JMenuItem("Exit",KeyEvent.VK_X);
		exit.setIcon(new ImageIcon("exitLogo.png"));
		
		//creating a sub menu named about, setting it's mnemonic and image icon
		JMenuItem about = new JMenuItem("About",KeyEvent.VK_A);
		about.setIcon(new ImageIcon("aboutLogo.png"));
		
		//creating a sub menu named open , setting it's mnemonic and image icon 
		JMenuItem open = new JMenuItem("Load File",KeyEvent.VK_A);
		open.setIcon(new ImageIcon("openLogo.png"));
		
		//when exit menu clicked it displays a exit confirmation dialogue box , when yes option pressed exits the program and dialogue box closed if no option is selected
		exit.addActionListener(e->{
			int exitOrNot = JOptionPane.showConfirmDialog(new JFrame(),"Do you want to exit?","Exit Confirm Box",JOptionPane.YES_NO_OPTION );
			if(exitOrNot == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		});
		
		//when about menu clicked it displays a information dialogue box with the author details and use of the program
		about.addActionListener(e->{
			String message = "Author : Sakchyam Nakarmi  \nUniversity ID: 77261137\nThis program converts british pounds to desired currency and vice versa.\nThe currencies rate and symbols are loaded from a .txt file\n Copyright \u00A9 2022";
			JOptionPane.showMessageDialog(new JFrame(),message,"About",JOptionPane.INFORMATION_MESSAGE );
		});
		
		//when open menu is clicked it opens a file chooser 
		open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooseFile = new JFileChooser();
				int fileSelected = chooseFile.showOpenDialog(null);
				
				//when a file is chosen all the array list is cleared and combo box items is cleared , then the selected file from file chooser is assigned to file object
				if(fileSelected == JFileChooser.APPROVE_OPTION) {
					fileLoaded = true;
					currencies.removeAll(currencies); // removing with same array list name to clear past stored values inside the array list
					convRate.removeAll(convRate);
					symbols.removeAll(symbols);
					currList.removeAllItems();
					
					file = chooseFile.getSelectedFile();
					loadFile(file); //calling the method load file and sending file as a parameter
					
					//setting items in the combo box using array list size and index
					for(int i =0;i<currencies.size();i++) {
						currList.addItem(currencies.get(i));
					}
				}
				else {
					//if no file chosen below dialogue box with the following message is displayed
					JOptionPane.showMessageDialog(new JFrame(), "No file chosen!","Not selected",JOptionPane.WARNING_MESSAGE);
				}
			}
			
		});
		//adding all items to menu bar
		menuBar.add(fileMenu);
		menuBar.add(help);
		//adding sub menus
		fileMenu.add(open);
		fileMenu.add(exit);
		help.add(about);
		
		return menuBar;
	}
	
	
	public void loadFile(File file) {
		try {
			errors = "";//setting error as blank so it does not repeat old file errors
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
			Scanner scan = new Scanner(in);
			int lineNum = 1;//to keep track of which line has error
			
			//while the file has next line this loop will keep running
			while(scan.hasNextLine()) {
				//storing each line from the file into the string line
				String line = scan.nextLine();
				//splitting the line into different array items 
				String[] tempArray = line.split(",");
				
				//checking if the array length is 3 or not
				if(tempArray.length == 3) {
					//checking to see if array index zero is empty or not , if it is then the line will not be stored in the array list
					if(tempArray[0].trim().isEmpty()) {
						errors += "Line " +lineNum+": Currency name is missing\n";
					}
					//checking to see if array index one is empty or not , if it is then the line will not be stored in the array list
					else if(tempArray[1].trim().isEmpty()) {
						errors += "Line " +lineNum+": Conversion rate is empty\n";
					}
					//checking to see if array index two is empty or not , if it is then the line will not be stored in the array list
					else if(tempArray[2].trim().isEmpty()) {
						errors += "Line " +lineNum+ ": Currency symbol is empty\n";
					}
					//if none of the array index is empty then the values is stored in their respective array list with use of index
					else {
						try {
							convRate.add(Double.parseDouble(tempArray[1].trim()));//converting conversion rate(array index 1) to double before storing it in the array list 
							currencies.add(tempArray[0].trim());
							symbols.add(tempArray[2].trim());
						}catch(NumberFormatException e) {
							//if the conversion rate is non numeric then the values are not stored in the array lists and an error is displayed  
							errors += "Line " +lineNum+": Conversion rate not in number format\n"; 
						}
					}
				}//checking if : is used as a delimiter
				else if(line.contains(":")) {
					errors += "Line " + lineNum+": Wrong delimiter(i.e :) used\n";
				}//if two , delimiter not found the error displayed
				else {
					errors += "Line " +lineNum+": Delimiters not enough\n";
				}
				lineNum++;
			}
			scan.close();
			
			//displays errors in a dialogue box only if the loaded file has invalid inputs
			if(!errors.isEmpty()) {
				JOptionPane.showMessageDialog(new JFrame(), errors,"Errors in file",JOptionPane.ERROR_MESSAGE);
			}
		}catch(IOException e) {
			JOptionPane.showMessageDialog(new JFrame(), "File could not be found","File not found",JOptionPane.WARNING_MESSAGE);
		}
		
	}
	
	
	public CurConverterPanel(){
		//creating another panel , setting its background color and max size
		insidePanel = new JPanel();
		insidePanel.setMaximumSize(new Dimension(500,35));
		insidePanel.setBackground(Color.darkGray);
		
		// initializing java label component and setting it's text color
		inputLabel = new JLabel("Enter amount to be converted: ");
		inputLabel.setForeground(Color.white);
		
		//initializing and setting the field size to 15 
		inputField = new JTextField(15);
		inputField.setToolTipText("Amount to be converted should be entered here"); // setting tool tip for input field
		
		//initializing convert button , setting it's background color and it's tool tip text
		convert = new JButton("Convert");
		convert.setToolTipText("Press this button to convert the entered value");
		convert.setBackground(Color.green);
		//initializing an object of convert listener class
		ConvertButListener convertListener = new ConvertButListener();
		//when convert button pressed action performed method of the convert listener class gets executed
		convert.addActionListener(convertListener);
		
		//when enter is pressed action performed method of the convert listener class gets executed
		inputField.addActionListener(convertListener);
		
		//initializing combo box but not sending any parameter
		currList = new JComboBox<String> ();
		currList.setToolTipText("List of conversions which can be done");//setting tool tip text for combo box
		currList.setMaximumSize(new Dimension(180,30)); //setting max size of combo box width 180 height 80
		if(fileLoaded) {
			for(int i =0;i<currencies.size();i++) {//setting items in the combo box using array list size and index
				currList.addItem(currencies.get(i));
			}
		}
		else {
			for(int i =0;i<defCurr.length;i++) {
				currList.addItem(defCurr[i]);
			}
			
		}
		
		//initializing count label to keep count of number of conversions done
		count = new JLabel("Conversion Count: "+countConv);
		count.setForeground(Color.white); //setting font color to white
		
		//initializing result label to display converted values in green color
		result = new JLabel("----");
		result.setToolTipText("Converted value is displayed here");
		result.setForeground(Color.green);
		
		//initializing reset button ,setting it's background color to red and setting it's tool tip text
		reset = new JButton("Reset");
		reset.setToolTipText("Press this button to clear all fields and selected checkbox");
		reset.setBackground(Color.red);
		//initializing an object of clear listener class
		ClearListener resetListen = new ClearListener();
		//when reset button is pressed the action performed method of clear listener is called
		reset.addActionListener(resetListen);
		
		//initializing check box
		reverse = new JCheckBox("Reverse Conversion");
		reverse.setToolTipText("Converts the currency to its british pound equivalent"); // setting tool tip for reverse check box
		reverse.setBackground(Color.darkGray); // setting background color for the check box
		reverse.setForeground(Color.white); // setting font color for the check box
		
		//aligning all components to the center
		currList.setAlignmentX(CENTER_ALIGNMENT); 
		inputLabel.setAlignmentX(CENTER_ALIGNMENT);
		inputField.setAlignmentX(CENTER_ALIGNMENT);
		convert.setAlignmentX(CENTER_ALIGNMENT);
		reverse.setAlignmentX(CENTER_ALIGNMENT);
		reset.setAlignmentX(CENTER_ALIGNMENT);
		count.setAlignmentX(CENTER_ALIGNMENT);
		result.setAlignmentX(CENTER_ALIGNMENT);
		
		//adding all components to the panel 
		add(currList);
		add(insidePanel);
		//adding input label and field to another nested panel for layout purpose
		insidePanel.add(inputLabel);
		insidePanel.add(inputField);
		
		//resetting inside panel then adding convert and reset button to it 
		insidePanel = new JPanel();
		insidePanel.setMaximumSize(new Dimension(500,35));
		insidePanel.setBackground(Color.darkGray);
		add(insidePanel);
		insidePanel.add(convert);
		insidePanel.add(reset);
		add(reverse);
		add(count);
		add(result);
		
		
		// panel's size , layout and background color is set here 
		setPreferredSize(new Dimension(500,200));
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		setBackground(Color.darkGray);
		
	}
	
	//inside class convert listener is a child class of interface class action listener
	private class ConvertButListener implements ActionListener{
		//overriding action performed abstract class 
		public void actionPerformed(ActionEvent event) {
			// storing the combo box option selected by user in this int variable
			int comboSelected = currList.getSelectedIndex();
			//storing the input from user in this string variable and trimming any white extra space
			String temp = inputField.getText().trim();
			
			//try and catch used for non numeric input entered in the input field
			try {
				if(temp.isEmpty()) {//checking if the input field is empty 
					// error message displayed on a new frame if input field left empty 
					JOptionPane.showMessageDialog(new JFrame(),"Input field is empty","Empty",JOptionPane.WARNING_MESSAGE ); 
				}
				else {
					//when user enters valid input in the input field then the user input which was stored in string
					//is converted to double 
					double input = Double.parseDouble(temp);
					double resultVal =0; //to store converted value
					double conversionRate=0; // to store conversion rate according to the option selected in the combo box
					String symbol=""; // to store symbol according to the option selected in the combo box
					
					if(fileLoaded) {
						//the data in array list is accessed according to the option selected in the combo box
						// for loop is ran to match which index's data to be retrieved
						for(int index =0;index<currencies.size();index++) {
							if(comboSelected == index) {
								conversionRate = convRate.get(index);
								symbol = symbols.get(index);
								countConv++;
							}
						}
					}
					else {
						//when file is not loaded then this switch case is used for the first time only
						switch (comboSelected) {
							case 0: //Japanese yen
								conversionRate = 157.07;
								countConv++;
								break;
							case 1://Euro
								conversionRate = 1.20;
								countConv++;
								break;
							case 2: //American Dollar
								conversionRate = 1.36;
								countConv++;
								break;
							case 3: //Australian Dollar
								conversionRate = 1.89;
								countConv++;
								break;
							case 4://Canadian Dollar
								conversionRate =1.72;
								countConv++;
								break;
							case 5: //South Korean Won
								conversionRate = 1627.30;
								countConv++;
								break;
							case 6://Thai Baht
								conversionRate = 45.67;
								countConv++;
								break;
							case 7://United Arab Emirates Dirham
								conversionRate = 4.99;
								countConv++;
								break;
								
						}
						symbol = symbolList[comboSelected]; 
					}
					
					//when reverse conversion is selected the formula changes accordingly and symbol is set to British pound
					if(reverse.isSelected()==true) {
						resultVal = input / conversionRate;
						symbol = "\u00A3";
					}
					else {
						resultVal = input* conversionRate;
					}
					
					@SuppressWarnings("resource")
					//Formatter used to limit the result to two decimal number
					Formatter formattedVal = new Formatter();
					formattedVal.format("%.2f", resultVal);
					// converting the result to string then setting it to the result label with it's respective symbol
					result.setText("Result: "+symbol+" "+formattedVal.toString()); 
					count.setText("Conversion Count: "+countConv);
					
				}		
			}catch(NumberFormatException error) {
				//when the user input is non numeric this error dialogue box appears on screen
				JOptionPane.showMessageDialog(new JFrame(),"Input value is non-numeric","Invalid input",JOptionPane.WARNING_MESSAGE );
			}
		}	
	}
	
	
	private class ClearListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			//setting result label to ---
			result.setText("---");
			//reseting the int to 0 again
			countConv =0;
			count.setText("Conversion Count: "+countConv);
			//clearing input field by setting nothing inside the field
			inputField.setText("");
			// combo box's 0 index to be selected
			currList.setSelectedIndex(0);
			// uncheck the check box
			reverse.setSelected(false);
		}
	}
}