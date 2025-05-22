package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;
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
import javax.swing.JPasswordField;
import javax.swing.JCheckBox;


public class changePasswordAdmin {
	
	protected boolean showPassword = false;
	protected boolean showPasswordNew = false;
	protected boolean showPasswordRetyped = false;

	public changePasswordAdmin() {
		super();
		Toolkit toolkit = Toolkit.getDefaultToolkit();

		Dimension size = toolkit.getScreenSize();
		URL urlAddUser = GUI.class.getResource("/adduser.png");
	    ImageIcon addUserIcon = new ImageIcon(urlAddUser);
		f.setSize(new Dimension(543, 372));
		f.setPreferredSize(new Dimension(300, 300));
		f.setLocation(size.width/4, size.height/4);
		f.setIconImage(addUserIcon.getImage());
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 23, 482, 242);
		f.getContentPane().add(panel);
		panel.setLayout(null);
		
		passwordField = new JPasswordField();
		passwordField.setColumns(10);
		passwordField.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Contrase\u00F1a antigua", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		passwordField.setBounds(28, 25, 255, 49);
		panel.add(passwordField);
		
		JCheckBox checkBox_passwordOld = new JCheckBox("Mostrar contrase\u00F1a");
		checkBox_passwordOld.setBounds(307, 42, 169, 23);
		panel.add(checkBox_passwordOld);
		
		checkBox_passwordOld.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				showPassword = !showPassword;

				if (showPassword) {
					
					passwordField.setEchoChar((char)0);
					
					
				}else {
					passwordField.setEchoChar('\u2022');
					
				}
				
			}
		});
		
		JCheckBox checkBox_passwordNew = new JCheckBox("Mostrar contrase\u00F1a");
		checkBox_passwordNew.setBounds(307, 113, 169, 23);
		panel.add(checkBox_passwordNew);
		
		checkBox_passwordNew.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				showPasswordNew = !showPasswordNew;

				if (showPasswordNew) {
					
					passwordField_new.setEchoChar((char)0);
					
					
				}else {
					passwordField_new.setEchoChar('\u2022');
					
				}
				
			}
		});
		
		passwordField_new = new JPasswordField();
		passwordField_new.setColumns(10);
		passwordField_new.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Contrase\u00F1a nueva", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		passwordField_new.setBounds(28, 96, 255, 49);
		panel.add(passwordField_new);
		
		JCheckBox checkBox_retypedPassword = new JCheckBox("Mostrar contrase\u00F1a");
		checkBox_retypedPassword.setBounds(307, 185, 169, 23);
		panel.add(checkBox_retypedPassword);
		
		checkBox_retypedPassword.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				showPasswordRetyped = !showPasswordRetyped;

				if (showPasswordRetyped) {
					
					passwordField_retyped.setEchoChar((char)0);
					
					
				}else {
					passwordField_retyped.setEchoChar('\u2022');
					
				}
				
			}
		});
		
		passwordField_retyped = new JPasswordField();
		passwordField_retyped.setColumns(10);
		passwordField_retyped.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Repetir contrase\u00F1a nueva", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		passwordField_retyped.setBounds(28, 168, 255, 49);
		panel.add(passwordField_retyped);
		
//		areaComboBox.addItem("Jefe1");
//		areaComboBox.addItem("Jefe2");
//		areaComboBox.addItem("Jefe3");
//		areaComboBox.addItem("Jefe4");
//		areaComboBox.addItem("Jefe5");
		
		
		
		JButton btnGuardar = new JButton("Actualizar contrase\u00F1a");
		btnGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
				if (String.valueOf(passwordField_new.getPassword()).equals(String.valueOf(passwordField_retyped.getPassword()))) {
					
					db db = new db();
					db.connect();
					db.updateAdminPassword(String.valueOf(passwordField.getPassword()), String.valueOf(passwordField_new.getPassword()));
					db.close();
					f.dispose();
				}else {
					JOptionPane.showMessageDialog(null, "La contraseña no coincide");
				}
				
			}
			
		});
		btnGuardar.setBounds(191, 289, 155, 23);
		f.getContentPane().add(btnGuardar);
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

	private JFrame f = new JFrame("Cambiar contraseña administrador");
	private JPasswordField passwordField;
	private JPasswordField passwordField_new;
	private JPasswordField passwordField_retyped;
}
