import javax.swing.*;
import java.util.Arrays;

public class Cell {
    private int id;
    private boolean is_empty = true;
    private Stone stone;
    private int[] neighbors;
    private Muehle mill;
    private String[] lines; // lines of the rects

    private JLabel label = new JLabel();

    public Cell(int id) {
        this.id = id;
    }

    public Cell(int id, Muehle mill) {
        this.id = id;
        this.mill = mill;
    }

    // METHODS //

    // GETTER and SETTER //

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setLabel(JLabel label) {
        this.label = label;
    }

    public int[] getNeighbors() { // per lenth und index kann zugegriffen werden
        return this.neighbors;
    }

    public void printNeighbors() { // for debugging to print neighbors
        System.out.print("[");
        for (int i = 0; i < this.neighbors.length; i++) {
            if (i == this.neighbors.length - 1) {
                System.out.print(this.neighbors[i]);
            } else {
                System.out.print(this.neighbors[i] + ", ");
            }
        }
        System.out.print("]\n");
    }

    public void setNeighbors(int[] neighbors) {
        this.neighbors = neighbors;
    }

    public Muehle getMill() {
        return mill;
    }

    public void setMill(Muehle mill) {
        this.mill = mill;
    }

    public String[] getLines() {
        return lines;
    }

    public void setLines(String[] lines) {
        this.lines = lines;
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
