import javax.swing.*;

public class Stone {
    private boolean is_set = false;
    private boolean is_visible = false;
    private Cell cell;
    private int id;
    private String color; // 0 = X 1 = O
    private JLabel label = new JLabel();
    private Player player;

    public Stone(int id, String color, Player player) {
        this.id = id;
        this.color = color;
        this.player = player;
    }

    // METHODS //
    


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


    public boolean isIs_set() {
        return is_set;
    }

    public void setIs_set(boolean is_set) {
        this.is_set = is_set;
    }

    public boolean isIs_visible() {
        return is_visible;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
