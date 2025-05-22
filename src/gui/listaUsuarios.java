package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import service.db;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.CardLayout;


public class listaUsuarios {
	private JFrame f = new JFrame("Listado usuarios");
	UtilDateModel model;
	JDatePanelImpl datePanel;
	JDatePickerImpl datePicker;
	JPanel tablaPanel; 
	JTable table;
	db db;
	DefaultTableModel tablemodel;
	public HashSet<Integer> entradaId = new HashSet<Integer>();
	private URL urlVacio;
	private URL urlSingle;
	private ImageIcon iconVacio;
	private ImageIcon iconSingle; 

	public listaUsuarios() {
		f.setSize(new Dimension(863, 480));
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		db = new db();

		Date date = new Date();
		model = new UtilDateModel(date);
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		//System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
		
		
		//Calendar calendar = Calendar.getInstance();
		//System.out.println(calendar.get(Calendar.YEAR));
		//model.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		datePanel = new JDatePanelImpl(model);
		
		datePicker = new JDatePickerImpl(datePanel);
		
		db.connect();
        f.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        tablaPanel = TableFromDatabase(db.mostrarUsuarios());
        
		f.getContentPane().add(tablaPanel);
		db.close();
		
		f.setVisible(true);
	}
	
	public JPanel TableFromDatabase(ResultSet rs) {
		JPanel result = new JPanel();
		result.setPreferredSize(new Dimension(1640, 1480));
		result.setSize(new Dimension(1640, 1480));
		
        Vector columnNames = new Vector();
        Vector data = new Vector();

        try {
            //  Connect to an Access Database
            
            //  Read data from a table
           
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();

            //  Get column names
            for (int i = 1; i <= columns; i++) {
            	
//            	if (md.getColumnName(i).equals("password")) {
//            		continue;
//            	}
//            	
//            	if (md.getColumnName(i).equals("permiso")) {
//                	continue;
//            	}
            	
            	

            	
                columnNames.addElement(md.getColumnName(i));
                
                if (i == columns) {
//                	columnNames.addElement("Permiso crear/borrar entrada");
                	


            	}
                
                
                
                
                
            }
            columnNames.addElement("Reset password");
        	columnNames.addElement("Eliminar usuario");

            //  Get row data
            while (rs.next()) {
                Vector row = new Vector(columns);

                for (int i = 1; i <= columns; i++) {
                	
                	URL url = GUI.class.getResource("/reset.png");
					ImageIcon icon = new ImageIcon(url);
					
					URL urlDelete = GUI.class.getResource("/delete.png");
					ImageIcon delete = new ImageIcon(urlDelete);
					
					urlVacio = GUI.class.getResource("/vacio.png");
					urlSingle = GUI.class.getResource("/single.png");
					iconVacio = new ImageIcon(urlVacio);
					iconSingle = new ImageIcon(urlSingle);
					System.out.println("i = " +i);
                	
                	if (i == 1) {
                		row.addElement(rs.getString("ID"));
                	}
                	
                	if (i == 2) {
                		
                    		row.addElement(rs.getString("Nombre Usuario"));
                		
                	}
                	
                	if (i == 3) {
                		row.addElement(rs.getString("Negociado"));
                	}
                	
                	if (i == 4) {
                		
                		 if (rs.getInt("Permiso Leer/Escribir") == 0) {
								row.addElement(iconVacio);

						  }else {
								row.addElement(iconSingle);
						  }
                		 row.addElement(icon);
                     	row.addElement(delete);
                	}
                	


                	
                	
                	
                	
                	
//            		row.addElement(rs.getObject(i));
//
//                	if (i==3) {
//                		continue;
//                	}else {
//                		row.addElement(rs.getObject(i));
//                	}
//                	
//                	
//                	
//                
//
//					
//					
//					if (i == 4) {
//					
//						  if (rs.getInt(i+1) == 0) {
//								row.addElement(iconVacio);
//
//						  }else {
//								row.addElement(iconSingle);
//						  }
//					}
//					if (i == columns-1) {
//						row.addElement(icon);
//						row.addElement(delete);
//					}
					
					
                	
                	/* if ((i > 5) && (i<=10)) {
                		 if (rs.getObject(i) == null) continue;
                		 System.out.println(columns + " columnas " + i + " dentro if nuevo " + rs.getObject(i));
                		 if (rs.getObject(i).equals(1)) {
                			 System.out.println(i + " dentro if nuevo equals " + rs.getObject(i));
                			 row.addElement(true);
                		 }else	row.addElement(false);

                       
                	}else {
                        row.addElement(rs.getObject(i));
                	}
                	*/
                }
                if (!rs.getString("Nombre Usuario").equals("admin")) {
                	data.addElement(row);
        		}

            }

            rs.close();
            
        } catch (Exception e) {
            System.out.println(e);
        }

        //  Create table with database data
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
            
            @Override  // hace no editable la columna 0
            public boolean isCellEditable(int row, int col) {
            	return false;
                /*
                 * if (col == 3) {
                 * return false;
                	}else return true;
                 */
                	
          }
           
            
            
        };
        
        JTable table = new JTable(tablemodel);
		table.setRowHeight(25);

        table.setAutoCreateRowSorter(true);
        
        table.addKeyListener(new KeyListener() {
			
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
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					System.out.println("Prueba");
				}
			}
		});
        
        table.addMouseListener(new MouseAdapter() {
        	  public void mouseClicked(MouseEvent e) {
        	    if (e.getClickCount() == 2) {
        	    	
        	      JTable target = (JTable)e.getSource();
        	      int row = target.getSelectedRow();
        	      int column = target.getSelectedColumn();
        	     // do some stuff
        	      if (column == 4) {
        	    	  System.out.println(row + " " + column + " valor " +  target.getValueAt(row, 1));
            	      db.connect();
            	      db.resetPassword((String)target.getValueAt(row,1));        	      
            	      //entradaId.add((Integer) target.getValueAt(row, column));
//            	      new editComentario((int)target.getValueAt(row, column));
            	      db.close();
            	      JOptionPane.showMessageDialog(null, "La contraseña de " + target.getValueAt(row, 1) + " ha sido reseteada. Ponga reset como password en el siguiente login");

        	      }
        	      
        	      if (column == 5) {
        	    	  
//            	      JOptionPane.showMessageDialog(null, "Esta opción ha sido deshabilitada.");

        	    	  
//        	    	  
        	    	  db.connect();
            	      db.deleteUser((String)target.getValueAt(row,1));        	      
            	      //entradaId.add((Integer) target.getValueAt(row, column));
//            	      new editComentario((int)target.getValueAt(row, column));
            	      db.close();            	     
            	      
            	      JOptionPane.showMessageDialog(null, "El usuario " + target.getValueAt(row, 1) + " ha sido eliminado.");
            	      DefaultTableModel dtm = (DefaultTableModel) target.getModel();

            	      dtm.removeRow(row);
        	      }
        	      
        	      if (column == 3) {
        	    	  
        	    	  db.connect();
            	      boolean permisoUsuario = db.getPermisosUsuario(Integer.parseInt(target.getValueAt(row,0).toString()));        	      
            	      db.close();  
            	      
            	      
        	    	  
            	      db.connect();
            	      db.cambiarPermisosUsuario((Integer.parseInt(target.getValueAt(row,0).toString())),!permisoUsuario);        	      
            	      //entradaId.add((Integer) target.getValueAt(row, column));
//            	      new editComentario((int)target.getValueAt(row, column));
            	      db.close();  
					  
            	      if (!permisoUsuario) {
            	    	  target.setValueAt(iconSingle, row, column);
            	      }else {
            	    	  target.setValueAt(iconVacio, row, column);

            	      }

        	    
        	      }
        	      
        	    	
        	    }
        	  }
        	});
        result.setLayout(null);
        table.setSize(new Dimension(1640, 1480));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(408, 11, 824, 402);
        result.add(scrollPane);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(623, 201, 10, 10);
        result.add(buttonPanel);
		return result;
    }

}
