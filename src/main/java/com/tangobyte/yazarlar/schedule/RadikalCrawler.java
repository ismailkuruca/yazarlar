package com.tangobyte.yazarlar.schedule;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tangobyte.yazarlar.model.Newspaper;
import com.tangobyte.yazarlar.model.Article;
import com.tangobyte.yazarlar.model.Author;
import com.tangobyte.yazarlar.rss.Feed;
import com.tangobyte.yazarlar.rss.FeedMessage;
import com.tangobyte.yazarlar.rss.RSSFeedParser;
import com.tangobyte.yazarlar.utils.ImageUtil;
import com.tangobyte.yazarlar.utils.StringUtils;

/**
 * Created by erk on 8.4.2015.
 */
@Component
public class RadikalCrawler extends BaseCrawler {
    
    private static final String NEWSPAPER_NAME = "Radikal";
    @Async
    @Scheduled(fixedDelay = SCHEDULER_DELAY, initialDelay = 1)
    public void getArticles() {
        Newspaper newspaper = newspaperService.getNewspaperByTitle(NEWSPAPER_NAME);


        RSSFeedParser parser = new RSSFeedParser("http://www.radikal.com.tr/d/rss/RssYazarlar.xml");
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
                String yazar = "";
                String baslik = "";
                String tarih = "";
                String icerik = "";

                Element newsHeadlines = doc.select(".author-content-writer").select("h3").first();
                yazar = newsHeadlines.text().trim();
                // System.out.println("Yazar : " + yazar);
                Author author = authorService.getAuthorByName(yazar);
                if(author == null) {
                    author = new Author();
                    author.setName(yazar);
                    Element image = doc.select(".author-content-writer").select("img").first();
                    String imageUrl = image.absUrl("src");
                    // System.out.println("gelen url = " + imageUrl);
                    String imageName = System.currentTimeMillis() + ".jpg";

                    try {
                        ImageUtil.createImage(imageUrl, imageName, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    author.setImageUrl(imageName);
                    author.setNewspaper(newspaper);
                    author = authorService.saveOrUpdateAuthor(author);
                }
                article.setAuthor(author);
                baslik = doc.select(".author-content-text").select("h1").first().text().trim();
                // System.out.println("baslik = " + baslik);
                
                article.setTitle(baslik);

                try {
                    tarih = doc.select(".date").get(0).text().trim();
                    // System.out.println(tarih);
                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    tarih = sdf.format(new Date());


                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date parse = sdf.parse(tarih);
                article.setPublishDate(parse);
                icerik = doc.select(".text-area").select("h6").first().html().trim();
                // // System.out.println("icerik = " + icerik);
                icerik = StringUtils.clean(icerik);
                // System.out.println("clean icerik = " + icerik);

                
                String icerik2 = doc.select("#metin2").html().trim();
                // // System.out.println("clean icerik2 = " + icerik2);
                icerik2 = StringUtils.clean(icerik2);

                article.setContent("<b>" + icerik + "</b>" + icerik2);

                articleService.saveOrUpdateArticle(article);


            } catch (Exception e) {
                // System.out.println(message.getLink() + " hata : " + e.getMessage());
            }

        }

    }

}
