package gui;

import java.awt.Dimension;
import java.util.Date;
import java.util.Locale;

import javax.swing.JFrame;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.awt.event.ActionEvent;

public class selectRangeDates {
	private JFrame f = new JFrame("Seleccionar Rango de fechas");
	public String[] dates; 
	JDatePickerImpl datePickerFrom;
	JDatePickerImpl datePickerTo;
	JDatePanelImpl datePanelFrom;
	JDatePanelImpl datePanelTo;
	UtilDateModel modelFrom;
	UtilDateModel modelTo;

	Date dateFrom;
	Date dateTo;
	public boolean verTodas = false;
	public boolean done = false;
	public boolean verDates = false;
	private boolean error = false;
	

	public selectRangeDates(String[] datesGUI) {
		
		if (datesGUI == null) {
			dateFrom = new Date();
			dateTo = new Date();
		}else {
			try {
				dateFrom = new SimpleDateFormat("dd/MM/yyyy").parse(datesGUI[0]);
				dateTo = new SimpleDateFormat("dd/MM/yyyy").parse(datesGUI[1]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		f.setSize(new Dimension(659, 405));
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		URL url = GUI.class.getResource("/entrada.png");
		ImageIcon icon = new ImageIcon(url);
		f.setIconImage(icon.getImage());
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		f.setLocation(dim.width/2-f.getSize().width/2, dim.height/2-f.getSize().height/2);
//		dateFrom = new Date();
//		dateTo = new Date();

		modelFrom = new UtilDateModel(dateFrom);
		modelTo = new UtilDateModel(dateTo);
		datePanelFrom = new JDatePanelImpl(modelFrom);
		datePanelFrom.setBounds(65, 57, 200, 180);
		datePanelTo = new JDatePanelImpl(modelTo);
		datePanelTo.setBounds(347, 57, 200, 180);
		datePanelTo.setLocale(new Locale("es", "ES"));
		datePanelFrom.setLocale(new Locale("es", "ES"));
		datePickerFrom = new JDatePickerImpl(datePanelFrom);
		datePickerTo = new JDatePickerImpl(datePanelTo);
		datePickerTo.getJFormattedTextField().setLocale(new Locale("es", "ES"));
		datePickerFrom.getJFormattedTextField().setLocale(new Locale("es", "ES"));
		datePickerTo.setLocale(new Locale("es", "ES"));
		datePickerFrom.setLocale(new Locale("es", "ES"));
		f.getContentPane().setLayout(null);
		
		f.getContentPane().add(datePanelFrom);
		f.getContentPane().add(datePanelTo);
		
		JLabel lblDesde = new JLabel("Desde");
		lblDesde.setBounds(137, 32, 46, 14);
		f.getContentPane().add(lblDesde);
		
		JLabel lblHasta = new JLabel("Hasta");
		lblHasta.setBounds(430, 32, 46, 14);
		f.getContentPane().add(lblHasta);
		
		JButton btnBuscar = new JButton("Buscar por fecha");
		btnBuscar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					getDates();
					if (error) {
						error = false;
						return;
					}
					f.dispose();
					verDates = true;
					
					GUI.PendienteVer = true;
					
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnBuscar.setBounds(236, 284, 145, 23);
		f.getContentPane().add(btnBuscar);
		
		JButton btnVerTodas = new JButton("Ver todas");
		btnVerTodas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				verTodas = true;
				GUI.PendienteVer = true;
				f.dispose();
			}
		});
		btnVerTodas.setBounds(105, 284, 97, 23);
		f.getContentPane().add(btnVerTodas);
		
		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GUI.PendienteVer = true;
				f.dispose();
			}
		});
		btnCancelar.setBounds(418, 284, 105, 23);
		f.getContentPane().add(btnCancelar);
		
		
		f.setVisible(true);
	}


	public String[] getDates() throws ParseException {
		// TODO Auto-generated method stub
		dates = new String[2];
		Date dateFrom = new SimpleDateFormat("dd-MMM-yyyy").parse(datePickerFrom.getJFormattedTextField().getText());
		Date dateTo = new SimpleDateFormat("dd-MMM-yyyy").parse(datePickerTo.getJFormattedTextField().getText());

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("es_ES"));

		if (dateTo.compareTo(dateFrom) == -1) {
			JOptionPane.showMessageDialog(null, "La fecha 'hasta' no puede ser anterior a la fecha 'desde'");
			error = true;
		}
		
		dates[0]= dateFormat.format(dateFrom);
		
		dates[1]= dateFormat.format(dateTo);
		
		return dates;
	}
}
