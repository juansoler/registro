package service;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxEditor;

public class AutoCompleteComboBox extends JComboBox {
	   public int caretPos = 0;
	   public JTextField tfield = null;
	   public AutoCompleteComboBox(final Object countries[]) {
	      super(countries);
	      setEditor(new BasicComboBoxEditor());
	      setEditable(true);
	   }
	   public void setSelectedIndex(int index) {
	      super.setSelectedIndex(index);
	      tfield.setText(getItemAt(index).toString());
	      tfield.setSelectionEnd(caretPos + tfield.getText().length());
	      tfield.moveCaretPosition(caretPos);
	   }
	   public void setEditor(ComboBoxEditor editor) {
	      super.setEditor(editor);
	      if(editor.getEditorComponent() instanceof JTextField) {
	         tfield = (JTextField) editor.getEditorComponent();
	         tfield.addKeyListener(new KeyAdapter() {
	            public void keyReleased(KeyEvent ke) {
	               char key = ke.getKeyChar();
	               if (!(Character.isLetterOrDigit(key) || Character.isSpaceChar(key) )) return;
	               caretPos = tfield.getCaretPosition();
	               String text="";
	               try {
	                  text = tfield.getText(0, caretPos);
	               } catch (javax.swing.text.BadLocationException e) {
	                  e.printStackTrace();
	               }
	               for (int i=0; i < getItemCount(); i++) {
	                  String element = (String) getItemAt(i);
	                  
	                  if (element.toLowerCase().startsWith(text.toLowerCase())) {
	                     setSelectedIndex(i);
	                     return;
	                  }
	               }
	            }
	         });
	      }
	   }
	}