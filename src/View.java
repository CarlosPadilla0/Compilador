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
    private JLabel lblFuente;
    private JLabel lblErrores;
    private JLabel lblResultado;

    public View() {
        super("Análisis Léxico y Sintáctico");
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
        txtFuente.setForeground(Color.DARK_GRAY);  // Color de texto

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

        scrollFuente = new JScrollPane(txtFuente);
        scrollResultado = new JScrollPane(lstResultado);
        scrollErrores = new JScrollPane(txtErrores);

        lblFuente = new JLabel("Código Fuente");
        lblFuente.setFont(new Font("Arial", Font.BOLD, 16));
        lblFuente.setForeground(Color.DARK_GRAY);

        lblErrores = new JLabel("Errores");
        lblErrores.setFont(new Font("Arial", Font.BOLD, 16));
        lblErrores.setForeground(Color.DARK_GRAY);

        lblResultado = new JLabel("Tokens");
        lblResultado.setFont(new Font("Arial", Font.BOLD, 16));
        lblResultado.setForeground(Color.DARK_GRAY);

        add(lblFuente);
        add(lblErrores);
        add(lblResultado);
        add(scrollFuente);
        add(scrollResultado);
        add(scrollErrores);
        add(limpiar);
        add(analizar);
        add(abrirArchivo);
        add(sintactico);

        sizes();
        revalidate();
    }

    public void ActionListener(Controlador c) {
        limpiar.addActionListener(c);
        analizar.addActionListener(c);
        abrirArchivo.addActionListener(c);
        sintactico.addActionListener(c);
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

        int botonAncho = (int) (w * 0.18);
        int botonAlto = (int) (h * 0.055);
        int separacion = (int) (w * 0.03);

        lblFuente.setBounds((int) (w * 0.015), (int) (h * 0.01), 150, 30);
        scrollFuente.setBounds((int) (w * 0.015), (int) (h * 0.05), (int) (w * 0.65), (int) (h * 0.5));

        lblResultado.setBounds((int) (w * 0.69), (int) (h * 0.01), 150, 30);
        scrollResultado.setBounds((int) (w * 0.69), (int) (h * 0.05), (int) (w * 0.29), (int) (h * 0.5));

        lblErrores.setBounds((int) (w * 0.015), (int) (h * 0.55), 150, 30); 
        scrollErrores.setBounds((int) (w * 0.015), (int) (h * 0.59), (int) (w * 0.65), (int) (h * 0.2));  

        limpiar.setBounds((int) (w * 0.015), (int) (h * 0.81), botonAncho, botonAlto);
        abrirArchivo.setBounds((int) (w * 0.015 + botonAncho + separacion), (int) (h * 0.81), botonAncho, botonAlto);
        analizar.setBounds((int) (w * 0.015 + 2 * (botonAncho + separacion)), (int) (h * 0.81), botonAncho, botonAlto);
        sintactico.setBounds((int) (w * 0.015 + 3 * (botonAncho + separacion)), (int) (h * 0.81), botonAncho, botonAlto);
    }

    public void mostrarResultado(String resultado) {
        DefaultListModel<String> model = (DefaultListModel<String>) lstResultado.getModel();
        model.clear();
        model.addElement(resultado);
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

    public JButton getLimpiar() {
        return limpiar;
    }

    public JButton getAnalizar() {
        return analizar;
    }

    public JButton getAbrirArchivo() {
        return abrirArchivo;
    }

    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    public JButton getSintactico() {
        return sintactico;
    }

    @Override
    public void componentMoved(ComponentEvent e) {}

    @Override
    public void componentShown(ComponentEvent e) {}

    @Override
    public void componentHidden(ComponentEvent e) {}
}

