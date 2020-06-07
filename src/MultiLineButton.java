import javax.swing.*;
import java.awt.*;

public class MultiLineButton extends JButton {
    public JButton createTwoLineButton(String line1, String line2){   // метод возвращающий созданую кнопку с названием в 2 ряда
        JButton button = new JButton();
        button.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        JLabel l1 = new JLabel(line1);
        JLabel l2 = new JLabel(line2);
        button.add(l1);
        button.add(l2);
        return button;
    }
    public JButton createThreeLineButton(String line1, String line2, String line3){  // метод возвращающий созданую кнопку с названием в 3 ряда
        JButton button = new JButton();
        button.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        JLabel l1 = new JLabel(line1);
        JLabel l2 = new JLabel(line2);
        JLabel l3 = new JLabel(line3);
        button.add(l1);
        button.add(l2);
        button.add(l3);
        return button;
    }
}
