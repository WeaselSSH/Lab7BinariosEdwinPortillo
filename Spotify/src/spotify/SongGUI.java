package spotify;

import Nodos.ManejoArchivos;
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

    private AudioPlayer player;
    private ManejoArchivos manejoArchivos;
    private MusicNodeList musicNodeList;
    private SongData songData;
    private SongData currentSong;
    private String loadedPath;

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

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        setContentPane(panelPrincipal);

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
        btnStop = crearBoton("Stop", 380, 270, 100, 40);
        btnPause = crearBoton("Pause", 110, 270, 100, 40);
        btnSelect = crearBoton("Select", 240, 340, 100, 40);
        btnAdd = crearBoton("Add", 380, 340, 100, 40);
        btnRemove = crearBoton("Remove", 110, 340, 100, 40);

        panelCentro.add(btnPlay);
        panelCentro.add(btnStop);
        panelCentro.add(btnPause);
        panelCentro.add(btnSelect);
        panelCentro.add(btnAdd);
        panelCentro.add(btnRemove);

        btnPlay.addActionListener(e -> playMusic());
        btnStop.addActionListener(e -> pausarMusica());
        btnPause.addActionListener(e -> detenerMusica());
        btnSelect.addActionListener(e -> selectMusic());
        btnAdd.addActionListener(e -> addMusic());
        btnRemove.addActionListener(e -> removeMusic());
    }

    private void playMusic() {
        try {
            if (currentSong == null) {
                currentSong = songData;
                if (currentSong == null) {
                    JOptionPane.showMessageDialog(this, "No hay canción seleccionada.");
                    return;
                }
            }

            File archivo = new File(currentSong.getPath());
            if (!archivo.exists()) {
                JOptionPane.showMessageDialog(this, "El archivo no existe: " + currentSong.getPath());
                return;
            }

            if (loadedPath == null || !loadedPath.equals(currentSong.getPath())) {
                player.pausar();
                player.cargar(currentSong.getPath());
                loadedPath = currentSong.getPath();
            }

            player.reproducir();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al reproducir: " + e.getMessage());
        }
    }

    private void detenerMusica() {
        player.pausar();
        currentSong = null;
        loadedPath = null;
    }

    private void pausarMusica() {
        if (player == null || !player.isPlaying()) {
            JOptionPane.showMessageDialog(this, "No hay música reproduciéndose.");
            return;
        }
        player.pausar();
    }

    private void selectMusic() {
        String codeString = JOptionPane.showInputDialog(this, "Código de la canción a seleccionar:");
        if (codeString == null) {
            return;
        }

        int code;
        try {
            code = Integer.parseInt(codeString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El código debe ser un número.");
            return;
        }

        SongData encontrada = musicNodeList.search(code);
        if (encontrada == null) {
            JOptionPane.showMessageDialog(this, "No se encontró ninguna canción con ese código.");
            return;
        }

        player.pausar();
        currentSong = encontrada;
        songData = encontrada;
        loadedPath = null;

        JOptionPane.showMessageDialog(this, "Canción seleccionada: " + encontrada.getNombreCancion());
    }

    private void addMusic() {
        JFileChooser fcAudio = new JFileChooser();
        fcAudio.setDialogTitle("Favor seleccione un MP3:");
        int audioResult = fcAudio.showOpenDialog(this);
        if (audioResult != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File audio = fcAudio.getSelectedFile();

        JFileChooser fcImg = new JFileChooser();
        fcImg.setDialogTitle("Selecciona una imagen (opcional)");
        int imageResult = fcImg.showOpenDialog(this);
        String imagePath = (imageResult == JFileChooser.APPROVE_OPTION)
                ? fcImg.getSelectedFile().getAbsolutePath()
                : "";

        String codeString = JOptionPane.showInputDialog(this, "Código de la canción:");
        if (codeString == null) {
            return;
        }

        int code;
        try {
            code = Integer.parseInt(codeString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El código debe ser un número.");
            return;
        }

        if (musicNodeList.search(code) != null) {
            JOptionPane.showMessageDialog(this, "Ese código ya existe en la lista.");
            return;
        }
        try {
            if (manejoArchivos.searchSong(code)) {
                JOptionPane.showMessageDialog(this, "Ese código ya existe en el archivo.");
                return;
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error verificando código: " + ex.getMessage());
            return;
        }

        String nombre = JOptionPane.showInputDialog(this, "Nombre de la canción:", audio.getName());
        if (nombre == null) {
            return;
        }

        String artista = JOptionPane.showInputDialog(this, "Artista:", "Desconocido");
        if (artista == null) {
            return;
        }

        String genero = JOptionPane.showInputDialog(this, "Género:", "N/A");
        if (genero == null) {
            return;
        }

        double duracion = 0.0;

        SongData nuevaCancion = new SongData(
                code, nombre, artista, duracion, audio.getAbsolutePath(), genero, imagePath
        );

        try {
            manejoArchivos.addSong(nuevaCancion);
            musicNodeList.add(nuevaCancion);
            songData = nuevaCancion; // dejas como "seleccionada" la última agregada
            JOptionPane.showMessageDialog(this, "Canción agregada con éxito.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar la canción: " + e.getMessage());
        }
    }

    private void removeMusic() {
        String codeString = JOptionPane.showInputDialog(this, "Código de la canción a eliminar:");
        if (codeString == null) {
            return;
        }

        int code;
        try {
            code = Integer.parseInt(codeString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El código debe ser un número.");
            return;
        }

        if (currentSong != null && currentSong.getCode() == code) {
            detenerMusica();
        }

        try {
            boolean eliminadoArchivo = manejoArchivos.removeSong(code);
            boolean eliminadoLista = musicNodeList.remove(code);

            if (eliminadoArchivo && eliminadoLista) {
                JOptionPane.showMessageDialog(this, "Canción eliminada correctamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ninguna canción con ese código.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar la canción: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new SongGUI().setVisible(true);
    }
}
