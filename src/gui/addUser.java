package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import service.db;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class addUser {
	
	public addUser() {
		super();
		Toolkit toolkit = Toolkit.getDefaultToolkit();

		Dimension size = toolkit.getScreenSize();
		URL urlAddUser = GUI.class.getResource("/adduser.png");
	    ImageIcon addUserIcon = new ImageIcon(urlAddUser);
		f.setSize(new Dimension(1005, 343));
		f.setPreferredSize(new Dimension(300, 300));
		f.setLocation(size.width/4, size.height/4);
		f.setIconImage(addUserIcon.getImage());
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(29, 23, 320, 159);
		f.getContentPane().add(panel);
		panel.setLayout(null);
		db db = new db();
		
		userTextField = new JTextField();
		userTextField.setBounds(72, 25, 180, 43);
		userTextField.setBorder(new TitledBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Usuario", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), "Usuario", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(userTextField);
		userTextField.setAlignmentX(10.0f);
		userTextField.setColumns(20);
		
		JComboBox areaComboBox = new JComboBox();
		areaComboBox.setBorder(new TitledBorder(null, "Role negociado", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		areaComboBox.setBounds(58, 91, 173, 43);
		
//		areaComboBox.addItem("Jefe1");
//		areaComboBox.addItem("Jefe2");
//		areaComboBox.addItem("Jefe3");
//		areaComboBox.addItem("Jefe4");
//		areaComboBox.addItem("Jefe5");
		
		for (String str : Login.NEGOCIADOS) {
			areaComboBox.addItem(str);
		}
		panel.add(areaComboBox);
		
		JButton btnGuardar = new JButton("Guardar Usuario");
		btnGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				db db = new db();
				db.connect();
				if (db.saveUser(userTextField.getText().toLowerCase(), (String)areaComboBox.getSelectedItem())) {
					JOptionPane.showMessageDialog(null, "Se ha creado con éxito el usuario " + userTextField.getText().toLowerCase() + " en el área " + (String)areaComboBox.getSelectedItem());

				}else {
					JOptionPane.showMessageDialog(null, "Error al guardar el usuario " + userTextField.getText().toLowerCase());

				}
				db.close();
			}
			
		});
		btnGuardar.setBounds(135, 206, 155, 23);
		f.getContentPane().add(btnGuardar);
		
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBounds(418, 23, 479, 225);
		f.getContentPane().add(panel_1);
		
		jefeTextField = new JTextField();
		jefeTextField.setColumns(20);
		jefeTextField.setBorder(new TitledBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Jefes", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), "Usuario", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jefeTextField.setAlignmentX(10.0f);
		jefeTextField.setBounds(35, 93, 180, 43);
		panel_1.add(jefeTextField);
		
		JComboBox comboBoxJefes = new JComboBox();
		comboBoxJefes.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Role jefe", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		comboBoxJefes.setBounds(154, 11, 173, 55);
		panel_1.add(comboBoxJefes);
		
		JButton btnGuardarJefe = new JButton("Crear usuario JEFE nuevo");
		btnGuardarJefe.setBounds(248, 107, 205, 23);
		panel_1.add(btnGuardarJefe);
		
		JComboBox comboBoxUsuarioExistente = new JComboBox();
		comboBoxUsuarioExistente.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Usuario ya existente", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		comboBoxUsuarioExistente.setBounds(35, 159, 173, 55);
		panel_1.add(comboBoxUsuarioExistente);
		
		JButton buttonAddExistsUser = new JButton("Asignar un usuario existente");
		buttonAddExistsUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				db.connect();
				db.updateJefe(((String)comboBoxUsuarioExistente.getSelectedItem()).split(";")[0], (String)comboBoxJefes.getSelectedItem());
				db.close();
			}
		});
		buttonAddExistsUser.setBounds(248, 180, 205, 23);
		panel_1.add(buttonAddExistsUser);
		
		JButton btnNewButtonAyuda = new JButton("Ayuda");
		btnNewButtonAyuda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JTextPane jtextPane = new JTextPane();
				jtextPane.setText("Role Jefe, se asocia al Jefe funcional de cada departamento.  "
						+ "\n "
						+ "Usuario, es el usuario asociado a Role Jefe."
						+ "\n"
						+ "Crear usuario Jefe nuevo si no existe el usuario. Escribir el nombre en el campo Usuario y pulsar el botón Crear Usuario"
						+ "\n"
						+ "Si un usuario ha pasado de una Jefatura Funcional a otra, se puede asignar este Usuario a su nueva Jefatura, para ello seleccionarlo en Usuario ya existente y pulsar el botón asignar.");
				JOptionPane.showMessageDialog(null, jtextPane);

			}
		});
		btnNewButtonAyuda.setBounds(795, 271, 89, 23);
		f.getContentPane().add(btnNewButtonAyuda);
		
		btnGuardarJefe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				db.connect();
				int posicionJefe = Integer.parseInt(db.getPosicion((String)comboBoxJefes.getSelectedItem()));
				db.close();
				
				if (posicionJefe ==1) {
					db.connect();
					db.saveJefe(jefeTextField.getText().toLowerCase(), (String)comboBoxJefes.getSelectedItem(), "Jefe1");
					db.close();

				}else {
					db.connect();
					db.saveJefe(jefeTextField.getText().toLowerCase(), (String)comboBoxJefes.getSelectedItem(), "Jefes");
					db.close();
				}
				
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
		
		db.connect();
		ArrayList<String> negociadosJefes = db.getNegociadosJefes();
		db.close();
		
		for (String me : negociadosJefes) {
			comboBoxJefes.addItem(me);
		}
		
		db.connect();
		ArrayList<String> usuarios = db.mostrarUsuariosHuerfanos();
		db.close();
		for (String me : usuarios) {
			comboBoxUsuarioExistente.addItem(me);
		}
		f.setVisible(true);
	}

	private JFrame f = new JFrame("Crear usuario");
	private JTextField userTextField;
	private JTextField jefeTextField;
}
