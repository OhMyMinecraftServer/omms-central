package icu.takeneko.omms.central.announcement;

public class AnnouncementNotExistException extends RuntimeException {
    String id;

    public AnnouncementNotExistException(String id) {
        super(id);
        this.id = id;
    }

    public AnnouncementNotExistException(Throwable cause, String id) {
        super(id, cause);
        this.id = id;
    }
}
