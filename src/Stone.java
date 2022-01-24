

public class Stone {
    private boolean is_set = false;
    private boolean is_visible = false;
    private Cell cell;
    private final int id;
    private final String color; // 0 = X 1 = O

    public Stone(int id, String color) {
        this.id = id;
        this.color = color;
    }

    // MAGIC METHODS //

    @Override
    public String toString() {
        return "Stone{" +
                "id=" + id +
                ", is_set=" + is_set +
                ", is_visible=" + is_visible +
                '}';
    }

    // GETTER and SETTER //

    public void setIs_set(boolean is_set) {
        this.is_set = is_set;
    }

    public void setIs_visible(boolean is_visible) {
        this.is_visible = is_visible;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public String getColor() {
        return color;
    }
}
