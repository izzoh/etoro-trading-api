package learn.monitoring.etoro;

import learn.monitoring.Monitor;
import learn.monitoring.Position;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class EtoroPortfolioMonitor implements Monitor {

    @Autowired
    WebDriver driver;

    public EtoroPortfolioMonitor(WebDriver driver) {
        this.driver = driver;
        this.url = "https://www.etoro.com/";

    }

    private String url;

    @PostConstruct
    public void init() {
        driver.get(url);
    }

    @Override
    //@Scheduled(fixedRate = 60000, initialDelay = 5000)
    public void scan() throws InterruptedException {
        log.info("scaning etoro");
//        log.info(getPortfolio("people/aimstrader/portfolio").toString());
    }

    public String getPortfolio(String traderId) throws InterruptedException {
        driver.navigate().to(String.format("https://www.etoro.com/sapi/trade-data-real/live/public/portfolios?cid=%s&format=json", traderId));

        String pageSrc = driver.getPageSource();
        log.info("Portfolio: " + pageSrc);
        JSONObject res = new JSONObject( Jsoup.parse(pageSrc).body().text());
        EtoroPortfolio portfolio = new EtoroPortfolio();
        portfolio.setId(traderId);
        JSONArray groups = res.getJSONArray("AggregatedPositions");
        List<EtoroPosition> positions = new ArrayList<>();
        groups.forEach(g -> {
            JSONObject groupObj = (JSONObject)g;
            String instId = "" + groupObj.get("InstrumentID");
            driver.navigate().to(String.format("https://www.etoro.com/sapi/trade-data-real/live/public/positions?InstrumentID=%s&cid=3314694&format=json", instId, traderId));
            JSONObject posJson = new JSONObject( Jsoup.parse( driver.getPageSource()).body().text());
            JSONArray posArray = posJson.getJSONArray("PublicPositions");
            posArray.forEach(pos -> {
                EtoroPosition posObj = new EtoroPosition();
                posObj.setInstrumentId(((JSONObject)pos).getString("id"));
               // posObj.setAmmount(((JSONObject)pos).getString("id"));
            });

        });

        return "";
    }

    @Override
    public void onClosePosition(Position p, String trader) {

    }

    @Override
    public void onOpenNewPosition(Position p, String trader) {

    }


}
