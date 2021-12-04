package Fann.fannmusic;

public class Song {
    private String name="";//歌名
    private String author="";//作者
    private String id="";//歌id
    private String duration="";//歌时长

    public Song(String name, String author, String id, String duration) {
        this.name = name;
        this.author = author;
        this.id = id;
        this.duration = duration;
    }

    public Song() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
