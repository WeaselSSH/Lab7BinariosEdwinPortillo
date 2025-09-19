package spotify;

import Nodos.ManejoArchivos;
import Nodos.MusicNode;
import Nodos.MusicNodeList;
import Nodos.SongData;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SongGUI extends BaseFrame {

    public SongGUI() {
        super("Spotify", 600, 500);
        initComponents();
    }

    private JButton btnPlay, btnStop, btnPause, btnSelect, btnAdd, btnRemove;

    //clases
    private ManejoArchivos manejoArchivos;
    private MusicNodeList musicNodeList;
    private SongData songData;
    private AudioPlayer player;
    private SongData currentSong;

    @Override
    protected void initComponents() {
        manejoArchivos = new ManejoArchivos();
        musicNodeList = new MusicNodeList();
        player = new AudioPlayer();

        try {
            for (SongData s : manejoArchivos.loadAll()) {
                musicNodeList.add(s);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar canciones: " + e.getMessage());
        }

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

        btnPlay.addActionListener(e -> playMusic());
        btnStop.addActionListener(e -> stopMusic());
        btnPause.addActionListener(e -> pauseMusic());
        btnSelect.addActionListener(e -> selectMusic());
        btnAdd.addActionListener(e -> addMusic());
        btnRemove.addActionListener(e -> removeMusic());

        setContentPane(panelPrincipal);
    }

    private void playMusic() {
        try {
            if (currentSong == null) {
                currentSong = songData;
                if (currentSong == null) {
                    JOptionPane.showMessageDialog(this, "No hay canci�n seleccionada.");
                    return;
                }
            }
            if (!player.isPlaying()) {
                player.load(currentSong.getPath());
            }
            player.play();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al reproducir: " + e.getMessage());
        }
    }

    private void stopMusic() {
        player.stop();
        currentSong = null;
    }

    private void pauseMusic() {
        if (player == null || !player.isPlaying()) {
            JOptionPane.showMessageDialog(this, "No hay m�sica reproduci�ndose.");
            return;
        }
        player.pause();
        JOptionPane.showMessageDialog(this, "Canci�n en pausa.");
    }

    private void selectMusic() {
        String codeString = JOptionPane.showInputDialog(this, "C�digo de la canci�n a seleccionar:");
        if (codeString == null) {
            return;
        }

        int code;
        try {
            code = Integer.parseInt(codeString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El c�digo debe ser un n�mero.");
            return;
        }

        SongData encontrada = musicNodeList.search(code);
        if (encontrada == null) {
            JOptionPane.showMessageDialog(this, "No se encontr� ninguna canci�n con ese c�digo.");
            return;
        }

        player.stop();
        currentSong = encontrada;
        songData = encontrada;
        JOptionPane.showMessageDialog(this, "Canci�n seleccionada: " + encontrada.getNombreCancion());
    }

    private void addMusic() {
        JFileChooser fcAudio = new JFileChooser();
        fcAudio.setDialogTitle("Favor seleccione un MP3:");
        int resultadoAudio = fcAudio.showOpenDialog(this);
        if (resultadoAudio != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File audio = fcAudio.getSelectedFile();

        JFileChooser fcImg = new JFileChooser();
        fcImg.setDialogTitle("Selecciona una imagen (opcional)");
        int resultadoImg = fcImg.showOpenDialog(this);
        String imagePath = (resultadoImg == JFileChooser.APPROVE_OPTION)
                ? fcImg.getSelectedFile().getAbsolutePath()
                : "";

        String codeString = JOptionPane.showInputDialog(this, "C�digo de la canci�n:");
        if (codeString == null) {
            return;
        }
        int code;
        try {
            code = Integer.parseInt(codeString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El c�digo debe ser un n�mero.");
            return;
        }

        String nombre = JOptionPane.showInputDialog(this, "Nombre de la canci�n:", audio.getName());
        if (nombre == null) {
            return;
        }
        String artista = JOptionPane.showInputDialog(this, "Artista:", "Desconocido");
        if (artista == null) {
            return;
        }
        String genero = JOptionPane.showInputDialog(this, "G�nero:", "N/A");
        if (genero == null) {
            return;
        }

        double duracion = 0.0;

        SongData nuevaCancion = new SongData(code, nombre, artista, duracion, audio.getAbsolutePath(),
                genero, imagePath);

        try {
            manejoArchivos.addSong(nuevaCancion);
            musicNodeList.add(nuevaCancion);
            songData = nuevaCancion;
            JOptionPane.showMessageDialog(this, "Canci�n agregada con �xito.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar la canci�n: " + e.getMessage());
        }
    }

    private void removeMusic() {
        String codeString = JOptionPane.showInputDialog(this, "C�digo de la canci�n a eliminar:");
        if (codeString == null) {
            return;
        }

        int code;
        try {
            code = Integer.parseInt(codeString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El c�digo debe ser un n�mero.");
            return;
        }
        
        if (currentSong != null && currentSong.getCode() == code) {
            stopMusic();
        }

        try {
            boolean eliminadoArchivo = manejoArchivos.removeSong(code);
            boolean eliminadoLista = musicNodeList.remove(code);

            if (eliminadoArchivo && eliminadoLista) {
                JOptionPane.showMessageDialog(this, "Canci�n eliminada correctamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontr� ninguna canci�n con ese c�digo.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar la canci�n: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new SongGUI().setVisible(true);
    }
}
