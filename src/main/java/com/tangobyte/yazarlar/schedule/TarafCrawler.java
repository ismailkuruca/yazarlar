package com.tangobyte.yazarlar.schedule;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.LocaleUtils;
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
public class TarafCrawler extends BaseCrawler{
    private static final String NEWSPAPER_NAME = "Taraf";
    @Async
    @Scheduled(fixedDelay = SCHEDULER_DELAY, initialDelay = 1)
    public void getArticles() {
        Newspaper newspaper = newspaperService.getNewspaperByTitle(NEWSPAPER_NAME);

        RSSFeedParser parser =
                new RSSFeedParser(
                        "http://pipes.yahoo.com/pipes/pipe.run?_id=6cf5bd9e2c0fa25c618a3a578a648c60&_render=rss");
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
                String yazar = doc.select("div.vcard.author > strong > a").get(0).text().trim();
//                news.setNewsHeading(yazar);
                // System.out.println("yazar = " + yazar); 
                String baslik = doc.select(".name.post-title.entry-title > span").get(0).text().trim();

                String tarih = "";
                try {
                    tarih = doc.select(".post-meta").select("span").get(0).text().trim();
                    // System.out.println(tarih);
                } catch (Exception e) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    tarih = sdf.format(new Date());
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MMMM.yyyy", LocaleUtils.toLocale("tr_TR"));
                String[] split = tarih.split(" ");
                String join = (split[0] + "." + split[1] + "." + split[2]);
                Date parse = sdf.parse(join);
                article.setPublishDate(parse);

                Author author = authorService.getAuthorByName(yazar);
                if(author == null) {
                    author = new Author();
                    author.setName(yazar);
                    Element image = doc.select(".single-post-thumb").select("img").first();
                    String imageUrl = image.absUrl("src");
                    imageUrl = imageUrl.substring(0, imageUrl.indexOf("?"));

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
                String icerik = doc.select(".post-inner").select("div").first().html();
                // // System.out.println("icerik = " + icerik);
                icerik = StringUtils.clean(icerik);

                icerik = icerik.substring(icerik.indexOf("</p>") + 4, icerik.length());
                // System.out.println("clean icerik = " + icerik);



                article.setContent(icerik);

                articleService.saveOrUpdateArticle(article);

            } catch (Exception e) {
                // System.out.println(message.getLink() + " hata : " + e.getMessage());
                e.printStackTrace();

            }
            // break;
        }

    }

}
