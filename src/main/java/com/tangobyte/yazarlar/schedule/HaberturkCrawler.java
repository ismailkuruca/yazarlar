package com.tangobyte.yazarlar.schedule;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.LocaleUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tangobyte.yazarlar.model.Author;
import com.tangobyte.yazarlar.model.Newspaper;
import com.tangobyte.yazarlar.model.Article;
import com.tangobyte.yazarlar.rss.Feed;
import com.tangobyte.yazarlar.rss.FeedMessage;
import com.tangobyte.yazarlar.rss.RSSFeedParser;
import com.tangobyte.yazarlar.utils.ImageUtil;
import com.tangobyte.yazarlar.utils.StringUtils;

/**
 * Created by erk on 8.4.2015.
 */
@Component
public class HaberturkCrawler extends BaseCrawler {


    private static final String NEWSPAPER_NAME = "Habertürk";

    @Async
    @Scheduled(fixedDelay = SCHEDULER_DELAY, initialDelay = 1)
    public void getArticles() {
        Newspaper newspaper = newspaperService.getNewspaperByTitle(NEWSPAPER_NAME);

        RSSFeedParser parser =
                new RSSFeedParser(
                        "http://pipes.yahoo.com/pipes/pipe.run?_id=3a33a688bacf72ed688c931cf7c4572a&_render=rss");
        Feed feed = parser.readFeed();
        // // System.out.println(feed);
        for (FeedMessage message : feed.getMessages()) {
            // System.out.println(message.getLink());
            Document doc = null;
            try {
                doc = Jsoup.connect(message.getLink()).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Article article = new Article();
            try {
                String yazar = doc.select(".author-name > a").get(0).text().trim();
                String baslik = doc.select(".news-tt > h1").get(0).text().trim();
                String tarih = "";
                String icerik = "";

                // System.out.println(message.getDate() + "---------->");
                // System.out.println("Yazar : " + message.getTitle());
                article.setTitle(baslik);

                Element newsHeadlines = doc.select(".news-date-create").select("span").first();
                try {
                    tarih = newsHeadlines.text().trim();
                    // System.out.println(tarih);
                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    tarih = sdf.format(new Date());
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MMMM.yyyy", LocaleUtils.toLocale("tr_TR"));
                String[] split = tarih.split(" ");
                Date parse = sdf.parse(split[0] + "." + split[1] + "." + split[2]);
                article.setPublishDate(parse);

                Author author = authorService.getAuthorByName(yazar);
                if (author == null) {
                    author = new Author();
                    author.setName(yazar);
                    Element image = doc.select(".author-image").select("img").first();
                    String imageUrl = image.absUrl("src");

                    // System.out.println("gelen url = " + imageUrl);
                    String imageName = System.currentTimeMillis() + ".jpg";
                    try {
                        imageUrl = imageUrl.substring(0, imageUrl.indexOf("?"));
                    } catch (Exception e) {
                        // System.out.println("image da soru isareti yok");
                    }

                    if (author.getImageUrl() == null) {
                        try {
                            ImageUtil.createImage(imageUrl, imageName, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        author.setImageUrl(imageName);
                    }
                    author.setNewspaper(newspaper);
                    author = authorService.saveOrUpdateAuthor(author);
                }
                article.setAuthor(author);



                icerik = doc.select(".news-content-text").first().html().trim();
                // // System.out.println("icerik = " + icerik);
                icerik = StringUtils.clean(icerik);
                // System.out.println("clean icerik = " + icerik);
                article.setContent(icerik);

                articleService.saveOrUpdateArticle(article);


            } catch (Exception e) {
                e.printStackTrace();
                // System.out.println(message.getLink() + " hata : " + e.getMessage());

            }

        }

    }
}
