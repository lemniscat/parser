package parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;



public class Parser

{
    //получаем данные по вакансии из ссылок с главной страницы
    public  static WorkInfo GetVacancyInfo(String url)
    {
        Document vacancyPage; //буфер для загрузки всей страницы
        WorkInfo temp = new WorkInfo(); //временный список по данным вакансии
        temp.Initialize();
        try
        {
            vacancyPage = Jsoup.connect(url).get().normalise();//получаем страницу

        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;  //если проблем то вернем
        }

        //собираем список
        try
        {
            if (url.indexOf("career.ru") == -1)
            {//если редирект на hh.ru
                temp.setName(vacancyPage.select(".vacancy__name").text());
                temp.setMoney(vacancyPage.select(".vacancy__salary").text());
                temp.SetDate(vacancyPage.select("span[data-qa=\"vacancy-date\"]").text(), 0);
                temp.setCity(vacancyPage.select("span[data-qa=\"vacancy-region\"]").text());
                temp.setGlobal(vacancyPage.select(".vacancy__description").text());
            } else
            {// если если редирект на career.ru
                temp.setName(vacancyPage.select(".b-vacancy-title").text());
                temp.setMoney(vacancyPage.select(".b-v-info-content").text());
                temp.SetDate(vacancyPage.select(".vacancy-sidebar__publication-date").attr("datetime"), 1);
                temp.setCity(vacancyPage.select(".l-content-colum-2.b-v-info-content").text());
                temp.setGlobal(vacancyPage.select(".l-content-colum-1").text());
            }
            temp.url = url;

        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            return temp;//если проблем то вернем пустой справочник
        }
        return temp;
    }

    //описываем всю логику получения ссылок
    public  static ArrayList<String> GetUrl(Elements link)
    {
        ArrayList<String> urls = new ArrayList<String>();
        for (Element a : link)
        {
            urls.add("http://ekaterinburg.hh.ru" + a.attr("href"));
            //urls.add(a.child(3).text());
        }
        return urls;
    }

    public static ArrayList<String> GetDate(Elements link)
    {
        ArrayList<String> dates = new ArrayList<String>();
        for (Element a : link)
        {
            dates.add(a.child(0).text().substring(0, 10));
        }
        return dates;
    }

    public static void main(String args[])
    {
        SqlInterface aaa = new SqlInterface();
        ArrayList<String> listUrls; //список ссылок
        ArrayList<WorkInfo> works = new ArrayList<WorkInfo>();//список вакансий
        Document sitePage;//главная страница сайта попадет сюда
        String url = "";
        url = "https://m.hh.ru/vacancies?no_magic=true&area=3&search_period=100";
        for (int n = 0; n < 6; n++)
            try
            {
                //получим страницу с вакансиями
                sitePage = Jsoup.connect(url + "&page=" + n).get();
                listUrls = (GetUrl(sitePage.select("a[class=\"vacancy-list-item-link\"]"))); //ВЕРНУТЬ
                //далее получим все необходимые нам ссылки на вакансии а также их наименования
                for (int i = 0; i < listUrls.size(); i++) //ВЕРНУТЬ
                    works.add(GetVacancyInfo(listUrls.get(i)));//ВЕРНУТЬ

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }


        Boolean a = aaa.UpdateSqlBase(aaa.ConnectSQL(), works);
        System.out.println(a);


    }


}


