import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Controlador implements ActionListener {
    private Scanner mod;
    private View pantalla;
    private String codigoIntermedio; // Variable para almacenar el código intermedio

    public Controlador(Scanner mod, View v1) {
        this.mod = mod;
        this.pantalla = v1;
        pantalla.ActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == pantalla.getAbrirArchivo()) {
            manejarAbrirArchivo();
        }
        if (e.getSource() == pantalla.getLimpiar()) {
            manejarLimpiar();
        }
        if (e.getSource() == pantalla.getAnalizar()) {
            manejarAnalizar();
        }
        if (e.getSource() == pantalla.getSintactico()) {
            manejarSintactico();
        }
        if (e.getSource() == pantalla.getSemantico()) { 
            manejarSemantico();
        }
        if (e.getSource() == pantalla.getGenerarCodigoObjeto()) {
            manejarGenerarCodigoObjeto();
        }
    }

    private void manejarAbrirArchivo() {
        System.out.println("Presionado");
        int returnValue = pantalla.getFileChooser().showOpenDialog(pantalla);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = pantalla.getFileChooser().getSelectedFile();
            try {
                String content = new String(Files.readAllBytes(selectedFile.toPath()));
                pantalla.getTxtFuente().setText(content);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al leer el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void manejarLimpiar() {
        pantalla.getTxtFuente().setText("");
        pantalla.getTxtErrores().setText("");
        ((DefaultListModel<String>) pantalla.getLstResultado().getModel()).clear();

        mod.reset();
        codigoIntermedio = null; // Limpiar el código intermedio almacenado
    }

    private void manejarAnalizar() {
        mod.setContenido(pantalla.getTxtFuente().getText());
        List<Token> tokens = mod.analizarLexico();
        DefaultListModel<String> model = (DefaultListModel<String>) pantalla.getLstResultado().getModel();
        model.clear();
        for (Token token : tokens) {
            model.addElement(token.toString());
        }
    }

    private void manejarSintactico() {
        String resultado = mod.analizarSintactico();
        pantalla.getTxtErrores().setText(resultado);
    }

    private void manejarSemantico() {
        String resultadoSemantico = mod.analizarSemantico();  
        pantalla.getTxtErrores().setText(resultadoSemantico);  
        
        if (resultadoSemantico.equalsIgnoreCase("Análisis semántico completado sin errores.")) {
            System.out.println("Generando código intermedio en Controlador...");
            codigoIntermedio = mod.generarCodigoIntermedio();  // Generar y almacenar el código intermedio
            pantalla.mostrarCodigoIntermedio(codigoIntermedio);  
        }
    }

    private void manejarGenerarCodigoObjeto() {
        if (codigoIntermedio == null) {
            System.out.println("Generando código intermedio antes de generar código objeto en Controlador...");
            codigoIntermedio = mod.generarCodigoIntermedio(); // Generar el código intermedio si no está almacenado
        }
        String codigoObjeto = mod.generarCodigoObjeto();
        pantalla.mostrarCodigoObjeto(codigoObjeto);
    }
}
