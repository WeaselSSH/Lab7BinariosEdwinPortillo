package Nodos;

public class MusicNodeList {

    private MusicNode inicio;
    private MusicNode finalLista;
    private int size;

    public MusicNodeList() {
        inicio = null;
        finalLista = null;
        size = 0;
    }

    public void add(SongData song) {
        MusicNode node = new MusicNode(song);
        if (inicio == null) {
            inicio = finalLista = node;
        } else {
            finalLista.next = node;
            finalLista = node;
        }
        size++;
    }

    public boolean remove(int code) {
        MusicNode previo = null;
        MusicNode actual = inicio;

        while (actual != null) {
            if (actual.data.getCode() == code) {
                if (previo == null) {
                    inicio = actual.next;
                } else {
                    previo.next = actual.next;
                }
                if (actual == finalLista) {
                    finalLista = previo;
                }
                size--;
                return true;
            }
            previo = actual;
            actual = actual.next;
        }
        return false;
    }

    public SongData search(int code) {
        MusicNode actual = inicio;
        while (actual != null) {
            if (actual.data.getCode() == code) {
                return actual.data;
            }
            actual = actual.next;
        }
        return null;
    }

    public void print() {
        MusicNode actual = inicio;
        while (actual != null) {
            System.out.println("[" + actual.data.getCode() + "] "
                    + actual.data.getNombreCancion() + " - "
                    + actual.data.getArtista() + " (" + actual.data.getGeneroMusical() + ")");
            actual = actual.next;
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return inicio == null;
    }
}
