package parser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 10.04.2015.
 */
public class WorkInfo
{

        String name;//название
        int money;//ЗП
        Date date;//дата публикации
        String city;//Город
        String global;//описание
        String url;

        //надо бы описать все методы, но пока лень
        public void setName(String text)
        {
            name = text;
        }

        public void setCity(String text)
        {
            city = text;
        }

        public void setGlobal(String text)
        {
            global = text;
        }

        public void setMoney(String text)// переделать чтобы круто парсил зп
        {
            String toNumber = "";
            text = text.replace(" ", "");
            text = text.replace("\u00a0", "");
            char a[] = text.toCharArray();
            for (int i = 0; i < text.length(); i++)
                if (Character.isDigit(a[i]))
                {
                    toNumber += String.valueOf(a[i]);
                    if (!Character.isDigit(a[i + 1]))
                        break;
                }

            try
            {
                this.money = Integer.valueOf(toNumber);
            }
            catch (NumberFormatException e)
            {
                this.money = -1;
            }
        }

        public void SetDate(String text, int i) // переделать нужно дату из стринга конвертнуть в нормальынй формат и загнать в бд
        {
            if (i == 1)//на career дата представлена нормально
            {
                text = text.substring(0, 10);
                try
                {

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                    Date date = format.parse(text);
                    this.date = date;

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            } else//на hh пришлось извращаться с конфертацией ибо парсер не может получить нужные значения
            {
                String month;
                String day;
                if (text.length() == 5)//если дата одним числом
                {
                    month = text.substring(2, 5);
                    day = text.substring(0, 1);
                } else//если дата 2-мя
                {
                    month = text.substring(3, 6);
                    day = text.substring(0, 2);
                }
                Map<String, Integer> a = new HashMap<String, Integer>();//можно создать на весь год
                a.put("мар", 3);
                a.put("апр", 4);
                a.put("май", 5);
                if (a.containsKey(month))//если нашли нужный месяц
                    try
                    {

                        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                        Date date = format.parse(new StringBuilder().append(day).append(".").append(a.get(month)).append(".").append("2015").toString());//приводим к формату dd.MM.yyyy
                        this.date = date;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
            }
        }

        //для инициализации
        public void Initialize()
        {
            name = "";
            money = 0;
            date = new Date();
            city = "";
            global = "";
            url = "";
        }

    }

