package Nodos;

public class MusicNode {

    public SongData data;
    public MusicNode next;

    public MusicNode(SongData data) {
        this.data = data;
        this.next = null;
    }
}
