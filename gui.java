package parser;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class gui extends JDialog
{
    static boolean sqlIsConnected = false;
    private JPanel contentPane;
    private JButton buttonCancel;
    private JTextField moneyMin;
    private JTextField moneyMax;
    private JButton execBtnMoney;
    private JTextField VacancyName;
    private JButton execBtnName;

    public gui()
    {
        setContentPane(contentPane);
        setModal(true);


        buttonCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });

        //Кнопка поиска по зп
        contentPane.registerKeyboardAction(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        execBtnMoney.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                searchByMoney(moneyMin.getText(), moneyMax.getText());
            }
        });
        execBtnName.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                searchByName(VacancyName.getText());
            }
        });
    }

    public static void main(String[] args)
    {
        gui dialog = new gui();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public static Connection connectSQL()//метод подключение к базе данный
    {
        // обьявление обьектов, пример из документации MS
        Connection con = null;
        try
        {
            // Establish the connection.
            SQLServerDataSource ds = new SQLServerDataSource();
            ds.setServerName("RIPHEYSRV\\BASECOMP");
            ds.setPortNumber(47634);
            ds.setDatabaseName("pars");
            ds.setPassword("1");
            ds.setUser("tandemservice задание/parser");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = ds.getConnection();
            if (!con.isClosed())
                sqlIsConnected = true;
        }
        // Handle any errors that may have occurred.
        catch (Exception e)
        {
            e.printStackTrace();
            sqlIsConnected = false;
            return con = null;
        }
        return con;
    }

    public static void dialog(final String page) throws Throwable //диалог со списком ссылок на вакансии
    {
        // for copying style
        JLabel label = new JLabel("Результаты");
        Font font = label.getFont();
        // create some css from the label's font
        StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
        style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
        style.append("font-size:" + font.getSize() + "pt;");
        // html content
        JEditorPane ep = new JEditorPane();
        ep.setContentType("text/html");
        ep.setText("<html><body style=\"" + style + "\">" + page);

        JScrollPane scrollPane = new JScrollPane(ep);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        ep.addHyperlinkListener(new HyperlinkListener()
        {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent hle)
            {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType()))
                {
                    System.out.println(hle.getURL());
                    Desktop desktop = Desktop.getDesktop();
                    try
                    {
                        desktop.browse(hle.getURL().toURI());
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        });
        ep.setEditable(false);
        ep.setBackground(label.getBackground());
        // show
        JOptionPane.showMessageDialog(null, scrollPane);
    }

    public static String genHtml(ResultSet resultSql)
    {//для диалогового окна сгенерируем простую страницу с ссылками
        String htmlPage = "";
        htmlPage = "\n<table>\n";
        try
        {
            while (resultSql.next())
            {//строки
                htmlPage = htmlPage + "<tr>\n";//1..n
                htmlPage += "<td><a href=\"" + resultSql.getString("url") + "\">" + resultSql.getString("name") + " " + resultSql.getInt("money") + "</a></td>";
                htmlPage = htmlPage + "</tr>\n";//1..n
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        htmlPage = htmlPage + "</table>\n" +
                " </body>\n" +
                "</html>";
        return htmlPage;
    }

    private void onCancel()
    {
// add your code here if necessary
        dispose();
    }

    public void searchByMoney(String min, String max)//метод для поиска по размеру ЗП привязан к кнопке
    {
        Connection con = null;
        con = connectSQL();
        int mn, mx;
        if (sqlIsConnected)
        {

            try
            {
                Statement stmt = con.createStatement();
                String query = "select name,url,money from vacancy where money BETWEEN " + min + " AND " + max;
                ResultSet rs = stmt.executeQuery(query);
                dialog(genHtml(rs));
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }

        }
        try
        {
            con.close();
            sqlIsConnected = false;
        }
        catch (Exception e)
        {
        }
    }

    public void searchByName(String name)
    {
        Connection con = null;
        con = connectSQL();
        if (sqlIsConnected)
        {

            try
            {
                Statement stmt = con.createStatement();
                String query = "select name,url,money from vacancy where name LIKE '%" + name + "%'";
                ResultSet rs = stmt.executeQuery(query);
                dialog(genHtml(rs));
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }

        }
        try
        {
            con.close();
            sqlIsConnected = false;
        }
        catch (Exception e)
        {
        }
    }


}



