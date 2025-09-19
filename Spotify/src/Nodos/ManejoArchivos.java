package Nodos;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class ManejoArchivos {

    private RandomAccessFile rsong;

    public ManejoArchivos() {
        try {
            File dir = new File("songs");
            if (!dir.exists()) {
                dir.mkdir();
            }
            
            rsong = new RandomAccessFile(new File(dir, "song.sng"), "rw");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addSong(SongData song) throws IOException {
        rsong.seek(rsong.length());
        rsong.writeInt(song.getCode());
        rsong.writeUTF(song.getNombreCancion());
        rsong.writeUTF(song.getArtista());
        rsong.writeDouble(song.getDuracion());
        rsong.writeUTF(song.getPath());
        rsong.writeUTF(song.getGeneroMusical());
        rsong.writeUTF(song.getImagePath());
        rsong.writeBoolean(false);
    }

    public boolean searchSong(int code) throws IOException {
        rsong.seek(0);
        while (rsong.getFilePointer() < rsong.length()) {
            long posInicio = rsong.getFilePointer();

            int codeI = rsong.readInt();
            String nombre = rsong.readUTF();
            String artista = rsong.readUTF();
            double dur = rsong.readDouble();
            String path = rsong.readUTF();
            String genero = rsong.readUTF();
            String img = rsong.readUTF();
            boolean eliminado = rsong.readBoolean();

            if (codeI == code && !eliminado) {
                rsong.seek(posInicio);
                return true;
            }
        }
        return false;
    }
    
    public boolean removeSong(int code) throws IOException {
        rsong.seek(0);
        while (rsong.getFilePointer() < rsong.length()) {
            int codeI = rsong.readInt();
            rsong.readUTF();
            rsong.readUTF();
            rsong.readDouble();
            rsong.readUTF();
            rsong.readUTF();
            rsong.readUTF();

            long posBool = rsong.getFilePointer();
            boolean eliminado = rsong.readBoolean();

            if (codeI == code && !eliminado) {
                rsong.seek(posBool);
                rsong.writeBoolean(true);
                return true;
            }
        }
        return false;
    }

    public ArrayList<SongData> loadAll() throws IOException {
        ArrayList<SongData> out = new ArrayList<>();
        rsong.seek(0);
        while (rsong.getFilePointer() < rsong.length()) {
            int code = rsong.readInt();
            String nombre = rsong.readUTF();
            String artista = rsong.readUTF();
            double duracion = rsong.readDouble();
            String path = rsong.readUTF();
            String genero = rsong.readUTF();
            String image = rsong.readUTF();
            boolean eliminado = rsong.readBoolean();

            if (!eliminado) {
                out.add(new SongData(code, nombre, artista, duracion, path, genero, image));
            }
        }
        return out;
    }
}