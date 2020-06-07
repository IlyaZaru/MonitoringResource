import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class Interface {
    static JFrame frame;
    JPanel south, panelTopMenuBar;
    JMenuBar menuBar;
    JMenu file;
    JMenuItem addDevice, exit;
    JTabbedPane tab;
    JButton deleteTab;
    File fileSave = new File("save.txt"); //имя файла для сериализации
    static ArrayList<Tabs> listTabs = new ArrayList<Tabs>(); //списокб куда добавляются все объекты Tabs (вкладки)

    public Interface() throws IOException {
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Interface inter = new Interface();
        inter.go();
        try{
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(inter.fileSave));
            int size = input.readInt();  //считываем из файла сериализации количество объектов которое было в listTabs
            for (int i=0;i<size;i++){    //читаем каждый объект Tabs и добавляем
                Tabs tab = (Tabs) input.readObject();
                tab.getPanelBoxes().removeAll();             //получаем панель боксов и удаляем с нее все компоненты
                for (Boxes box:tab.getListBoxes()){          //из списка listBoxes текущего объекта Tabs добавляем все боксы на панель panelBoxes
                    tab.getPanelBoxes().add(box.getBox());
                }
                inter.tab.add(tab.getPublicPanel());
                inter.listTabs.add(tab);
            }
            input.close();}
        catch (FileNotFoundException e1){}
    }

    public void go(){
        //инициализация полей
        this.frame = new JFrame("Monitoring Resources");
        this.south = new JPanel();
        this.menuBar = new JMenuBar();
        this.panelTopMenuBar = new JPanel();
        this.tab = new JTabbedPane();
        this.deleteTab = new JButton("Удалить текущую вкладку");
        //добавление кнопок в менюБар
        menuBar.add(file = new JMenu("Файл"));
        file.add(addDevice = new JMenuItem("Добавить технику"));
        file.add(exit = new JMenuItem("Выход"));
        //добавление компонентов на панели и форму
        panelTopMenuBar.add(menuBar);
        south.add(deleteTab);
        frame.add(BorderLayout.NORTH, menuBar);
        frame.add(BorderLayout.CENTER, tab);
        frame.add(BorderLayout.SOUTH, south);
        //добавление слушателей к компонентам
        addDevice.addActionListener(new AddDevice());
        deleteTab.addActionListener(new DeleteTab());
        frame.addComponentListener(new ChangeSizeFrame());
        frame.addWindowListener(new CloseFrame());
        exit.addActionListener(new Exit());
        //установки
        frame.setBounds(400,100,1000,700);
        frame.setLocationRelativeTo(null); // при запуске отображает окно по центру экрана
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  //при нажатии крестика (закрытие окна) - ничего не происходит
    }
    //класс обработки события нажатия кнопки менюБара "Добавить Технику"
    private final class AddDevice implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {   //при нажатии вызывается диалоговое окно с полем ввода и кнопкой ДОБАВИТЬ
            String[] options = {"Добавить"};
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
            JLabel label = new JLabel("Введите наименованиие техники: ");
            JTextField txt = new JTextField(20);
            panel.add(label);
            panel.add(txt);
            int selectedOption = JOptionPane.showOptionDialog(null, panel, "Добавление", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);

            if(selectedOption == 0) //если кнопка нажата создается объект класса Tabs, возвращается панель publicPanel и добавляется на tab в виде вкладки
            {
                String text = txt.getText();
                Tabs t = new Tabs(text);
                listTabs.add(t);
                tab.add(t.getPublicPanel());
                tab.revalidate();     //обновляется отображение tab
            }
        }
    }
    //класс обработки события нажатия кнопки "Удалить текущую вкладку"
    private final class DeleteTab implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            int index = tab.getSelectedIndex(); //в массиве из компонентов контейнера tab получаем индекс текущей открытой вкладки
            Component[] arr = tab.getComponents(); // получаем массив компонентов на tab
            for (int i=0;i<listTabs.size();i++){
                if ((arr[index].getName()).equals(listTabs.get(i).getPublicPanel().getName())){ //если совпадает имя текущей вкладки с именем вкладки из списка listTabs то удаляем из списка
                    listTabs.remove(i);
                    i--;
                }
            }
            tab.remove(index); //удаляем с tab контейнера текущую вкладку по индексу
        }
    }
    //класс обработки события при закрытии окна приложения - сериализация объектов
    private final class CloseFrame extends WindowAdapter{
        public void windowClosing(WindowEvent event){ //метод отработки при закрытии окна
            try {
                FileOutputStream writer = new FileOutputStream(fileSave);
                String s = "";
                writer.write(s.getBytes());
                writer.close();
            } catch (FileNotFoundException e1) {
            } catch (IOException e) {
            }
            try {       //сериализация объектов из списка listTabs
                ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(fileSave,true));
                output.writeInt(listTabs.size());
                for (Tabs t:listTabs){
                    output.writeObject(t);
                }
                output.close();
            } catch (IOException e) {}
            System.exit(0);   //закрываем окно приложения
        }
    }
    //класс обработки события при изменении размеров окна
    private final class ChangeSizeFrame extends ComponentAdapter{
        public void componentResized(ComponentEvent evt){         //метод изменения размеров frame - изменяет размеры всех panelBoxes
            int width = frame.getWidth();
            int height = frame.getHeight();
            for (Tabs tab:listTabs){
                JPanel panelBoxes = tab.getPanelBoxes();
                panelBoxes.setSize(width-217, height-138);
            }
        }
    }
    //класс обработки события нажатия кнопки менюБара "Выход" - алгоритм идентичен из class CloseFrame
    private final class Exit implements ActionListener{
        public void actionPerformed(ActionEvent arg0) {
            try {
                FileOutputStream writer = new FileOutputStream(fileSave);
                String s = "";
                writer.write(s.getBytes());
                writer.close();
            } catch (FileNotFoundException e1) {
            } catch (IOException e) {
            }
            try {       //сериализация объектов из списка listTabs
                ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(fileSave,true));
                output.writeInt(listTabs.size());
                for (Tabs t:listTabs){
                    output.writeObject(t);
                }
                output.close();
            } catch (IOException e) {}
            System.exit(0);   //закрываем окно приложения
        }
    }
}

