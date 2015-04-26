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
public class SabahCrawler extends BaseCrawler{

    private static final String NEWSPAPER_NAME = "Sabah";
    @Async
    @Scheduled(fixedDelay = SCHEDULER_DELAY, initialDelay = 1)
    public void getArticles() {
        Newspaper newspaper = newspaperService.getNewspaperByTitle(NEWSPAPER_NAME);

        RSSFeedParser parser = new RSSFeedParser("http://www.sabah.com.tr/rss/yazarlar.xml");
        Feed feed = parser.readFeed();
        //System.out.println(feed);
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
                String yazar = "";
                String baslik = "";
                String tarih = "";
                String icerik = "";

                Element newsHeadlines = doc.select(".yazarList").select("strong").first();
                yazar = newsHeadlines.text().trim();
                System.out.println("Yazar : " + yazar);

                baslik =  doc.select(".yazarList").select("h1").first().text().trim();
                System.out.println("baslik = " + baslik);

                Author author = authorService.getAuthorByName(yazar);
                if(author == null) {
                    author = new Author();
                    author.setName(yazar);
                    Element image = doc.select(".yazarList").select("img").first();
                    String imageUrl = image.absUrl("src");
                    imageUrl = imageUrl.substring(0,imageUrl.indexOf("?"));

                    System.out.println("gelen url = " + imageUrl);


                    String imageName = System.currentTimeMillis() + ".jpg";
                    if(author.getImageUrl() == null) {
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
                
                try {
                    tarih = doc.select(".date").get(0).text().trim();
                    System.out.println(tarih);
                }catch (Exception e) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    tarih = sdf.format(new Date());


                }


                icerik = doc.select(".txtIn").select("article").get(0).html().trim();
                //System.out.println("icerik = " + icerik);
                icerik = StringUtils.clean(icerik);
                System.out.println("clean icerik = " + icerik);

                article.setTitle(baslik);
                article.setPublishDate(tarih);
                article.setContent(icerik);

                articleService.saveOrUpdateArticle(article);

            }catch (Exception e) {
                System.out.println(message.getLink() + " hata : " + e.getMessage());
            }
            //break;
        }

    }

}
