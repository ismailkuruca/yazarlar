package com.tangobyte.yazarlar.schedule;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
public class ZamanCrawler extends BaseCrawler{
    private static final String NEWSPAPER_NAME = "Zaman";
    @Async
    @Scheduled(fixedDelay = SCHEDULER_DELAY, initialDelay = 1)
    public void getArticles() {
        Newspaper newspaper = newspaperService.getNewspaperByTitle(NEWSPAPER_NAME);

        RSSFeedParser parser = new RSSFeedParser("http://www.zaman.com.tr/yazarlar.rss");
        Feed feed = parser.readFeed();
        //// System.out.println(feed);
        for (FeedMessage message : feed.getMessages()) {
            // System.out.println(message.getLink());
            Document doc = null;
            try {
                doc = Jsoup.connect(message.getLink()).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Article article = new Article();
            try {
                String yazar = "";
                String baslik = "";
                String tarih = "";
                String icerik = "";

                yazar = doc.select("div.yazarInfo.pull-left > h5").get(0).text().trim();
                baslik = doc.select("#sonYazi > h1").get(0).text().trim();
                article.setTitle(baslik);
                
                
                Element newsHeadlines = doc.select(".detayTarih").first();
                try {
                    tarih = newsHeadlines.text().trim();
                    // System.out.println(tarih);
                }catch (Exception e) {
                    e.printStackTrace();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    tarih = sdf.format(new Date());
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MMMM.yyyy", LocaleUtils.toLocale("tr_TR"));
                String[] split = tarih.replaceAll(",", "").split(" ");
                String join = (split[0] + "." + split[1] + "." + split[2]);
                Date parse = sdf.parse(join);
                article.setPublishDate(parse);


                Author author = authorService.getAuthorByName(yazar);
                if(author == null) {
                    author = new Author();
                    author.setName(yazar);
                    Element image = doc.select(".yazarWrap").select("img").first();
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

                icerik = doc.select(".detaySpot").first().html().trim();
                //// System.out.println("icerik = " + icerik);
                icerik = StringUtils.clean(icerik);
                // System.out.println("clean icerik = " + icerik);


                String icerik2 = doc.select("span[itemProp=articleBody]").html();
                // System.out.println("icerik2 = " + icerik2);
                icerik2 = StringUtils.clean(icerik2);

                article.setContent(icerik + icerik2);

                articleService.saveOrUpdateArticle(article);

            }catch (Exception e) {
                e.printStackTrace();
                // System.out.println(message.getLink() + " hata : " + e.getMessage());

            }
            
        }

    }

}
