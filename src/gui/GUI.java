package gui;

import java.io.*;
import java.io.ObjectInputStream.GetField;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.net.URL;
import java.nio.file.Files;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import service.db;
import utils.WrapLayout;
import gui.editComentario;
import models.Usuario;
import models.comentarioJefe;
import models.entrada;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * FileChooserDemo.java uses these files:
 *   images/Open16.gif
 *   images/Save16.gif
 */
public class GUI extends JPanel implements ActionListener {

	DefaultTableModel tablemodel;
	static private final String newline = "\n";
	db db = new db();
	JButton openButton, saveButton, nuevaEntrada, execute, refresh;
	UtilDateModel model;
	Date date;
	DateFormat dateFormat;
	JDatePanelImpl datePanel;
	JDatePickerImpl datePicker;
	public static JTextField logDB = new JTextField();
	JFileChooser fc;
	JTextField minimumSupport;
	JTextField numeroInstancias;
	String saveFile, loadFile;
	JComboBox<String> comboComponentes;
	String usuario;
	JPanel tablaPanel;
	JTable table;
	JScrollPane scrollPane;
	static JFrame frame;
	static GUI gui;
	public static boolean error = false;
	public static boolean EntradaClosed = false;
	public static boolean PendienteVer = false;

	public StringBuilder salida = new StringBuilder();
	private JComboBox<String> comboBoxNegociado;
	nuevaEntrada nuevaentrada;
	Vector columnNames;
	Vector data;
	// private static int idUsuario;
	public static String usuario_id;
	private JPanel panel_log;
	public String role;
	private JButton btnMostrarPendientes;
	private JTextField searchTextField;
	Timer timer2;
	private boolean refreshFiltrado = false;
	private boolean refreshNormal = false;
	private boolean refreshInicio = false;
	private boolean refreshNoGestionadas = false;
	private JButton btnMostrarPdteVer;
	private JTextField userAreaLog = new JTextField();
	private JTextField updateLog = new JTextField();
	JScrollBar vScroll = new JScrollBar();
	int valueScroll = 0;
	selectRangeDates dates;
	private boolean refreshFechas = false;
	private String[] datesGUI;
	private JButton backDayButton;
	private JButton forwardDayButton;
	private boolean copiaSeguridad = true;
	private JComboBox comboBoxTipoBusqueda;
	private JLabel lblBusquedaPor;
	private JComboBox comboBoxZonaGeneral;
	private String dirTemp;
	public static JComboBox<String> comboBoxCategoria;

	class MyThread implements Runnable {

		private Boolean stop = false;

		public void run() {

			while (!stop) {

				// some business logic
			}
		}

		public Boolean getStop() {
			return stop;
		}

		public void setStop(Boolean stop) {
			this.stop = stop;
		}
	}

	public GUI(String usuario) {

		this.usuario = usuario;
//		this.role = role;

		// idUsuario = -1;

		// Cambiar el switch por role... case "jefe1" idUsuario = 0, case "jefe2"
		// idUsuario = 1.
		// Buscar donde ponga role.equals("Jefes") y buscar solucion para diferenciar
		// jefe1 del resto de jefes.
		// Crear una tabla nueva que distinga entre Jefes y Negociados?
		// Otra opcion es que en el archivo Config.cfg se pongan por orden los cargos y
		// se asigne idUsuario en funcion al index del array Cargos.
		// role.equals("Jefes") sería igual a idUsuario >0 && idUsuario <5.
		System.out.println("ROLE");
		System.out.println(role);
		System.out.println("Usuario");
		System.out.println(usuario);
		db.connect();
		usuario_id = db.getUserID(usuario);
		db.close();
		db.connect();
		
		Login.usuarioActivo = db.getUsuario(Integer.parseInt(usuario_id));
		db.close();
		System.out.println("Usuario_id");
		System.out.println(usuario_id);

//		if (posicionTemp != null) {
//			idUsuario = Integer.parseInt(posicionTemp) - 1;
//			System.out.println("idUsuario");
//			System.out.println(idUsuario);
//		} else {
//			switch (role) {
//			case "Jefe1":
//				idUsuario = 0;
//				break;
//			case "Jefe2":
//				idUsuario = 1;
//				break;
//			case "Jefe3":
//				idUsuario = 2;
//				break;
//			case "Jefe4":
//				idUsuario = 3;
//				break;
//			case "Jefe5":
//				idUsuario = 4;
//				break;
//			case "Registro":
//				idUsuario = 5;
//				break;
//			default:
//				idUsuario = -1;
//				break;
//			}
//		}

		/*
		 * switch (usuario) { case "coronel": idUsuario = 0; break; case "jop":
		 * idUsuario = 1; break; case "jepe": idUsuario = 2; break; case "jpj":
		 * idUsuario = 3; break; case "japo": idUsuario = 4; break; case "registro":
		 * idUsuario = 5; break; default: idUsuario = -1; break; }
		 * 
		 * 
		 * if (role.equals("Registro")){ idUsuario = 5; }
		 */

		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		Timer timer = new Timer(0, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {

					

					

					if (EntradaClosed || PendienteVer) {

						if (dates != null) {
							datesGUI = dates.dates;
							if (dates.verTodas) {
								refreshInicio();
							} else if (dates.verDates) {
								refreshFiltradoFechas(datesGUI);
							}
						} else if (refreshNormal) {
							
							
							refresh();

						} else if (refreshFiltrado) {
							refreshFiltrado();
						} else if (refreshInicio) {
							refreshInicio();
						} else if (refreshFechas) {
							refreshFiltradoFechas(datesGUI);
						}
//							else if (refreshNoGestionadas) {
//							refreshNoGestionadas();
//						}
						EntradaClosed = false;
						PendienteVer = false;
						updateLog();

					}

				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		timer2 = new Timer(0, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
//				System.out.println(EntradaClosed);
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				DateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy"); // 2016/11/16 12:08:43
				if (copiaSeguridad && cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && !new File(
						"H:\\plmcomd\\COMUN\\CORRESPONDENCIA ENTRADA\\DOCS\\backup_" + dateFormat1.format(new Date()))
								.exists()) {
					try {
						System.out.println("copiando ");
						Files.copy(new File(Login.BASE_DIR + "db.sqlite").toPath(),
								new File("H:\\plmcomd\\COMUN\\CORRESPONDENCIA ENTRADA\\DOCS\\backup_"
										+ dateFormat1.format(new Date())).toPath());
						copiaSeguridad = false;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

				try {
					if (refreshNormal) {
						refresh();
					} else if (refreshFiltrado) {
						refreshFiltrado();
					} else if (refreshInicio) {
						refreshInicio();
					} else if (refreshFechas) {
						refreshFiltradoFechas(datesGUI);
					}

					// if ((idUsuario >= 0 && idUsuario < 5)) {
//					if (role.equals("Jefe1") || role.equals("Jefes")) {
					if (Login.usuarioActivo.isJefe) {

						db.connect();
						if (!db.tieneJefePendienteVer(Integer.parseInt(usuario_id), Login.usuarioActivo)) {
							btnMostrarPdteVer.setBackground(btnMostrarPendientes.getBackground());
						} else {
							btnMostrarPdteVer.setBackground(new Color(255, 102, 102));

						}
						db.close();
					}

				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					db.close();

					e1.printStackTrace();
				}

				updateLog();
			}
		});

		timer.setDelay(5000); // delay for 1 seconds
		timer.start();

		timer2.setDelay(30000); // delay for 15 seconds
		timer2.start();

		// Create the log first, because the action listeners
		// need to refer to it.
		logDB = new JTextField();
		logDB.setColumns(15);

		logDB.setEditable(false);
		updateLog.setEditable(false);
		userAreaLog.setEditable(false);
//		JScrollPane logScrollPane = new JScrollPane(log);

		// Create a file chooser

		try {
			FileNameExtensionFilter filter = new FileNameExtensionFilter("DATA FILES *.dat", "dat");
			fc = new JFileChooser(new java.io.File(".").getCanonicalPath() + "\\dataset");
			fc.setFileFilter(filter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Uncomment one of the following lines to try a different
		// file selection mode. The first allows just directories
		// to be selected (and, at least in the Java look and feel,
		// shown). The second allows both files and directories
		// to be selected. If you leave these lines commented out,
		// then the default mode (FILES_ONLY) will be used.
		//
		// fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		// Create the open button. We use the image from the JLF
		// Graphics Repository (but we extracted it from the jar).
//		openButton = new JButton("Open a File...", createImageIcon("../images/Open16.gif"));
//		openButton.addActionListener(this);

		// Create the save button. We use the image from the JLF
		// Graphics Repository (but we extracted it from the jar).

//		saveButton = new JButton("Save", createImageIcon("../images/save16.gif"));
//		saveButton.addActionListener(this);

		URL url = GUI.class.getResource("/refresh.png");
		ImageIcon icon = new ImageIcon(url);
		refresh = new JButton("Actualizar", icon);
		refresh.addActionListener(this);

		nuevaEntrada = new JButton("Nueva entrada");

		// que solo pueda agregar entradas Registro????? o también los Jefes???

		// if (role.equals("Jefes") || role.equals("Registro")) {Asesoria Juridica

		nuevaEntrada.setEnabled(false);

		if (Login.usuarioActivo != null) {
			if (Login.usuarioActivo.permiso) {
				nuevaEntrada.setEnabled(true);
			}
		}
		

//		nuevaEntrada.setEnabled((idUsuario >= 0 || idUsuario <=5)?true:false);
//		nuevaEntrada.setEnabled((idUsuario == -1)?false:true);

		nuevaEntrada.addActionListener(this);

		comboComponentes = new JComboBox<>();
		// For layout purposes, put the buttons in a separate panel
		JPanel buttonPanel = new JPanel();
//		buttonPanel.setPreferredSize(new Dimension(10, 70));
		buttonPanel.setLayout(new WrapLayout());
		buttonPanel.setBackground(new Color(204, 204, 204));
		date = new Date();

		model = new UtilDateModel(date);

		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println(arg0.getKeyCode());
				if (arg0.getKeyCode() == KeyEvent.VK_F5) {
					System.out.println("Pulsada la tecla f5");
					try {
						if (refreshNormal) {
							refresh();
						} else if (refreshFiltrado) {
							refreshFiltrado();
						} else if (refreshInicio) {
							refreshInicio();
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
					datePanel.getModel().setDate(datePicker.getModel().getYear(), datePicker.getModel().getMonth(),
							datePicker.getModel().getDay() + 1);
					datePanel.getModel().setSelected(true);
					try {
						refresh();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				if (arg0.getKeyCode() == KeyEvent.VK_LEFT) {
					datePanel.getModel().setDate(datePicker.getModel().getYear(), datePicker.getModel().getMonth(),
							datePicker.getModel().getDay() - 1);
					datePanel.getModel().setSelected(true);
					try {
						refresh();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		});

		dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("es_ES"));

		// System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43

		// Calendar calendar = Calendar.getInstance();
		// System.out.println(calendar.get(Calendar.YEAR));
		// model.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
		// calendar.get(Calendar.DAY_OF_MONTH));
		datePanel = new JDatePanelImpl(model);

		comboBoxNegociado = new JComboBox<>();
		comboBoxNegociado.setBackground(new Color(204, 204, 204));
		comboBoxNegociado.setBorder(new TitledBorder(null, "Area", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		comboBoxNegociado.addItem("Todos");
		for (String str : Login.NEGOCIADOS) {
			comboBoxNegociado.addItem(str);

		}
		/*
		 * comboBoxNegociado.addItem("Todos"); comboBoxNegociado.addItem("Operaciones");
		 * comboBoxNegociado.addItem("Personal"); comboBoxNegociado.addItem("Apoyo");
		 * comboBoxNegociado.addItem("Expedientes"); comboBoxNegociado.addItem("OGE");
		 * comboBoxNegociado.addItem("SIGO");
		 * comboBoxNegociado.addItem("Varios negociados");
		 * comboBoxNegociado.addItem("Ayudantia"); comboBoxNegociado.addItem("OPC");
		 * comboBoxNegociado.addItem("I.A.E."); comboBoxNegociado.addItem("Otros");
		 */

//		if (role.equals("Jefe1") || role.equals("Jefes") || role.equals("Registro")) {
		
		if (Login.usuarioActivo.isJefe || Login.usuarioActivo.permiso) {
			comboBoxNegociado.setSelectedItem("Todos");
		} else {
			comboBoxNegociado.setEnabled(false);
			comboBoxNegociado.setSelectedItem(Login.usuarioActivo.getRole());
		}

		comboBoxNegociado.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

				try {
					if (refreshNormal) {
						refresh();
					} else if (refreshFiltrado) {
						refreshFiltrado();
					} else if (refreshInicio) {
						refreshInicio();
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		searchTextField = new JTextField();
		searchTextField.setToolTipText("escribir * para cualquier coincidencia");
		searchTextField.setHorizontalAlignment(SwingConstants.LEFT);
		searchTextField.setPreferredSize(new Dimension(20, 20));
		searchTextField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {

			}

			@Override
			public void keyReleased(KeyEvent arg0) {

			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == 10) {
					try {
						search();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						
						e.printStackTrace();
					}
				}
			}
		});

		comboBoxZonaGeneral = new JComboBox();
		comboBoxZonaGeneral.setModel(new DefaultComboBoxModel(new String[] { "ZONA-GENERAL", "PLM" }));

//		if (Login.usuarioActivo.getRole().equals("Jefe1")) {
//			buttonPanel.add(comboBoxZonaGeneral);
//		}

		if (Login.BASE_DIR.equals(Login.BASE_DIR_GENERAL)) {
			comboBoxZonaGeneral.setSelectedIndex(0);
		} else {
			comboBoxZonaGeneral.setSelectedIndex(1);
		}

		comboBoxZonaGeneral.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (comboBoxZonaGeneral.getSelectedIndex() == 0) {
					Login.BASE_DIR = Login.BASE_DIR_GENERAL;
					db.connect();
					Login.NEGOCIADOS = db.getNegociados();
					db.close();

					db.connect();
					Login.CARGOS = db.getCargos();
					db.close();

					db.connect();
					Login.CANALES = db.getCanales();
					db.close();
					remove(table);
					try {
						refresh();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				if (comboBoxZonaGeneral.getSelectedIndex() == 1) {
					Login.BASE_DIR = Login.BASE_DIR_PLM;
					db.connect();
					Login.NEGOCIADOS = db.getNegociados();
					db.close();

					db.connect();
					Login.CARGOS = db.getCargos();
					db.close();

					db.connect();
					Login.CANALES = db.getCanales();
					db.close();
					remove(table);
					try {
						refresh();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		});

		lblBusquedaPor = new JLabel("BUSQUEDA POR");
		buttonPanel.add(lblBusquedaPor);

		comboBoxTipoBusqueda = new JComboBox();
		comboBoxTipoBusqueda.setModel(new DefaultComboBoxModel(new String[] { "Asunto", "NUM. ENTRADA" }));
		buttonPanel.add(comboBoxTipoBusqueda);
		buttonPanel.add(searchTextField);
		searchTextField.setColumns(20);
		buttonPanel.add(comboBoxNegociado);

//		usuario = new JComboBox();
//		usuario.addItem("Jefe 1");
//		usuario.addItem("Jefe 2");
//		usuario.addItem("Jefe 3");
//		usuario.addItem("Jefe 4");
//		usuario.addItem("Jefe 5");
//		buttonPanel.add(usuario);
		datePanel.setLocale(new Locale("es", "ES"));

		URL back = GUI.class.getResource("/back.png");
		ImageIcon backIcon = new ImageIcon(back);
		backDayButton = new JButton("", backIcon);
		backDayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				datePanel.getModel().setDate(datePicker.getModel().getYear(), datePicker.getModel().getMonth(),
						datePicker.getModel().getDay() - 1);
				datePanel.getModel().setSelected(true);
				try {
					refresh();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		});
		
		comboBoxCategoria = new JComboBox<String>();
		comboBoxCategoria.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Categoria", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
		comboBoxCategoria.setBackground(new Color(204, 204, 204));
		buttonPanel.add(comboBoxCategoria);
		
		comboBoxCategoria.addItem("Todas");
		
		for (String categoria : Login.CATEGORIAS) {
			comboBoxCategoria.addItem(categoria);
		}
		
		comboBoxCategoria.addActionListener(new ActionListener() {
			


			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
//				System.out.println("comboBoxCategoria.getItemCount() " + comboBoxCategoria.getItemCount());
//				System.out.println("Login.CATEGORIAS.size() "+ Login.CATEGORIAS.size());
//				
//				if (comboBoxCategoria.getItemCount() != Login.CATEGORIAS.size()) {
//					System.out.println("dentro if ");
//					comboBoxCategoria.removeAllItems();
//					comboBoxCategoria.addItem("Todas");
//					
//					for (String categoria : Login.CATEGORIAS) {
//						comboBoxCategoria.addItem(categoria);
//					}
//					
//				}

				try {
					if (refreshNormal) {
						refresh();
					} else if (refreshFiltrado) {
						refreshFiltrado();
					} else if (refreshInicio) {
						refreshInicio();
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		});

		buttonPanel.add(backDayButton);

		URL forward = GUI.class.getResource("/forward.png");
		ImageIcon forwardIcon = new ImageIcon(forward);
		forwardDayButton = new JButton("", forwardIcon);
		forwardDayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				datePanel.getModel().setDate(datePicker.getModel().getYear(), datePicker.getModel().getMonth(),
						datePicker.getModel().getDay() + 1);
				datePanel.getModel().setSelected(true);
				try {
					refresh();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		});

		datePicker = new JDatePickerImpl(datePanel);
		datePicker.getJFormattedTextField().setLocale(new Locale("es", "ES"));
		datePicker.setLocale(new Locale("es", "ES"));
//		datePanel.setDateFormat(DateFormat.FULL);
		buttonPanel.add(datePicker);
		buttonPanel.add(forwardDayButton);

		// buttonPanel.add(openButton);
		// buttonPanel.add(saveButton);
		buttonPanel.add(nuevaEntrada);
		buttonPanel.add(refresh);

		datePicker.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				try {
					refresh();
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		});

		setLayout(new BorderLayout(0, 0));

//		panel.setBounds(10, 356, 677, 96);

		// Add the buttons and the log to this panel.
		add(buttonPanel, BorderLayout.NORTH);

		btnMostrarPendientes = new JButton("Mostrar Pdte. Tramit.");
		btnMostrarPendientes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					refreshFiltrado();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		buttonPanel.add(btnMostrarPendientes);

//		if (role.equals("Registro")) {
//			// btnMostrarEntradasNo.setVisible(true);
//		}

		btnMostrarPdteVer = new JButton("Mostrar Pdte. ver");

//		if (role.equals("Jefe1") || role.equals("Jefes")) {
//			buttonPanel.add(btnMostrarPdteVer);
//		}

		btnMostrarPdteVer.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

//				dates = new selectRangeDates(datesGUI);

				System.out.println("btnMostrarPdteVer pulsado");
				try {
					refreshInicio();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

//		if (role.equals("Jefe1") || role.equals("Jefes")) {
		if (Login.usuarioActivo.isJefe) {
			
			buttonPanel.add(btnMostrarPdteVer);
		}

		db.connect();
		if (Login.usuarioActivo.isJefe
				&& db.tieneJefePendienteVer(Integer.parseInt(usuario_id), Login.usuarioActivo)) {
			btnMostrarPdteVer.setBackground(new Color(255, 102, 102));
		}
		db.close();

		refreshNormal = true;

		db.connect();
		Date date1;
		try {
			date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(datePicker.getJFormattedTextField().getText());

//			if (role.equals("Jefe1") || role.equals("Registro")) {
//				add(TableFromDatabase(db.mostrarEntradasCoronelPendienteVerV2(dateFormat.format(date1), Login.usuarioActivo)),
//						BorderLayout.CENTER);
//
//			} else if (role.equals("Jefes")) {
//				add(TableFromDatabase(db.mostrarEntradasJefesPendienteVer(dateFormat.format(date1), Integer.parseInt(usuario_id))), BorderLayout.CENTER);
//
//			} else {
//				add(TableFromDatabase(db.mostrarEntradasSinConfidencial(dateFormat.format(date1), role,
//						comboBoxNegociado.getSelectedItem().toString())), BorderLayout.CENTER);
//			}

//			add(TableFromDatabase(db.mostrarEntradasPendienteVerV2(dateFormat.format(date1), Login.usuarioActivo, comboBoxCategoria.getSelectedItem().toString())),
//					BorderLayout.CENTER);

//			log.selectAll();
//			log.replaceSelection("");
//			log.append("Conectado");+
			
			if (Login.usuarioActivo.isJefe) {
				add(TableFromDatabase(db.pendienteVer(Login.usuarioActivo, comboBoxNegociado.getSelectedItem().toString(),comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);

			}
			
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			db.close();
			e1.printStackTrace();
//			log.selectAll();
//			log.replaceSelection("");
//			log.append("Fallo en la conexión");
		}

		db.close();

		timer2.restart();
		addUserLog();

		// panel = new JPanel();
		// panel.setPreferredSize(new Dimension(1000, 200));
		// add(panel, BorderLayout.SOUTH);

		panel_log = new JPanel();
		panel_log.setLayout(new WrapLayout());
		panel_log.setBackground(new Color(204, 204, 204));
//		panel_1.setPreferredSize(new Dimension(10, 30));
		panel_log.add(logDB);

//		FlowLayout flowLayout = (FlowLayout) panel_log.getLayout();
//		flowLayout.setHgap(10);
		add(panel_log, BorderLayout.SOUTH);

		panel_log.add(userAreaLog);
		userAreaLog.setColumns(30);

		panel_log.add(updateLog);
		updateLog.setColumns(20);
//		add(logScrollPane, BorderLayout.CENTER);
	}

	public void addUserLog() {
		String output = usuario.substring(0, 1).toUpperCase() + usuario.substring(1);
		userAreaLog.setText("Usuario: " + output + "   Area: " + Login.usuarioActivo.getRole());
	}

	public void updateLog() {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		updateLog.setText("Última Actualización: " + timeFormat.format(new Date()));
	}

	public JScrollPane TableFromDatabase(ResultSet rs) {
		// JPanel result = new JPanel();
		// result.setSize(new Dimension(1200, 800));
		if (scrollPane != null) {
			valueScroll = scrollPane.getVerticalScrollBar().getValue();
		}

		columnNames = new Vector();
		data = new Vector();

		try {
			// Connect to an Access Database

			// Read data from a table

			ResultSetMetaData md = rs.getMetaData();

//			for (int i = 1; i < md.getColumnCount(); i++) {
//				System.out.println("Columna: " + md.getColumnName(i));
//			}

			// int columns = md.getColumnCount();

			// Get column names 8 fijas nº / asunto / Fecha / Area / Confidencial /
			// Tramitado / Canal Entrada / Eliminar
			columnNames.addElement("Nº");
			columnNames.addElement("ASUNTO");
			columnNames.addElement("FECHA");
			columnNames.addElement("AREA");

			columnNames.addElement("CONFIDENCIAL");
			columnNames.addElement("TRAMITADO");
			columnNames.addElement("CANAL ENTRADA");

			columnNames.addElement("ELIMINAR");
			for (Map.Entry<Integer, String> entry : Login.CARGOS.entrySet()) {
				// System.out.println(entry.getKey() + "key");
				// System.out.println(entry.getValue() + "value");
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
					
					System.out.println("id " + rs.getObject("id"));

					if (!data.isEmpty()) {
						lastPosition = data.lastIndexOf(data.lastElement());
						lastElement = (Vector) data.lastElement();
					}

					entrada_id = (int) rs.getObject("id");
					row.add(0, rs.getObject("id"));
					row.add(1, rs.getObject("Asunto"));
					row.add(2, rs.getObject("Fecha"));
					row.add(3, rs.getObject("Area"));
					row.add(4, "4"); // confidencianl
					row.add(5, "5"); // tramitado
					row.add(6, "6"); // canal entrada
					URL url = GUI.class.getResource("/delete.png");
					ImageIcon icon = new ImageIcon(url);
					row.add(7, icon); // eliminar

					url = GUI.class.getResource("/vacio.png");
					icon = new ImageIcon(url);

					int iterator = 0;
					for (Map.Entry<Integer, String> entry : Login.CARGOS.entrySet()) {
						row.add(8 + iterator, icon);
						iterator++;
					}

					row.add(12, icon);

					// System.out.println("rs.getObject(id)" + rs.getObject("id"));
					// System.out.println("rs.getObject(visto)" + rs.getObject("visto"));
					try {
//						db.connect();
//						posicion = db.getPosicion(rs.getInt("usuario_id"));
//						db.close();
						
								for (Map.Entry<Integer, Integer> entry : Login.POSICIONES.entrySet()) {
									
									if (entry.getKey().equals(rs.getInt("usuario_id"))) {
										posicion = entry.getValue();
									}
								}
								
						if (rs.getInt("visto") == 0) {
							url = GUI.class.getResource("/vacio.png");
							icon = new ImageIcon(url);
							row.set(posicion + 7, icon);

						}
						if (rs.getInt("visto") == 1) {
							url = GUI.class.getResource("/single.png");
							icon = new ImageIcon(url);
							row.set(posicion + 7, icon);
						}
						if (rs.getInt("visto") == 2) {
							url = GUI.class.getResource("/doble.png");
							icon = new ImageIcon(url);
							row.set(posicion + 7, icon);
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(
								"DB ERROR posicion = Integer.parseInt(db.getPosicion((int) rs.getObject(\"usuario_id\")));\r\n"
										+ e);
					}

					try {

						if (rs.getInt("confidencial") == 0) {
							url = GUI.class.getResource("/vacio.png");
							icon = new ImageIcon(url);
							row.set(4, icon);

						}
						if (rs.getInt("confidencial") == 1) {
							url = GUI.class.getResource("/single.png");
							icon = new ImageIcon(url);
							row.set(4, icon);
						}
						if (rs.getInt("tramitado") == 0) {
							url = GUI.class.getResource("/vacio.png");
							icon = new ImageIcon(url);
							row.set(5, icon);

						}
						if (rs.getInt("tramitado") == 1) {
							url = GUI.class.getResource("/single.png");
							icon = new ImageIcon(url);
							row.set(5, icon);
						}

					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("confidencial y tramitado " + e);
					}

					row.set(6, rs.getObject("canalEntrada"));

					data.addElement(row);

				} else {
//					System.out.println("elseeeeeeeeeeeeeee " + (int) rs.getObject("usuario_id"));
//					db.connect();
//					posicion = db.getPosicion((int) rs.getObject("usuario_id"));
//					db.close();
					for (Map.Entry<Integer, Integer> entry : Login.POSICIONES.entrySet()) {
						
						if (entry.getKey().equals(rs.getInt("usuario_id"))) {
							posicion = entry.getValue();
						}
					}
					if (data.isEmpty()) {
						System.out.println("Data is empty");
						lastElement = (Vector) data.lastElement();
						if ((int) rs.getObject("visto") == 0) {
							URL url = GUI.class.getResource("/vacio.png");
							ImageIcon icon = new ImageIcon(url);
							lastElement.set(posicion + 7, icon);

						}
						if ((int) rs.getObject("visto") == 1) {
							URL url = GUI.class.getResource("/single.png");
							ImageIcon icon = new ImageIcon(url);
							lastElement.set(posicion + 7, icon);
						}
						if ((int) rs.getObject("visto") == 2) {
							URL url = GUI.class.getResource("/doble.png");
							ImageIcon icon = new ImageIcon(url);
							lastElement.set(posicion + 7, icon);
						}
					} else {
						lastElement = (Vector) data.lastElement();

						if ((int) rs.getObject("visto") == 0) {
							URL url = GUI.class.getResource("/vacio.png");
							ImageIcon icon = new ImageIcon(url);
							lastElement.set(posicion + 7, icon);

						}
						if ((int) rs.getObject("visto") == 1) {
							URL url = GUI.class.getResource("/single.png");
							ImageIcon icon = new ImageIcon(url);
							lastElement.set(posicion + 7, icon);
						}
						if ((int) rs.getObject("visto") == 2) {
							URL url = GUI.class.getResource("/doble.png");
							ImageIcon icon = new ImageIcon(url);
							lastElement.set(posicion + 7, icon);
						}
					}
				}

//				for (Map.Entry<String, String> entry : Login.CARGOS.entrySet()) {
//
//					if (Integer.parseInt(entry.getKey().split(";")[1]) == (int) rs.getObject("usuario_id")) {
//						System.out.println(entry.getValue());
//
//						if (Integer.parseInt(entry.getKey().split(";")[0]) == posicion) {
//							if ((int) rs.getObject("visto") == 0) {
//								URL url = GUI.class.getResource("/vacio.png");
//								ImageIcon icon = new ImageIcon(url);
//								row.addElement(icon);
//							}
//							if ((int) rs.getObject("visto") == 1) {
//								URL url = GUI.class.getResource("/single.png");
//								ImageIcon icon = new ImageIcon(url);
//								row.addElement(icon);
//							}
//							if ((int) rs.getObject("visto") == 2) {
//								URL url = GUI.class.getResource("/doble.png");
//								ImageIcon icon = new ImageIcon(url);
//								row.addElement(icon);
//							}
//							posicion = 1;
//							break;
//						} else {
//							posicion++;
//						}
//
//					}
//
//				}

//				if (entrada_id != (int) rs.getObject("id")) {
//					rs.previous();
//				}

//					if (Integer.parseInt(entry.getKey().split(";")[0]) == posicion) {
//						System.out.println("Entry posicion =  " + entry.getKey().split(";")[0]);
//						System.out.println("Posicion = " + posicion);
//						
//						if (Integer.parseInt(entry.getKey().split(";")[1]) == (int) rs.getObject("usuario_id")) {
//							System.out.println("Usuario_id Entry = " + (entry.getKey().split(";")[1]));
//							System.out.println("Usuario_id vector = " + rs.getObject("usuario_id"));
//							
//							if ((int) rs.getObject("visto") == 0) {
//								URL url = GUI.class.getResource("/vacio.png");
//								ImageIcon icon = new ImageIcon(url);
//								row.addElement(icon);
//							}
//							if ((int) rs.getObject("visto") == 1) {
//								URL url = GUI.class.getResource("/single.png");
//								ImageIcon icon = new ImageIcon(url);
//								row.addElement(icon);
//							}
//							if ((int) rs.getObject("visto") == 2) {
//								URL url = GUI.class.getResource("/doble.png");
//								ImageIcon icon = new ImageIcon(url);
//								row.addElement(icon);
//							}
//						}
//						
//					}

//				if (entrada_id != (int) rs.getObject("id")) {
//					entrada_id = (int) rs.getObject("id");
//					
//				}else {
//					
//				}

//				for (Map.Entry<String, String> entry: Login.CARGOS.entrySet()) {
//					
//					
//					if (Integer.parseInt(entry.getKey().split(";")[0]) == posicion) {
//						URL url = GUI.class.getResource("/vacio.png");
//						ImageIcon icon = new ImageIcon(url);
//						row.addElement(icon);
//						
//						continue;
//					}
//					
//					if ((int) rs.getObject(entry.getValue()) == 0) {
//						URL url = GUI.class.getResource("/vacio.png");
//						ImageIcon icon = new ImageIcon(url);
//						row.addElement(icon);
//						continue;
//					}
//					
//					if ((int) rs.getObject(entry.getValue()) == 1) {
//						URL url = GUI.class.getResource("/single.png");
//						ImageIcon icon = new ImageIcon(url);
//						row.addElement(icon);
//						continue;
//					}
//					if ((int) rs.getObject(entry.getValue()) == 2) {
//						URL url = GUI.class.getResource("/doble.png");
//						ImageIcon icon = new ImageIcon(url);
//						row.addElement(icon);
//						continue;
//					}
//				}
//				
//				
//
//
//				for (int i = 1; i <= columns; i++) {
////					if (i == 2) System.out.println("tipo data " + rs.getObject(2));
//					if (i == 5) {
//						continue;
//					}
//
////					if (i == 2) {
////						URL url = GUI.class.getResource("/vacio.png");
////						ImageIcon icon = new ImageIcon(url);
////						row.addElement((String)rs.getObject(i) + " Con comentario");
////						
////						continue;
////					}
//
//					if ((i > 6) && (i <= 14)) {
////						System.out.println("7 " + (int) rs.getObject(7));
//						if (rs.getObject(i) == null)
//							continue;
//
//						if (i >= 7 && i <= 11) {
//							if ((int) rs.getObject(i) == 0) {
//								URL url = GUI.class.getResource("/vacio.png");
//								ImageIcon icon = new ImageIcon(url);
//								row.addElement(icon);
//
//								continue;
//							}
//							if ((int) rs.getObject(i) == 1) {
//								URL url = GUI.class.getResource("/single.png");
//								ImageIcon icon = new ImageIcon(url);
//								row.addElement(icon);
//								continue;
//							}
//							if ((int) rs.getObject(i) == 2) {
//								URL url = GUI.class.getResource("/doble.png");
//								ImageIcon icon = new ImageIcon(url);
//								row.addElement(icon);
//								continue;
//							}
//
//						}
//						if (rs.getObject(i).equals(1)) {
//							URL url = GUI.class.getResource("/single.png");
//							ImageIcon icon = new ImageIcon(url);
//							row.addElement(icon);
//
//						} else {
//							URL url = GUI.class.getResource("/vacio.png");
//							ImageIcon icon = new ImageIcon(url);
//							row.addElement(icon);
//
//						}
//
//					} else {
//						row.addElement(rs.getObject(i));
//					}
//
//					URL url = GUI.class.getResource("/delete.png");
//					ImageIcon icon = new ImageIcon(url);
//					if (i == columns - 1)
//						row.addElement(icon);
//				}

			}
			System.out.println("fin tableModel without data");
			rs.close();

		} catch (Exception e) {
			System.out.println("catch fin while " + e);
			e.printStackTrace();
		}

		// Create table with database data
		tablemodel = new DefaultTableModel(data, columnNames) {

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
				return false;
			}

		};
		


		table = new JTable(tablemodel);
		table.setPreferredScrollableViewportSize(new Dimension(0, 0));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.getColumnModel().getColumn(0).setPreferredWidth(40);
		table.getColumnModel().getColumn(1).setPreferredWidth(600);

		table.getColumnModel().getColumn(2).setPreferredWidth(55);
		table.getColumnModel().getColumn(3).setPreferredWidth(55);

		table.getColumnModel().getColumn(6).setPreferredWidth(90);
//		table.getColumnModel().getColumn(9).setPreferredWidth(100);

		// table.getColumnModel().getColumn(4).setMaxWidth(90);
		table.getColumnModel().getColumn(4).setMinWidth(90);

		table.getColumnModel().getColumn(4).setPreferredWidth(90);

		// table.getColumnModel().getColumn(11).setMaxWidth(20);
//		table.getColumnModel().getColumn(11).setMinWidth(90);

//		table.getColumnModel().getColumn(11).setPreferredWidth(90);

//		table.getColumnModel().getColumn(12).setPreferredWidth(50);
		// table.getColumnModel().getColumn(13).setPreferredWidth(100);

		// table.getColumnModel().getColumn(14).setPreferredWidth(30);
//		table.getColumnModel().getColumn(10).setPreferredWidth(60);

		table.setFont(new Font("Tahoma", Font.TRUETYPE_FONT, 14));

		table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));

		table.setRowHeight(25);

		// URGENTE

		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int col) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
				db.connect();
				entrada entrada = db.mostrarEntrada((int) table.getModel().getValueAt(row, 0));
				db.close();
				// int status = (int) table.getModel().getValueAt(row, 4);
				if (col == 1 && entrada.isUrgente()) {
					// table.getColumnModel().getColumn(1).setCellRenderer(new
					// ColumnColorRenderer(Color.lightGray, Color.red));
					setToolTipText(entrada.getObservaciones());

					setBackground(Color.RED);
					setForeground(Color.WHITE);
				} else {
					setBackground(table.getBackground());
					setForeground(table.getForeground());
				}

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

		table.setAutoCreateRowSorter(true);

		table.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {

				if (e.getClickCount() == 2) {
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();
					int column = target.getSelectedColumn();
					// do some stuff

//					System.out.println(row + " " + column + " valor " + target.getValueAt(row, column));

					// System.out.println("idUsuario " + idUsuario + " fuera if confidencial " +
					// target.getValueAt(row, 10));

//					db.connect();
//					boolean isConfidencial = db.entradaIsConfidencial((int) target.getValueAt(row, 0));
//					db.close();
//					if (target.getValueAt(row, 10).equals(true) && (idUsuario < 0 || idUsuario >5)) {

					if (column == 7) {

//						if (!role.equals("Registro")) {
						if (!Login.usuarioActivo.permiso) {

							JOptionPane.showMessageDialog(null, "No tiene permisos para eliminar entrada.");
							return;
						}
						String[] options = { "Si", "No" };

						int opcionSeleccionada = -1;

						opcionSeleccionada = JOptionPane.showOptionDialog(null,
								"¿Desea eliminar esta entrada y sus archivos adjuntos?", "", JOptionPane.DEFAULT_OPTION,
								JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
						// int index = listDrag.locationToIndex(e.getPoint());

						if (opcionSeleccionada == 1) {
							return;
						}

						Object item = target.getValueAt(row, column);
						db.connect();
						if (!db.deleteFiles((int) target.getValueAt(row, 0))) {
							JOptionPane.showMessageDialog(null, "Error al eliminar los archivos.");
							return;
						}
						db.close();

						db.connect();
						if (!db.deleteAntecedentesFiles((int) target.getValueAt(row, 0))) {
							JOptionPane.showMessageDialog(null, "Error al eliminar los archivos.");
							return;
						}
						db.close();

						db.connect();
						if (!db.deleteSalidaFiles((int) target.getValueAt(row, 0))) {
							JOptionPane.showMessageDialog(null, "Error al eliminar los archivos.");
							return;
						}
						db.close();

						db.connect();
						db.eliminarAllFiles((int) target.getValueAt(row, 0));
						db.close();

						db.connect();
						db.eliminarComentario((int) target.getValueAt(row, 0));
						db.close();

						db.connect();
						db.eliminarDestinatarios((int) target.getValueAt(row, 0));
						db.close();

						db.connect();
						db.eliminarDestinatariosJefes((int) target.getValueAt(row, 0));
						db.close();

						db.connect();
						db.eliminarAntecedentes((int) target.getValueAt(row, 0));
						db.close();

						db.connect();
						db.eliminarSalida((int) target.getValueAt(row, 0));
						db.close();
						
						db.connect();
						db.borrarCategorias((int) target.getValueAt(row, 0));
						db.close();
						
						db.connect();
						db.eliminarCanalEntradas((int) target.getValueAt(row, 0));
						db.close(); 
						
//						db.connect();
//						db.borrarVistoBuenoSalida((int) target.getValueAt(row, 0));
//						db.close();

						db.connect();
						if (db.eliminarEntrada((int) target.getValueAt(row, 0))) {
							JOptionPane.showMessageDialog(null, "La entrada ha sido eliminada correctamente.");
						} else
							JOptionPane.showMessageDialog(null, "La entrada no ha sido eliminada.");
						db.close();

						DefaultTableModel prueba = (DefaultTableModel) target.getModel();

						prueba.removeRow(row);

					}
					
					if (column > 7) {
						if (column-7 == Login.usuarioActivo.posicion) {
							System.out.println("columna jefe");
							int item = (int) target.getValueAt(row, 0);
							Date dateToday = new Date();
							DateFormat dateFormat_normal = new SimpleDateFormat("dd/MM/yyyy");
							DateFormat timeFormat = new SimpleDateFormat("HH:mm");
							

							
//							db.connect();
//							db.actualizarComentario(comentarioVacio);
							db.connect();
							entrada entradaItem = db.mostrarEntrada(item);
							db.close();
							db.connect();
							entradaItem.comentario = db.mostrarComentario(item);
							db.close();
							
							System.out.println("numero de comentario  " + entradaItem.comentario.comentarios.size());
							
							if (entradaItem.comentario.comentarios.size() == 0) {
								comentarioJefe comentarioVacio = new comentarioJefe(item, Login.usuarioActivo.usuario_id, Login.usuarioActivo.getRole(),  Login.usuarioActivo.getNombre_usuario(),Login.usuarioActivo.posicion, dateFormat_normal.format(dateToday), timeFormat.format(dateToday), "", 1);
								db.connect();
								db.actualizarComentario(comentarioVacio);
								db.close();
								URL url = GUI.class.getResource("/single.png");
								ImageIcon icon = new ImageIcon(url);
								target.setValueAt(icon, row, column);
							}
							
							for (comentarioJefe comentarioElement : entradaItem.comentario.comentarios) {
								System.out.println("comentarioElement " + comentarioElement.getNombreJefe());
									System.out.println(Login.usuarioActivo.getUsuario_id());
									System.out.println(comentarioElement.getUsuario_id());
									if (Login.usuarioActivo.getUsuario_id() == comentarioElement.getUsuario_id()) {
										System.out.println("dentro if 22");
										if (comentarioElement.getVisto() == 2) {
											return;
										}
										comentarioElement.setVisto(comentarioElement.getVisto() == 0 ? 1 : 0);										
										if (comentarioElement.getVisto() == 1) {
											URL url = GUI.class.getResource("/single.png");
											ImageIcon icon = new ImageIcon(url);
											target.setValueAt(icon, row, column);
										}
										if (comentarioElement.getVisto() == 0) {
											URL url = GUI.class.getResource("/vacio.png");
											ImageIcon icon = new ImageIcon(url);
											target.setValueAt(icon, row, column);
										}
									}
							}
							entradaItem.comentario.update();
						}
					}

					

					if (column == 1) {

						
						setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						File tempPLM = new File(Login.BASE_DIR_PLM + "\\TEMP\\");
						File tempGeneral = new File(Login.BASE_DIR_GENERAL + "\\TEMP\\");

						

						deleteFolder(tempPLM);
						deleteFolder(tempGeneral);
						
						new editComentario((int) target.getValueAt(row, 0), Integer.parseInt(usuario_id), usuario);
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}

				}
			}

		});
		if (scrollPane != null) {
			scrollPane.remove(table);
			scrollPane.add(table);
		} else {
			scrollPane = new JScrollPane(table);
		}

		scrollPane = new JScrollPane(table);
		return scrollPane;
	}

	public void refresh() throws ParseException {
		refreshNormal = true;
		refreshInicio = false;
		refreshFiltrado = false;
		refreshNoGestionadas = false;

		logDB.selectAll();
		logDB.replaceSelection("Actualizando...");
		
		db.connect();
		if (error) {
			db.close();

			return;
		}
		try {
			remove(scrollPane);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		Date date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(datePicker.getJFormattedTextField().getText());
		
//int tempSelectedCategoria = comboBoxCategoria.getSelectedIndex();
//		
//		comboBoxCategoria.removeAllItems();
//
//		comboBoxCategoria.addItem("Todas");
//		
//		for (String categoria : Login.CATEGORIAS) {
//			comboBoxCategoria.addItem(categoria);
//		}
//		
//		comboBoxCategoria.setSelectedIndex(tempSelectedCategoria);

		add(TableFromDatabase(db.mostrarEntradasPorFechaYNegociado(dateFormat.format(date1), Login.usuarioActivo, comboBoxNegociado.getSelectedItem().toString(), comboBoxCategoria.getSelectedItem().toString())),
				BorderLayout.CENTER);
		
		
		
		repaint();
		revalidate();
		db.close();
		
		

		
		timer2.start();
	}

	public void refreshInicio() throws ParseException {
		refreshNormal = false;
		refreshInicio = true;
		refreshFiltrado = false;
		refreshNoGestionadas = false;

		logDB.selectAll();
		logDB.replaceSelection("Actualizando...");
//		if (comboBoxNegociado.getSelectedIndex() == 0) {
//			db.connect();
//			if (error) {
//				return;
//			}
//			remove(scrollPane);
//			Date date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(datePicker.getJFormattedTextField().getText());
////			if (role.equals("Registro")) {
////				add(TableFromDatabase(db.mostrarEntradasInicioRegistroTodos(dateFormat.format(date1),comboBoxCategoria.getSelectedItem().toString())),
////						BorderLayout.CENTER);
////			} else {
////				add(TableFromDatabase(db.mostrarEntradasPendienteVerTodos(Login.usuarioActivo, comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);
////			}
//			
//			add(TableFromDatabase(db.pendienteVer(Login.usuarioActivo, comboBoxNegociado.getSelectedItem().toString(), comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);
//
//			
//			repaint();
//			revalidate();
//			db.close();
//		} else {
//			db.connect();
//			remove(scrollPane);
//			Date date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(datePicker.getJFormattedTextField().getText());
//			if (role.equals("Registro")) {
//				add(TableFromDatabase(db.mostrarEntradasInicioRegistroPorNegociado(dateFormat.format(date1),
//						comboBoxNegociado.getSelectedItem().toString(),comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);
//			} else {
//				add(TableFromDatabase(db.mostrarEntradasPendientesVerPorFechaYNegociado(dateFormat.format(date1),
//						Login.usuarioActivo, comboBoxNegociado.getSelectedItem().toString(),comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);
//			}
//			repaint();
//			revalidate();
//			db.close();
//		}
		
		
		db.connect();
		if (error) {
			return;
		}
		remove(scrollPane);
		Date date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(datePicker.getJFormattedTextField().getText());
//		if (role.equals("Registro")) {
//			add(TableFromDatabase(db.mostrarEntradasInicioRegistroTodos(dateFormat.format(date1),comboBoxCategoria.getSelectedItem().toString())),
//					BorderLayout.CENTER);
//		} else {
//			add(TableFromDatabase(db.mostrarEntradasPendienteVerTodos(Login.usuarioActivo, comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);
//		}
		
		if (Login.usuarioActivo.isJefe) {
			add(TableFromDatabase(db.pendienteVer(Login.usuarioActivo, comboBoxNegociado.getSelectedItem().toString(), comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);

		}else {
			add(TableFromDatabase(db.mostrarEntradasPorFechaYNegociado(dateFormat.format(date1), Login.usuarioActivo, comboBoxNegociado.getSelectedItem().toString(), comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);	
		}
		

		
		repaint();
		revalidate();
		db.close();
		timer2.start();
	}

	public void refreshNoGestionadas() throws ParseException {
		// TODO Auto-generated method stub
		refreshNormal = false;
		refreshInicio = false;
		refreshFiltrado = false;
		refreshNoGestionadas = true;

		logDB.setText("Actualizando...");
		if (comboBoxNegociado.getSelectedIndex() == 0) {
			db.connect();
			if (error) {
				db.close();

				logDB.setText("Error leyendo la base de datos");
				return;
			}
			remove(scrollPane);
			add(TableFromDatabase(db.mostrarEntradasNoGestionadas()), BorderLayout.CENTER);

			repaint();
			revalidate();
			db.close();

		} else {
			db.connect();
			remove(scrollPane);
			Date date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(datePicker.getJFormattedTextField().getText());
			if (role.equals("Jefe1") || role.equals("Registro")) {
				add(TableFromDatabase(db.mostrarEntradasCoronelPorNegociadoPendiente(dateFormat.format(date1),
						comboBoxNegociado.getSelectedItem().toString(), comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);
			} else if (role.equals("Jefes")) {
//				add(TableFromDatabase(db.mostrarEntradasPorNegociadoPendiente(dateFormat.format(date1),
//						comboBoxNegociado.getSelectedItem().toString(), comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);

			} else {
				add(TableFromDatabase(db.mostrarEntradasSinConfidencialPorNegociadoPendiente(dateFormat.format(date1),
						comboBoxNegociado.getSelectedItem().toString(), role, comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);
			}
			table.getRowSorter().toggleSortOrder(0);
			table.getRowSorter().toggleSortOrder(0);

			repaint();
			revalidate();
			db.close();
		}
		updateLog.setText("Actualizar automática desactivada");
		timer2.stop();
	}

	public void refreshFiltrado() throws ParseException {

		refreshNormal = false;
		refreshInicio = false;
		refreshFiltrado = true;
		refreshNoGestionadas = false;
		logDB.setText("Actualizando...");
//		if (comboBoxNegociado.getSelectedIndex() == 0) {
//			db.connect();
//			if (error) {
//				db.close();
//
//				logDB.setText("Error leyendo la base de datos");
//				return;
//			}
//			remove(scrollPane);
//			Date date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(datePicker.getJFormattedTextField().getText());
//			
//			//	public ResultSet pendienteVer(Usuario usuario, String area, String categoria, String tipoBusqueda) {
//
//			add(TableFromDatabase(db.pendienteTramitar(Login.usuarioActivo, comboBoxNegociado.getSelectedItem().toString(), comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);
//
//
////			if (role.equals("Registro")) {
////				add(TableFromDatabase(db.mostrarEntradasPendienteTramitarRegistroTodos(comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);
////			} else {
////				add(TableFromDatabase(db.mostrarEntradasPendienteTramitarTodos(Login.usuarioActivo, comboBoxCategoria.getSelectedItem().toString())),
////						BorderLayout.CENTER);
////			}
//
//			repaint();
//			revalidate();
//			db.close();
//
//		} else {
//			db.connect();
//			remove(scrollPane);
//			Date date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(datePicker.getJFormattedTextField().getText());
//
//			if (role.equals("Registro")) {
//				add(TableFromDatabase(db.mostrarEntradasPendienteTramitarRegistroPorNegociado(
//						comboBoxNegociado.getSelectedItem().toString(), comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);
//			} else {
//				add(TableFromDatabase(db.mostrarEntradasPendienteTramitarPorNegociado(
//						comboBoxNegociado.getSelectedItem().toString(), Login.usuarioActivo, comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);
//			}
//			table.getRowSorter().toggleSortOrder(0);
//			table.getRowSorter().toggleSortOrder(0);
//
//			repaint();
//			revalidate();
//			db.close();
//		}
		
		db.connect();
		if (error) {
			db.close();

			logDB.setText("Error leyendo la base de datos");
			return;
		}
		remove(scrollPane);
		Date date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(datePicker.getJFormattedTextField().getText());
		
		//	public ResultSet pendienteVer(Usuario usuario, String area, String categoria, String tipoBusqueda) {

		add(TableFromDatabase(db.pendienteTramitar(Login.usuarioActivo, comboBoxNegociado.getSelectedItem().toString(), comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);

		table.getRowSorter().toggleSortOrder(0);


		repaint();
		revalidate();
		db.close();
		
		updateLog.setText("Actualizar automática desactivada");
		timer2.stop();
	}

	public void refreshFiltradoFechas(String[] dates) throws ParseException {

		refreshNormal = false;
		refreshInicio = false;
		refreshFiltrado = false;
		refreshFechas = true;
		refreshNoGestionadas = false;
		logDB.setText("Actualizando...");
		if (comboBoxNegociado.getSelectedIndex() == 0) {
			db.connect();
			if (error) {
				db.connect();
				logDB.setText("Error leyendo la base de datos");
				return;
			}
			remove(scrollPane);
			Date date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(datePicker.getJFormattedTextField().getText());
			if (role.equals("Jefe1") || role.equals("Registro")) {
				add(TableFromDatabase(db.mostrarEntradasCoronelPendientePorFecha(dates, comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);
			} else if (role.equals("Jefes")) {
				add(TableFromDatabase(db.mostrarEntradasJefesPendientePorFecha(dates, Integer.parseInt(usuario_id), comboBoxCategoria.getSelectedItem().toString())),
						BorderLayout.CENTER);

			}

//			else {
//				add(TableFromDatabase(db.mostrarEntradasSinConfidencialPendiente(dateFormat.format(date1), role)),
//						BorderLayout.CENTER);
//			}

			repaint();
			revalidate();
			db.close();

		} else {
			db.connect();
			remove(scrollPane);
			Date date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(datePicker.getJFormattedTextField().getText());
			if (role.equals("Jefe1") || role.equals("Registro")) {
				add(TableFromDatabase(db.mostrarEntradasCoronelPorNegociadoPendientePorFecha(dates,
						comboBoxNegociado.getSelectedItem().toString(),comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);
			} else if (role.equals("Jefes")) {
				add(TableFromDatabase(db.mostrarEntradasJefesPorNegociadoPendientePorFechas(dates,
						comboBoxNegociado.getSelectedItem().toString(), comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);

			}

//			else {
//				add(TableFromDatabase(db.mostrarEntradasSinConfidencialPorNegociadoPendiente(dateFormat.format(date1),
//						comboBoxNegociado.getSelectedItem().toString(), role)), BorderLayout.CENTER);
//			}
			repaint();
			revalidate();
			db.close();
		}
		timer2.start();
	}

	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			// some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
	}

	

	public void search() throws ParseException {
		logDB.selectAll();
		logDB.setText("Actualizando...");
		db.connect();
		if (error) {
			logDB.setText("Error leyendo la base de datos");
			return;
		}
		remove(scrollPane);
		
		Date date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(datePicker.getJFormattedTextField().getText());

		add(TableFromDatabase(db.search(dateFormat.format(date1), Login.usuarioActivo, comboBoxNegociado.getSelectedItem().toString(), comboBoxCategoria.getSelectedItem().toString(), searchTextField.getText(),comboBoxTipoBusqueda.getSelectedItem().toString())),
				BorderLayout.CENTER);
		
		
		if (comboBoxTipoBusqueda.getSelectedItem().toString().equals("ASUNTO")) {

//			if (role.equals("Registro")) {
//				add(TableFromDatabase(db.searchRegistro(searchTextField.getText())),
//						BorderLayout.CENTER);
//			} else {
//				add(TableFromDatabase(db.mostrarEntradasPendienteVerV2(dateFormat.format(date1), Login.usuarioActivo)),
//						BorderLayout.CENTER);
//			} 
//			if (comboBoxNegociado.getSelectedIndex() == 0) {
//				if (role.equals("Registro")) {
//					add(TableFromDatabase(db.searchRegistroTodosPorAsunto(searchTextField.getText(), comboBoxCategoria.getSelectedItem().toString())),   // FALTA ESPECIFICAR CATEGORIA
//							
//							BorderLayout.CENTER);
//				} else {
//					add(TableFromDatabase(db.searchJefesPorAsunto(searchTextField.getText(), Login.usuarioActivo, comboBoxCategoria.getSelectedItem().toString())),
//							BorderLayout.CENTER);
//
//				}
//			} else {
//				if (role.equals("Registro")) {
//					add(TableFromDatabase(db.searchRegistroPorNegociadoPorAsunto(searchTextField.getText(),
//							comboBoxNegociado.getSelectedItem().toString(), comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);
//				} else {
//					add(TableFromDatabase(db.searchJefesPorNegociadoPorAsunto(searchTextField.getText(),
//							Login.usuarioActivo, comboBoxNegociado.getSelectedItem().toString(), comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);
//
//				}
//			}	public ResultSet search(String date, Usuario usuario, String area, String categoria, String word, String tipoBusqueda) {
			



			
			
		}
//		if (comboBoxTipoBusqueda.getSelectedItem().toString().equals("N\u00BA ENTRADA")) {
//			System.out.println("NUMERO ENTRADA");
//
//			if (comboBoxNegociado.getSelectedIndex() == 0) {
//				if (role.equals("Registro")) {
//					add(TableFromDatabase(db.searchRegistroTodosPorNumEntrada(searchTextField.getText(), comboBoxCategoria.getSelectedItem().toString())),
//							BorderLayout.CENTER);
//				} else {
//					add(TableFromDatabase(db.searchJefesPorNumEntrada(searchTextField.getText(), Login.usuarioActivo, comboBoxCategoria.getSelectedItem().toString() )),
//							BorderLayout.CENTER);
//
//				}
//			} else {
//				if (role.equals("Registro")) {
//					add(TableFromDatabase(db.searchRegistroPorNegociadoPorNumEntrada(searchTextField.getText(),
//							comboBoxNegociado.getSelectedItem().toString(), comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);
//				} else {
//					add(TableFromDatabase(db.searchJefesPorNegociadoPorNumEntrada(searchTextField.getText(),
//							Login.usuarioActivo, comboBoxNegociado.getSelectedItem().toString(), comboBoxCategoria.getSelectedItem().toString())), BorderLayout.CENTER);
//
//				}
//			}
//		}
		
		
		
		
		repaint();
		revalidate();
		db.close();
		updateLog.setText("Actualizar automática desactivada");
		timer2.stop();
	}

	public void actionPerformed(ActionEvent e) {

		// Handle open button action.
		if (e.getSource() == openButton) {
//			int returnVal = fc.showOpenDialog(GUI.this);
//
//			if (returnVal == JFileChooser.APPROVE_OPTION) {
//				File file = fc.getSelectedFile();
//				// This is where a real application would open the file.
//				log.append("Opening: " + file.getAbsolutePath() + "." + newline);
//				saveFile = file.getAbsolutePath();
//			} else {
//				log.append("Open command cancelled by user." + newline);
//			}
//			log.setCaretPosition(log.getDocument().getLength());

			// Handle save button action.
		} else if (e.getSource() == refresh) {

			try {
				refresh();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getSource() == saveButton) {

//			int returnVal = fc.showSaveDialog(GUI.this);
//			if (returnVal == JFileChooser.APPROVE_OPTION) {
//				File file = fc.getSelectedFile();
//				// This is where a real application would save the file.
//				log.append("Saving: " + file.getAbsolutePath() + "." + newline);
//				saveFile = file.getAbsolutePath();
//			} else {
//				log.append("Save command cancelled by user." + newline);
//			}
//			log.setCaretPosition(log.getDocument().getLength());
		} else if (e.getSource() == saveButton) {

//			int returnVal = fc.showSaveDialog(GUI.this);
//			if (returnVal == JFileChooser.APPROVE_OPTION) {
//				File file = fc.getSelectedFile();
//				// This is where a real application would save the file.
//				log.append("Saving: " + file.getAbsolutePath() + "." + newline);
//				saveFile = file.getAbsolutePath();
//			} else {
//				log.append("Save command cancelled by user." + newline);
//			}
//			log.setCaretPosition(log.getDocument().getLength());
		}

		// Handle generate button
		else if (e.getSource() == nuevaEntrada) {

			nuevaentrada = new nuevaEntrada();
			this.setFocusable(true);
//			DefaultTableModel prueba = (DefaultTableModel) table.getModel();
//	            
////			 prueba.addColumn(nuevaentrada.entrada.getAsunto());
//			 System.out.println("nueva entrada id "  + nuevaentrada.entrada.getId());
//			 prueba.addRow(new Object[]{nuevaentrada.entrada.getId(), nuevaentrada.entrada.getAsunto(), "v3", "v4", true, true, true , true, true, ""});
////	            prueba.addRow(nuevaentrada.entrada);
//	            
//			 
//			table.setModel(prueba);

		}
		// Handle execute button action.
		else if (e.getSource() == execute) {
			// Apriori apriori = new Apriori(saveFile,
			// comboComponentes.getSelectedItem().toString() );
//			log.selectAll();
//			log.replaceSelection("");
			// log.append(apriori.salida.toString()+ newline);
			// log.append("--------------------------------------------------------"+
			// newline);
			// apriori.pw.println("--------------------------------------------------------"+
			// newline);
			// salida.append("--------------------------------------------------------"+
			// newline);
			// for (List<String> lista : apriori.lista) {
			// if (lista.contains(comboComponentes.getSelectedItem().toString()) &&
			// lista.size()>2) {
			// log.append(lista.toString()+ newline);
			// salida.append(lista.toString()+ newline);
			// apriori.pw.println(lista.toString()+ newline);
			//
			// }
			// }
		}

	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = GUI.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	class ColumnColorRenderer extends DefaultTableCellRenderer {
		Color backgroundColor, foregroundColor;

		public ColumnColorRenderer(Color backgroundColor, Color foregroundColor) {
			super();
			this.backgroundColor = backgroundColor;
			this.foregroundColor = foregroundColor;
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			cell.setBackground(backgroundColor);
			cell.setForeground(foregroundColor);
			return cell;
		}
	}

}
