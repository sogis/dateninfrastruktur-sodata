package ch.so.agi.datenportal.search;


public record PageRequest(int page, int size, boolean paged) {

    private static final PageRequest UNPAGED = new PageRequest(1, 0, false);

    public PageRequest {
        if (!paged) {
            page = 1;
            size = 0;
        }
    }

    public static PageRequest unpaged() {
        return UNPAGED;
    }

    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size, true);
    }

    public int offset() {
        if (!paged) {
            return 0;
        }
        return Math.max(0, page - 1) * Math.max(0, size);
    }

}
