package spotify;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SongGUI extends BaseFrame {

    public SongGUI() {
        super("Spotify", 600, 500);
    }

    private JButton btnPlay, btnStop, btnPause, btnSelect, btnAdd, btnRemove;

    @Override
    protected void initComponents() {
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        //resto de paneles
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        panelNorte.setPreferredSize(new Dimension(0, 60));
        panelNorte.setOpaque(false);
        panelPrincipal.add(panelNorte, BorderLayout.NORTH);

        JLabel lblTitulo = crearLabel("Spotify", 0, 0, 0, 0, Font.BOLD, 32f);
        panelNorte.add(lblTitulo);

        JPanel panelCentro = new JPanel(null);
        panelCentro.setOpaque(false);
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        btnPlay = crearBoton("Play", 240, 270, 100, 40);
        panelCentro.add(btnPlay);

        btnStop = crearBoton("Stop", 380, 270, 100, 40);
        panelCentro.add(btnStop);

        btnPause = crearBoton("Pause", 110, 270, 100, 40);
        panelCentro.add(btnPause);

        btnSelect = crearBoton("Select", 240, 340, 100, 40);
        panelCentro.add(btnSelect);

        btnAdd = crearBoton("Add", 380, 340, 100, 40);
        panelCentro.add(btnAdd);

        btnRemove = crearBoton("Remove", 110, 340, 100, 40);
        panelCentro.add(btnRemove);

        setContentPane(panelPrincipal);
    }

    public static void main(String[] args) {
        new SongGUI().setVisible(true);
    }
}