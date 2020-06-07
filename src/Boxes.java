import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class Boxes implements Serializable {
    private String nameBox;
    private Box box;
    private JCheckBox checkBox;
    private JTextField fieldRes, fieldLast, fieldLeft ;
    private ArrayList<Tabs> listTabs = Interface.listTabs;

    public Boxes(String nameBox){
        this.fieldRes = new JTextField(10);
        this.fieldLast = new JTextField(10);
        this.fieldLeft  = new JTextField(10);
        this.checkBox = new JCheckBox();
        this.nameBox=nameBox;
        this.box = Box.createHorizontalBox();
        //задаем оформление границ и заголовка для box
        TitledBorder title=new TitledBorder(BorderFactory.createLineBorder(Color.black, 1, true),nameBox,1,0);
        title.setTitleFont(Design.FONT_NAME_BOX);
        title.setTitleColor(Color.BLUE);
        //задаем созданное выше оформление границ box
        box.setBorder(title);
        box.setName(nameBox);
        //создаем компоненты для добавления на box
        JLabel resource = new JLabel("Пробег по ресурсу: ");
        JLabel lastRefresh = new JLabel(" Пробег последней замены: ");
        JLabel left = new JLabel(" Осталось до замены: ");
        resource.setFont(Design.FONT_LABEL_BOX);
        lastRefresh.setFont(Design.FONT_LABEL_BOX);
        left.setFont(Design.FONT_LABEL_BOX);
        fieldLeft.setEditable(false);    //активирует\деактивирует текстовое поле ввода
        box.setSize(800, 20);
        //подключаем слушателей к полям ввода
        fieldRes.addKeyListener(new FieldResourceKeyListener());
        fieldLast.addKeyListener(new FieldLastReplaceKeyListener());
        //добавляем компоненты на box
        box.add(resource);
        box.add(fieldRes);
        box.add(lastRefresh);
        box.add(fieldLast);
        box.add(left);
        box.add(fieldLeft);
        box.add(checkBox);
    }
    public Box getBox(){
        return box; }
    public String getNameBox(){
        return nameBox;
    }
    public String getTextFieldRes(){
        return fieldRes.getText();
    }
    public String getTextFieldLast(){
        return fieldLast.getText();
    }
    public String getTextFieldLeft(){
        return fieldLeft.getText();
    }
    public boolean getIsSelectedCheckBox(){         //возвращает состояние (поставлен флажок или нет)
        boolean isSelected = checkBox.isSelected();
        return isSelected;
    }
    public int getValueFieldResource(){     //возвращает значение поля "пробег по ресурсу" , если поле пустое возвращает 0
        String line = fieldRes.getText();
        if (line.isEmpty()){return 0;}
        else return Integer.parseInt(line);
    }
    public int getValueFieldLastReplace(){   //возвращает значение поля "пробег последней замены" , если поле пустое возвращает 0
        String line = fieldLast.getText();
        if (line.isEmpty()){return 0;}
        else return Integer.parseInt(line);
    }
    public void setValueFieldLeft(int value){                // устанавливает значение поля "Осталось до замены", меняет цвет: желтый - менее 30% от ресурса пробега, красный - менее 10%
        double resource = this.getValueFieldResource();
        if (resource*0.3>value&&resource*0.1<=value){fieldLeft.setBackground(Color.yellow);}
        else if (resource*0.1>value){fieldLeft.setBackground(Color.red);}
        else {fieldLeft.setBackground(null);}
        String text = Integer.toString(value);
        fieldLeft.setText(text);
    }
    //далее вложенные классы обработки событий

    //класс обработки события нажатия на клавиши клавиатуры (ввод) в текстовом поле "пробег по ресурсу"
    private final class FieldResourceKeyListener extends KeyAdapter implements Serializable{
        public void keyReleased(KeyEvent event){
            if (event.getKeyCode() != KeyEvent.VK_BACK_SPACE){ //если нажимается кнопка Backspace код снизу не выполняется
                try {
                    JPanel publicPanel = (JPanel) box.getParent().getParent(); //получаем общую панель publicPanel данной вкладки(box на panelBoxes а та на publicPanel)
                    for (Tabs tab:listTabs){   //
                        if ((publicPanel.getName()).equals(tab.getPublicPanel().getName())){
                            int dist = tab.getCurrentDistance();
                            for(Boxes box:tab.getListBoxes()){          //реализует изменение последнего "осталось до замены" поля всех боксов
                                int value = box.getValueFieldLastReplace()+box.getValueFieldResource()-dist;
                                box.setValueFieldLeft(value);
                            }
                            break;}
                    }
                }
                catch (NumberFormatException exc){
                    JOptionPane.showMessageDialog(null, "Введите в поле \"Пробег по ресурсу\" число!");
                }
            }
        }
    }
    //класс обработки события нажатия на клавиши клавиатуры (ввод) в текстовом поле "пробег последней замены"
    //алгорит аналогичен алгоритму из FieldResourceKeyListener, но добавлено условие - если значение текущего пробега меньше значения пробега посл. замены, то в текущий пробег устанавливается знач пробега посл. замены
    private final class FieldLastReplaceKeyListener extends KeyAdapter implements Serializable{
        public void keyReleased(KeyEvent event){
            if (event.getKeyCode() != KeyEvent.VK_BACK_SPACE){ //если нажимается кнопка Backspace код снизу не выполняется
                String line = fieldLast.getText();
                try{
                    int lastReplace = Integer.parseInt(line);
                    JPanel publicPanel = (JPanel) box.getParent().getParent(); //получаем общую панель данной вкладки(box на panelBoxes а та на publicPanel)
                    for (Tabs tab:listTabs){
                        if ((publicPanel.getName()).equals(tab.getPublicPanel().getName())){
                            int dist = tab.getCurrentDistance();
                            if (lastReplace>dist){
                                dist=lastReplace;
                                tab.setCurrentField(dist);}
                            for(Boxes box:tab.getListBoxes()){                            //реализует изменение последнего поля всех боксов
                                int value = box.getValueFieldLastReplace()+box.getValueFieldResource()-dist;
                                box.setValueFieldLeft(value);
                            }
                            break;}
                    }
                }
                catch (NumberFormatException exc){
                    JOptionPane.showMessageDialog(null, "Введите в поле \"Пробег последней замены\"");
                }
            }
        }
    }
}
