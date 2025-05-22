package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import service.db;

import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextPane;


public class cuadroAyuda {
	
	String texto;
	
	public cuadroAyuda(String Texto) {
		
		super();
		this.texto = texto;
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		db db = new db();
		Dimension size = toolkit.getScreenSize();
		URL urlAddUser = GUI.class.getResource("/negociado.png");
	    ImageIcon addUserIcon = new ImageIcon(urlAddUser);
		f.setTitle("Ayuda");
		f.setSize(new Dimension(688, 439));
		f.setPreferredSize(new Dimension(300, 300));
		f.setLocation(size.width/4, size.height/4);
		f.setIconImage(addUserIcon.getImage());
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.getContentPane().setLayout(null);
		
		JPanel panelNegociado = new JPanel();
		
		panelNegociado.setBounds(25, 23, 625, 354);

		f.getContentPane().add(panelNegociado);

		panelNegociado.setLayout(null);
		
		JTextPane textPane = new JTextPane();
		textPane.setBounds(10, 11, 605, 332);
		panelNegociado.add(textPane);
		
		
		
		
		
		f.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.out.println("Uncomment following to open another window!");
				// MainPage m = new MainPage();
				// m.setVisible(true);
				
				e.getWindow().dispose();
				System.out.println("JFrame Closed!");

			}
		});
		
		f.setVisible(true);
	}

	private JFrame f = new JFrame("Negociados");
}
