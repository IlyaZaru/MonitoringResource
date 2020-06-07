import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Tabs implements Serializable {
    private JPanel publicPanel,panelBoxes,panelControl,panelButton,panelField;
    private JButton addBox,deleteCheckBox,deleteAll,addArchive,addAllArchive;
    private JLabel currentLabel;
    private JTextField currentField;
    private JFileChooser fileChooser;
    private ArrayList<Boxes> listBox = new ArrayList<Boxes>(); //список куда добавляются все объекты Boxes (добавленные панели Box)

    public Tabs (String nameTab){
        this.publicPanel = new JPanel();
        this.panelBoxes = new JPanel();
        this.panelControl = new JPanel();
        this.panelField = new JPanel();
        this.panelButton = new JPanel();
        this.addBox = new MultiLineButton().createTwoLineButton("Добавить", "позицию");
        this.deleteCheckBox = new MultiLineButton().createTwoLineButton("Удалить", "выбранные");
        this.deleteAll = new JButton("Удалить все");
        this.addArchive = new MultiLineButton().createThreeLineButton("Добавить", "выбранные", "в архив");
        this.addAllArchive = new MultiLineButton().createTwoLineButton("Добавить все", "в архив");
        this.currentLabel = new JLabel("Текущий пробег:");
        this.currentField = new JTextField(10);

        //преференс панелей
        publicPanel.setName(nameTab);
        publicPanel.setLayout(null);
        panelControl.setLayout(null);
        panelControl.setName("panelControl");
        panelField.setName("panelField");
        currentField.setName("currentField");
        panelBoxes.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        panelField.setLayout(new BoxLayout(panelField,BoxLayout.Y_AXIS));
        panelButton.setLayout(new GridLayout(5,1,0,12));
        publicPanel.setBorder(Design.BORDER_PANEL);
        panelBoxes.setBorder(Design.BORDER_PANEL);
        panelControl.setBounds(4, 4, 180, 500);
        panelBoxes.setBounds(190, 7, Interface.frame.getWidth()-217, Interface.frame.getHeight()-138);
        panelField.setBounds(5, 20, 172, 40);
        panelButton.setBounds(30, 70, 120, 312);
        //добавление слушателей в компоненты
        addBox.addActionListener(new AddBox());
        deleteCheckBox.addActionListener(new DeleteCheckBox());
        currentField.addKeyListener(new MainFieldKeyListener());
        deleteAll.addActionListener(new DeleteAll());
        addArchive.addActionListener(new AddChangeArchive());
        addAllArchive.addActionListener(new AddAllArchive());
        //добавление компонентов на панели
        panelField.add(currentLabel);
        panelField.add(currentField);
        panelButton.add(addBox);
        panelButton.add(deleteCheckBox);
        panelButton.add(deleteAll);
        panelButton.add(addArchive);
        panelButton.add(addAllArchive);
        panelControl.add(panelField);
        panelControl.add(panelButton);
        publicPanel.add(panelControl);
        publicPanel.add(panelBoxes);
    }
    public JPanel getPublicPanel(){
        return publicPanel;
    }
    public JPanel getPanelBoxes(){
        return panelBoxes;
    }
    public ArrayList<Boxes> getListBoxes(){
        return listBox;
    }
    public void setCurrentField(int dist){
        currentField.setText(Integer.toString(dist));
    }
    public int getCurrentDistance(){  //возращает значение поля текущего пробега, если пустое возвращает 0
        String text = currentField.getText();
        if (text.isEmpty()){return 0;}
        else {return Integer.parseInt(text);}
    }
    //далее вложенные классы обработки событий
    //класс обработки нажатия кнопки "Добавить позицию"
    private class AddBox implements ActionListener,Serializable{
        @Override
        //при нажатии появляется диалоговое окно с полем ввода названия Box
        public void actionPerformed(ActionEvent e) {
            String[] options = {"Добавить"};

            JPanel panel = new JPanel();
            JLabel label = new JLabel("Введите наименованиие работы: ");
            JTextField field = new JTextField(25);
            panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
            panel.add(label);
            panel.add(field);

            int selectedOption = JOptionPane.showOptionDialog(null, panel, "Добавление новой позиции", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
            if(selectedOption == 0)
            {
                String nameBox = field.getText();
                Boxes newBox = new Boxes(nameBox);
                Box box = newBox.getBox();
                panelBoxes.add(box);
                listBox.add(newBox);
                panelBoxes.revalidate();       //обнавляет отображение компонентов в контейнере
            }
        }
    }
    //класс обработки события нажатия кнопки "Удалить выбранные"
    private class DeleteCheckBox implements ActionListener,Serializable{
        //событие кнопки удаления
        @Override
        public void actionPerformed(ActionEvent e) {  //удаляет выбранные боксы
            for (int i=0;i<listBox.size();i++){
                if (listBox.get(i).getIsSelectedCheckBox()){
                    listBox.get(i).getBox().setVisible(false); //делаем бокс невидимым
                    panelBoxes.remove(listBox.get(i).getBox()); //удаляем с panelBoxes
                    listBox.remove(i);            //удаляем из списка боксов
                    i--;
                }
            }
        }
    }
    //класс обработки события нажатия кнопки "Удалить все"
    private class DeleteAll implements ActionListener,Serializable{
        @Override
        public void actionPerformed(ActionEvent arg0) {
            for (Boxes box:listBox){
                box.getBox().setVisible(false); //делаем бокс невидимым
                panelBoxes.remove(box.getBox());//удаляем с panelBoxes
            }
            listBox.clear();          //удаляем из списка боксов
            System.out.println(listBox);
        }
    }
    //класс обработки события ввода в поле "Текущий пробег"
    private class MainFieldKeyListener extends KeyAdapter implements Serializable{

        public void keyReleased(KeyEvent event){
            if (event.getKeyCode() != KeyEvent.VK_BACK_SPACE){ //если нажимается кнопка Backspace код снизу не выполняется
                String line = currentField.getText();
                try{
                    int dist = Integer.parseInt(line);
                    for (Boxes box:listBox){
                        int resource = box.getValueFieldResource();
                        int lastReplace = box.getValueFieldLastReplace();
                        box.setValueFieldLeft(lastReplace+resource-dist);}
                }
                catch (NumberFormatException exc){
                    JOptionPane.showMessageDialog(null, "Введите в поле \"Текущий пробег\" число! Либо число больше 2147483647");
                }
            }
        }
    }
    //класс обработки события нажатия кнопки "Добавить выбранные в архив"
    private class AddChangeArchive implements ActionListener, Serializable {
        @Override
        public void actionPerformed(ActionEvent evenet) {
            String[] options = {"Сохранить"};

            JPanel panel = new JPanel();
            JLabel l = new JLabel("Введите дату : ");
            JTextField dataField = new JTextField(2);
            panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
            panel.add(l); panel.add(dataField);

            Date nowDate = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            dataField.setText(format.format(nowDate));

            int selectedOption = JOptionPane.showOptionDialog(null, panel, "Добавление в архив выбранных", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
            if (selectedOption==0){
                //меняем название кнпок FileChooser на свои
                UIManager.put("FileChooser.saveButtonText", "Сохранить");
                UIManager.put("FileChooser.cancelButtonText", "Отмена");
                UIManager.put("FileChooser.fileNameLabelText", "Наименование файла");
                UIManager.put("FileChooser.filesOfTypeLabelText", "Типы файлов");
                UIManager.put("FileChooser.lookInLabelText", "Директория");
                UIManager.put("FileChooser.saveInLabelText", "Сохранить в директории");
                UIManager.put("FileChooser.folderNameLabelText", "Путь директории");
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Файл Блокнот (.txt)", "txt"); //создаем фильтр .txt файлов
                fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());  //удаляем все фильтры в fileChooser
                fileChooser.setFileFilter(filter);                                 //устанавливаем созданный фильтр в файлЧоосер
                int saveWindow = fileChooser.showSaveDialog(null);  //создаем окно сохранения файла
                File file = null;
                if (saveWindow == JFileChooser.APPROVE_OPTION){  //если выбор файла прошел успешно и нажата кнопка СОХРАНИТЬ то выполняется код ниже
                    String nameFile = fileChooser.getSelectedFile().getName();  //передается имя выделенного файла
                    if (!nameFile.contains(".txt")){ //вариант для создания нового файла - имя вводится в поле "FileChooser.fileNameLabelText", "Наименование файла"
                        file = new File(fileChooser.getCurrentDirectory()+"\\"+nameFile+".txt"); //формируется имя файла - имя директории+\+имя файла+.txt
                    }
                    else {file = fileChooser.getSelectedFile();} //вариант для выбранного файла

                    try {
                        PrintStream writer = new PrintStream (new FileOutputStream(file,true));
                        for (Boxes box:listBox){
                            if (box.getIsSelectedCheckBox()){    //если у бокса галочка на ЧекБоксе то формируем строку из значений полей и записываем в файл
                                String data = dataField.getText();
                                String nameTab = publicPanel.getName()+":";
                                String nameBox = box.getNameBox();
                                String fieldRes = box.getTextFieldRes();
                                String fieldLast = box.getTextFieldLast();
                                String fieldLeft = "("+box.getTextFieldLeft()+")";
                                String currentDistance = currentField.getText();
                                String lineSave = String.format("%-11s  %-15s %-40s Текущий пробег-%-12s Ресурс-%-12s Пробег последней замены-%-12s Осталось до замены-%-12s",data,nameTab,nameBox,currentDistance,fieldRes,fieldLast,fieldLeft);
                                writer.println(lineSave);
                            }
                        }
                        writer.close();
                        JOptionPane.showMessageDialog(null, "Данные в файл добавлены"); //диалоговое окно о успешной записи данных в файл
                    } catch (IOException e) {
                    }
                }

            }
        }

    }
    //класс обработки события нажатия кнопки "Добавить все в архив"
    private class AddAllArchive implements ActionListener, Serializable {

        @Override
        public void actionPerformed(ActionEvent evenet) {
            String[] options = {"Сохранить"};

            JPanel panel = new JPanel();
            JLabel l = new JLabel("Введите дату : ");
            JTextField dataField = new JTextField(2);
            panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
            panel.add(l); panel.add(dataField);

            Date nowDate = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            dataField.setText(format.format(nowDate));
            //меняем название кнпок FileChooser на свои
            UIManager.put("FileChooser.saveButtonText", "Сохранить");
            UIManager.put("FileChooser.cancelButtonText", "Отмена");
            UIManager.put("FileChooser.fileNameLabelText", "Наименование файла");
            UIManager.put("FileChooser.filesOfTypeLabelText", "Типы файлов");
            UIManager.put("FileChooser.lookInLabelText", "Директория");
            UIManager.put("FileChooser.saveInLabelText", "Сохранить в директории");
            UIManager.put("FileChooser.folderNameLabelText", "Путь директории");
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Файл Блокнот (.txt)", "txt"); //создаем фильтр .txt файлов
            fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());  //удаляем все фильтры в fileChooser
            fileChooser.setFileFilter(filter);                                 //устанавливаем созданный фильтр в файлЧоосер
            int selectedOption = JOptionPane.showOptionDialog(null, panel, "Добавление в архив выбранных", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
            if (selectedOption==0){

                int saveWindow = fileChooser.showSaveDialog(null);
                File file = null;
                if (saveWindow == JFileChooser.APPROVE_OPTION){
                    String nameFile = fileChooser.getSelectedFile().getName();
                    if (!nameFile.contains(".txt")){
                        file = new File(fileChooser.getCurrentDirectory()+"\\"+nameFile+".txt");
                    }
                    else {file = fileChooser.getSelectedFile();}

                    try {
                        PrintStream writer = new PrintStream (new FileOutputStream(file,true));
                        for (Boxes box:listBox){
                            String data = dataField.getText();
                            String nameTab = publicPanel.getName()+":";
                            String nameBox = box.getNameBox();
                            String fieldRes = box.getTextFieldRes();
                            String fieldLast = box.getTextFieldLast();
                            String fieldLeft = "("+box.getTextFieldLeft()+")";
                            String currentDistance = currentField.getText();
                            String lineSave = String.format("%-11s  %-15s %-40s Текущий пробег-%-12s Ресурс-%-12s Пробег последней замены-%-12s Осталось до замены-%-12s",data,nameTab,nameBox,currentDistance,fieldRes,fieldLast,fieldLeft);
                            writer.println(lineSave);
                        }
                        writer.close();
                        JOptionPane.showMessageDialog(null, "Данные в файл добавлены");
                    } catch (IOException e) {
                    }
                }

            }
        }

    }
}
