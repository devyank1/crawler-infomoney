package org.example;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class InfomoneyCrawler {
    public static void main(String[] args) {

        try {
            String baseUrl = "https://www.infomoney.com.br/mercados/";
            int maxPages = 3;

            for (int page = 1; page <= maxPages; page++) {
                String url = page == 1 ? baseUrl : baseUrl + "?page=" + page;
                System.out.println("Carregando página: " + url);

                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .timeout(10000)
                        .get();

                Elements titles = doc.select("h2");

                for (Element title : titles) {
                    String titleText = title.text();

                    Elements linkElements = title.select("a[href]");
                    if (!linkElements.isEmpty()) {
                        Element linkElement = linkElements.get(0);
                        String linkHref = linkElement.attr("href");

                        Document articleDoc = Jsoup.connect(linkHref).get();

                        Elements authorElements = articleDoc.select("a[class*='text-base']");
                        String author = null;

                        if (!authorElements.isEmpty()) {
                            String fullAuthorText = authorElements.get(0).text();
                            String[] words = fullAuthorText.split(" ");
                            if (words.length > 1) {
                                author = words[0] + " " + words[1];
                            } else {
                                author = words[0];
                            }
                        }

                        if (author == null || author.isEmpty()) {
                            Elements spanElement = articleDoc.select("span.typography__body--5 a");
                            if (spanElement != null) {
                                author = spanElement.text();
                            } else {
                                author = "Autor não encontrado";
                            }
                        }

                        Elements subtitleElement = articleDoc.select("div.text-lg");
                        String subtitle = subtitleElement != null ? subtitleElement.text() : "Não foi encontrado nenhum subtítulo";

                        Elements dateElement = articleDoc.select("time[datetime]");
                        String date = dateElement != null ? dateElement.text() : "Data não encontrada";

                        System.out.println("URL: " + linkHref);
                        System.out.println("Título: " + titleText);
                        System.out.println("Subtítulo: " + subtitle);
                        System.out.println("Autor: " + author);
                        System.out.println("Data e Hora: " + date);
                        System.out.println("------------");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}