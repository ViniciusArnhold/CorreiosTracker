package me.vinicius.correios.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vinicius on 16/07/2016.
 */
public class Rastreamento {

    private static final String URL_BASE = "http://websro.correios.com.br/sro_bin/txect01%24.QueryList?P_LI" +
            "NGUA=001&P_TIPO=001&P_COD_UNI=";

    private String code;

    private Event events[];

    //

    @SuppressWarnings("unused")
    public Rastreamento() {

    }

    public Rastreamento(String code) {
        setCode(code);
    }

    @SuppressWarnings("unused")
    public String getCode() {
        return this.code;
    }

    @SuppressWarnings("WeakerAccess")
    public void setCode(String code) {
        code = code.toUpperCase();

        Matcher m = Pattern.compile("[A-Z]{2}\\d{9}[A-Z]*").matcher(code); //Quick Match
        if (m.find()) {
            this.code = code;
            return;
        }
        throw new InvalidValueException("Invalid value for code : " + code);
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isCodeSet() {
        return code != null;
    }

    @SuppressWarnings("")
    private Document getHTML() throws IOException {
        if (isCodeSet()) {

            return Jsoup.connect(URL_BASE + code).get();

        }
        throw new NullPointerException();
    }

    private void getTrackingData() throws IOException {

        Document doc = getHTML();
        Event e[] = new Event[(doc.select("tr").size() - 1) / 2];


        String data = "";
        String local = "";
        String action = "";
        String movement;


        int count = 0;
        int index = 0;
        for (Element tr : doc.select("tr")) {
            if (count == 0) {
                count++;
                continue;
            }
            if (count % 2 != 0) { //
                Elements td = tr.select("td");
                data = td.get(0).text();
                local = td.get(1).text();
                action = td.get(2).text();
                count++;
            } else {
                movement = tr.select("td").get(0).text();
                e[index] = new Event(data, local, action, movement);
                count++;
                index++;
            }

        }

        events = e;

    }

    @SuppressWarnings("unused")
    public void track() throws IOException {
        getTrackingData();
    }

    public Event[] getEvents() {
        return events;
    }

}
