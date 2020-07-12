package sql.demo;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.*;



class TextInputFrame extends AbstractTableModel {
    private JFrame frame;
    String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    String DB_Driver = "org.postgresql.Driver";
    String username = "postgres";
    String password = "postgres";
    int x;
    private String TITLE_message;
    Double money = 100.0;
    private Object[][] array = new String[][]{{"Google", "7.5", "10", "0"},
            {"Microsoft", "12.5", "5", "0"},
            {"Apple", "6.8", "13", "0"},
            {"Sony", "10.0", "9", "i"}};
    private Object[][] array1 = new String[][]{{"Компания","Операция","Время","Деньги","Всего"}};
    // Заголовки столбцов
    private Object[] columnsHeader = new String[]{"Компания", "Цена", "Курс", "Всего"};
    private Object[] columnsHeader1 = new String[]{"Компания","Операция","Время","Деньги","Всего"};
    JLabel enter;
    JTextField tf;
    JTable Share;
    private DefaultTableModel model;
    JTable Oper;
    JTable CurrentMoney;
    JButton btBuy, btSell;
    int id = 0;
    private int name;

    TextInputFrame(String msg) {

        try(Connection db = DriverManager.getConnection(DB_URL, username, password)) {
            Class.forName(DB_Driver); //Проверяем наличие JDBC драйвера для работы с БД

            JFrame.setDefaultLookAndFeelDecorated(true);
            frame = new JFrame("Симулятор Биржи");
            JLabel label = new JLabel("");
            JPanel panel = new JPanel();

            panel.setLayout(new BorderLayout());

            btBuy = new JButton("Купить");
            btBuy.setBounds(220, 200, 100, 50);
            btBuy.setFocusPainted(false);

            btSell = new JButton("Продать");
            btSell.setBounds(320, 200, 100, 50);
            btSell.setFocusPainted(false);

            ShareTable(array, db);

            Vector<Vector<String>> data = new Vector<Vector<String>>();
            // Вектор с заголовками столбцов
            Vector<String> header = new Vector<String>();
            // Формирование в цикле массива данных
            TableInit(data,header);

            // Таблица на основе вектора
            Share = new JTable(data, header);


            Share.setForeground(Color.black);
            Share.setSelectionForeground(Color.white);
            Share.setSelectionBackground(Color.blue);
            Share.setRowHeight(35);
            // Скрытие сетки таблицы
            Share.setShowGrid(true);
            Share.setSize(500, 140);

            JScrollPane scrollPane1 = new JScrollPane(Share);

            CurrentMoney=new JTable(5,1);
            CurrentMoney.setForeground(Color.black);
            CurrentMoney.setSelectionBackground(Color.white);
            CurrentMoney.setRowHeight(45);
            CurrentMoney.setShowGrid(true);
            CurrentMoney.setBounds(5,163,70,225);
            CurrentMoney.setValueAt(money,0,0);

            model = new DefaultTableModel(){
                @Override
                public boolean isCellEditable(int row, int column){
                    return true;
                };
            };

            model.addColumn("Id");
            model.addColumn("Компания");
            model.addColumn("Операция");
            model.addColumn("Время");
            model.addColumn("Деньги");
            model.addColumn("Всего");


            Oper = new JTable(model);
            Oper.setForeground(Color.black);
            Oper.setRowHeight(20);
            Oper.setSelectionBackground(Color.white);
            Oper.setShowGrid(true);


            JScrollPane scrollPane = new JScrollPane(Oper);
            scrollPane.setBounds(75,300,515,265);

            TimerTask repeatedTask = new TimerTask() {
                public void run() {
                    RandomStock();
                }
            };
            Timer timer = new Timer("Timer");

            long delay = 1000L;
            long period = 1000L*10;
            timer.scheduleAtFixedRate(repeatedTask, delay, period);


            panel.add(scrollPane1,BorderLayout.NORTH);
            frame.add(CurrentMoney);
            frame.add(scrollPane);
            frame.add(btBuy);
            frame.add(btSell);
            frame.add(panel);
            frame.setSize(600,600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setResizable(false);

            RandomStock();

            btBuy.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    x=1;

                    Dialog(db);
                }
            });
            btSell.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    x=0;

                    Dialog(db);
                }
            });



            frame.setVisible(true);


        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // обработка ошибки  Class.forName
            System.out.println("JDBC драйвер для СУБД не найден!");
        } catch (SQLException e) {
            e.printStackTrace(); // обработка ошибок  DriverManager.getConnection
            System.out.println("Ошибка SQL !");
        }
    }


    public void TableInit(Vector<Vector<String>> data,Vector<String> header){
        for (int j = 0; j < array.length; j++) {
            header.add((String) columnsHeader[j]);
            Vector<String> row = new Vector<String>();
            for (int i = 0; i < array[j].length; i++) {
                row.add((String) array[j][i]);
            }
            data.add(row);
        }

    }



    public void Dialog (Connection db){
        JFrame Amount = new JFrame("Количество акций");
        Amount.setBounds(0,0,200,200);

        JTextField tf1 = new JTextField(1);
        tf1.setBounds(0, 0, 50, 100);

        JButton BtConfirm = new JButton("Подтвердить");
        BtConfirm.setBounds(10,40,25,40);
        BtConfirm.setFocusPainted(false);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(tf1,BorderLayout.CENTER);
        panel.add(BtConfirm,BorderLayout.SOUTH);

        Amount.add(panel);

        Amount.setVisible(true);
        Amount.setDefaultCloseOperation(Amount.DISPOSE_ON_CLOSE);
        BtConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int Stock  = Share.getSelectedRow();

                String Company = "";
                if(Stock==0)Company="Google";
                if(Stock==1)Company="Microsoft";
                if(Stock==2)Company="Apple";
                if(Stock==3)Company="Sony";
                Double Price = Double.parseDouble(array[Stock][1].toString());


                if (x ==1) {
                    name = Integer.parseInt(tf1.getText());
                    Double StockPrice = Price * name;
                    if (money >= StockPrice) {

                        try{
                            String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
                            String username = "postgres";
                            String password = "postgres";
                            Connection db1 = DriverManager.getConnection(DB_URL, username, password);
                            String sql = "insert into OperLogTable(id,Stock,type,operTime,money,shareoverall) values(?,?,'Buy',?,?,?)";
                            Date now = new Date();
                            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                            String s = formatter.format(now);


                            PreparedStatement preparedStatement = db1.prepareStatement(sql);
                            preparedStatement.setInt(1, id);
                            preparedStatement.setString(2, Company);
                            preparedStatement.setString(3, s);
                            preparedStatement.setDouble(4, StockPrice);
                            preparedStatement.setInt(5,name);
                            int rows = preparedStatement.executeUpdate();
                            id++;
                        } catch (SQLException ae) {
                            ae.printStackTrace(); // обработка ошибок  DriverManager.getConnection
                            System.out.println("Ошибка SQL !");
                        }


                        int NewAmountStock=Integer.parseInt(array[Stock][3].toString())+name;
                        array[Stock][3]=Integer.toString(NewAmountStock);

                        Share.setValueAt(array[Stock][3],Stock,3);
                        money -= StockPrice;
                        CurrentMoney.setValueAt(money,0,0);

                    }else JOptionPane.showMessageDialog(frame, "Недостаточно денег на счету");
                    Amount.setVisible(false);


                }
                else if (x ==0) {
                    //Добавить Функций, работу с деньгами,разобратья насчет времени, Таймер,ScrollPane,
                    name=Integer.parseInt(tf1.getText());
                    if(Integer.parseInt(array[Stock][3].toString())>=name){
                        Double StockPrice = Price*name;

                        try{
                            String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
                            String username = "postgres";
                            String password = "postgres";
                            Connection db1 = DriverManager.getConnection(DB_URL, username, password);
                            String sql = "insert into OperLogTable(id,Stock,type,operTime,money,shareoverall) values(?,?,'Sell',?,?,?)";
                            Date now = new Date();
                            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                            String s = formatter.format(now);


                            PreparedStatement preparedStatement = db1.prepareStatement(sql);
                            preparedStatement.setInt(1, id);
                            preparedStatement.setString(2, Company);
                            preparedStatement.setString(3, s);
                            preparedStatement.setDouble(4, StockPrice);
                            preparedStatement.setInt(5,name);
                            int rows = preparedStatement.executeUpdate();
                            id++;

                        } catch (SQLException ae) {
                            ae.printStackTrace(); // обработка ошибок  DriverManager.getConnection
                            System.out.println("Ошибка SQL !");
                        }

                        int NewAmountStock=Integer.parseInt(array[Stock][3].toString())-name;
                        array[Stock][3]=Integer.toString(NewAmountStock);

                        Share.setValueAt(array[Stock][3],Stock,3);
                        money += StockPrice;
                        CurrentMoney.setValueAt(money,0,0);
                    } else JOptionPane.showMessageDialog(frame, "Некорретное число акций");
                    Amount.setVisible(false);
                }

                try{
                    String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
                    String username = "postgres";
                    String password = "postgres";
                    Connection db1 = DriverManager.getConnection(DB_URL, username, password);
                    Statement statement = db1.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM OperLogTable ORDER BY id DESC LIMIT 1");
                    resultSet.next();
                    String id1 = resultSet.getString("id");
                    String stock1 = resultSet.getString("stock");
                    String type1 = resultSet.getString("type");
                    String opertime1 = resultSet.getString("opertime");
                    String money1 =resultSet.getString("money");
                    String shareoverall1= resultSet.getString("shareoverall");
                    String[] data = {id1, stock1, type1, opertime1, money1, shareoverall1};
                    model.addRow(data);

                } catch (SQLException ae) {
                    ae.printStackTrace(); // обработка ошибок  DriverManager.getConnection
                    System.out.println("Ошибка SQL !");
                }

                Share.clearSelection();
            }
        });



    }


    public int getRowCount() {
        return 0;
    }

    public int getColumnCount() {
        return 0;
    }

    public Object getValueAt(int row, int column) {
        return null;
    }

    public void ShareTable(Object[][] array, Connection db){
        try {
            Statement statement = db.createStatement();
            int i = 0;
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ShareTable  ");
            while(resultSet.next()){
                array[i][0]=resultSet.getString(2);
                array[i][1]=resultSet.getString(4);
                array[i][2]=resultSet.getString(3);
                array[i][3]="0";
                i+=1;
            }
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace(); // обработка ошибок  DriverManager.getConnection
            System.out.println("Ошибка SQL !");
        }
    }

    public void RandomStock(){
        try{
            String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
            String username = "postgres";
            String password = "postgres";
            Connection db1 = DriverManager.getConnection(DB_URL, username, password);
            int i =0;
            int[] MaxPerc= new int[4];
            Statement statement = db1.createStatement();
            double rand;
            double OldPrice;
            double NewPrice;
            double CurrentPrice;
            double course;
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ShareTable  ");
            while(resultSet.next()){

                MaxPerc[i]=resultSet.getInt(4);
                rand=Math.random()*(MaxPerc[i]*2+1)-MaxPerc[i];//починить
                OldPrice =Double.parseDouble(array[i][1].toString());
                NewPrice = Math.round(OldPrice*((rand/100)+1)*10);
                NewPrice/=10;
                course = Math.round(rand*10);
                course/=10;
                CurrentPrice=NewPrice*Integer.parseInt(array[i][3].toString());
                String NewPrice1=Double.toString(NewPrice);
                String course1=Double.toString(course)+"%";

                array[i][2]= course1;
                array[i][1]=NewPrice1;

                Share.setValueAt(array[i][1],i,1);
                Share.setValueAt(array[i][2],i,2);
                CurrentMoney.setValueAt(CurrentPrice,i+1,0);
                i+=1;
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace(); // обработка ошибок  DriverManager.getConnection
            System.out.println("Ошибка SQL !");
        }

    }

    static void show(String txt) {
        new TextInputFrame(txt);

    }




}




public class StockMarket{
    public static void main(String[] args) {
        try{
            String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
            String username = "postgres";
            String password = "postgres";
            Connection db1 = DriverManager.getConnection(DB_URL, username, password);
            Statement statement = db1.createStatement();
            int resultSet = statement.executeUpdate("TRUNCATE OperLogTable");

            db1.close();


        } catch (SQLException ae) {
            ae.printStackTrace(); // обработка ошибок  DriverManager.getConnection
            System.out.println("Ошибка SQL !");
        }
        TextInputFrame.show("");


    }
}
