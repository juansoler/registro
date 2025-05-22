package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import models.*;
import service.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;


import javax.print.attribute.standard.JobSheets;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Login implements KeyListener {
	
	public static String BASE_DIR = "";
	public static String BASE_DIR_GENERAL = "";
	public static String BASE_DIR_PLM = "";
	public static String LOCAL_DIR = "";

	public static ArrayList<String> NEGOCIADOS;
	public static ArrayList<String> CANALES;
	public static ArrayList<String> CATEGORIAS;

	public static HashMap<Integer, String> CARGOS;
	public static HashMap<Integer, Integer> POSICIONES;

	
	public static Usuario usuarioActivo;


	private JFrame f = new JFrame("Carpeta de entrada");
	
	private String user = "";
//	private String password = "";
	private JTextField usuario;
	private JPasswordField password;
	private JPasswordField newPassword;
	private Password passwordSHA;
	private db db = new db();
	private dbProd dbProd = new dbProd();

	static JFrame frame;
	private boolean newPasswordbool;
	private String saltedHash = "";
	JButton btnConectar;
	public static GUI gui;
	public static JFrame frame2;
	private JLabel lblVersinActual;
	private JButton btnActualizar;
	private boolean showPassword;
	private String dirTemp;

	public static void createAndShowGUI(String usuario) throws IOException {
		// Create and set up the window.

		frame2 = new JFrame("Carpeta de entrada");
		URL url = GUI.class.getResource("/icon.png");
		ImageIcon icon = new ImageIcon(url);

		frame2.setIconImage(icon.getImage());
		frame2.setSize(1024, 768);
		frame2.setMinimumSize(new Dimension(1024, 800));
		frame2.setExtendedState(frame2.getExtendedState() | frame2.MAXIMIZED_BOTH);
		frame2.setLocationRelativeTo(null);

		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Add content to the window.
		gui = new GUI(usuario);
		gui.setFocusable(true);
		frame2.getContentPane().add(gui);

		// Display the window.
//		frame.pack();
		frame2.setVisible(true);
		
		
	}
	
	

	public Login() throws IOException {
		
		
		// try(BufferedReader br = new BufferedReader(new FileReader("CONFIG.CFG"))) {
		// try(BufferedReader br = new BufferedReader(new FileReader("C:\\entrada\\CONFIG.CFG"))) {
		String config = "CONFIG.CFG";
		createMenuBar();
		
		if (!new File(config).exists()) {
			config = ".\\CONFIG.CFG";
		}
		
		 try(BufferedReader br = new BufferedReader(new FileReader(config))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
				String temp = line;
				System.out.println(temp);
				if (temp.split("=").length > 0) {
					 if (temp.split("=")[0].equals("BASE_DIR")) {
						 
					    	System.out.println("ok");
					    	System.out.println(temp.split("=")[0]);
					    	System.out.println(temp.split("=")[1]);
					    	BASE_DIR = temp.split("=")[1];
					    }
					 
					 if (temp.split("=")[0].equals("BASE_DIR_GENERAL")) {
						 
					    	System.out.println("ok");
					    	System.out.println(temp.split("=")[0]);
					    	System.out.println(temp.split("=")[1]);
					    	BASE_DIR_GENERAL = temp.split("=")[1];
					    }
					 
					 if (temp.split("=")[0].equals("BASE_DIR_PLM")) {
						 
					    	System.out.println("ok");
					    	System.out.println(temp.split("=")[0]);
					    	System.out.println(temp.split("=")[1]);
					    	BASE_DIR_PLM = temp.split("=")[1];
					    }
					 if (temp.split("=")[0].equals("LOCAL_DIR")) {
						 
					    	System.out.println("ok");
					    	System.out.println(temp.split("=")[0]);
					    	System.out.println(temp.split("=")[1]);
					    	LOCAL_DIR = temp.split("=")[1];
					    }
					 
//					 if (temp.split("=")[0].equals("NEGOCIADOS")) {
//					    	System.out.println("ok");
//					    	System.out.println(temp.split("=")[0]);
//					    	System.out.println(temp.split("=")[1]);
//					    	NEGOCIADOS = temp.split("=")[1].split(",");
//					    }
					 
//					 if (temp.split("=")[0].equals("CARGOS")) {
//					    	System.out.println("ok");
//					    	System.out.println(temp.split("=")[0]);
//					    	System.out.println(temp.split("=")[1]);
//					    	CARGOS = temp.split("=")[1].split(",");
//					    }
				}
			   line = br.readLine();
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "No se puede encontrar el archivo CONFIG.CFG");
			System.exit(0);
			e1.printStackTrace();
		}
		 
		 db.connect();
		 NEGOCIADOS = db.getNegociados();
		 db.close();
		 
		 db.connect();
		 CARGOS = db.getCargos();
		 db.close();
		 
		 db.connect();
		 CANALES = db.getCanales();
		 db.close();
		 
		 db.connect();
		 CATEGORIAS = db.mostrarCategorias();
		 db.close();
		 
		 db.connect();
		 POSICIONES = db.getPosiciones();
		 db.close();
		 
		 
//		 dbProd.connect();
//		 ResultSet entradas = dbProd.mostrarEntradas();
//		
//		 int idTemp = 0;
//		 entradaProd entradaProdTemp = null;
//		 entrada entradaTemp = null;
//		 comentarioProd comentarioProdTemp = null;
//		 comentario comentarioTemp = null;
//		 int idEntradaNueva = -1;
//		try {
//			while (entradas.next()) {
//				
//				idTemp = entradas.getInt("id");
//				System.out.println("Entrada: " + idTemp);
//		      
//				dbProd.connect();
//				entradaProdTemp = dbProd.mostrarEntrada(idTemp);
//				dbProd.close(); 
//				db.connect();
//				if (db.entradaExist(idTemp)) {
//					System.out.println("exist");
//					db.close();
//					continue;
//				}
//				db.close();
//				
//				entradaTemp = new entrada(entradaProdTemp.getAsunto(), entradaProdTemp.getFecha(), entradaProdTemp.getArea(), entradaProdTemp.isConfidencial(), entradaProdTemp.isUrgente());
//				entradaTemp.setObservaciones(entradaProdTemp.getObservaciones());
//				entradaTemp.setCanalEntrada("Antiguo");
//				entradaTemp.setNumEntrada("-1");
//				entradaTemp.setTramitado(entradaProdTemp.isTramitado());
//				
//				
//				db.connect();
//				idEntradaNueva = db.saveEntradaWithId(entradaTemp, idTemp);
//				db.close();
//				
//				db.connect();
//				entradaTemp = db.mostrarEntrada(idEntradaNueva);
//				db.close();
//				
//				db.connect();
//				entradaTemp.comentario = db.mostrarComentario(idEntradaNueva);
//				db.close();
//				
//				dbProd.connect();
//				comentarioProdTemp = dbProd.mostrarComentario(idTemp);
//				dbProd.close();
//				
//				dbProd.connect();
//				ResultSet listFiles = dbProd.mostrarFilesResultSet(idTemp);
//				
//				while(listFiles.next()) {
//					System.out.println("file: " + listFiles.getString("file"));
//					db.connect();
//					db.saveFileEntrada(listFiles.getString("file"), idTemp, entradaTemp.getFecha(), entradaTemp.getAsunto(), "", "");
//					db.close();
//				}
//				dbProd.close();
//				
//				dbProd.connect();
//				ResultSet listFilesAntecedentes = dbProd.mostrarFilesAntecedentesResultSet(idTemp);
//				dbProd.close();
//				
//				while(listFiles.next()) {
//					db.connect();
//					db.saveFileAntecedentes(listFiles.getString("file"), idTemp, entradaTemp.getFecha(), entradaTemp.getAsunto(), "", "", "");
//					db.close();
//				}
//				entradaTemp.setTramitadoPor(comentarioProdTemp.getTramitadoPor());
//
//				comentarioTemp = new comentario(idEntradaNueva);
////				String sql = "UPDATE comentario (entrada_id,  fecha, hora, visto, usuario_id, comentario) VALUES (?,?,?,?,?,?)";
//				for (comentarioJefe comentarioElement : entradaTemp.comentario.comentarios) {
//					System.out.println("comentarioElement.getUsuario_id() " + comentarioElement.getUsuario_id());
//					if (comentarioElement.getUsuario_id() == 23) {
//						System.out.println("Comentario Jefe1 "  +comentarioProdTemp.getJefe1());
//						comentarioElement.setComentario(comentarioProdTemp.getJefe1());
//						comentarioElement.setFecha(comentarioProdTemp.getJefe1fecha());
//						comentarioElement.setHora(comentarioProdTemp.getJefe1hora());
//						comentarioElement.setVisto(entradaProdTemp.getJefe1());
//					}
//					if (comentarioElement.getUsuario_id() == 26) {
//						comentarioElement.setComentario(comentarioProdTemp.getJefe2());
//						comentarioElement.setFecha(comentarioProdTemp.getJefe2fecha());
//						comentarioElement.setHora(comentarioProdTemp.getJefe2hora());
//						comentarioElement.setVisto(entradaProdTemp.getJefe2());
//					}
//					if (comentarioElement.getUsuario_id() == 27) {
//						comentarioElement.setComentario(comentarioProdTemp.getJefe3());
//						comentarioElement.setFecha(comentarioProdTemp.getJefe3fecha());
//						comentarioElement.setHora(comentarioProdTemp.getJefe3hora());
//						comentarioElement.setVisto(entradaProdTemp.getJefe3());
//					}
//					if (comentarioElement.getUsuario_id() == 30) {
//						comentarioElement.setComentario(comentarioProdTemp.getJefe4());
//						comentarioElement.setFecha(comentarioProdTemp.getJefe4fecha());
//						comentarioElement.setHora(comentarioProdTemp.getJefe4hora());
//						comentarioElement.setVisto(entradaProdTemp.getJefe4());
//					}
//					if (comentarioElement.getUsuario_id() == 29) {
//						comentarioElement.setComentario(comentarioProdTemp.getJefe5());
//						comentarioElement.setFecha(comentarioProdTemp.getJefe5fecha());
//						comentarioElement.setHora(comentarioProdTemp.getJefe5hora());
//						comentarioElement.setVisto(entradaProdTemp.getJefe5());
//					}
//				}
//				
//				
//				entradaTemp.comentario.update();
//				
//				
//				dbProd.connect();
//				ArrayList<String> destinos = dbProd.getDestinos(idTemp);
//				dbProd.close();
//				
//				for (String destino : destinos) {
//					db.connect();
//					db.agregarDestinatario(idEntradaNueva, destino);
//					db.close();
//				}
//				
//				db.connect();
//				db.agregarDestinatarioJefe(idEntradaNueva, "Todos");
//				db.close();
//				
//				
//				
//				db.connect();
//				db.actualizarEntrada(entradaTemp);
//				db.close();
//			}
//		} catch (SQLException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
//
//		 dbProd.close();

		 
		
		
		URL url = GUI.class.getResource("/icon.png");
		ImageIcon icon = new ImageIcon(url);
		f.setIconImage(icon.getImage());
		f.setSize(new Dimension(339, 399));
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.getContentPane().setLayout(null);
		f.setLocationRelativeTo(null);  

		dirTemp = Login.BASE_DIR;
		

		usuario = new JTextField();
		usuario.addKeyListener(this);

		usuario.addFocusListener(new FocusListener() {

	            @Override
	            public void focusGained(FocusEvent e) {
	                //Your code here
	            	if (Toolkit.getDefaultToolkit().getLockingKeyState (KeyEvent.VK_CAPS_LOCK ) && usuario.hasFocus()) {
	        			usuario.setBorder(new TitledBorder(null, "Usuario  - Mayúsculas activa", TitledBorder.LEADING, TitledBorder.TOP, null, null));
	        			
	        		}else {
	        			usuario.setBorder(new TitledBorder(null, "Usuario", TitledBorder.LEADING, TitledBorder.TOP, null, null));

	        		}
	            }

	            @Override
	            public void focusLost(FocusEvent e) {
	                //Your code here
        			usuario.setBorder(new TitledBorder(null, "Usuario", TitledBorder.LEADING, TitledBorder.TOP, null, null));

	            }
	        });
		
		
		
		JComboBox comboBoxZonaGeneral = new JComboBox();
		comboBoxZonaGeneral.setBounds(24, 281, 129, 23);
		comboBoxZonaGeneral.setModel(new DefaultComboBoxModel(new String[] {"PLM", "ZONA-GENERAL"}));
		
		
//		f.getContentPane().add(comboBoxZonaGeneral);    
		
		comboBoxZonaGeneral.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (comboBoxZonaGeneral.getSelectedIndex() == 1) {
					Login.BASE_DIR = Login.BASE_DIR_GENERAL;
					 db.connect();
					 Login.NEGOCIADOS = db.getNegociados();
					 db.close();
					 
					 db.connect();
					 Login.CARGOS = db.getCargos();
					 db.close();
					
					
					
				}
				
				if (comboBoxZonaGeneral.getSelectedIndex() == 0) {
					Login.BASE_DIR = BASE_DIR_PLM;
					 db.connect();
					 Login.NEGOCIADOS = db.getNegociados();
					 db.close();
					 
					 db.connect();
					 Login.CARGOS = db.getCargos();
					 db.close();
					 
					

				}
			}
		});
		
		usuario.setBorder(new TitledBorder(null, "Usuario", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		usuario.setBounds(24, 49, 255, 49);
		f.getContentPane().add(usuario);
		usuario.setColumns(10);

		password = new JPasswordField();
		password.addKeyListener(this);

		password.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                //Your code here
            	if (Toolkit.getDefaultToolkit().getLockingKeyState (KeyEvent.VK_CAPS_LOCK ) && password.hasFocus()) {
            		password.setBorder(new TitledBorder(null, "Contraseña  - Mayúsculas activa", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        			
        		}else {
        			password.setBorder(new TitledBorder(null, "Contraseña", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        		}
            }

            @Override
            public void focusLost(FocusEvent e) {
                //Your code here
            	password.setBorder(new TitledBorder(null, "Contraseña", TitledBorder.LEADING, TitledBorder.TOP, null, null));

            }
        });
		password.setBorder(
				new TitledBorder(null, "Contrase\u00F1a", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		password.setBounds(24, 122, 255, 49);
		f.getContentPane().add(password);
		password.setColumns(10);

		newPassword = new JPasswordField();
		newPassword.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Contrase\u00F1a nueva",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		newPassword.setBounds(24, 188, 255, 49);
		newPassword.setColumns(10);
		newPassword.addKeyListener(this);

		btnConectar = new JButton("Conectar");
		btnConectar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				conectar();

//        		
			}
		});
		btnConectar.setBounds(95, 210, 98, 23);
		f.getContentPane().add(btnConectar);
		
		showPassword = false;
		JCheckBox chckbxMostrarContrasea = new JCheckBox("Mostrar contrase\u00F1a");
		chckbxMostrarContrasea.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				showPassword = !showPassword;

				if (showPassword) {
					
					password.setEchoChar((char)0);
					
					
				}else {
					password.setEchoChar('\u2022');
					
				}
				
			}
		});
		
		chckbxMostrarContrasea.setBounds(24, 180, 169, 23);
		f.getContentPane().add(chckbxMostrarContrasea);
		
		lblVersinActual = new JLabel("Versi\u00F3n actual:");
		lblVersinActual.setBounds(10, 315, 146, 14);
		f.getContentPane().add(lblVersinActual);
		
		btnActualizar = new JButton("Actualizar");
		btnActualizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				abrirActualizador();
				System.exit(0);
			}
		});
		btnActualizar.setBounds(166, 311, 142, 23);

		
		
		File local = new File(Login.LOCAL_DIR + "VER.CFG");
		//File remoto = new File("H:\\plmcomd\\COMUN\\CORRESPONDENCIA ENTRADA\\entrada1.exe");
		File remoto = new File(Login.BASE_DIR + "VER.CFG");

		String versionLocal = "";

		
		String versionRemota = "";
		
		
		
		 try(BufferedReader br = new BufferedReader(new FileReader(remoto))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
				String temp = line;
				if (temp.split("=").length > 0) {
					 if (temp.split("=")[0].equals("VERSION")) {
					    	System.out.println(temp);
					    	versionRemota = temp.split("=")[1];
					    }
				}
			   line = br.readLine();
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "No se puede encontrar el archivo VER.CFG");
			System.out.println("No se puede encontrar el archivo VER.CFG");
			e1.printStackTrace();
		}
		 
		 try(BufferedReader br = new BufferedReader(new FileReader(local))) {
			    StringBuilder sb = new StringBuilder();
			    String line = br.readLine();

			    while (line != null) {
					String temp = line;
					if (temp.split("=").length > 0) {
						 if (temp.split("=")[0].equals("VERSION")) {
						    	System.out.println(temp);
						    	versionLocal = temp.split("=")[1];
						    }
					}
				   line = br.readLine();
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, "No se puede encontrar el archivo VER.CFG");
				System.out.println("No se puede encontrar el archivo VER.CFG");
				e1.printStackTrace();
			}
		 
			lblVersinActual.setText(lblVersinActual.getText() + " " + versionLocal);

		 
		 if (Double.parseDouble(versionLocal) < Double.parseDouble(versionRemota)) {
			 
			    btnActualizar.setText(btnActualizar.getText() + ": " + versionRemota);
				
				String[] options = { "Si", "No" };

				int opcionSeleccionada = -1;
				
				opcionSeleccionada = JOptionPane.showOptionDialog(null, "¿Desea actualizar?", "",
						JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
				
				if (opcionSeleccionada==0) {
					abrirActualizador();
					System.exit(0);
				}else {
					f.getContentPane().add(btnActualizar);

					System.out.println("version menor");
					btnActualizar.setEnabled(true);
					btnActualizar.setBackground(new Color(255, 102, 102));
				}
				
			}

//		if (local.exists() && remoto.exists()) {
//			
//			
//			lblVersinActual.setText(lblVersinActual.getText() + versionLocal);
//			btnActualizar.setText(btnActualizar.getText() + ": " + versionRemota);
//
//			
//			
//		}

		
//		f.getContentPane().add();

		f.setVisible(true);

	}
	
	public void abrirActualizador() {
		//File myFile = new File("H:\\plmcomd\\COMUN\\CORRESPONDENCIA ENTRADA\\actu.exe");
		File myFile = new File(Login.BASE_DIR + "actu.exe");

		if (!Desktop.isDesktopSupported()) {
			System.out.println("Desktop is not supported");
			return;
		}
		Desktop desktop = Desktop.getDesktop();
		if (myFile.exists())
			try {
				desktop.open(myFile);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		else JOptionPane.showMessageDialog(null, "No se encuentra el archivo.");

	}

	public void conectar() {
		
				f.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				if (usuario.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "El nombre de usuario no puede estar vacío.");
					f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

					return;
				}
				
				if (String.valueOf(password.getPassword()).equals("")) {
					JOptionPane.showMessageDialog(null, "La constraseña no puede estar vacia.");
					f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

					return;
				}
				db.connect();
				if (!db.comprobarUsuario(usuario.getText().toLowerCase())) {
					JOptionPane.showMessageDialog(null, "El usuario no existe.");
					f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

					usuario.setText("");
					password.setText("");
				}
				db.close();
				
        		passwordSHA = new Password();
        		
        		if (newPasswordbool) {
        			db.connect();
        			String newPass=String.valueOf(newPassword.getPassword());
					try {
						saltedHash = passwordSHA.getSaltedHash(newPass);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

						e.printStackTrace();
						return;

					}

        			if (db.saveNewPassword(usuario.getText().toLowerCase(), saltedHash)) {
						JOptionPane.showMessageDialog(null, "Password ha sido cambiado con éxito. Ingrese su nuevo password para conectar.");
						newPasswordbool = false;
						password.setText("");
						password.setEnabled(true);
						f.remove(newPassword);
						password.grabFocus();
	        	        btnConectar.setSize(100, 23);
						btnConectar.setText("Conectar");
						f.repaint();
						f.revalidate();
						f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

						return;
        			}else {
						JOptionPane.showMessageDialog(null, "Error al restablecer la nueva contraseña.");
						f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        			}
        			db.close();
        			
        		}
        		
        		try {
        			
        			String Pass =String.valueOf(password.getPassword());

        			System.out.println("Password " + Pass);
					db.connect(); 
	        		int logged = db.login(usuario.getText().toLowerCase(), Pass);
	        		db.close();
	        		
	        		if (logged == 1) {
	        			System.out.println("Login Correcto");
	        			f.dispose();
	        			SwingUtilities.invokeLater(new Runnable() {
	        				public void run() {
	        					// Turn off metal's use of bold fonts
	        					UIManager.put("swing.boldMetal", Boolean.FALSE);
	        					try {
//	        						db.connect();
//	        						String role = db.getRole(usuario.getText().toLowerCase());
//	        						db.close();
	        						
	        						createAndShowGUI(usuario.getText().toLowerCase());
	        					} catch (IOException e) {
	        						// TODO Auto-generated catch block
	        						e.printStackTrace();
	        					}
	        				}
	        			});
	        		}
	        		
	        		if (logged == 2) {
	        			System.out.println("Password reset, introducir pass nuevo...");
						JOptionPane.showMessageDialog(null, "Reseteo de password, introduzca su password nuevo");
						newPasswordbool = true;
						password.setEnabled(false);
	        	        f.getContentPane().add(newPassword);
	        	        newPassword.grabFocus();
	        	        btnConectar.setSize(170, 23);
	        	        btnConectar.setText("Cambiar contraseña");
	        	        f.repaint();
	        	        f.revalidate();
						f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

	        		}
	        		System.out.println("logged " + logged);
	        		
	        		if (logged == -1) {
	        			System.out.println("Error...");
						JOptionPane.showMessageDialog(null, "Contraseña incorrecta.");

	        		}
//					System.out.println("Salted hash " +temp );
//					boolean temp2 = passwordSHA.check("123", temp);
//					System.out.println("Checked " + temp2);
        			
        			
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        		f.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
//        		
        	}
		
	private void createMenuBar() {

        JMenuBar menuBar = new JMenuBar();
    	URL url = GUI.class.getResource("/exit.png");
        ImageIcon exitIcon = new ImageIcon(url);

        JMenu fileMenu = new JMenu("Administrador");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem eMenuItem = new JMenuItem("Exit", exitIcon);
        Action exitAction = new AbstractAction("Exit") {
        	 
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Exit...");
                //System.exit(0);
            }
        };
         
        exitAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));
        
        eMenuItem.setAction(exitAction);
        eMenuItem.setIcon(exitIcon);
        
        eMenuItem.setToolTipText("Exit application");
        eMenuItem.addActionListener((event) -> System.exit(0));
        
        URL urlAddUser = GUI.class.getResource("/adduser.png");
        ImageIcon addUserIcon = new ImageIcon(urlAddUser);
        JMenuItem userMenuItem = new JMenuItem("Crear Usuario", addUserIcon);
        userMenuItem.setToolTipText("Añadir usuario");
        userMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				/*String code = JOptionPane.showInputDialog(frame,
						"Introduzca la contraseña de administrador ",
						"Secret code needed (title)", JOptionPane.WARNING_MESSAGE);
				*/
				JPasswordField pwd = new JPasswordField(10);
				JOptionPane.showConfirmDialog(null, pwd,"Enter Password",JOptionPane.OK_CANCEL_OPTION);
				db.connect(); 
        		int logged = db.login("admin", String.valueOf(pwd.getPassword()));
        		db.close();
        		if (logged == -1) {
        			return;
        		}
        		
        		if (logged == 1) {
    				new addUser();
        		}
        		
//				if (!(new String(pwd.getPassword()).equals("admin1234"))) {
//					return;
//				}
			
			}
		});
        
        
        URL urlChangePasswordAdmin = GUI.class.getResource("/adduser.png");
        ImageIcon changePasswordAdminIcon = new ImageIcon(urlChangePasswordAdmin);
        JMenuItem changePasswordAdminItem = new JMenuItem("Cambiar contraseña administrador", changePasswordAdminIcon);
        changePasswordAdminItem.setToolTipText("Cambiar contraseña administrador");
        changePasswordAdminItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				/*String code = JOptionPane.showInputDialog(frame,
						"Introduzca la contraseña de administrador ",
						"Secret code needed (title)", JOptionPane.WARNING_MESSAGE);
				*/
				JPasswordField pwd = new JPasswordField(10);
				JOptionPane.showConfirmDialog(null, pwd,"Enter Password",JOptionPane.OK_CANCEL_OPTION);
				db.connect(); 
        		int logged = db.login("admin", String.valueOf(pwd.getPassword()));
        		db.close();
        		if (logged == -1) {
        			return;
        		}
        		
        		if (logged == 1) {
    				new changePasswordAdmin();
        		}
			
			}
		});
        
        Action addAction = new AbstractAction("Crear usuario") {
       	 
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Exit...");
            }
        };
         
        addAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        
        userMenuItem.setAction(addAction);
        userMenuItem.setIcon(addUserIcon);
        

        
        URL urlEditUser = GUI.class.getResource("/editUser.png");
        ImageIcon editUserIcon = new ImageIcon(urlEditUser);
        JMenuItem editMenuItem = new JMenuItem("Reset password", editUserIcon);
        editMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JPasswordField pwd = new JPasswordField(10);
			    JOptionPane.showConfirmDialog(null, pwd,"Enter Password",JOptionPane.OK_CANCEL_OPTION);
			    db.connect(); 
        		int logged = db.login("admin", String.valueOf(pwd.getPassword()));
        		db.close();
        		if (logged == -1) {
        			return;
        		}
        		
        		if (logged == 1) {
    				new listaUsuarios();
        		}
			
			}
		});
        
        URL urlEditNegociado = GUI.class.getResource("/negociado.png");
        ImageIcon editNegociadoIcon = new ImageIcon(urlEditNegociado);
        JMenuItem editNegociadoItem = new JMenuItem("Editar Negociados", editNegociadoIcon);
        editNegociadoItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JPasswordField pwd = new JPasswordField(10);
			    JOptionPane.showConfirmDialog(null, pwd,"Enter Password",JOptionPane.OK_CANCEL_OPTION);
			    db.connect(); 
        		int logged = db.login("admin", String.valueOf(pwd.getPassword()));
        		db.close();
        		if (logged == -1) {
        			return;
        		}
        		
        		if (logged == 1) {
    				new editNegociados();
        		}
			
			}
		});
        
        URL urlCanalEntrada = GUI.class.getResource("/negociado.png");
        ImageIcon canalEntradaIcon = new ImageIcon(urlEditNegociado);
        JMenuItem editCanalEntradaItem = new JMenuItem("Editar Canales de Entrada", canalEntradaIcon);
        editCanalEntradaItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JPasswordField pwd = new JPasswordField(10);
			    JOptionPane.showConfirmDialog(null, pwd,"Enter Password",JOptionPane.OK_CANCEL_OPTION);
			    db.connect(); 
        		int logged = db.login("admin", String.valueOf(pwd.getPassword()));
        		db.close();
        		if (logged == -1) {
        			return;
        		}
        		
        		if (logged == 1) {
    				new editCanalEntrada();
        		}
			
			}
		});
        
        fileMenu.add(userMenuItem);
        fileMenu.add(editMenuItem);
        fileMenu.add(editNegociadoItem);
        fileMenu.add(editCanalEntradaItem);
        fileMenu.add(changePasswordAdminItem);
        fileMenu.add(eMenuItem);

        menuBar.add(fileMenu);

        f.setJMenuBar(menuBar);
    }	
	

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getKeyCode() == 10) {
			conectar();
		}
		
		if (Toolkit.getDefaultToolkit().getLockingKeyState (KeyEvent.VK_CAPS_LOCK ) && password.hasFocus()) {
			password.setBorder(new TitledBorder(null, "Contraseña  - Mayúsculas activa", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			
		}else {
			password.setBorder(new TitledBorder(null, "Contraseña", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		}
		
		if (Toolkit.getDefaultToolkit().getLockingKeyState (KeyEvent.VK_CAPS_LOCK ) && usuario.hasFocus()) {
			usuario.setBorder(new TitledBorder(null, "Usuario  - Mayúsculas activa", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			
		}else {
			usuario.setBorder(new TitledBorder(null, "Usuario", TitledBorder.LEADING, TitledBorder.TOP, null, null));

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
}
