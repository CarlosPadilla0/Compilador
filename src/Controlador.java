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

    public Controlador(Scanner mod, View v1) {
        this.mod = mod;
        this.pantalla = v1;
        pantalla.ActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == pantalla.getAbrirArchivo()) {
            manejarAbrirArchivo();
            return;
        }
        if (e.getSource() == pantalla.getLimpiar()) {
            manejarLimpiar();
            return;
        }
        if (e.getSource() == pantalla.getAnalizar()) {
            manejarAnalizar();
            return;
        }
        if (e.getSource() == pantalla.getSintactico()) {
            manejarSintactico();
            return;
        }
        if (e.getSource() == pantalla.getSemantico()) { 
            manejarSemantico();
            return;
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
            String codigoIntermedio = mod.generarCodigoIntermedio();  
            pantalla.mostrarCodigoIntermedio(codigoIntermedio);  
        }
    }


}

