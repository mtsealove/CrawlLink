import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Crawler {
    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        crawler.crawl();
    }

    private final String DRIVER_ID = "webdriver.chrome.driver";

    private final String DRIVER_PATH = "chromedriver";
    private final String baseUrl = "https://ahrefs.com/backlink-checker";


    private WebDriver driver;

    public Crawler() {
        System.setProperty(DRIVER_ID, DRIVER_PATH);
        driver = new ChromeDriver();
    }

    public void crawl() {
        driver.get(baseUrl);
        try {
            TimeUnit.SECONDS.sleep(30);
            System.out.println("start");
            List<WebElement> links = driver.findElements(By.className("css-syaaed-url"));
            LinkedList<String> list = new LinkedList<>();
            for (WebElement link : links) {
                if (link.getText().contains("bbs")) {
                    list.add(link.getText());
                }
            }
            OkHttpClient client = new OkHttpClient();
            for (String url : list) {
                checkIsAlive(client, url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 자동 브라우저 종료
            driver.quit();
        }
    }

    // 살아있는지 체크
    private void checkIsAlive(OkHttpClient client, String url) {
        String[] pre = {"http://", "https://"};
        for (String p : pre) {
            Request.Builder builder = new Request.Builder().url(p + url).get();
            Request request = builder.build();
            Response response = null;
            try {
                // http 요청
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null && !response.isRedirect()) {
                        // url 츌력
                        System.out.println(p + url);
                    }
                }
            } catch (IOException e) {

            }
        }
    }
}
