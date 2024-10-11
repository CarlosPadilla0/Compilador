import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class View extends JFrame implements ComponentListener {
    private static final long serialVersionUID = 1L;
    private JTextArea txtFuente;
    private JTextArea txtErrores;
    private JList<String> lstResultado;
    private JButton limpiar;
    private JButton analizar;
    private JScrollPane scrollFuente;
    private JScrollPane scrollResultado;
    private JScrollPane scrollErrores;
    private JButton abrirArchivo;
    private JFileChooser fileChooser;
    private JButton sintactico;
    private JButton semantico;  
    private JLabel lblFuente;
    private JLabel lblErrores;
    private JLabel lblResultado;
    private JTextArea txtCodigoIntermedio;      
    private JScrollPane scrollCodigoIntermedio;  
    private JLabel lblCodigoIntermedio;  

    public View() {
        super("Análisis Léxico, Sintáctico y Semántico");
        setSize(840, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(true);
        setVisible(true);
        init();
    }

    private void init() {
        setLayout(null);

        getContentPane().setBackground(new Color(245, 245, 245));

        txtFuente = new JTextArea();
        txtFuente.setEditable(true);
        txtFuente.setFont(new Font("Arial", Font.PLAIN, 18));
        txtFuente.setBackground(Color.WHITE);
        txtFuente.setForeground(Color.DARK_GRAY);  
        
        txtErrores = new JTextArea();
        txtErrores.setEditable(false);
        txtErrores.setFont(new Font("Arial", Font.PLAIN, 14));
        txtErrores.setForeground(Color.RED);
        txtErrores.setBackground(new Color(255, 230, 230));

        lstResultado = new JList<>(new DefaultListModel<>());
        lstResultado.setFont(new Font("Arial", Font.PLAIN, 18));
        lstResultado.setBackground(Color.WHITE);
        lstResultado.setForeground(Color.DARK_GRAY);

        limpiar = new JButton("Limpiar");
        limpiar.setBackground(new Color(58, 130, 247));
        limpiar.setForeground(Color.WHITE);
        limpiar.setFont(new Font("Arial", Font.BOLD, 14));

        analizar = new JButton("Analizar");
        analizar.setBackground(new Color(58, 130, 247));
        analizar.setForeground(Color.WHITE);
        analizar.setFont(new Font("Arial", Font.BOLD, 14));

        abrirArchivo = new JButton("Abrir Archivo");
        abrirArchivo.setBackground(new Color(58, 130, 247));
        abrirArchivo.setForeground(Color.WHITE);
        abrirArchivo.setFont(new Font("Arial", Font.BOLD, 14));

        fileChooser = new JFileChooser();

        sintactico = new JButton("Análisis Sintáctico");
        sintactico.setBackground(new Color(58, 130, 247));
        sintactico.setForeground(Color.WHITE);
        sintactico.setFont(new Font("Arial", Font.BOLD, 14));

        semantico = new JButton("Análisis Semántico");
        semantico.setBackground(new Color(58, 130, 247));
        semantico.setForeground(Color.WHITE);
        semantico.setFont(new Font("Arial", Font.BOLD, 14));

        
        txtCodigoIntermedio = new JTextArea();
        txtCodigoIntermedio.setEditable(false);
        txtCodigoIntermedio.setFont(new Font("Arial", Font.PLAIN, 14));
        txtCodigoIntermedio.setBackground(new Color(230, 255, 230));
        txtCodigoIntermedio.setForeground(Color.DARK_GRAY);
        txtCodigoIntermedio.setText(".DATA\n");  

        scrollFuente = new JScrollPane(txtFuente);
        scrollResultado = new JScrollPane(lstResultado);
        scrollErrores = new JScrollPane(txtErrores);
        scrollCodigoIntermedio = new JScrollPane(txtCodigoIntermedio);  

        lblFuente = new JLabel("Código Fuente");
        lblFuente.setFont(new Font("Arial", Font.BOLD, 16));
        lblFuente.setForeground(Color.DARK_GRAY);

        lblErrores = new JLabel("Errores");
        lblErrores.setFont(new Font("Arial", Font.BOLD, 16));
        lblErrores.setForeground(Color.DARK_GRAY);

        lblResultado = new JLabel("Tokens");
        lblResultado.setFont(new Font("Arial", Font.BOLD, 16));
        lblResultado.setForeground(Color.DARK_GRAY);
        
        lblCodigoIntermedio = new JLabel("Código Intermedio");
        lblCodigoIntermedio.setFont(new Font("Arial", Font.BOLD, 16));
        lblCodigoIntermedio.setForeground(Color.DARK_GRAY);
        
   

        add(lblFuente);
        add(lblErrores);
        add(lblResultado);
        add(lblCodigoIntermedio);  
        add(scrollFuente);
        add(scrollResultado);
        add(scrollErrores);
        add(scrollCodigoIntermedio);  
        add(limpiar);
        add(analizar);
        add(abrirArchivo);
        add(sintactico);
        add(semantico);  

        sizes();
        revalidate();
    }

    public void ActionListener(Controlador c) {
        limpiar.addActionListener(c);
        analizar.addActionListener(c);
        abrirArchivo.addActionListener(c);
        sintactico.addActionListener(c);
        semantico.addActionListener(c); 
        this.addComponentListener(this);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        sizes();
        revalidate();
    }

    private void sizes() {
        int w = this.getWidth();
        int h = this.getHeight();

        int botonAncho = (int) (w * 0.14);
        int botonAlto = (int) (h * 0.05);
        int separacion = (int) (w * 0.01);

        lblFuente.setBounds((int) (w * 0.015), (int) (h * 0.01), 150, 30);
        scrollFuente.setBounds((int) (w * 0.015), (int) (h * 0.05), (int) (w * 0.65), (int) (h * 0.35));

        lblResultado.setBounds((int) (w * 0.69), (int) (h * 0.01), 150, 30);
        scrollResultado.setBounds((int) (w * 0.69), (int) (h * 0.05), (int) (w * 0.29), (int) (h * 0.4));

        lblErrores.setBounds((int) (w * 0.015), (int) (h * 0.42), 150, 30);
        scrollErrores.setBounds((int) (w * 0.015), (int) (h * 0.46), (int) (w * 0.65), (int) (h * 0.14));

        lblCodigoIntermedio.setBounds((int) (w * 0.015), (int) (h * 0.61), 200, 30); 
        scrollCodigoIntermedio.setBounds((int) (w * 0.015), (int) (h * 0.65), (int) (w * 0.40), (int) (h * 0.20)); 

        int botonX = (int) (w * 0.69);
        limpiar.setBounds(botonX, (int) (h * 0.51), botonAncho, botonAlto);
        abrirArchivo.setBounds(botonX, (int) (h * 0.52) + botonAlto + separacion, botonAncho, botonAlto);
        analizar.setBounds(botonX, (int) (h * 0.53) + 2 * (botonAlto + separacion), botonAncho, botonAlto);
        sintactico.setBounds(botonX, (int) (h * 0.54) + 3 * (botonAlto + separacion), botonAncho, botonAlto);
        semantico.setBounds(botonX, (int) (h * 0.55) + 4 * (botonAlto + separacion), botonAncho, botonAlto);
    }

    public void mostrarResultado(String resultado) {
        DefaultListModel<String> model = (DefaultListModel<String>) lstResultado.getModel();
        model.clear();
        model.addElement(resultado);
    }

    // Método para mostrar el código intermedio en el nuevo JTextArea
    public void mostrarCodigoIntermedio(String codigo) {
        txtCodigoIntermedio.setText(codigo);  // Actualizar el JTextArea con el código intermedio
    }

    public JTextArea getTxtFuente() {
        return txtFuente;
    }

    public JTextArea getTxtErrores() {
        return txtErrores;
    }

    public JList<String> getLstResultado() {
        return lstResultado;
    }

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public JButton getLimpiar() {
		return limpiar;
	}

	public void setLimpiar(JButton limpiar) {
		this.limpiar = limpiar;
	}

	public JButton getAnalizar() {
		return analizar;
	}

	public void setAnalizar(JButton analizar) {
		this.analizar = analizar;
	}

	public JScrollPane getScrollFuente() {
		return scrollFuente;
	}

	public void setScrollFuente(JScrollPane scrollFuente) {
		this.scrollFuente = scrollFuente;
	}

	public JScrollPane getScrollResultado() {
		return scrollResultado;
	}

	public void setScrollResultado(JScrollPane scrollResultado) {
		this.scrollResultado = scrollResultado;
	}

	public JScrollPane getScrollErrores() {
		return scrollErrores;
	}

	public void setScrollErrores(JScrollPane scrollErrores) {
		this.scrollErrores = scrollErrores;
	}

	public JButton getAbrirArchivo() {
		return abrirArchivo;
	}

	public void setAbrirArchivo(JButton abrirArchivo) {
		this.abrirArchivo = abrirArchivo;
	}

	public JFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(JFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public JButton getSintactico() {
		return sintactico;
	}

	public void setSintactico(JButton sintactico) {
		this.sintactico = sintactico;
	}

	public JButton getSemantico() {
		return semantico;
	}

	public void setSemantico(JButton semantico) {
		this.semantico = semantico;
	}

	public JLabel getLblFuente() {
		return lblFuente;
	}

	public void setLblFuente(JLabel lblFuente) {
		this.lblFuente = lblFuente;
	}

	public JLabel getLblErrores() {
		return lblErrores;
	}

	public void setLblErrores(JLabel lblErrores) {
		this.lblErrores = lblErrores;
	}

	public JLabel getLblResultado() {
		return lblResultado;
	}

	public void setLblResultado(JLabel lblResultado) {
		this.lblResultado = lblResultado;
	}

	public JTextArea getTxtCodigoIntermedio() {
		return txtCodigoIntermedio;
	}

	public void setTxtCodigoIntermedio(JTextArea txtCodigoIntermedio) {
		this.txtCodigoIntermedio = txtCodigoIntermedio;
	}

	public JScrollPane getScrollCodigoIntermedio() {
		return scrollCodigoIntermedio;
	}

	public void setScrollCodigoIntermedio(JScrollPane scrollCodigoIntermedio) {
		this.scrollCodigoIntermedio = scrollCodigoIntermedio;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setTxtFuente(JTextArea txtFuente) {
		this.txtFuente = txtFuente;
	}

	public void setTxtErrores(JTextArea txtErrores) {
		this.txtErrores = txtErrores;
	}

	public void setLstResultado(JList<String> lstResultado) {
		this.lstResultado = lstResultado;
	}
	
}
