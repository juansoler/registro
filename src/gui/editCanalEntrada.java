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


public class editCanalEntrada {
	
	public editCanalEntrada() {
		super();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		db db = new db();
		Dimension size = toolkit.getScreenSize();
		URL urlAddUser = GUI.class.getResource("/negociado.png");
	    ImageIcon addUserIcon = new ImageIcon(urlAddUser);
		f.setTitle("Canales de entrada");
		f.setSize(new Dimension(412, 339));
		f.setPreferredSize(new Dimension(300, 300));
		f.setLocation(size.width/4, size.height/4);
		f.setIconImage(addUserIcon.getImage());
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.getContentPane().setLayout(null);
		
		JPanel panelNegociado = new JPanel();
		
		panelNegociado.setBounds(25, 23, 352, 249);

		f.getContentPane().add(panelNegociado);

		panelNegociado.setLayout(null);

		canalEntradaTextField = new JTextField();
		canalEntradaTextField.setBounds(96, 37, 180, 43);
		canalEntradaTextField.setBorder(new TitledBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Negociados", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), "Canal de entrada", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelNegociado.add(canalEntradaTextField);
		canalEntradaTextField.setAlignmentX(10.0f);
		canalEntradaTextField.setColumns(20);
		
		JComboBox areaComboBox = new JComboBox();
		areaComboBox.setBounds(96, 91, 180, 20);
		
		if (Login.CANALES.size() > 0) {
			for (String string : Login.CANALES) {
				areaComboBox.addItem(string);
			}
		}
	
		
		
		
		
		panelNegociado.add(areaComboBox);
		
		JButton btnGuardar = new JButton("Guardar");
		btnGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				areaComboBox.addItem(canalEntradaTextField.getText());
				db db = new db();
				db.connect();
				db.saveCanalEntrada(canalEntradaTextField.getText());
				db.close();
				canalEntradaTextField.setText("");

			}
			
		});
		
		btnGuardar.setBounds(92, 146, 91, 23);

		JButton btnEliminar = new JButton("Eliminar");
		btnEliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				db db = new db();
				db.connect();
				db.eliminarCanalEntrada(areaComboBox.getSelectedItem().toString());
				areaComboBox.removeItemAt(areaComboBox.getSelectedIndex());
				//db.saveUser(negociadoTextField.getText().toLowerCase(), (String)areaComboBox.getSelectedItem());
				db.close();
				canalEntradaTextField.setText("");			}
			
		});
		
		btnEliminar.setBounds(204, 146, 91, 23);
		
		panelNegociado.add(btnGuardar);
		panelNegociado.add(btnEliminar);
		
		f.addWindowListener(new java.awt.event.WindowAdapter() {

			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
					
					db.connect();
					Login.CANALES = db.getCanales();
					db.close();
				}
			});
		
		
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
	private JTextField canalEntradaTextField;
}
