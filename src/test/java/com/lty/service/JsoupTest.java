package com.lty.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

/**
 *
 * @author lty
 */
public class JsoupTest {

    @Test
    public void test() {
        // 爬取网页
        String url = "https://juejin.cn/post/7363519735056007194";

        try {
            Document document = Jsoup.connect(url).get();
            // 根据区域的class名获取
            Elements title = document.getElementsByClass("article-title");
            Elements tags = document.getElementsByClass("article");
            Elements content = document.getElementsByClass("article-viewer markdown-body result");

            System.out.println("标题为:" + title.get(0).text());
            System.out.println("标签为:" + tags.get(0).text());
            System.out.println("内容为:" + content.get(0).text());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
