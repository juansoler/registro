package service;

import java.awt.List;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;

import gui.GUI;
import gui.Login;
import models.Usuario;
import models.comentario;
import models.comentarioJefe;
import models.entrada;

public class db {
//	String url = System.getProperty("user.dir") + "\\db.sqlite";

	// String url = "H:\\plmcomd\\COMUN\\CORRESPONDENCIA ENTRADA\\db.sqlite";
	// String url = "C:\\Users\\DGGC\\Downloads\\JUAN\\prueba\\prueba\\db.sqlite";

	Connection connect;

	public void connect() {
		try {
//			 System.out.println(url);

//			System.out.println("connect " + Login.BASE_DIR + "db.sqlite");
			DriverManager.registerDriver(new org.sqlite.JDBC());
			File newFile = new File(Login.BASE_DIR + "db.sqlite");
			if (!newFile.exists()) {
				throw new SQLException();
			}
			GUI.error = false;
			connect = DriverManager.getConnection("jdbc:sqlite:" + Login.BASE_DIR + "db.sqlite");

			if (connect != null) {
//		         System.out.println("Conectado connect()");

				GUI.logDB.setText("Conectado");

			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "No se ha podido conectar a la base de datos. Conéctese a H.");
			System.err.println("No se ha podido conectar a la base de datos\n" + ex.getMessage());
			GUI.error = true;
			GUI.logDB.setText("No se ha podido conectar a la base de datos.");
		}
	}

	public Connection getConnection() {
		return connect;

	}

	public void close() {
		try {
			connect.close();
		} catch (SQLException ex) {
			System.out.println("Closing error " + ex);
		}
	}

	public void saveComentario(comentarioJefe comentario) {
		

		String sql = "UPDATE comentario (entrada_id,  fecha, hora, visto, usuario_id, comentario) VALUES (?,?,?,?,?,?)";
		try {
			PreparedStatement st = connect.prepareStatement(sql);
			st.setString(1, String.valueOf(comentario.getEntrada_id()));
			st.setString(2, comentario.getFecha());
			st.setString(3, comentario.getHora());
			st.setInt(4, comentario.getVisto());
			st.setString(5, String.valueOf(comentario.getUsuario_id()));
			st.setString(6, comentario.getComentario());

			st.execute();
			// close();
		} catch (SQLException ex) {
			close();

			System.err.println(ex.getMessage());
		}

	}

	public int saveEntradaWithId(entrada entrada, int id) {
		// TODO Auto-generated method stub
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement(
					"insert into entrada (asunto, fecha, area, confidencial, urgente, observaciones, numeroEntrada, id) values (?,?,?,?,?,?,?,?)");
			st.setString(1, entrada.getAsunto());
			st.setString(2, entrada.getFecha());
			st.setString(3, entrada.getArea());
			st.setBoolean(4, entrada.isConfidencial());
			st.setBoolean(5, entrada.isUrgente());
			st.setString(6, entrada.getObservaciones());
			st.setString(7, entrada.getNumEntrada());
			st.setInt(8, id);
			st.execute();

		} catch (SQLException ex) {
			close();

			System.err.println("Save entrada error " + ex.getMessage());
		}

//		try {
//			PreparedStatement st = connect.prepareStatement("select seq from sqlite_sequence where name='entrada'; ");
//			result = st.executeQuery();
//			int entradaInt = result.getInt("seq");
//			for (Map.Entry<Integer, String> entry : Login.CARGOS.entrySet()) {
//				System.out.println(
//						"INSERT INTO COMENTARIO (usuario_posicion, comentario, usuario_id, visto, hora, fecha, entrada_id) VALUES (NULL,'',"
//								+ entry.getKey() + ",0,'--:--','--/--/----',''," + entradaInt + ")");
//				st = connect.prepareStatement(
//						"INSERT INTO COMENTARIO (usuario_posicion, comentario, usuario_id, visto, hora, fecha, entrada_id) VALUES (NULL,'',"
//								+ entry.getKey() + ",0,'--:--','--/--/----'," + entradaInt + ")");
//				st.execute();
//			}
//			return entradaInt;
//		} catch (SQLException ex) {
//			close();
//			System.err.println("Save entrada error 2 " + ex.getMessage());
//		}

		return -1;

	}

	public int saveEntrada(entrada entrada) {
		// TODO Auto-generated method stub
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement(
					"insert into entrada (asunto, fecha, area, confidencial, urgente, observaciones, numeroEntrada) values (?,?,?,?,?,?,?)");
			st.setString(1, entrada.getAsunto());
			st.setString(2, entrada.getFecha());
			st.setString(3, entrada.getArea());
			st.setBoolean(4, entrada.isConfidencial());
			st.setBoolean(5, entrada.isUrgente());
			st.setString(6, entrada.getObservaciones());
			st.setString(7, entrada.getNumEntrada());

			st.execute();

		} catch (SQLException ex) {
			close();

			System.err.println("Save entrada error " + ex.getMessage());
		}

		try {
			PreparedStatement st = connect.prepareStatement("select seq from sqlite_sequence where name='entrada'; ");
			result = st.executeQuery();
			int entradaInt = result.getInt("seq");
			
//			System.out.println("select * from canalesEntrada where nombre='"+ entrada.getCanalEntrada() + "' ");
//			st = connect.prepareStatement("select * from canalesEntrada where nombre='"+ entrada.getCanalEntrada() + "' ");
//			result = st.executeQuery();
//			int canalEntradaId = result.getInt("id");

			System.out.println(
					"INSERT INTO entrada_canales (entrada_id, canalEntrada) VALUES ("+entradaInt+ ", '" + entrada.getCanalEntrada() + "')");
			st = connect.prepareStatement(
					"INSERT INTO entrada_canales (entrada_id, canalEntrada) VALUES ("+entradaInt+ ", '" + entrada.getCanalEntrada() + "')");
			st.execute();
			
//			for (Map.Entry<Integer, String> entry : Login.CARGOS.entrySet()) {
//				System.out.println(
//						"INSERT INTO COMENTARIO (usuario_posicion, comentario, usuario_id, visto, hora, fecha, entrada_id) VALUES (NULL,'',"
//								+ entry.getKey() + ",0,'--:--','--/--/----',''," + entradaInt + ")");
//				st = connect.prepareStatement(
//						"INSERT INTO COMENTARIO (usuario_posicion, comentario, usuario_id, visto, hora, fecha, entrada_id) VALUES (NULL,'',"
//								+ entry.getKey() + ",0,'--:--','--/--/----'," + entradaInt + ")");
//				st.execute();
//			}
			return entradaInt;
		} catch (SQLException ex) {
			close();
			System.err.println("Save entrada error 2 " + ex.getMessage());
		}

		return -1;

	}

//	public ResultSet mostrarEntradasCoronel(String date) {
//		ResultSet result = null;
//		try {
//			// PreparedStatement st = connect.prepareStatement("select * from entrada where
//			// fecha = '" + date + "'");
//			PreparedStatement st = connect.prepareStatement(
//					"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id WHERE entrada.Fecha = '"
//							+ date + "'");
//			result = st.executeQuery();
//
//		} catch (SQLException ex) {
//			close();
//
//			System.err.println("mostrarEntrdasCoronel error " + ex.getMessage());
//		}
//		return result;
//	}

	public ResultSet mostrarEntradasPorFecha(String date) {
		System.out.println("mostrarEntradas");
		ResultSet result = null;
		try {
			// PreparedStatement st = connect.prepareStatement("select * from entrada where
			// fecha = '" + date + "' and soloJefe1 = '0'");
			PreparedStatement st = connect.prepareStatement(
					"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id WHERE entrada.Fecha = '"
							+ date + "' and soloJefe1 = '0'");

			result = st.executeQuery();

		} catch (SQLException ex) {
			close();

			System.err.println("mostrarEntradasPorFecha " + ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasPorFechaNegociado(String date, String area) {

		ResultSet result = null;
		try {
			// PreparedStatement st = connect.prepareStatement("select * from entrada where
			// fecha = '" + date + "' and Area = '" + area + "' and soloJefe1 = '0'");
			PreparedStatement st = connect.prepareStatement(
					"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id WHERE entrada.Fecha = '"
							+ date + "' and Area = '" + area + "' and soloJefe1 = '0'");

			result = st.executeQuery();

		} catch (SQLException ex) {
			close();

			System.err.println("mostrarEntradasPorFechaNegociado " + ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasPorFechaYNegociado(String date, Usuario usuario, String area, String categoria) {

		System.out.println("mostrarEntradasPorFechaYNegociado categoria " + categoria);
		ResultSet result = null;
		
		
		

		try {
			
//			if (area.equals("Todos")) {
//				area = "";
//			}
			
			if (categoria.equals("Todas")) {
				
				categoria = "";

				
			}
			
			if (area.equals("Todos")) {
				area = "";
			}
			
//			System.out.println(
//			  "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//			+ "LEFT JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//			+ "LEFT JOIN destinatario ON entrada.id = destinatario.entrada_id "
//			+ "LEFT JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//			+ "LEFT JOIN entrada_canales ON entrada.id = entrada_canales.entrada_id " 
//			+ "WHERE entrada.Fecha = '"
//			+ date + "' " + "AND (CATEGORIA_ENTRADA.CATEGORIA LIKE '%" + categoria + "%' "
//			+ "OR CATEGORIA_ENTRADA.CATEGORIA IS NULL) "
//			+ "AND (destinatario.role LIKE '%" + area + "%' "
//			+ "OR destinatario.role LIKE '%" + usuario.getRole() + "%') "
//			
////			+ "UNION "
////			+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado FROM entrada "
////			+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
////			+ "INNER JOIN destinatario ON entrada.id = destinatario.entrada_id "
////			+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
////			+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.Fecha = '" + date + "' "
////			+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' "
////			+ "AND destinatario.role_id = '" + usuario.getRole_id() + "' " 
//			
//			+ "");
	
	
	System.out.println("SELECT * FROM (SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada \r\n" + 
			"LEFT JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id\r\n" + 
			"LEFT JOIN destinatario ON entrada.id = destinatario.entrada_id \r\n" + 
			"LEFT JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID \r\n" + 
			"LEFT JOIN entrada_canales ON entrada.id = entrada_canales.entrada_id \r\n" + 
			"WHERE entrada.Fecha = '"+date+"' AND (CATEGORIA_ENTRADA.CATEGORIA LIKE '%"+categoria+"%' OR CATEGORIA_ENTRADA.CATEGORIA IS NULL) AND destinatario.role IN ('"+usuario.getRole()+"'))\r\n" + 
			"WHERE Area LIKE '%"+area+"%'\r\n" + 
			"");
//	PreparedStatement st = connect.prepareStatement(
//					  "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//					+ "LEFT JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//					+ "LEFT JOIN destinatario ON entrada.id = destinatario.entrada_id "
//					+ "LEFT JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//					+ "LEFT JOIN entrada_canales ON entrada.id = entrada_canales.entrada_id " 
//					+ "WHERE entrada.Fecha = '"
//					+ date + "' " + "AND (CATEGORIA_ENTRADA.CATEGORIA LIKE '%" + categoria + "%' "
//					+ "OR CATEGORIA_ENTRADA.CATEGORIA IS NULL) "
//					+ "AND (destinatario.role LIKE '%" + area + "%' "
//					+ "OR destinatario.role LIKE '%" + usuario.getRole() + "%') "
//					
////					+ "UNION "
////					+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado FROM entrada "
////					+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
////					+ "INNER JOIN destinatario ON entrada.id = destinatario.entrada_id "
////					+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
////					+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.Fecha = '" + date + "' "
////					+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' "
////					+ "AND destinatario.role_id = '" + usuario.getRole_id() + "' " 
//					
//					+ "");
	PreparedStatement st = connect.prepareStatement("SELECT * FROM (SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada \r\n" + 
			"LEFT JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id\r\n" + 
			"LEFT JOIN destinatario ON entrada.id = destinatario.entrada_id \r\n" + 
			"LEFT JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID \r\n" + 
			"LEFT JOIN entrada_canales ON entrada.id = entrada_canales.entrada_id \r\n" + 
			"WHERE entrada.Fecha = '"+date+"' AND (CATEGORIA_ENTRADA.CATEGORIA LIKE '%"+categoria+"%' OR CATEGORIA_ENTRADA.CATEGORIA IS NULL) AND destinatario.role IN ('"+usuario.getRole()+"'))\r\n" + 
			"WHERE Area LIKE '%"+area+"%'\r\n" + 
			"");
	
	
	result = st.executeQuery();
	
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		try {
//			if (usuario.nombre_jefe == null || usuario.nombre_jefe.equals("")) {
//				if (categoria.equals("Todas")) {
//					System.out.println("mostrarEntradasPorFechaYNegociado1 cargeoria " + categoria);
//					System.out.println(
//							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + "AND entrada.Fecha = '"
//									+ date + "' " + "UNION "
//									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.Fecha = '" + date + "' "
//									+ "");
//					PreparedStatement st = connect.prepareStatement(
//							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + "AND entrada.Fecha = '"
//									+ date + "' " + "UNION "
//									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.Fecha = '" + date + "' "
//									+ "");
//					result = st.executeQuery();
//				} else {
//					System.out.println("mostrarEntradasPorFechaYNegociado2 categoria " + categoria);
//
//					System.out.println(
//							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//									+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + "AND entrada.Fecha = '"
//									+ date + "' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
//									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.Fecha = '" + date + "' "
//									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");
//					PreparedStatement st = connect.prepareStatement(
//							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//									+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + "AND entrada.Fecha = '"
//									+ date + "' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
//									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.Fecha = '" + date + "' "
//									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");
//
//					result = st.executeQuery();
//				}
//			} else {
//				if (categoria.equals("Todas")) {
//					System.out.println("mostrarEntradasPorFechaYNegociado3 + categoria " + categoria);
//
//					System.out.println(
//							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = '"
//									+ usuario.nombre_jefe + "' " + "AND destinatarios.negociado= '" + area + "' " + ""
//									+ "UNION "
//									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = 'Todos'"
//									+ "AND destinatarios.negociado= '" + area + "' " + "");
//					PreparedStatement st = connect.prepareStatement(
//							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = '"
//									+ usuario.nombre_jefe + "' " + "AND destinatarios.negociado= '" + area + "' " + ""
//									+ "UNION "
//									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = 'Todos' "
//									+ "AND destinatarios.negociado= '" + area + "' " + "UNION "
//									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = 'Todos' "
//									+ "AND destinatarios.negociado= 'Todos' " + "");
//					result = st.executeQuery();
//				} else {
//					System.out.println("mostrarEntradasPorFechaYNegociado4 + categoria " + categoria);
//					PreparedStatement st = connect.prepareStatement(
//							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = '"
//									+ usuario.nombre_jefe + "' " + "AND destinatarios.negociado= '" + area + "' " + ""
//									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
//									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = 'Todos' "
//									+ "AND destinatarios.negociado= '" + area + "' "
//									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
//									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = 'Todos' "
//									+ "AND destinatarios.negociado= 'Todos' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '"
//									+ categoria + "'");
//
//					result = st.executeQuery();
//				}
//			}
//
//		} catch (SQLException ex) {
//			close();
//
//			System.err.println(ex.getMessage());
//		}

		return result;
	}
	
	
	public ResultSet search(String date, Usuario usuario, String area, String categoria, String word, String tipoBusqueda) {

		System.out.println("search categoria " + categoria);
		System.out.println("word "+ word);
		ResultSet result = null;
		
			

		try {
			

			
			if (categoria.equals("Todas")) {
				
				categoria = "";

				
			}
			
			if (area.equals("Todos")) {
				area = "";
			}
			
			if (!word.isEmpty()) {
				date = "";
			}
			
			if (word.equals("*")) {
				word = "";
			}
			
			
			if (tipoBusqueda.equals("NUM. ENTRADA")) {
				tipoBusqueda = "numeroEntrada";
			}
	
	
	System.out.println("SELECT * FROM (SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada, numeroEntrada FROM entrada " + 
			"LEFT JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id " + 
			"LEFT JOIN destinatario ON entrada.id = destinatario.entrada_id " + 
			"LEFT JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID " + 
			"LEFT JOIN entrada_canales ON entrada.id = entrada_canales.entrada_id " + 
			"WHERE entrada.Fecha LIKE '%"+date+"%' AND (CATEGORIA_ENTRADA.CATEGORIA LIKE '%"+categoria+"%' OR CATEGORIA_ENTRADA.CATEGORIA IS NULL) AND destinatario.role IN ('"+usuario.getRole()+"')) " + 
			"WHERE Area LIKE '%"+area+"%' " + 
			"AND "+tipoBusqueda+" LIKE '%"+word+"%'" +
			"");

	PreparedStatement st = connect.prepareStatement("SELECT * FROM (SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada, numeroEntrada FROM entrada " + 
			"LEFT JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id " + 
			"LEFT JOIN destinatario ON entrada.id = destinatario.entrada_id " + 
			"LEFT JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID " + 
			"LEFT JOIN entrada_canales ON entrada.id = entrada_canales.entrada_id " + 
			"WHERE entrada.Fecha LIKE '%"+date+"%' AND (CATEGORIA_ENTRADA.CATEGORIA LIKE '%"+categoria+"%' OR CATEGORIA_ENTRADA.CATEGORIA IS NULL) AND destinatario.role IN ('"+usuario.getRole()+"')) " + 
			"WHERE Area LIKE '%"+area+"%' " + 
			"AND "+tipoBusqueda+" LIKE '%"+word+"%'" +
			"");
	
	
	result = st.executeQuery();
	
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		


		return result;
	}
	
	
	public ResultSet pendienteVer(Usuario usuario, String area, String categoria) {

		System.out.println("pendienteVer categoria " + categoria);
		ResultSet result = null;

		try {
			

			
			if (categoria.equals("Todas")) {
				
				categoria = "";
				
			}
			
			if (area.equals("Todos")) {
				area = "";
			}
			
	System.out.println("SELECT * FROM (SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada, numeroEntrada FROM entrada " + 
			"LEFT JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id " + 
			"LEFT JOIN destinatario ON entrada.id = destinatario.entrada_id " + 
			"LEFT JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID " + 
			"LEFT JOIN entrada_canales ON entrada.id = entrada_canales.entrada_id " + 
			"WHERE (CATEGORIA_ENTRADA.CATEGORIA LIKE '%"+categoria+"%' OR CATEGORIA_ENTRADA.CATEGORIA IS NULL) AND destinatario.role IN ('"+usuario.getRole()+"')) " + 
			"WHERE Area LIKE '%"+area+"%' "
			+ "AND (visto IS NULL OR visto = 0) " + 
			"");

	PreparedStatement st = connect.prepareStatement("SELECT * FROM (SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada, numeroEntrada FROM entrada " + 
			"LEFT JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id " + 
			"LEFT JOIN destinatario ON entrada.id = destinatario.entrada_id " + 
			"LEFT JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID " + 
			"LEFT JOIN entrada_canales ON entrada.id = entrada_canales.entrada_id " + 
			"WHERE (CATEGORIA_ENTRADA.CATEGORIA LIKE '%"+categoria+"%' OR CATEGORIA_ENTRADA.CATEGORIA IS NULL) AND destinatario.role IN ('"+usuario.getRole()+"')) " + 
			"WHERE Area LIKE '%"+area+"%' "
			+ "AND (visto IS NULL OR visto = 0) " + 
			"");
	
	
	result = st.executeQuery();
	
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		


		return result;
	}
	
	public ResultSet pendienteTramitar(Usuario usuario, String area, String categoria) {

		System.out.println("pendienteTramitar categoria " + categoria);
		ResultSet result = null;
		
			

		try {
			

			
			if (categoria.equals("Todas")) {
				
				categoria = "";

				
			}
			
			if (area.equals("Todos")) {
				area = "";
			}
			
			
			
			
			
			
			
	
	
	System.out.println("SELECT * FROM (SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada, numeroEntrada FROM entrada " + 
			"LEFT JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id " + 
			"LEFT JOIN destinatario ON entrada.id = destinatario.entrada_id " + 
			"LEFT JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID " + 
			"LEFT JOIN entrada_canales ON entrada.id = entrada_canales.entrada_id " + 
			"WHERE entrada.Fecha LIKE '%%' AND (CATEGORIA_ENTRADA.CATEGORIA LIKE '%"+categoria+"%' OR CATEGORIA_ENTRADA.CATEGORIA IS NULL) AND destinatario.role IN ('"+usuario.getRole()+"')) " + 
			"WHERE Area LIKE '%"+area+"%' " +
			"AND tramitado = 0"+
			"");

	PreparedStatement st = connect.prepareStatement("SELECT * FROM (SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada, numeroEntrada FROM entrada " + 
			"LEFT JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id " + 
			"LEFT JOIN destinatario ON entrada.id = destinatario.entrada_id " + 
			"LEFT JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID " + 
			"LEFT JOIN entrada_canales ON entrada.id = entrada_canales.entrada_id " + 
			"WHERE entrada.Fecha LIKE '%%' AND (CATEGORIA_ENTRADA.CATEGORIA LIKE '%"+categoria+"%' OR CATEGORIA_ENTRADA.CATEGORIA IS NULL) AND destinatario.role IN ('"+usuario.getRole()+"')) " + 
			"WHERE Area LIKE '%"+area+"%' " + 
			"AND tramitado = 0"+
			"");
	
	
	result = st.executeQuery();
	
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		


		return result;
	}


//	public ResultSet mostrarEntradasSinConfidencial(String date, String role, String busqueda) {
//
//		ResultSet result = null;
//		try {
//			if (busqueda.equals("Todos")) {
//				System.out.println("mostrarEntradasSinConfidencial TODOS");
//
////				PreparedStatement st = connect.prepareStatement("select * from entrada where area = 'Todos' and fecha = '" + date
////						+ "' and soloJefe1 = '0' and confidencial = '0' UNION select * from entrada where fecha = '" + date
////						+ "' and area='" + role + "' and confidencial = '1'");
////				PreparedStatement st = connect.prepareStatement("select * from entrada where area = 'Todos' and fecha = '" + date
////						+ "' and soloJefe1 = '0' and confidencial = '0'");
//				PreparedStatement st = connect.prepareStatement(
//						"select entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada from entrada "
//								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id"
//								+ " INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//								+ "AND entrada.Fecha = '" + date + "' " + "AND entrada.soloJefe1 = 0 "
//								+ "AND entrada.confidencial = 0 " + "AND destinatarios.negociado = 'Todos' ");
//
//				result = st.executeQuery();
//
//			} else {
//				System.out.println("mostrarEntradasSinConfidencial else");
//
////				PreparedStatement st = connect.prepareStatement("select * from entrada where fecha = '" + date
////						+ "' and soloJefe1 = '0' and area='"+ role +"' and confidencial = '0' UNION select * from entrada where fecha = '" + date
////						+ "' and area='" + role + "' and confidencial = '1'");
////				PreparedStatement st = connect.prepareStatement("select * from entrada where fecha = '" + date
////						+ "' and soloJefe1 = '0' and area='"+ role +"'");
//
////				PreparedStatement st = connect.prepareStatement("select id, asunto, fecha, area, observaciones, "
////						+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
////						+ "soloJefe1, Tramitado from entrada "
////						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
////						+ "AND entrada.fecha = '"+date+ "' "
////						+ "AND entrada.soloJefe1 = 0 "
////						+ "AND entrada.confidencial = 0 "
////						+ "AND destinatarios.negociado = '"+role+"' ");
//				PreparedStatement st = connect.prepareStatement(
//						"select entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada from entrada "
//								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id"
//								+ " INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//								+ "AND entrada.Fecha = '" + date + "' " + "AND entrada.soloJefe1 = 0 "
//								+ "AND entrada.confidencial = 0 " + "AND destinatarios.negociado = '" + role + "' ");
//				result = st.executeQuery();
//
//			}
//
//		} catch (SQLException ex) {
//			close();
//
//			System.err.println("mostrarEntradasSinConfidencial " + ex.getMessage());
//		}
//		return result;
//	}

//	public ResultSet mostrarEntradasSinConfidencialPorNegociado(String date, String area, String role) {
//
//		ResultSet result = null;
//		try {
//			if (area.equals(role)) {
////				PreparedStatement st = connect.prepareStatement("select * from entrada where area = '" + area
////						+ "' and soloJefe1 = '0' and fecha = '" + date
////						+ "' and confidencial = '0' UNION select * from entrada where fecha = '" + date
////						+ "' and soloJefe1 = '0' and area='" + role + "' and confidencial = '1'"
////						+ "UNION select * from entrada where area='Varios negociados' and soloJefe1 = '0' and fecha ='"
////						+ date + "'");
////				PreparedStatement st = connect.prepareStatement("select * from entrada where area = '" + area
////						+ "' and soloJefe1 = '0' and fecha = '" + date
////						+ "' and confidencial = '0' UNION select * from entrada where fecha = '" + date
////						+ "' and soloJefe1 = '0' and area='" + role + "' and confidencial = '1'" 
////						+ "UNION select * from entrada where area='Todos' and soloJefe1 = '0' and fecha ='"
////						+ date + "'");
//
////				System.out.println("select id, asunto, fecha, area, observaciones, "
////						+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
////						+ "soloJefe1, Tramitado from entrada "
////						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
////						+ "AND entrada.fecha = '"+date+ "' "
////						+ "AND entrada.soloJefe1 = 0 "
////						+ "AND entrada.confidencial = 0 "
////						+ "AND destinatarios.negociado = '"+role+"'"  
////						
////						+ "UNION select id, asunto, fecha, area, observaciones, "
////						+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
////						+ "soloJefe1, Tramitado from entrada "
////						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
////						+ "AND entrada.fecha = '"+date+ "' "
////						+ "AND entrada.soloJefe1 = 0 "
////						+ "AND entrada.confidencial = 0 "
////						+ "AND destinatarios.negociado = 'Todos'");
//
////				PreparedStatement st = connect.prepareStatement("select id, asunto, fecha, area, observaciones, "
////						+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
////						+ "soloJefe1, Tramitado from entrada "
////						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
////						
////						+ "AND entrada.fecha = '"+date+ "' "
////						+ "AND entrada.soloJefe1 = 0 "
////						+ "AND entrada.confidencial = 0 "
////						+ "AND destinatarios.negociado = '"+role+"'"
//// 
////						+ "UNION select id, asunto, fecha, area, observaciones, "
////						+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
////						+ "soloJefe1, Tramitado from entrada "
////						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
////						
////						+ "AND entrada.fecha = '"+date+ "' "
////						+ "AND entrada.soloJefe1 = 0 "
////						+ "AND entrada.confidencial = 0 "
////						+ "AND destinatarios.negociado = 'Todos'");
//
//				System.out.println(
//						"select entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada from entrada "
//								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//
//								+ "AND entrada.fecha = '" + date + "' " + "AND entrada.soloJefe1 = 0 "
//								+ "AND entrada.confidencial = 0 " + "AND destinatarios.negociado = '" + role + "' "
//
//								+ "UNION select entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada from entrada "
//								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//
//								+ "WHERE entrada.fecha = '" + date + "' " + "AND entrada.soloJefe1 = 0 "
//								+ "AND entrada.confidencial = 0 " + "AND destinatarios.negociado = 'Todos'");
//
//				PreparedStatement st = connect.prepareStatement(
//						"select entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada from entrada "
//								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//
//								+ "WHERE entrada.fecha = '" + date + "' " + "AND entrada.soloJefe1 = 0 "
//								+ "AND entrada.confidencial = 0 " + "AND destinatarios.negociado = '" + role + "' "
//
//								+ "UNION select entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada from entrada "
//								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//
//								+ "WHERE entrada.fecha = '" + date + "' " + "AND entrada.soloJefe1 = 0 "
//								+ "AND entrada.confidencial = 0 " + "AND destinatarios.negociado = 'Todos'");
//
//				result = st.executeQuery();
//
//			} else {
////				PreparedStatement st = connect.prepareStatement("select * from entrada where area = '" + area
////						+ "' and soloJefe1 = '0' and fecha = '" + date
////						+ "' and confidencial = '0' UNION select * from entrada where area='Varios Negociados' and soloJefe1 = '0' and fecha ='"
////						+ date + "'");
////				PreparedStatement st = connect.prepareStatement("select * from entrada where area='Todos' and soloJefe1 = '0' and fecha ='"
////						+ date + "'");
//				PreparedStatement st = connect.prepareStatement("select * from entrada where asunto = 'NULL'");
//
//				result = st.executeQuery();
//
//			}
//		} catch (SQLException ex) {
//			close();
//
//			System.err.println("mostrarEntradasSinConfidencialPorNegociado " + ex.getMessage());
//		}
//		close();
//
//		return result;
//	}

	public boolean entradaIsConfidencial(int id) {
		boolean result = false;
		try {
			PreparedStatement st = connect.prepareStatement("select * from entrada where id = '" + id + "'");
			ResultSet temp = st.executeQuery();

			result = temp.getBoolean("Confidencial");
		} catch (SQLException ex) {
			close();

			System.err.println("entradaIsConfidencial " + ex.getMessage());
		}

		return result;
	}

	public ResultSet mostrarEntradas() {
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement("select * from entrada");
			result = st.executeQuery();

		} catch (SQLException ex) {
			close();

			System.err.println("mostrarEntradas  " + ex.getMessage());
		}

		return result;
	}

	public ResultSet mostrarUsuarios() {
		ResultSet result = null;
		System.out.println("mostrarUsuarios");
		try {
			PreparedStatement st = connect.prepareStatement("select user.id AS ID, user.user AS 'Nombre usuario', usuario_role.role AS Negociado, usuario_role.permiso AS 'Permiso Leer/Escribir' from user "
					+ "LEFT JOIN usuario_role ON usuario_role.user_id = id\r\n" + 
					"");
			System.out.println("select user.id AS ID, user.user AS 'Nombre usuario', usuario_role.role AS Negociado, usuario_role.permiso AS 'Permiso Leer/Escribir' from user "
					+ "LEFT JOIN usuario_role ON usuario_role.user_id = id\r\n" + 
					"");
			
			result = st.executeQuery();
//			st.close();
		} catch (SQLException ex) {
			close();

			System.err.println("mostrarEntradas  " + ex.getMessage());
		}

		return result;
	}
	
	public LinkedHashMap<String, String> mostrarUsuariosLinkedHashMap() {
		ResultSet result = null;
		LinkedHashMap<String, String> cargos = new LinkedHashMap<String, String>();
		try {
			PreparedStatement st = connect.prepareStatement("SELECT * FROM user "
					+ "LEFT JOIN usuario_role ON usuario_role.user_id = user.id "
					+ "WHERE isJefe = '1'"
					+ "");
			result = st.executeQuery();
			while (result.next()) {
				cargos.put(result.getString("user"), result.getString("role"));
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
		return cargos;
	}
	
	public ArrayList<String> mostrarUsuariosHuerfanos() {
		ResultSet result = null;
		 ArrayList<String> usuarios = new  ArrayList<String>();
		try {
			PreparedStatement st = connect.prepareStatement("SELECT * FROM user\r\n" + 
					"LEFT JOIN usuario_role ON usuario_role.user_id = user.id\r\n" + 
					"WHERE usuario_role.user_id IS NULL");
			result = st.executeQuery();
			while (result.next()) {
				usuarios.add(result.getString("user"));
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
		return usuarios;
	}
	
	
	

	public void resetPassword(String usuario) {
		String sql = "UPDATE user SET password = 'reset' " + "WHERE user = ?";
		try {
			PreparedStatement st = connect.prepareStatement(sql);
			st.setString(1, usuario);
			st.execute();

		} catch (SQLException ex) {
			close();

			System.err.println(ex.getMessage());
		}

	}

	public void deleteUser(String usuario) {
		String sql = "DELETE FROM user WHERE user = ?";
		try {
			PreparedStatement st = connect.prepareStatement(sql);
			st.setString(1, usuario);
			st.execute();

		} catch (SQLException ex) {
			close();

			System.err.println(ex.getMessage());
		}

	}

	public boolean saveUser(String user, String role) {
		int result = -1;
		try {
			PreparedStatement st = connect.prepareStatement("insert into user (user, password) values (?,?)");
			st.setString(1, user);
			st.setString(2, "reset");
			result = st.executeUpdate();
			System.out.println("result save user " + result);
			
			st = connect.prepareStatement("select * from user where user = '" + user + "'");
			ResultSet result2 = st.executeQuery();
			
			int userId = result2.getInt("id"); 
			
			
			st = connect.prepareStatement("insert into usuario_role (role, user_id, permiso, isJefe) values (?,?,?,?)");
			st.setString(1, role);
			st.setInt(2, userId);
			st.setInt(3, 0);
			st.setInt(4, 0);
			result = st.executeUpdate();


			
			if (result == 1) {
				return true;
			}else {
				return false;
			}
			
		} catch (SQLException ex) {
			close();
			
			System.err.println("saveUser error " + ex.getMessage());
			return false;
		}

	}

	public entrada mostrarEntrada(int id) {
		ResultSet result = null;
		entrada temp = null;
		System.out.println("Mostrar entrada " + "select * from entrada where id=" + id);
		try {
			PreparedStatement st = connect.prepareStatement("select * from entrada where id=" + id);
			result = st.executeQuery();

			temp = new entrada(result.getString("asunto"), result.getString("fecha"), result.getString("area"),
					result.getBoolean("confidencial"), result.getBoolean("urgente"));
//			temp.setJefe1(result.getInt("jefe1"));
//			temp.setJefe2(result.getInt("jefe2"));
//			temp.setJefe3(result.getInt("jefe3"));
//			temp.setJefe4(result.getInt("jefe4"));
//			temp.setJefe5(result.getInt("jefe5"));
			temp.setObservaciones(result.getString("observaciones"));
			temp.setTramitado(result.getBoolean("Tramitado"));
			temp.setId(result.getInt("ID"));
//			temp.setCanalEntrada(result.getString("canalEntrada"));
			temp.setNumEntrada(result.getString("numeroEntrada"));
			temp.setTramitadoPor(result.getString("tramitadoPor"));
			temp.comentario = new comentario(result.getInt("ID"));
		} catch (SQLException ex) {
			close();
			System.err.println("mostrarEntrada error " + ex.getMessage());
			ex.printStackTrace();

		}

		return temp;
	}

	public int buscarIdEntradaPorNombreArchivo(String file) {
		ResultSet result = null;
		int temp = 0;
		try {
			PreparedStatement st = connect.prepareStatement("select * from files where file='" + file + "'");
			result = st.executeQuery();

			temp = result.getInt("entrada_id");
		} catch (SQLException ex) {
			close();

			System.err.println("buscarIdEntradaPorNombreArchivo error" + ex.getMessage());
		}

		return temp;
	}

	public comentario mostrarComentario(int id) {
		ResultSet result = null;
		comentario temp = null;

		try {
			// PreparedStatement st = connect.prepareStatement("select * from comentario
			// where entrada_id=" + id);
//			System.out.println(
//					"select JEFES.nombre, entrada.tramitadoPor, COMENTARIO.comentario, COMENTARIO.fecha AS fechaComentario, COMENTARIO.hora AS horaComentario, JEFES.posicion, entrada.id, Asunto, entrada.Fecha, Area, visto, COMENTARIO.usuario_id, Confidencial, tramitado, canalEntrada from entrada "
//							+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//							+ "LEFT OUTER JOIN JEFES ON COMENTARIO.usuario_id = JEFES.ID " + "WHERE entrada.id=" + id
//							+ " ORDER BY posicion");
			
//			PreparedStatement st = connect.prepareStatement(
//					"select JEFES.nombre, entrada.tramitadoPor, COMENTARIO.comentario, COMENTARIO.fecha AS fechaComentario, COMENTARIO.hora AS horaComentario, JEFES.posicion, entrada.id, Asunto, entrada.Fecha, Area, visto, COMENTARIO.usuario_id, Confidencial, tramitado, canalEntrada from entrada "
//							+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//							+ "LEFT OUTER JOIN JEFES ON COMENTARIO.usuario_id = JEFES.ID " + "WHERE entrada.id=" + id
//							+ " ORDER BY posicion");
			System.out.println("select user.user, COMENTARIO.role, COMENTARIO.id AS comentarioId, entrada.tramitadoPor, COMENTARIO.comentario, COMENTARIO.fecha AS fechaComentario, COMENTARIO.hora AS horaComentario, COMENTARIO.usuario_posicion, entrada.id, Asunto, entrada.Fecha, Area, COMENTARIO.visto, COMENTARIO.usuario_id, Confidencial, tramitado, canalEntrada from entrada\r\n" + 
					"LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id\r\n" + 
					"LEFT JOIN entrada_canales ON entrada.id = entrada_canales.entrada_id\r\n" + 
					"LEFT OUTER JOIN user ON user.id = COMENTARIO.usuario_id\r\n" + 
					"WHERE entrada.id= "+id+"\r\n" + 
					"AND user IS NOT NULL "+
					"ORDER BY usuario_posicion");
			PreparedStatement st = connect.prepareStatement("select user.user, COMENTARIO.role, COMENTARIO.id AS comentarioId, entrada.tramitadoPor, COMENTARIO.comentario, COMENTARIO.fecha AS fechaComentario, COMENTARIO.hora AS horaComentario, COMENTARIO.usuario_posicion, entrada.id, Asunto, entrada.Fecha, Area, COMENTARIO.visto, COMENTARIO.usuario_id, Confidencial, tramitado, canalEntrada from entrada\r\n" + 
					"LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id\r\n" + 
					"LEFT JOIN entrada_canales ON entrada.id = entrada_canales.entrada_id\r\n" + 
					"LEFT OUTER JOIN user ON user.id = COMENTARIO.usuario_id\r\n" + 
					"WHERE entrada.id= "+id+"\r\n" + 
					"AND user IS NOT NULL "+
					"ORDER BY usuario_posicion");

			result = st.executeQuery();
			temp = new comentario(id);

			temp.setId(result.getInt("comentarioId"));
			temp.setEntrada_id(result.getInt("id"));
			// public comentarioJefe(int entrada_id, int usuario_id, String nombre, String
			// posicion, String fecha, String hora, String comentario, int visto) {
			System.out.println("ID mostrarComentario " + result.getInt("id"));
			while (result.next()) {
//					temp.addComentarioJefe(new comentarioJefe(Integer.parseInt(result.getString("id")),
//							Integer.parseInt(result.getString("usuario_id")), result.getString("nombre"),
//							result.getString("posicion"),
//							result.getString("fechaComentario") == null ? "01/01/1900"
//									: result.getString("fechaComentario"),
//							result.getString("horaComentario") == null ? "00:00" : result.getString("horaComentario"),
//							result.getString("comentario") == null ? "" : result.getString("comentario"),
//							Integer.parseInt(result.getString("visto"))));
					
//					public comentarioJefe(int entrada_id, int usuario_id, String nombre, int posicion, String fecha, String hora, String comentario, int visto) {
					System.out.println("comentario " +  result.getInt("id"));
					
					temp.addComentarioJefe(new comentarioJefe(id, result.getInt("usuario_id"), result.getString("role"), result.getString("user"), result.getInt("usuario_posicion"), result.getString("fechaComentario") == null ? "01/01/1900"
									: result.getString("fechaComentario"),
							result.getString("horaComentario") == null ? "00:00" : result.getString("horaComentario"),
							result.getString("comentario") == null ? "" : result.getString("comentario"), result.getInt("visto")));
					
				

			}

		} catch (SQLException ex) {
			close();

			System.err.println("mostrarComentario id " + id + " error " + ex.getMessage());
		}

		return temp;
	}

	public DefaultListModel mostrarFiles(int id) {
		ResultSet result = null;
		DefaultListModel resultado = new DefaultListModel();
		try {
			PreparedStatement st = connect.prepareStatement("select * from files where entrada_id=" + id);
			result = st.executeQuery();
			while (result.next()) {
				resultado.addElement(result.getString("id") + "-"
						+ result.getString("file").split("\\\\")[result.getString("file").split("\\\\").length - 1]);
			}
		} catch (SQLException ex) {
			close();

			System.err.println("mostrarFiles error " + ex.getMessage());
		}

		return resultado;
	}

	public boolean deleteFiles(int id) {
		System.out.println("Id" + id);
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement("select * from files where entrada_id=" + id);
			result = st.executeQuery();
			while (result.next()) {
				System.out.println("Eliminando " + result.getString("file"));
				new File(result.getString("file")).delete();
			}

			return true;
		} catch (SQLException ex) {
			close();

			return false;
		}
	}

	public DefaultListModel mostrarFilesAntecedentes(int id) {
		ResultSet result = null;
		DefaultListModel resultado = new DefaultListModel();
		try {
			PreparedStatement st = connect.prepareStatement("select * from antecedentesFiles where entrada_id=" + id);
			result = st.executeQuery();
			while (result.next()) {

				resultado.addElement(result.getString("id") + "-"
						+ result.getString("file").split("\\\\")[result.getString("file").split("\\\\").length - 1]);
			}
		} catch (SQLException ex) {
			close();

			System.err.println("mostrarFilesAntecedentes error " + ex.getMessage());
		}

		return resultado;
	}

	public void saveFile(String absolutePath, int idEntrada) {
		try {
			PreparedStatement st = connect.prepareStatement("insert into files (file, entrada_id) values (?,?)");
			st.setString(1, absolutePath);
			st.setInt(2, idEntrada);
			st.execute();

		} catch (SQLException ex) {
			close();

			System.err.println("saveFile error " + ex.getMessage());
		}

	}

	public void saveFileAntecedentes(String absolutePath, int idEntrada) {
		try {
			PreparedStatement st = connect
					.prepareStatement("insert into antecedentesFiles (file, entrada_id) values (?,?)");
			st.setString(1, absolutePath);
			st.setInt(2, idEntrada);
			st.execute();

		} catch (SQLException ex) {
			close();

			System.err.println("saveFileAntecedentes error " + ex.getMessage());
		}

	}

	public void saveAntecedentesEntrada(Integer idAntecedente, int entradaId) {
		try {
			PreparedStatement st = connect
					.prepareStatement("insert into antecedentesEntrada (idAntecedente, entrada_id) values (?,?)");
			st.setInt(1, (int) idAntecedente);
			st.setInt(2, entradaId);
			st.execute();

		} catch (SQLException ex) {
			close();

			System.err.println("saveAntecedentesEntrada error " + ex.getMessage());
		}

	}

	public void actualizarEntrada(entrada entrada) {
		// TODO Auto-generated method stub
		
		String sql = "UPDATE entrada SET confidencial = ? ," + "urgente = ? ," + "observaciones = ? ," + "asunto = ? ,"
				+ "area = ?," + "tramitado = ? ," + "numeroEntrada = ? , " + "tramitadoPor = ? "
				+ " WHERE id = ?";
		try {
//			System.out.println("actualizar Entrada");
			PreparedStatement st = connect.prepareStatement(sql);
			st.setBoolean(1, entrada.isConfidencial());
			st.setBoolean(2, entrada.isUrgente());
			st.setString(3, entrada.getObservaciones());
			st.setString(4, entrada.getAsunto());
			st.setString(5, entrada.getArea());
			st.setBoolean(6, entrada.isTramitado());
			st.setString(7, entrada.getNumEntrada());
			st.setString(8, entrada.getTramitadoPor());
			st.setInt(9, entrada.getId());

			st.execute();

		} catch (SQLException ex) {
			close();

			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
		
		sql = "UPDATE entrada_canales SET canalEntrada = '"+entrada.getCanalEntrada() + "' WHERE entrada_id = " +  entrada.getId();
		System.out.println(sql);
		try {
			
			PreparedStatement st = connect.prepareStatement(sql);
			

			st.execute();

		} catch (SQLException ex) {
			close();

			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}


	}

	public void actualizarAsuntoEntrada(entrada entrada) {
		String sql = "UPDATE entrada SET asunto = ? " + "WHERE id = ?";
		try {
			PreparedStatement st = connect.prepareStatement(sql);
			st.setString(1, entrada.getAsunto());
			st.setInt(2, entrada.getId());
			st.execute();

		} catch (SQLException ex) {
			close();

			System.err.println(ex.getMessage());
		}

	}

	public void actualizarComentario(comentarioJefe comentario) {

		System.out.println("actualizarComentario ");
		System.out.println("entrada id " + comentario.getEntrada_id());
		boolean exist = false;
		try {
			System.out.println("select * from comentario where entrada_id="
					+ comentario.getEntrada_id() + " AND usuario_id=" + comentario.getUsuario_id());
			PreparedStatement st = connect.prepareStatement("select * from comentario where entrada_id="
					+ comentario.getEntrada_id() + " AND usuario_id=" + comentario.getUsuario_id());
			ResultSet result = st.executeQuery();
			exist = result.next();

		} catch (SQLException ex) {
			close();

			System.err.println(ex.getMessage());
		}
		if (exist) {

			System.out.println("actualizarComentario exists");
			System.out.println(comentario.getComentario());
			String sql = "UPDATE comentario SET comentario = ? , " + "fecha = ? ," + "hora = ? ," + "visto = ? "
					+ "WHERE entrada_id = ? AND usuario_id = ?";
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				st.setString(1, comentario.getComentario());
				st.setString(2, comentario.getFecha());
				st.setString(3, comentario.getHora());
				st.setInt(4, comentario.getVisto());
				st.setString(5, String.valueOf(comentario.getEntrada_id()));
				st.setString(6, String.valueOf(comentario.getUsuario_id()));
				st.execute();

			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
		} else {
			String sql = "INSERT INTO comentario (entrada_id, fecha, hora, visto, usuario_id, comentario, role, usuario_posicion) VALUES (?,?,?,?,?,?,?,?)";
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				st.setString(1, String.valueOf(comentario.getEntrada_id()));
				st.setString(2, comentario.getFecha());
				st.setString(3, comentario.getHora());
				st.setInt(4, comentario.getVisto());
				st.setString(5, String.valueOf(comentario.getUsuario_id()));
				st.setString(6, comentario.getComentario());
				st.setString(7, comentario.getNombreJefe());
				st.setInt(8, comentario.getPosicion());

				st.execute();

			} catch (SQLException ex) {
				close();

				System.err.println(ex.getMessage());
			}
		}

	}

//	public void actualizarComentarioSoloJefe(int idJefe, comentario comentario) {
//		String jefeText = "";
//		String jefeComentario = "";
//		String jefeFecha = "";
//		String jefeFechaText = "";
//		String jefeHora = "";
//		String jefeHoraText = "";
//		switch (idJefe) {
//		case 0:
//			jefeText = "jefe1";
//			jefeFecha = "jefe1fecha";
//			jefeHora = "jefe1hora";
//			jefeComentario = comentario.getJefe1();
//			jefeFechaText = comentario.getJefe1fecha();
//			jefeHoraText = comentario.getJefe1hora();
//			break;
//		case 1:
//			jefeText = "jefe2";
//			jefeFecha = "jefe2fecha";
//			jefeHora = "jefe2hora";
//			jefeComentario = comentario.getJefe2();
//			jefeFechaText = comentario.getJefe2fecha();
//			jefeHoraText = comentario.getJefe2hora();
//			break;
//		case 2:
//			jefeText = "jefe3";
//			jefeFecha = "jefe3fecha";
//			jefeHora = "jefe3hora";
//			jefeComentario = comentario.getJefe3();
//			jefeFechaText = comentario.getJefe3fecha();
//			jefeHoraText = comentario.getJefe3hora();
//			break;
//		case 3:
//			jefeText = "jefe4";
//			jefeFecha = "jefe4fecha";
//			jefeHora = "jefe4hora";
//			jefeComentario = comentario.getJefe4();
//			jefeFechaText = comentario.getJefe4fecha();
//			jefeHoraText = comentario.getJefe4hora();
//			break;
//		case 4:
//			jefeText = "jefe5";
//			jefeFecha = "jefe5fecha";
//			jefeHora = "jefe5hora";
//			jefeComentario = comentario.getJefe5();
//			jefeFechaText = comentario.getJefe5fecha();
//			jefeHoraText = comentario.getJefe5hora();
//			break;
//
//		default:
//			return;
//		}
//
//		boolean exist = false;
//		try {
//			PreparedStatement st = connect
//					.prepareStatement("select * from comentario where entrada_id=" + comentario.getEntrada_id());
//			ResultSet result = st.executeQuery();
//			exist = result.next();
//		} catch (SQLException ex) {
//			System.err.println(ex.getMessage());
//		}
//		if (exist) {
//			String sql = "UPDATE comentario SET " + jefeText + "= ? , " + jefeFecha + "= ? ," + jefeHora + "= ?"
//					+ "WHERE entrada_id = ?";
//			try {
//				PreparedStatement st = connect.prepareStatement(sql);
//				st.setString(1, jefeComentario);
//				st.setString(2, jefeFechaText);
//				st.setString(3, jefeHoraText);
//				st.setInt(4, comentario.getEntrada_id());
//				st.execute();
//			} catch (SQLException ex) {
//				System.err.println(ex.getMessage());
//			}
//		} else {
//			String sql = "INSERT INTO comentario (jefe1, jefe1fecha, jefe1hora, jefe2, jefe2fecha, jefe2hora, jefe3, jefe3fecha, jefe3hora, jefe4, jefe4fecha, jefe4hora, jefe5, jefe5fecha, jefe5hora, entrada_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//			try {
//				PreparedStatement st = connect.prepareStatement(sql);
//				st.setString(1, comentario.getJefe1());
//				st.setString(2, comentario.getJefe1fecha());
//				st.setString(3, comentario.getJefe1hora());
//				st.setString(4, comentario.getJefe2());
//				st.setString(5, comentario.getJefe2fecha());
//				st.setString(6, comentario.getJefe2hora());
//				st.setString(7, comentario.getJefe3());
//				st.setString(8, comentario.getJefe3fecha());
//				st.setString(9, comentario.getJefe3hora());
//				st.setString(10, comentario.getJefe4());
//				st.setString(11, comentario.getJefe4fecha());
//				st.setString(12, comentario.getJefe4hora());
//				st.setString(13, comentario.getJefe5());
//				st.setString(14, comentario.getJefe5fecha());
//				st.setString(15, comentario.getJefe5hora());
//				st.setInt(16, comentario.getEntrada_id());
//				st.execute();
//			} catch (SQLException ex) {
//				System.err.println(ex.getMessage());
//			}
//		}
//
//	}

//	public void actualizarComentarioJefe1(comentario comentario) {
//		boolean exist = false;
//		try {
//			PreparedStatement st = connect
//					.prepareStatement("select * from comentario where entrada_id=" + comentario.getEntrada_id());
//			ResultSet result = st.executeQuery();
//			exist = result.next();
//		} catch (SQLException ex) {
//			System.err.println(ex.getMessage());
//		}
//		if (exist) {
//			String sql = "UPDATE comentario SET jefe1 = ? , " + "jefe1fecha = ? ," + "jefe1hora = ?"
//					+ "WHERE entrada_id = ?";
//			try {
//				PreparedStatement st = connect.prepareStatement(sql);
//				st.setString(1, comentario.getJefe1());
//				st.setString(2, comentario.getJefe1fecha());
//				st.setString(3, comentario.getJefe1hora());
//				st.setInt(4, comentario.getEntrada_id());
//				st.execute();
//			} catch (SQLException ex) {
//				System.err.println(ex.getMessage());
//			}
//		} else {
//			String sql = "INSERT INTO comentario (jefe1, jefe1fecha, jefe1hora, jefe2, jefe2fecha, jefe2hora, jefe3, jefe3fecha, jefe3hora, jefe4, jefe4fecha, jefe4hora, jefe5, jefe5fecha, jefe5hora, entrada_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//			try {
//				PreparedStatement st = connect.prepareStatement(sql);
//				st.setString(1, comentario.getJefe1());
//				st.setString(2, comentario.getJefe1fecha());
//				st.setString(3, comentario.getJefe1hora());
//				st.setString(4, comentario.getJefe2());
//				st.setString(5, comentario.getJefe2fecha());
//				st.setString(6, comentario.getJefe2hora());
//				st.setString(7, comentario.getJefe3());
//				st.setString(8, comentario.getJefe3fecha());
//				st.setString(9, comentario.getJefe3hora());
//				st.setString(10, comentario.getJefe4());
//				st.setString(11, comentario.getJefe4fecha());
//				st.setString(12, comentario.getJefe4hora());
//				st.setString(13, comentario.getJefe5());
//				st.setString(14, comentario.getJefe5fecha());
//				st.setString(15, comentario.getJefe5hora());
//				st.setInt(16, comentario.getEntrada_id());
//				st.execute();
//			} catch (SQLException ex) {
//				System.err.println(ex.getMessage());
//			}
//		}
//
//	}
//
//	public void actualizarComentarioJefe2(comentario comentario) {
//		boolean exist = false;
//		try {
//			PreparedStatement st = connect
//					.prepareStatement("select * from comentario where entrada_id=" + comentario.getEntrada_id());
//			ResultSet result = st.executeQuery();
//			exist = result.next();
//		} catch (SQLException ex) {
//			System.err.println(ex.getMessage());
//		}
//		if (exist) {
//			String sql = "UPDATE comentario SET jefe2 = ? , " + "jefe2fecha = ? ," + "jefe2hora = ?"
//					+ "WHERE entrada_id = ?";
//			try {
//				PreparedStatement st = connect.prepareStatement(sql);
//				st.setString(1, comentario.getJefe2());
//				st.setString(2, comentario.getJefe2fecha());
//				st.setString(3, comentario.getJefe2hora());
//				st.setInt(4, comentario.getEntrada_id());
//				st.execute();
//			} catch (SQLException ex) {
//				System.err.println(ex.getMessage());
//			}
//		} else {
//			String sql = "INSERT INTO comentario (jefe1, jefe1fecha, jefe1hora, jefe2, jefe2fecha, jefe2hora, jefe3, jefe3fecha, jefe3hora, jefe4, jefe4fecha, jefe4hora, jefe5, jefe5fecha, jefe5hora, entrada_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//			try {
//				PreparedStatement st = connect.prepareStatement(sql);
//				st.setString(1, comentario.getJefe1());
//				st.setString(2, comentario.getJefe1fecha());
//				st.setString(3, comentario.getJefe1hora());
//				st.setString(4, comentario.getJefe2());
//				st.setString(5, comentario.getJefe2fecha());
//				st.setString(6, comentario.getJefe2hora());
//				st.setString(7, comentario.getJefe3());
//				st.setString(8, comentario.getJefe3fecha());
//				st.setString(9, comentario.getJefe3hora());
//				st.setString(10, comentario.getJefe4());
//				st.setString(11, comentario.getJefe4fecha());
//				st.setString(12, comentario.getJefe4hora());
//				st.setString(13, comentario.getJefe5());
//				st.setString(14, comentario.getJefe5fecha());
//				st.setString(15, comentario.getJefe5hora());
//				st.setInt(16, comentario.getEntrada_id());
//				st.execute();
//			} catch (SQLException ex) {
//				System.err.println(ex.getMessage());
//			}
//		}
//
//	}
//
//	public void actualizarComentarioJefe3(comentario comentario) {
//		boolean exist = false;
//		try {
//			PreparedStatement st = connect
//					.prepareStatement("select * from comentario where entrada_id=" + comentario.getEntrada_id());
//			ResultSet result = st.executeQuery();
//			exist = result.next();
//		} catch (SQLException ex) {
//			System.err.println(ex.getMessage());
//		}
//		if (exist) {
//			String sql = "UPDATE comentario SET jefe3 = ? , " + "jefe3fecha = ? ," + "jefe3hora = ?"
//					+ "WHERE entrada_id = ?";
//			try {
//				PreparedStatement st = connect.prepareStatement(sql);
//				st.setString(1, comentario.getJefe3());
//				st.setString(2, comentario.getJefe3fecha());
//				st.setString(3, comentario.getJefe3hora());
//				st.setInt(4, comentario.getEntrada_id());
//				st.execute();
//			} catch (SQLException ex) {
//				System.err.println(ex.getMessage());
//			}
//		} else {
//			String sql = "INSERT INTO comentario (jefe1, jefe1fecha, jefe1hora, jefe2, jefe2fecha, jefe2hora, jefe3, jefe3fecha, jefe3hora, jefe4, jefe4fecha, jefe4hora, jefe5, jefe5fecha, jefe5hora, entrada_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//			try {
//				PreparedStatement st = connect.prepareStatement(sql);
//				st.setString(1, comentario.getJefe1());
//				st.setString(2, comentario.getJefe1fecha());
//				st.setString(3, comentario.getJefe1hora());
//				st.setString(4, comentario.getJefe2());
//				st.setString(5, comentario.getJefe2fecha());
//				st.setString(6, comentario.getJefe2hora());
//				st.setString(7, comentario.getJefe3());
//				st.setString(8, comentario.getJefe3fecha());
//				st.setString(9, comentario.getJefe3hora());
//				st.setString(10, comentario.getJefe4());
//				st.setString(11, comentario.getJefe4fecha());
//				st.setString(12, comentario.getJefe4hora());
//				st.setString(13, comentario.getJefe5());
//				st.setString(14, comentario.getJefe5fecha());
//				st.setString(15, comentario.getJefe5hora());
//				st.setInt(16, comentario.getEntrada_id());
//				st.execute();
//			} catch (SQLException ex) {
//				System.err.println(ex.getMessage());
//			}
//		}
//
//	}
//
//	public void actualizarComentarioJefe4(comentario comentario) {
//		boolean exist = false;
//		try {
//			PreparedStatement st = connect
//					.prepareStatement("select * from comentario where entrada_id=" + comentario.getEntrada_id());
//			ResultSet result = st.executeQuery();
//			exist = result.next();
//		} catch (SQLException ex) {
//			System.err.println(ex.getMessage());
//		}
//		if (exist) {
//			String sql = "UPDATE comentario SET jefe4 = ? , " + "jefe4fecha = ? ," + "jefe4hora = ?"
//					+ "WHERE entrada_id = ?";
//			try {
//				PreparedStatement st = connect.prepareStatement(sql);
//				st.setString(1, comentario.getJefe4());
//				st.setString(2, comentario.getJefe4fecha());
//				st.setString(3, comentario.getJefe4hora());
//				st.setInt(4, comentario.getEntrada_id());
//				st.execute();
//			} catch (SQLException ex) {
//				System.err.println(ex.getMessage());
//			}
//		} else {
//			String sql = "INSERT INTO comentario (jefe1, jefe1fecha, jefe1hora, jefe2, jefe2fecha, jefe2hora, jefe3, jefe3fecha, jefe3hora, jefe4, jefe4fecha, jefe4hora, jefe5, jefe5fecha, jefe5hora, entrada_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//			try {
//				PreparedStatement st = connect.prepareStatement(sql);
//				st.setString(1, comentario.getJefe1());
//				st.setString(2, comentario.getJefe1fecha());
//				st.setString(3, comentario.getJefe1hora());
//				st.setString(4, comentario.getJefe2());
//				st.setString(5, comentario.getJefe2fecha());
//				st.setString(6, comentario.getJefe2hora());
//				st.setString(7, comentario.getJefe3());
//				st.setString(8, comentario.getJefe3fecha());
//				st.setString(9, comentario.getJefe3hora());
//				st.setString(10, comentario.getJefe4());
//				st.setString(11, comentario.getJefe4fecha());
//				st.setString(12, comentario.getJefe4hora());
//				st.setString(13, comentario.getJefe5());
//				st.setString(14, comentario.getJefe5fecha());
//				st.setString(15, comentario.getJefe5hora());
//				st.setInt(16, comentario.getEntrada_id());
//				st.execute();
//			} catch (SQLException ex) {
//				System.err.println(ex.getMessage());
//			}
//		}
//
//	}
//
//	public void actualizarComentarioJefe5(comentario comentario) {
//		boolean exist = false;
//		try {
//			PreparedStatement st = connect
//					.prepareStatement("select * from comentario where entrada_id=" + comentario.getEntrada_id());
//			ResultSet result = st.executeQuery();
//			exist = result.next();
//		} catch (SQLException ex) {
//			System.err.println(ex.getMessage());
//		}
//		if (exist) {
//			String sql = "UPDATE comentario SET jefe5 = ? , " + "jefe5fecha = ? ," + "jefe5hora = ?"
//					+ "WHERE entrada_id = ?";
//			try {
//				PreparedStatement st = connect.prepareStatement(sql);
//				st.setString(1, comentario.getJefe5());
//				st.setString(2, comentario.getJefe5fecha());
//				st.setString(3, comentario.getJefe5hora());
//				st.setInt(4, comentario.getEntrada_id());
//				st.execute();
//			} catch (SQLException ex) {
//				System.err.println(ex.getMessage());
//			}
//		} else {
//			String sql = "INSERT INTO comentario (jefe1, jefe1fecha, jefe1hora, jefe2, jefe2fecha, jefe2hora, jefe3, jefe3fecha, jefe3hora, jefe4, jefe4fecha, jefe4hora, jefe5, jefe5fecha, jefe5hora, entrada_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//			try {
//				PreparedStatement st = connect.prepareStatement(sql);
//				st.setString(1, comentario.getJefe1());
//				st.setString(2, comentario.getJefe1fecha());
//				st.setString(3, comentario.getJefe1hora());
//				st.setString(4, comentario.getJefe2());
//				st.setString(5, comentario.getJefe2fecha());
//				st.setString(6, comentario.getJefe2hora());
//				st.setString(7, comentario.getJefe3());
//				st.setString(8, comentario.getJefe3fecha());
//				st.setString(9, comentario.getJefe3hora());
//				st.setString(10, comentario.getJefe4());
//				st.setString(11, comentario.getJefe4fecha());
//				st.setString(12, comentario.getJefe4hora());
//				st.setString(13, comentario.getJefe5());
//				st.setString(14, comentario.getJefe5fecha());
//				st.setString(15, comentario.getJefe5hora());
//				st.setInt(16, comentario.getEntrada_id());
//				st.execute();
//			} catch (SQLException ex) {
//				System.err.println(ex.getMessage());
//			}
//		}
//
//	}

	public boolean eliminarEntrada(int id) {
		int result = -2;
		boolean exist = false;
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM ENTRADA WHERE ID = ?");
			st.setInt(1, id);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("primer try entrada" + ex.getMessage());
		}

		if (result == 1) {
			return true;
		} else
			return false;
	}

	public boolean eliminarComentario(int entradaid) {
		int result = -2;
		boolean exist = false;
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM COMENTARIO WHERE entrada_id = ?");
			st.setInt(1, entradaid);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("primer try comentario" + ex.getMessage());
		}
		if (result == 1) {
			return true;
		} else
			return false;

	}

	public boolean eliminarFile(String id) {
		int result = -2;
		boolean exist = false;
		try {
			System.out.println("Eliminar file " + id);
			PreparedStatement st = connect.prepareStatement("DELETE FROM FILES WHERE id = ?");
			st.setString(1, id.split("-")[0]);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("primer try entrada" + ex.getMessage());
		}

		if (result == 1) {
			return true;
		} else
			return false;
	}

	public boolean eliminarAntecedentesFile(String id) {
		int result = -2;
		boolean exist = false;
		try {
			System.out.println("Eliminar antecedentes file " + id);
			PreparedStatement st = connect.prepareStatement("DELETE FROM antecedentesFiles WHERE id = ?");
			st.setString(1, id.split("-")[0]);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("eliminarAntecedentesFile" + ex.getMessage());
		}

		if (result == 1) {
			return true;
		} else
			return false;
	}

	public boolean eliminarAllFiles(int idEntrada) {
		int result = -2;
		boolean exist = false;
		try {
			System.out.println("Eliminar file " + idEntrada);
			PreparedStatement st = connect.prepareStatement("DELETE FROM FILES WHERE entrada_id = ?");
			st.setInt(1, idEntrada);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("primer try entrada" + ex.getMessage());
		}

		if (result == 1) {
			return true;
		} else
			return false;
	}

	public int login(String user, String password) {
		Password passwordSHA = new Password();
		ResultSet result = null;
		entrada temp = null;
		if (password.equals("reset")) {
			try {
				PreparedStatement st = connect.prepareStatement(
						"select * from user where user='" + user + "' and password='" + password + "'");
				result = st.executeQuery();
				if (result.next())
					return 2;
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}

		}
		try {
			PreparedStatement st = connect.prepareStatement("select * from user where user='" + user + "'");
			result = st.executeQuery();
			while (result.next()) {

				String pass = result.getString("password");
				try {
					if (passwordSHA.check(password, pass)) {
						return 1;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return -1;
	}

	public boolean saveNewPassword(String user, String newPass) {
		String sql = "UPDATE user SET password = ? " + "WHERE user = ?";
		int result = -2;
		try {
			PreparedStatement st = connect.prepareStatement(sql);
			st.setString(1, newPass);
			st.setString(2, user);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		if (result == 1) {
			return true;
		} else
			return false;
	}

	public boolean comprobarUsuario(String user) {
		try {
			PreparedStatement st = connect.prepareStatement("SELECT * FROM USER WHERE USER='" + user + "'");
			ResultSet result = st.executeQuery();
			if (result.next()) {
				return true;
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return false;
	}

	public String getRole(String user) {
		try {
			PreparedStatement st = connect.prepareStatement("select * from user where user='" + user + "'");
			ResultSet result = st.executeQuery();
			if (result.next()) {
				return result.getString("role");
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}

	public String getPosicion(String cargo) {
		try {
			PreparedStatement st = connect.prepareStatement("select * from role where nombre_role ='" + cargo + "'");
			ResultSet result = st.executeQuery();
			if (result.next()) {
				return result.getString("posicion");
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return null;
	}

//	public ResultSet mostrarEntradasCoronelPendiente(String date) {
//		System.out.println("mostrarEntradasCoronelPendiente");
//		ResultSet result = null;
//		try {
//			// PreparedStatement st = connect.prepareStatement("select * from entrada where
//			// fecha = '" + date + "' UNION select * from entrada where tramitado = '0'");
//
//			System.out.println(
//					"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id WHERE entrada.Fecha = '"
//							+ date + "' " + "UNION "
//							+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id WHERE entrada.tramitado = '0' ");
//
//			PreparedStatement st = connect.prepareStatement(
//					"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id WHERE entrada.Fecha = '"
//							+ date + "' " + "UNION "
//							+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id WHERE entrada.tramitado = '0' ");
//
//			result = st.executeQuery();
//		} catch (SQLException ex) {
//			System.err.println(ex.getMessage());
//		}
//		return result;
//	}

//	public ResultSet mostrarEntradasJefesPendiente(String date) {
//		System.out.println("mostrarEntradasJefesPendiente");
//		ResultSet result = null;
//		try {
//			// PreparedStatement st = connect.prepareStatement("select * from entrada where
//			// fecha = '" + date + "' and soloJefe1 = '0' UNION select * from entrada where
//			// tramitado = '0' and soloJefe1 = '0'");
//
//			PreparedStatement st = connect.prepareStatement(
//					"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id WHERE entrada.soloJefe1 = '0' AND entrada.Fecha = '"
//							+ date + "' " + "UNION "
//							+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id WHERE entrada.soloJefe1 = '0' AND entrada.tramitado = '0' ");
//
//			result = st.executeQuery();
//		} catch (SQLException ex) {
//			System.err.println(ex.getMessage());
//		}
//		return result;
//	}

//	public ResultSet mostrarEntradasSinConfidencialPendiente(String date, String role) {
//		System.out.println("mostrarEntradasSinConfidencialPendiente");
//		ResultSet result = null;
//		try {
////			PreparedStatement st = connect.prepareStatement("select * from entrada where area = 'Todos' and fecha = '" + date
////					+ "' and soloJefe1 = '0' and confidencial = '0'  UNION select * from entrada where fecha = '" + date
////					+ "' and area='" + role
////					+ "' and confidencial = '1' and soloJefe1 = '0' UNION select * from entrada where tramitado = '0' and area='"
////					+ role + "' and soloJefe1 = '0' UNION select * from entrada where tramitado = '0' and area='Todos' and soloJefe1 = '0' ");
//
////			PreparedStatement st = connect.prepareStatement("select id, asunto, fecha, area, observaciones, "
////					+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
////					+ "soloJefe1, Tramitado from entrada "
////					+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
////					+ "AND entrada.fecha = '"+date+ "' "
////					+ "AND entrada.soloJefe1 = 0 "
////					+ "AND entrada.confidencial = 0 "
////					+ "AND destinatarios.negociado = 'Todos' "
////					
////					+ "UNION select id, asunto, fecha, area, observaciones, "
////					+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
////					+ "soloJefe1, Tramitado from entrada "
////					+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
////					+ "AND entrada.fecha = '"+date+ "' "
////					+ "AND entrada.soloJefe1 = 0 "
////					+ "AND entrada.confidencial = 0 "
////					+ "AND destinatarios.negociado = '"+role+"' "
////					
////					+ "UNION select id, asunto, fecha, area, observaciones, "
////					+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
////					+ "soloJefe1, Tramitado from entrada "
////					+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
////					+ "AND entrada.tramitado = '0' "
////					+ "AND entrada.soloJefe1 = 0 "
////					+ "AND entrada.confidencial = 0 "
////					+ "AND destinatarios.negociado = '"+role+"' ");
//
//			PreparedStatement st = connect.prepareStatement(
//					"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//							+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//							+ "WHERE entrada.soloJefe1 = '0' " + "AND entrada.Fecha = '" + date + "' "
//							+ "AND entrada.confidencial = 0 " + "AND destinatarios.negociado = 'Todos' "
//
//							+ "UNION "
//							+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//							+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//							+ "WHERE entrada.soloJefe1 = '0' " + "AND entrada.Fecha = '" + date + "' "
//							+ "AND entrada.confidencial = 0 " + "AND destinatarios.negociado = '" + role + "'  "
//
//							+ "UNION "
//							+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//							+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//							+ "WHERE entrada.soloJefe1 = '0' " + "AND entrada.tramitado = '0' "
//							+ "AND entrada.confidencial = 0 " + "AND destinatarios.negociado = '" + role + "'  ");
//
//			result = st.executeQuery();
//		} catch (SQLException ex) {
//			System.err.println("mostrarEntradasSinConfidencialPendiente " + ex.getMessage());
//		}
//		return result;
//	}
//
//	public ResultSet mostrarEntradasPorNegociadoPendiente(String date, String area, String categoria) {
//		System.out.println("mostrarEntradasPorNegociadoPendiente");
//		ResultSet result = null;
//		try {
//
//			if (categoria.equals("Todas")) {
//
//				PreparedStatement st = connect.prepareStatement(
//						"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id WHERE entrada.soloJefe1 = '0' AND entrada.Fecha = '"
//								+ date + "' AND entrada.Area = '" + area + "' " + "UNION "
//								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id WHERE entrada.soloJefe1 = '0' AND entrada.tramitado = '0'  AND entrada.Area = '"
//								+ area + "' ");
//
//				result = st.executeQuery();
//			} else {
//				PreparedStatement st = connect.prepareStatement(
//						"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
//						+ "FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//						+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//						+ "WHERE entrada.soloJefe1 = '0' AND entrada.Fecha = '"
//								+ date + "' AND entrada.Area = '" + area + "' " 
//								+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' "
//								+ "UNION "
//								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
//								+ "FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//								+ "WHERE entrada.soloJefe1 = '0' AND entrada.tramitado = '0'  AND entrada.Area = '"
//								+ area + "' "
//								+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");
//
//
//				result = st.executeQuery();
//			}
//		} catch (SQLException ex) {
//			System.err.println(ex.getMessage());
//		}
//		return result;
//	}

	public ResultSet mostrarEntradasSinConfidencialPorNegociadoPendiente(String date, String area, String role, String categoria) {
		System.out.println("mostrarEntradasSinConfidencialPorNegociadoPendiente");
		ResultSet result = null;
		try {
			if (area.equals(role)) {

				if (categoria.equals("Todas")) {

				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE entrada.soloJefe1 = '0' " + "AND entrada.Fecha = '" + date + "' "
								+ "AND entrada.confidencial = 0 " + "AND destinatarios.negociado = '" + role + "'  "

								+ "UNION "
								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE entrada.soloJefe1 = '0' " + "AND entrada.tramitado = '0' "
								+ "AND entrada.confidencial = 0 " + "AND destinatarios.negociado = '" + role + "'  ");

				result = st.executeQuery();
				}else {
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.soloJefe1 = '0' " + "AND entrada.Fecha = '" + date + "' "
									+ "AND entrada.confidencial = 0 " + "AND destinatarios.negociado = '" + role + "'  "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' "

									+ "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.soloJefe1 = '0' " + "AND entrada.tramitado = '0' "
									+ "AND entrada.confidencial = 0 " + "AND destinatarios.negociado = '" + role + "'  "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");


					result = st.executeQuery();
				}
			} else {
				System.out.println("mostrarEntradasSinConfidencialPorNegociadoPendiente todos");

//				PreparedStatement st = connect.prepareStatement(
//						"select * from entrada where area = 'Todos' and soloJefe1 = '0' and fecha = '" + date
//								+ "' and confidencial = '0' and tramitado = '0'");
//				PreparedStatement st = connect.prepareStatement("select id, asunto, fecha, area, observaciones, "
//						+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
//						+ "soloJefe1, Tramitado from entrada "
//						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//						
//						+ "AND entrada.soloJefe1 = 0 "
//						+ "AND entrada.confidencial = 0 "
//						+ "AND entrada.tramitado = 0 "
//						+ "AND destinatarios.negociado = 'Todos'"  );
				if (categoria.equals("Todas")) {

				System.out.println(
						("SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE entrada.soloJefe1 = '0' " + "AND entrada.tramitado = '0' "
								+ "AND entrada.confidencial = 0 " + "AND destinatarios.negociado = 'Todos' "));

				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE entrada.soloJefe1 = '0' " + "AND entrada.tramitado = '0' "
								+ "AND entrada.confidencial = 0 " + "AND destinatarios.negociado = 'Todos' ");
				result = st.executeQuery();
				}else {

					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
							+ "FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.soloJefe1 = '0' " + "AND entrada.tramitado = '0' "
									+ "AND entrada.confidencial = 0 " + "AND destinatarios.negociado = 'Todos' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");

					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
							+ "FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.soloJefe1 = '0' " + "AND entrada.tramitado = '0' "
									+ "AND entrada.confidencial = 0 " + "AND destinatarios.negociado = 'Todos' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");
					result = st.executeQuery();
				}
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasCoronelPorNegociado(String date, String area) {
		System.out.println("mostrarEntradasCoronelPorNegociado");
		ResultSet result = null;
		try {
			// PreparedStatement st = connect.prepareStatement("select * from entrada where
			// fecha = '" + date + "' and Area = '" + area + "'");
			PreparedStatement st = connect.prepareStatement(
					"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id where entrada.Fecha = '"
							+ date + "' and Area = '" + area + "'");

			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasInicioRegistroPorNegociado(String date, String area, String categoria) {
		ResultSet result = null;
		try {

			if (categoria.equals("Todas")) {
				System.out.println("mostrarEntradasRegistroPorNegociado categoria " + categoria);

				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE entrada.Fecha = '" + date + "' and destinatarios.negociado = '" + area + "'");

				result = st.executeQuery();
			} else {
				System.out.println("mostrarEntradasRegistroPorNegociado categoria " + categoria);

				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
								+ "WHERE entrada.Fecha = '" + date + "' and destinatarios.negociado = '" + area + "' "
								+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");

				result = st.executeQuery();
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasCoronelPorNegociadoPendiente(String date, String area, String categoria) {
		System.out.println("mostrarEntradasCoronelPorNegociadoPendiente");
		ResultSet result = null;
		try {
			if (categoria.equals("Todas")) {

//			PreparedStatement st = connect.prepareStatement("select * from entrada where fecha = '" + date + "' and Area = '" + area + "' UNION select * from entrada where Area= '" + area + "'  and tramitado ='0'");
				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id where entrada.Fecha = '"
								+ date + "' and Area = '" + area + "'" + " UNION "
								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id where tramitado = '0' and Area = '"
								+ area + "'");

				result = st.executeQuery();
			} else {
				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
								+ "FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
								+ "where entrada.Fecha = '" + date + "' and Area = '" + area + "' "
								+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " 
								+ " UNION "
								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
								+ "FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
								+ "where tramitado = '0' and Area = '" + area + "' "
								+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");

				result = st.executeQuery();
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet searchRegistroTodosPorAsunto(String word, String categoria) {
		ResultSet result = null;
		try {

			if (categoria.equals("Todas")) {
				System.out.println("Search searchRegistroTodosPorAsunto categoria 1 " + categoria);
				System.out.println(
						"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id where asunto LIKE '%"
								+ word + "%'");

				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
								+ "FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "WHERE asunto LIKE '%" + word + "%'");
				result = st.executeQuery();

			} else {
				System.out.println("Search searchRegistroTodosPorAsunto categoria 2" + categoria);
				System.out.println(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
								+ "FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
								+ "WHERE asunto LIKE '%" + word + "%' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '"
								+ categoria + "'");

				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
								+ "FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
								+ "WHERE asunto LIKE '%" + word + "%' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '"
								+ categoria + "'");
				result = st.executeQuery();

			}

		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet searchRegistroTodosPorNumEntrada(String word, String categoria) {
		ResultSet result = null;
		try {
			if (categoria.equals("Todas")) {

				System.out.println("searchRegistroTodosPorNumEntrada categoria " + categoria);
				System.out.println(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id where numeroEntrada LIKE '%"
								+ word + "%'");
				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id where numeroEntrada LIKE '%"
								+ word + "%'");

				result = st.executeQuery();
			} else {

				System.out.println("searchRegistroTodosPorNumEntrada categoria " + categoria);
				System.out.println(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
								+ "FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
								+ "WHERE numeroEntrada LIKE '%" + word + "%' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '"
								+ categoria + "'");
				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
								+ "FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
								+ "WHERE numeroEntrada LIKE '%" + word + "%' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '"
								+ categoria + "'");

				result = st.executeQuery();
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

//	public ResultSet searchRegistroPorNegociadoPorAsunto(String word, String area, String categoria) {
//		ResultSet result = null;
//		try {
//			if (categoria.equals("Todas")) {
//				System.out.println("Search searchRegistroPorNegociadoPorAsunto categoria " + categoria);
//				System.out.println(
//						"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//								+ "WHERE asunto LIKE '%" + word + "%' " + "AND destinatarios.negociado= '" + area
//								+ "' ");
//				PreparedStatement st = connect.prepareStatement(
//						"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//								+ "WHERE asunto LIKE '%" + word + "%' " + "AND destinatarios.negociado= '" + area
//								+ "' ");
//				result = st.executeQuery();
//			} else {
//				System.out.println("Search searchRegistroPorNegociadoPorAsunto categoria " + categoria);
//				System.out.println(
//						"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//								+ "WHERE asunto LIKE '%" + word + "%' " + "AND destinatarios.negociado= '" + area + "' "
//								+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");
//
//				PreparedStatement st = connect.prepareStatement(
//						"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//								+ "WHERE asunto LIKE '%" + word + "%' " + "AND destinatarios.negociado= '" + area + "' "
//								+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");
//
//				result = st.executeQuery();
//			}
//
//		} catch (SQLException ex) {
//			System.err.println(ex.getMessage());
//		}
//		return result;
//	}
	
	public ResultSet searchRegistroPorNegociadoPorAsunto(String word, String area, String categoria) {
		ResultSet result = null;
		try {
			if (categoria.equals("Todas")) {
				categoria = "";
				
			} 
			
			if (area.equals("Todos")) {
				area = "";
			}
			
			System.out.println("Search searchRegistroPorNegociadoPorAsunto categoria " + categoria);
			System.out.println(
					"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
							+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "WHERE asunto LIKE '%" + word + "%' " + "AND destinatarios.negociado= '" + area
							+ "' ");
			
			System.out.println("SELECT * FROM (SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada \r\n" + 
					"LEFT JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id\r\n" + 
					"LEFT JOIN destinatario ON entrada.id = destinatario.entrada_id \r\n" + 
					"LEFT JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID \r\n" + 
					"LEFT JOIN entrada_canales ON entrada.id = entrada_canales.entrada_id \r\n" + 
					"WHERE (CATEGORIA_ENTRADA.CATEGORIA LIKE '%"+categoria+"%' OR CATEGORIA_ENTRADA.CATEGORIA IS NULL) AND destinatario.role IN ('Coronel'))\r\n" + 
					"WHERE Asunto LIKE '%"+word+"%'");
			PreparedStatement st = connect.prepareStatement(
					"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
							+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "WHERE asunto LIKE '%" + word + "%' " + "AND destinatarios.negociado= '" + area
							+ "' ");
			result = st.executeQuery();

		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet searchRegistroPorNegociadoPorNumEntrada(String word, String area, String categoria) {
		ResultSet result = null;
		try {
			if (categoria.equals("Todas")) {

				System.out.println("searchRegistroPorNegociadoPorNumEntrada categoria " + categoria);
				System.out.println(
						"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE numeroEntrada LIKE '%" + word + "%' " + "AND destinatarios.negociado= '" + area
								+ "' ");
				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE numeroEntrada LIKE '%" + word + "%' " + "AND destinatarios.negociado= '" + area
								+ "' ");
				result = st.executeQuery();
			} else {
				System.out.println("searchRegistroPorNegociadoPorNumEntrada categoria " + categoria);
				System.out.println(
						"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
								+ "WHERE numeroEntrada LIKE '%" + word + "%' " + "AND destinatarios.negociado= '" + area
								+ "' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");

				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
								+ "WHERE numeroEntrada LIKE '%" + word + "%' " + "AND destinatarios.negociado= '" + area
								+ "' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");

				result = st.executeQuery();
			}

		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet searchJefesPorAsunto(String word, Usuario usuario, String categoria) {
		System.out.println("searchJefesPorAsunto");
		ResultSet result = null;
		try {

			if (usuario.username == null || usuario.username.equals("")) {

				if (categoria.equals("Todas")) {
					System.out.println("searchJefes jefe null categoria Todas");
					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id WHERE destinatarios.negociado= '"
									+ usuario.role + "' " + "AND entrada.asunto LIKE '%" + word + "%' " + "");

					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id WHERE destinatarios.negociado= '"
									+ usuario.role + "' " + "AND entrada.asunto LIKE '%" + word + "%' " + "");
					result = st.executeQuery();
				} else {
					System.out.println("searchJefes jefe null categoria " + categoria);
					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id WHERE destinatarios.negociado= '"
									+ usuario.role + "' " + "AND entrada.asunto LIKE '%" + word + "%' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");

					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id WHERE destinatarios.negociado= '"
									+ usuario.role + "' " + "AND entrada.asunto LIKE '%" + word + "%' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");

					result = st.executeQuery();
				}

			} else {
				if (categoria.equals("Todas")) {

					System.out.println("searchJefes jefe " + usuario.username + " categoria " + categoria);
					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "WHERE entrada.asunto LIKE '%" + word + "%' " + "AND destinatariosJefes.jefe = '"
									+ usuario.username + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "WHERE entrada.asunto LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = 'Todos' " + "");
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "WHERE entrada.asunto LIKE '%" + word + "%' " + "AND destinatariosJefes.jefe = '"
									+ usuario.username + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "WHERE entrada.asunto LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = 'Todos' " + "");
					result = st.executeQuery();
				} else {
					System.out.println("searchJefes jefe " + usuario.username + " categoria " + categoria);
					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "WHERE entrada.asunto LIKE '%" + word + "%' " + "AND destinatariosJefes.jefe = '"
									+ usuario.username + "' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria
									+ "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "WHERE entrada.asunto LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = 'Todos' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '"
									+ categoria + "'");

					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "WHERE entrada.asunto LIKE '%" + word + "%' " + "AND destinatariosJefes.jefe = '"
									+ usuario.username + "' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria
									+ "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "WHERE entrada.asunto LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = 'Todos' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '"
									+ categoria + "'");
					result = st.executeQuery();
				}
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet searchJefesPorNumEntrada(String word, Usuario usuario, String categoria) {
		System.out.println("searchJefes");
		ResultSet result = null;
		try {

			if (usuario.username == null || usuario.username.equals("")) {

				if (categoria.equals("Todas")) {

					System.out.println("searchJefes jefe null categoria " + categoria);
					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id WHERE destinatarios.negociado= '"
									+ usuario.role + "' " + "AND entrada.numeroEntrada LIKE '%" + word + "%' " + "");
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id WHERE destinatarios.negociado= '"
									+ usuario.role + "' " + "AND entrada.numeroEntrada LIKE '%" + word + "%' " + "");
					result = st.executeQuery();
				} else {
					System.out.println("searchJefes jefe null categoria " + categoria);
					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id WHERE destinatarios.negociado= '"
									+ usuario.role + "' " + "AND entrada.numeroEntrada LIKE '%" + word + "%' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");

					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id WHERE destinatarios.negociado= '"
									+ usuario.role + "' " + "AND entrada.numeroEntrada LIKE '%" + word + "%' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");
					result = st.executeQuery();
				}
			} else {

				if (categoria.equals("Todas")) {

					System.out.println("searchJefes jefe " + usuario.username + " categoria " + categoria);
					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "WHERE entrada.numeroEntrada LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = '" + usuario.username + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "WHERE entrada.numeroEntrada LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = 'Todos' " + "");
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "WHERE entrada.numeroEntrada LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = '" + usuario.username + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "WHERE entrada.numeroEntrada LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = 'Todos' " + "");
					result = st.executeQuery();
				} else {
					System.out.println("searchJefes jefe " + usuario.username + " categoria " + categoria);
					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.numeroEntrada LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = '" + usuario.username + "' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.numeroEntrada LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = 'Todos' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '"
									+ categoria + "'");

					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.numeroEntrada LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = '" + usuario.username + "' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.numeroEntrada LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = 'Todos' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '"
									+ categoria + "'");
					result = st.executeQuery();
				}
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet search(String word, String role, String area) {

		if (area.equals("Todos")) {
			System.out.println("search Todos");
			ResultSet result = null;
			try {
//				PreparedStatement st = connect.prepareStatement("select * from entrada where area = 'Todos' and asunto LIKE '%" + word
//						+ "%' and soloJefe1 = '0' and confidencial = '0' UNION select * from entrada where asunto LIKE '%"
//						+ word + "%' and soloJefe1 = '0' and area = '" + role + "' and confidencial = '1'");
//				PreparedStatement st = connect.prepareStatement("select * from entrada where area = 'Todos' and asunto LIKE '%" + word
//						+ "%' and soloJefe1 = '0' and confidencial = '0'");
				System.out.println("Todos");
				System.out.println(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id LEFT OUTER JOIN destinatarios ON entrada.id = destinatarios.entrada_id where asunto LIKE '%"
								+ word + "%' and soloJefe1 = '0' AND destinatarios.negociado = 'Todos'");
				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id LEFT OUTER JOIN destinatarios ON entrada.id = destinatarios.entrada_id where asunto LIKE '%"
								+ word + "%' and soloJefe1 = '0' AND destinatarios.negociado = 'Todos'");

				// PreparedStatement st = connect.prepareStatement("select id, asunto, fecha,
				// area, observaciones, urgente, jefe1, jefe2, jefe3, jefe4, jefe5,
				// confidencial, soloJefe1, Tramitado from entrada "
				// + "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
				// + "AND entrada.asunto LIKE '%"+word+"%' "
				// + "AND entrada.confidencial = '0' "
				// + "AND entrada.soloJefe1 = 0 "
				// + "AND destinatarios.negociado = 'Todos' ");

				result = st.executeQuery();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
			return result;
		} else if (role.equals(area)) {

			System.out.println("search " + area);

			ResultSet result = null;
			try {
//				PreparedStatement st = connect.prepareStatement(
//						"select * from entrada where asunto LIKE '%" + word + "%' and soloJefe1 = '0' and area = '"
//								+ area + "' and confidencial = '0' UNION select * from entrada where asunto LIKE '%"
//								+ word + "%' and soloJefe1 = '0' and area = '" + role + "' and confidencial = '1'");

//				PreparedStatement st = connect.prepareStatement("select id, asunto, fecha, area, observaciones, urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, soloJefe1, Tramitado from entrada "
//						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//						+ "AND entrada.asunto LIKE '%"+word+"%' "
//						+ "AND entrada.confidencial = '0' "
//						+ "AND entrada.soloJefe1 = 0 "
//						+ "AND destinatarios.negociado = 'Todos' "
//						+ "UNION select id, asunto, fecha, area, observaciones, urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, soloJefe1, Tramitado from entrada "
//						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//						+ "AND entrada.asunto LIKE '%"+word+"%' "
//						+ "AND entrada.soloJefe1 = 0 "
//						+ "AND entrada.confidencial = '0' "
//						+ "AND destinatarios.negociado = '"+role+"' ");
				System.out.println(
						"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "LEFT OUTER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE asunto LIKE '%" + word
								+ "%' and soloJefe1 = '0' AND destinatarios.negociado = 'Todos' "
								+ "UNION SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id LEFT OUTER JOIN destinatarios ON entrada.id = destinatarios.entrada_id where asunto LIKE '%"
								+ word + "%' and soloJefe1 = '0' AND destinatarios.negociado = '" + role + "'");

				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id LEFT OUTER JOIN destinatarios ON entrada.id = destinatarios.entrada_id where asunto LIKE '%"
								+ word + "%' and soloJefe1 = '0' AND destinatarios.negociado = 'Todos' "
								+ "UNION SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id LEFT OUTER JOIN destinatarios ON entrada.id = destinatarios.entrada_id where asunto LIKE '%"
								+ word + "%' and soloJefe1 = '0' AND destinatarios.negociado = '" + role + "'");

				result = st.executeQuery();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
			return result;
		} else {
			ResultSet result = null;
			try {
//				PreparedStatement st = connect.prepareStatement("select * from entrada where asunto LIKE '%" + word
//						+ "%' and soloJefe1 = '0' and area = 'Todos' and confidencial = '0'");
				PreparedStatement st = connect.prepareStatement("select * from entrada where asunto = 'NULL'");
				result = st.executeQuery();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
			return result;
		}

	}

	public ResultSet mostrarEntradasCoronelPendienteVer(String date) {
		System.out.println("mostrarEntradasCoronelPendienteVer");
		ResultSet result = null;
		try {
			// PreparedStatement st = connect.prepareStatement(
			// "select * from entrada where fecha = '" + date + "' UNION select * from
			// entrada where jefe1 = '0'");

			PreparedStatement st = connect.prepareStatement("SELECT * FROM user WHERE role = 'Jefe1'");
			int idJefe1 = (int) st.executeQuery().getObject("id");

			st = connect.prepareStatement(
					"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id AND COMENTARIO.visto = 0 AND COMENTARIO.usuario_id = "
							+ idJefe1 + " WHERE entrada.Fecha = '" + date + "'");
			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasInicioRegistroTodos(String date, String categoria) {
		ResultSet result = null;
		try {

			if (categoria.equals("Todas")) {
				System.out.println("mostrarEntradasInicioRegistroTodos categoria " + categoria);

				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "WHERE entrada.Fecha = '" + date + "' ");

				result = st.executeQuery();
			} else {
				System.out.println("mostrarEntradasInicioRegistroTodos categoria " + categoria);

				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
								+ "WHERE entrada.Fecha = '" + date + "' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '"
								+ categoria + "'");

				result = st.executeQuery();
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public boolean tieneJefePendienteVer(int idUsuario, Usuario usuario) {
		System.out.println("tieneJefePendienteVer");
		boolean result = false;
		ResultSet temp;
		try {
			if (usuario.username != null) {
				System.out.println(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
								+ "WHERE COMENTARIO.usuario_id = '" + usuario.jefe_id + "' " + "AND COMENTARIO.visto = 0 "
								+ "AND destinatariosJefes.jefe = '" + usuario.username + "'");
				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
								+ "WHERE COMENTARIO.usuario_id = '" + usuario.jefe_id + "' " + "AND COMENTARIO.visto = 0 "
								+ "AND destinatariosJefes.jefe = '" + usuario.username + "'");
				temp = st.executeQuery();
				while (temp.next()) {
					result = true;
				}
			}

		} catch (SQLException ex) {
			System.err.println("Error tieneJefePendienteVer" + ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}

	public ResultSet mostrarEntradasJefesPendienteVer(String date, int idUsuario) {
		System.out.println("mostrarEntradasJefesPendienteVer");
		ResultSet result = null;

		try {
			// PreparedStatement st = connect.prepareStatement("select * from entrada where
			// fecha = '" + date + "' and soloJefe1 = '0' UNION select * from entrada where
			// soloJefe1 = '0' and " + usuario + "='0'");
			
			System.out.println(
					"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
							+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "WHERE COMENTARIO.usuario_id = '" + idUsuario + "' " + "AND entrada.fecha = '" + date
							+ "' " + "AND entrada.soloJefe1 = 0 " + "UNION "
							+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
							+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "WHERE COMENTARIO.usuario_id = '" + idUsuario + "' " + "AND COMENTARIO.visto = 0 "
							+ "AND entrada.soloJefe1 = 0 " + "");

			PreparedStatement st = connect.prepareStatement(
					"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
							+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "WHERE COMENTARIO.usuario_id = '" + idUsuario + "' " + "AND entrada.fecha = '" + date
							+ "' " + "AND entrada.soloJefe1 = 0 " + "UNION "
							+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
							+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "WHERE COMENTARIO.usuario_id = '" + idUsuario + "' " + "AND COMENTARIO.visto = 0 "
							+ "AND entrada.soloJefe1 = 0 " + "");

			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasCoronelPendientePorFecha(String[] dates, String categoria) {
		System.out.println("mostrarEntradasCoronelPendientePorFecha ");

		ResultSet result = null;
		try {
			// PreparedStatement st = connect.prepareStatement("select * from entrada where
			// (substr(fecha, 7, 4) || '/' || substr(fecha, 4, 2) || '/' || substr(fecha, 1,
			// 2)) between '"+cambiarFormatFecha(dates[0])+"' and
			// '"+cambiarFormatFecha(dates[1]) +"' and jefe1 = '0'");
//			PreparedStatement st = connect.prepareStatement("select * from entrada where (substr(fecha, 7, 4) || '/' || substr(fecha, 4, 2) || '/' || substr(fecha, 1, 2)) between '"+cambiarFormatFecha(dates[0])+"' and '"+cambiarFormatFecha(dates[1]) +"' and jefe1 = '0'");

			if (categoria.equals("Todas")) {

			PreparedStatement st = connect.prepareStatement("SELECT * FROM user WHERE role = 'Jefe1'");
			int idJefe1 = (int) st.executeQuery().getObject("id");
			System.out.println(
					"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id AND COMENTARIO.visto = 0 AND COMENTARIO.usuario_id = "
							+ idJefe1
							+ " WHERE (substr(entrada.Fecha, 7, 4) || '/' || substr(entrada.Fecha, 4, 2) || '/' || substr(entrada.Fecha, 1, 2)) between '"
							+ cambiarFormatFecha(dates[0]) + "' and '" + cambiarFormatFecha(dates[1]) + "'");

			st = connect.prepareStatement(
					"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id AND COMENTARIO.visto = 0 AND COMENTARIO.usuario_id = "
							+ idJefe1
							+ " WHERE (substr(entrada.Fecha, 7, 4) || '/' || substr(entrada.Fecha, 4, 2) || '/' || substr(entrada.Fecha, 1, 2)) between '"
							+ cambiarFormatFecha(dates[0]) + "' AND '" + cambiarFormatFecha(dates[1]) + "' ");


			result = st.executeQuery();
			}else {
				PreparedStatement st = connect.prepareStatement("SELECT * FROM user WHERE role = 'Jefe1'");
				int idJefe1 = (int) st.executeQuery().getObject("id");
				System.out.println(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
						+ "FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id AND COMENTARIO.visto = 0 AND COMENTARIO.usuario_id = "
								+ idJefe1 + " "
								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
								+ " WHERE (substr(entrada.Fecha, 7, 4) || '/' || substr(entrada.Fecha, 4, 2) || '/' || substr(entrada.Fecha, 1, 2)) between '"
								+ cambiarFormatFecha(dates[0]) + "' and '" + cambiarFormatFecha(dates[1]) + "' "
								+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");

				st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
						+ "FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id AND COMENTARIO.visto = 0 AND COMENTARIO.usuario_id = "
								+ idJefe1 + " "
								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
								+ " WHERE (substr(entrada.Fecha, 7, 4) || '/' || substr(entrada.Fecha, 4, 2) || '/' || substr(entrada.Fecha, 1, 2)) between '"
								+ cambiarFormatFecha(dates[0]) + "' and '" + cambiarFormatFecha(dates[1]) + "' "
								+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");


				result = st.executeQuery();
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasJefesPendientePorFecha(String[] dates, int idUsuario, String categoria) {
		ResultSet result = null;
		String usuario = "";

		try {
			if (categoria.equals("Todas")) {

			PreparedStatement st = connect.prepareStatement("select * from entrada "
					+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id" + " where fecha >= '" + dates[0]
					+ "' and fecha <= '" + dates[1] + "' " + "AND usuario_id = " + idUsuario + " AND visto = 0");
			result = st.executeQuery();
			}else {
				PreparedStatement st = connect.prepareStatement("select * from entrada "
						+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id" 
						+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
						+ " where fecha >= '" + dates[0]
						+ "' and fecha <= '" + dates[1] + "' " + "AND usuario_id = " + idUsuario + " AND visto = 0 "
						+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");

				result = st.executeQuery();
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasCoronelPorNegociadoPendientePorFecha(String[] dates, String string, String categoria) {
		System.out.println("mostrarEntradasCoronelPorNegociadoPendientePorFecha " + string);

		ResultSet result = null;
		try {

			if (categoria.equals("Todas")) {

			PreparedStatement st = connect.prepareStatement("SELECT * FROM user WHERE role = 'Jefe1'");
			int idJefe1 = (int) st.executeQuery().getObject("id");
			System.out.println(
					"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id AND COMENTARIO.visto = 0 AND COMENTARIO.usuario_id = "
							+ idJefe1
							+ " WHERE (substr(entrada.Fecha, 7, 4) || '/' || substr(entrada.Fecha, 4, 2) || '/' || substr(entrada.Fecha, 1, 2)) between '"
							+ cambiarFormatFecha(dates[0]) + "' and '" + cambiarFormatFecha(dates[1]) + "'");

			System.out.println(
					"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
							+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "AND COMENTARIO.visto = 0 AND COMENTARIO.usuario_id = " + idJefe1 + " "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "WHERE (substr(entrada.Fecha, 7, 4) || '/' || substr(entrada.Fecha, 4, 2) || '/' || substr(entrada.Fecha, 1, 2)) between '"
							+ cambiarFormatFecha(dates[0]) + "' and '" + cambiarFormatFecha(dates[1]) + "' "
							+ "AND destinatarios.negociado = '" + string + "' " + "UNION "
							+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
							+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "AND COMENTARIO.visto = 0 AND COMENTARIO.usuario_id = " + idJefe1 + " "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "WHERE (substr(entrada.Fecha, 7, 4) || '/' || substr(entrada.Fecha, 4, 2) || '/' || substr(entrada.Fecha, 1, 2)) between '"
							+ cambiarFormatFecha(dates[0]) + "' and '" + cambiarFormatFecha(dates[1]) + "' "
							+ "AND destinatarios.negociado = 'Todos' ");

			st = connect.prepareStatement(
					"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
							+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "AND COMENTARIO.visto = 0 AND COMENTARIO.usuario_id = " + idJefe1 + " "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "WHERE (substr(entrada.Fecha, 7, 4) || '/' || substr(entrada.Fecha, 4, 2) || '/' || substr(entrada.Fecha, 1, 2)) between '"
							+ cambiarFormatFecha(dates[0]) + "' and '" + cambiarFormatFecha(dates[1]) + "' "
							+ "AND destinatarios.negociado = '" + string + "' " + "UNION "
							+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
							+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "AND COMENTARIO.visto = 0 AND COMENTARIO.usuario_id = " + idJefe1 + " "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "WHERE (substr(entrada.Fecha, 7, 4) || '/' || substr(entrada.Fecha, 4, 2) || '/' || substr(entrada.Fecha, 1, 2)) between '"
							+ cambiarFormatFecha(dates[0]) + "' and '" + cambiarFormatFecha(dates[1]) + "' "
							+ "AND destinatarios.negociado = 'Todos' ");

			result = st.executeQuery();
			}else {
				PreparedStatement st = connect.prepareStatement("SELECT * FROM user WHERE role = 'Jefe1'");
				int idJefe1 = (int) st.executeQuery().getObject("id");

				System.out.println();

				st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
								+ "AND COMENTARIO.visto = 0 AND COMENTARIO.usuario_id = " + idJefe1 + " "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE (substr(entrada.Fecha, 7, 4) || '/' || substr(entrada.Fecha, 4, 2) || '/' || substr(entrada.Fecha, 1, 2)) between '"
								+ cambiarFormatFecha(dates[0]) + "' and '" + cambiarFormatFecha(dates[1]) + "' "
								+ "AND destinatarios.negociado = '" + string + "' " 
								+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' "
								+ "UNION "
								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
								+ "AND COMENTARIO.visto = 0 AND COMENTARIO.usuario_id = " + idJefe1 + " "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE (substr(entrada.Fecha, 7, 4) || '/' || substr(entrada.Fecha, 4, 2) || '/' || substr(entrada.Fecha, 1, 2)) between '"
								+ cambiarFormatFecha(dates[0]) + "' and '" + cambiarFormatFecha(dates[1]) + "' "
								+ "AND destinatarios.negociado = 'Todos' "
								+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");


				result = st.executeQuery();
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasJefesPorNegociadoPendientePorFechas(String[] dates, String string, String categoria) {
		return null;
	}

	public String cambiarFormatFecha(String date) {

		String[] elements = date.split("/");

		return elements[2] + "/" + elements[1] + "/" + elements[0];
	}

	public ArrayList<String> getNegociados() {
		ResultSet result = null;
		ArrayList<String> names = new ArrayList<String>();
		try {
//			PreparedStatement st = connect.prepareStatement("select * from negociados");
//			PreparedStatement st = connect.prepareStatement("SELECT * FROM role\r\n" + 
//			"LEFT JOIN usuario_role ON usuario_role.role = nombre_role\r\n" + 
//			"WHERE isJefe = 0\r\n" + 
//			"ORDER BY posicion");
			PreparedStatement st = connect.prepareStatement("SELECT * FROM role\r\n" + 
//					"LEFT JOIN usuario_role ON usuario_role.role = nombre_role\r\n" + 
					"WHERE posicion IS NULL" + 
					"");
			result = st.executeQuery();
			while (result.next()) {
				if (result.getString("nombre_role").equals("admin")) {
					continue;
				}
				names.add(result.getString("nombre_role"));
				System.out.println(result.getString("nombre_role"));
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return names;
	}
	
	public ArrayList<String> getNegociadosJefes() {
		ResultSet result = null;
		ArrayList<String> names = new ArrayList<String>();
		try {
//			PreparedStatement st = connect.prepareStatement("select * from negociados");
//			PreparedStatement st = connect.prepareStatement("SELECT * FROM role\r\n" + 
//			"LEFT JOIN usuario_role ON usuario_role.role = nombre_role\r\n" + 
//			"WHERE isJefe = 0\r\n" + 
//			"ORDER BY posicion");
			PreparedStatement st = connect.prepareStatement("SELECT * FROM role\r\n" + 
//					"LEFT JOIN usuario_role ON usuario_role.role = nombre_role\r\n" + 
					"WHERE posicion IS NOT NULL " + 
					"ORDER BY posicion");

			result = st.executeQuery();
			while (result.next()) {
				if (result.getString("nombre_role").equals("admin")) {
					continue;
				}
				names.add(result.getString("nombre_role"));
				System.out.println(result.getString("nombre_role"));
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return names;
	}

//	public LinkedHashMap<Integer, String> getCargos() {
//		ResultSet result = null;
//		LinkedHashMap<Integer, String> cargos = new LinkedHashMap<Integer, String>();
//		try {
//			PreparedStatement st = connect.prepareStatement("select * from JEFES ORDER BY posicion");
//			result = st.executeQuery();
//			while (result.next()) {
//				cargos.put(result.getInt("ID"), result.getString("nombre"));
//			}
//		} catch (SQLException ex) {
//			System.err.println(ex.getMessage());
//		}
//		return cargos;
//	}
	
	public LinkedHashMap<Integer, String> getCargos() {
		ResultSet result = null;
		LinkedHashMap<Integer, String> cargos = new LinkedHashMap<Integer, String>();
		try {
			System.out.println("getCargos ");
//			System.out.println("SELECT user.id, user.user, usuario_role.permiso, usuario_role.role as role, usuario_role.isJefe, role.posicion from user\r\n" + 
//					"LEFT JOIN usuario_role ON user.id = usuario_role.user_id\r\n" + 
//					"LEFT JOIN role ON role.nombre_role = usuario_role.role\r\n" + 
//
//					"where usuario_role.isJefe = 1\r\n" + 
//					"ORDER BY role.posicion");
//
//			PreparedStatement st = connect.prepareStatement("SELECT user.id, user.user, usuario_role.permiso, usuario_role.role as role, usuario_role.isJefe, role.posicion from user\r\n" + 
//					"LEFT JOIN usuario_role ON user.id = usuario_role.user_id\r\n" + 
//					"LEFT JOIN role ON role.nombre_role = usuario_role.role\r\n" + 
//
//					"where usuario_role.isJefe = 1\r\n" + 
//					"ORDER BY role.posicion");
			
			System.out.println("SELECT * from role\r\n" + 
//					"LEFT JOIN usuario_role ON user.id = usuario_role.user_id\r\n" + 
//					"LEFT JOIN role ON role.nombre_role = usuario_role.role\r\n" + 

					"where posicion IS NOT NULL\r\n" + 
					"ORDER BY posicion");

			PreparedStatement st = connect.prepareStatement("SELECT * from role\r\n" + 
//					"LEFT JOIN usuario_role ON user.id = usuario_role.user_id\r\n" + 
//					"LEFT JOIN role ON role.nombre_role = usuario_role.role\r\n" + 

					"where posicion IS NOT NULL\r\n" + 
					"ORDER BY posicion");
			result = st.executeQuery();
			while (result.next()) {
				cargos.put(result.getInt("id"), result.getString("nombre_role"));
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
		return cargos;
	}

	public String getCargo(int posicion) {
		ResultSet result = null;
		String resultado = "";
		try {
			PreparedStatement st = connect.prepareStatement("select * from JEFES where posicion='" + posicion + "'");
			result = st.executeQuery();
			while (result.next()) {
				resultado = result.getString("nombre");
				System.out.println(result.getString("nombre"));
				return resultado;
			}
			return null;
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
			return null;
		}
	}

	public int getPosicion(int usuario_id) {
		ResultSet result = null;
		int resultado = -1;
		try {
			PreparedStatement st = connect.prepareStatement("select role.posicion from usuario_role \r\n" + 
					"LEFT JOIN role ON role.nombre_role = usuario_role.role\r\n" + 
					"where usuario_role.user_id = '" + usuario_id + "'");
			result = st.executeQuery();
			while (result.next()) {
//				System.out.println("posicion " + result.getInt("posicion"));
				resultado = result.getInt("posicion");
				return resultado;
			}
			return -1;
		} catch (SQLException ex) {
			System.err.println("getPosicion" + ex.getMessage());
			return -1;
		}
	}

	public boolean eliminarNegociado(String negociado) {
		int result = -2;
		boolean exist = false;
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM role WHERE nombre_role = ?");
			st.setString(1, negociado);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("primer try comentario" + ex.getMessage());
		}
		if (result == 1) {
			return true;
		} else
			return false;
	}
	
	public boolean eliminarCargo(String negociado) {
		int result = -2;
		boolean exist = false;
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM role WHERE nombre_role = ?");
			st.setString(1, negociado);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("primer try comentario" + ex.getMessage());
			ex.printStackTrace();
		}
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM usuario_role WHERE role = ?");
			st.setString(1, negociado);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("primer try comentario" + ex.getMessage());
			ex.printStackTrace();
		}
		if (result == 1) {
			return true;
		} else
			return false;
	}

	public boolean eliminarCanalEntrada(String canal) {
		int result = -2;
		boolean exist = false;
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM canalesEntrada WHERE nombre = ?");
			st.setString(1, canal);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("primer try comentario" + ex.getMessage());
		}
		if (result == 1) {
			return true;
		} else
			return false;
	}
	
	public boolean eliminarCanalEntradas(int entradaId) {
		int result = -2;
		boolean exist = false;
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM entrada_canales WHERE entrada_id = ?");
			st.setInt(1, entradaId);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("eliminarCanalEntradas" + ex.getMessage());
			ex.printStackTrace();
		}
		if (result == 1) {
			return true;
		} else
			return false;
	}


	public boolean eliminarCargoAntiguo(String cargo) {
		int result = -2;
		boolean exist = false;
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM JEFES WHERE nombre = ?");
			st.setString(1, cargo);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("primer try comentario" + ex.getMessage());
		}
		if (result == 1) {
			return true;
		} else {
			return false;

		}

	}

	public boolean saveNegociado(String negociado) {
		// TODO Auto-generated method stub
		try {
			PreparedStatement st = connect.prepareStatement("insert into role (nombre_role) values (?)");
			st.setString(1, negociado);
			st.execute();
			return true;
		} catch (SQLException ex) {
			System.err.println("Save negociado error " + ex.getMessage());
			return false;
		}
	}

	public boolean saveCargo(String cargo, int posicion) {
		// TODO Auto-generated method stub
		try {
			PreparedStatement st = connect.prepareStatement("insert into role (nombre_role, posicion) values (?,?)");
			st.setString(1, cargo);
			st.setInt(2, posicion);

			st.execute();
			return true;
		} catch (SQLException ex) {
			System.err.println("Save cargo error " + ex.getMessage());
			return false;
		}
	}

	public String getCargoPosicion(String cargo) {
		ResultSet result = null;
		String resultado = "";
		try {
			PreparedStatement st = connect.prepareStatement("select * from role where nombre_role='" + cargo + "'");
			result = st.executeQuery();
			while (result.next()) {
				resultado = result.getString("posicion");
				return resultado;
			}
			return "";
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
			return "";
		}
	}

	public boolean agregarDestinatario(int entradaid, String destino) {

		try {
			PreparedStatement st = connect
					.prepareStatement("insert into destinatario (entrada_id, role) values (?,?)");
			st.setInt(1, entradaid);
			st.setString(2, destino);

			return st.execute();
		} catch (SQLException ex) {
			System.err.println("Save Comentario " + ex.getMessage());
			return false;
		}
	}

	public ArrayList<String> getDestinos(int id) {
		ResultSet result = null;

		ArrayList<String> resultado = new ArrayList<String>();
		try {
			System.out.println("select destinatario.role from destinatario "
					+ "LEFT JOIN entrada ON entrada.id = destinatario.entrada_id "
					+ "LEFT JOIN usuario_role ON usuario_role.role = destinatario.role " 
					+ "WHERE entrada.id = " + id +" "
					+ "AND usuario_role.isJefe = 0");
			PreparedStatement st = connect.prepareStatement("select destinatario.role from destinatario "
					+ "LEFT JOIN entrada ON entrada.id = destinatario.entrada_id "
					+ "LEFT JOIN usuario_role ON usuario_role.role = destinatario.role " 
					+ "WHERE entrada.id = " + id +" "
					+ "AND usuario_role.isJefe = 0");
			result = st.executeQuery();
			while (result.next()) {
				resultado.add(result.getString("role"));
			}

		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
			return null;
		}

		return resultado;
	}
	
//	public ArrayList<String> getDestinos(int id) {
//		ResultSet result = null;
//
//		ArrayList<String> resultado = new ArrayList<String>();
//		try {
//			System.out.println("select destinatarios.negociado from destinatarios "
//					+ "INNER JOIN entrada ON entrada.id = destinatarios.entrada_id " + "AND entrada.id = " + id);
//			PreparedStatement st = connect.prepareStatement("select destinatarios.negociado from destinatarios "
//					+ "INNER JOIN entrada ON entrada.id = destinatarios.entrada_id " + "AND entrada.id = '" + id + "'");
//			result = st.executeQuery();
//			while (result.next()) {
//				resultado.add(result.getString("negociado"));
//			}
//
//		} catch (SQLException ex) {
//			System.err.println(ex.getMessage());
//			return null;
//		}
//
//		return resultado;
//	}

	public boolean borrarDestinos(int id) {
		int result = -2;
		boolean exist = false;
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM destinatario WHERE entrada_id = ?");
			st.setInt(1, id);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("borrarDestinos " + ex.getMessage());
		}

		if (result == 1) {
			return true;
		} else
			return false;
	}

//	public void actualizarComentarioTramitado(comentario comentario) {
//		boolean exist = false;
//		try {
//			PreparedStatement st = connect
//					.prepareStatement("select * from comentario where entrada_id=" + comentario.getEntrada_id());
//			ResultSet result = st.executeQuery();
//			exist = result.next();
//		} catch (SQLException ex) {
//			System.err.println(ex.getMessage());
//		}
//		if (exist) {
//			String sql = "UPDATE comentario SET tramitadoPor = ? WHERE entrada_id = ?";
//			try {
//				PreparedStatement st = connect.prepareStatement(sql);
//				st.setString(1, comentario.getTramitadoPor());
//				st.setInt(2, comentario.getEntrada_id());
//				st.execute();
//			} catch (SQLException ex) {
//				System.err.println("No existe actualizarComentario "  + ex.getMessage());
//			}		
//		}else {
//			String sql = "INSERT INTO comentario (tramitadoPor, entrada_id) VALUES (?,?)";
//			try {
//				PreparedStatement st = connect.prepareStatement(sql);
//				st.setString(1, comentario.getTramitadoPor());
//				st.setInt(2, comentario.getEntrada_id());
//				st.execute();
//			} catch (SQLException ex) {
//				System.err.println("Existe actualizarComentario "  + ex.getMessage());
//			}		
//		}
//		
//		
//	}

	public boolean eliminarEntradaNoGestionada(int id) {
		int result = -2;
		boolean exist = false;
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM entradaNoGestionada WHERE idEntrada = ?");
			st.setInt(1, id);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("primer try entrada" + ex.getMessage());
		}

		if (result == 1) {
			return true;
		} else
			return false;
	}

	public void addEntradaNoGestionada(int id) {

		try {
			PreparedStatement st = connect.prepareStatement("insert into entradaNoGestionada (idEntrada) values (?)");
			st.setInt(1, id);
			st.execute();
		} catch (SQLException ex) {
			System.err.println("Insert addEntradaNoGestionada " + ex.getMessage());
		}

	}

	public boolean isNotGestionado(String string) {
		ResultSet result = null;

		try {
			PreparedStatement st = connect.prepareStatement("select * from negociados where nombre = '" + string + "'");
			result = st.executeQuery();
			if (result.next()) {
				if (result.getBoolean("gestionado") == true) {
					return false;
				} else {
					return true;
				}

			} else
				throw new SQLException();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
			return true;
		}
	}

	public ResultSet mostrarEntradasNoGestionadas() {
		ResultSet result = null;
		try {

			System.out.println("mostrarEntradasNoGestionadas TODOS");
			System.out.println("select id, asunto, fecha, area, observaciones, "
					+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
					+ "soloJefe1, Tramitado from entrada "
					+ "INNER JOIN entradaNoGestionada ON entrada.id = entradaNoGestionada.idEntrada");

//				PreparedStatement st = connect.prepareStatement("select * from entrada where area = 'Todos' and fecha = '" + date
//						+ "' and soloJefe1 = '0' and confidencial = '0' UNION select * from entrada where fecha = '" + date
//						+ "' and area='" + role + "' and confidencial = '1'");
//				PreparedStatement st = connect.prepareStatement("select * from entrada where area = 'Todos' and fecha = '" + date
//						+ "' and soloJefe1 = '0' and confidencial = '0'");
			PreparedStatement st = connect.prepareStatement("select entrada.id, asunto, fecha, area, observaciones, "
					+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
					+ "soloJefe1, Tramitado from entrada "
					+ "INNER JOIN entradaNoGestionada ON entrada.id = entradaNoGestionada.idEntrada");

			result = st.executeQuery();

		} catch (SQLException ex) {
			System.err.println("mostrarEntradasNoGestionadas " + ex.getMessage());
		}
		return result;
	}

	public boolean entradaGestionadaExist(int id) {
		ResultSet result = null;

		try {
			PreparedStatement st = connect
					.prepareStatement("select * from entradaNoGestionada where idEntrada = '" + id + "'");
			result = st.executeQuery();
			if (result.next()) {

				return true;

			} else
				return false;
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
			return false;
		}
	}

	public boolean saveCanalEntrada(String canal) {
		// TODO Auto-generated method stub
		try {
			PreparedStatement st = connect.prepareStatement("insert into canalesEntrada (nombre) values (?)");
			st.setString(1, canal);
			st.execute();
			return true;
		} catch (SQLException ex) {
			System.err.println("Save canal de entrada error " + ex.getMessage());
			return false;
		}
	}

	public ArrayList<String> getCanales() {
		ResultSet result = null;
		ArrayList<String> canales = new ArrayList<String>();
		try {
			PreparedStatement st = connect.prepareStatement("select * from canalesEntrada");
			result = st.executeQuery();
			while (result.next()) {
				canales.add(result.getString("nombre"));
				System.out.println(result.getString("nombre"));
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return canales;
	}

	public ResultSet searchCoronelNumEntrada(String word) {
		System.out.println("searchCoronelNumEntrada ");
		ResultSet result = null;
		try {
			System.out.println(
					"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id where numeroEntrada LIKE '%"
							+ word + "%'");
			PreparedStatement st = connect.prepareStatement(
					"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id where numeroEntrada LIKE '%"
							+ word + "%'");

			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet searchJefesNumEntrada(String word) {
		ResultSet result = null;
		try {

			PreparedStatement st = connect.prepareStatement(
					"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id where numeroEntrada LIKE '%"
							+ word + "%' and soloJefe1 = '0'");

			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet searchNumEntrada(String word, String role, String area) {
		if (area.equals("Todos")) {
			ResultSet result = null;
			System.out.println("searchNumEntrada todos");

			try {
//				PreparedStatement st = connect.prepareStatement("select * from entrada where area = 'Todos' and asunto LIKE '%" + word
//						+ "%' and soloJefe1 = '0' and confidencial = '0' UNION select * from entrada where asunto LIKE '%"
//						+ word + "%' and soloJefe1 = '0' and area = '" + role + "' and confidencial = '1'");
//				PreparedStatement st = connect.prepareStatement("select * from entrada where area = 'Todos' and asunto LIKE '%" + word
//						+ "%' and soloJefe1 = '0' and confidencial = '0'");
//				PreparedStatement st = connect.prepareStatement("select id, asunto, fecha, area, observaciones, urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, soloJefe1, Tramitado from entrada "
//						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//						+ "AND entrada.numeroEntrada LIKE '%"+word+"%' "
//						+ "AND entrada.confidencial = '0' "
//						+ "AND entrada.soloJefe1 = 0 "
//						+ "AND destinatarios.negociado = 'Todos' ");

				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "LEFT OUTER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "where numeroEntrada LIKE '%" + word
								+ "%' AND soloJefe1 = '0' AND destinatarios.negociado = 'Todos' "
								+ "AND entrada.confidencial = '0' ");

				result = st.executeQuery();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
			return result;
		} else if (role.equals(area)) {
			ResultSet result = null;
			System.out.println("searchNumEntrada area: " + area);
			try {
//				PreparedStatement st = connect.prepareStatement(
//						"select * from entrada where asunto LIKE '%" + word + "%' and soloJefe1 = '0' and area = '"
//								+ area + "' and confidencial = '0' UNION select * from entrada where asunto LIKE '%"
//								+ word + "%' and soloJefe1 = '0' and area = '" + role + "' and confidencial = '1'");

				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "LEFT OUTER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "where numeroEntrada LIKE '%" + word
								+ "%' AND soloJefe1 = '0' AND destinatarios.negociado = 'Todos' "
								+ "AND entrada.confidencial = '0' " + "UNION "
								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "LEFT OUTER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "where numeroEntrada LIKE '%" + word
								+ "%' AND soloJefe1 = '0' AND destinatarios.negociado = '" + role + "' "
								+ "AND entrada.confidencial = '0' ");

				result = st.executeQuery();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
			return result;
		} else {
			ResultSet result = null;
			try {
//				PreparedStatement st = connect.prepareStatement("select * from entrada where asunto LIKE '%" + word
//						+ "%' and soloJefe1 = '0' and area = 'Todos' and confidencial = '0'");
				PreparedStatement st = connect.prepareStatement("select * from entrada where asunto = 'NULL'");
				result = st.executeQuery();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
			return result;
		}
	}

	public String getUserID(String usuario) {
		try {
			PreparedStatement st = connect.prepareStatement("select * from user where user='" + usuario + "'");
			ResultSet result = st.executeQuery();
			if (result.next()) {
				return result.getString("id");
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return null;
	}

	public Usuario getUsuario(int usuario_id) {
		Usuario usuario = null;
		try {
//			PreparedStatement st = connect.prepareStatement(
//					"SELECT user.id, user.user, user.role, user.permiso, JEFES.ID as jefeID, JEFES.nombre AS nombreJefe, JEFES.posicion from user\r\n"
//							+ "LEFT JOIN JEFES ON user.id = JEFES.usuario_id\r\n" + "where user.id = '" + usuario_id
//							+ "'");
			PreparedStatement st = connect.prepareStatement("SELECT user.id, user.user, usuario_role.permiso, usuario_role.role as role, usuario_role.isJefe, role.posicion from user\r\n" + 
					"LEFT JOIN usuario_role ON user.id = usuario_role.user_id\r\n" + 
					"LEFT JOIN role ON role.nombre_role = usuario_role.role\r\n" + 

					"where user.id = '"+usuario_id+"'");
			
			ResultSet result = st.executeQuery();
			if (result.next()) {
				System.out.println("DENTRO getUsuario new Usuario");
//				public Usuario(int usuario_id, String role, String nombre_usuario, int posicion, boolean permiso, boolean isJefe) {

				usuario = new Usuario(usuario_id, result.getString("role"), (result.getString("user") == null ? "": result.getString("user")), result.getInt("posicion"), result.getBoolean("permiso"),result.getBoolean("isJefe"));
			}
		} catch (SQLException ex) {
			System.err.println("getUsuario " + ex.getMessage());
			ex.printStackTrace();
		}
		return usuario;
	}

	public void saveFileSalida(String absolutePath, int idEntrada, String fecha, String asunto, String destino) {
		try {
			PreparedStatement st = connect.prepareStatement(
					"insert into salidaFiles (file, entrada_id, fecha, destino, asunto) values (?,?,?,?,?)");
			st.setString(1, absolutePath);
			st.setInt(2, idEntrada);
			st.setString(3, fecha);
			st.setString(4, asunto);
			st.setString(5, destino);
			st.execute();
		} catch (SQLException ex) {
			System.err.println("saveFileSalida error " + ex.getMessage());
		}
	}

	public void saveFileAntecedentes(String absolutePath, int idEntrada, String fecha, String tipo, String destino,
			String asunto, String observaciones) {
		try {
			PreparedStatement st = connect.prepareStatement(
					"insert into antecedentesFiles (file, entrada_id, fecha, destino, asunto, observaciones, tipo) values (?,?,?,?,?,?,?)");
			st.setString(1, absolutePath);
			st.setInt(2, idEntrada);
			st.setString(3, fecha);
			st.setString(4, destino);
			st.setString(5, asunto);
			st.setString(6, observaciones);
			st.setString(7, tipo);

			st.execute();
		} catch (SQLException ex) {
			System.err.println("saveFileAntecedentes error " + ex.getMessage());
		}
	}

	public void saveFileEntrada(String absolutePath, int idEntrada, String fecha, String asunto, String origen,
			String observaciones) {
		try {
			System.out.println("observaciones " + observaciones);
			System.out.println("origen " + origen);
			System.out.println("asunto " + asunto);
			PreparedStatement st = connect.prepareStatement(
					"insert into files (file, entrada_id, fecha, asunto, origen, observaciones) values (?,?,?,?,?,?)");
			st.setString(1, absolutePath);
			st.setInt(2, idEntrada);
			st.setString(3, fecha);
			st.setString(4, asunto);
			st.setString(5, origen);
			st.setString(6, observaciones);

			st.execute();
		} catch (SQLException ex) {
			System.err.println("saveFileEntrada error " + ex.getMessage());
		}
	}

	public DefaultListModel mostrarFilesSalida(int id) {
		ResultSet result = null;
		// DefaultListModel resultado = new DefaultListModel();
		DefaultListModel<JCheckBox> resultado = new DefaultListModel<JCheckBox>();
		JCheckBoxList checkBoxList = new JCheckBoxList(resultado);
		try {
			PreparedStatement st = connect.prepareStatement("select * from salidaFiles where entrada_id=" + id);
			result = st.executeQuery();
			while (result.next()) {
				JCheckBox checkbox = new JCheckBox(result.getString("id") + "-Fecha y hora: "
						+ result.getString("file").split("_")[0] + "- Nombre: "
						+ result.getString("file").split("\\\\")[result.getString("file").split("\\\\").length - 1]);
				checkbox.setSelected(result.getBoolean("vistoBueno"));
				resultado.addElement(checkbox);
			}
		} catch (SQLException ex) {
			System.err.println("mostrarFilesSalida error " + ex.getMessage());
		}
		return resultado;
	}

	public boolean eliminarSalidaFile(String id) {
		int result = -2;
		boolean exist = false;
		try {
			System.out.println("Eliminar salida file " + id);
			PreparedStatement st = connect.prepareStatement("DELETE FROM salidaFiles WHERE id = ?");
			st.setString(1, id.split("-")[0]);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("eliminarSalidaFile" + ex.getMessage());
		}

		if (result == 1) {
			return true;
		} else
			return false;

	}

	public String getFileById(String id) {
		ResultSet result = null;
		String resultado = "";
		try {
			PreparedStatement st = connect.prepareStatement("select * from files where id=" + id);
			result = st.executeQuery();
			while (result.next()) {
				resultado = result.getString("file");
			}
		} catch (SQLException ex) {
			System.err.println("mostrarFilesSalida error " + ex.getMessage());
		}
		return resultado;

	}

	public String getAntecedentesById(String id) {
		ResultSet result = null;
		String resultado = "";
		try {
			PreparedStatement st = connect.prepareStatement("select * from antecedentesFiles where id=" + id);
			result = st.executeQuery();
			while (result.next()) {
				resultado = result.getString("file");
			}
		} catch (SQLException ex) {
			System.err.println("mostrarFilesSalida error " + ex.getMessage());
		}
		return resultado;

	}

	public String getSalidaById(String id) {
		ResultSet result = null;
		String resultado = "";
		try {
			PreparedStatement st = connect.prepareStatement("select * from salidaFiles where id=" + id);
			result = st.executeQuery();
			while (result.next()) {
				resultado = result.getString("file").substring(result.getString("file").indexOf("_") + 1,
						result.getString("file").length());
			}
		} catch (SQLException ex) {
			System.err.println("mostrarFilesSalida error " + ex.getMessage());
		}
		return resultado;

	}

	public ResultSet getSalidaByIdBIS(int id) {

		System.out.println("getSalidaByIdBIS");
		ResultSet result = null;
		try {
			// PreparedStatement st = connect.prepareStatement(
			// "select * from entrada where fecha = '" + date + "' UNION select * from
			// entrada where jefe1 = '0'");
			System.out.println(
					"select salidaFiles.id AS salida_id, salidaFiles.file AS file, salidaFiles.entrada_id AS entrada_id, salidaFiles.fecha AS fecha, salidaFiles.destino AS destino, salidaFiles.asunto AS asunto, vistoBuenoJefes.id AS vistoBuenoId, vistoBuenoJefes.usuario_id as usuario_id, vistoBuenoJefes.vistoBueno AS vistoBueno  from salidaFiles "
							+ "LEFT JOIN vistoBuenoJefes on salidaFiles.id = vistoBuenoJefes.salidaFile_id "
							+ "where salidaFiles.entrada_id=" + id);
			PreparedStatement st = connect.prepareStatement(
					"select salidaFiles.id AS salida_id, salidaFiles.file AS file, salidaFiles.entrada_id AS entrada_id, salidaFiles.fecha AS fecha, salidaFiles.destino AS destino, salidaFiles.asunto AS asunto, vistoBuenoJefes.id AS vistoBuenoId, vistoBuenoJefes.usuario_id as usuario_id, vistoBuenoJefes.vistoBueno AS vistoBueno  from salidaFiles "
							+ "LEFT JOIN vistoBuenoJefes on salidaFiles.id = vistoBuenoJefes.salidaFile_id "
							+ "where salidaFiles.entrada_id=" + id);

			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;

	}

	public ResultSet getAntecedentesById(int id) {

		System.out.println("getAntecedentesById");
		ResultSet result = null;
		try {

			PreparedStatement st = connect.prepareStatement(
					"select antecedentesFiles.id AS antecedentesFiles_id, antecedentesFiles.file AS file, antecedentesFiles.tipo AS tipo, antecedentesFiles.entrada_id AS entrada_id, antecedentesFiles.fecha AS fecha, antecedentesFiles.destino AS destino, antecedentesFiles.asunto AS asunto, antecedentesFiles.observaciones AS observaciones from antecedentesFiles "
							+ "where antecedentesFiles.entrada_id=" + id);

			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;

	}

	public boolean setVistoBuenoSalida(String idSalidaFile, int usuario, int vistoBueno) {

		boolean exist = false;
		try {
			PreparedStatement st = connect.prepareStatement("select * from vistoBuenoJefes where salidaFile_id ='"
					+ idSalidaFile + "' AND usuario_id= '" + usuario + "'");
			System.out.println("select * from vistoBuenoJefes where salidaFile_id ='" + idSalidaFile
					+ "' AND usuario_id= '" + usuario + "'");
			ResultSet result = st.executeQuery();
			exist = result.next();
		} catch (SQLException ex) {
			close();

			System.err.println(ex.getMessage());
			return false;

		}

		if (exist) {

			System.out.println("actualizarVisto Bueno exists");
			String sql = "UPDATE vistoBuenoJefes SET vistoBueno = ? WHERE salidaFile_id = ? AND usuario_id = ?";
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				st.setInt(1, vistoBueno);
				st.setString(2, idSalidaFile);
				st.setInt(3, usuario);
				st.executeUpdate();
				return true;
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
				return false;

			}
		} else {
			String sql = "INSERT INTO vistoBuenoJefes (vistoBueno,  salidaFile_id, usuario_id) VALUES (?,?,?)";
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				st.setInt(1, vistoBueno);
				st.setString(2, idSalidaFile);
				st.setInt(3, usuario);
				st.execute();
				return true;

			} catch (SQLException ex) {
				close();

				System.err.println(ex.getMessage());
				return false;

			}
		}

	}

	public void saveJefe(String user, String selectedItem, String role) {

		int usuario_id = -1;
		try {
			PreparedStatement st = connect.prepareStatement("insert into user (user, password) values (?,?)");
			st.setString(1, user);
			st.setString(2, "reset");
			st.execute();

		} catch (SQLException ex) {
			close();

			System.err.println("saveJefe error " + ex.getMessage());
			ex.printStackTrace();
		}

		
		try {
			System.out.println("select * from user where user = '" + user + "'");
			PreparedStatement st = connect.prepareStatement("select * from user where user = '" + user + "'");
			ResultSet result = st.executeQuery();
			while (result.next()) {
				usuario_id = result.getInt("id");
				System.out.println("usuario_id " + usuario_id);
			}
			
		} catch (SQLException ex) {
			System.err.println("saveJefe error " + ex.getMessage());
			ex.printStackTrace();

		}

//		String sql = "UPDATE JEFES SET usuario_id = ? " + "WHERE nombre = ?";
		String sql = "INSERT INTO usuario_role (role, user_id, permiso, isJefe) values ('"+selectedItem+"', "+usuario_id+", '1', '1')";
		
		try {
			System.out.println(sql);
			PreparedStatement st = connect.prepareStatement(sql);
//			st.setInt(1, usuario_id);
//			st.setString(2, selectedItem);
//			st.executeUpdate();
			st.execute();
		} catch (SQLException ex) {
			System.err.println("updateJefe id " + ex.getMessage());
			ex.printStackTrace();

		}

	}

	public boolean agregarDestinatarioJefe(int entradaid, String jefe) {
		try {
			PreparedStatement st = connect
					.prepareStatement("insert into destinatario (entrada_id, role) values (?,?)");
			st.setInt(1, entradaid);
			st.setString(2, jefe);

			return st.execute();
		} catch (SQLException ex) {
			System.err.println("agregarDestinatarioJefe " + ex.getMessage());
			return false;
		}
	}

	public ArrayList<String> getDestinosJefes(int id) {
		ResultSet result = null;
		System.out.println("getDestinosJefes");
		ArrayList<String> resultado = new ArrayList<String>();
		try {
//			System.out.println("select destinatario.role from destinatario "
//					+ "LEFT JOIN entrada ON entrada.id = destinatario.entrada_id "
//					+ "LEFT JOIN usuario_role ON usuario_role.role = destinatario.role " 
//					+ "WHERE entrada.id = " + id +" "
//					+ "AND usuario_role.isJefe = 1");
//			PreparedStatement st = connect.prepareStatement("select destinatario.role from destinatario "
//					+ "LEFT JOIN entrada ON entrada.id = destinatario.entrada_id "
//					+ "LEFT JOIN usuario_role ON usuario_role.role = destinatario.role " 
//					+ "WHERE entrada.id = " + id +" "
//					+ "AND usuario_role.isJefe = 1");
			
			System.out.println("select destinatario.role from destinatario "
					+ "LEFT JOIN entrada ON entrada.id = destinatario.entrada_id "
					+ "LEFT JOIN role ON role.nombre_role = destinatario.role " 
					+ "WHERE entrada.id = " + id +" "
					+ "AND posicion IS NOT NULL");
			PreparedStatement st = connect.prepareStatement("select destinatario.role from destinatario "
					+ "LEFT JOIN entrada ON entrada.id = destinatario.entrada_id "
					+ "LEFT JOIN role ON role.nombre_role = destinatario.role " 
					+ "WHERE entrada.id = " + id +" "
					+ "AND posicion IS NOT NULL");
			result = st.executeQuery();
			while (result.next()) {
				resultado.add(result.getString("role"));
			}

		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
			return null;
		}

		return resultado;
	}

	public boolean borrarDestinosJefes(int id) {
		int result = -2;
		boolean exist = false;
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM destinatariosJefes WHERE entrada_id = ?");
			st.setInt(1, id);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("borrarDestinos " + ex.getMessage());
		}

		if (result == 1) {
			return true;
		} else
			return false;
	}

	public ResultSet mostrarEntradasPendienteVerV2(String date, Usuario usuario, String categoria) {
		System.out.println("mostrarEntradasPendienteVerV2");
		ResultSet result = null;
		try {
			// PreparedStatement st = connect.prepareStatement("select * from entrada where
			// fecha = '" + date + "' UNION select * from entrada where tramitado = '0'");
			// st = connect.prepareStatement("SELECT entrada.id AS id, Asunto, entrada.Fecha
			// AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM
			// entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id AND
			// COMENTARIO.visto = 0 AND COMENTARIO.usuario_id = "+idJefe1+ " WHERE
			// entrada.Fecha = '" + date +"'");
//			st = connect.prepareStatement("SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada " 
//			+"INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//			+"INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id " 
//			+"AND COMENTARIO.visto = 0 " 
//			+"AND COMENTARIO.usuario_id = '"+usuario.usuario_id+ "' "
//			+"WHERE entrada.Fecha = '" + date +"' " 
//			+"AND destinatariosJefes.jefe = '"+usuario.nombre_jefe +"'");

			if (usuario.username == null || usuario.username.equals("")) {

				if (categoria.equals("Todas")) {

					System.out.println("mostrarEntradasPendienteVerV2 categoria " + categoria);

					System.out.println();
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id WHERE destinatarios.negociado= '"
									+ usuario.role + "' " + "AND entrada.Fecha = '" + date + "' "

									+ "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id WHERE destinatarios.negociado= '"
									+ usuario.role + "' " + "AND entrada.tramitado = '0' " + "");
					result = st.executeQuery();

				} else {
					System.out.println("mostrarEntradasPendienteVerV2 categoria " + categoria);

					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + "AND entrada.Fecha = '"
									+ date + "' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' "

									+ "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' "
									+ "AND entrada.tramitado = '0' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria
									+ "'");
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + "AND entrada.Fecha = '"
									+ date + "' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' "

									+ "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' "
									+ "AND entrada.tramitado = '0' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria
									+ "'");

					result = st.executeQuery();
				}
			} else {

				if (categoria.equals("Todas")) {

					System.out.println("mostrarEntradasPendienteVerV2 categoria " + categoria);
					System.out.println();

					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = '"
									+ usuario.username + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = 'Todos'"
									+ "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.usuario_id
									+ "' " + "AND destinatariosJefes.jefe = 'Todos'" + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.usuario_id
									+ "' " + "AND destinatariosJefes.jefe = '" + usuario.username + "' " + "");
					result = st.executeQuery();
				} else {
					System.out.println("mostrarEntradasPendienteVerV2 categoria " + categoria);
					System.out.println();

					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = '"
									+ usuario.username + "' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria
									+ "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = 'Todos' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.usuario_id
									+ "' " + "AND destinatariosJefes.jefe = 'Todos' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.usuario_id
									+ "' " + "AND destinatariosJefes.jefe = '" + usuario.username + "' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");

					result = st.executeQuery();
				}
			}

		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasPendienteVerPorNegociadoV2(String date, Usuario usuario, String area) {
		System.out.println("mostrarEntradasCoronelPendienteV2");
		ResultSet result = null;
		try {
			// PreparedStatement st = connect.prepareStatement("select * from entrada where
			// fecha = '" + date + "' UNION select * from entrada where tramitado = '0'");
			// st = connect.prepareStatement("SELECT entrada.id AS id, Asunto, entrada.Fecha
			// AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM
			// entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id AND
			// COMENTARIO.visto = 0 AND COMENTARIO.usuario_id = "+idJefe1+ " WHERE
			// entrada.Fecha = '" + date +"'");
//			st = connect.prepareStatement("SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada " 
//			+"INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//			+"INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id " 
//			+"AND COMENTARIO.visto = 0 " 
//			+"AND COMENTARIO.usuario_id = '"+usuario.usuario_id+ "' "
//			+"WHERE entrada.Fecha = '" + date +"' " 
//			+"AND destinatariosJefes.jefe = '"+usuario.nombre_jefe +"'");

			if (usuario.username == null || usuario.username.equals("")) {
				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id WHERE destinatarios.negociado= '"
								+ usuario.role + "' " + "WHERE entrada.Fecha = '" + date + "' "

								+ "UNION "
								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id WHERE destinatarios.negociado= '"
								+ usuario.role + "' " + "WHERE entrada.tramitado = '0' " + "");
				result = st.executeQuery();
			} else {

//				PreparedStatement st = connect.prepareStatement("SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//						+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//						+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id WHERE destinatariosJefes.jefe = '" + usuario.nombre_jefe +"' "
//						+ "AND entrada.Fecha = '" + date +"' "
//						+ "UNION "
//						+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//						+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//						+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id WHERE destinatariosJefes.jefe = '" + usuario.nombre_jefe +"' "
//						+ "AND entrada.tramitado = '0' "
//						+ "");
				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
								+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = '"
								+ usuario.username + "' " + "UNION "
								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
								+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = 'Todos'"
								+ "UNION "
								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
								+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.usuario_id
								+ "' " + "AND destinatariosJefes.jefe = 'Todos'" + "UNION "
								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
								+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.usuario_id
								+ "' " + "AND destinatariosJefes.jefe = '" + usuario.username + "' " + "");
				result = st.executeQuery();
			}

		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasPorFecha(String date, Usuario usuario) {
		System.out.println("mostrarEntradasPorFecha");
		ResultSet result = null;
		try {
			// PreparedStatement st = connect.prepareStatement("select * from entrada where
			// fecha = '" + date + "' UNION select * from entrada where tramitado = '0'");
			// st = connect.prepareStatement("SELECT entrada.id AS id, Asunto, entrada.Fecha
			// AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM
			// entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id AND
			// COMENTARIO.visto = 0 AND COMENTARIO.usuario_id = "+idJefe1+ " WHERE
			// entrada.Fecha = '" + date +"'");
//			st = connect.prepareStatement("SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada " 
//			+"INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//			+"INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id " 
//			+"AND COMENTARIO.visto = 0 " 
//			+"AND COMENTARIO.usuario_id = '"+usuario.usuario_id+ "' "
//			+"WHERE entrada.Fecha = '" + date +"' " 
//			+"AND destinatariosJefes.jefe = '"+usuario.nombre_jefe +"'");

			if (usuario.username == null || usuario.username.equals("")) {
				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + "AND entrada.Fecha = '"
								+ date + "' "

								+ "UNION "
								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.Fecha = '" + date + "' "
								+ "");
				result = st.executeQuery();
			} else {

//				PreparedStatement st = connect.prepareStatement("SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//						+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//						+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id WHERE destinatariosJefes.jefe = '" + usuario.nombre_jefe +"' "
//						+ "AND entrada.Fecha = '" + date +"' "
//						+ "UNION "
//						+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//						+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//						+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id WHERE destinatariosJefes.jefe = '" + usuario.nombre_jefe +"' "
//						+ "AND entrada.tramitado = '0' "
//						+ "");
				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
								+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = '"
								+ usuario.username + "' " + "UNION"
								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
								+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = 'Todos'"
								+ "");
				result = st.executeQuery();
			}

		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasPorFechaYNegociadoSinTodos(String date, Usuario usuario, String area,
			String categoria) {

		System.out.println("mostrarEntradasPorFechaYNegociadoSinTodos 1");
		ResultSet result = null;
		try {
			if (usuario.username == null || usuario.username.equals("")) {

				if (categoria.equals("Todas")) {
					System.out.println("mostrarEntradasPorFechaYNegociadoSinTodos 2 categoria " + categoria);

					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + "AND entrada.Fecha = '"
									+ date + "' ");
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + "AND entrada.Fecha = '"
									+ date + "' ");

					result = st.executeQuery();

				} else {
					System.out.println("mostrarEntradasPorFechaYNegociadoSinTodos 3 categoria " + categoria);

					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + "AND entrada.Fecha = '"
									+ date + "' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + "AND entrada.Fecha = '"
									+ date + "' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");
					result = st.executeQuery();
				}
			} else {
				if (categoria.equals("Todas")) {
					System.out.println("mostrarEntradasPorFechaYNegociadoSinTodos 4 categoria " + categoria);

					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = '"
									+ usuario.username + "' " + "" + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = 'Todos'"
									+ "");
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = '"
									+ usuario.username + "' " + "" + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = 'Todos' "
									+ "");
					result = st.executeQuery();
				} else {
					System.out.println("mostrarEntradasPorFechaYNegociadoSinTodos 5 categoria " + categoria);

					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = '"
									+ usuario.username + "' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria
									+ "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = 'Todos' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");

					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = '"
									+ usuario.username + "' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria
									+ "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.Fecha = '" + date + "' " + "AND destinatariosJefes.jefe = 'Todos' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");
					result = st.executeQuery();
				}
			}

		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet searchRegistro(String word) {
		ResultSet result = null;
		try {

			System.out.println("SearchRegistro");
			System.out.println(
					"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id where asunto LIKE '%"
							+ word + "%'");
			PreparedStatement st = connect.prepareStatement(
					"SELECT entrada.id, Asunto, entrada.Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id where asunto LIKE '%"
							+ word + "%'");

			// PreparedStatement st = connect.prepareStatement("select * from entrada where
			// asunto LIKE '%" + word + "%'");
			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet searchJefesPorNegociadoPorAsunto(String word, Usuario usuario, String area, String categoria) {
		ResultSet result = null;

		try {

			if (usuario.username == null || usuario.username.equals("")) {

				if (categoria.equals("Todas")) {
					System.out.println("searchJefesPorNegociadoPorAsunto1 " + " word " + word  + " categoria " + categoria + " jefe " +usuario.username);
					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + ""
									+ "AND entrada.asunto LIKE '%" + word + "%' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.asunto LIKE '%" + word
									+ "%' " + "");
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + ""
									+ "AND entrada.asunto LIKE '%" + word + "%' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.asunto LIKE '%" + word
									+ "%' " + "");
					result = st.executeQuery();

				} else {
					System.out.println("searchJefesPorNegociadoPorAsunto2 " + " word " + word  + " categoria " + categoria + " jefe " +usuario.username);

					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + ""
									+ "AND entrada.asunto LIKE '%" + word + "%' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.asunto LIKE '%" + word
									+ "%' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + ""
									+ "AND entrada.asunto LIKE '%" + word + "%' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.asunto LIKE '%" + word
									+ "%' " + " " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");

					result = st.executeQuery();

				}
			} else {

				if (categoria.equals("Todas")) {

					System.out.println("searchJefesPorNegociadoPorAsunto3 " + " word " + word  + " categoria " + categoria + " jefe " +usuario.username + " area " + area);

					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE entrada.asunto LIKE '%" + word + "%' " + "AND destinatariosJefes.jefe = '"
									+ usuario.username + "' " + "AND destinatarios.negociado= '" + area + "' "
									+ "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE entrada.asunto LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = 'Todos' " + "AND destinatarios.negociado= '" + area
									+ "' " + "");
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE entrada.asunto LIKE '%" + word + "%' " + "AND destinatariosJefes.jefe = '"
									+ usuario.username + "' " + "AND destinatarios.negociado= '" + area + "' "
									+ "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE entrada.asunto LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = 'Todos' " + "AND destinatarios.negociado= '" + area
									+ "' " + "");
					result = st.executeQuery();

				} else {
					
					System.out.println("searchJefesPorNegociadoPorAsunto4 " + " word " + word  + " categoria " + categoria + " jefe " +usuario.username + " area " + area);

					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.asunto LIKE '%" + word + "%' " + "AND destinatariosJefes.jefe = '"
									+ usuario.username + "' " + "AND destinatarios.negociado= '" + area + "' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.asunto LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = 'Todos' " + "AND destinatarios.negociado= '" + area
									+ "' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");

					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.asunto LIKE '%" + word + "%' " + "AND destinatariosJefes.jefe = '"
									+ usuario.username + "' " + "AND destinatarios.negociado= '" + area + "' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.asunto LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = 'Todos' " + "AND destinatarios.negociado= '" + area
									+ "' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");
					result = st.executeQuery();
				}

			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet searchJefesPorNegociadoPorNumEntrada(String word, Usuario usuario, String area, String categoria) {
		ResultSet result = null;

		try {

			if (usuario.username == null || usuario.username.equals("")) {
				if (categoria.equals("Todas")) {
					System.out.println("searchJefesPorNegociadoPorNumEntrada categoria " + categoria);
					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + ""
									+ "AND entrada.numeroEntrada LIKE '%" + word + "%' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.numeroEntrada LIKE '%"
									+ word + "%' ");
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + ""
									+ "AND entrada.numeroEntrada LIKE '%" + word + "%' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.numeroEntrada LIKE '%"
									+ word + "%' ");
					result = st.executeQuery();
				} else {
					System.out.println("searchJefesPorNegociadoPorNumEntrada categoria " + categoria);
					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + ""
									+ "AND entrada.numeroEntrada LIKE '%" + word + "%' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.numeroEntrada LIKE '%"
									+ word + "%' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");

					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' " + ""
									+ "AND entrada.numeroEntrada LIKE '%" + word + "%' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.numeroEntrada LIKE '%"
									+ word + "%' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");
					result = st.executeQuery();
				}
			} else {
				if (categoria.equals("Todas")) {
					System.out.println("searchJefesPorNegociadoPorNumEntrada categoria " + categoria);

					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE entrada.numeroEntrada LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = '" + usuario.username + "' "
									+ "AND destinatarios.negociado= '" + area + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE entrada.numeroEntrada LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = 'Todos' " + "AND destinatarios.negociado= '" + area
									+ "' " + "");
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE entrada.numeroEntrada LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = '" + usuario.username + "' "
									+ "AND destinatarios.negociado= '" + area + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE entrada.numeroEntrada LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = 'Todos' " + "AND destinatarios.negociado= '" + area
									+ "' " + "");
					result = st.executeQuery();
				} else {
					System.out.println("searchJefesPorNegociadoPorNumEntrada categoria " + categoria);

					System.out.println();

					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.numeroEntrada LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = '" + usuario.username + "' "
									+ "AND destinatarios.negociado= '" + area + "' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.numeroEntrada LIKE '%" + word + "%' "
									+ "AND destinatariosJefes.jefe = 'Todos' " + "AND destinatarios.negociado= '" + area
									+ "' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");
					result = st.executeQuery();
				}

			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasPendienteTramitarRegistroTodos(String categoria) {

		System.out.println("mostrarEntradasPendienteTramitarRegistroTodos");
		ResultSet result = null;
		try {
			// PreparedStatement st = connect.prepareStatement(
			// "select * from entrada where fecha = '" + date + "' UNION select * from
			// entrada where jefe1 = '0'");
			if (categoria.equals("Todas")) {

			PreparedStatement st = connect.prepareStatement(
					"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
					+ "FROM entrada "
							+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "WHERE entrada.tramitado = '0'");
			result = st.executeQuery();
			}else {
				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
						+ "FROM entrada "
								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
								+ "WHERE entrada.tramitado = '0'"
								+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");
				result = st.executeQuery();
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasPendienteTramitarTodos(Usuario usuario, String categoria) {
		System.out.println("mostrarEntradasPendienteTramitarTodos");
		ResultSet result = null;
		try {
			// PreparedStatement st = connect.prepareStatement("select * from entrada where
			// fecha = '" + date + "' UNION select * from entrada where tramitado = '0'");
			// st = connect.prepareStatement("SELECT entrada.id AS id, Asunto, entrada.Fecha
			// AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM
			// entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id AND
			// COMENTARIO.visto = 0 AND COMENTARIO.usuario_id = "+idJefe1+ " WHERE
			// entrada.Fecha = '" + date +"'");
//			st = connect.prepareStatement("SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada " 
//			+"INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//			+"INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id " 
//			+"AND COMENTARIO.visto = 0 " 
//			+"AND COMENTARIO.usuario_id = '"+usuario.usuario_id+ "' "
//			+"WHERE entrada.Fecha = '" + date +"' " 
//			+"AND destinatariosJefes.jefe = '"+usuario.nombre_jefe +"'");

			if (usuario.username == null || usuario.username.equals("")) {
				
				if (categoria.equals("Todas")) {

				System.out.println("mostrarEntradasPendienteTramitarTodos 1");
				System.out.println(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id WHERE destinatarios.negociado= '"
								+ usuario.role + "' " + "AND entrada.tramitado = '0' " + "");
				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id WHERE destinatarios.negociado= '"
								+ usuario.role + "' " + "AND entrada.tramitado = '0' " + "");
				result = st.executeQuery();
				}else {
					System.out.println("mostrarEntradasPendienteTramitarTodos 2");
					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatarios.negociado= '"
									+ usuario.role + "' " + "AND entrada.tramitado = '0' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatarios.negociado= '"
									+ usuario.role + "' " + "AND entrada.tramitado = '0' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");
					result = st.executeQuery();
				}
			} else {
				if (categoria.equals("Todas")) {

					System.out.println("mostrarEntradasPendienteTramitarTodos 3");
				System.out.println(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "

								+ "WHERE entrada.tramitado = '0' " + "AND destinatariosJefes.jefe = '"
								+ usuario.username + "' " + "" + "UNION "
								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
								+ "WHERE entrada.tramitado = '0' " + "AND destinatariosJefes.jefe = 'Todos' ");
				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
								+ "WHERE entrada.tramitado = '0' " + "AND destinatariosJefes.jefe = '"
								+ usuario.username + "' "
								+ "UNION "
								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
								+ "WHERE entrada.tramitado = '0' " + "AND destinatariosJefes.jefe = 'Todos' "
								+ "");
				result = st.executeQuery();
				}else {
					System.out.println("mostrarEntradasPendienteTramitarTodos 4");
					System.out.println();
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.tramitado = '0' " + "AND destinatariosJefes.jefe = '"
									+ usuario.username + "' " + "" 
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' "
									+ "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE entrada.tramitado = '0' " + "AND destinatariosJefes.jefe = 'Todos' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");

					result = st.executeQuery();
				}
			}

		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasPendienteTramitarRegistroPorNegociado(String area, String categoria) {
		System.out.println("mostrarEntradasPendienteTramitarRegistroPorNegociado");
		ResultSet result = null;
		try {
			// PreparedStatement st = connect.prepareStatement(
			// "select * from entrada where fecha = '" + date + "' UNION select * from
			// entrada where jefe1 = '0'");
			if (categoria.equals("Todas")) {

			PreparedStatement st = connect.prepareStatement(
					"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
							+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "WHERE entrada.tramitado = '0' " + "AND destinatarios.negociado = '" + area + "'");
			result = st.executeQuery();
			}else {
				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "LEFT OUTER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
								+ "WHERE entrada.tramitado = '0' " + "AND destinatarios.negociado = '" + area + "' "
								+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");

				result = st.executeQuery();
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasPendienteTramitarPorNegociado(String area, Usuario usuario, String categoria) {

		System.out.println("mostrarEntradasPendienteTramitarPorNegociado");
		ResultSet result = null;
		try {
			if (usuario.username == null || usuario.username.equals("")) {
				if (categoria.equals("Todas")) {
				System.out.println("mostrarEntradasPendienteTramitarPorNegociado1 categoria " + categoria);
				System.out.println(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE destinatarios.negociado= '" + usuario.role + "' "
								+ "AND entrada.tramitado = '0' " + "UNION "
								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.tramitado = '0' " + "");
				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE destinatarios.negociado= '" + usuario.role + "' "
								+ "AND entrada.tramitado = '0' " + "UNION "
								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.tramitado = '0' " + "");
				result = st.executeQuery();
				}else {
					System.out.println("mostrarEntradasPendienteTramitarPorNegociado2 categoria " + categoria);

					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' "
									+ "AND entrada.tramitado = '0' " 
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' "
									+ "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.tramitado = '0' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");

					result = st.executeQuery();
				}
			} else {
				if (categoria.equals("Todas")) {
					
				System.out.println("mostrarEntradasPendienteTramitarPorNegociado3 categoria " + categoria);

				System.out.println(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE destinatariosJefes.jefe = '" + usuario.username + "' "
								+ "AND destinatarios.negociado= '" + area + "' " + "AND entrada.tramitado = '0' "
								+ "UNION "
								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE destinatariosJefes.jefe = 'Todos'" + "AND destinatarios.negociado= '" + area
								+ "' " + "AND entrada.tramitado = '0' ");
				PreparedStatement st = connect.prepareStatement(
						"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE destinatariosJefes.jefe = '" + usuario.username + "' "
								+ "AND destinatarios.negociado= '" + area + "' " + "AND entrada.tramitado = '0' "
								+ "UNION "
								+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
								+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
								+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
								+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
								+ "WHERE destinatariosJefes.jefe = 'Todos'" + "AND destinatarios.negociado= '" + area
								+ "' " + "AND entrada.tramitado = '0' ");
				result = st.executeQuery();
				}else {
					System.out.println("mostrarEntradasPendienteTramitarPorNegociado4 categoria " + categoria);

					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatariosJefes.jefe = '" + usuario.username + "' "
									+ "AND destinatarios.negociado= '" + area + "' " + "AND entrada.tramitado = '0' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' "
									+ "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatariosJefes.jefe = 'Todos'" + "AND destinatarios.negociado= '" + area
									+ "' " + "AND entrada.tramitado = '0' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");
					result = st.executeQuery();
				}
			}

		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasPendienteVerTodos(Usuario usuario, String categoria) {

		System.out.println("mostrarEntradasPendienteVerTodos");
		ResultSet result = null;
		try {
			// PreparedStatement st = connect.prepareStatement("select * from entrada where
			// fecha = '" + date + "' UNION select * from entrada where tramitado = '0'");
			// st = connect.prepareStatement("SELECT entrada.id AS id, Asunto, entrada.Fecha
			// AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM
			// entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id AND
			// COMENTARIO.visto = 0 AND COMENTARIO.usuario_id = "+idJefe1+ " WHERE
			// entrada.Fecha = '" + date +"'");
//			st = connect.prepareStatement("SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada " 
//			+"INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//			+"INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id " 
//			+"AND COMENTARIO.visto = 0 " 
//			+"AND COMENTARIO.usuario_id = '"+usuario.usuario_id+ "' "
//			+"WHERE entrada.Fecha = '" + date +"' " 
//			+"AND destinatariosJefes.jefe = '"+usuario.nombre_jefe +"'");

			if (usuario.username == null || usuario.username.equals("")) {

				if (categoria.equals("Todas")) {

					System.out.println("mostrarEntradasPendienteVerTodos categoria " + categoria);
					System.out.println();
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.tramitado = '0' "
									+ "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' "
									+ "AND entrada.tramitado = '0' ");
					result = st.executeQuery();
				} else {
					System.out.println("mostrarEntradasPendienteVerTodos categoria " + categoria);
					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.tramitado = '0' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' "
									+ "AND entrada.tramitado = '0' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria
									+ "'");
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.tramitado = '0' "
									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' "
									+ "AND entrada.tramitado = '0' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria
									+ "'");

					result = st.executeQuery();
				}
			} else {

				if (categoria.equals("Todas")) {

					System.out.println("else mostrarEntradasPendienteVerTodos categoria " + categoria);
//					System.out.println(
//							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.jefe_id
//									+ "' " + "AND destinatariosJefes.jefe = 'Todos' " + "UNION "
//									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.jefe_id
//									+ "' " + "AND destinatariosJefes.jefe = '" + usuario.nombre_jefe + "' " + "");
//					PreparedStatement st = connect.prepareStatement(
//							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.jefe_id
//									+ "' " + "AND destinatariosJefes.jefe = 'Todos' " + "UNION "
//									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.jefe_id
//									+ "' " + "AND destinatariosJefes.jefe = '" + usuario.nombre_jefe + "' " + "");
					System.out.println("SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
							+ "FROM entrada "
							+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id AND (destinatariosJefes.jefe = 'Todos' OR destinatariosJefes.jefe = '"+ usuario.username +"') "
							+ "WHERE entrada.id NOT IN "
							+ "(SELECT entrada.id "
							+ "FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
							+ "AND COMENTARIO.visto != 0 AND COMENTARIO.usuario_id = '"+ usuario.jefe_id +"' AND destinatariosJefes.jefe = 'Todos' "
							+ "UNION "
							+ "SELECT entrada.id "
							+ "FROM entrada "
							+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
							+ "AND COMENTARIO.visto != 0 AND COMENTARIO.usuario_id = '"+ usuario.jefe_id +"' AND destinatariosJefes.jefe = '" + usuario.username +"')");
							PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
					+ "FROM entrada "
					+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
					+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id AND (destinatariosJefes.jefe = 'Todos' OR destinatariosJefes.jefe = '"+ usuario.username +"') "
					+ "WHERE entrada.id NOT IN "
					+ "(SELECT entrada.id "
					+ "FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
					+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
					+ "AND COMENTARIO.visto != 0 AND COMENTARIO.usuario_id = '"+ usuario.jefe_id +"' AND destinatariosJefes.jefe = 'Todos' "
					+ "UNION "
					+ "SELECT entrada.id "
					+ "FROM entrada "
					+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
					+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
					+ "AND COMENTARIO.visto != 0 AND COMENTARIO.usuario_id = '"+ usuario.jefe_id +"' AND destinatariosJefes.jefe = '" + usuario.username +"')");
					
					result = st.executeQuery();
				} else {
					System.out.println("else mostrarEntradasPendienteVerTodos categoria " + categoria);
//					System.out.println(
//							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.jefe_id
//									+ "' " + "AND destinatariosJefes.jefe = 'Todos' "
//									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
//									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.jefe_id
//									+ "' " + "AND destinatariosJefes.jefe = '" + usuario.nombre_jefe + "' "
//									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");
//					PreparedStatement st = connect.prepareStatement(
//							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.jefe_id
//									+ "' " + "AND destinatariosJefes.jefe = 'Todos' "
//									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
//									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.jefe_id
//									+ "' " + "AND destinatariosJefes.jefe = '" + usuario.nombre_jefe + "' "
//									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "'");
					System.out.println("SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
							+ "FROM entrada "
							+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID " 
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id AND (destinatariosJefes.jefe = 'Todos' OR destinatariosJefes.jefe = '"+ usuario.username +"') "
							+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' "
							+ "WHERE entrada.id NOT IN "
							+ "(SELECT entrada.id "
							+ "FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
							+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID " 
							+ "AND COMENTARIO.visto != 0 AND COMENTARIO.usuario_id = '"+ usuario.jefe_id +"' AND (destinatariosJefes.jefe = 'Todos' OR destinatariosJefes.jefe = '" + usuario.username +"') "
							+ ""
							+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "')");
					PreparedStatement st = connect.prepareStatement("SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
							+ "FROM entrada "
							+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID " 
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id AND (destinatariosJefes.jefe = 'Todos' OR destinatariosJefes.jefe = '"+ usuario.username +"') "
							+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' "
							+ "WHERE entrada.id NOT IN "
							+ "(SELECT entrada.id "
							+ "FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
							+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID " 
							+ "AND COMENTARIO.visto != 0 AND COMENTARIO.usuario_id = '"+ usuario.jefe_id +"' AND (destinatariosJefes.jefe = 'Todos' OR destinatariosJefes.jefe = '" + usuario.username +"') "
							+ ""
							+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "')");
					
					result = st.executeQuery();
				}
			}

		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasPendientesVerPorFechaYNegociado(String date, Usuario usuario, String area,
			String categoria) {

		System.out.println("mostrarEntradasPendientesVerPorFechaYNegociado");
		ResultSet result = null;
		try {
			// PreparedStatement st = connect.prepareStatement("select * from entrada where
			// fecha = '" + date + "' UNION select * from entrada where tramitado = '0'");
			// st = connect.prepareStatement("SELECT entrada.id AS id, Asunto, entrada.Fecha
			// AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM
			// entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id AND
			// COMENTARIO.visto = 0 AND COMENTARIO.usuario_id = "+idJefe1+ " WHERE
			// entrada.Fecha = '" + date +"'");
//			st = connect.prepareStatement("SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada " 
//			+"INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//			+"INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id " 
//			+"AND COMENTARIO.visto = 0 " 
//			+"AND COMENTARIO.usuario_id = '"+usuario.usuario_id+ "' "
//			+"WHERE entrada.Fecha = '" + date +"' " 
//			+"AND destinatariosJefes.jefe = '"+usuario.nombre_jefe +"'");

			if (usuario.username == null || usuario.username.equals("")) {
				if (categoria.equals("Todas")) {

					System.out.println("mostrarEntradasPendientesVerPorFechaYNegociado NEGOCIado " + area
							+ " categoria " + categoria);
					System.out.println(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.tramitado = '0' "
									+ "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' "
									+ "AND entrada.tramitado = '0' ");
					PreparedStatement st = connect.prepareStatement(
							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.tramitado = '0' "
									+ "UNION "
									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
									+ "WHERE destinatarios.negociado= '" + usuario.role + "' "
									+ "AND entrada.tramitado = '0' ");
					result = st.executeQuery();
				} else {
					System.out.println("mostrarEntradasPendientesVerPorFechaYNegociado NEGOCIado " + area
							+ " categoria " + categoria);
//					System.out.println(
//							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.tramitado = '0' "
//									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
//									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//									+ "WHERE destinatarios.negociado= '" + usuario.role + "' "
//									+ "AND entrada.tramitado = '0' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria
//									+ "' ");
//					PreparedStatement st = connect.prepareStatement(
//							"SELECT entrada.id AS id, Asunto, entrada.Fecha as Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//									+ "WHERE destinatarios.negociado= 'Todos' " + "AND entrada.tramitado = '0' "
//									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
//									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//									+ "WHERE destinatarios.negociado= '" + usuario.role + "' "
//									+ "AND entrada.tramitado = '0' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria
//									+ "' ");
					System.out.println("SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
							+ "FROM entrada "
							+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
							+ "AND (destinatariosJefes.jefe = 'Todos' OR destinatariosJefes.jefe = '"+ usuario.username +"') "
							+ "AND destinatarios.negociado= '"+ area + "' "
							+ "WHERE entrada.id NOT IN "
							+ "(SELECT entrada.id "
							+ "FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "AND COMENTARIO.visto != 0 AND COMENTARIO.usuario_id = '"+ usuario.jefe_id +"' AND (destinatariosJefes.jefe = 'Todos' OR destinatariosJefes.jefe = '" + usuario.username +"') "
							+ ""
							+ "AND destinatarios.negociado= '"+ area + "')");
					PreparedStatement st = connect.prepareStatement("SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
							+ "FROM entrada "
							+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
							+ "AND (destinatariosJefes.jefe = 'Todos' OR destinatariosJefes.jefe = '"+ usuario.username +"') "
							+ "AND destinatarios.negociado= '"+ area + "' "
							+ "WHERE entrada.id NOT IN "
							+ "(SELECT entrada.id "
							+ "FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "AND COMENTARIO.visto != 0 AND COMENTARIO.usuario_id = '"+ usuario.jefe_id +"' AND (destinatariosJefes.jefe = 'Todos' OR destinatariosJefes.jefe = '" + usuario.username +"') "
							+ ""
							+ "AND destinatarios.negociado= '"+ area + "')");
					result = st.executeQuery();
				}
			} else {

				if (categoria.equals("Todas")) {

					System.out.println(
							"mostrarEntradasPendientesVerPorFechaYNegociado jefes " + area + " categoria " + categoria);
//					System.out.println(
//							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
					
//									+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.jefe_id
//									+ "' " + "AND destinatariosJefes.jefe = 'Todos' " + "AND destinatarios.negociado= '"
//									+ area + "' "
//
//									+ "UNION "
//									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//
//									+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.jefe_id
//									+ "' " + "AND destinatariosJefes.jefe = '" + usuario.nombre_jefe + "' "
//									+ "AND destinatarios.negociado= '" + area + "' " + "");
//					PreparedStatement st = connect.prepareStatement(
//							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.jefe_id
//									+ "' " + "AND destinatariosJefes.jefe = 'Todos' " + "AND destinatarios.negociado= '"
//									+ area + "' "
//
//									+ "UNION "
//									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//
//									+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.jefe_id
//									+ "' " + "AND destinatariosJefes.jefe = '" + usuario.nombre_jefe + "' "
//									+ "AND destinatarios.negociado= '" + area + "' " + "");
					System.out.println("SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
							+ "FROM entrada "
							+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
							+ "AND (destinatariosJefes.jefe = 'Todos' OR destinatariosJefes.jefe = '"+ usuario.username +"') "
							+ "AND destinatarios.negociado= '"+ area + "' "
							+ "WHERE entrada.id NOT IN "
							+ "(SELECT entrada.id "
							+ "FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "AND COMENTARIO.visto != 0 AND COMENTARIO.usuario_id = '"+ usuario.jefe_id +"' AND (destinatariosJefes.jefe = 'Todos' OR destinatariosJefes.jefe = '" + usuario.username +"') "
							+ "AND destinatarios.negociado= '"+ area + "')");
					PreparedStatement st = connect.prepareStatement("SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
							+ "FROM entrada "
							+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
							+ "AND (destinatariosJefes.jefe = 'Todos' OR destinatariosJefes.jefe = '"+ usuario.username +"') "
							+ "AND destinatarios.negociado= '"+ area + "' "
							+ "WHERE entrada.id NOT IN "
							+ "(SELECT entrada.id "
							+ "FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "AND COMENTARIO.visto != 0 AND COMENTARIO.usuario_id = '"+ usuario.jefe_id +"' AND (destinatariosJefes.jefe = 'Todos' OR destinatariosJefes.jefe = '" + usuario.username +"') "
							+ "AND destinatarios.negociado= '"+ area + "')");
					
					result = st.executeQuery();
				} else {

					System.out.println(
							"mostrarEntradasPendientesVerPorFechaYNegociado jefes " + area + " categoria " + categoria);
					System.out.println();
//					PreparedStatement st = connect.prepareStatement(
//							"SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//									+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.jefe_id
//									+ "' " + "AND destinatariosJefes.jefe = 'Todos' " + "AND destinatarios.negociado= '"
//									+ area + "' " + "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' " + "UNION "
//									+ "SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada FROM entrada "
//									+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
//									+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
//									+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
//									+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID "
//									+ "AND COMENTARIO.visto = 0 " + "AND COMENTARIO.usuario_id = '" + usuario.jefe_id
//									+ "' " + "AND destinatariosJefes.jefe = '" + usuario.nombre_jefe + "' "
//									+ "AND destinatarios.negociado= '" + area + "' "
//									+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' ");
					
					System.out.println("SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
							+ "FROM entrada "
							+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
							+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID " 
							+ "AND (destinatariosJefes.jefe = 'Todos' OR destinatariosJefes.jefe = '"+ usuario.username +"') "
							+ "AND destinatarios.negociado= '"+ area + "' "
							+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' "
							+ "WHERE entrada.id NOT IN "
							+ "(SELECT entrada.id "
							+ "FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID " 
							+ "AND COMENTARIO.visto != 0 AND COMENTARIO.usuario_id = '"+ usuario.jefe_id +"' AND (destinatariosJefes.jefe = 'Todos' OR destinatariosJefes.jefe = '" + usuario.username +"') "
							+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' "
							+ "AND destinatarios.negociado= '"+ area + "')");
					PreparedStatement st = connect.prepareStatement("SELECT entrada.id AS id, Asunto, entrada.Fecha AS Fecha, Area, visto, usuario_id, Confidencial, tramitado, canalEntrada "
							+ "FROM entrada "
							+ "INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
							+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID " 
							+ "AND (destinatariosJefes.jefe = 'Todos' OR destinatariosJefes.jefe = '"+ usuario.username +"') "
							+ "AND destinatarios.negociado= '"+ area + "' "
							+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' "
							+ "WHERE entrada.id NOT IN "
							+ "(SELECT entrada.id "
							+ "FROM entrada INNER JOIN COMENTARIO ON entrada.id = COMENTARIO.entrada_id "
							+ "INNER JOIN destinatariosJefes ON entrada.id = destinatariosJefes.entrada_id "
							+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
							+ "INNER JOIN CATEGORIA_ENTRADA ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID " 
							+ "AND COMENTARIO.visto != 0 AND COMENTARIO.usuario_id = '"+ usuario.jefe_id +"' AND (destinatariosJefes.jefe = 'Todos' OR destinatariosJefes.jefe = '" + usuario.username +"') "
							+ "AND CATEGORIA_ENTRADA.CATEGORIA = '" + categoria + "' "
							+ "AND destinatarios.negociado= '"+ area + "')");

					result = st.executeQuery();
				}
			}

		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public boolean eliminarDestinatarios(int entradaid) {
		int result = -2;
		boolean exist = false;
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM destinatario WHERE entrada_id = ?");
			st.setInt(1, entradaid);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("error borrando destinatarios" + ex.getMessage());
		}
		if (result == 1) {
			return true;
		} else
			return false;

	}

	public boolean eliminarDestinatariosJefes(int entradaid) {
		int result = -2;
		boolean exist = false;
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM destinatariosJefes WHERE entrada_id = ?");
			st.setInt(1, entradaid);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("error borrando destinatariosJefes" + ex.getMessage());
		}
		if (result == 1) {
			return true;
		} else
			return false;

	}

	public boolean deleteAntecedentesFiles(int id) {
		System.out.println("deleteAntecedentesFiles Id" + id);
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement("select * from antecedentesFiles where entrada_id=" + id);
			result = st.executeQuery();
			while (result.next()) {
				System.out.println("deleteAntecedentesFiles " + result.getString("file"));
				new File(result.getString("file")).delete();
			}
			return true;
		} catch (SQLException ex) {
			return false;
		}
	}

	public boolean eliminarAntecedentes(int entradaid) {
		int result = -2;
		boolean exist = false;
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM antecedentesFiles WHERE entrada_id = ?");
			st.setInt(1, entradaid);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("error borrando eliminarAntecedentes" + ex.getMessage());
		}
		if (result == 1) {
			return true;
		} else
			return false;

	}

	public boolean deleteSalidaFiles(int id) {
		System.out.println("deleteSalidaFiles Id" + id);
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement("select * from salidaFiles where entrada_id=" + id);
			result = st.executeQuery();
			while (result.next()) {
				new File(result.getString("file")).delete();
			}
			return true;
		} catch (SQLException ex) {
			return false;
		}
	}

	public boolean eliminarSalida(int entradaid) {
		int result = -2;
		boolean exist = false;
		ResultSet resultset = null;
		int salidaFilesId = -1;

		try {
			PreparedStatement st = connect.prepareStatement("select * from salidaFiles where entrada_id=" + entradaid);
			resultset = st.executeQuery();
			while (resultset.next()) {
				salidaFilesId = resultset.getInt("id");
			}
		} catch (SQLException ex) {
			return false;
		}
		
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM salidaFiles WHERE entrada_id = ?");
			st.setInt(1, entradaid);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("error borrando eliminarSalida" + ex.getMessage());
		}
		
		
		try {
			System.out.println("DELETE FROM vistoBuenoJefes WHERE salidaFile_id = '"+salidaFilesId+"'");
			PreparedStatement st = connect.prepareStatement("DELETE FROM vistoBuenoJefes WHERE salidaFile_id = '"+salidaFilesId+"'");
			result = st.executeUpdate();
			System.out.println("result " + result);
		} catch (SQLException ex) {
			System.err.println("error borrando VistoBuenoJefes" + ex.getMessage());
		}
		
		if (result == 1) {
			return true;
		} else
			return false;

	}

	public boolean deleteSalidaFile(int id) {
		System.out.println("deleteSalidaFile Id" + id);
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement("select * from salidaFiles where id =" + id);
			result = st.executeQuery();
			while (result.next()) {
				System.out.println("deleteSalidaFile " + result.getString("file"));
				new File(result.getString("file")).delete();
			}

			st = connect.prepareStatement("delete from salidaFiles where id =" + id);
			int resultado = st.executeUpdate();
			System.out.println("resultado " + resultado);
			if (resultado == 0) {
				return false;
			} else {
				return true;
			}

		} catch (SQLException ex) {
			System.out.println(ex);
			return false;
		}
	}

	public boolean updateFileSalida(String idSalida, String file, int idEntrada, String fecha, String asunto,
			String destino) {
		String sql = "UPDATE salidaFiles SET file = ?, fecha = ?, destino = ?, asunto = ? " + "WHERE id = ?";
		int result = -2;
		try {
			PreparedStatement st = connect.prepareStatement(sql);
			st.setString(1, file);
			st.setString(2, fecha);
			st.setString(3, destino);
			st.setString(4, asunto);
			st.setString(5, idSalida);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("updateFileSalida " + ex.getMessage());
		}

		if (result == 1) {
			return true;
		} else
			return false;

	}

	public boolean updateFileAntecedentes(String idAntecedente, String file, int idEntrada, String fecha, String tipo,
			String destino, String asunto, String observaciones) {
		String sql = "UPDATE antecedentesFiles SET file = ?, fecha = ?, tipo = ?, destino = ?, asunto = ?, observaciones = ? "
				+ "WHERE id = ?";
		System.out.println("observaciones " + observaciones);
		System.out.println("destino  " + destino);
		System.out.println("asunto " + asunto);

		int result = -2;
		try {
			PreparedStatement st = connect.prepareStatement(sql);
			st.setString(1, file);
			st.setString(2, fecha);
			st.setString(3, tipo);
			st.setString(4, destino);
			st.setString(5, asunto);
			st.setString(6, observaciones);
			st.setString(7, idAntecedente);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("updateFileAntecedentesa " + ex.getMessage());
		}

		if (result == 1) {
			return true;
		} else
			return false;

	}

	public boolean updateFileEntrada(String idEntradaFile, String file, int idEntrada, String fecha, String destino,
			String asunto, String observaciones) {
		System.out.println("updateFileEntrada");
		String sql = "UPDATE files SET file = ?, fecha = ?, origen = ?, asunto = ?, observaciones = ? "
				+ "WHERE id = ?";

		int result = -2;
		try {
			PreparedStatement st = connect.prepareStatement(sql);
			st.setString(1, file);
			st.setString(2, fecha);
			st.setString(3, destino);
			st.setString(4, asunto);
			st.setString(5, observaciones);
			st.setString(6, idEntradaFile);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("updateFileEntrada " + ex.getMessage());
		}

		if (result == 1) {
			return true;
		} else
			return false;

	}

	public int getVistoBuenoSalida(int usuario_id, String salidaId) {
		ResultSet result = null;
		int resultado = -1;
		try {
			PreparedStatement st = connect.prepareStatement("select * from vistoBuenoJefes where usuario_id='"
					+ usuario_id + "' AND salidaFile_id= '" + salidaId + "'");
			result = st.executeQuery();
			while (result.next()) {
//				System.out.println("posicion " + result.getInt("posicion"));
				resultado = result.getInt("vistoBueno");
				return resultado;
			}
			return -1;
		} catch (SQLException ex) {
			System.err.println("getVistoBuenoSalida" + ex.getMessage());
			return -1;
		}
	}

	public boolean deleteAntecedentesFile(int id) {
		System.out.println("deleteAntecedentesFile Id" + id);
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement("select * from antecedentesFiles where id =" + id);
			result = st.executeQuery();
			while (result.next()) {
				System.out.println("deleteAntecedentesFile " + result.getString("file"));
				new File(result.getString("file")).delete();
			}

			st = connect.prepareStatement("delete from antecedentesFiles where id =" + id);
			int resultado = st.executeUpdate();
			System.out.println("resultado " + resultado);
			if (resultado == 0) {
				return false;
			} else {
				return true;
			}

		} catch (SQLException ex) {
			System.out.println(ex);
			return false;
		}
	}

	public boolean deleteEntradaFile(int id) {
		System.out.println("deleteAntecedentesFile Id" + id);
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement("select * from files where id =" + id);
			result = st.executeQuery();
			while (result.next()) {
				System.out.println("deleteEntradaFile " + result.getString("file"));
				new File(result.getString("file")).delete();
			}

			st = connect.prepareStatement("delete from files where id =" + id);
			int resultado = st.executeUpdate();
			System.out.println("resultado " + resultado);
			if (resultado == 0) {
				return false;
			} else {
				return true;
			}

		} catch (SQLException ex) {
			System.out.println(ex);
			return false;
		}
	}

	public ResultSet getEntradaFilesById(int id) {

		System.out.println("getEntradaFilesById");
		ResultSet result = null;
		try {

			PreparedStatement st = connect.prepareStatement("select * from files where entrada_id=" + id);

			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println("getEntradaFilesById " + ex.getMessage());
		}
		return result;

	}

	public boolean entradaExist(int idTemp) {
		// TODO Auto-generated method stub
		ResultSet result = null;

		try {

			PreparedStatement st = connect.prepareStatement("select * from entrada where id=" + idTemp);

			result = st.executeQuery();
			while (result.next()) {
				return true;
			}
		} catch (SQLException ex) {
			System.err.println("entradaExist " + ex.getMessage());
		}
		return false;
	}

	public boolean addCategoria(String categoria) {

		// CREATE TABLE IF NOT EXIST CATEGORIA (NOMBRE TEXT UNIQUE);

		try {
			PreparedStatement st = connect
					.prepareStatement("CREATE TABLE IF NOT EXISTS CATEGORIA (NOMBRE TEXT UNIQUE)");
			st.execute();
		} catch (SQLException ex) {
			System.err.println("create table error " + ex.getMessage());
		}

		try {
			PreparedStatement st = connect.prepareStatement("insert into CATEGORIA (NOMBRE) values (?)");
			st.setString(1, categoria);
			return st.execute();
		} catch (SQLException ex) {
			System.err.println("Save categoria error " + ex.getMessage());
			return false;
		}
	}

	public boolean CheckIfExistCategoria(String categoria) {
		ResultSet result = null;
		try {
			PreparedStatement st = connect
					.prepareStatement("select * from CATEGORIA where LOWER(nombre) LIKE LOWER('%" + categoria + "%')");
			result = st.executeQuery();
			if (result.next()) {
				return true;
			} else
				return false;
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
			return false;
		}
	}

	public ArrayList<String> mostrarCategorias() {
		ResultSet result = null;
		ArrayList<String> names = new ArrayList<String>();
		try {
			PreparedStatement st = connect.prepareStatement("select * from CATEGORIA");
			result = st.executeQuery();
			while (result.next()) {
				names.add(result.getString("NOMBRE"));
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return names;

	}

	public boolean borrarCategorias(int id) {
		int result = -2;
		boolean exist = false;
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM CATEGORIA_ENTRADA WHERE entrada_id = ?");
			st.setInt(1, id);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("borrarCategorias " + ex.getMessage());
		}

		if (result == 1) {
			return true;
		} else
			return false;

	}

	public boolean agregarCategoriaEntrada(int entradaid, String categoria) {

		try {
			PreparedStatement st = connect.prepareStatement(
					"CREATE TABLE IF NOT EXISTS CATEGORIA_ENTRADA (CATEGORIA TEXT REFERENCES CATEGORIA (NOMBRE) ON DELETE CASCADE ON UPDATE CASCADE, ENTRADA_ID INTEGER REFERENCES entrada (id) ON DELETE CASCADE ON UPDATE CASCADE);");
			st.execute();
		} catch (SQLException ex) {
			System.err.println("create table error " + ex.getMessage());
		}
		try {
			PreparedStatement st = connect
					.prepareStatement("insert into CATEGORIA_ENTRADA (ENTRADA_ID, CATEGORIA) values (?,?)");
			st.setInt(1, entradaid);
			st.setString(2, categoria);

			return st.execute();
		} catch (SQLException ex) {
			System.err.println("agregarCategoriaEntrada " + ex.getMessage());
			return false;
		}

	}

	public ArrayList<String> getCategoriasEntrada(int id) {
		ResultSet result = null;

		ArrayList<String> resultado = new ArrayList<String>();
		
		try {
			PreparedStatement st = connect
					.prepareStatement("CREATE TABLE IF NOT EXISTS CATEGORIA (NOMBRE TEXT UNIQUE)");
			st.execute();
		} catch (SQLException ex) {
			System.err.println("create table error " + ex.getMessage());
		}
		
		try {
			PreparedStatement st = connect.prepareStatement(
					"CREATE TABLE IF NOT EXISTS CATEGORIA_ENTRADA (CATEGORIA TEXT REFERENCES CATEGORIA (NOMBRE) ON DELETE CASCADE ON UPDATE CASCADE, ENTRADA_ID INTEGER REFERENCES entrada (id) ON DELETE CASCADE ON UPDATE CASCADE);");
			st.execute();
		} catch (SQLException ex) {
			System.err.println("create table error " + ex.getMessage());
		}
		
		try {
			System.out.println("select CATEGORIA_ENTRADA.CATEGORIA from CATEGORIA_ENTRADA "
					+ "INNER JOIN entrada ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID " + "AND entrada.id = " + id);
			PreparedStatement st = connect.prepareStatement("select CATEGORIA_ENTRADA.CATEGORIA from CATEGORIA_ENTRADA "
					+ "INNER JOIN entrada ON entrada.id = CATEGORIA_ENTRADA.ENTRADA_ID " + "AND entrada.id = " + id);
			result = st.executeQuery();
			while (result.next()) {
				resultado.add(result.getString("CATEGORIA"));
			}

		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
			return null;
		}

		return resultado;
	}

	public int updateAdminPassword(String password, String newPassword) {
		// TODO Auto-generated method stub
		Password passwordSHA = new Password();
		ResultSet result = null;
		entrada temp = null;
		

		
		try {
			PreparedStatement st = connect.prepareStatement("select * from user where user='admin'");
			result = st.executeQuery();
			while (result.next()) {

				String pass = result.getString("password");
				try {
					if (passwordSHA.check(password, pass)) {
						String saltedHash = passwordSHA.getSaltedHash(newPassword);
						System.out.println("saltedhash " + saltedHash);
						System.out.println("passwordNew " + newPassword);
						st = connect.prepareStatement("UPDATE user SET password = '"+ saltedHash + "' WHERE user = 'admin'");
						st.executeUpdate();
						JOptionPane.showMessageDialog(null, "La contraseña ha sido cambiada con éxito");
						return 1;

					}else {
						JOptionPane.showMessageDialog(null, "La contraseña antigua no es correcta.");

					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return -1;
	}

	public void updateJefe(String user, String role) {
		// TODO Auto-generated method stub
		int usuario_id = -1;

		try {
			PreparedStatement st = connect.prepareStatement("select * from user where user = '" + user + "'");
			ResultSet result = st.executeQuery();
			while (result.next()) {
				usuario_id = result.getInt("id");
				System.out.println("usuario_id " + usuario_id);
			}
		} catch (SQLException ex) {
			System.err.println("saveJefe error " + ex.getMessage());
		}

		String sql = "UPDATE usuario_role SET user_id = '"+usuario_id + "' AND permiso = '1' AND isJefe ='1' WHERE role = '"+role +"'";
		System.out.println(sql);
		int result = -1;
		try {
			PreparedStatement st = connect.prepareStatement(sql);
			result = st.executeUpdate();
			System.out.println("resultado updateJefe " + result);
		} catch (SQLException ex) {
			System.err.println("updateJefeId " + ex.getMessage());
		}
		
		if (result == 0) {
			sql = "INSERT INTO usuario_role (role, user_id, permiso, isJefe) values ('"+role+"', '"+usuario_id + "', '1', '1')";
			System.out.println(sql);
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				System.out.println("resultado createJefe " + st.execute());
			} catch (SQLException ex) {
				System.err.println("updateJefeId " + ex.getMessage());
			}
		}
		
	}

	public boolean borrarVistoBuenoSalida(int id) {
		int result = -2;
		boolean exist = false;
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM VistoBuenoJefes WHERE entrada_id = ?");
			st.setInt(1, id);
			result = st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("borrarCategorias " + ex.getMessage());
		}

		if (result == 1) {
			return true;
		} else
			return false;

		
	}

	public void cambiarPermisosUsuario(int usuarioId, boolean permiso) {
		String sql = "";
		if (permiso) {
			sql = "UPDATE usuario_role SET permiso = '"+1 + "' WHERE user_id = '"+usuarioId +"'";

		}else {
			sql = "UPDATE usuario_role SET permiso = '"+0 + "' WHERE user_id = '"+usuarioId +"'";

		}
		System.out.println("cambiarPermisosUsuario " + sql);
		try {
			PreparedStatement st = connect.prepareStatement(sql);
			st.executeUpdate();
		} catch (SQLException ex) {
			System.err.println("cambiarPermisosUsuario " + ex.getMessage());
		}
		
	}

	public boolean getPermisosUsuario(int usuarioId) {
		String sql = "SELECT * FROM usuario_role WHERE user_id = ?";
		boolean permiso = false;
		try {
			PreparedStatement st = connect.prepareStatement(sql);
			st.setInt(1, usuarioId);
			ResultSet result = st.executeQuery();
			while (result.next()) {
				permiso = result.getBoolean("permiso");
				System.out.println("usuario_id " + usuarioId + " permiso " + permiso);
			}

		} catch (SQLException ex) {
			close();
			System.err.println("getPermisosUsuario " + ex.getMessage());
		}
		return permiso;
	}

	public LinkedHashMap<Integer, Integer> getPosiciones() {
		ResultSet result = null;
		LinkedHashMap<Integer, Integer> posiciones = new LinkedHashMap<Integer, Integer>();
		try {
			System.out.println("SELECT * FROM USER\r\n" + 
					"LEFT JOIN usuario_role ON usuario_role.user_id = user.id\r\n" + 
					"LEFT JOIN role ON role.nombre_role = usuario_role.role\r\n" + 
					"WHERE isJefe = 1\r\n" + 
					"ORDER BY role.posicion");
			PreparedStatement st = connect.prepareStatement("SELECT * FROM USER\r\n" + 
					"LEFT JOIN usuario_role ON usuario_role.user_id = user.id\r\n" + 
					"LEFT JOIN role ON role.nombre_role = usuario_role.role\r\n" + 
					"WHERE isJefe = 1\r\n" + 
					"ORDER BY role.posicion");
			result = st.executeQuery();
			while (result.next()) {
				posiciones.put(result.getInt("user_id"), result.getInt("posicion"));
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
		return posiciones;
	}



}
