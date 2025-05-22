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


public class editNegociados {
	
	public editNegociados() {
		super();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		db db = new db();
		Dimension size = toolkit.getScreenSize();
		URL urlAddUser = GUI.class.getResource("/negociado.png");
	    ImageIcon addUserIcon = new ImageIcon(urlAddUser);
		f.setSize(new Dimension(793, 338));
		f.setPreferredSize(new Dimension(300, 300));
		f.setLocation(size.width/4, size.height/4);
		f.setIconImage(addUserIcon.getImage());
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.getContentPane().setLayout(null);
		
		JPanel panelNegociado = new JPanel();
		JPanel panelJefes = new JPanel();
		chckbxGestionado = new JCheckBox("Gestionado");
		chckbxGestionado.setBounds(150, 118, 97, 23);
		
		panelNegociado.setBounds(25, 23, 352, 249);
		panelJefes.setBounds(391, 23, 340, 249);

		f.getContentPane().add(panelNegociado);
		f.getContentPane().add(panelJefes);

		panelNegociado.setLayout(null);
		panelJefes.setLayout(null);

		negociadoTextField = new JTextField();
		negociadoTextField.setBounds(96, 37, 180, 43);
		negociadoTextField.setBorder(new TitledBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Negociados", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), "Negociados", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelNegociado.add(negociadoTextField);
		negociadoTextField.setAlignmentX(10.0f);
		negociadoTextField.setColumns(20);
		
		cargoTextField = new JTextField();
		cargoTextField.setBounds(45, 37, 180, 43);
		cargoTextField.setBorder(new TitledBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Cargos", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), "Cargos", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelJefes.add(cargoTextField);
		cargoTextField.setAlignmentX(10.0f);
		cargoTextField.setColumns(20);
		
		JComboBox areaComboBox = new JComboBox();
		areaComboBox.setBounds(96, 91, 180, 20);
//		areaComboBox.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				db.connect();
//				chckbxGestionado.setSelected(!db.isNotGestionado(areaComboBox.getSelectedItem().toString()));
//				db.close();
//				
//			}
//		});
		
		
		db.connect();
		ArrayList<String> negociadosJefes = db.getNegociadosJefes();
		db.close();
		
		JComboBox jefeComboBox = new JComboBox();
		jefeComboBox.setBounds(35, 91, 180, 20);
		
		JLabel labelPosicion = new JLabel("-");
		
		JComboBox posicionComboBox = new JComboBox();
		posicionComboBox.setBounds(235, 52, 76, 20);
		
		int contadorPosicion = 0;
		
		
		jefeComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				db.connect();
				if (jefeComboBox.getSelectedItem() != null){
					labelPosicion.setText(db.getCargoPosicion(jefeComboBox.getSelectedItem().toString()));
				}else {
					labelPosicion.setText("");
				}
				db.close();
			}
		});
		
		for (String string : Login.NEGOCIADOS) {
			areaComboBox.addItem(string);
		}
		
		for (Entry<Integer, String> entry : Login.CARGOS.entrySet()) {
//			db.connect();
//			posicionComboBox.removeItem(db.getPosicion(entry.getKey()));
//			db.close();
		    jefeComboBox.addItem(entry.getValue());
		    contadorPosicion++;
		}
		
		posicionComboBox.addItem(contadorPosicion+1);
		
		
		panelNegociado.add(areaComboBox);
		panelJefes.add(jefeComboBox);
		panelJefes.add(posicionComboBox);
		
		JButton btnGuardar = new JButton("Guardar");
		btnGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				areaComboBox.addItem(negociadoTextField.getText());
				db db = new db();
				db.connect();
				db.saveNegociado(negociadoTextField.getText());
				db.close();
				negociadoTextField.setText("");

			}
			
		});
		
		btnGuardar.setBounds(92, 146, 91, 23);
		
		JButton btnGuardarCargo = new JButton("Guardar");
		btnGuardarCargo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				db db = new db();
				db.connect();
				if (!db.saveCargo(cargoTextField.getText(), Integer.parseInt(posicionComboBox.getSelectedItem().toString()))) {
					JOptionPane.showMessageDialog(null, "Ese cargo ya está ocupado");
				}else {
					jefeComboBox.addItem(cargoTextField.getText());
					posicionComboBox.addItem((int) posicionComboBox.getSelectedItem()+1);
					posicionComboBox.removeItem(posicionComboBox.getSelectedItem());
				}
				db.close();
				cargoTextField.setText("");
			}
			
		});
		
		btnGuardarCargo.setBounds(92, 146, 91, 23);

		JButton btnEliminar = new JButton("Eliminar");
		btnEliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				db db = new db();
				db.connect();
				db.eliminarNegociado(areaComboBox.getSelectedItem().toString());
				areaComboBox.removeItemAt(areaComboBox.getSelectedIndex());
//				chckbxGestionado.setSelected(false);
				//db.saveUser(negociadoTextField.getText().toLowerCase(), (String)areaComboBox.getSelectedItem());
				db.close();
				negociadoTextField.setText("");			}
			
		});
		
		btnEliminar.setBounds(204, 146, 91, 23);
		
		JButton btnEliminarCargo = new JButton("Eliminar");
		btnEliminarCargo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
				db db = new db();
				db.connect();
				posicionComboBox.addItem(Integer.parseInt(db.getCargoPosicion(jefeComboBox.getSelectedItem().toString())));
				db.eliminarCargo(jefeComboBox.getSelectedItem().toString());

				jefeComboBox.removeItemAt(jefeComboBox.getSelectedIndex());

				//db.saveUser(negociadoTextField.getText().toLowerCase(), (String)areaComboBox.getSelectedItem());
				db.close();
				
				db.connect();
				if (jefeComboBox.getSelectedItem() != null){
 					labelPosicion.setText(db.getCargoPosicion(jefeComboBox.getSelectedItem().toString()));
 				}else {
 					labelPosicion.setText("");
 				}
				db.close();
				}
			
		});
		
		btnEliminarCargo.setBounds(204, 146, 91, 23);
		
		panelNegociado.add(btnGuardar);
		panelNegociado.add(btnEliminar);
		
		
		
		//panelNegociado.add(chckbxGestionado);
		panelJefes.add(btnGuardarCargo);
		panelJefes.add(btnEliminarCargo);
		
		JLabel labelPosicionText = new JLabel("Posici\u00F3n:");
		labelPosicionText.setBounds(225, 94, 70, 14);
		panelJefes.add(labelPosicionText);
		
		
		labelPosicion.setBounds(305, 94, 25, 14);
		panelJefes.add(labelPosicion);
		
		f.addWindowListener(new java.awt.event.WindowAdapter() {

			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
					db.connect();
					Login.CARGOS = db.getCargos();
					db.close();
					db.connect();
					Login.NEGOCIADOS = db.getNegociados();
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
	private JTextField negociadoTextField;
	private JTextField cargoTextField;
	private JCheckBox chckbxGestionado;
}
