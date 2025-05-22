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

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import gui.GUI;
import gui.Login;
import models.comentario;
import models.comentarioProd;
import models.entrada;
import models.entradaProd;

public class dbProd {
//	String url = System.getProperty("user.dir") + "\\db.sqlite";

	 //String url = "H:\\plmcomd\\COMUN\\CORRESPONDENCIA ENTRADA\\db.sqlite";
	 // String url = "C:\\Users\\DGGC\\Downloads\\JUAN\\prueba\\prueba\\db.sqlite";

	Connection connect;
	public void connect() {
		try {
			 System.out.println("connect dbProd");
			
			DriverManager.registerDriver(new org.sqlite.JDBC());
			File newFile = new File(Login.BASE_DIR+"db.sqlite");
			if (!newFile.exists()) {
				throw new SQLException();
			}
			GUI.error = false;
			connect = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\DGGC\\Downloads\\dbProd.sqlite");

			if (connect != null) {
		         System.out.println("Conectado dbProd connect()");

				GUI.logDB.setText("Conectado dbProd");

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

	public void saveComentario(comentarioProd comentario) {
		// TODO Auto-generated method stub
		try {
			PreparedStatement st = connect.prepareStatement(
					"insert into comentario (jefe1, jefe1fecha, jefe1hora, jefe2, jefe2fecha, jefe2hora,jefe3, jefe3fecha, jefe3hora,jefe4, jefe4fecha, jefe4hora,jefe5, jefe5fecha, jefe5hora,) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			st.setString(1, comentario.getJefe1());
			st.setString(2, comentario.getJefe1fecha());
			st.setString(3, comentario.getJefe1hora());
			st.setString(4, comentario.getJefe2());
			st.setString(5, comentario.getJefe2fecha());
			st.setString(6, comentario.getJefe2hora());
			st.setString(7, comentario.getJefe3());
			st.setString(8, comentario.getJefe3fecha());
			st.setString(9, comentario.getJefe3hora());
			st.setString(10, comentario.getJefe4());
			st.setString(11, comentario.getJefe4fecha());
			st.setString(12, comentario.getJefe4hora());
			st.setString(13, comentario.getJefe5());
			st.setString(14, comentario.getJefe5fecha());
			st.setString(15, comentario.getJefe5hora());
			st.setString(16, comentario.getEntrada_id() + "");
			st.execute();
		} catch (SQLException ex) {
			System.err.println("Save Comentario " + ex.getMessage());
		}

	}

	public int saveEntrada(entradaProd entrada) {
		// TODO Auto-generated method stub
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement(
					"insert into entrada (asunto, fecha, area, confidencial, urgente, observaciones, soloJefe1) values (?,?,?,?,?,?,?)");
			st.setString(1, entrada.getAsunto());
			st.setString(2, entrada.getFecha());
			st.setString(3, entrada.getArea());
			st.setBoolean(4, entrada.isConfidencial());
			st.setBoolean(5, entrada.isUrgente());
			st.setString(6, entrada.getObservaciones());
			st.setBoolean(7, entrada.isSoloCoronel());

			st.execute();
		} catch (SQLException ex) {
			System.err.println("Save entrada error " + ex.getMessage());
		}

		try {
			PreparedStatement st = connect.prepareStatement("select seq from sqlite_sequence where name='entrada'; ");
			result = st.executeQuery();
			return result.getInt("seq");
		} catch (SQLException ex) {
			System.err.println("Save entrada error 2 " + ex.getMessage());
		}

		return -1;

	}

	public ResultSet mostrarEntradasCoronel(String date) {
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement("select * from entrada where fecha = '" + date + "'");
			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println("mostrarEntrdasCoronel error " + ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradas(String date) {
		ResultSet result = null;
		try {
			PreparedStatement st = connect
					.prepareStatement("select * from entrada where fecha = '" + date + "' and soloJefe1 = '0'");
			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println("mostrarEntradas " + ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasPorNegociado(String date, String area) {

		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement(
					"select * from entrada where fecha = '" + date + "' and Area = '" + area + "' and soloJefe1 = '0'");
			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println("mostrarEntradasPorNegociado " + ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasSinConfidencial(String date, String role, String busqueda) {
		ResultSet result = null;
		try {
			if (busqueda.equals("Todos")) {
				System.out.println("mostrarEntradasSinConfidencial TODOS");

//				PreparedStatement st = connect.prepareStatement("select * from entrada where area = 'Todos' and fecha = '" + date
//						+ "' and soloJefe1 = '0' and confidencial = '0' UNION select * from entrada where fecha = '" + date
//						+ "' and area='" + role + "' and confidencial = '1'");
//				PreparedStatement st = connect.prepareStatement("select * from entrada where area = 'Todos' and fecha = '" + date
//						+ "' and soloJefe1 = '0' and confidencial = '0'");
				PreparedStatement st = connect.prepareStatement("select id, asunto, fecha, area, observaciones, "
						+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
						+ "soloJefe1, Tramitado from entrada "
						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
						+ "AND entrada.fecha = '"+date+ "' "
						+ "AND entrada.soloJefe1 = 0 "
						+ "AND entrada.confidencial = 0 "
						+ "AND destinatarios.negociado = 'Todos' ");
						
				
				
				result = st.executeQuery();

			}else {
				System.out.println("mostrarEntradasSinConfidencial else");

//				PreparedStatement st = connect.prepareStatement("select * from entrada where fecha = '" + date
//						+ "' and soloJefe1 = '0' and area='"+ role +"' and confidencial = '0' UNION select * from entrada where fecha = '" + date
//						+ "' and area='" + role + "' and confidencial = '1'");
//				PreparedStatement st = connect.prepareStatement("select * from entrada where fecha = '" + date
//						+ "' and soloJefe1 = '0' and area='"+ role +"'");
				
				PreparedStatement st = connect.prepareStatement("select id, asunto, fecha, area, observaciones, "
						+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
						+ "soloJefe1, Tramitado from entrada "
						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
						+ "AND entrada.fecha = '"+date+ "' "
						+ "AND entrada.soloJefe1 = 0 "
						+ "AND entrada.confidencial = 0 "
						+ "AND destinatarios.negociado = '"+role+"' ");
				
				result = st.executeQuery();

			}
			
		} catch (SQLException ex) {
			System.err.println("mostrarEntradasSinConfidencial " + ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasSinConfidencialPorNegociado(String date, String area, String role) {

		ResultSet result = null;
		try {
			if (area.equals(role)) {
//				PreparedStatement st = connect.prepareStatement("select * from entrada where area = '" + area
//						+ "' and soloJefe1 = '0' and fecha = '" + date
//						+ "' and confidencial = '0' UNION select * from entrada where fecha = '" + date
//						+ "' and soloJefe1 = '0' and area='" + role + "' and confidencial = '1'"
//						+ "UNION select * from entrada where area='Varios negociados' and soloJefe1 = '0' and fecha ='"
//						+ date + "'");
//				PreparedStatement st = connect.prepareStatement("select * from entrada where area = '" + area
//						+ "' and soloJefe1 = '0' and fecha = '" + date
//						+ "' and confidencial = '0' UNION select * from entrada where fecha = '" + date
//						+ "' and soloJefe1 = '0' and area='" + role + "' and confidencial = '1'" 
//						+ "UNION select * from entrada where area='Todos' and soloJefe1 = '0' and fecha ='"
//						+ date + "'");
				
				System.out.println("select id, asunto, fecha, area, observaciones, "
						+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
						+ "soloJefe1, Tramitado from entrada "
						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
						+ "AND entrada.fecha = '"+date+ "' "
						+ "AND entrada.soloJefe1 = 0 "
						+ "AND entrada.confidencial = 0 "
						+ "AND destinatarios.negociado = '"+role+"'"  
						
						+ "UNION select id, asunto, fecha, area, observaciones, "
						+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
						+ "soloJefe1, Tramitado from entrada "
						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
						+ "AND entrada.fecha = '"+date+ "' "
						+ "AND entrada.soloJefe1 = 0 "
						+ "AND entrada.confidencial = 0 "
						+ "AND destinatarios.negociado = 'Todos'");
				
				PreparedStatement st = connect.prepareStatement("select id, asunto, fecha, area, observaciones, "
						+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
						+ "soloJefe1, Tramitado from entrada "
						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
						
						+ "AND entrada.fecha = '"+date+ "' "
						+ "AND entrada.soloJefe1 = 0 "
						+ "AND entrada.confidencial = 0 "
						+ "AND destinatarios.negociado = '"+role+"'"
 
						+ "UNION select id, asunto, fecha, area, observaciones, "
						+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
						+ "soloJefe1, Tramitado from entrada "
						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
						
						+ "AND entrada.fecha = '"+date+ "' "
						+ "AND entrada.soloJefe1 = 0 "
						+ "AND entrada.confidencial = 0 "
						+ "AND destinatarios.negociado = 'Todos'");
				
				result = st.executeQuery();
			} else {
//				PreparedStatement st = connect.prepareStatement("select * from entrada where area = '" + area
//						+ "' and soloJefe1 = '0' and fecha = '" + date
//						+ "' and confidencial = '0' UNION select * from entrada where area='Varios Negociados' and soloJefe1 = '0' and fecha ='"
//						+ date + "'");
//				PreparedStatement st = connect.prepareStatement("select * from entrada where area='Todos' and soloJefe1 = '0' and fecha ='"
//						+ date + "'");
				PreparedStatement st = connect.prepareStatement("select * from entrada where asunto = 'NULL'");
				
				result = st.executeQuery();
			}
		} catch (SQLException ex) {
			System.err.println("mostrarEntradasSinConfidencialPorNegociado " + ex.getMessage());
		}
		return result;
	}

	public boolean entradaIsConfidencial(int id) {
		boolean result = false;
		try {
			PreparedStatement st = connect.prepareStatement("select * from entrada where id = '" + id + "'");
			ResultSet temp = st.executeQuery();
			result = temp.getBoolean("Confidencial");
		} catch (SQLException ex) {
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
			System.err.println("mostrarEntradas  " + ex.getMessage());
		}
		return result;
	}
	
	public ResultSet mostrarUsuarios() {
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement("select * from user");
			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println("mostrarEntradas  " + ex.getMessage());
		}
		return result;
	}
	
	public void resetPassword(String usuario) {
		String sql = "UPDATE user SET password = 'reset' " + "WHERE user = ?";
		try {
			PreparedStatement st = connect.prepareStatement(sql);
			st.setString(1, usuario);
			st.execute();
		} catch (SQLException ex) {
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
			System.err.println(ex.getMessage());
		}

	}
	
	public void saveUser(String user, String role) {
		try {
			PreparedStatement st = connect.prepareStatement("insert into user (user, password, role) values (?,?,?)");
			st.setString(1, user);
			st.setString(2, "reset");
			st.setString(3, role);
			st.execute();
		} catch (SQLException ex) {
			System.err.println("saveUser error " + ex.getMessage());
		}

	}

	public entradaProd mostrarEntrada(int id) {
		ResultSet result = null;
		entradaProd temp = null;

		try {
			PreparedStatement st = connect.prepareStatement("select * from entrada where id=" + id);
			result = st.executeQuery();

			temp = new entradaProd(result.getString("asunto"), result.getString("fecha"), result.getString("area"),
					result.getBoolean("confidencial"), result.getBoolean("urgente"));
			temp.setJefe1(result.getInt("jefe1"));
			temp.setJefe2(result.getInt("jefe2"));
			temp.setJefe3(result.getInt("jefe3"));
			temp.setJefe4(result.getInt("jefe4"));
			temp.setJefe5(result.getInt("jefe5"));
			temp.setObservaciones(result.getString("observaciones"));
			temp.setSoloCoronel(result.getBoolean("soloJefe1"));
			temp.setTramitado(result.getBoolean("Tramitado"));
			temp.setId(result.getInt("ID"));

//            }
		} catch (SQLException ex) {
			System.err.println("mostrarEntrada error " + ex.getMessage());
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
			System.err.println("buscarIdEntradaPorNombreArchivo error" + ex.getMessage());
		}
		return temp;
	}

	public comentarioProd mostrarComentario(int id) {
		ResultSet result = null;
		comentarioProd temp = null;

		try {
			PreparedStatement st = connect.prepareStatement("select * from comentario where entrada_id=" + id);
			result = st.executeQuery();
			temp = new comentarioProd(id);
			temp.setJefe1(result.getString("jefe1"));
			temp.setJefe1fecha(result.getString("jefe1fecha"));
			temp.setJefe1hora(result.getString("jefe1hora"));
			temp.setJefe2(result.getString("jefe2"));
			temp.setJefe2fecha(result.getString("jefe2fecha"));
			temp.setJefe2hora(result.getString("jefe2hora"));
			temp.setJefe3(result.getString("jefe3"));
			temp.setJefe3fecha(result.getString("jefe3fecha"));
			temp.setJefe3hora(result.getString("jefe3hora"));
			temp.setJefe4(result.getString("jefe4"));
			temp.setJefe4fecha(result.getString("jefe4fecha"));
			temp.setJefe4hora(result.getString("jefe4hora"));
			temp.setJefe5(result.getString("jefe5"));
			temp.setJefe5fecha(result.getString("jefe5fecha"));
			temp.setJefe5hora(result.getString("jefe5hora"));
			temp.setId(result.getInt("id"));
			temp.setEntrada_id(result.getInt("entrada_id"));
			temp.setTramitadoPor(result.getString("tramitadoPor"));

			if (temp.getJefe1() == null) {
				temp.setJefe1("");
			}
			if (temp.getJefe2() == null) {
				temp.setJefe2("");
			}
			if (temp.getJefe3() == null) {
				temp.setJefe3("");
			}
			if (temp.getJefe4() == null) {
				temp.setJefe4("");
			}
			if (temp.getJefe5() == null) {
				temp.setJefe5("");
			}
		} catch (SQLException ex) {
			System.err.println("mostrarComentario id " + id  + " error " + ex.getMessage());
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
				resultado.addElement(result.getString("file"));
			}
		} catch (SQLException ex) {
			System.err.println("mostrarFiles error " + ex.getMessage());
		}
		return resultado;
	}
	
	public ResultSet mostrarFilesResultSet(int id) {
		ResultSet result = null;
		DefaultListModel resultado = new DefaultListModel();
		try {
			PreparedStatement st = connect.prepareStatement("select * from files where entrada_id=" + id);
			result = st.executeQuery();
			
		} catch (SQLException ex) {
			System.err.println("mostrarFiles error " + ex.getMessage());
		}
		return result;
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
				resultado.addElement(result.getString("file"));
			}
		} catch (SQLException ex) {
			System.err.println("mostrarFilesAntecedentes error " + ex.getMessage());
		}
		return resultado;
	}
	
	public ResultSet mostrarFilesAntecedentesResultSet(int id) {
		ResultSet result = null;
		DefaultListModel resultado = new DefaultListModel();
		try {
			PreparedStatement st = connect.prepareStatement("select * from antecedentesFiles where entrada_id=" + id);
			result = st.executeQuery();
			
		} catch (SQLException ex) {
			System.err.println("mostrarFilesAntecedentes error " + ex.getMessage());
		}
		return result;
	}

	
	public void saveFile(String absolutePath, int idEntrada) {
		try {
			PreparedStatement st = connect.prepareStatement("insert into files (file, entrada_id) values (?,?)");
			st.setString(1, absolutePath);
			st.setInt(2, idEntrada);
			st.execute();
		} catch (SQLException ex) {
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
			System.err.println("saveFileAntecedentes error "  + ex.getMessage());
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
			System.err.println("saveAntecedentesEntrada error " + ex.getMessage());
		}

	}

	public void actualizarEntrada(entradaProd entrada) {
		// TODO Auto-generated method stub
		String sql = "UPDATE entrada SET jefe1 = ? , " + "jefe2 = ? ," + "jefe3 = ? ," + "jefe4 = ? ," + "jefe5 = ? ,"
				+ "confidencial = ? ," + "urgente = ? ," + "observaciones = ? ," + "soloJefe1 = ? ," + "asunto = ? ,"
				+ "area = ?," + "tramitado = ?" + "WHERE id = ?";
		try {
//			System.out.println("actualizar Entrada");
			PreparedStatement st = connect.prepareStatement(sql);
			st.setInt(1, entrada.getJefe1());
			st.setInt(2, entrada.getJefe2());
			st.setInt(3, entrada.getJefe3());
			st.setInt(4, entrada.getJefe4());
			st.setInt(5, entrada.getJefe5());
			st.setBoolean(6, entrada.isConfidencial());
			st.setBoolean(7, entrada.isUrgente());
			st.setString(8, entrada.getObservaciones());
			st.setBoolean(9, entrada.isSoloCoronel());
			st.setString(10, entrada.getAsunto());
			st.setString(11, entrada.getArea());
			st.setBoolean(12, entrada.isTramitado());
			st.setInt(13, entrada.getId());
			st.execute();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
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
			System.err.println(ex.getMessage());
		}

	}

	public void actualizarComentario(comentarioProd comentario) {
		boolean exist = false;
		try {
			PreparedStatement st = connect
					.prepareStatement("select * from comentario where entrada_id=" + comentario.getEntrada_id());
			ResultSet result = st.executeQuery();
			exist = result.next();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		if (exist) {
			String sql = "UPDATE comentario SET jefe1 = ? , " + "jefe1fecha = ? ," + "jefe1hora = ? ," + "jefe2 = ? ,"
					+ "jefe2fecha = ? ," + "jefe2hora = ? ," + "jefe3 = ? ," + "jefe3fecha = ? ," + "jefe3hora = ? ,"
					+ "jefe4 = ? ," + "jefe4fecha = ? ," + "jefe4hora = ? ," + "jefe5 = ? ," + "jefe5fecha = ? ,"
					+ "jefe5hora = ? " + "WHERE entrada_id = ?";
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				st.setString(1, comentario.getJefe1());
				st.setString(2, comentario.getJefe1fecha());
				st.setString(3, comentario.getJefe1hora());
				st.setString(4, comentario.getJefe2());
				st.setString(5, comentario.getJefe2fecha());
				st.setString(6, comentario.getJefe2hora());
				st.setString(7, comentario.getJefe3());
				st.setString(8, comentario.getJefe3fecha());
				st.setString(9, comentario.getJefe3hora());
				st.setString(10, comentario.getJefe4());
				st.setString(11, comentario.getJefe4fecha());
				st.setString(12, comentario.getJefe4hora());
				st.setString(13, comentario.getJefe5());
				st.setString(14, comentario.getJefe5fecha());
				st.setString(15, comentario.getJefe5hora());
				st.setInt(16, comentario.getEntrada_id());
				st.execute();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
		} else {
			String sql = "INSERT INTO comentario (jefe1, jefe1fecha, jefe1hora, jefe2, jefe2fecha, jefe2hora, jefe3, jefe3fecha, jefe3hora, jefe4, jefe4fecha, jefe4hora, jefe5, jefe5fecha, jefe5hora, entrada_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				st.setString(1, comentario.getJefe1());
				st.setString(2, comentario.getJefe1fecha());
				st.setString(3, comentario.getJefe1hora());
				st.setString(4, comentario.getJefe2());
				st.setString(5, comentario.getJefe2fecha());
				st.setString(6, comentario.getJefe2hora());
				st.setString(7, comentario.getJefe3());
				st.setString(8, comentario.getJefe3fecha());
				st.setString(9, comentario.getJefe3hora());
				st.setString(10, comentario.getJefe4());
				st.setString(11, comentario.getJefe4fecha());
				st.setString(12, comentario.getJefe4hora());
				st.setString(13, comentario.getJefe5());
				st.setString(14, comentario.getJefe5fecha());
				st.setString(15, comentario.getJefe5hora());
				st.setInt(16, comentario.getEntrada_id());
				st.execute();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
		}

	}

	public void actualizarComentarioSoloJefe(int idJefe, comentarioProd comentario) {
		String jefeText = "";
		String jefeComentario = "";
		String jefeFecha = "";
		String jefeFechaText = "";
		String jefeHora = "";
		String jefeHoraText = "";
		switch (idJefe) {
		case 0:
			jefeText = "jefe1";
			jefeFecha = "jefe1fecha";
			jefeHora = "jefe1hora";
			jefeComentario = comentario.getJefe1();
			jefeFechaText = comentario.getJefe1fecha();
			jefeHoraText = comentario.getJefe1hora();
			break;
		case 1:
			jefeText = "jefe2";
			jefeFecha = "jefe2fecha";
			jefeHora = "jefe2hora";
			jefeComentario = comentario.getJefe2();
			jefeFechaText = comentario.getJefe2fecha();
			jefeHoraText = comentario.getJefe2hora();
			break;
		case 2:
			jefeText = "jefe3";
			jefeFecha = "jefe3fecha";
			jefeHora = "jefe3hora";
			jefeComentario = comentario.getJefe3();
			jefeFechaText = comentario.getJefe3fecha();
			jefeHoraText = comentario.getJefe3hora();
			break;
		case 3:
			jefeText = "jefe4";
			jefeFecha = "jefe4fecha";
			jefeHora = "jefe4hora";
			jefeComentario = comentario.getJefe4();
			jefeFechaText = comentario.getJefe4fecha();
			jefeHoraText = comentario.getJefe4hora();
			break;
		case 4:
			jefeText = "jefe5";
			jefeFecha = "jefe5fecha";
			jefeHora = "jefe5hora";
			jefeComentario = comentario.getJefe5();
			jefeFechaText = comentario.getJefe5fecha();
			jefeHoraText = comentario.getJefe5hora();
			break;

		default:
			return;
		}

		boolean exist = false;
		try {
			PreparedStatement st = connect
					.prepareStatement("select * from comentario where entrada_id=" + comentario.getEntrada_id());
			ResultSet result = st.executeQuery();
			exist = result.next();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		if (exist) {
			String sql = "UPDATE comentario SET " + jefeText + "= ? , " + jefeFecha + "= ? ," + jefeHora + "= ?"
					+ "WHERE entrada_id = ?";
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				st.setString(1, jefeComentario);
				st.setString(2, jefeFechaText);
				st.setString(3, jefeHoraText);
				st.setInt(4, comentario.getEntrada_id());
				st.execute();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
		} else {
			String sql = "INSERT INTO comentario (jefe1, jefe1fecha, jefe1hora, jefe2, jefe2fecha, jefe2hora, jefe3, jefe3fecha, jefe3hora, jefe4, jefe4fecha, jefe4hora, jefe5, jefe5fecha, jefe5hora, entrada_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				st.setString(1, comentario.getJefe1());
				st.setString(2, comentario.getJefe1fecha());
				st.setString(3, comentario.getJefe1hora());
				st.setString(4, comentario.getJefe2());
				st.setString(5, comentario.getJefe2fecha());
				st.setString(6, comentario.getJefe2hora());
				st.setString(7, comentario.getJefe3());
				st.setString(8, comentario.getJefe3fecha());
				st.setString(9, comentario.getJefe3hora());
				st.setString(10, comentario.getJefe4());
				st.setString(11, comentario.getJefe4fecha());
				st.setString(12, comentario.getJefe4hora());
				st.setString(13, comentario.getJefe5());
				st.setString(14, comentario.getJefe5fecha());
				st.setString(15, comentario.getJefe5hora());
				st.setInt(16, comentario.getEntrada_id());
				st.execute();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
		}

	}

	public void actualizarComentarioJefe1(comentarioProd comentario) {
		boolean exist = false;
		try {
			PreparedStatement st = connect
					.prepareStatement("select * from comentario where entrada_id=" + comentario.getEntrada_id());
			ResultSet result = st.executeQuery();
			exist = result.next();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		if (exist) {
			String sql = "UPDATE comentario SET jefe1 = ? , " + "jefe1fecha = ? ," + "jefe1hora = ?"
					+ "WHERE entrada_id = ?";
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				st.setString(1, comentario.getJefe1());
				st.setString(2, comentario.getJefe1fecha());
				st.setString(3, comentario.getJefe1hora());
				st.setInt(4, comentario.getEntrada_id());
				st.execute();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
		} else {
			String sql = "INSERT INTO comentario (jefe1, jefe1fecha, jefe1hora, jefe2, jefe2fecha, jefe2hora, jefe3, jefe3fecha, jefe3hora, jefe4, jefe4fecha, jefe4hora, jefe5, jefe5fecha, jefe5hora, entrada_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				st.setString(1, comentario.getJefe1());
				st.setString(2, comentario.getJefe1fecha());
				st.setString(3, comentario.getJefe1hora());
				st.setString(4, comentario.getJefe2());
				st.setString(5, comentario.getJefe2fecha());
				st.setString(6, comentario.getJefe2hora());
				st.setString(7, comentario.getJefe3());
				st.setString(8, comentario.getJefe3fecha());
				st.setString(9, comentario.getJefe3hora());
				st.setString(10, comentario.getJefe4());
				st.setString(11, comentario.getJefe4fecha());
				st.setString(12, comentario.getJefe4hora());
				st.setString(13, comentario.getJefe5());
				st.setString(14, comentario.getJefe5fecha());
				st.setString(15, comentario.getJefe5hora());
				st.setInt(16, comentario.getEntrada_id());
				st.execute();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
		}

	}

	public void actualizarComentarioJefe2(comentarioProd comentario) {
		boolean exist = false;
		try {
			PreparedStatement st = connect
					.prepareStatement("select * from comentario where entrada_id=" + comentario.getEntrada_id());
			ResultSet result = st.executeQuery();
			exist = result.next();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		if (exist) {
			String sql = "UPDATE comentario SET jefe2 = ? , " + "jefe2fecha = ? ," + "jefe2hora = ?"
					+ "WHERE entrada_id = ?";
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				st.setString(1, comentario.getJefe2());
				st.setString(2, comentario.getJefe2fecha());
				st.setString(3, comentario.getJefe2hora());
				st.setInt(4, comentario.getEntrada_id());
				st.execute();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
		} else {
			String sql = "INSERT INTO comentario (jefe1, jefe1fecha, jefe1hora, jefe2, jefe2fecha, jefe2hora, jefe3, jefe3fecha, jefe3hora, jefe4, jefe4fecha, jefe4hora, jefe5, jefe5fecha, jefe5hora, entrada_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				st.setString(1, comentario.getJefe1());
				st.setString(2, comentario.getJefe1fecha());
				st.setString(3, comentario.getJefe1hora());
				st.setString(4, comentario.getJefe2());
				st.setString(5, comentario.getJefe2fecha());
				st.setString(6, comentario.getJefe2hora());
				st.setString(7, comentario.getJefe3());
				st.setString(8, comentario.getJefe3fecha());
				st.setString(9, comentario.getJefe3hora());
				st.setString(10, comentario.getJefe4());
				st.setString(11, comentario.getJefe4fecha());
				st.setString(12, comentario.getJefe4hora());
				st.setString(13, comentario.getJefe5());
				st.setString(14, comentario.getJefe5fecha());
				st.setString(15, comentario.getJefe5hora());
				st.setInt(16, comentario.getEntrada_id());
				st.execute();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
		}

	}

	public void actualizarComentarioJefe3(comentarioProd comentario) {
		boolean exist = false;
		try {
			PreparedStatement st = connect
					.prepareStatement("select * from comentario where entrada_id=" + comentario.getEntrada_id());
			ResultSet result = st.executeQuery();
			exist = result.next();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		if (exist) {
			String sql = "UPDATE comentario SET jefe3 = ? , " + "jefe3fecha = ? ," + "jefe3hora = ?"
					+ "WHERE entrada_id = ?";
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				st.setString(1, comentario.getJefe3());
				st.setString(2, comentario.getJefe3fecha());
				st.setString(3, comentario.getJefe3hora());
				st.setInt(4, comentario.getEntrada_id());
				st.execute();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
		} else {
			String sql = "INSERT INTO comentario (jefe1, jefe1fecha, jefe1hora, jefe2, jefe2fecha, jefe2hora, jefe3, jefe3fecha, jefe3hora, jefe4, jefe4fecha, jefe4hora, jefe5, jefe5fecha, jefe5hora, entrada_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				st.setString(1, comentario.getJefe1());
				st.setString(2, comentario.getJefe1fecha());
				st.setString(3, comentario.getJefe1hora());
				st.setString(4, comentario.getJefe2());
				st.setString(5, comentario.getJefe2fecha());
				st.setString(6, comentario.getJefe2hora());
				st.setString(7, comentario.getJefe3());
				st.setString(8, comentario.getJefe3fecha());
				st.setString(9, comentario.getJefe3hora());
				st.setString(10, comentario.getJefe4());
				st.setString(11, comentario.getJefe4fecha());
				st.setString(12, comentario.getJefe4hora());
				st.setString(13, comentario.getJefe5());
				st.setString(14, comentario.getJefe5fecha());
				st.setString(15, comentario.getJefe5hora());
				st.setInt(16, comentario.getEntrada_id());
				st.execute();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
		}

	}

	public void actualizarComentarioJefe4(comentarioProd comentario) {
		boolean exist = false;
		try {
			PreparedStatement st = connect
					.prepareStatement("select * from comentario where entrada_id=" + comentario.getEntrada_id());
			ResultSet result = st.executeQuery();
			exist = result.next();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		if (exist) {
			String sql = "UPDATE comentario SET jefe4 = ? , " + "jefe4fecha = ? ," + "jefe4hora = ?"
					+ "WHERE entrada_id = ?";
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				st.setString(1, comentario.getJefe4());
				st.setString(2, comentario.getJefe4fecha());
				st.setString(3, comentario.getJefe4hora());
				st.setInt(4, comentario.getEntrada_id());
				st.execute();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
		} else {
			String sql = "INSERT INTO comentario (jefe1, jefe1fecha, jefe1hora, jefe2, jefe2fecha, jefe2hora, jefe3, jefe3fecha, jefe3hora, jefe4, jefe4fecha, jefe4hora, jefe5, jefe5fecha, jefe5hora, entrada_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				st.setString(1, comentario.getJefe1());
				st.setString(2, comentario.getJefe1fecha());
				st.setString(3, comentario.getJefe1hora());
				st.setString(4, comentario.getJefe2());
				st.setString(5, comentario.getJefe2fecha());
				st.setString(6, comentario.getJefe2hora());
				st.setString(7, comentario.getJefe3());
				st.setString(8, comentario.getJefe3fecha());
				st.setString(9, comentario.getJefe3hora());
				st.setString(10, comentario.getJefe4());
				st.setString(11, comentario.getJefe4fecha());
				st.setString(12, comentario.getJefe4hora());
				st.setString(13, comentario.getJefe5());
				st.setString(14, comentario.getJefe5fecha());
				st.setString(15, comentario.getJefe5hora());
				st.setInt(16, comentario.getEntrada_id());
				st.execute();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
		}

	}

	public void actualizarComentarioJefe5(comentarioProd comentario) {
		boolean exist = false;
		try {
			PreparedStatement st = connect
					.prepareStatement("select * from comentario where entrada_id=" + comentario.getEntrada_id());
			ResultSet result = st.executeQuery();
			exist = result.next();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		if (exist) {
			String sql = "UPDATE comentario SET jefe5 = ? , " + "jefe5fecha = ? ," + "jefe5hora = ?"
					+ "WHERE entrada_id = ?";
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				st.setString(1, comentario.getJefe5());
				st.setString(2, comentario.getJefe5fecha());
				st.setString(3, comentario.getJefe5hora());
				st.setInt(4, comentario.getEntrada_id());
				st.execute();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
		} else {
			String sql = "INSERT INTO comentario (jefe1, jefe1fecha, jefe1hora, jefe2, jefe2fecha, jefe2hora, jefe3, jefe3fecha, jefe3hora, jefe4, jefe4fecha, jefe4hora, jefe5, jefe5fecha, jefe5hora, entrada_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			try {
				PreparedStatement st = connect.prepareStatement(sql);
				st.setString(1, comentario.getJefe1());
				st.setString(2, comentario.getJefe1fecha());
				st.setString(3, comentario.getJefe1hora());
				st.setString(4, comentario.getJefe2());
				st.setString(5, comentario.getJefe2fecha());
				st.setString(6, comentario.getJefe2hora());
				st.setString(7, comentario.getJefe3());
				st.setString(8, comentario.getJefe3fecha());
				st.setString(9, comentario.getJefe3hora());
				st.setString(10, comentario.getJefe4());
				st.setString(11, comentario.getJefe4fecha());
				st.setString(12, comentario.getJefe4hora());
				st.setString(13, comentario.getJefe5());
				st.setString(14, comentario.getJefe5fecha());
				st.setString(15, comentario.getJefe5hora());
				st.setInt(16, comentario.getEntrada_id());
				st.execute();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
		}

	}

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
			System.out.println("Eliminar file "+ id);
			PreparedStatement st = connect.prepareStatement("DELETE FROM FILES WHERE FILE = ?");
			st.setString(1, id);
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
			System.out.println("Eliminar antecedentes file "+ id);
			PreparedStatement st = connect.prepareStatement("DELETE FROM antecedentesFiles WHERE FILE = ?");
			st.setString(1, id);
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
			System.out.println("Eliminar file "+ idEntrada);
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
		}
		return null;
	}
	
	public String getPosicion(String cargo) {
		try {
			PreparedStatement st = connect.prepareStatement("select * from jefes where nombre ='" + cargo + "'");
			ResultSet result = st.executeQuery();
			if (result.next()) {
				return result.getString("posicion");
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return null;
	}

	public ResultSet mostrarEntradasCoronelPendiente(String date) {
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement("select * from entrada where fecha = '" + date
					+ "' UNION select * from entrada where tramitado = '0'");
			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasJefesPendiente(String date) {
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement("select * from entrada where fecha = '" + date
					+ "' and soloJefe1 = '0' UNION select * from entrada where tramitado = '0' and soloJefe1 = '0'");
			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasSinConfidencialPendiente(String date, String role) {
		ResultSet result = null;
		try {
//			PreparedStatement st = connect.prepareStatement("select * from entrada where area = 'Todos' and fecha = '" + date
//					+ "' and soloJefe1 = '0' and confidencial = '0'  UNION select * from entrada where fecha = '" + date
//					+ "' and area='" + role
//					+ "' and confidencial = '1' and soloJefe1 = '0' UNION select * from entrada where tramitado = '0' and area='"
//					+ role + "' and soloJefe1 = '0' UNION select * from entrada where tramitado = '0' and area='Todos' and soloJefe1 = '0' ");
			System.out.println("select id, asunto, fecha, area, observaciones, "
					+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
					+ "soloJefe1, Tramitado from entrada "
					+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
					+ "AND entrada.fecha = '"+date+ "' "
					+ "AND entrada.soloJefe1 = 0 "
					+ "AND destinatarios.negociado = 'Todos' "
					
					+ "UNION select id, asunto, fecha, area, observaciones, "
					+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
					+ "soloJefe1, Tramitado from entrada "
					+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
					+ "AND entrada.fecha = '"+date+ "' "
					+ "AND entrada.soloJefe1 = 0 "
					+ "AND destinatarios.negociado = '"+role+"' "
					
					+ "UNION select id, asunto, fecha, area, observaciones, "
					+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
					+ "soloJefe1, Tramitado from entrada "
					+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
					+ "AND entrada.tramitado = '0' "
					+ "AND entrada.soloJefe1 = 0 "
					+ "AND destinatarios.negociado = '"+role+"' ");
			PreparedStatement st = connect.prepareStatement("select id, asunto, fecha, area, observaciones, "
					+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
					+ "soloJefe1, Tramitado from entrada "
					+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
					+ "AND entrada.fecha = '"+date+ "' "
					+ "AND entrada.soloJefe1 = 0 "
					+ "AND entrada.confidencial = 0 "
					+ "AND destinatarios.negociado = 'Todos' "
					
					+ "UNION select id, asunto, fecha, area, observaciones, "
					+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
					+ "soloJefe1, Tramitado from entrada "
					+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
					+ "AND entrada.fecha = '"+date+ "' "
					+ "AND entrada.soloJefe1 = 0 "
					+ "AND entrada.confidencial = 0 "
					+ "AND destinatarios.negociado = '"+role+"' "
					
					+ "UNION select id, asunto, fecha, area, observaciones, "
					+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
					+ "soloJefe1, Tramitado from entrada "
					+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
					+ "AND entrada.tramitado = '0' "
					+ "AND entrada.soloJefe1 = 0 "
					+ "AND entrada.confidencial = 0 "
					+ "AND destinatarios.negociado = '"+role+"' ");
			
			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println("mostrarEntradasSinConfidencialPendiente " + ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasPorNegociadoPendiente(String date, String area) {
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement("select * from entrada where fecha = '" + date
					+ "' and Area = '" + area + "' and soloJefe1 = '0' UNION select * from entrada where Area= '" + area
					+ "' and soloJefe1 = '0' and tramitado ='0'");
			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasSinConfidencialPorNegociadoPendiente(String date, String area, String role) {
		ResultSet result = null;
		try {
			if (area.equals(role)) {
				
//				PreparedStatement st = connect.prepareStatement("select * from entrada where area = '" + area  
//						+ "' and soloJefe1 = '0' and fecha = '" + date
//						+ "' and confidencial = '0' UNION select * from entrada where fecha = '" + date
//						+ "' and soloJefe1 = '0' and area='" + role
//						+ "' and confidencial = '1' UNION select * from entrada where soloJefe1 = '0' and area='" + role
//						+ "' and tramitado = '0'");
				
				PreparedStatement st = connect.prepareStatement("select id, asunto, fecha, area, observaciones, "
						+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
						+ "soloJefe1, Tramitado from entrada "
						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
						+ "AND entrada.fecha = '"+date+ "' "
						+ "AND entrada.soloJefe1 = 0 "
						+ "AND entrada.confidencial = 0 "
						+ "AND destinatarios.negociado = '"+role+"'"  
						
						+ "UNION select id, asunto, fecha, area, observaciones, "
						+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
						+ "soloJefe1, Tramitado from entrada "
						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
						+ "AND entrada.tramitado = '0' "
						+ "AND entrada.soloJefe1 = 0 "
						+ "AND entrada.confidencial = 0 "
						+ "AND destinatarios.negociado = '"+role+"'");
				
				result = st.executeQuery();
			} else {
				
//				PreparedStatement st = connect.prepareStatement(
//						"select * from entrada where area = 'Todos' and soloJefe1 = '0' and fecha = '" + date
//								+ "' and confidencial = '0' and tramitado = '0'");
				PreparedStatement st = connect.prepareStatement("select id, asunto, fecha, area, observaciones, "
						+ "urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, "
						+ "soloJefe1, Tramitado from entrada "
						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
						
						+ "AND entrada.soloJefe1 = 0 "
						+ "AND entrada.confidencial = 0 "
						+ "AND entrada.tramitado = 0 "
						+ "AND destinatarios.negociado = 'Todos'"  );
				result = st.executeQuery();
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasCoronelPorNegociado(String date, String area) {
		ResultSet result = null;
		try {
			PreparedStatement st = connect
					.prepareStatement("select * from entrada where fecha = '" + date + "' and Area = '" + area + "'");
			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasCoronelPorNegociadoPendiente(String date, String area) {
		ResultSet result = null;
		try {
			PreparedStatement st = connect
					.prepareStatement("select * from entrada where fecha = '" + date + "' and Area = '" + area
							+ "' UNION select * from entrada where Area= '" + area + "'  and tramitado ='0'");
			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet searchCoronel(String word) {
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement("select * from entrada where asunto LIKE '%" + word + "%'");
			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet searchJefes(String word) {
		ResultSet result = null;
		try {
			PreparedStatement st = connect
					.prepareStatement("select * from entrada where asunto LIKE '%" + word + "%' and soloJefe1 = '0'");
			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet search(String word, String role, String area) {
		if (area.equals("Todos")) {
			ResultSet result = null;
			try {
//				PreparedStatement st = connect.prepareStatement("select * from entrada where area = 'Todos' and asunto LIKE '%" + word
//						+ "%' and soloJefe1 = '0' and confidencial = '0' UNION select * from entrada where asunto LIKE '%"
//						+ word + "%' and soloJefe1 = '0' and area = '" + role + "' and confidencial = '1'");
//				PreparedStatement st = connect.prepareStatement("select * from entrada where area = 'Todos' and asunto LIKE '%" + word
//						+ "%' and soloJefe1 = '0' and confidencial = '0'");
				PreparedStatement st = connect.prepareStatement("select id, asunto, fecha, area, observaciones, urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, soloJefe1, Tramitado from entrada "
						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
						+ "AND entrada.asunto LIKE '%"+word+"%' "
						+ "AND entrada.confidencial = '0' "
						+ "AND entrada.soloJefe1 = 0 "
						+ "AND destinatarios.negociado = 'Todos' ");
				
				


				result = st.executeQuery();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
			return result;
		} else if (role.equals(area)) {
			ResultSet result = null;
			try {
//				PreparedStatement st = connect.prepareStatement(
//						"select * from entrada where asunto LIKE '%" + word + "%' and soloJefe1 = '0' and area = '"
//								+ area + "' and confidencial = '0' UNION select * from entrada where asunto LIKE '%"
//								+ word + "%' and soloJefe1 = '0' and area = '" + role + "' and confidencial = '1'");
				
				PreparedStatement st = connect.prepareStatement("select id, asunto, fecha, area, observaciones, urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, soloJefe1, Tramitado from entrada "
						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
						+ "AND entrada.asunto LIKE '%"+word+"%' "
						+ "AND entrada.confidencial = '0' "
						+ "AND entrada.soloJefe1 = 0 "
						+ "AND destinatarios.negociado = 'Todos' "
						+ "UNION select id, asunto, fecha, area, observaciones, urgente, jefe1, jefe2, jefe3, jefe4, jefe5, confidencial, soloJefe1, Tramitado from entrada "
						+ "INNER JOIN destinatarios ON entrada.id = destinatarios.entrada_id "
						+ "AND entrada.asunto LIKE '%"+word+"%' "
						+ "AND entrada.soloJefe1 = 0 "
						+ "AND entrada.confidencial = '0' "
						+ "AND destinatarios.negociado = '"+role+"' ");
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
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement(
					"select * from entrada where fecha = '" + date + "' UNION select * from entrada where jefe1 = '0'");
			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public boolean tieneJefePendienteVer(int idUsuario) {
		String usuario = "";
		switch (idUsuario) {
		case 0:
			usuario = "jefe1";
			break;
		case 1:
			usuario = "jefe2";
			break;
		case 2:
			usuario = "jefe3";
			break;
		case 3:
			usuario = "jefe4";
			break;
		case 4:
			usuario = "jefe5";
			break;
		default:
			break;
		}
		boolean result = false;
		ResultSet temp;
		try {
			PreparedStatement st = connect.prepareStatement(
					"select * from entrada where "+usuario+" = '0'");
			temp = st.executeQuery();
			while(temp.next()) {
				result = true;
			}
		} catch (SQLException ex) {
			System.err.println("Error tieneJefePendienteVer" +ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasJefesPendienteVer(String date, int idUsuario) {
		ResultSet result = null;
		String usuario = "";
		switch (idUsuario) {
		case 1:
			usuario = "jefe2";
			break;
		case 2:
			usuario = "jefe3";
			break;
		case 3:
			usuario = "jefe4";
			break;
		case 4:
			usuario = "jefe5";
			break;
		default:
			break;
		}
		try {
			PreparedStatement st = connect.prepareStatement("select * from entrada where fecha = '" + date
					+ "' and soloJefe1 = '0' UNION select * from entrada where soloJefe1 = '0' and " + usuario
					+ "='0'");
			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasCoronelPendientePorFecha(String[] dates) {
		
		
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement(
					"select * from entrada where (substr(fecha, 7, 4) || '/' || substr(fecha, 4, 2) || '/' || substr(fecha, 1, 2)) between '"+cambiarFormatFecha(dates[0])+"' and '"+cambiarFormatFecha(dates[1]) +"' and jefe1 = '0'");
			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasJefesPendientePorFecha(String[] dates, int idUsuario) {
		ResultSet result = null;
		String usuario = "";
		switch (idUsuario) {
		case 1:
			usuario = "jefe2";
			break;
		case 2:
			usuario = "jefe3";
			break;
		case 3:
			usuario = "jefe4";
			break;
		case 4:
			usuario = "jefe5";
			break;
		default:
			break;
		}
		try {
			PreparedStatement st = connect.prepareStatement(
					"select * from entrada where fecha >= '" + dates[0] + "' and fecha <= '" + dates[1] + "' and "+usuario+" = '0'");
			result = st.executeQuery();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public ResultSet mostrarEntradasCoronelPorNegociadoPendientePorFecha(String[] dates, String string) {
		return null;
	}

	public ResultSet mostrarEntradasJefesPorNegociadoPendientePorFechas(String[] dates, String string) {
		return null;
	}
	
	
	public String cambiarFormatFecha(String date) {
		
		String[] elements = date.split("/");
		
		
		return elements[2]+"/"+elements[1]+"/"+elements[0];
	}
	
	public ArrayList<String> getNegociados() {
		ResultSet result = null;
		ArrayList<String> names = new ArrayList<String>();
		try {
			PreparedStatement st = connect.prepareStatement("select * from negociados");
			result = st.executeQuery();
			while(result.next()) {
				names.add(result.getString("nombre"));
				System.out.println(result.getString("nombre"));
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return names;
	}
	
	public HashMap<Integer, String> getCargos() {
		ResultSet result = null;
		HashMap<Integer, String> cargos = new HashMap<Integer, String>();
		try {
			PreparedStatement st = connect.prepareStatement("select * from JEFES");
			result = st.executeQuery();
			while(result.next()) {
				cargos.put(result.getInt("posicion"), result.getString("nombre"));
				System.out.println();
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
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
	
	public boolean eliminarNegociado(String negociado) {
		int result = -2;
		boolean exist = false;
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM negociados WHERE nombre = ?");
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
	
	public boolean eliminarCargo(String cargo) {
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
	
	public boolean saveNegociado(String negociado, boolean b) {
		// TODO Auto-generated method stub
		try {
			PreparedStatement st = connect.prepareStatement(
					"insert into negociados (nombre, gestionado) values (?,?)");
			st.setString(1, negociado);
			st.setBoolean(2, b);
			st.execute();
			return true;
		} catch (SQLException ex) {
			System.err.println("Save negociado error " + ex.getMessage());
			return false;
		}
	}
	
	public boolean saveCargo(String cargo, String posicion) {
		// TODO Auto-generated method stub
		try {
			PreparedStatement st = connect.prepareStatement(
					"insert into JEFES (nombre, posicion) values (?,?)");
			st.setString(1, cargo);
			st.setString(2, posicion);

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
			PreparedStatement st = connect.prepareStatement("select * from JEFES where nombre='" + cargo + "'");
			result = st.executeQuery();
			while (result.next()) {
				resultado = result.getString("posicion");
				return resultado;
			}
			return "";
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
			return "";
		}
	}

	public boolean agregarDestinatario(int entradaid, String destino) {

			try {
				PreparedStatement st = connect.prepareStatement(
						"insert into destinatarios (entrada_id, negociado) values (?,?)");
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
			System.out.println("select destinatarios.negociado from destinatarios "
					+ "INNER JOIN entrada ON entrada.id = destinatarios.entrada_id "
					+ "AND entrada.id = "+id);
			PreparedStatement st = connect.prepareStatement("select destinatarios.negociado from destinatarios "
					+ "INNER JOIN entrada ON entrada.id = destinatarios.entrada_id "
					+ "AND entrada.id = '"+id+"'");
			result =  st.executeQuery();
			while (result.next()) {
				resultado.add(result.getString("negociado"));
			}
			
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
			return null;
		}
		
		return resultado;
	}

	public boolean borrarDestinos(int id) {
		int result = -2;
		boolean exist = false;
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM destinatarios WHERE entrada_id = ?");
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

	public void actualizarComentarioTramitado(comentarioProd comentario) {
		String sql = "UPDATE comentario SET tramitadoPor= ? WHERE entrada_id = ?";
		try {
			PreparedStatement st = connect.prepareStatement(sql);
			st.setString(1, comentario.getTramitadoPor());
			st.setInt(2, comentario.getEntrada_id());
			st.execute();
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}		
	}

	
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
			PreparedStatement st = connect.prepareStatement(
					"insert into entradaNoGestionada (idEntrada) values (?)");
			st.setInt(1, id);
			st.execute();
		} catch (SQLException ex) {
			System.err.println("Insert addEntradaNoGestionada " + ex.getMessage());
		}
		
	}

	public boolean isNotGestionado(String string) {
		ResultSet result = null;
		
		try {
			PreparedStatement st = connect
					.prepareStatement("select * from negociados where nombre = '"+  string + "'");
			result = st.executeQuery();
			if (result.next()) {
				if (result.getBoolean("gestionado") == true) {
					return false;
				}else {
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
			PreparedStatement st = connect.prepareStatement("select * from entradaNoGestionada where idEntrada = '"+  id + "'");
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

	public ResultSet mostrarEntradasPendientesAsignar() {
		ResultSet result = null;
		try {
		
				System.out.println("mostrarEntradasPendientesAsignar");
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

	public void corregirRutas() {
		// TODO Auto-generated method stub
		ResultSet result = null;
		System.out.println("corregirRutas");
		String nuevaRuta = "H:\\alm1\\";
		try {
			PreparedStatement st = connect.prepareStatement("select * from files");
			result = st.executeQuery();
			while (result.next()) {
					String file = result.getString("file");
					int index = result.getInt(1);
					System.out.println(file);
					if (file.subSequence(3, 5).toString().toLowerCase().equals("pl")) {
						System.out.println(nuevaRuta + file.substring(3, file.length()));
						System.out.println("update files set file = '" +  nuevaRuta + file.substring(3, file.length()) + "' where id= " + index);
						PreparedStatement stupdate = connect.prepareStatement("update files set file = '" +  nuevaRuta + file.substring(3, file.length()) + "' where id= " + index);
						stupdate.executeUpdate();
					}
				
				
			}
			
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
	}

	

//	public boolean isSeen(int id) {
//		ResultSet result = null;
//		
//		try {
//			PreparedStatement st = connect
//					.prepareStatement("select * from entradaNoGestionada where idEntrada = '"+  id + "'");
//			result = st.executeQuery();
//			if (result.next()) {
//				if (result.getBoolean("visto") == true) {
//					return true;
//				}else {
//					return false;
//				}
//				
//			} else
//				return false;
//		} catch (SQLException ex) {
//			System.err.println(ex.getMessage());
//			return false;
//		}
//	}

//	public void actualizarEntradaNoGestionada(int id, boolean selected) {
//		try {
//			PreparedStatement st = connect.prepareStatement(
//					"REPLACE into entradaNoGestionada (idEntrada, visto) values (?,?)");
//			st.setInt(1, id);
//			st.setBoolean(2, selected);
//			st.execute();
//		} catch (SQLException ex) {
//			System.err.println("actualizarEntradaNoGestionada " + ex.getMessage());
//		}
//		
//	}

	
	
	
	
}
