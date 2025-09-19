package spotify;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import java.io.FileInputStream;

public class AudioPlayer {

    private AdvancedPlayer player;
    private Thread playThread;
    private String currentPath;
    private int pausedFrame = 0;
    private int lastFrame = 0;
    private boolean playing = false;

    public void load(String path) {
        stop();
        currentPath = path;
        pausedFrame = 0;
        lastFrame = 0;
    }

    public void play() {
        if (currentPath == null || playing) {
            return;
        }

        playing = true;
        playThread = new Thread(() -> {
            try (FileInputStream fis = new FileInputStream(currentPath)) {
                player = new AdvancedPlayer(fis);
                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        lastFrame = evt.getFrame();
                        playing = false;
                    }
                });
                player.play(pausedFrame, Integer.MAX_VALUE);
            } catch (Exception e) {
                e.getMessage();
            } finally {
                playing = false;
                player = null;
            }
        });
        playThread.start();
    }

    public void pause() {
        if (!playing || player == null) {
            return;
        }
        pausedFrame = Math.max(lastFrame, pausedFrame);
        player.close();
        playing = false;
    }

    public void stop() {
        playing = false;
        pausedFrame = 0;
        lastFrame = 0;
    }

    public boolean isPlaying() {
        return playing;
    }
}
