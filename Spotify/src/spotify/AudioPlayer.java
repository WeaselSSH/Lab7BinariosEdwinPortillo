package spotify;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.FileInputStream;

public class AudioPlayer {

    private AdvancedPlayer player;
    private Thread hilo;
    private String rutaCancion;
    private int framePausado = 0;
    private int frameUltimo = 0;
    private boolean reproduciendo = false;

    public void cargar(String ruta) {
        pausar();
        rutaCancion = ruta;
        framePausado = 0;
        frameUltimo = 0;
    }

    public void reproducir() {
        if (rutaCancion == null || reproduciendo) {
            return;
        }

        reproduciendo = true;
        hilo = new Thread(() -> {
            try (FileInputStream fis = new FileInputStream(rutaCancion)) {
                player = new AdvancedPlayer(fis);
                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        frameUltimo = evt.getFrame();
                        reproduciendo = false;
                    }
                });
                player.play(framePausado, Integer.MAX_VALUE);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                reproduciendo = false;
                player = null;
                hilo = null;
            }
        }, "hilo-reproductor");
        hilo.start();
    }

    public void detner() {
        if (!reproduciendo || player == null) {
            return;
        }
        framePausado = Math.max(frameUltimo, framePausado);
        try {
            player.close();
        } catch (Exception ignored) {
        }
        esperarHilo();
        reproduciendo = false;
        player = null;
    }

    public void pausar() {
        try {
            if (player != null) {
                player.close();
            }
        } catch (Exception ignored) {
        }
        esperarHilo();
        reproduciendo = false;
        framePausado = 0;
        frameUltimo = 0;
        player = null;
    }

    public boolean isPlaying() {
        return reproduciendo;
    }

    private void esperarHilo() {
        try {
            if (hilo != null && hilo.isAlive()) {
                hilo.join(120);
            }
        } catch (InterruptedException ignored) {
        }
    }
}
