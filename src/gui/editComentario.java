package gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;

import org.apache.commons.lang3.StringUtils;


import models.comentario;
import models.comentarioJefe;
import models.entrada;

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
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import service.AutoCompleteComboBox;
import service.CheckboxListCellRenderer;
import service.CryptoException;
import service.CryptoUtils;
import service.DefaultCheckListModel;
import service.JCheckBoxList;
import service.JCheckList;
import service.db;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.print.attribute.standard.JobSheets;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultEditorKit.CopyAction;
import javax.swing.text.DefaultEditorKit.CutAction;
import javax.swing.text.DefaultEditorKit.PasteAction;

import org.apache.commons.lang3.StringUtils;

import javax.swing.border.CompoundBorder;
import javax.swing.border.BevelBorder;
import javax.swing.UIManager;
import javax.swing.TransferHandler.TransferSupport;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Rectangle;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.JComboBox;
import javax.swing.JComponent;

public class editComentario implements KeyListener {
	private JFrame f = new JFrame("Comentario");
//	private JTextArea jefe1;
//	private JTextArea jefe2;
//	private JTextArea jefe3;
//	private JTextArea jefe4;
//	private JTextArea jefe5;
//	private String jefe1Backup;
//	private String jefe2Backup;
//	private String jefe3Backup;
//	private String jefe4Backup;
//	private String jefe5Backup;
//	private String jefe1Backuphora;
//	private String jefe2Backuphora;
//	private String jefe3Backuphora;
//	private String jefe4Backuphora;
//	private String jefe5Backuphora;
//	private String jefe1Backupdia;
//	private String jefe2Backupdia;
//	private String jefe3Backupdia;
//	private String jefe4Backupdia;
//	private String jefe5Backupdia;

	private JList list = new JList();
	// private JList listSalida = new JList();
	public comentario comentario2;
	public entrada entrada;
	private db db;
	Date date = new Date();
	UtilDateModel model = new UtilDateModel(date);
	DateFormat dateFormat_normal = new SimpleDateFormat("dd/MM/yyyy");
	DateFormat timeFormat = new SimpleDateFormat("HH:mm");
	private DateFormat dateFormat_guiones = new SimpleDateFormat("dd-MM-yyyy");

	JCheckBox chckbxConfidencial;
	private JTextField asunto;
	int idJefe = -1;

	private JCheckBox chckbxUrgente;
	private JTextArea textObservaciones;
	private JScrollPane scrollEntrada;
	private JScrollPane scrollAntecedentes;
	private JScrollPane scrollSalida;

	private JScrollPane scrollPane;
	private JScrollPane scrollPaneAntecedentes;

	private JPanel panel_observaciones;
	private JButton btnImprimir;
	File report;
	JasperReport jasReport;
	private JComboBox<String> comboBoxNegociado;
	private JLabel lblFecha;
	private JCheckBox chckbxTramitado;
	private JButton btnCancelar;
	private boolean cambiosRealizados = false;
	DefaultListModel listModel2;
	DefaultListModel listModelAntecedentes;
	DefaultListModel<JCheckBox> listModelSalida;

	@FunctionalInterface
	public interface SimpleDocumentListener extends DocumentListener {
		void update(DocumentEvent e);

		@Override
		default void insertUpdate(DocumentEvent e) {
			update(e);
		}

		@Override
		default void removeUpdate(DocumentEvent e) {
			update(e);
		}

		@Override
		default void changedUpdate(DocumentEvent e) {
			update(e);
		}
	}

	private String stripExtension(String str) {
		// Handle null case specially.

		if (str == null)
			return null;

		// Get position of last '.'.

		int pos = str.lastIndexOf(".");

		// If there wasn't any '.' just return the string as is.

		if (pos == -1)
			return str;

		// Otherwise return the string, up to the dot.

		return str.substring(0, pos);
	}

	private String getExtension(String str) {
		// Handle null case specially.
		if (str == null)
			return null;

		// Get position of last '.'.

		int pos = str.lastIndexOf(".");

		// If there wasn't any '.' just return the string as is.
		if (pos == -1)
			return str;
		// Otherwise return the string, up to the dot.

		return str.substring(pos, str.length());
	}

	
	private JLabel labelDestinos;
	private JTextArea destinosField;
	private JPanel panel_2;
	private JPanel panel_3;
	private JButton btnAgregarDestinatario;
	private JTextField tramitadoPor;
	private ArrayList<String> destinos;
	private JPanel panel_4;
	private JComboBox comboBoxCanalEntrada;
	private JPanel panel_6;
	private JTextField textFieldnumEntrada;
	// private JCheckBox soloJefe1;
//	private boolean enableJefe;

	private ArrayList<String> destinosJefes;

	private JTextArea destinatariosJefes;

	private Vector dataEntrada;
	private Vector dataSalida;
	private Vector dataAntecedentes;

	private int valueScroll;

	private DefaultTableModel tablemodelSalida;
	private DefaultTableModel tablemodelAntecedentes;


	private JTable tableSalida;
	private JTable tableAntecedentes;

	private JScrollPane scrollPaneEntrada;

	private DefaultTableModel tablemodelEntrada;

	private JTable tableEntrada;

	private ArrayList<String> categorias;

	private JTextArea textAreaCategorias;
	private AutoCompleteComboBox comboBoxCategorias;

	private comentarioJefe comentarioVacio;


	private static void copyFile(File source, File dest) throws IOException {
		try {
			CryptoUtils.encrypt("PdSgVkYp3s6v9y$B&E)H@MbQeThWmZq4", source, dest);
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void copyFileOverwrite(File source, File dest) throws IOException {
		try {
			CryptoUtils.encrypt("PdSgVkYp3s6v9y$B&E)H@MbQeThWmZq4", source, dest);
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public editComentario(int id, int idJefe, String usuario) {

		report = new File(Login.BASE_DIR + "informe.jasper");

		try {
			jasReport = (JasperReport) JRLoader.loadObject(report);
		} catch (JRException e2) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, e2);

			e2.printStackTrace();
		}
		URL url = GUI.class.getResource("/entrada.png");
		ImageIcon icon = new ImageIcon(url);
		f.setIconImage(icon.getImage());
		Toolkit toolkit = Toolkit.getDefaultToolkit();

		Dimension size = toolkit.getScreenSize();

		f.setLocation(size.width / 4, 20);
		this.idJefe = idJefe;

		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		f.addWindowListener(new java.awt.event.WindowAdapter() {

			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				// MainPage m = new MainPage();
				// m.setVisible(true);
				comprobarCambios();
				if (!cambiosRealizados) {
					e.getWindow().dispose();
				} else {
					String[] options = { "Si", "No" };
					int opcionSeleccionada = -1;
					opcionSeleccionada = JOptionPane.showOptionDialog(null, "¿Desea guardar los cambios?", "",
							JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
					if (opcionSeleccionada == 0) {
						guardarComentario();
					} else {

						
					}
					e.getWindow().dispose();
				}
			}
		});

		f.setFocusable(true);

		f.addKeyListener(this);
		db = new db();

		

		db.connect();
		entrada = db.mostrarEntrada(id);
		db.close();

		db.connect();
		entrada.comentario = db.mostrarComentario(id);
		db.close();

		db.connect();
		scrollEntrada = TableFromDatabaseEntrada(db.getEntradaFilesById(id));
		db.close();
		scrollEntrada.setTransferHandler(new FileListTransferHandlerEntrada(dataEntrada));
		scrollEntrada
		.setBorder(new TitledBorder(null, "ENTRADA", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		
		
		db.connect();
		scrollAntecedentes = TableFromDatabaseAntecedentes(db.getAntecedentesById(id));
		db.close();
		scrollAntecedentes.setTransferHandler(new FileListTransferHandlerAntecedentes(dataAntecedentes));
		

		db.connect();
		scrollSalida = TableFromDatabaseSalida(db.getSalidaByIdBIS(id));
		db.close();
		scrollSalida.setTransferHandler(new FileListTransferHandlerSalida(dataSalida));

//		db.connect();
//		listModel2 = db.mostrarFiles(id);
//		if (listModel2.capacity() > 0) {
//			list.setModel(listModel2);
//		}
//
//		db.close();

		f.setSize(988, 1005);
		f.getContentPane().setLayout(null);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(0, 5, 966, 608);

		// panel_1.setPreferredSize(new Dimension(650, 500));
		// panel_1.setSize(new Dimension(800, 1000));
		f.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		// panel_1.setLayout(null);

		panel_2 = new JPanel();
		panel_2.setBounds(21, 5, 935, 30);
		panel_2.setPreferredSize(new Dimension(650, 30));
		panel_1.add(panel_2);
		panel_2.setLayout(null);

		// list.setBounds(5, 5, 0, 0);

		JLabel lblAsunto = new JLabel("Asunto: ");
		lblAsunto.setBounds(5, 8, 80, 14);
		lblAsunto.setPreferredSize(new Dimension(80, 14));
		panel_2.add(lblAsunto);

		asunto = new JTextField();
		asunto.setEditable(false);
		asunto.setBounds(90, 5, 835, 20);
		asunto.setHorizontalAlignment(SwingConstants.LEFT);
		panel_2.add(asunto);
		asunto.setColumns(50);
		asunto.setText(entrada.getAsunto());
		textObservaciones = new JTextArea(3, 90);
		textObservaciones.setMargin(new Insets(1, 1, 1, 1));
		textObservaciones.setPreferredSize(new Dimension(950, 58));
		textObservaciones.setLineWrap(true);
		textObservaciones.addKeyListener(this);
		textObservaciones.setText(entrada.getObservaciones());

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

		textObservaciones.setComponentPopupMenu(menu);

		comboBoxNegociado = new JComboBox<>();
		comboBoxNegociado
				.setBorder(new TitledBorder(null, "Areas: ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		comboBoxNegociado.setBounds(10, 1, 163, 52);
		btnAgregarDestinatario = new JButton("Agregar area");
		btnAgregarDestinatario.setBounds(183, 1, 131, 52);

		for (String str : Login.NEGOCIADOS) {
			comboBoxNegociado.addItem(str);
		}

		btnAgregarDestinatario.setEnabled(Login.usuarioActivo.permiso ? true : false);

		comboBoxNegociado.setEnabled(Login.usuarioActivo.permiso ? true : false);

		panel_3 = new JPanel();
		panel_3.setBounds(21, 40, 935, 102);
		panel_3.setPreferredSize(new Dimension(650, 30));
		panel_1.add(panel_3);
		panel_3.setLayout(null);

		labelDestinos = new JLabel("Areas: ");
		labelDestinos.setBounds(5, 9, 80, 14);
		panel_3.add(labelDestinos);
		labelDestinos.setPreferredSize(new Dimension(80, 14));

		destinosField = new JTextArea();
		destinosField.setBorder(new LineBorder(new Color(0, 0, 0)));
		destinosField.setBounds(68, 4, 857, 22);
		panel_3.add(destinosField);
		destinosField.setText((String) null);
		destinosField.setEditable(false);
		destinosField.setColumns(50);

		JLabel label = new JLabel("Jefes: ");
		label.setBounds(5, 43, 80, 14);
		label.setPreferredSize(new Dimension(80, 14));
		panel_3.add(label);

		destinatariosJefes = new JTextArea();
		destinatariosJefes.setBorder(new LineBorder(new Color(0, 0, 0)));
		destinatariosJefes.setBounds(68, 38, 857, 22);
		destinatariosJefes.setText((String) null);
		destinatariosJefes.setEditable(false);
		destinatariosJefes.setColumns(50);

		JComboBox<String> comboBoxJefes = new JComboBox<String>();

		if (Login.usuarioActivo.permiso) {

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
						// arrayList.remove(destinatariosField.getSelectedText());

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
						} else {
							selectionStart = 0;
						}

						if (destinatariosJefes.getSelectionEnd() != destinatariosJefes.getText().length()) {
							for (int i = destinatariosJefes.getSelectionEnd(); i < destinatariosJefes.getText()
									.length(); i++) {
								System.out.println("posicion " + i + destinatariosJefes.getText().charAt(i));
								if (new Character(destinatariosJefes.getText().charAt(i)).equals(';')) {
									selectionEnd = i;
									break;
								} else if (i == destinatariosJefes.getText().length() - 1) {
									selectionEnd = i + 1;
								}
							}
						} else {
							selectionEnd = destinatariosJefes.getText().length() + 2;
						}

						System.out.println("Start " + selectionStart);
						System.out.println("End " + selectionEnd);
						if (selectionStart == 0) {
							destinatariosJefes.select(selectionStart, selectionEnd + 2);
						} else {
							destinatariosJefes.select(selectionStart, selectionEnd);
						}
						String destinatariosJefesTemp = destinatariosJefes.getSelectedText();

						destinatariosJefesTemp.replace(";", "");

						comboBoxJefes.addItem(destinatariosJefesTemp.replace(";", "").trim());

						destinatariosJefes.replaceSelection("");

					}

				}
			});
		}

		panel_3.add(destinatariosJefes);
		
		textAreaCategorias = new JTextArea();
		textAreaCategorias.setBorder(new LineBorder(new Color(0, 0, 0)));
		textAreaCategorias.setText((String) null);
		textAreaCategorias.setEditable(false);
		textAreaCategorias.setColumns(50);
		textAreaCategorias.setBounds(68, 69, 857, 22);
		panel_3.add(textAreaCategorias);
		
		JLabel lblCategorias = new JLabel("Categorias: ");
		lblCategorias.setPreferredSize(new Dimension(80, 14));
		lblCategorias.setBounds(5, 74, 80, 14);
		panel_3.add(lblCategorias);

		if (Login.usuarioActivo.permiso) {
			destinosField.addMouseListener(new MouseListener() {

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
						// arrayList.remove(destinatariosField.getSelectedText());

						if (destinosField.getSelectedText().equals("Todos")) {
							comboBoxNegociado.addItem("Todos");

							for (String str : Login.NEGOCIADOS) {
								comboBoxNegociado.addItem(str);
							}

							destinosField.selectAll();
							destinosField.replaceSelection("");
							return;
						}

						if (!destinosField.getText().contains(";")) {
							destinosField.selectAll();
							destinosField.replaceSelection("");
							comboBoxNegociado.removeAllItems();
							comboBoxNegociado.addItem("Todos");

							for (String str : Login.NEGOCIADOS) {
								comboBoxNegociado.addItem(str);
							}

							return;
						}

						comboBoxNegociado.addItem(destinosField.getSelectedText());
						if (destinosField.getSelectionStart() == 0) {
							destinosField.select(destinosField.getSelectionStart(),
									destinosField.getSelectionEnd() + 1);
							destinosField.replaceSelection("");
						} else {
							destinosField.select(destinosField.getSelectionStart() - 2,
									destinosField.getSelectionEnd());
							destinosField.replaceSelection("");
						}
					}

				}
			});
		}

		db.connect();
		destinos = db.getDestinos(entrada.getId());
		db.close();

		db.connect();
		destinosJefes = db.getDestinosJefes(entrada.getId());
		db.close();
		
		
		
	
		destinos.forEach((temp) -> {
			System.out.println("destinos " + temp);
			if (destinosField.getText().equals("")) {
				destinosField.append(temp);
				comboBoxNegociado.removeItem(temp);

			} else {
				destinosField.append("; " + temp);
				comboBoxNegociado.removeItem(temp);

			}
		});

		try {
			destinosJefes.forEach((temp) -> {
				if (destinatariosJefes.getText().equals("")) {
					destinatariosJefes.append(temp);
					comboBoxJefes.removeItem(temp);

				} else {
					destinatariosJefes.append("; " + temp);
					comboBoxJefes.removeItem(temp);

				}
			});
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		

		comboBoxNegociado.setEnabled(Login.usuarioActivo.permiso ? true : false);
		comboBoxJefes.setEnabled(Login.usuarioActivo.permiso ? true : false);

//		else {
//			db.connect();
//			chckbxTramitado.setEnabled(destinosField.getText().split(";")[0].equals(db.getRole(usuario)));
//			db.close();
//		}

		btnAgregarDestinatario.setEnabled(Login.usuarioActivo.permiso ? true : false);

		panel_4 = new JPanel();
		panel_4.setBounds(21, 144, 935, 53);
		panel_4.setPreferredSize(new Dimension(650, 40));
		panel_1.add(panel_4);
		panel_4.setLayout(null);

		panel_4.add(comboBoxNegociado);

		comboBoxNegociado.setPreferredSize(new Dimension(120, 20));

		comboBoxNegociado.setSelectedItem(entrada.getArea());

		panel_4.add(btnAgregarDestinatario);

		comboBoxJefes.setEnabled(false);
		comboBoxJefes.setEnabled(Login.usuarioActivo.permiso ? true : false);

		comboBoxJefes.setPreferredSize(new Dimension(120, 20));
		comboBoxJefes.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Jefes",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		comboBoxJefes.setBounds(324, 1, 163, 52);
		panel_4.add(comboBoxJefes);

		comboBoxJefes.addItem("Todos");

		for (Entry<Integer, String> entry : Login.CARGOS.entrySet()) {
			comboBoxJefes.addItem(entry.getValue());
		}
		

		JButton btnAgregarJefe = new JButton("Agregar jefe");

		btnAgregarJefe.addActionListener(new ActionListener() {
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

				} else {
					destinatariosJefes.append("; " + comboBoxJefes.getSelectedItem().toString());
					comboBoxJefes.removeItem(comboBoxJefes.getSelectedItem());
					comboBoxJefes.removeItem("Todos");
				}
				comboBoxJefes.setSelectedIndex(-1);
			}
		});
		btnAgregarJefe.setEnabled(Login.usuarioActivo.permiso ? true : false);

		btnAgregarJefe.setBounds(497, 1, 131, 52);
		panel_4.add(btnAgregarJefe);
		
		JButton btnAgregarCategoria = new JButton("Agregar categoria");
		btnAgregarCategoria.setBounds(804, 1, 131, 52);
		
		
		panel_4.add(btnAgregarCategoria);
		
		comboBoxCategorias = new AutoCompleteComboBox(new String[] {""});
		comboBoxCategorias.setEditable(true);
		comboBoxCategorias.setPreferredSize(new Dimension(120, 20));
		comboBoxCategorias.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Categorias", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		comboBoxCategorias.setBounds(631, 1, 163, 52);
		
		for (String categoria : Login.CATEGORIAS) {
			comboBoxCategorias.addItem(categoria);
		}
		
		
		db.connect();
		categorias = db.getCategoriasEntrada(entrada.getId());
		db.close();

		
		
		panel_4.add(comboBoxCategorias);
		
		categorias.forEach((temp) -> {
			if (textAreaCategorias.getText().equals("")) {
				textAreaCategorias.append(temp);
				comboBoxCategorias.removeItem(temp);

			} else {
				textAreaCategorias.append("; " + temp);
				comboBoxCategorias.removeItem(temp);

			}
		});
		
		btnAgregarCategoria.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (textAreaCategorias.getText().equals("")) {
					textAreaCategorias.append(comboBoxCategorias.getSelectedItem().toString());
					comboBoxCategorias.removeItem(comboBoxCategorias.getSelectedItem());

				} else {
					textAreaCategorias.append("; " + comboBoxCategorias.getSelectedItem().toString());
					comboBoxCategorias.removeItem(comboBoxCategorias.getSelectedItem());
				}
				comboBoxCategorias.getEditor().setItem("");
			}
		});

		comboBoxCategorias.getEditor().getEditorComponent().addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				
				
				
				
				
			}
		});
		
		
		textAreaCategorias.addMouseListener(new MouseListener() {

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
					// arrayList.remove(destinatariosField.getSelectedText());

					

					if (!textAreaCategorias.getText().contains(";")) {
						textAreaCategorias.selectAll();
						textAreaCategorias.replaceSelection("");
						comboBoxCategorias.removeAllItems();
						db.connect();
						categorias = db.mostrarCategorias();
						db.close();
						for (String categoria : categorias) {
							comboBoxCategorias.addItem(categoria);
						}
						
						return;
					}

					int selectionStart = -1;
					int selectionEnd = -1;
					int indexOfcoma = 9999;

					if (textAreaCategorias.getSelectionStart() != 0) {
						for (int i = textAreaCategorias.getSelectionStart(); i > 0; i--) {
							System.out.println("posicion " + i + textAreaCategorias.getText().charAt(i));

							if (new Character(textAreaCategorias.getText().charAt(i)).equals(';')) {
								selectionStart = i;
								break;
							}
						}
					} else {
						selectionStart = 0;
					}

					if (textAreaCategorias.getSelectionEnd() != textAreaCategorias.getText().length()) {
						for (int i = textAreaCategorias.getSelectionEnd(); i < textAreaCategorias.getText()
								.length(); i++) {
							System.out.println("posicion " + i + textAreaCategorias.getText().charAt(i));
							if (new Character(textAreaCategorias.getText().charAt(i)).equals(';')) {
								selectionEnd = i;
								break;
							} else if (i == textAreaCategorias.getText().length() - 1) {
								selectionEnd = i + 1;
							}
						}
					} else {
						selectionEnd = textAreaCategorias.getText().length() + 2;
					}

					System.out.println("Start " + selectionStart);
					System.out.println("End " + selectionEnd);
					if (selectionStart == 0) {
						textAreaCategorias.select(selectionStart, selectionEnd + 2);
					} else {
						textAreaCategorias.select(selectionStart, selectionEnd);
					}
					String categoriasTemp = textAreaCategorias.getSelectedText();

					categoriasTemp.replace(";", "");

					comboBoxCategorias.addItem(categoriasTemp.replace(";", "").trim());

					textAreaCategorias.replaceSelection("");

				}

			}
		});
		
		comboBoxCategorias.getEditor().getEditorComponent().addKeyListener(new KeyListener() {
			
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
				if (arg0.getKeyCode() == 10) {
					if (comboBoxCategorias.getEditor().getItem().toString().length() == 0) {
						JOptionPane.showMessageDialog(null, "Debe introducir un nombre de categoría");
						return;
					}
					String[] options = { "Si", "No" };

					int opcionSeleccionada = -1;

					opcionSeleccionada = JOptionPane.showOptionDialog(null,
							"¿Desea crear la categoría "+ comboBoxCategorias.getEditor().getItem().toString() + "?", "", JOptionPane.DEFAULT_OPTION,
							JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
					if (opcionSeleccionada == 1) {
						return;
					}
					
					GUI.comboBoxCategoria.addItem(StringUtils.stripAccents(comboBoxCategorias.getEditor().getItem().toString()));
					GUI.comboBoxCategoria.repaint();
					
					db.connect();
					boolean rs = false;
					
					if (comboBoxCategorias.getEditor().getItem().toString().length() > 3) {
						rs = db.CheckIfExistCategoria(StringUtils.stripAccents(comboBoxCategorias.getEditor().getItem().toString().substring(0, 4)));
					}else {
						rs = db.CheckIfExistCategoria(StringUtils.stripAccents(comboBoxCategorias.getEditor().getItem().toString()));
					}
					
					db.close();
							if (rs) {

							opcionSeleccionada = -1;

							opcionSeleccionada = JOptionPane.showOptionDialog(null,
									"Error esta categoria ya existe, comprueba el nombre. Si lo ha comprobado y está correctamente, pulse SI", "", JOptionPane.DEFAULT_OPTION,
									JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
							// int index = listDrag.locationToIndex(e.getPoint());

							if (opcionSeleccionada == 0) {
								db.connect();
								System.out.println("Guardando " +StringUtils.stripAccents(comboBoxCategorias.getEditor().getItem().toString()));

								if (db.addCategoria(StringUtils.stripAccents(comboBoxCategorias.getEditor().getItem().toString()))) {
									JOptionPane.showMessageDialog(null, "Error esta categoria ya existe");

								}else {
									comboBoxCategorias.addItem(StringUtils.stripAccents(comboBoxCategorias.getEditor().getItem().toString()));
									Login.CATEGORIAS.add(StringUtils.stripAccents(comboBoxCategorias.getEditor().getItem().toString()));
									
									comboBoxCategorias.setSelectedItem(comboBoxCategorias.getEditor().getItem().toString());
									comboBoxCategorias.setSelectedIndex(comboBoxCategorias.getItemCount()-1);
								}
								db.close();
							}
							if (opcionSeleccionada == 1) {
								return;
							}
						}else {
							db.connect();
							System.out.println("Guardando " +StringUtils.stripAccents(comboBoxCategorias.getEditor().getItem().toString()));
							if (db.addCategoria(StringUtils.stripAccents(comboBoxCategorias.getEditor().getItem().toString()))) {
								JOptionPane.showMessageDialog(null, "Error esta categoria ya existe");
							}else {
								comboBoxCategorias.addItem(StringUtils.stripAccents(comboBoxCategorias.getEditor().getItem().toString()));
								Login.CATEGORIAS.add(StringUtils.stripAccents(comboBoxCategorias.getEditor().getItem().toString()));
								

								comboBoxCategorias.setSelectedItem(comboBoxCategorias.getEditor().getItem().toString());
								comboBoxCategorias.setSelectedIndex(comboBoxCategorias.getItemCount()-1);

							}
							db.close();
						}
						
				}
			}
		});
		
		
		btnAgregarDestinatario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
//				if (comboBoxNegociado.getSelectedItem().toString().equals("Todos")) {
//					destinosField.append(comboBoxNegociado.getSelectedItem().toString());
//					comboBoxNegociado.removeAllItems();
//					return;
//				}
				
				if (comboBoxNegociado.getSelectedItem().toString().equals("Todos")) {	
					for (String str : Login.NEGOCIADOS) {
						if (destinosField.getText().equals("")) {
							destinosField.append(str);
							comboBoxNegociado.removeItem(str);
							comboBoxNegociado.removeItem("Todos");
						}else {
							destinosField.append("; " + str);
							comboBoxNegociado.removeItem(str);
							comboBoxNegociado.removeItem("Todos");
						}
					}
					comboBoxNegociado.removeAllItems();
					return;
				}
				
				
//				if (comboBoxNegociado.getSelectedItem().toString().equals("Otro") && destinosField.getText().length() == 0) {
//					JOptionPane.showMessageDialog(null, "La lista de destinatarios no puede empezar por Otro, debe empezar por un destinatario.");
//					return;
//				}

//				if (comboBoxNegociado.getSelectedItem().toString().equals("Otro")
//						&& destinosField.getText().length() == 0) {
//					JOptionPane.showMessageDialog(null,
//							"La lista de destinatarios no puede empezar por Otro, debe empezar por un destinatario.");
//					return;
//				}

//				db.connect();
//				if (db.isNotGestionado(comboBoxNegociado.getSelectedItem().toString())) {
//					int opcionNegociado = -1;
//					String[] optionsNegociado = { "Si, agregar de todos modos", "Cancelar" };
//
//					opcionNegociado = JOptionPane.showOptionDialog(null,
//							"El destinatario " + comboBoxNegociado.getSelectedItem().toString()
//									+ " es un Negociado no gestionado, por lo tanto no tiene acceso a esta Aplicación",
//							"Click en una opcion", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
//							optionsNegociado, optionsNegociado[0]);
//					db.close();
//					if (opcionNegociado == 1) {
//						return;
//					}
//				} else {
//					db.close();
//
//				}
//				db.close();

				if (destinosField.getText().equals("")) {
					destinosField.append(comboBoxNegociado.getSelectedItem().toString());
					comboBoxNegociado.removeItem(comboBoxNegociado.getSelectedItem());
					comboBoxNegociado.removeItem("Todos");
				} else {
					destinosField.append("; " + comboBoxNegociado.getSelectedItem().toString());
					comboBoxNegociado.removeItem(comboBoxNegociado.getSelectedItem());
					comboBoxNegociado.removeItem("Todos");
				}
				comboBoxNegociado.setSelectedIndex(-1);
			}
		});

		comboBoxCanalEntrada = new JComboBox();

		if (Login.CANALES.size() > 0) {
			for (String string : Login.CANALES) {
				comboBoxCanalEntrada.addItem(string);
			}
		}

		panel_6 = new JPanel();
		panel_6.setBounds(21, 200, 935, 53);
		panel_1.add(panel_6);
		panel_6.setLayout(null);

		comboBoxCanalEntrada.setBorder(
				new TitledBorder(null, "Canal de entrada", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		comboBoxCanalEntrada.setBounds(10, 0, 219, 51);
		comboBoxCanalEntrada.setEnabled(Login.usuarioActivo.permiso ? true : false);

		comboBoxCanalEntrada.setSelectedItem(entrada.getCanalEntrada());
		panel_6.add(comboBoxCanalEntrada);
		comboBoxCanalEntrada.setPreferredSize(new Dimension(100, 20));

		textFieldnumEntrada = new JTextField();
		textFieldnumEntrada.setBounds(239, 7, 355, 44);
		panel_6.add(textFieldnumEntrada);
		textFieldnumEntrada.setPreferredSize(new Dimension(100, 40));
		textFieldnumEntrada.setMinimumSize(new Dimension(50, 20));
		textFieldnumEntrada.setColumns(20);
		textFieldnumEntrada.setBorder(
				new TitledBorder(null, "N\u00BA Entrada", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		textFieldnumEntrada.setText(entrada.getNumEntrada());
		textFieldnumEntrada.setEnabled(Login.usuarioActivo.permiso);

//		scrollEntrada = new JScrollPane(list);
		scrollEntrada.setBounds(21, 260, 935, 90);
		scrollEntrada.setPreferredSize(new Dimension(600, 90));
		panel_1.add(scrollEntrada);

		if (Login.usuarioActivo.permiso) {

			list.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_DELETE) {
						int opcionSeleccionada = -1;
						String[] options = { "Si", "No" };

						opcionSeleccionada = JOptionPane.showOptionDialog(null, "¿Desea eliminar esta entrada?", "",
								JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
						// int index = listDrag.locationToIndex(e.getPoint());

						if (opcionSeleccionada == 1) {
							return;
						}

						int index = list.getSelectedIndex();

						db.connect();
						db.eliminarFile(listModel2.getElementAt(index).toString());
						db.close();
						listModel2.removeElementAt(index);
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

		}


		scrollAntecedentes.setBounds(21, 351, 935, 90);
		scrollAntecedentes.setPreferredSize(new Dimension(600, 90));
		panel_1.add(scrollAntecedentes);

		scrollAntecedentes
				.setBorder(new TitledBorder(null, "Antecedentes", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		

		scrollSalida.setBounds(21, 442, 935, 112);
		scrollSalida.setPreferredSize(new Dimension(600, 90));
		scrollSalida.setBorder(new TitledBorder(null, "Salida", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.add(scrollSalida);

		
		panel_observaciones = new JPanel();
		panel_observaciones.setBounds(0, 613, 966, 91);
		panel_observaciones.setLayout(null);

		textObservaciones
				.setBorder(new TitledBorder(null, "Observaciones", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		JScrollPane scroll = new JScrollPane(textObservaciones);
		scroll.setBounds(23, 5, 933, 81);

		scroll.setPreferredSize(new Dimension(600, 81));
		panel_observaciones.add(scroll);
		f.getContentPane().add(panel_observaciones);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 715, 956, 163);
		tabbedPane.setPreferredSize(new Dimension(600, 150));

		tabbedPane.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentResized(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				Dimension dim = f.getSize();
				System.out.println("componentResized");
				f.setSize((int) dim.getWidth(), (int) dim.getHeight() + 10);
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("componentMoved");

			}

			@Override
			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		f.getContentPane().add(tabbedPane);

		// panel button

		JPanel panel_button = new JPanel();
		panel_button.setLocation(0, 889);
		panel_button.setPreferredSize(new Dimension(550, 40));
		panel_button.setSize(new Dimension(966, 67));
		f.getContentPane().add(panel_button);
		chckbxConfidencial = new JCheckBox("Confidencial");
		chckbxConfidencial.setBounds(478, 26, 120, 23);
		chckbxConfidencial.setSelected(entrada.isConfidencial());
//		chckbxConfidencial.setEnabled(Login.usuarioActivo.getRole().equals("Jefe1") || Login.usuarioActivo.getRole().equals("Jefes")|| Login.usuarioActivo.getRole().equals("Registro") ? true : false);
		chckbxConfidencial.setEnabled(Login.usuarioActivo.getRole().equals("Jefe1") || Login.usuarioActivo.getRole().equals("Jefes")|| Login.usuarioActivo.permiso ? true : false);
		// chckbxConfidencial.setEnabled((idJefe == -1) ? false : true);

		chckbxUrgente = new JCheckBox("Urgente");
		chckbxUrgente.setBounds(381, 26, 99, 23);
		chckbxUrgente.setEnabled(
				Login.usuarioActivo.getRole().equals("Jefe1") || Login.usuarioActivo.getRole().equals("Jefes")
						|| Login.usuarioActivo.permiso ? true : false);
		// chckbxUrgente.setEnabled((idJefe == -1) ? false : true);

		chckbxUrgente.setSelected(entrada.isUrgente());
		JButton button = new JButton("Guardar");
		button.setBounds(654, 26, 94, 23);

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				// falta por implementar que usuario es el que está actualizando la informacion
				// ademas que avise si ha escrito algo y el check visto sigue sin estar en true

				guardarComentario();
			}
		});
		panel_button.setLayout(null);
		// lblPor.setVisible(entrada.isTramitado());
		tramitadoPor = new JTextField();
		tramitadoPor.setBounds(9, 11, 241, 45);
		panel_button.add(tramitadoPor);
		tramitadoPor
				.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Asignado a: ", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		tramitadoPor.setText(entrada.getTramitadoPor());
		// tramitadoPor.setVisible(entrada.isTramitado());
		tramitadoPor.setVisible(true);
		tramitadoPor.setColumns(15);
		chckbxTramitado = new JCheckBox("Tramitado");
		chckbxTramitado.setBounds(259, 26, 120, 23);
		panel_button.add(chckbxTramitado);
		chckbxTramitado.setEnabled(true);

		chckbxTramitado.setPreferredSize(new Dimension(120, 23));
		chckbxTramitado.setSelected(entrada.isTramitado());

		// soloJefe1 = new JCheckBox("Solo Jefe");
		// soloJefe1.setBounds(381, 26, 69, 23);

//		soloJefe1.setSelected(entrada.isSoloCoronel());
//		soloJefe1.setEnabled(
//				Login.usuarioActivo.getRole().equals("Jefe1") || Login.usuarioActivo.getRole().equals("Registro") ? true
//						: false);

		GridBagConstraints gbc_soloJefe1 = new GridBagConstraints();
		gbc_soloJefe1.insets = new Insets(0, 0, 5, 0);
		gbc_soloJefe1.anchor = GridBagConstraints.NORTHWEST;
		gbc_soloJefe1.gridwidth = 3;
		gbc_soloJefe1.gridx = 0;
		gbc_soloJefe1.gridy = 2;
		// panel_button.add(soloJefe1);

		panel_button.add(chckbxUrgente);

		panel_button.add(chckbxConfidencial);
		panel_button.add(button);
		JPanel loading = loadingPanel();

		btnImprimir = new JButton("Imprimir");
		btnImprimir.setBounds(758, 26, 94, 23);
		panel_button.add(btnImprimir);
		btnImprimir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Connection conn = null;
				panel_button.add(loading);
				f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				f.repaint();
				f.revalidate();
				new Thread(new Runnable() {
					boolean done = false;

					@Override
					public void run() {
						// time consuming algorithm.

						try {
							JasperReport jasperReport;
							try {
//									InputStream jasperStream = getClass().getResourceAsStream("report3.jasper");
//									JasperReport report = (JasperReport) JRLoader.loadObject(jasperStream);

//									jasperReport = JasperCompileManager.compileReport("simple.jrxml");

								HashMap para = new HashMap();
								para.put("entradaId", entrada.getId());
//								para.put("asunto", entrada.getAsunto());
//								para.put("area", entrada.getArea());
//								para.put("fecha", entrada.getFecha());
//								para.put("confidencial", entrada.isConfidencial()? 1 : 0);
//								para.put("urgente", entrada.isUrgente()? 1 : 0);
//								para.put("tramitado", entrada.isTramitado()? 1 : 0);
//								para.put("observaciones", entrada.getObservaciones());

								para.put("usuario", Login.usuarioActivo.getNombre_usuario());
								StringBuilder comentario = new StringBuilder();

								try {
									for (comentarioJefe comentarioElement : entrada.comentario.comentarios) {
										comentario.append("------------------------------------------------------------------------------------------------------------------------");
										comentario.append("\n");

										comentario.append("Negociado " + comentarioElement.getNombreJefe() + " - Usuario " + comentarioElement.getUsuarioNombre());
										comentario.append("\n");
										comentario.append("------------------------------------------------------------------------------------------------------------------------");
										comentario.append("\n");
										comentario.append("Visto: "+ (comentarioElement.getVisto() == 0 ? "No" : "Si") +" - Fecha:  " + comentarioElement.getFecha() + " - Hora: " + comentarioElement.getHora());
										comentario.append("\n");
										comentario.append("------------------------------------------------------------------------------------------------------------------------");

										comentario.append("\n");


										comentario.append(comentarioElement.getComentario());
										comentario.append("\n");
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								System.out.println("size " + comentario.length());
								System.out.println(comentario.toString());
								
								if (comentario.length() > 3150) {
									System.out.println("grande");
									para.put("comentarios", comentario.toString().substring(0, 3150));
									para.put("comentario2", comentario.toString().substring(3150, comentario.length()));

								}else {
									System.out.println("pequeño");

									para.put("comentarios", comentario.toString());
									para.put("comentario2", "1");

								}
								
								
								db.connect();
								JasperPrint jasperPrint = JasperFillManager.fillReport(jasReport, para, db.getConnection());
								db.close();
//									JasperPrintManager.printReport( jasperPrint, true);

								JasperViewer.viewReport(jasperPrint, false);

//								    JasperExportManager.exportReportToPdfFile(jasperPrint, "sample.pdf");
							} catch (JRException e) {
								// TODO Auto-generated catch block
								f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

								JOptionPane.showMessageDialog(null, e);

								e.printStackTrace();
							}
						} catch (Exception e) {
							f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							e.printStackTrace();

						} finally {
							done = true;
						}

						if (done) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									panel_button.remove(loading);
									panel_button.repaint();
									f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								}
							});
						}
					}

				}).start();

			}
		});

		btnCancelar = new JButton("Cancelar");
		btnCancelar.setBounds(862, 26, 94, 23);
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				f.dispose();
			}
		});
		panel_button.add(btnCancelar);

//		JPanel panel3 = new JPanel();
//		tabbedPane.addTab("Jefe 3", null, panel3, null);
//		GridBagLayout gbl_panel3 = new GridBagLayout();
//		gbl_panel3.columnWidths = new int[] { 59, 471, 0 };
//		gbl_panel3.rowHeights = new int[] { 23, 72, 0 };
//		gbl_panel3.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
//		gbl_panel3.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
//		panel3.setLayout(gbl_panel3);
//
//		JCheckBox jefe3visto = new JCheckBox("Visto");
//		jefe3visto.setSelected(entrada.isJefe3());
//
//		GridBagConstraints gbc_jefe3visto = new GridBagConstraints();
//		gbc_jefe3visto.anchor = GridBagConstraints.NORTHWEST;
//		gbc_jefe3visto.insets = new Insets(0, 0, 5, 0);
//		gbc_jefe3visto.gridwidth = 2;
//		gbc_jefe3visto.gridx = 0;
//		gbc_jefe3visto.gridy = 0;
//		panel3.add(jefe3visto, gbc_jefe3visto);
//
//		JLabel labelComentario3 = new JLabel("Comentario:");
//		GridBagConstraints gbc_labelComentario3 = new GridBagConstraints();
//		gbc_labelComentario3.anchor = GridBagConstraints.WEST;
//		gbc_labelComentario3.insets = new Insets(0, 0, 0, 5);
//		gbc_labelComentario3.gridx = 0;
//		gbc_labelComentario3.gridy = 1;
//		panel3.add(labelComentario3, gbc_labelComentario3);
//
//		jefe3 = new JTextField();
//		jefe3.setText(comentario.getJefe2());
//		GridBagConstraints gbc_jefe3 = new GridBagConstraints();
//		gbc_jefe3.fill = GridBagConstraints.BOTH;
//		gbc_jefe3.gridx = 1;
//		gbc_jefe3.gridy = 1;
//		panel3.add(jefe3, gbc_jefe3);
//		jefe3.setColumns(10);

		// ---------------------------------------------------------- fin
		// --------------------------------

		String salida = "";
		int contador = 1;
		try {
			if (entrada.comentario.comentarios != null) {
				if (entrada.comentario.comentarios.size() == 0) {

					if (Login.usuarioActivo.isJefe) {
						
						
						Date dateToday = new Date();
						DateFormat dateFormat_normal = new SimpleDateFormat("dd/MM/yyyy");
						DateFormat timeFormat = new SimpleDateFormat("HH:mm");
						
						
						
						comentarioVacio = new comentarioJefe(entrada.getId(), Login.usuarioActivo.usuario_id, Login.usuarioActivo.getRole(),  Login.usuarioActivo.getNombre_usuario(),Login.usuarioActivo.posicion, dateFormat_normal.format(dateToday), timeFormat.format(dateToday), "", 0);

						tabbedPane.addTab(Login.usuarioActivo.getRole(), null, new comentarioJefeVista(this, comentarioVacio,
								usuario, entrada.getId(), true, menu, cambiosRealizados).panel, null);
						

					}
				}else {
					boolean existeComentario = false;
					
					for (comentarioJefe comentarioElement : entrada.comentario.comentarios) {
						if (Login.usuarioActivo.getJefe_id() == comentarioElement.getUsuario_id()) {
							existeComentario = true;
						}
						
						if (!comentarioElement.getComentario().isEmpty()) {

							if (Login.usuarioActivo.getJefe_id() == comentarioElement.getUsuario_id()) {
								contador++;
								System.out.println("if primero");
							} else {
								
								if (salida.isEmpty()) {
									System.out.println("salida is empty");
									System.out.println("size" + entrada.comentario.comentarios.size());
									System.out.println("contador " + contador);
									salida += comentarioElement.getNombreJefe();
									contador++;
								} else if (contador != entrada.comentario.comentarios.size()+1) {
									System.out.println("salida is not empty");
									salida += ", " + comentarioElement.getNombreJefe();
									contador++;
								}
							}

						}
						
						tabbedPane.addTab(comentarioElement.getNombreJefe(), null, new comentarioJefeVista(this, comentarioElement,
								usuario, entrada.getId(), Login.usuarioActivo.usuario_id == comentarioElement.getUsuario_id(), menu, cambiosRealizados).panel, null);
						
					}
					
					if (!existeComentario && Login.usuarioActivo.isJefe) {
						Date dateToday = new Date();
						DateFormat dateFormat_normal = new SimpleDateFormat("dd/MM/yyyy");
						DateFormat timeFormat = new SimpleDateFormat("HH:mm");
						
						
						
						comentarioVacio = new comentarioJefe(entrada.getId(), Login.usuarioActivo.usuario_id, Login.usuarioActivo.getRole(),  Login.usuarioActivo.getNombre_usuario(),Login.usuarioActivo.posicion, dateFormat_normal.format(dateToday), timeFormat.format(dateToday), "", 0);

						tabbedPane.addTab(Login.usuarioActivo.getRole(), null, new comentarioJefeVista(this, comentarioVacio,
								usuario, entrada.getId(), true, menu, cambiosRealizados).panel, null);
					}
					
				}
				
			}else {
				
				
				
//				entrada.setComentario(new comentario(id));

			
				
			}
			
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block+
//			if (Login.usuarioActivo.isJefe) {
//				
////				System.out.println("Dentro jefeVisto " + Login.usuarioActivo.getNombre_usuario().equals(usuario));
//				Date dateToday = new Date();
//				DateFormat dateFormat_normal = new SimpleDateFormat("dd/MM/yyyy");
//				DateFormat timeFormat = new SimpleDateFormat("HH:mm");
//				
//				
//				
//				comentarioVacio = new comentarioJefe(entrada.getId(), Login.usuarioActivo.usuario_id, Login.usuarioActivo.getRole(),  Login.usuarioActivo.getNombre_usuario(),Login.usuarioActivo.posicion, dateFormat_normal.format(dateToday), timeFormat.format(dateToday), "", 0);
//																										// confirmar
//				System.out.println("prueba");
//				tabbedPane.addTab("prueba", null, new comentarioJefeVista(this, comentarioVacio,
//						usuario, entrada.getId(), true, menu, cambiosRealizados).panel, null);
//				
//
//			}
//			
//			entrada.setComentario(new comentario(id));

			e1.printStackTrace();
		}

		
		
		if (Login.usuarioActivo.posicion-1 >= 0 && Login.usuarioActivo.posicion-1 < tabbedPane.getTabCount()) {
			
			tabbedPane.setSelectedIndex(Login.usuarioActivo.posicion-1);
		}
		


		if (!salida.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Comentario de " + salida + ".");
		}

//		if (idJefe == -1 || idJefe == 5) {
//			tabbedPane.setSelectedIndex(0);
//		} else
//			tabbedPane.setSelectedIndex(idJefe);     //pendiente

		boolean otroComentario = false;
//		ArrayList<String> userWithComment = new ArrayList<String>();
//		System.out.println("IdJefe " + idJefe);
//
//		if (idJefe != 0 && comentario.getJefe1() != null) {
//			if (!comentario.getJefe1().equals("")) {
//				if (Login.CARGOS.containsKey(1)) {
//					userWithComment.add(Login.CARGOS.get(1));
//				} else {
//					userWithComment.add("Jefe 1");
//
//				}
//			}
//		}
//		if (idJefe != 1 && comentario.getJefe2() != null) {
//			if (!comentario.getJefe2().equals("")) {
//				if (Login.CARGOS.containsKey(2)) {
//					userWithComment.add(Login.CARGOS.get(2));
//				} else {
//					userWithComment.add("Jefe 2");
//
//				}
//			}
//		}
//		if (idJefe != 2 && comentario.getJefe3() != null) {
//			if (!comentario.getJefe3().equals("")) {
//				if (Login.CARGOS.containsKey(3)) {
//					userWithComment.add(Login.CARGOS.get(3));
//				} else {
//					userWithComment.add("Jefe 3");
//				}
//			}
//		}
//		if (idJefe != 3 && comentario.getJefe4() != null) {
//			if (!comentario.getJefe4().equals("")) {
//				if (Login.CARGOS.containsKey(4)) {
//					userWithComment.add(Login.CARGOS.get(4));
//				} else {
//					userWithComment.add("Jefe 4");
//				}
//			}
//		}
//		if (idJefe != 4 && comentario.getJefe5() != null) {
//			if (!comentario.getJefe5().equals("")) {
//				if (Login.CARGOS.containsKey(5)) {
//					userWithComment.add(Login.CARGOS.get(5));
//				} else {
//					userWithComment.add("Jefe 5");
//				}
//			}
//		}

//		String salida = "";
//		if (userWithComment.size() > 0) {
//			int contador = 1;
//			for (String string : userWithComment) {
//				if (contador != userWithComment.size()) {
//					salida += string + ", ";
//					contador++;
//				} else {
//					salida += string + ".";
//				}
//			}
//			JOptionPane.showMessageDialog(null, "Comentario de " + salida);
//		}

		f.setVisible(true);

	}

	private void comprobarCambios() {
//		if (!comentario.getJefe1().equals(jefe1.getText())) {
//			cambiosRealizados = true;
//		}
//		if (!comentario.getJefe2().equals(jefe2.getText())) {
//			cambiosRealizados = true;
//		}
//		if (!comentario.getJefe3().equals(jefe3.getText())) {
//			cambiosRealizados = true;
//		}
//		if (!comentario.getJefe4().equals(jefe4.getText())) {
//			cambiosRealizados = true;
//		}
//		if (!comentario.getJefe5().equals(jefe5.getText())) {
//			cambiosRealizados = true;
//		}
//		if (entrada.isJefe1() != jefe1visto.isSelected()) {
//			cambiosRealizados = true;
//		}
//		if (entrada.isJefe2() != jefe2visto.isSelected()) {
//			cambiosRealizados = true;
//		}
//		if (entrada.isJefe3() != jefe3visto.isSelected()) {
//			cambiosRealizados = true;
//		}
//		if (entrada.isJefe4() != jefe4visto.isSelected()) {
//			cambiosRealizados = true;
//		}
//		if (entrada.isJefe5() != jefe5visto.isSelected()) {
//			cambiosRealizados = true;
//		}
//		if (entrada.isSoloCoronel() != soloJefe1.isSelected()) {
//			cambiosRealizados = true;
//		}
//		if (entrada.isTramitado() != chckbxTramitado.isSelected()) {
//			cambiosRealizados = true;
//		}
//		if (entrada.isUrgente() != chckbxUrgente.isSelected()) {
//			cambiosRealizados = true;
//		}
//		if (entrada.isConfidencial() != chckbxConfidencial.isSelected()) {
//			cambiosRealizados = true;
//		}
//		if (!entrada.getObservaciones().equals(textObservaciones.getText())) {
//			cambiosRealizados = true;
//		}

	}

	private JPanel loadingPanel() {
		JPanel panel = new JPanel();
		BoxLayout layoutMgr = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
		panel.setLayout(layoutMgr);

		ClassLoader cldr = this.getClass().getClassLoader();
		java.net.URL imageURL = cldr.getResource("ajax-loader.gif");
		ImageIcon imageIcon = new ImageIcon(imageURL);
		JLabel iconLabel = new JLabel();
		iconLabel.setIcon(imageIcon);
		imageIcon.setImageObserver(iconLabel);

		JLabel label = new JLabel("Cargando...");
		panel.add(iconLabel);
		panel.add(label);
		return panel;
	}

	public void guardarComentario() {
		String[] options = { "Si", "No" };

		int opcionSeleccionada;
		
		if (destinosField.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Debe añadir al menos un destinatario.");
			return;
		}
		

		entrada.comentario.addComentarioJefe(comentarioVacio);

		try {
			for (comentarioJefe element : entrada.comentario.comentarios) {

				if (element.getUsuario_id() == Login.usuarioActivo.getUsuario_id()) {

					if (element.getVisto() == 0) {
						opcionSeleccionada = JOptionPane.showOptionDialog(null, "¿Desea marcar como vista esta entrada?",
								"", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
						if (opcionSeleccionada == 0) {
							if (element.getComentario().equals("")) {
								element.setVisto(1);
								System.out.println("setVisto 1 ");
							} else
								element.setVisto(2);
						}
						if (opcionSeleccionada == 1) {
							element.setVisto(0);
						}
					} else if (element.getComentario().equals("")) {

						element.setVisto(element.getVisto() == 1 ? 1 : 0);

					} else {

						element.setVisto(element.getVisto() == 1 || element.getVisto() == 2 ? 2 : 0);

					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
//			if (comentarioVacio.getVisto() == 0) {
//				opcionSeleccionada = JOptionPane.showOptionDialog(null, "¿Desea marcar como vista esta entrada?",
//						"", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
//				if (opcionSeleccionada == 0) {
//					if (comentarioVacio.getComentario().equals("")) {
//						comentarioVacio.setVisto(1);
//						System.out.println("setVisto 1 ");
//					} else
//						comentarioVacio.setVisto(2);
//				}
//				if (opcionSeleccionada == 1) {
//					comentarioVacio.setVisto(0);
//				}
//			} else if (comentarioVacio.getComentario().equals("")) {
//
//				comentarioVacio.setVisto(comentarioVacio.getVisto() == 1 ? 1 : 0);
//
//			} else {
//
//				comentarioVacio.setVisto(comentarioVacio.getVisto() == 1 || comentarioVacio.getVisto() == 2 ? 2 : 0);
//
//			}
		}
		
		

		ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(destinosField.getText().split(";")));

		ArrayList<String> arrayListJefes = new ArrayList<String>(
				Arrays.asList(destinatariosJefes.getText().split(";")));
		
		ArrayList<String> arrayListCategorias = new ArrayList<String>(Arrays.asList(textAreaCategorias.getText().split(";")));

		db.connect();
		db.borrarDestinos(entrada.getId());
		db.close();

//		db.connect();
//		db.borrarDestinosJefes(entrada.getId());
//		db.close();
		
		db.connect();
		db.borrarCategorias(entrada.getId());
		db.close();

		for (String destino : arrayList) {

			db.connect();
			db.agregarDestinatario(entrada.getId(), destino.trim());
			db.close();
		}

		for (String jefe : arrayListJefes) {

			db.connect();
			db.agregarDestinatarioJefe(entrada.getId(), jefe.trim());
			db.close();
		}
		
		for (String categoria : arrayListCategorias) {

			db.connect();
			db.agregarCategoriaEntrada(entrada.getId(), categoria.trim());
			db.close();
		}


		entrada.setConfidencial(chckbxConfidencial.isSelected());
		entrada.setUrgente(chckbxUrgente.isSelected());
		entrada.setObservaciones(textObservaciones.getText());
		entrada.setAsunto(asunto.getText());
		entrada.setCanalEntrada(comboBoxCanalEntrada.getSelectedItem().toString());
		entrada.setNumEntrada(textFieldnumEntrada.getText());

		// entrada.setArea((String) comboBoxNegociado.getSelectedItem());
		
		
		entrada.setArea(destinosField.getText());
		

		entrada.setTramitado(chckbxTramitado.isSelected());
		entrada.setTramitadoPor(tramitadoPor.getText());
		
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

			new File(Login.BASE_DIR + "DOCS\\" + dateFormat_guiones.format(date1)).mkdir();

			String newFileString = Login.BASE_DIR + "DOCS\\" + dateFormat_guiones.format(date1) + "\\"
					+ tempFile.getName();

			File newFile = new File(newFileString);
			opcionSeleccionada = -1;
			if (newFile.exists()) {
//				String[] options2 = { "Crear archivo nuevo", "Cancelar", "Sobrescribir archivo anterior" };
//
//				opcionSeleccionada = JOptionPane.showOptionDialog(null,
//						"El archivo ya está guardado, posiblemente esa entrada ya está dada de alta",
//						"Click en una opcion", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
//						null, options2, options2[0]);
				System.out.println("newFile exists");
				System.out.println(tableEntrada.getValueAt(i, 0));
				if (tableEntrada.getValueAt(i, 0).equals("")) {
					db.connect();


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
				
				File temp = new File(Login.BASE_DIR + "\\TEMP\\" + newFile.getName());

				if (temp.exists()) {
					try {
						copyFileOverwrite(temp, newFile);
						temp.delete();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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

			new File(Login.BASE_DIR + "DOCS_ANTECEDENTES\\" + dateFormat_guiones.format(date1)).mkdir();

			String newFileString = Login.BASE_DIR + "DOCS_ANTECEDENTES\\" + dateFormat_guiones.format(date1) + "\\"
					+ tempFile.getName();

			File newFile = new File(newFileString);
			opcionSeleccionada = -1;
			if (newFile.exists()) {
//				String[] options2 = { "Crear archivo nuevo", "Cancelar", "Sobrescribir archivo anterior" };
//
//				opcionSeleccionada = JOptionPane.showOptionDialog(null,
//						"El archivo ya está guardado, posiblemente esa entrada ya está dada de alta",
//						"Click en una opcion", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
//						null, options2, options2[0]);

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
				
				File temp = new File(Login.BASE_DIR + "\\TEMP\\" + newFile.getName());

				try {
					copyFileOverwrite(temp, newFile);
					temp.delete();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

			File tempFile = new File(tableSalida.getValueAt(i, 1).toString());

			new File(Login.BASE_DIR + "DOCS_SALIDA\\" + dateFormat_guiones.format(date1)).mkdir();

			String newFileString = Login.BASE_DIR + "DOCS_SALIDA\\" + dateFormat_guiones.format(date1) + "\\"
					+ tempFile.getName();

			File newFile = new File(newFileString);
			opcionSeleccionada = -1;
			if (newFile.exists()) {
//				String[] options2 = { "Crear archivo nuevo", "Cancelar", "Sobrescribir archivo anterior" };
//
//				opcionSeleccionada = JOptionPane.showOptionDialog(null,
//						"El archivo ya está guardado, posiblemente esa entrada ya está dada de alta",
//						"Click en una opcion", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
//						null, options2, options2[0]);

				if (tableSalida.getValueAt(i, 0).equals("")) {
					db.connect();
					db.saveFileSalida(newFile.getAbsolutePath(), entrada.getId(), tableSalida.getValueAt(i, 2).toString(),
							tableSalida.getValueAt(i, 3).toString(), tableSalida.getValueAt(i, 4).toString());
					db.close();
				} else {
					db.connect();
					db.updateFileSalida(tableSalida.getValueAt(i, 0).toString(), newFile.getAbsolutePath(), entrada.getId(),
							tableSalida.getValueAt(i, 2).toString(), tableSalida.getValueAt(i, 4).toString(),
							tableSalida.getValueAt(i, 3).toString());
					db.close();
				}
				
				File temp = new File(Login.BASE_DIR + "\\TEMP\\" + newFile.getName());

				try {
					copyFileOverwrite(temp, newFile);
					temp.delete();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				try {
					copyFile(tempFile, newFile);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Error al guardar el archivo " + e1.getMessage());
				}

				db.connect();
				db.saveFileSalida(newFile.getAbsolutePath(), entrada.getId(), tableSalida.getValueAt(i, 2).toString(),
						tableSalida.getValueAt(i, 3).toString(), tableSalida.getValueAt(i, 4).toString());
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
				db.saveFileSalida(newFile.getAbsolutePath(), entrada.getId(), tableSalida.getValueAt(i, 2).toString(),
						tableSalida.getValueAt(i, 3).toString(), tableSalida.getValueAt(i, 4).toString());
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

//		comentario.setJefe4(jefe4.getText());
//		comentario.setJefe4fecha(dateFormat.format(date));
//		comentario.setJefe4hora(timeFormat.format(date));
//		entrada.setJefe4(jefe4visto.isSelected());

//		comentario.setJefe5(jefe5.getText());
//		comentario.setJefe5fecha(dateFormat.format(date));
//		comentario.setJefe5hora(timeFormat.format(date));
//		entrada.setJefe5(jefe5visto.isSelected());

//		if (chckbxTramitado.isSelected()) {
//			if (tramitadoPor.getText().equals("")) {
//				JOptionPane.showMessageDialog(null, "Tramitado por no puede estar vacio");
//				return;
//			}
//			
//		}else {
//			entrada.setTramitadoPor(tramitadoPor.getText());
//		}
//		

		// falta si es negociado no gestionado //
//		boolean isNotGestionado = false;
//		
//		for (String string : destinos) {
//			db.connect();
//			isNotGestionado = db.isNotGestionado(string);
//			db.close();
//		}
//		
//		if (isNotGestionado) {
//			System.out.println("Prueba is Not Gestionado");
//			if (jefe1.getText().equals("") && jefe2.getText().equals("") && jefe3.getText().equals("") && jefe4.getText().equals("")  && jefe5.getText().equals("")) {
//				db.connect();
//				db.eliminarEntradaNoGestionada(entrada.getId());
//				db.close();
//			}else {
//				db.connect();
//				db.addEntradaNoGestionada(entrada.getId());
//				db.close();
//			}
//			
//			if (Login.usuarioActivo.getRole().equals("Registro") && chckbxAvisadoAlNegociado.isSelected()) {
//				db.connect();
//				
//				db.eliminarEntradaNoGestionada(entrada.getId());
////				db.actualizarEntradaNoGestionada(entrada.getId(), chckbxAvisadoAlNegociado.isSelected());
//				db.close();
//			}else if (Login.usuarioActivo.getRole().equals("Registro") && !chckbxAvisadoAlNegociado.isSelected()) {
//				db.connect();
//				db.addEntradaNoGestionada(entrada.getId());
//				db.close();
//			}
//		}

//		db.connect();
//		db.actualizarComentarioSoloJefe(idJefe, comentario);   pendiente
//		db.close();

//		db.connect();
//		db.actualizarComentarioTramitado(entrada.comentario);
//		db.close();

		db.connect();
		db.actualizarEntrada(entrada);
		db.close();

		entrada.comentario.update();

		GUI.EntradaClosed = true;
		f.dispose();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {

			String[] options = { "Si", "No" };
			int opcionSeleccionada = -1;
			opcionSeleccionada = JOptionPane.showOptionDialog(null, "¿Desea guardar los cambios?", "",
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			if (opcionSeleccionada == 0) {
				guardarComentario();
			}
			f.dispose();
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

	public JScrollPane TableFromDatabaseSalida(ResultSet rs) {

		if (scrollPane != null) {
			valueScroll = scrollPane.getVerticalScrollBar().getValue();
		}

		Vector columnNames = new Vector();
		dataSalida = new Vector();

		try {

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

			while (rs.next()) {

				Vector row = new Vector();
				row.setSize(100);

				int posicion = -1;

				if (entrada_id != (int) rs.getObject("salida_id") || entrada_id == -1) {

					if (!dataSalida.isEmpty()) {
						lastPosition = dataSalida.lastIndexOf(dataSalida.lastElement());
						lastElement = (Vector) dataSalida.lastElement();
					}

					entrada_id = (int) rs.getObject("salida_id");

					row.add(0, rs.getObject("salida_id"));
					row.add(1, rs.getObject("file"));
					row.add(2, rs.getObject("fecha"));
					row.add(3, rs.getObject("destino"));
					row.add(4, rs.getObject("asunto"));
					URL url = GUI.class.getResource("/delete.png");
					ImageIcon icon = new ImageIcon(url);
					row.add(5, icon); // eliminar

					url = GUI.class.getResource("/vacio.png");
					icon = new ImageIcon(url);

					int iterator = 0;
					for (Map.Entry<Integer, String> entry : Login.CARGOS.entrySet()) {
						row.add(6 + iterator, icon);
						iterator++;
					}
					try {

						if (rs.getObject("usuario_id") != null) {

							db.connect();
							posicion = db.getPosicion((int) rs.getObject("usuario_id"));
							db.close();

							if ((int) rs.getObject("vistoBueno") == 0) {
								url = GUI.class.getResource("/vacio.png");
								icon = new ImageIcon(url);
								row.set(posicion + 5, icon);

							}
							if ((int) rs.getObject("vistoBueno") == 1) {
								url = GUI.class.getResource("/single.png");
								icon = new ImageIcon(url);
								row.set(posicion + 5, icon);
							}
							if ((int) rs.getObject("vistoBueno") == 2) {
								url = GUI.class.getResource("/lapiz.png");
								icon = new ImageIcon(url);
								row.set(posicion + 5, icon);
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(
								"DB ERROR posicion = Integer.parseInt(db.getPosicion((int) rs.getObject(\"usuario_id\")));\r\n"
										+ e);
					}

					dataSalida.addElement(row);

				} else {
					if (rs.getObject("usuario_id") != null) {

						db.connect();
						posicion = db.getPosicion((int) rs.getObject("usuario_id"));
						db.close();

						if (dataSalida.isEmpty()) {
							System.out.println("Data is empty");
							lastElement = (Vector) dataSalida.lastElement();
							if ((int) rs.getObject("vistoBueno") == 0) {
								URL url = GUI.class.getResource("/vacio.png");
								ImageIcon icon = new ImageIcon(url);
								lastElement.set(posicion + 5, icon);

							}
							if ((int) rs.getObject("vistoBueno") == 1) {
								URL url = GUI.class.getResource("/single.png");
								ImageIcon icon = new ImageIcon(url);
								lastElement.set(posicion + 5, icon);
							}
							if ((int) rs.getObject("vistoBueno") == 2) {
								URL url = GUI.class.getResource("/lapiz.png");
								ImageIcon icon = new ImageIcon(url);
								lastElement.set(posicion + 5, icon);
							}
						} else {
							lastElement = (Vector) dataSalida.lastElement();

							if ((int) rs.getObject("vistoBueno") == 0) {
								URL url = GUI.class.getResource("/vacio.png");
								ImageIcon icon = new ImageIcon(url);
								lastElement.set(posicion + 5, icon);

							}
							if ((int) rs.getObject("vistoBueno") == 1) {
								URL url = GUI.class.getResource("/single.png");
								ImageIcon icon = new ImageIcon(url);
								lastElement.set(posicion + 5, icon);
							}
							if ((int) rs.getObject("vistoBueno") == 2) {
								URL url = GUI.class.getResource("/lapiz.png");
								ImageIcon icon = new ImageIcon(url);
								lastElement.set(posicion + 5, icon);
							}
						}
					}
				}

			}

			rs.close();

		} catch (Exception e) {
			System.out.println(e);
		}

		tablemodelSalida = new DefaultTableModel(dataSalida, columnNames) {

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
				return false;
			}

		};

		tableSalida = new JTable(tablemodelSalida);
		tableSalida.setPreferredScrollableViewportSize(new Dimension(0, 0));
		tableSalida.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tableSalida.getColumnModel().getColumn(1).setWidth(0);
		tableSalida.getColumnModel().getColumn(1).setMaxWidth(0);
		tableSalida.getColumnModel().getColumn(1).setMinWidth(0);
		tableSalida.getColumnModel().getColumn(1).setPreferredWidth(0);

		tableSalida.setFont(new Font("Tahoma", Font.TRUETYPE_FONT, 14));

		tableSalida.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));

		tableSalida.setRowHeight(25);

		// URGENTE

		tableSalida.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int col) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
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

						System.out.println(!destinosField.getText().contains("Todos"));
						if (!Login.usuarioActivo.getRole().equals("Registro")
								&& (!destinosField.getText().contains(Login.usuarioActivo.getRole())
										&& !destinosField.getText().contains("Todos")) && (!destinatariosJefes.getText().contains(Login.usuarioActivo.getRole())
												&& !destinatariosJefes.getText().contains("Todos"))) {
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
							if (!db.deleteSalidaFile((int) target.getValueAt(row, 0))) {
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
						
						if (target.getValueAt(row, 0).equals("")) {
							try {
								desktop.open(myFile);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}else {
							try {
								File temp = new File(Login.BASE_DIR + "\\TEMP\\" + myFile.getName());
								CryptoUtils.decrypt("PdSgVkYp3s6v9y$B&E)H@MbQeThWmZq4", myFile, temp);
								desktop.open(temp);
								} 
							catch (Exception e2) {
								// TODO Auto-generated catch block
								JOptionPane.showMessageDialog(null, "Error al abrir el archivo " + myFile.getAbsolutePath());

								e2.printStackTrace();
							}
						}
						

					}

					System.out.println("column-5 " + (column - 5));
					System.out.println("posicion usuario activo " + Login.usuarioActivo.posicion);

					if (column - 5 == Login.usuarioActivo.posicion && Login.usuarioActivo.posicion > 0) {
						// pendiente doble click jefe visto
						System.out.println("hola");
						System.out.println(target.getValueAt(row, column));
						URL urlvacio = GUI.class.getResource("/vacio.png");
						ImageIcon iconvacio = new ImageIcon(urlvacio);
						URL urlsingle = GUI.class.getResource("/single.png");
						ImageIcon iconsingle = new ImageIcon(urlsingle);
						URL urldoble = GUI.class.getResource("/lapiz.png");
						ImageIcon icondoble = new ImageIcon(urldoble);

						db.connect();
						int vistoBuenoTemp = db.getVistoBuenoSalida(Login.usuarioActivo.jefe_id,
								target.getValueAt(row, 0).toString());
						db.close();

						if (vistoBuenoTemp == 0 || vistoBuenoTemp == -1) {
							System.out.println("hola 0");

							db.connect();
							db.setVistoBuenoSalida(target.getValueAt(row, 0).toString(), Login.usuarioActivo.jefe_id,
									1);
							db.close();
							target.setValueAt(iconsingle, row, column);

						}

						if (vistoBuenoTemp == 1) {
							System.out.println("hola 1");

							db.connect();
							db.setVistoBuenoSalida(target.getValueAt(row, 0).toString(), Login.usuarioActivo.jefe_id,
									2);
							db.close();

							target.setValueAt(icondoble, row, column);
						}

						if (vistoBuenoTemp == 2) {
							System.out.println("hola 2");

							db.connect();
							db.setVistoBuenoSalida(target.getValueAt(row, 0).toString(), Login.usuarioActivo.jefe_id,
									0);
							db.close();

							target.setValueAt(iconvacio, row, column);
						}

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

		return scrollPane;
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

						System.out.println(!destinosField.getText().contains("Todos"));
						if (!Login.usuarioActivo.getRole().equals("Registro")
								&& (!destinosField.getText().contains(Login.usuarioActivo.getRole())
										&& !destinosField.getText().contains("Todos")) && (!destinatariosJefes.getText().contains(Login.usuarioActivo.getRole())
												&& !destinatariosJefes.getText().contains("Todos"))) {
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
						if (target.getValueAt(row, 0).equals("")) {
							try {
								desktop.open(myFile);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}else {
							try {
								File temp = new File(Login.BASE_DIR + "\\TEMP\\" + myFile.getName());
								CryptoUtils.decrypt("PdSgVkYp3s6v9y$B&E)H@MbQeThWmZq4", myFile, temp);
								desktop.open(temp);
								} 
							catch (Exception e2) {
								// TODO Auto-generated catch block
								JOptionPane.showMessageDialog(null, "Error al abrir el archivo " + myFile.getAbsolutePath());
								e2.printStackTrace();
							}
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

						System.out.println(!destinosField.getText().contains("Todos"));
						if (!Login.usuarioActivo.getRole().equals("Registro")
								&& (!destinosField.getText().contains(Login.usuarioActivo.getRole())
										&& !destinosField.getText().contains("Todos")) && (!destinatariosJefes.getText().contains(Login.usuarioActivo.getRole())
												&& !destinatariosJefes.getText().contains("Todos"))) {
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
						if (target.getValueAt(row, 0).equals("")) {
							try {
								desktop.open(myFile);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}else {
							try {
								File temp = new File(Login.BASE_DIR + "\\TEMP\\" + myFile.getName());
								CryptoUtils.decrypt("PdSgVkYp3s6v9y$B&E)H@MbQeThWmZq4", myFile, temp);
								desktop.open(temp);
								} 
							catch (Exception e2) {
								// TODO Auto-generated catch block
								JOptionPane.showMessageDialog(null, "Error al abrir el archivo " + myFile.getAbsolutePath());
								e2.printStackTrace();
							}
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

	@SuppressWarnings("serial")
	class FileListTransferHandlerSalida extends TransferHandler {
		private Vector list;
		private File file;

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

				List data = (List) ts.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

				System.out.println(data.size());
				if (data.size() < 1) {
					return false;
				}

				for (Object item : data) {
					file = (File) item;
					DefaultTableModel prueba = (DefaultTableModel) tableSalida.getModel();
					FileTime creationTime = (FileTime) Files.getAttribute(file.toPath(), "lastModifiedTime");
					Date dateCreated = new Date(creationTime.toMillis());

					URL url = GUI.class.getResource("/delete.png");
					ImageIcon icon = new ImageIcon(url);

					int opcionSeleccionada = -1;
					boolean existFile = false;

					for (int i = 0; i < tableSalida.getRowCount(); i++) {
						System.out.println("table.getvalue ");
						System.out.println(tableSalida.getValueAt(i, 1));
						System.out.println("file ");
						System.out.println(file);
						if (tableSalida.getValueAt(i, 1).equals(file)) {
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
								new Object[] { "", file,
										dateFormat_normal.format(dateCreated).toString() + " "
												+ timeFormat.format(dateCreated).toString(),
										"", file.getName(), icon });
					}

					if (opcionSeleccionada == 0) {
						File newFile = new File(stripExtension(file.getName()) + "_" + new Date().getTime() + "_COPIA."
								+ getExtension(file.getName()));

						prueba.addRow(
								new Object[] { "", newFile,
										dateFormat_normal.format(dateCreated).toString() + " "
												+ timeFormat.format(dateCreated).toString(),
										"", newFile.getName(), icon });

					} else if (opcionSeleccionada == 1) {

						continue;

					} else if (opcionSeleccionada == 2) {

						prueba.addRow(
								new Object[] { "", file,
										dateFormat_normal.format(dateCreated).toString() + " "
												+ timeFormat.format(dateCreated).toString(),
										"", file.getName(), icon });

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
}
