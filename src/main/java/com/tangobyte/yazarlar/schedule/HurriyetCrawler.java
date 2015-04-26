package com.tangobyte.yazarlar.schedule;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.LogManager;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.tangobyte.yazarlar.model.Newspaper;
import com.tangobyte.yazarlar.model.Article;
import com.tangobyte.yazarlar.model.Author;
import com.tangobyte.yazarlar.rss.Feed;
import com.tangobyte.yazarlar.rss.FeedMessage;
import com.tangobyte.yazarlar.rss.RSSFeedParser;
import com.tangobyte.yazarlar.utils.StringUtils;

/**
 * Created by erk on 8.4.2015.
 */
@Component
public class HurriyetCrawler extends BaseCrawler{

    private static final String NEWSPAPER_NAME = "Hürriyet";
    @Async
    @Scheduled(fixedDelay = SCHEDULER_DELAY, initialDelay = 1)
    public void getArticles() {
        Newspaper newspaper = newspaperService.getNewspaperByTitle(NEWSPAPER_NAME);
        LogManager.getLogManager().reset();
        RSSFeedParser parser = new RSSFeedParser("http://rss.hurriyet.com.tr/rss.aspx?sectionId=9");
        // g1405532@trbvm.com
        // deneme

        Feed feed = parser.readFeed();
        Map<String, String> cookies = new HashMap<String, String>();
        WebClient wc = null;
        try {
            setTrustAllCerts();
            Response res =
                    Jsoup.connect(
                            "https://hurpass.com/iframe/login?appkey=52da7ef64037f9497f0acb091390051062215&secret=52da7f0c4037f9497f0acb0b1390051084754"
                                    + "&domain=sosyal.hurriyet.com.tr"
                                    + "&callback_url=http%3a%2f%2fsosyal.hurriyet.com.tr%2fAccount%2fAutoLogin%3freturnUrl%3dhttp%3a%2f%2fsosyal.hurriyet.com.tr"
                                    + "&referer=http%3a%2f%2fsosyal.hurriyet.com.tr"
                                    + "&user_page=http%3a%2f%2fsosyal.hurriyet.com.tr%2fAccount%2fAutoLogin%3freturnUrl%3dhttp%3a%2f%2fsosyal.hurriyet.com.tr"
                                    + "&is_mobile=0&session_timeout=0&is_vative=0&email=")
                            .data("email", "g1405532@trbvm.com", "password", "deneme", "keepAlive",
                                    "ok").method(Method.POST).followRedirects(true).execute();
            cookies.putAll(res.cookies());
            Response mid = Jsoup.connect(
                "http://sosyal.hurriyet.com.tr/Account/Login?" +
                "ssoid=" + cookies.get("sso_3") +
                "&ssosid=" + cookies.get("sso_4") +
                "&ssosv=" + cookies.get("sso_1") + "&returnUrl="
                ).method(Method.GET).followRedirects(true).execute();
            
            cookies.putAll(mid.cookies());
            wc = new WebClient(BrowserVersion.CHROME);
            wc.getOptions().setCssEnabled(false);
            wc.getOptions().setJavaScriptEnabled(true);
            wc.waitForBackgroundJavaScript(5000);
            wc.setAjaxController(new NicelyResynchronizingAjaxController());
            wc.getOptions().setUseInsecureSSL(true);
            CookieManager cm = new CookieManager();
            
            Set<String> keySet = cookies.keySet();
            for (String s : keySet) {
                System.out.println(s + " " + cookies.get(s));
                cm.addCookie(new Cookie("hurriyet.com.tr", s, cookies.get(s)));
            }
            
            wc.setCookieManager(cm);

        } catch (IOException e1) {
            System.err.println("failed to login");
            e1.printStackTrace();
        } catch (Exception e) {
            System.err.println("ssl error");
            e.printStackTrace();
        }

        for (FeedMessage message : feed.getMessages()) {
            System.out.println(message.getLink());
            ScriptResult spotText = null;
            ScriptResult content = null;
            ScriptResult postDate = null;
            ScriptResult title = null;
            ScriptResult authorSc = null;
//            Document doc = null;
            try {
                HtmlPage page = wc.getPage(message.getLink());
                spotText = page.executeJavaScript("articleDetailData.articleData.aSpotText");
                postDate = page.executeJavaScript("articleDetailData.articleData.addDate");
                title = page.executeJavaScript("articleDetailData.articleData.aTitle");
                content = page.executeJavaScript("articleDetailData.articleData.aContent");
                authorSc = page.executeJavaScript("HRO.user.fullname");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Article article = new Article(); 
            try {
                String yazar = authorSc.getJavaScriptResult().toString();
                Author author = authorService.getAuthorByName(yazar);
                if(author == null) {
                    author = new Author();
                    author.setName(yazar);
                    author.setNewspaper(newspaper);
                    author = authorService.saveOrUpdateAuthor(author);
                }
                article.setAuthor(author);
                String baslik = title.getJavaScriptResult().toString();
                String pubDate = postDate.getJavaScriptResult().toString();
                pubDate = pubDate.substring(0, pubDate.indexOf("T"));
                String icerik = spotText.getJavaScriptResult().toString() + "<br/>" + content.getJavaScriptResult().toString();

                System.out.println(message.getDate() + "---------->");
                System.out.println("Yazar : " + message.getTitle());
                article.setTitle(baslik);
                article.setPublishDate(pubDate);

                icerik = StringUtils.clean(icerik);
                System.out.println("clean icerik = " + icerik);
                article.setContent(icerik);

                articleService.saveOrUpdateArticle(article);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(message.getLink() + " hata : " + e.getMessage());

            }
        }

    }
    
    private static void setTrustAllCerts() throws Exception
    {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted( java.security.cert.X509Certificate[] certs, String authType ) { }
                public void checkServerTrusted( java.security.cert.X509Certificate[] certs, String authType ) { }
            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance( "SSL" );
            sc.init( null, trustAllCerts, new java.security.SecureRandom() );
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier( 
                new HostnameVerifier() {
                    public boolean verify(String urlHostName, SSLSession session) {
                        return true;
                    }
                });
        }
        catch ( Exception e ) {
            //We can not recover from this exception.
            e.printStackTrace();
        }
    }

}
