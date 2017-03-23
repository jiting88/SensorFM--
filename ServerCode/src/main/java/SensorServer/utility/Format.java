package SensorServer.utility;

/**
 * Created by jicl on 16/6/30.
 */
public class Format {
    private final long id;
    private final String content;

    public Format(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

}
