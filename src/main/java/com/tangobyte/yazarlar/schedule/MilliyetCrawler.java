package com.tangobyte.yazarlar.schedule;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.tangobyte.yazarlar.model.Newspaper;
import com.tangobyte.yazarlar.model.Article;
import com.tangobyte.yazarlar.model.Author;
import com.tangobyte.yazarlar.rss.Feed;
import com.tangobyte.yazarlar.rss.FeedMessage;
import com.tangobyte.yazarlar.rss.RSSFeedParser;
import com.tangobyte.yazarlar.utils.ImageUtil;
import com.tangobyte.yazarlar.utils.StringUtils;

@Controller
public class MilliyetCrawler extends BaseCrawler {
	
	private static final String NEWSPAPER_NAME = "Milliyet";
	@Async
	@Scheduled(fixedDelay = SCHEDULER_DELAY, initialDelay = 1)
	public void getArticles() {
		Newspaper newspaper = newspaperService.getNewspaperByTitle(NEWSPAPER_NAME);
		
		RSSFeedParser parser = new RSSFeedParser(
				"http://www.milliyet.com.tr/D/rss/rss/RssY.xml");
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
				Elements hTags = doc.select("h1, h2, h3, h4, h5, h6");
				Elements h1Tags = hTags.select("h1");
				String baslik = h1Tags.get(0).text().trim();

				Elements newsHeadlines = doc.select(".detayTop").select(
						".current");
				String yazar = newsHeadlines.get(0).text().trim();
				// System.out.println("Yazar : " + yazar);
				Author author = authorService.getAuthorByName(yazar);
				if(author == null) {
					author = new Author();
					author.setName(yazar);
					Element image = doc.select(".yazarBox").select("img").first();
					String imageUrl = image.absUrl("src");
					// System.out.println("url = " + imageUrl);
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

				// System.out.println("baslik = " + baslik);

				// System.out.println("Date : " + doc.select(".date"));
				
				String publishDate = doc.select(".date").get(0).text().trim();
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		        Date parse = sdf.parse(publishDate.substring(0, 10));
				article.setPublishDate(parse);

				String icerik = doc.select("#divAdnetKeyword3").get(0).html()
						.trim();
				// // System.out.println("icerik = " + icerik);
				icerik = StringUtils.clean(icerik);
				// // System.out.println("clean icerik = " + icerik);
				article.setContent(icerik);

				articleService.saveOrUpdateArticle(article);
			} catch (Exception e) {
				 System.out.println(message.getLink() + " hata : "
						+ e.getMessage());
			}
		}
	}
	
}
