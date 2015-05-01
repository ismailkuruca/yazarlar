package com.tangobyte.yazarlar.schedule;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import sun.util.locale.LocaleUtils;

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
public class SozcuCrawler extends BaseCrawler{

    private static final String NEWSPAPER_NAME = "Sözcü";
    @Async
    @Scheduled(fixedDelay = SCHEDULER_DELAY, initialDelay = 1)
    public void getArticles() {
        Newspaper newspaper = newspaperService.getNewspaperByTitle(NEWSPAPER_NAME);
        RSSFeedParser parser =
                new RSSFeedParser(
                        "http://pipes.yahoo.com/pipes/pipe.run?_id=6ed575cf74309c1d6c70eb4afc8d7a83&_render=rss");
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
                String yazar =  doc.select(".yazdet").get(0).html().trim();
                // System.out.println("yazar = " + yazar);
                String baslik = doc.select(".basdet > a").get(0).text().trim();
                
                String tarih = "";
                String icerik = "";


                Author author = authorService.getAuthorByName(yazar);
                if(author == null) {
                    author = new Author();
                    author.setName(yazar);
                    Element image = doc.select(".yazardet-img").select("img").first();
                    String imageUrl = image.absUrl("src");


                    // System.out.println("gelen url = " + imageUrl);

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
                article.setAuthor(author);
                article.setTitle(baslik);
                
                try {
                    tarih = doc.select(".tarihdet").select("p").get(0).text().trim();
                    // System.out.println(tarih);
                } catch (Exception e) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    tarih = sdf.format(new Date());
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MMMM.yyyy", org.apache.commons.lang3.LocaleUtils.toLocale("tr_TR"));
                String[] split = tarih.split(" ");
                Date parse = sdf.parse(split[0] + "." + split[1] + "." + split[2]);
                article.setPublishDate(parse);
                icerik = doc.select(".content").get(0).html().trim();
                // // System.out.println("icerik = " + icerik);
                icerik = StringUtils.clean(icerik);
                // System.out.println("clean icerik = " + icerik);

                article.setContent(icerik);

                articleService.saveOrUpdateArticle(article);

            } catch (Exception e) {
                // System.out.println(message.getLink() + " hata : " + e.getMessage());
            }
            // break;
        }

    }

}
