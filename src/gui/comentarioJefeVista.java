package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import gui.editComentario.SimpleDocumentListener;
import models.Usuario;
import models.comentarioJefe;
import models.entrada;
import service.db;
import java.awt.Dimension;
import javax.swing.JLabel;

public class comentarioJefeVista {

	JCheckBox jefeVisto = new JCheckBox("Visto");
	public entrada entrada;
	private db db;
	private JCheckBox soloJefe1;
	private JTextArea jefe1;
	public JPanel panel = new JPanel();
	public JScrollPane scroll = new JScrollPane();
	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	DateFormat timeFormat = new SimpleDateFormat("HH:mm");
	private final JLabel usuarioComentarioLabel = new JLabel("");
	private final JLabel fechaHoraLabel = new JLabel("");

	public comentarioJefeVista(editComentario editComentario, comentarioJefe comentario, String usuario, int entradaId,
			boolean enableJefe, JPopupMenu menu, boolean cambiosRealizados) {
		panel.setPreferredSize(new Dimension(600, 180));
		db = new db();

//		db.connect();
//		entrada = db.mostrarEntrada(entradaId);
//		db.close();
//		db.connect();
//		Usuario usuarioComentario = db.getUsuario(comentario.getUsuario_id());
//		db.close();
//		System.out.println("usuario_id "  + comentario.getUsuario_id());
//		System.out.println("nombre usuario " + usuarioComentario.getNombre_usuario());
//		usuarioComentarioLabel.setText("Usuario: " + usuarioComentario.nombre_usuario);
		
		// pendiente cambiar tabla Usuario y Jefes para que cuando un Jefe escriba un comentario se ponga que Usuario estaba asociado a esa jefatura
		
		
		GridBagLayout gbl_panel = new GridBagLayout();

		gbl_panel.columnWidths = new int[] {0, 59, 0, 0};
		gbl_panel.rowHeights = new int[] {0, 3, 100, 5};
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
		panel.setLayout(gbl_panel);

		jefeVisto.setSelected(comentario.getVisto() > 0 ? true : false);

		jefeVisto.setEnabled(enableJefe);
		
//		usuarioComentarioLabel.setText("Usuario: " + comentario.getUsuarioNombre());
		fechaHoraLabel.setText("Usuario: " + comentario.getUsuarioNombre() + " Fecha comentario: " + comentario.getFecha() + " Hora: " + comentario.getHora());

		jefeVisto.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("ACTION LISTENER JEFE VISTO");
				comentario.setComentario(jefe1.getText());
				Date date = new Date();

				comentario.setFecha(dateFormat.format(date));
				comentario.setHora(timeFormat.format(date));
				if (!jefe1.getText().equals("")) {
					comentario.setVisto(2);
				} else if (jefeVisto.isSelected()) {
					comentario.setVisto(1);
				} else {
					comentario.setVisto(0);
				}
				db.connect();
				db.actualizarComentario(comentario);
				db.close();
			}
		});
		
		GridBagConstraints gbc_usuarioComentarioLabel = new GridBagConstraints();
		gbc_usuarioComentarioLabel.insets = new Insets(0, 0, 5, 5);
		gbc_usuarioComentarioLabel.gridx = 1;
		gbc_usuarioComentarioLabel.gridy = 0;
		panel.add(usuarioComentarioLabel, gbc_usuarioComentarioLabel);
		GridBagConstraints gbc_jefe1visto = new GridBagConstraints();
		gbc_jefe1visto.anchor = GridBagConstraints.NORTHWEST;
		gbc_jefe1visto.insets = new Insets(0, 0, 5, 5);
		gbc_jefe1visto.gridx = 0;
		gbc_jefe1visto.gridy = 1;
		panel.add(jefeVisto, gbc_jefe1visto);

		jefe1 = new JTextArea();
		jefe1.setComponentPopupMenu(menu);
		jefe1.setText(comentario.getComentario());

		jefe1.setEditable(enableJefe);
		// jefe1.addKeyListener(this); pendiente
		jefe1.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
			// cambiosRealizados = true; pendiente
			comentario.setComentario(jefe1.getText());
			Date date = new Date();

			comentario.setFecha(dateFormat.format(date));
			comentario.setHora(timeFormat.format(date));
			if (!jefe1.getText().equals("")) {
				comentario.setVisto(2);
			} else if (jefeVisto.isSelected()) {
				comentario.setVisto(1);
			} else {
				comentario.setVisto(0);
			}
//			db.connect();
//			db.actualizarComentario(comentario);
//			db.close();
		});
		
		GridBagConstraints gbc_fechaHoraLabel = new GridBagConstraints();
		gbc_fechaHoraLabel.anchor = GridBagConstraints.WEST;
		gbc_fechaHoraLabel.insets = new Insets(0, 0, 5, 5);
		gbc_fechaHoraLabel.gridx = 1;
		gbc_fechaHoraLabel.gridy = 1;
		panel.add(fechaHoraLabel, gbc_fechaHoraLabel);

		jefe1.setLineWrap(true);
		jefe1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Comentario", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		GridBagConstraints gbc_jefe1 = new GridBagConstraints();
		gbc_jefe1.insets = new Insets(0, 0, 5, 0);
		gbc_jefe1.gridwidth = 3;
		gbc_jefe1.fill = GridBagConstraints.BOTH;
		gbc_jefe1.gridx = 0;
		gbc_jefe1.gridy = 2;
		JScrollPane scrollBar=new JScrollPane(jefe1,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//		scroll.add(jefe1);

		panel.add(scrollBar, gbc_jefe1);
		jefe1.setColumns(20);

		// lblFecha = new JLabel(" Actualizado : ");
		// GridBagConstraints gbc_lblFecha = new GridBagConstraints();
		// gbc_lblFecha.insets = new Insets(0, 0, 5, 5);
		// gbc_lblFecha.gridx = 0;
		// gbc_lblFecha.gridy = 1;
		// panel.add(lblFecha, gbc_lblFecha);

	}

}
