package com.tangobyte.yazarlar.schedule;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

@Component
public class PostaCrawler extends BaseCrawler {

    private static final String NEWSPAPER_NAME = "Posta";
    @Async
    @Scheduled(fixedDelay = SCHEDULER_DELAY, initialDelay = 1)
    public void getArticles() {
        Newspaper newspaper = newspaperService.getNewspaperByTitle(NEWSPAPER_NAME);

        RSSFeedParser parser = new RSSFeedParser("http://www.posta.com.tr/xml/rss/rss_309_0.xml");
        Feed feed = parser.readFeed();
        // System.out.println(feed);
        for (FeedMessage message : feed.getMessages()) {
            System.out.println(message.getLink());
            Document doc = null;
            try {
                doc = Jsoup.connect(message.getLink()).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Article article = new Article();
            try {
                Elements hTags = doc.select("h1, h2, h3, h4, h5, h6");
                Elements h1Tags = hTags.select("h3");
                String baslik = h1Tags.get(0).text().trim();
                String yazar ="";
                
                Element newsHeadlines = doc.select(".yazarDetCont").select("h1").first();
                try {
                    baslik = newsHeadlines.text().trim();
                } catch (Exception e) {
                    newsHeadlines = doc.select(".kunye").select("h1").first();
                    yazar = newsHeadlines.text();
                }
                Author author = authorService.getAuthorByName(yazar);
                if(author == null) {
                    author = new Author();
                    author.setName(yazar);
                    author.setNewspaper(newspaper);
                    author = authorService.saveOrUpdateAuthor(author);
                }
                article.setAuthor(author);
                article.setTitle(baslik);
                System.out.println(baslik);


                String tarih = "";
                String icerik = "";


                newsHeadlines = doc.select(".dateTxt").first();
                try {
                    tarih = newsHeadlines.text().trim();
                    System.out.println(tarih);
                } catch (Exception e) {
                    try {
                        tarih = doc.select(".date").first().text().trim();
                    } catch (Exception ex) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                        tarih = sdf.format(new Date());
                    }
                }
                icerik = doc.select(".yazarHaberTxt").first().html().trim();
                // System.out.println("icerik = " + icerik);
                icerik = StringUtils.clean(icerik);
                // System.out.println("clean icerik = " + icerik);
                article.setContent(icerik);
                Element image = doc.select(".yImg").select("img").first();

                String imageUrl = "";



                try {
                    imageUrl = image.absUrl("src");
                    imageUrl = imageUrl.substring(0, imageUrl.indexOf("?"));
                } catch (Exception e) {
                    imageUrl = doc.select(".kunye").select("img").first().absUrl("src");

                    System.out.println("image da soru isareti yok");


                }

                System.out.println("gelen url = " + imageUrl);

                String imageName = System.currentTimeMillis() + ".jpg";

                try {
                    ImageUtil.createImage(imageUrl, imageName, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                article.setImageUrl(imageName);
                article.setPublishDate(message.getDate());


                articleService.saveOrUpdateArticle(article);



            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(message.getLink() + " hata : " + e.getMessage());

            }

        }

    }

}
