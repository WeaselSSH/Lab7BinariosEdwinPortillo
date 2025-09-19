package Nodos;

public class SongData {

    private int code;
    private String nombreCancion;
    private String artista;
    private double duracion;
    private String path;
    private String generoMusical;
    private String imagePath;

    public SongData(int code, String nombreCancion, String artista,
            double duracion, String path, String generoMusical,
            String imagePath) {
        this.code = code;
        this.nombreCancion = nombreCancion;
        this.artista = artista;
        this.duracion = duracion;
        this.path = path;
        this.generoMusical = generoMusical;
        this.imagePath = imagePath;
    }

    public int getCode() {
        return code;
    }

    public String getNombreCancion() {
        return nombreCancion;
    }

    public String getArtista() {
        return artista;
    }

    public double getDuracion() {
        return duracion;
    }

    public String getPath() {
        return path;
    }

    public String getGeneroMusical() {
        return generoMusical;
    }

    public String getImagePath() {
        return imagePath;
    }
}
