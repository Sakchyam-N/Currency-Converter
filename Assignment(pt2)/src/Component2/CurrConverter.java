package Component2;

import javax.swing.*;

public class CurrConverter {

	public static void main(String[] args) {
		JFrame frame = new JFrame("Currency Converter");
		ImageIcon image = new ImageIcon("logo.png");
		frame.setIconImage(image.getImage());
		CurConverterPanel panel = new CurConverterPanel();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setJMenuBar(panel.setUpMenu());
		frame.getContentPane().add(panel);
		
		frame.setVisible(true);
		frame.pack();

	}

}
