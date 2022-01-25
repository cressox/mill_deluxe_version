import javax.swing.*;
import java.util.Arrays;

public class Cell {
    private final int id;
    private boolean is_empty = true;
    private Stone stone;
    private int[] neighbors;
    private String[] lines; // lines of the rects
    private String color_of_player;

    private final JLabel label = new JLabel("", SwingConstants.CENTER);

    public Cell(int id) {
        this.id = id;
    }

    public Cell(int id, String color_of_player) {
        this.id = id;
        this.color_of_player = color_of_player;
    }

    // METHODS //

    // GETTER and SETTER //

    public int getId() {
        return id;
    }

    public boolean isIs_empty() {
        return is_empty;
    }

    public void setIs_empty(boolean is_empty) {
        this.is_empty = is_empty;
    }

    public Stone getStone() {
        return stone;
    }

    public void setStone(Stone stone) {
        this.stone = stone;
    }

    public JLabel getLabel() {
        return label;
    }

    public int[] getNeighbors() {
        return this.neighbors;
    }

    public void setNeighbors(int[] neighbors) {
        this.neighbors = neighbors;
    }

    public String[] getLines() {
        return lines;
    }

    public void setLines(String[] lines) {
        this.lines = lines;
    }

    public String getColor_of_player() {
        return color_of_player;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "id=" + id +
                ", is_empty=" + is_empty +
                ", stone=" + stone +
                ", lines=" + Arrays.toString(lines) +
                '}';
    }


}
