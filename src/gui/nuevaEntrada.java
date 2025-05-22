package gui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.print.attribute.standard.JobSheets;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import service.CryptoException;
import service.CryptoUtils;
import service.db;

import java.awt.Desktop.Action;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultEditorKit.CopyAction;
import javax.swing.text.DefaultEditorKit.CutAction;
import javax.swing.text.DefaultEditorKit.PasteAction;

import models.entrada;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.ScrollPaneConstants;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Desktop;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.EtchedBorder;
import javax.swing.SpringLayout;

public class nuevaEntrada {

	private JFrame f = new JFrame("Nueva entrada");

	private JList list = new JList();
	private DefaultListModel<File> defaultlistAntecedentes = new DefaultListModel<File>();
	private DefaultListModel<File> defaultlistSalida = new DefaultListModel<File>();
	private DateFormat dateFormat_normal = new SimpleDateFormat("dd/MM/yyyy");
	private DateFormat timeFormat = new SimpleDateFormat("HH:mm");

	private DefaultListModel<File> defaultlist = new DefaultListModel<File>();

	private JList listDragAntecedentes = new JList();
	private JList listDragSalida = new JList();
	private JScrollPane scrollPane;
	int valueScroll = 0;


	private JList listDrag = new JList();
	Date date = new Date();

	File file = null;
	File antecedentes = null;
	UtilDateModel model;
	JDatePanelImpl datePanel;
	JDatePickerImpl datePicker;
	private JTextField asuntoText;
	JButton guardar, cancelar;
	int entradaIdAntecedente = -1;
	listaUsuarios ventana;
	public entrada entrada;
	public boolean terminado = false;
//	JTextArea textObservaciones;

	private JPanel panel_observaciones;

	private JTextArea textObservaciones;
	private JTextArea destinatariosField;
	private JTextField textFieldnumEntrada;

	private db db;

	private DefaultTableModel tablemodel;

	private JTable tableSalida;

	private Vector dataSalida;

	private JScrollPane scrollSalida;

	private JScrollPane dragSalida;

	private JPanel panel_salida;

	private DefaultTableModel tablemodelAntecedentes;

	private JScrollPane scrollPaneAntecedentes;

	private JTable tableAntecedentes;

	private Vector dataAntecedentes;

	private JScrollPane scrollPaneEntrada;

	private Vector dataEntrada;

	private DefaultTableModel tablemodelEntrada;

	private JTable tableEntrada;
	
	
	// probar usar super.refresh() pero con public void windowClosed(java.awt.event.WindowEvent e) {

	
//	public void refresh() {
//		try {
//			System.out.println("Refresh nueva entrada");
//			super.refresh();
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public nuevaEntrada() {
		URL url = GUI.class.getResource("/entrada.png");
		ImageIcon icon = new ImageIcon(url);
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();

		Dimension size = toolkit.getScreenSize();

		f.setLocation(size.width/5, size.height/7);
		f.setIconImage(icon.getImage());
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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

		
		
		db = new db();
		model = new UtilDateModel(date);
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		datePanel = new JDatePanelImpl(model);

		datePicker = new JDatePickerImpl(datePanel);
		SpringLayout springLayout = (SpringLayout) datePicker.getLayout();
		springLayout.putConstraint(SpringLayout.SOUTH, datePicker.getJFormattedTextField(), 0, SpringLayout.SOUTH, datePicker);
		datePicker.setBounds(112, 0, 120, 27);

		f.setSize(852, 793);
		JPanel panel_asunto = new JPanel();
		panel_asunto.setBounds(10, 5, 816, 218);
		JPanel panel_adjuntos = new JPanel();
		panel_adjuntos.setBounds(10, 234, 816, 105);
		f.getContentPane().setLayout(null);
		JPopupMenu menu = new JPopupMenu();
		CutAction cut = new DefaultEditorKit.CutAction();
		cut.putValue(CutAction.NAME, "Cut");
		cut.putValue(CutAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
		menu.add(cut);

		CopyAction copy = new DefaultEditorKit.CopyAction();
		copy.putValue(CopyAction.NAME, "Copy");
		copy.putValue(CopyAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
		menu.add(copy);

		PasteAction paste = new DefaultEditorKit.PasteAction();
		paste.putValue(PasteAction.NAME, "Paste");
		paste.putValue(PasteAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
		menu.add(paste);
		panel_asunto.setLayout(null);

		JLabel lblFechaDeEntrada = new JLabel("Fecha de entrada: ");
		lblFechaDeEntrada.setBounds(10, 0, 92, 14);
		panel_asunto.add(lblFechaDeEntrada);
//		f.getContentPane().add(asuntoText);
		panel_asunto.add(datePicker);

		f.getContentPane().add(panel_asunto);

		JComboBox comboArea = new JComboBox();
		comboArea.setBorder(new TitledBorder(null, "Area:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		comboArea.setBounds(10, 33, 182, 41);

		JComboBox comboBoxCanalEntrada = new JComboBox();

		comboArea.addItem("Todos");
		
		for (String str : Login.NEGOCIADOS) {
			comboArea.addItem(str);
		}
		
		if (Login.CANALES.size() > 0) {
			for (String string : Login.CANALES) {
				comboBoxCanalEntrada.addItem(string);
			}
		}
	
		/* 
		comboArea.addItem("Operaciones");
		comboArea.addItem("Personal");
		comboArea.addItem("Apoyo");
		comboArea.addItem("Expedientes");
		comboArea.addItem("OGE");
		comboArea.addItem("SIGO");
		comboArea.addItem("Varios negociados");
		comboArea.addItem("Ayudantia");
		comboArea.addItem("OPC");
		comboArea.addItem("Otros");
		*/
		
		panel_asunto.add(comboArea);
		
		JButton btnAgregarDestinatario = new JButton("Agregar area");
		btnAgregarDestinatario.setBounds(202, 33, 120, 41);
		btnAgregarDestinatario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("comboArea selected " + comboArea.getSelectedItem().toString());
				if (comboArea.getSelectedItem().toString().equals("Todos")) {	
					System.out.println("todos selected");
					for (String str : Login.NEGOCIADOS) {
						if (destinatariosField.getText().equals("")) {
							destinatariosField.append(str);
							comboArea.removeItem(str);
							comboArea.removeItem("Todos");
						}else {
							destinatariosField.append("; " + str);
							comboArea.removeItem(str);
							comboArea.removeItem("Todos");
						}
					}
					comboArea.removeAllItems();
					return;
				}
				
//				if (comboArea.getSelectedItem().toString().equals("Otro") && destinatariosField.getText().length() == 0) {
//					JOptionPane.showMessageDialog(null, "La lista de destinatarios no puede empezar por Otro, debe empezar por un destinatario.");
//					return;
//				}
				
//				db.connect();
//	    		if (db.isNotGestionado(comboArea.getSelectedItem().toString())) {
//					int opcionNegociado = -1;
//	    			String[] optionsNegociado = { "Si, agregar de todos modos", "Cancelar" };
//					
//					opcionNegociado = JOptionPane.showOptionDialog(null,
//							"El destinatario "+  comboArea.getSelectedItem().toString() + " es un Negociado no gestionado, por lo tanto no tiene acceso a esta Aplicación",
//							"Click en una opcion", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
//							null, optionsNegociado, optionsNegociado[0]);
//					db.close();
//	        		if (opcionNegociado == 1) {
//	        			return;
//	        		}
//	    		}else {
//		        	db.close();
//	    			
//	    		}
//	        	db.close();
				
				if (destinatariosField.getText().equals("")) {
					destinatariosField.append(comboArea.getSelectedItem().toString());
					comboArea.removeItem(comboArea.getSelectedItem());
					comboArea.removeItem("Todos");

				}else {
					destinatariosField.append("; " + comboArea.getSelectedItem().toString());
					comboArea.removeItem(comboArea.getSelectedItem());
					comboArea.removeItem("Todos");
				}
				
				comboArea.setSelectedIndex(-1);
			}
		});
		panel_asunto.add(btnAgregarDestinatario);
		
		JComboBox comboBoxJefes = new JComboBox();
		comboBoxJefes.setBorder(new TitledBorder(null, "Jefes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		comboBoxJefes.setBounds(332, 34, 202, 39);
		panel_asunto.add(comboBoxJefes);
		comboBoxJefes.addItem("Todos");

		for (Entry<Integer, String> entry : Login.CARGOS.entrySet()) {
			comboBoxJefes.addItem(entry.getValue());
		}
		
		JButton btnAgregarJefes = new JButton("Agregar jefes");
		JTextArea destinatariosJefes = new JTextArea();

		btnAgregarJefes.setBounds(544, 33, 120, 41);
		
		btnAgregarJefes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (comboBoxJefes.getSelectedItem().toString().equals("Todos")) {
					
					for (Entry<Integer, String> entry : Login.CARGOS.entrySet()) {
						if (destinatariosJefes.getText().equals("")) {
							
							destinatariosJefes.append(entry.getValue());
							comboBoxJefes.removeItem(entry.getValue());
							comboBoxJefes.removeItem("Todos");

						}else {
							destinatariosJefes.append(";" + entry.getValue());
							comboBoxJefes.removeItem(entry.getValue());
							comboBoxJefes.removeItem("Todos");
						}
						
					}
					
					comboBoxJefes.removeAllItems();
					return;
				}
				
				
				
				
				if (destinatariosJefes.getText().equals("")) {
					destinatariosJefes.append(comboBoxJefes.getSelectedItem().toString());
					comboBoxJefes.removeItem(comboBoxJefes.getSelectedItem());
					comboBoxJefes.removeItem("Todos");

				}else {
					destinatariosJefes.append("; " + comboBoxJefes.getSelectedItem().toString());
					comboBoxJefes.removeItem(comboBoxJefes.getSelectedItem());
					comboBoxJefes.removeItem("Todos");
				}
				comboBoxJefes.setSelectedIndex(-1);
			}
		});
		panel_asunto.add(btnAgregarJefes);
		
		textFieldnumEntrada = new JTextField();
		textFieldnumEntrada.setLocation(170, 87);
		textFieldnumEntrada.setSize(new Dimension(625, 40));
		textFieldnumEntrada.setMinimumSize(new Dimension(50, 20));
		textFieldnumEntrada.setPreferredSize(new Dimension(100, 40));
		textFieldnumEntrada.setBorder(new TitledBorder(null, "N\u00BA Entrada", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_asunto.add(textFieldnumEntrada);
		textFieldnumEntrada.setColumns(20);
		
		JCheckBox chckbxConfidencial = new JCheckBox("Confidencial");
		chckbxConfidencial.setBounds(238, 0, 100, 23);
		chckbxConfidencial.setPreferredSize(new Dimension(100, 23));
		chckbxConfidencial.setMinimumSize(new Dimension(100, 23));
		chckbxConfidencial.setMaximumSize(new Dimension(100, 23));
		chckbxConfidencial.setHorizontalAlignment(SwingConstants.CENTER);
		panel_asunto.add(chckbxConfidencial);
		
		JCheckBox chckbxUrgente = new JCheckBox("Urgente");
		chckbxUrgente.setBounds(340, 0, 80, 23);
		chckbxUrgente.setPreferredSize(new Dimension(80, 23));
		panel_asunto.add(chckbxUrgente);
		
		//JCheckBox chckbxSoloCoronel = new JCheckBox("Solo Jefe");
		//chckbxSoloCoronel.setBounds(430, 0, 92, 23);
		//panel_asunto.add(chckbxSoloCoronel);

		JPanel panel_1 = new JPanel();
		panel_1.setLocation(10, 126);
		panel_1.setPreferredSize(new Dimension(796, 90));
		panel_1.setSize(new Dimension(796, 81));
		panel_asunto.add(panel_1);
		panel_1.setLayout(null);

		JLabel lblAsunto = new JLabel("Asunto:");
		lblAsunto.setBounds(3, 8, 45, 14);
		panel_1.add(lblAsunto);

		asuntoText = new JTextField();
		asuntoText.setBounds(47, 5, 739, 20);
		
		
		
		panel_1.add(asuntoText);

		asuntoText.setComponentPopupMenu(menu);
		GridBagConstraints gbc_asuntoText = new GridBagConstraints();
		gbc_asuntoText.insets = new Insets(0, 0, 0, 5);
		gbc_asuntoText.fill = GridBagConstraints.BOTH;
		gbc_asuntoText.gridx = 1;
		gbc_asuntoText.gridy = 1;
		asuntoText.setColumns(65);
		
		JLabel lblDestinatarios = new JLabel("Areas:");
		lblDestinatarios.setBounds(3, 33, 45, 14);
		panel_1.add(lblDestinatarios);
		
		destinatariosField = new JTextArea();
		destinatariosField.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		destinatariosField.setBounds(47, 30, 739, 20);
		destinatariosField.setColumns(65);
		destinatariosField.setEditable(false);
		
		destinatariosField.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
		          if (arg0.getClickCount() == 2) {
		        	  //arrayList.remove(destinatariosField.getSelectedText());
		        	  
		        	  if (destinatariosField.getSelectedText().equals("Todos")) {
		        		  	comboArea.addItem("Todos");
		        			
		        			for (String str : Login.NEGOCIADOS) {
		        				comboArea.addItem(str);
		        			}
		        			destinatariosField.selectAll();
		        			destinatariosField.replaceSelection("");
		        			return;
		        	  }
		        	  
		        	  comboArea.addItem(destinatariosField.getSelectedText());
		        	  
		        	  if (!destinatariosField.getText().contains(";")) {
		        		  destinatariosField.selectAll();
		        		  destinatariosField.replaceSelection("");
		        		  comboArea.removeAllItems();
		        		  comboArea.addItem("Todos");
		        			
		        			for (String str : Login.NEGOCIADOS) {
		        				comboArea.addItem(str);
		        			}
		        			return;
		        	  }
		        	  
		        	  if (destinatariosField.getSelectionStart() == 0) {
		        		  destinatariosField.select(destinatariosField.getSelectionStart(), destinatariosField.getSelectionEnd()+1);
			        	  destinatariosField.replaceSelection("");
		        	  }else {
		        		  System.out.println("start " +destinatariosField.getSelectionStart());
		        		  System.out.println("end " +destinatariosField.getSelectionEnd());

		        		  destinatariosField.select(destinatariosField.getSelectionStart()-2, destinatariosField.getSelectionEnd());
			        	  destinatariosField.replaceSelection("");
		        	  }
		          }

			}
		});
		panel_1.add(destinatariosField);
		
		JLabel label_1 = new JLabel("Jefes:");
		label_1.setBounds(3, 61, 45, 14);
		panel_1.add(label_1);
		
		destinatariosJefes.setEditable(false);
		destinatariosJefes.setColumns(65);
		destinatariosJefes.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		destinatariosJefes.setBounds(47, 58, 739, 20);
		
		
		destinatariosJefes.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
		          if (arg0.getClickCount() == 2) {
		        	  //arrayList.remove(destinatariosField.getSelectedText());
		        	  
		        	  if (destinatariosJefes.getSelectedText().equals("Todos")) {
		        		  	comboBoxJefes.addItem("Todos");
		        			
		        			for (Entry<Integer, String> entry : Login.CARGOS.entrySet()) {
		        				comboBoxJefes.addItem(entry.getValue());
		        			}
		        			destinatariosJefes.selectAll();
		        			destinatariosJefes.replaceSelection("");
		        			return;
		        	  }
		        	  
		        	  
		        	  if (!destinatariosJefes.getText().contains(";")) {
		        		  destinatariosJefes.selectAll();
		        		  destinatariosJefes.replaceSelection("");
		        		  comboBoxJefes.removeAllItems();
		        		  comboBoxJefes.addItem("Todos");
		        			
		        			for (Entry<Integer, String> entry : Login.CARGOS.entrySet()) {
		        				comboBoxJefes.addItem(entry.getValue());
		        			}
		        			return;
		        	  }
		        	  
		        	  
		        	  
		        		  int selectionStart = -1;
		        		  int selectionEnd = -1;
		        		  int indexOfcoma = 9999;
		        		  
		        		  
		        		  if (destinatariosJefes.getSelectionStart() != 0) {
		        			  for (int i = destinatariosJefes.getSelectionStart(); i > 0; i--) {
		        				  System.out.println("posicion " + i + destinatariosJefes.getText().charAt(i));

			        			  if (new Character(destinatariosJefes.getText().charAt(i)).equals(';')) {
			        				  selectionStart = i;
			        				  break;
			        			  }
			        		  }  
		        		  }else {
		        			  selectionStart = 0;
		        		  }
		        		  
		        		  if (destinatariosJefes.getSelectionEnd() != destinatariosJefes.getText().length()) {
		        			  for (int i = destinatariosJefes.getSelectionEnd(); i < destinatariosJefes.getText().length(); i++) {
		        				  System.out.println("posicion " + i + destinatariosJefes.getText().charAt(i));
			        			  if (new Character(destinatariosJefes.getText().charAt(i)).equals(';')){
			        				  selectionEnd = i;
			        				  break;
			        			  }else if (i == destinatariosJefes.getText().length()-1) {
			        				  selectionEnd = i+1;
			        			  }
			        		  }
		        		  }else {
		        			  selectionEnd = destinatariosJefes.getText().length()+2;
		        		  }
		        		  
		        		  
		        		  System.out.println("Start " + selectionStart);
		        		  System.out.println("End " + selectionEnd);
		        		  if (selectionStart == 0 ) {
			        		  destinatariosJefes.select(selectionStart, selectionEnd+2);
		        		  }else {
		        			  destinatariosJefes.select(selectionStart, selectionEnd);  
		        		  }
		        		  String destinatariosJefesTemp = destinatariosJefes.getSelectedText();
		        		  
		        		  destinatariosJefesTemp.replace(";", "");
		        		
			        	  comboBoxJefes.addItem(destinatariosJefesTemp.replace(";", "").trim());

		        		  destinatariosJefes.replaceSelection("");

		        	  
		          }

			}
		});

		panel_1.add(destinatariosJefes);
		comboBoxCanalEntrada.setBounds(10, 85, 150, 41);
		panel_asunto.add(comboBoxCanalEntrada);
		comboBoxCanalEntrada.setBorder(new TitledBorder(null, "Canal de Entrada", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		comboBoxCanalEntrada.setPreferredSize(new Dimension(150, 30));
		panel_adjuntos.setLayout(null);

//		f.getContentPane().add(datePicker);

		JScrollPane dragEntrada = FileDragEntrada();
		dragEntrada.setBorder(new TitledBorder(null, "Entrada", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		dragEntrada.setLocation(10, 5);
		dragEntrada.setSize(796, 89);
		panel_adjuntos.add(dragEntrada);

		JLabel lblNewLabel = new JLabel("Archivos de entrada");
		dragEntrada.setColumnHeaderView(lblNewLabel);
		f.getContentPane().add(panel_adjuntos);

		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
//				if (destinatariosField.getText().equals("")) {
//					JOptionPane.showMessageDialog(null, "Debe añadir algún destinatario");
//					return;
//				}
	        
				/*
				 * if (destinatariosField.getText().equals("Otro")) {
				 * JOptionPane.showMessageDialog(null,
				 * "El destinatario no puede ser solo Otro, debe incluir un destinatario y Otro"
				 * ); return; }
				 * 
				 * if (destinatariosField.getText().split(";")[0].equals("Otro")) {
				 * JOptionPane.showMessageDialog(null,
				 * "La lista de destinatarios no puede empezar por Otro, debe empezar por un destinatario."
				 * ); return; }
				 */
				
				
				/*
				 * int opcionSeleccionada1 = -1;
				 * 
				 * db.connect(); if (!destinatariosField.getText().contains(";") &&
				 * db.isNotGestionado(destinatariosField.getText().trim())) { String[] options =
				 * { "Si, agregar de todos modos", "Cancelar" }; db.close();
				 * 
				 * opcionSeleccionada1 = JOptionPane.showOptionDialog(null, "El destinatario " +
				 * destinatariosField.getText().trim() +
				 * " es un Negociado no gestionado, por lo tanto no tiene acceso a esta Aplicación"
				 * , "Click en una opcion", JOptionPane.DEFAULT_OPTION,
				 * JOptionPane.INFORMATION_MESSAGE, null, options, options[0]); if
				 * (opcionSeleccionada1 == 1) { //
				 * comboArea.addItem(destinatariosField.getText().trim()); //
				 * destinatariosField.setText(""); return; } } db.close();
				 */
				

				if (tableEntrada.getRowCount() == 0) {
					JOptionPane.showMessageDialog(null, "Debe adjuntar un archivo en archivo adjunto de entrada");
					return;
				}
				
				if (destinatariosField.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Debe añadir al menos un destinatario.");
					return;
				}

				DateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy"); // 2016/11/16 12:08:43
				
				try {
					date = new SimpleDateFormat("dd-MMM-yyyy").parse(datePicker.getJFormattedTextField().getText());
				} catch (ParseException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				// crea el directorio
				
				// new File("H:\\plmcomd\\COMUN\\CORRESPONDENCIA ENTRADA\\DOCS\\" + dateFormat1.format(date)).mkdir();
				new File(Login.BASE_DIR+ "DOCS\\" + dateFormat1.format(date)).mkdir();

				// crea el directorio
				
				new File(Login.BASE_DIR+ "DOCS_ANTECEDENTES\\" + dateFormat1.format(date)).mkdir();
				//new File("H:\\plmcomd\\COMUN\\CORRESPONDENCIA ENTRADA\\DOCS_ANTECEDENTES\\" + dateFormat1.format(date)).mkdir();

				new File(Login.BASE_DIR+ "DOCS_SALIDA\\" + dateFormat1.format(date)).mkdir();
				
				JList<File> listEntrada = new JList<File>(defaultlist);

				
				// tambien se podría hacer un select con el asunto.
				
				// copiar el nombre del primer archivo adjuntado en el asunto   
				// ( comprobar que el archivo sea GWI y entonces si copuar el asuntok, en el caso que haya mas que un archivo adjunto=
				
				// hacer una peticion para ver el ultimo numero de seq en lugar de crear la entrada y devolver el id
				
				// luego comprobar que sean iguales para evitar errores de redundancia.
				
				System.out.println("Confidencial está activo " + chckbxConfidencial.isSelected());
				
				entrada = new entrada(asuntoText.getText(), dateFormat.format(date),
						destinatariosField.getText(), chckbxConfidencial.isSelected(), chckbxUrgente.isSelected());
				System.out.println("Set observaciones " + textObservaciones.getText());
				entrada.setObservaciones(textObservaciones.getText());
				//entrada.setSoloCoronel(chckbxSoloCoronel.isSelected());
				entrada.setCanalEntrada(comboBoxCanalEntrada.getSelectedItem().toString());
				entrada.setNumEntrada(textFieldnumEntrada.getText());
				int idEntrada = -1;

//				for (int i = 0; i < defaultlist.size(); i++) {
//
//					System.out.println("entrada: " + defaultlist.get(i).getName());
//					String fileName = defaultlist.get(i).getName();
//					String newFileString = Login.BASE_DIR + "DOCS\\" + dateFormat1.format(date) + "\\"
//							+ fileName;
//					//String newFileString = "H:\\plmcomd\\COMUN\\CORRESPONDENCIA ENTRADA\\DOCS\\" + dateFormat1.format(date) + "\\"
//					//		+ fileName;
//					
//					File newFile = new File(newFileString);
//					System.out.println(newFile.exists());
//					int opcionSeleccionada = -1;
//					if (newFile.exists()) {
////						JOptionPane.showOptionDialog(null, "El archivo ya está guardado, posiblemente esa entrada ya está dada de alta",
////				                "Archivo existe",
////				                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
//						String[] options = { "Crear entrada nueva", "Cancelar", "Sobrescribir entrada anterior" };
//						
//						opcionSeleccionada = JOptionPane.showOptionDialog(null,
//								"El archivo ya está guardado, posiblemente esa entrada ya está dada de alta",
//								"Click en una opcion", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
//								null, options, options[0]);
//
//					} else {
//						try {
////							if (chckbxSoloCoronel.isSelected()) {
////								try {
////									CryptoUtils.encrypt("PdSgVkYp3s6v9y$B&E)H@MbQeThWmZq4", defaultlist.get(i), newFile);
////								} catch (CryptoException e1) {
////									// TODO Auto-generated catch block
////									e1.printStackTrace();
////								}
////							}else {
//								copyFile(defaultlist.get(i), newFile);
////							}
//							
//						
//						} catch (IOException e1) {
//							// TODO Auto-generated catch block
//							JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
//						}
//						System.out.println("Antes de db.connect");
//						if (idEntrada == -1) {
//							db.connect();
//							idEntrada = db.saveEntrada(entrada);
//							db.close();
//							entrada.setId(idEntrada);
//						}
//						
//						if (idEntrada == -1) {
//							JOptionPane.showMessageDialog(null, "Error al guardar el archivo");
//							return;
//						}
//						db.connect();
//						db.saveFile(newFile.getAbsolutePath(), idEntrada);
//						db.close();
//					}
//					
//					if (opcionSeleccionada == 0) {
//						System.out.println(stripExtension(newFileString) +"_COPIA."  + getExtension(newFileString));
//						try {
//							newFile = new File(stripExtension(newFileString) +"_COPIA."  + getExtension(newFileString));
////							if (chckbxSoloCoronel.isSelected()) {
////								File fileEncrypted = null;
////								try {
////									CryptoUtils.encrypt("PdSgVkYp3s6v9y$B&E)H@MbQeThWmZq4", defaultlist.get(i), newFile);
////									//copyFileOverwrite(defaultlist.get(i), newFile);
////								} catch (CryptoException e1) {
////									// TODO Auto-generated catch block
////									e1.printStackTrace();
////								}
////							}else {
//								copyFileOverwrite(defaultlist.get(i), newFile);
////							}
//							
//						} catch (IOException e1) {
//							// TODO Auto-generated catch block
//							JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
//						}
//						if (idEntrada == -1) {
//							db.connect();
//							idEntrada = db.saveEntrada(entrada);
//							db.close();
//						}
//						
//						if (idEntrada == -1) {
//							JOptionPane.showMessageDialog(null, "Error al guardar el archivo");
//							return;
//						}
//						
//						db.connect();
//						db.saveFile(newFile.getAbsolutePath(), idEntrada);
//						db.close();
//						entrada.setId(idEntrada);
////						if (!defaultlist.get(i).renameTo(newFile)) {
////						}
//					} else if (opcionSeleccionada == 1) {
//						System.out.println("DENTRO OPCIOEN 1");
//						
//						asuntoText.setText("");
//
//						for (int j = 0; j < listDrag.getModel().getSize();i++) {
//							System.out.println("DENTRO FOR MODEL");
//							int index = listDrag.locationToIndex(listDrag.indexToLocation(j));
//				            Object item = defaultlist.getElementAt(index);
//				            defaultlist.remove(index);
//				            ListSelectionModel selmodel = listDrag.getSelectionModel();
//				            selmodel.setLeadSelectionIndex(index);
//						}
//						return;
////						try {
////							copyFile(defaultlist.get(i), newFile);
////						} catch (IOException e1) {
////							// TODO Auto-generated catch block
////							JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
////						}
////						if (!defaultlist.get(i).renameTo(newFile)) {
////						}
//					} else if (opcionSeleccionada == 2) {
//						// buscar entrada por nombre de file 
//						// actualizar entrada 
//						// sobre escribir archivo
//						System.out.println("opcion 2");
//						db.connect();
//						entrada.setId(db.buscarIdEntradaPorNombreArchivo(newFile.getAbsolutePath()));
//						
//						
//						
//						idEntrada = entrada.getId();
//						db.close();
//						db.connect();
//						db.actualizarAsuntoEntrada(entrada);
//						db.close();
//						if (idEntrada == -1) {
//							JOptionPane.showMessageDialog(null, "Error al guardar el archivo");
//							return;
//						}
//						
//						try {
//							
////							if (chckbxSoloCoronel.isSelected()) {
////								File fileEncrypted = null;
////								try {
////									CryptoUtils.encrypt("PdSgVkYp3s6v9y$B&E)H@MbQeThWmZq4", defaultlist.get(i), newFile);
////									//copyFileOverwrite(defaultlist.get(i), newFile);
////								} catch (CryptoException e1) {
////									// TODO Auto-generated catch block
////									e1.printStackTrace();
////								}
////							}else {
////								copyFileOverwrite(defaultlist.get(i), newFile);
////							}
//							copyFileOverwrite(defaultlist.get(i), newFile);
//
//						} catch (IOException e1) {
//							// TODO Auto-generated catch block
//							JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
//						}
//						
//					}
//
//				}
//				
				

//				for (int i = 0; i < defaultlistAntecedentes.size(); i++) {
//
//					System.out.println("antecedentes: " + defaultlistAntecedentes.get(i).getName());
//					String fileName = defaultlistAntecedentes.get(i).getName();
//					String newFileString = Login.BASE_DIR + "DOCS_ANTECEDENTES\\" + dateFormat1.format(date) + "\\"
//							+ fileName;
//					//String newFileString = "H:\\plmcomd\\COMUN\\CORRESPONDENCIA ENTRADA\\DOCS_ANTECEDENTES\\" + dateFormat1.format(date) + "\\"
//					//		+ fileName;
//					File newFile = new File(newFileString);
//					System.out.println(newFile.exists());
//					int opcionSeleccionada = -1;
//					if (newFile.exists()) {
////						JOptionPane.showOptionDialog(null, "El archivo ya está guardado, posiblemente esa entrada ya está dada de alta",
////				                "Archivo existe",
////				                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
//						String[] options = { "Sobrescribir archivo", "No sobreescribir" };
//						opcionSeleccionada = JOptionPane.showOptionDialog(null,
//								"El archivo ya está guardado, posiblemente ese antecedente ya está dada de alta",
//								"Click en una opcion", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
//								null, options, options[0]);
//
//					} else {
//						
//						try {
////							if (chckbxSoloCoronel.isSelected()) {
////								try {
////									CryptoUtils.encrypt("PdSgVkYp3s6v9y$B&E)H@MbQeThWmZq4", defaultlistAntecedentes.get(i), newFile);
////								} catch (CryptoException e1) {
////									// TODO Auto-generated catch block
////									e1.printStackTrace();
////								}
////							}else {
////								copyFile(defaultlistAntecedentes.get(i), newFile);
////							}
////							
//							copyFile(defaultlistAntecedentes.get(i), newFile);
//
//						
//						} catch (IOException e1) {
//							// TODO Auto-generated catch block
//							JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
//						}
//						
////						try {
////							copyFile(defaultlistAntecedentes.get(i), newFile);
////						} catch (IOException e1) {
////							// TODO Auto-generated catch block
////							JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
////						}
//						db.connect();
//						db.saveFileAntecedentes(newFile.getAbsolutePath(), idEntrada);
//						db.close();
//					}
//					
//					if (opcionSeleccionada == 0) {
//						
//						try {
//							newFile = new File(stripExtension(newFileString) +"_COPIA."  + getExtension(newFileString));
////							if (chckbxSoloCoronel.isSelected()) {
////								File fileEncrypted = null;
////								try {
////									CryptoUtils.encrypt("PdSgVkYp3s6v9y$B&E)H@MbQeThWmZq4", defaultlistAntecedentes.get(i), newFile);
////									//copyFileOverwrite(defaultlist.get(i), newFile);
////								} catch (CryptoException e1) {
////									// TODO Auto-generated catch block
////									e1.printStackTrace();
////								}
////							}else {
////							}
//							copyFileOverwrite(defaultlistAntecedentes.get(i), newFile);
//
//							
//						} catch (IOException e1) {
//							// TODO Auto-generated catch block
//							JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
//						}
//						
//						
////						try {
////							copyFileOverwrite(defaultlistAntecedentes.get(i), newFile);
////						} catch (IOException e1) {
////							// TODO Auto-generated catch block
////							JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
////						}
//						
//						db.connect();
//						db.saveFileAntecedentes(newFile.getAbsolutePath(), idEntrada);
//						db.close();
////						if (!defaultlist.get(i).renameTo(newFile)) {
////						}
//					} else if (opcionSeleccionada == 1) {
//						
//						for (int j = 0; j < listDragAntecedentes.getModel().getSize();i++) {
//							int index = listDragAntecedentes.locationToIndex(listDragAntecedentes.indexToLocation(j));
//				            Object item = defaultlistAntecedentes.getElementAt(index);
//				            defaultlistAntecedentes.remove(index);
//				            ListSelectionModel selmodel = listDragAntecedentes.getSelectionModel();
//				            selmodel.setLeadSelectionIndex(index);
//						}
//												
////						listDrag.repaint();
//						
////						defaultlist = new DefaultListModel<File>();
////						panel_adjuntos.remove(drag);
////						JScrollPane drag = FileDragDemo();
////						drag.setLocation(10, 5);
////						drag.setSize(647, 61);
////						panel_adjuntos.add(drag);
////						repaint();
////						revalidate();
//						return;
////						try {
////							copyFile(defaultlistAntecedentes.get(i), newFile);
////						} catch (IOException e1) {
////							// TODO Auto-generated catch block
////							JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
////						}
////						if (!defaultlist.get(i).renameTo(newFile)) {
////						}
//						
//					} 
//					
//
//				}
				
				if (idEntrada == -1) {
					db.connect();
					idEntrada = db.saveEntrada(entrada);
					db.close();
					entrada.setId(idEntrada);
				}
				
				for (int i = 0; i < tableEntrada.getRowCount(); i++) {
					// agregar file a la list.
					Date date1 = new Date();
					
					try {
						date1 = new SimpleDateFormat("dd/MM/yyyy").parse(entrada.getFecha());
					} catch (ParseException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

					File tempFile = new File(tableEntrada.getValueAt(i, 1).toString());

					new File(Login.BASE_DIR + "DOCS\\" + dateFormat1.format(date1)).mkdir();

					String newFileString = Login.BASE_DIR + "DOCS\\" + dateFormat1.format(date1) + "\\"
							+ tempFile.getName();

					File newFile = new File(newFileString);
					int opcionSeleccionada = -1;
					if (newFile.exists()) {
//						String[] options2 = { "Crear archivo nuevo", "Cancelar", "Sobrescribir archivo anterior" };
		//
//						opcionSeleccionada = JOptionPane.showOptionDialog(null,
//								"El archivo ya está guardado, posiblemente esa entrada ya está dada de alta",
//								"Click en una opcion", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
//								null, options2, options2[0]);

						if (tableEntrada.getValueAt(i, 0).equals("")) {
							db.connect();

		//saveFileEntrada(String absolutePath, int idEntrada, String fecha, String asunto, String origen, String observaciones) {

							db.saveFileEntrada(newFile.getAbsolutePath(), entrada.getId(), tableEntrada.getValueAt(i, 2).toString(),
									tableEntrada.getValueAt(i, 4).toString(), tableEntrada.getValueAt(i, 3).toString(), tableEntrada.getValueAt(i, 5).toString());
							db.close();
						} else {
							db.connect();
		// updateFileEntrada(String idEntradaFile, String file, int idEntrada, String fecha, String tipo, String destino, String asunto, String observaciones) {

							db.updateFileEntrada(tableEntrada.getValueAt(i, 0).toString(), newFile.getAbsolutePath(), entrada.getId(),
									tableEntrada.getValueAt(i, 2).toString(), tableEntrada.getValueAt(i, 3).toString(), tableEntrada.getValueAt(i, 4).toString(), tableEntrada.getValueAt(i, 5).toString());
							db.close();
						}

					} else {
						try {
							copyFile(tempFile, newFile);
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
						}

						db.connect();
						db.saveFileEntrada(newFile.getAbsolutePath(), entrada.getId(), tableEntrada.getValueAt(i, 2).toString(),
								tableEntrada.getValueAt(i, 4).toString(), tableEntrada.getValueAt(i, 3).toString(), tableEntrada.getValueAt(i, 5).toString());
						db.close();
					}

					if (opcionSeleccionada == 0) {
						try {
							newFile = new File(stripExtension(newFileString) + "_" + new Date().getTime() + "_COPIA."
									+ getExtension(newFileString));
							copyFileOverwrite(tempFile, newFile);

						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
						}

						db.connect();
						db.saveFileEntrada(newFile.getAbsolutePath(), entrada.getId(), tableEntrada.getValueAt(i, 2).toString(),
								tableEntrada.getValueAt(i, 4).toString(), tableEntrada.getValueAt(i, 3).toString(), tableEntrada.getValueAt(i, 5).toString());
						db.close();

					} else if (opcionSeleccionada == 1) {
						DefaultTableModel prueba = (DefaultTableModel) tableEntrada.getModel();
						prueba.removeRow(i);
						return;

					} else if (opcionSeleccionada == 2) {

						try {
							copyFileOverwrite(tempFile, newFile);

						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
						}
					}

				}
				
				for (int i = 0; i < tableAntecedentes.getRowCount(); i++) {
					// agregar file a la list.
					Date date1 = new Date();
					
					try {
						date1 = new SimpleDateFormat("dd/MM/yyyy").parse(entrada.getFecha());
					} catch (ParseException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

					File tempFile = new File(tableAntecedentes.getValueAt(i, 1).toString());

					new File(Login.BASE_DIR + "DOCS_ANTECEDENTES\\" + dateFormat1.format(date1)).mkdir();

					String newFileString = Login.BASE_DIR + "DOCS_ANTECEDENTES\\" + dateFormat1.format(date1) + "\\"
							+ tempFile.getName();

					File newFile = new File(newFileString);
					int opcionSeleccionada = -1;
					if (newFile.exists()) {
//						String[] options2 = { "Crear archivo nuevo", "Cancelar", "Sobrescribir archivo anterior" };
		//
//						opcionSeleccionada = JOptionPane.showOptionDialog(null,
//								"El archivo ya está guardado, posiblemente esa entrada ya está dada de alta",
//								"Click en una opcion", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
//								null, options2, options2[0]);

						if (tableAntecedentes.getValueAt(i, 0).equals("")) {
							db.connect();
		// saveFileAntecedentes(String absolutePath, int idEntrada, String fecha, String tipo, String destino, String asunto, String observaciones) {

							db.saveFileAntecedentes(newFile.getAbsolutePath(), entrada.getId(), tableAntecedentes.getValueAt(i, 2).toString(),
									tableAntecedentes.getValueAt(i, 3).toString(), tableAntecedentes.getValueAt(i, 4).toString(), tableAntecedentes.getValueAt(i, 5).toString(), tableAntecedentes.getValueAt(i, 6).toString());
							db.close();
						} else {
							db.connect();
		// updateFileAntecedentesa(String idSalida, String file, int idEntrada, String fecha, String tipo, String destino, String asunto, String observaciones) {

							db.updateFileAntecedentes(tableAntecedentes.getValueAt(i, 0).toString(), newFile.getAbsolutePath(), entrada.getId(),
									tableAntecedentes.getValueAt(i, 2).toString(), tableAntecedentes.getValueAt(i, 3).toString(), tableAntecedentes.getValueAt(i, 4).toString(), tableAntecedentes.getValueAt(i, 5).toString(), tableAntecedentes.getValueAt(i, 6).toString());
							db.close();
						}

					} else {
						try {
							copyFile(tempFile, newFile);
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
						}

						db.connect();
						db.saveFileAntecedentes(newFile.getAbsolutePath(), entrada.getId(), tableAntecedentes.getValueAt(i, 2).toString(),
								tableAntecedentes.getValueAt(i, 3).toString(), tableAntecedentes.getValueAt(i, 4).toString(), tableAntecedentes.getValueAt(i, 5).toString(), tableAntecedentes.getValueAt(i, 6).toString());
						db.close();
					}

					if (opcionSeleccionada == 0) {
						try {
							newFile = new File(stripExtension(newFileString) + "_" + new Date().getTime() + "_COPIA."
									+ getExtension(newFileString));
							copyFileOverwrite(tempFile, newFile);

						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
						}

						db.connect();
						db.saveFileAntecedentes(newFile.getAbsolutePath(), entrada.getId(), tableAntecedentes.getValueAt(i, 2).toString(),
								tableAntecedentes.getValueAt(i, 3).toString(), tableAntecedentes.getValueAt(i, 4).toString(), tableAntecedentes.getValueAt(i, 5).toString(), tableAntecedentes.getValueAt(i, 6).toString());
						db.close();

					} else if (opcionSeleccionada == 1) {
						DefaultTableModel prueba = (DefaultTableModel) tableAntecedentes.getModel();
						prueba.removeRow(i);
						return;

					} else if (opcionSeleccionada == 2) {

						try {
							copyFileOverwrite(tempFile, newFile);

						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
						}
					}

				}
				
				 for (int i = 0; i < tableSalida.getRowCount(); i++) {
						// agregar file a la list.
						Date date1 = new Date();
						try {
							date1 = new SimpleDateFormat("dd/MM/yyyy").parse(entrada.getFecha());
						} catch (ParseException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						File tempFile = (File) tableSalida.getValueAt(i, 1);
						new File(Login.BASE_DIR + "DOCS_SALIDA\\" + dateFormat1.format(date1)).mkdir();
						String newFileString = Login.BASE_DIR + "DOCS_SALIDA\\" + dateFormat1.format(date1) + "\\"
								+ tempFile.getName();
						
						File newFile = new File(newFileString);
						int opcionSeleccionada = -1;
						if (newFile.exists()) {
							String[] options = { "Crear archivo nuevo", "Cancelar", "Sobrescribir archivo anterior" };

							opcionSeleccionada = JOptionPane.showOptionDialog(null,
									"El archivo ya está guardado, posiblemente esa entrada ya está dada de alta",
									"Click en una opcion", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
									null, options, options[0]);

						} else {
							try {
								copyFile(tempFile, newFile);
							} catch (IOException e1) {
								JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
							}
							


							db.connect();
							db.saveFileSalida(newFile.getAbsolutePath(), entrada.getId(), tableSalida.getValueAt(i, 2).toString(), tableSalida.getValueAt(i, 3).toString(), tableSalida.getValueAt(i, 4).toString());
							db.close();
						}

						if (opcionSeleccionada == 0) {
							try {
								newFile = new File(stripExtension(newFileString) + "_" + new Date().getTime() + "_COPIA."
										+ getExtension(newFileString));
								copyFileOverwrite(tempFile, newFile);


							} catch (IOException e1) {
								JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
							}

							db.connect();
							db.saveFileSalida(newFile.getAbsolutePath(), entrada.getId(), tableSalida.getValueAt(i, 2).toString(), tableSalida.getValueAt(i, 3).toString(), tableSalida.getValueAt(i, 4).toString());
							db.close();
							
						} else if (opcionSeleccionada == 1) {
							DefaultTableModel prueba = (DefaultTableModel) tableSalida.getModel();
							prueba.removeRow(i);
							return;
							
						} else if (opcionSeleccionada == 2) {

							try {
								copyFileOverwrite(tempFile, newFile);

							} catch (IOException e1) {
								JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
							}
						}
						
					 
				}
				
				
				ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(destinatariosField.getText().split(";")));
	        	
	        	for (String destino : arrayList) {
        			
	        			db.connect();
			        	db.agregarDestinatario(entrada.getId(), destino.trim());
			        	db.close();
	        		

	        		
				}
	        	
	        	ArrayList<String> arrayListJefes = new ArrayList<String>(Arrays.asList(destinatariosJefes.getText().split(";")));
	        	
	        	for (String jefe : arrayListJefes) {
	        			System.out.println("destino jefe " + jefe );
	        			db.connect();
			        	db.agregarDestinatarioJefe(entrada.getId(), jefe.trim());
			        	db.close();
				}
	        	

//				String[] options = {"Continuar", "Cancelar"};

				
			

//				if (defaultlistAntecedentes != null) {
//					db.connect();
//					db.saveFileAntecedentes(newFile.getAbsolutePath(), idEntrada);
//					db.close();
//				}

//				if (ventana.entradaId.size() > 0) {
//					for (Integer item : ventana.entradaId) {
//						System.out.println("for entradaId");
//						System.out.println(item);
//						db.connect();
//						db.saveAntecedentesEntrada(item, idEntrada);
//						db.close();
//					}
//				}
				GUI.EntradaClosed = true;
				terminado = true;
				f.dispose();
				//refresh();
			}
		};

		JPanel panel_antecedentes = new JPanel();
		panel_antecedentes.setBounds(10, 350, 816, 116);
		f.getContentPane().add(panel_antecedentes);
		panel_antecedentes.setLayout(null);

//		JButton btnNewButton = new JButton("Seleccionar entrada anterior");
//		btnNewButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//
//				ventana = new nuevaVentana();
//
//			}
//		});
//		btnNewButton.setBounds(10, 27, 204, 23);

		JScrollPane dragAntecedentes = FileDragAntecedentes();
		dragAntecedentes.setBorder(new TitledBorder(null, "Antecedentes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		dragAntecedentes.setBounds(10, 11, 796, 94);
		panel_antecedentes.add(dragAntecedentes);

		JLabel label = new JLabel("Antecedentes");
		dragAntecedentes.setColumnHeaderView(label);
		
		
		
		panel_salida = new JPanel();
		panel_salida.setBounds(10, 477, 816, 116);
		f.getContentPane().add(panel_salida);
		panel_salida.setLayout(null);
		
		
		dragSalida = FileDragSalida();
		dragSalida.setBorder(new TitledBorder(null, "Salida", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		dragSalida.setBounds(10, 11, 796, 94);
		panel_salida.add(dragSalida);

		JLabel labelSalida = new JLabel("Salida");
		dragSalida.setColumnHeaderView(labelSalida);

		
		
		panel_observaciones = new JPanel();
		panel_observaciones.setBounds(10, 604, 816, 96);
		textObservaciones = new JTextArea();
		textObservaciones.setLineWrap(true);
		textObservaciones.setBorder(new TitledBorder(null, "Observaciones", TitledBorder.LEADING, TitledBorder.TOP, null, null));
	    //txtQuery.setBounds(10, 10, 365, 45);        
	    JScrollPane scroll = new JScrollPane (textObservaciones);
	    scroll.setPreferredSize(new Dimension(796, 81));
		panel_observaciones.add(scroll);
		f.getContentPane().add(panel_observaciones);
		
//		textObservaciones = new JTextArea();
//		textObservaciones.setColumns(3);
//		textObservaciones.setRows(1);
//		textObservaciones.setMaximumSize(new Dimension(0, 0));
//		textObservaciones.setMinimumSize(new Dimension(0, 0));
//		textObservaciones.setPreferredSize(new Dimension(200, 100));
//		textObservaciones.setBorder(new TitledBorder(null, "Observaciones", TitledBorder.LEADING, TitledBorder.TOP, null, null));
//		textObservaciones.setToolTipText("Observaciones");
//		textObservaciones.setBounds(10, 11, 647, 66);
//		
//		JScrollPane scrollObservaciones = new JScrollPane(textObservaciones);
//		scrollObservaciones.setMaximumSize(new Dimension(0, 0));
//		scrollObservaciones.setMinimumSize(new Dimension(0, 0));
//		scrollObservaciones.setPreferredSize(new Dimension(150, 50));
//		scrollObservaciones.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
//		scrollObservaciones.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
//		scrollObservaciones.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//		scrollObservaciones.setLayout(null);
//		
//		JPanel panel_observaciones = new JPanel();
//		panel_observaciones.setPreferredSize(new Dimension(600, 100));
//		panel_observaciones.setBounds(17, 289, 643, 88);
//		panel_observaciones.add(scrollObservaciones);
//		
//		f.getContentPane().add(panel_observaciones);

		JPanel panelButton = new JPanel();
		panelButton.setBounds(10, 711, 816, 33);
		f.getContentPane().add(panelButton);
		FlowLayout fl_panelButton = new FlowLayout(FlowLayout.CENTER, 0, 0);
		fl_panelButton.setAlignOnBaseline(true);
		panelButton.setLayout(fl_panelButton);

		guardar = new JButton("Guardar");
		panelButton.add(guardar);
		guardar.addActionListener(actionListener);

		f.setVisible(true);

	}
	
private JScrollPane FileDragEntrada() {
		
		
		
		
		db.connect();
		scrollPaneEntrada = TableFromDatabaseEntrada(db.getEntradaFilesById(0));
		db.close();

		
		scrollPaneEntrada.setTransferHandler(new FileListTransferHandlerEntrada(dataEntrada));
		
		

		return scrollPaneEntrada;
	}

	private JScrollPane FileDragAntecedentes() {
		
		
		
		
		db.connect();
		scrollPaneAntecedentes = TableFromDatabaseAntecedentes(db.getAntecedentesById(0));
		db.close();

		
		scrollPaneAntecedentes.setTransferHandler(new FileListTransferHandlerAntecedentes(dataAntecedentes));
		
		

		return scrollPaneAntecedentes;
	}
	
private JScrollPane FileDragSalida() {
		
		
		
		
		db.connect();
		scrollSalida = TableFromDatabaseSalida(db.getSalidaByIdBIS(0));
		db.close();

		
		scrollSalida.setTransferHandler(new FileListTransferHandlerSalida(dataSalida));
		
		

		return scrollSalida;
	}

public JScrollPane TableFromDatabaseEntrada(ResultSet rs) {

	if (scrollPaneEntrada != null) {
		valueScroll = scrollPaneEntrada.getVerticalScrollBar().getValue();
	}

	Vector columnNames = new Vector();
	dataEntrada = new Vector();

	try {

		ResultSetMetaData md = rs.getMetaData();
		
		columnNames.addElement("Abrir");
		columnNames.addElement("File");
		columnNames.addElement("Fecha");
		columnNames.addElement("Origen");
		columnNames.addElement("Asunto");
		columnNames.addElement("Observaciones");
		columnNames.addElement("Eliminar");

		int columns = columnNames.size();
		int lastPosition = -1;
		Vector lastElement = null;

		while (rs.next()) {

			Vector row = new Vector();
			row.setSize(100);

			row.add(0, rs.getObject("id"));
			row.add(1, rs.getObject("file"));
			row.add(2, rs.getObject("fecha"));
			row.add(3, rs.getObject("origen"));
			row.add(4, rs.getObject("asunto"));
			row.add(5, rs.getObject("observaciones"));

			URL url = GUI.class.getResource("/delete.png");
			ImageIcon icon = new ImageIcon(url);
			row.add(6, icon); // eliminar

			url = GUI.class.getResource("/vacio.png");
			icon = new ImageIcon(url);

			dataEntrada.addElement(row);

		}

		rs.close();

	} catch (Exception e) {
		System.out.println(e);
	}

	tablemodelEntrada = new DefaultTableModel(dataEntrada, columnNames) {

		public Class getColumnClass(int column) {
			for (int row = 0; row < getRowCount(); row++) {
				Object o = getValueAt(row, column);

				if (o != null) {
					return o.getClass();
				}

			}

			return Object.class;
		}

		@Override // hace no editable la columna 0
		public boolean isCellEditable(int row, int col) {
			if (col == columnNames.size()) {
				return true;
			}

			if (col == 2) {
				return true;
			}

			if (col == 3) {
				return true;
			}

			if (col == 4) {
				return true;
			}
			if (col == 5) {
				return true;
			}
		
			return false;
		}

	};

	tableEntrada = new JTable(tablemodelEntrada);
	tableEntrada.setPreferredScrollableViewportSize(new Dimension(0, 0));
	tableEntrada.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	tableEntrada.getColumnModel().getColumn(1).setWidth(0);
	tableEntrada.getColumnModel().getColumn(1).setMaxWidth(0);
	tableEntrada.getColumnModel().getColumn(1).setMinWidth(0);
	tableEntrada.getColumnModel().getColumn(1).setPreferredWidth(0);

	tableEntrada.setFont(new Font("Tahoma", Font.TRUETYPE_FONT, 14));

	tableEntrada.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));

	tableEntrada.setRowHeight(25);

	// URGENTE

	tableEntrada.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int col) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
			return this;
		}
	});

	tableEntrada.setAutoCreateRowSorter(true);

	tableEntrada.addMouseListener(new MouseAdapter() {

		public void mouseClicked(MouseEvent e) {

			if (e.getClickCount() == 2) {
				JTable target = (JTable) e.getSource();
				int row = target.getSelectedRow();
				int column = target.getSelectedColumn();
				if (column == 6) {

					System.out.println(!destinatariosField.getText().contains("Todos"));
					if (!Login.usuarioActivo.getRole().equals("Registro")
							&& (!destinatariosField.getText().contains(Login.usuarioActivo.getRole())
									&& !destinatariosField.getText().contains("Todos"))) {
						JOptionPane.showMessageDialog(null, "No tiene permisos para eliminar entrada.");
						return;
					}
					String[] options = { "Si", "No" };

					int opcionSeleccionada = -1;

					opcionSeleccionada = JOptionPane.showOptionDialog(null,
							"¿Desea eliminar esta entrada y sus archivos adjuntos?", "", JOptionPane.DEFAULT_OPTION,
							JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

					if (opcionSeleccionada == 1) {
						return;
					}

					Object item = target.getValueAt(row, column);

					if (target.getValueAt(row, 0).equals("")) {
						DefaultTableModel prueba = (DefaultTableModel) target.getModel();
						prueba.removeRow(row);
					} else {
						db.connect();
						if (!db.deleteEntradaFile((int) target.getValueAt(row, 0))) {
							JOptionPane.showMessageDialog(null, "Error al eliminar los archivos.");
							db.close();
							return;
						} else {
							db.close();
							DefaultTableModel prueba = (DefaultTableModel) target.getModel();
							prueba.removeRow(row);
							return;
						}
					}
				}
				if (column == 0) {

					File myFile = new File(target.getValueAt(row, 1).toString());

					if (!Desktop.isDesktopSupported()) {
						System.out.println("Desktop is not supported");
						return;
					}
					Desktop desktop = Desktop.getDesktop();
					try {
						desktop.open(myFile);
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "Error al abrir el archivo " + myFile.getAbsolutePath());

						e2.printStackTrace();
					}
				}
			}
		}

	});
	if (scrollPaneEntrada != null) {
		scrollPaneEntrada.remove(tableEntrada);
		scrollPaneEntrada.add(tableEntrada);

	} else {
		scrollPaneEntrada = new JScrollPane(tableEntrada);
	}
	return scrollPaneEntrada;
}


	public JScrollPane TableFromDatabaseAntecedentes(ResultSet rs) {

	if (scrollPaneAntecedentes != null) {
		valueScroll = scrollPaneAntecedentes.getVerticalScrollBar().getValue();
	}

	Vector columnNames = new Vector();
	dataSalida = new Vector();

	try {

		ResultSetMetaData md = rs.getMetaData();
		
		columnNames.addElement("Abrir");
		columnNames.addElement("File");
		columnNames.addElement("Fecha");
		columnNames.addElement("Salida/Entrada");
		columnNames.addElement("Destino/Origen");
		columnNames.addElement("Asunto");
		columnNames.addElement("Observaciones");
		columnNames.addElement("Eliminar");

		int columns = columnNames.size();
		int lastPosition = -1;
		Vector lastElement = null;

		while (rs.next()) {

			Vector row = new Vector();
			row.setSize(100);

			row.add(0, rs.getObject("antecedentesFiles_id"));
			row.add(1, rs.getObject("file"));
			row.add(2, rs.getObject("fecha"));
			row.add(3, rs.getObject("tipo"));
			row.add(4, rs.getObject("destino"));
			row.add(5, rs.getObject("asunto"));
			row.add(6, rs.getObject("observaciones"));

			URL url = GUI.class.getResource("/delete.png");
			ImageIcon icon = new ImageIcon(url);
			row.add(7, icon); // eliminar

			url = GUI.class.getResource("/vacio.png");
			icon = new ImageIcon(url);

			dataSalida.addElement(row);

		}

		rs.close();

	} catch (Exception e) {
		System.out.println(e);
	}

	tablemodelAntecedentes = new DefaultTableModel(dataSalida, columnNames) {

		public Class getColumnClass(int column) {
			for (int row = 0; row < getRowCount(); row++) {
				Object o = getValueAt(row, column);

				if (o != null) {
					return o.getClass();
				}

			}

			return Object.class;
		}

		@Override // hace no editable la columna 0
		public boolean isCellEditable(int row, int col) {
			if (col == columnNames.size()) {
				return true;
			}

			if (col == 2) {
				return true;
			}

			if (col == 3) {
				return true;
			}

			if (col == 4) {
				return true;
			}
			if (col == 5) {
				return true;
			}
			if (col == 6) {
				return true;
			}
			return false;
		}

	};

	tableAntecedentes = new JTable(tablemodelAntecedentes);
	tableAntecedentes.setPreferredScrollableViewportSize(new Dimension(0, 0));
	tableAntecedentes.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	tableAntecedentes.getColumnModel().getColumn(1).setWidth(0);
	tableAntecedentes.getColumnModel().getColumn(1).setMaxWidth(0);
	tableAntecedentes.getColumnModel().getColumn(1).setMinWidth(0);
	tableAntecedentes.getColumnModel().getColumn(1).setPreferredWidth(0);

	tableAntecedentes.setFont(new Font("Tahoma", Font.TRUETYPE_FONT, 14));

	tableAntecedentes.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));

	tableAntecedentes.setRowHeight(25);

	// URGENTE

	tableAntecedentes.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int col) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
			return this;
		}
	});

	tableAntecedentes.setAutoCreateRowSorter(true);

	tableAntecedentes.addMouseListener(new MouseAdapter() {

		public void mouseClicked(MouseEvent e) {

			if (e.getClickCount() == 2) {
				JTable target = (JTable) e.getSource();
				int row = target.getSelectedRow();
				int column = target.getSelectedColumn();
				if (column == 7) {

					System.out.println(!destinatariosField.getText().contains("Todos"));
					if (!Login.usuarioActivo.getRole().equals("Registro")
							&& (!destinatariosField.getText().contains(Login.usuarioActivo.getRole())
									&& !destinatariosField.getText().contains("Todos"))) {
						JOptionPane.showMessageDialog(null, "No tiene permisos para eliminar entrada.");
						return;
					}
					String[] options = { "Si", "No" };

					int opcionSeleccionada = -1;

					opcionSeleccionada = JOptionPane.showOptionDialog(null,
							"¿Desea eliminar esta entrada y sus archivos adjuntos?", "", JOptionPane.DEFAULT_OPTION,
							JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

					if (opcionSeleccionada == 1) {
						return;
					}

					Object item = target.getValueAt(row, column);

					if (target.getValueAt(row, 0).equals("")) {
						DefaultTableModel prueba = (DefaultTableModel) target.getModel();
						prueba.removeRow(row);
					} else {
						db.connect();
						if (!db.deleteAntecedentesFile((int) target.getValueAt(row, 0))) {
							JOptionPane.showMessageDialog(null, "Error al eliminar los archivos.");
							db.close();
							return;
						} else {
							db.close();
							DefaultTableModel prueba = (DefaultTableModel) target.getModel();
							prueba.removeRow(row);
							return;

						}
					}
				}

				if (column == 0) {

					File myFile = new File(target.getValueAt(row, 1).toString());

					if (!Desktop.isDesktopSupported()) {
						System.out.println("Desktop is not supported");
						return;
					}
					Desktop desktop = Desktop.getDesktop();
					try {
						desktop.open(myFile);
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "Error al abrir el archivo " + myFile.getAbsolutePath());

						e2.printStackTrace();
					}

				}

				
				
			}
		}

	});
	if (scrollPaneAntecedentes != null) {
		scrollPaneAntecedentes.remove(tableAntecedentes);
		scrollPaneAntecedentes.add(tableAntecedentes);

	} else {
		scrollPaneAntecedentes = new JScrollPane(tableAntecedentes);
	}

	return scrollPaneAntecedentes;
}

	
	private static void copyFile(File source, File dest) throws IOException {
		try {
			CryptoUtils.encrypt("PdSgVkYp3s6v9y$B&E)H@MbQeThWmZq4", source, dest);
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Files.copy(source.toPath(), dest.toPath());
	}

	private static void copyFileOverwrite(File source, File dest) throws IOException {
		try {
			CryptoUtils.encrypt("PdSgVkYp3s6v9y$B&E)H@MbQeThWmZq4", source, dest);
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	private String stripExtension (String str) {
        // Handle null case specially.

        if (str == null) return null;

        // Get position of last '.'.

        int pos = str.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.

        if (pos == -1) return str;

        // Otherwise return the string, up to the dot.

        return str.substring(0, pos);
    }
	
	private String getExtension (String str) {
        // Handle null case specially.
		System.out.println(str);
		if (str == null) return null;

        // Get position of last '.'.

        int pos = str.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.
        System.out.println("posicion . " + pos);
        if (pos == -1) return str;
        System.out.println(str.substring(pos, str.length()));
        // Otherwise return the string, up to the dot.

        return str.substring(pos, str.length());
    }


	public JScrollPane FileDragDemoBack() {
		listDrag = new JList((ListModel) defaultlist);

		listDrag.setSize(1000, 1000);
		listDrag.setDragEnabled(true);
		listDrag.addKeyListener(new KeyListener() {
	        @Override
	        public void keyPressed(KeyEvent e) {
	            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
	            	int index = listDrag.getSelectedIndex();
			        Object item = defaultlist.getElementAt(index);
			        defaultlist.remove(index);
			        ListSelectionModel selmodel = listDrag.getSelectionModel();
			        selmodel.setLeadSelectionIndex(index);
	            }
	        }
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub	
			}
	    });
		listDrag.addMouseListener(new MouseAdapter() {
		      public void mouseClicked(MouseEvent e) {
		          if (e.getClickCount() == 2) {
							int index = listDrag.locationToIndex(e.getPoint());
							// do some stuff
							File myFile = new File(listDrag.getSelectedValue().toString());
							if (!Desktop.isDesktopSupported()) {
								JOptionPane.showMessageDialog(null, "No se puede abrir el archivo.");
								System.out.println("Desktop is not supported");
								return;
							}
							Desktop desktop = Desktop.getDesktop();
							if (myFile.exists())
								try {
									desktop.open(myFile);
								} catch (IOException e1) {
									JOptionPane.showMessageDialog(null, "No se encuentra el archivo " + myFile.getAbsolutePath());
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
		          }
		        }
		      });

		listDrag.setTransferHandler(new FileListTransferHandler(listDrag));
		return new JScrollPane(listDrag);
	}

	public JScrollPane FileDragAntecedentesBack() {

//		  list.setSize(1000, 1000);
		listDragAntecedentes = new JList((ListModel) defaultlistAntecedentes);

		listDragAntecedentes.setDragEnabled(true);
		listDragAntecedentes.addKeyListener(new KeyListener() {
	        @Override
	        public void keyPressed(KeyEvent e) {
	            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
	            	
	            	int index = listDragAntecedentes.getSelectedIndex();
			        Object item = defaultlistAntecedentes.getElementAt(index);
			        defaultlistAntecedentes.remove(index);
			        ListSelectionModel selmodel = listDragAntecedentes.getSelectionModel();
			        selmodel.setLeadSelectionIndex(index);
	            }
	        }
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub	
			}
	    });
		
		listDrag.addMouseListener(new MouseAdapter() {
		      public void mouseClicked(MouseEvent e) {
		          if (e.getClickCount() == 2) {
							int index = listDragAntecedentes.locationToIndex(e.getPoint());
							// do some stuff
							File myFile = new File(listDragAntecedentes.getSelectedValue().toString());
							if (!Desktop.isDesktopSupported()) {
								JOptionPane.showMessageDialog(null, "No se puede abrir el archivo.");
								System.out.println("Desktop is not supported");
								return;
							}
							Desktop desktop = Desktop.getDesktop();
							if (myFile.exists())
								try {
									desktop.open(myFile);
								} catch (IOException e1) {
									JOptionPane.showMessageDialog(null, "No se encuentra el archivo " + myFile.getAbsolutePath());
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
		          }
		        }
		      });

		listDragAntecedentes.setTransferHandler(new FileListTransferHandler2(listDragAntecedentes));

		return new JScrollPane(listDragAntecedentes);
	}

	@SuppressWarnings("serial")
	class FileListTransferHandler extends TransferHandler {
		private JList list;

		public FileListTransferHandler(JList list) {
			this.list = list;
		}

		public int getSourceActions(JComponent c) {
			return COPY_OR_MOVE;
		}

		public boolean canImport(TransferSupport ts) {
			return ts.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
		}

		public boolean importData(TransferSupport ts) {
			try {
				System.out.println("prueba dentro drag");
				@SuppressWarnings("rawtypes")

//	    	  List data = Arrays.asList(ts.getDataFlavors());

				List data = (List) ts.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

				System.out.println(data.size());
				if (data.size() < 1) {
					return false;
				}

//	         DefaultListModel listModel = new DefaultListModel();
				for (Object item : data) {
					System.out.println("for " + item.getClass());
					file = (File) item;
					
					// quitar extension 
					
					asuntoText.setText(stripExtension(file.getName()));

					
					
					
					
					
					defaultlist.addElement(file);
					System.out.println(defaultlist.size());
				}

				list.setModel(defaultlist);
				return true;

			} catch (UnsupportedFlavorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
	}

	@SuppressWarnings("serial")
	class FileListTransferHandler2 extends TransferHandler {
		private JList list;

		public FileListTransferHandler2(JList list) {
			this.list = list;
		}

		public int getSourceActions(JComponent c) {
			return COPY_OR_MOVE;
		}

		public boolean canImport(TransferSupport ts) {
			return ts.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
		}

		public boolean importData(TransferSupport ts) {
			try {
				System.out.println("prueba dentro drag");
				@SuppressWarnings("rawtypes")

//	    	  List data = Arrays.asList(ts.getDataFlavors());

				List data = (List) ts.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

				System.out.println(data.size());
				if (data.size() < 1) {
					return false;
				}

//	         DefaultListModel listModel = new DefaultListModel();

				for (Object item : data) {
					System.out.println("for " + item.getClass());
					file = (File) item;
					defaultlistAntecedentes.addElement(file);
//	            listModel.addElement(file);
					System.out.println(defaultlistAntecedentes.size());
				}

				list.setModel(defaultlistAntecedentes);
				return true;

			} catch (UnsupportedFlavorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
	}
	
	@SuppressWarnings("serial")
	class FileListTransferHandler3 extends TransferHandler {
		private JList list;

		public FileListTransferHandler3(JList list) {
			this.list = list;
		}

		public int getSourceActions(JComponent c) {
			return COPY_OR_MOVE;
		}

		public boolean canImport(TransferSupport ts) {
			return ts.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
		}

		public boolean importData(TransferSupport ts) {
			try {
				System.out.println("prueba dentro drag");
				@SuppressWarnings("rawtypes")

//	    	  List data = Arrays.asList(ts.getDataFlavors());

				List data = (List) ts.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

				System.out.println(data.size());
				if (data.size() < 1) {
					return false;
				}

//	         DefaultListModel listModel = new DefaultListModel();

				for (Object item : data) {
					System.out.println("for " + item.getClass());
					file = (File) item;
					defaultlistSalida.addElement(file);
//	            listModel.addElement(file);
					System.out.println(defaultlistSalida.size());
				}

				list.setModel(defaultlistSalida);
				return true;

			} catch (UnsupportedFlavorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
	}
	
	@SuppressWarnings("serial")
	class FileListTransferHandlerEntrada extends TransferHandler {
		private Vector list;
		private File file;

		public FileListTransferHandlerEntrada(Vector list) {
			this.list = list;
		}

		public int getSourceActions(JComponent c) {
			return COPY_OR_MOVE;
		}

		public boolean canImport(TransferSupport ts) {
			return ts.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
		}

		public boolean importData(TransferSupport ts) {
			try {
				@SuppressWarnings("rawtypes")

				List data = (List) ts.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

				if (data.size() < 1) {
					return false;
				}

				for (Object item : data) {
					file = (File) item;
					DefaultTableModel prueba = (DefaultTableModel) tableEntrada.getModel();
					FileTime creationTime = (FileTime) Files.getAttribute(file.toPath(), "lastModifiedTime");
					Date dateCreated = new Date(creationTime.toMillis());
					if (asuntoText.getText().isEmpty()){
						asuntoText.setText(stripExtension(file.getName()));
					}
					


					URL url = GUI.class.getResource("/delete.png");
					ImageIcon icon = new ImageIcon(url);

					int opcionSeleccionada = -1;
					boolean existFile = false;

					for (int i = 0; i < tableEntrada.getRowCount(); i++) {
						
						if (tableEntrada.getValueAt(i, 1).equals(file)) {
							existFile = true;
						}
					}

					if (existFile) {
						String[] options = { "Crear archivo nuevo", "Cancelar", "Sobrescribir archivo anterior" };

						opcionSeleccionada = JOptionPane.showOptionDialog(null,
								"El archivo ya está guardado, posiblemente ya se adjuntado", "Click en una opcion",
								JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
					} else {

						prueba.addRow(
								new Object[] { "", file, dateFormat_normal.format(dateCreated).toString() + " " + timeFormat.format(dateCreated).toString(),
										"", file.getName(), "", icon });
					}

					if (opcionSeleccionada == 0) {
						File newFile = new File(stripExtension(file.getName()) + "_" + new Date().getTime() + "_COPIA."
								+ getExtension(file.getName()));

						prueba.addRow(
								new Object[] { "", newFile, dateFormat_normal.format(dateCreated).toString() + " " + timeFormat.format(dateCreated).toString(),
										"", newFile.getName(), "", icon });

					} else if (opcionSeleccionada == 1) {

						continue;

					} else if (opcionSeleccionada == 2) {

						
						prueba.addRow(
								new Object[] { "", file, dateFormat_normal.format(dateCreated).toString() + " " + timeFormat.format(dateCreated).toString(),
										"", file.getName(), "", icon });

					}

				}

				return true;

			} catch (UnsupportedFlavorException e) {
				System.out.println(e);
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				System.out.println(e);
				e.printStackTrace();
				return false;
			}
		}
	}
	
	@SuppressWarnings("serial")
	class FileListTransferHandlerAntecedentes extends TransferHandler {
		private Vector list;
		private File file;

		public FileListTransferHandlerAntecedentes(Vector list) {
			this.list = list;
		}

		public int getSourceActions(JComponent c) {
			return COPY_OR_MOVE;
		}

		public boolean canImport(TransferSupport ts) {
			return ts.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
		}

		public boolean importData(TransferSupport ts) {
			try {
				@SuppressWarnings("rawtypes")

				List data = (List) ts.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

				if (data.size() < 1) {
					return false;
				}

				for (Object item : data) {
					file = (File) item;
					DefaultTableModel prueba = (DefaultTableModel) tableAntecedentes.getModel();
					FileTime creationTime = (FileTime) Files.getAttribute(file.toPath(), "lastModifiedTime");
					Date dateCreated = new Date(creationTime.toMillis());

					URL url = GUI.class.getResource("/delete.png");
					ImageIcon icon = new ImageIcon(url);

					int opcionSeleccionada = -1;
					boolean existFile = false;

					for (int i = 0; i < tableAntecedentes.getRowCount(); i++) {
						System.out.println("tableAntecedentes.getvalue ");
						System.out.println(tableAntecedentes.getValueAt(i, 1));
						System.out.println("file ");
						System.out.println(file);
						if (tableAntecedentes.getValueAt(i, 1).equals(file)) {
							existFile = true;
						}
					}

					if (existFile) {
						String[] options = { "Crear archivo nuevo", "Cancelar", "Sobrescribir archivo anterior" };

						opcionSeleccionada = JOptionPane.showOptionDialog(null,
								"El archivo ya está guardado, posiblemente ya se adjuntado", "Click en una opcion",
								JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
					} else {

						prueba.addRow(
								new Object[] { "", file, dateFormat_normal.format(dateCreated).toString() + " " + timeFormat.format(dateCreated).toString(),
										"", "", file.getName(), "", icon });
					}

					if (opcionSeleccionada == 0) {
						File newFile = new File(stripExtension(file.getName()) + "_" + new Date().getTime() + "_COPIA."
								+ getExtension(file.getName()));

						prueba.addRow(new Object[] { "", newFile, dateFormat_normal.format(dateCreated).toString() + " " + timeFormat.format(dateCreated).toString(),
								"", "", newFile.getName(), "", icon });

					} else if (opcionSeleccionada == 1) {

						continue;

					} else if (opcionSeleccionada == 2) {

						
						prueba.addRow(new Object[] { "", file, dateFormat_normal.format(dateCreated).toString() + " " + timeFormat.format(dateCreated).toString(),
										"", "", file.getName(), "", icon });

					}

				}

				return true;

			} catch (UnsupportedFlavorException e) {
				System.out.println(e);
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				System.out.println(e);
				e.printStackTrace();
				return false;
			}
		}
	}
	
	@SuppressWarnings("serial")
	class FileListTransferHandlerSalida extends TransferHandler {
		private Vector list;

		public FileListTransferHandlerSalida(Vector list) {
			this.list = list;
		}

		public int getSourceActions(JComponent c) {
			return COPY_OR_MOVE;
		}

		public boolean canImport(TransferSupport ts) {
			return ts.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
		}

		public boolean importData(TransferSupport ts) {
			try {
				System.out.println("prueba dentro drag");
				@SuppressWarnings("rawtypes")

//	    	  List data = Arrays.asList(ts.getDataFlavors());

				List data = (List) ts.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

				System.out.println(data.size());
				if (data.size() < 1) {
					return false;
				}

//	         DefaultListModel listModel = new DefaultListModel();

				for (Object item : data) {
					System.out.println("for 1" + item.getClass());
					file = (File) item;
					System.out.println("file name " + file.getName());
//					Vector row = new Vector();
					
//					columnNames.addElement("ID");
//					columnNames.addElement("FECHA");
//					columnNames.addElement("DESTINO");
//					columnNames.addElement("ASUNTO");
//					columnNames.addElement("VISTO BUENO");
//					columnNames.addElement("ELIMINAR");
					
					FileTime creationTime = (FileTime) Files.getAttribute(file.toPath(), "lastModifiedTime");
					Date dateCreated = new Date(creationTime.toMillis());
					
					DefaultTableModel prueba = (DefaultTableModel) tableSalida.getModel();
					Vector row = new Vector();
					row.setSize(100);
					row.add(0, "");					
					row.add(1,file);
					row.add(2,dateFormat_normal.format(dateCreated).toString() + " "+timeFormat.format(dateCreated).toString());
					row.add(3, "");
					row.add(4, file.getName());
					URL url = GUI.class.getResource("/delete.png");
					ImageIcon icon = new ImageIcon(url);
					row.add(5,icon); // eliminar
					//row.add(prueba.getRowCount()-1,file);
					
					prueba.addRow(row);
					
					
//					System.out.println("antes remove");
//					
//					panel_salida.remove(dragSalida);
//					panel_salida.removeAll();
//					
//					panel_salida.add(table);
//					panel_salida.repaint();
					
					
//					if (scrollSalida != null) {
//						scrollSalida.add(table);
//					} else {
//						scrollSalida = new JScrollPane(table);
//					}
					
					//dataSalida.add(row);
					
//	            listModel.addElement(file);
					System.out.println("Despues remove ");
					System.out.println(defaultlistSalida.size());
				}

//				list.setModel(defaultlistSalida);
				return true;

			} catch (UnsupportedFlavorException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
				e.printStackTrace();
				return false;
			}
		}
	}
	
	public JScrollPane TableFromDatabaseSalida(ResultSet rs) {
		// JPanel result = new JPanel();
		// result.setSize(new Dimension(1200, 800));
		
		if (scrollPane != null) {
			valueScroll = scrollPane.getVerticalScrollBar().getValue();
		}

		Vector columnNames = new Vector();
		dataSalida = new Vector();

		try {
			// Connect to an Access Database

			// Read data from a table

			ResultSetMetaData md = rs.getMetaData();
			
			for (int i = 1; i < md.getColumnCount(); i++) {
				System.out.println("Columna: " + md.getColumnName(i));
			}


			
			columnNames.addElement("Abrir");
			columnNames.addElement("File");
			columnNames.addElement("Fecha");
			columnNames.addElement("Destino");
			columnNames.addElement("Asunto");
			columnNames.addElement("Eliminar");
			
			for (Map.Entry<Integer, String> entry : Login.CARGOS.entrySet()) {
				columnNames.addElement(entry.getValue());
			}

			
			int columns = columnNames.size();
			int entrada_id = -1;
			int lastPosition = -1;
			Vector lastElement = null;

			// Get row data
			while (rs.next()) {

				Vector row = new Vector();
				row.setSize(100);

				// posicion usuario

				int posicion = -1;
				

				if (entrada_id != (int) rs.getObject("id") || entrada_id == -1) {
					
					if (!dataSalida.isEmpty()) {
						lastPosition = dataSalida.lastIndexOf(dataSalida.lastElement());
						lastElement = (Vector) dataSalida.lastElement();
					}
					
					
					entrada_id = (int) rs.getObject("id");
					row.add(0,rs.getObject("id"));
					row.add(2,rs.getObject("fecha"));
					row.add(3,rs.getObject("destino"));
					row.add(4,rs.getObject("asunto"));
					URL url = GUI.class.getResource("/delete.png");
					ImageIcon icon = new ImageIcon(url);
					row.add(5,icon); // eliminar
					
					
					
					url = GUI.class.getResource("/vacio.png");
					icon = new ImageIcon(url);
					
					
					
					
					
					int iterator = 0;
					for (Map.Entry<Integer, String> entry : Login.CARGOS.entrySet()) {
						row.add(6 + iterator, icon);
						iterator++;
					}
					
					

					
					//row.add(12,icon);
					
					
					//System.out.println("rs.getObject(id)" + rs.getObject("id"));
					//System.out.println("rs.getObject(visto)" + rs.getObject("visto"));
					try {
						db.connect();
						posicion = db.getPosicion((int) rs.getObject("usuario_id"));
						db.close();
						if ((int) rs.getObject("vistoBueno") == 0) {
							url = GUI.class.getResource("/vacio.png");
							icon = new ImageIcon(url);
							row.set(posicion+5,icon);

						}
						if ((int) rs.getObject("vistoBueno") == 1) {
							url = GUI.class.getResource("/single.png");
							icon = new ImageIcon(url);
							row.set(posicion+5,icon);
						}
						if ((int) rs.getObject("vistoBueno") == 2) {
							url = GUI.class.getResource("/doble.png");
							icon = new ImageIcon(url);
							row.set(posicion+5,icon);
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("DB ERROR posicion = Integer.parseInt(db.getPosicion((int) rs.getObject(\"usuario_id\")));\r\n" + 
								e);
					}
					
					
					
					
					

					dataSalida.addElement(row);
					
					

				}else {
					//System.out.println("elseeeeeeeeeeeeeee " + (int) rs.getObject("usuario_id"));
					db.connect();
					posicion = db.getPosicion((int) rs.getObject("usuario_id"));
					db.close();
					
					if (dataSalida.isEmpty()) {
						System.out.println("Data is empty");
						lastElement = (Vector) dataSalida.lastElement();
						if ((int) rs.getObject("vistoBueno") == 0) {
							URL url = GUI.class.getResource("/vacio.png");
							ImageIcon icon = new ImageIcon(url);
							lastElement.set(posicion+5,icon);

						}
						if ((int) rs.getObject("vistoBueno") == 1) {
							URL url = GUI.class.getResource("/single.png");
							ImageIcon icon = new ImageIcon(url);
							lastElement.set(posicion+5,icon);
						}
						if ((int) rs.getObject("vistoBueno") == 2) {
							URL url = GUI.class.getResource("/doble.png");
							ImageIcon icon = new ImageIcon(url);
							lastElement.set(posicion+5,icon);
						}
					}else {
						lastElement = (Vector) dataSalida.lastElement();

						if ((int) rs.getObject("vistoBueno") == 0) {
							URL url = GUI.class.getResource("/vacio.png");
							ImageIcon icon = new ImageIcon(url);
							lastElement.set(posicion+5,icon);

						}
						if ((int) rs.getObject("vistoBueno") == 1) {
							URL url = GUI.class.getResource("/single.png");
							ImageIcon icon = new ImageIcon(url);
							lastElement.set(posicion+5,icon);
						}
						if ((int) rs.getObject("vistoBueno") == 2) {
							URL url = GUI.class.getResource("/doble.png");
							ImageIcon icon = new ImageIcon(url);
							lastElement.set(posicion+5,icon);
						}
					}
				}
				
				
			}

			rs.close();

		} catch (Exception e) {
			System.out.println(e);
		}

		// Create table with database data
		tablemodel = new DefaultTableModel(dataSalida, columnNames) {

			public Class getColumnClass(int column) {
				for (int row = 0; row < getRowCount(); row++) {
					Object o = getValueAt(row, column);

					if (o != null) {
						return o.getClass();
					}

				}

				return Object.class;
			}

			@Override // hace no editable la columna 0
			public boolean isCellEditable(int row, int col) {
				if (col == columnNames.size()) {
					return true;
				}
				
				if (col == 3) {
					return true;
				}
				
				if (col == 2) {
					return true;
				}
				
				if (col == 4) {
					return true;
				}
				return false;
			}

		};

		tableSalida = new JTable(tablemodel);
		tableSalida.setPreferredScrollableViewportSize(new Dimension(0, 0));
		tableSalida.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//		table.getColumnModel().getColumn(0).setPreferredWidth(40);
		tableSalida.getColumnModel().getColumn(1).setWidth(0);
		tableSalida.getColumnModel().getColumn(1).setMaxWidth(0);
		tableSalida.getColumnModel().getColumn(1).setMinWidth(0);
		tableSalida.getColumnModel().getColumn(1).setPreferredWidth(0);

//	     
//
//		table.getColumnModel().getColumn(2).setPreferredWidth(55);
//		table.getColumnModel().getColumn(3).setPreferredWidth(55);
//
//		table.getColumnModel().getColumn(6).setPreferredWidth(90);
//		table.getColumnModel().getColumn(9).setPreferredWidth(100);
//
//		// table.getColumnModel().getColumn(4).setMaxWidth(90);
//		table.getColumnModel().getColumn(4).setMinWidth(90);
//
//		table.getColumnModel().getColumn(4).setPreferredWidth(90);
//
//		// table.getColumnModel().getColumn(11).setMaxWidth(20);
//		table.getColumnModel().getColumn(11).setMinWidth(90);
//
//		table.getColumnModel().getColumn(11).setPreferredWidth(90);
//
//		table.getColumnModel().getColumn(12).setPreferredWidth(50);
//		// table.getColumnModel().getColumn(13).setPreferredWidth(100);
//
//		// table.getColumnModel().getColumn(14).setPreferredWidth(30);
//		table.getColumnModel().getColumn(10).setPreferredWidth(60);
		
		

		tableSalida.setFont(new Font("Tahoma", Font.TRUETYPE_FONT, 14));

		tableSalida.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));

		tableSalida.setRowHeight(25);

		// URGENTE

		tableSalida.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int col) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
//				db.connect();
//				entrada entrada = db.mostrarEntrada((int)table.getModel().getValueAt(row, 0));
//				db.close();
				//int status = (int) table.getModel().getValueAt(row, 4);
//				if (col == 1 && entrada.isUrgente()) {
//					//table.getColumnModel().getColumn(1).setCellRenderer(new ColumnColorRenderer(Color.lightGray, Color.red));
//					setToolTipText(entrada.getObservaciones());
//					setBackground(Color.RED);
//					setForeground(Color.WHITE);
//				}else {
//					setBackground(table.getBackground());
//					setForeground(table.getForeground());
//				}

//				if (status == 1) {
//					setBackground(Color.RED);
//					setForeground(Color.WHITE);
//				} else {
//					setBackground(table.getBackground());
//					setForeground(table.getForeground());
//				}
				return this;
			}
		});

		tableSalida.setAutoCreateRowSorter(true);

		tableSalida.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {

				if (e.getClickCount() == 2) {
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();
					int column = target.getSelectedColumn();
					if (column == 5) {
						if (!Login.usuarioActivo.getRole().equals("Registro")) {
							JOptionPane.showMessageDialog(null, "No tiene permisos para eliminar entrada.");
							return;
						}
						String[] options = { "Si", "No" };

						int opcionSeleccionada = -1;

						opcionSeleccionada = JOptionPane.showOptionDialog(null,
								"¿Desea eliminar esta entrada y sus archivos adjuntos?", "", JOptionPane.DEFAULT_OPTION,
								JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

						if (opcionSeleccionada == 1) {
							return;
						}

						Object item = target.getValueAt(row, column);
						
						if (target.getValueAt(row, 0).equals("")) {
							DefaultTableModel prueba = (DefaultTableModel) target.getModel();
							prueba.removeRow(row);
						}else {
							db.connect();
							if (!db.deleteSalidaFile((int)target.getValueAt(row, 0))) {
								JOptionPane.showMessageDialog(null, "Error al eliminar los archivos.");
								db.close();
								return;
							}else {
								db.close();
								DefaultTableModel prueba = (DefaultTableModel) target.getModel();
								prueba.removeRow(row);
								return;

							}
						}
					}

					if (column == 1) {
						// pendiente doble click jefe visto
						
						//new editComentario((int) target.getValueAt(row, 0), Integer.parseInt(usuario_id), usuario);
					}

				}
			}

		});
		if (scrollPane != null) {
			scrollPane.remove(tableSalida);
			scrollPane.add(tableSalida);
		} else {
			scrollPane = new JScrollPane(tableSalida);
		}

		//scrollPane = new JScrollPane(table);
		return scrollPane;
	}


	
}
