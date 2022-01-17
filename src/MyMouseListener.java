import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MyMouseListener implements MouseListener {
    private JLabel label;
    private Cell cell;

    public MyMouseListener(JLabel label, Cell cell){
        this.label = label;
        this.cell = cell;
    }
    public MyMouseListener(JLabel label){
        this.label = label;
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // red circle //
        Color c = new Color(255,255,255);
        label.setBackground(c);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // red circle //
        Color c = new Color(0,0,0);
        label.setBackground(c);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (cell != null) System.out.println(cell.getId());
        else System.out.println(label.getText());
    }
}
