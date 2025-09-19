package spotify;

import Nodos.ManejoArchivos;
import Nodos.MusicNodeList;
import Nodos.SongData;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JProgressBar;
import javax.swing.Timer;

public class SongGUI extends BaseFrame {

    public SongGUI() {
        super("Spotify", 600, 540);
        initComponents();
    }

    private JButton btnPlay, btnStop, btnPause, btnSelect, btnAdd, btnRemove;
    private AudioPlayer player;
    private ManejoArchivos manejoArchivos;
    private MusicNodeList musicNodeList;
    private SongData songData;
    private SongData currentSong;
    private String loadedPath;

    private JLabel cover;
    private JLabel lblNombre;
    private JLabel lblTranscurrido;
    private JLabel lblRestante;
    private JProgressBar barra;

    private Timer timer;
    private int elapsedSec = 0;

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

        JPanel root = new JPanel(null);
        root.setBackground(new Color(24, 26, 32));
        setContentPane(root);

        JLabel titulo = crearLabel("Spotify", 0, 0, 0, 0, Font.BOLD, 30f);
        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(JLabel.CENTER);
        titulo.setBounds(0, 12, 600, 40);
        root.add(titulo);

        cover = new JLabel("Sin portada", JLabel.CENTER);
        cover.setForeground(Color.WHITE);
        cover.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new Color(255, 255, 255, 120), 1),
                javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        cover.setBounds(170, 70, 260, 260);
        root.add(cover);

        lblNombre = new JLabel("—", JLabel.CENTER);
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(lblNombre.getFont().deriveFont(Font.BOLD, 16f));
        lblNombre.setBounds(40, 340, 520, 24);
        root.add(lblNombre);

        lblTranscurrido = new JLabel("00:00");
        lblTranscurrido.setForeground(Color.WHITE);
        lblTranscurrido.setBounds(80, 374, 60, 20);
        root.add(lblTranscurrido);

        barra = new JProgressBar(0, 100);
        barra.setValue(0);
        barra.setOpaque(true);
        barra.setBackground(new Color(40, 42, 48));
        barra.setBounds(150, 374, 300, 18);
        root.add(barra);

        lblRestante = new JLabel("--:--");
        lblRestante.setForeground(Color.WHITE);
        lblRestante.setBounds(460, 374, 60, 20);
        root.add(lblRestante);

        btnPause = crearBoton("Pause", 110, 420, 100, 36);
        btnPlay = crearBoton("Play", 250, 420, 100, 36);
        btnStop = crearBoton("Stop", 390, 420, 100, 36);
        btnSelect = crearBoton("Select", 110, 468, 100, 36);
        btnAdd = crearBoton("Add", 250, 468, 100, 36);
        btnRemove = crearBoton("Remove", 390, 468, 100, 36);

        root.add(btnPause);
        root.add(btnPlay);
        root.add(btnStop);
        root.add(btnSelect);
        root.add(btnAdd);
        root.add(btnRemove);

        btnPlay.addActionListener(e -> playMusic());
        btnStop.addActionListener(e -> detenerMusica());
        btnPause.addActionListener(e -> pausarMusica());
        btnSelect.addActionListener(e -> selectMusic());
        btnAdd.addActionListener(e -> addMusic());
        btnRemove.addActionListener(e -> removeMusic());

        renderCancionActual(null);
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
            File f = new File(currentSong.getPath());
            if (!f.exists()) {
                JOptionPane.showMessageDialog(this, "El archivo no existe: " + currentSong.getPath());
                return;
            }
            if (loadedPath == null || !loadedPath.equals(currentSong.getPath())) {
                player.detener();
                player.cargar(currentSong.getPath());
                loadedPath = currentSong.getPath();
                elapsedSec = 0;
            }
            player.reproducir();
            renderCancionActual(currentSong);
            startOrUpdateTimerFor(currentSong);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al reproducir: " + e.getMessage());
        }
    }

    private void detenerMusica() {
        player.detener();
        stopTimerAndResetProgress();
        currentSong = null;
        loadedPath = null;
        renderCancionActual(null);
    }

    private void pausarMusica() {
        if (player == null || !player.isPlaying()) {
            JOptionPane.showMessageDialog(this, "No hay música reproduciéndose.");
            return;
        }
        player.pausar();
        stopTimerKeepProgress();
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

        SongData s = musicNodeList.search(code);
        if (s == null) {
            JOptionPane.showMessageDialog(this, "No se encontró ninguna canción con ese código.");
            return;
        }

        player.detener();
        stopTimerAndResetProgress();
        currentSong = s;
        songData = s;
        loadedPath = null;
        renderCancionActual(currentSong);
        JOptionPane.showMessageDialog(this, "Canción seleccionada: " + s.getNombreCancion());
    }

    private void addMusic() {
        JFileChooser fcAudio = new JFileChooser();
        fcAudio.setDialogTitle("Favor seleccione un MP3:");
        int rAudio = fcAudio.showOpenDialog(this);
        if (rAudio != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File audio = fcAudio.getSelectedFile();

        JFileChooser fcImg = new JFileChooser();
        fcImg.setDialogTitle("Selecciona una imagen (opcional)");
        int rImg = fcImg.showOpenDialog(this);
        String imagePath = (rImg == JFileChooser.APPROVE_OPTION) ? fcImg.getSelectedFile().getAbsolutePath() : "";

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

        SongData nueva = new SongData(code, nombre, artista, duracion, audio.getAbsolutePath(), genero, imagePath);
        try {
            manejoArchivos.addSong(nueva);
            musicNodeList.add(nueva);
            songData = nueva;
            renderCancionActual(songData);
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
            boolean a = manejoArchivos.removeSong(code);
            boolean b = musicNodeList.remove(code);
            if (a && b) {
                JOptionPane.showMessageDialog(this, "Canción eliminada correctamente.");
                if (songData != null && songData.getCode() == code) {
                    songData = null;
                    renderCancionActual(null);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ninguna canción con ese código.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar la canción: " + e.getMessage());
        }
    }

    private void renderCancionActual(SongData s) {
        if (s == null) {
            cover.setIcon(null);
            cover.setText("Sin portada");
            lblNombre.setText("—");
            lblTranscurrido.setText("00:00");
            lblRestante.setText("--:--");
            barra.setValue(0);
            return;
        }
        if (s.getImagePath() != null && !s.getImagePath().isBlank() && new File(s.getImagePath()).exists()) {
            ImageIcon icon = new ImageIcon(s.getImagePath());
            java.awt.Image scaled = icon.getImage().getScaledInstance(260, 260, java.awt.Image.SCALE_SMOOTH);
            cover.setIcon(new ImageIcon(scaled));
            cover.setText("");
        } else {
            cover.setIcon(null);
            cover.setText("Sin portada");
        }
        lblNombre.setText(s.getNombreCancion() + " — " + s.getArtista());
        int total = totalSecondsOf(s);
        if (total > 0) {
            int percent = Math.min(100, (int) Math.round((elapsedSec * 100.0) / total));
            barra.setValue(percent);
            lblTranscurrido.setText(formatMMSS(elapsedSec));
            int rest = Math.max(total - elapsedSec, 0);
            lblRestante.setText("-" + formatMMSS(rest));
        } else {
            barra.setValue(0);
            lblTranscurrido.setText("00:00");
            lblRestante.setText("--:--");
        }
    }

    private void startOrUpdateTimerFor(SongData s) {
        int total = totalSecondsOf(s);
        if (total <= 0) {
            if (timer != null) {
                timer.stop();
            }
            return;
        }
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(1000, ev -> {
            if (!player.isPlaying()) {
                return;
            }
            elapsedSec = Math.min(elapsedSec + 1, total);
            int percent = Math.min(100, (int) Math.round((elapsedSec * 100.0) / total));
            barra.setValue(percent);
            lblTranscurrido.setText(formatMMSS(elapsedSec));
            int rest = Math.max(total - elapsedSec, 0);
            lblRestante.setText("-" + formatMMSS(rest));
            if (elapsedSec >= total) {
                ((Timer) ev.getSource()).stop();
            }
        });
        timer.start();
    }

    private void stopTimerAndResetProgress() {
        if (timer != null) {
            timer.stop();
        }
        elapsedSec = 0;
        barra.setValue(0);
        lblTranscurrido.setText("00:00");
        lblRestante.setText("--:--");
    }

    private void stopTimerKeepProgress() {
        if (timer != null) {
            timer.stop();
        }
    }

    private int totalSecondsOf(SongData s) {
        if (s == null) {
            return 0;
        }
        Double d = s.getDuracion();
        if (d == null || d <= 0) {
            return 0;
        }
        return (int) Math.round(d * 60.0);
    }

    private String formatMMSS(int totalSec) {
        int mm = totalSec / 60;
        int ss = totalSec % 60;
        return String.format("%02d:%02d", mm, ss);
    }

    public static void main(String[] args) {
        new SongGUI().setVisible(true);
    }
}
