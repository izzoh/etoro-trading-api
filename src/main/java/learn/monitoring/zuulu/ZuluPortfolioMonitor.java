package learn.monitoring.zuulu;


import learn.monitoring.Monitor;
import learn.monitoring.Position;
import learn.monitoring.etoro.EtoroPosition;
import learn.order.EtoroOrderExecuter;
import learn.order.Order;
import learn.units.TradeUnitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;


@Component
@Slf4j
public class ZuluPortfolioMonitor implements Monitor {

    @Autowired
    ZuluService zuluService;

    @Autowired
    EtoroOrderExecuter executer;

    @Autowired
    private ZuluPortfolioRepository portfolioRepository;

    @Autowired
    private ZuluPositionRepository positionRepository;

    @Autowired
    private TradeUnitService tradeUnitService;

    private Set<String> ignoreList = new HashSet<>();

    @PostConstruct
    public void initPortfolioRepository() {
        ignoreList.add("XAU/USD");
        if(portfolioRepository.findAll().size() == 0) {
            portfolioRepository.save(new ZuluPortfolio("369739"));
            portfolioRepository.save(new ZuluPortfolio("369854"));
            portfolioRepository.save(new ZuluPortfolio("371076"));
            portfolioRepository.save(new ZuluPortfolio("352381"));
            portfolioRepository.save(new ZuluPortfolio("292620"));

        }

    }

    public void getTraderPositions() {
        log.info("monitoring...");
        List<ZuluPortfolio> portfolios = portfolioRepository.findAll();

        for(int i = 0; i < portfolios.size(); i++) {
            List<ZuluPosition> idsToAdd = new ArrayList<>();
            List<ZuluPosition> idsToRemove = new ArrayList<>();
            ZuluPortfolio p = portfolios.get(i);
            List<ZuluPosition> newPos = zuluService.scanPositions(p.getId());
            p.getPositionsMap().forEach((k,v) -> {
                if(!newPos.contains(v)) {
//                    p.getPositionsMap().remove(k);
                    idsToRemove.add(v);
                }
            });

            newPos.forEach(pos -> {
                log.info("check to add " + pos);
                if(!p.getPositionsMap().containsKey(pos.getId()) && tradeUnitService.canAddPosition() && pos.getEtoroRef() == null && !ignoreList.contains(pos.getCurrencyName())) {
                    log.info("adding to list " + pos);
                    idsToAdd.add(pos);
                }
            });

            idsToRemove.forEach(pos -> {
                p.getPositionsMap().remove(pos.getId());
                try {
                    onClosePosition(pos, pos.getId());
                    tradeUnitService.removePositionFromCounter();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            idsToAdd.forEach(pos -> {
                p.getPositionsMap().put(pos.getId(), pos);
                try {
                    //todo transaction
                    onOpenNewPosition(pos, pos.getId());
                    p.positionsMap.put(pos.getId(), pos);
                    portfolioRepository.save(p);
                    tradeUnitService.addPositionToCounter();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            portfolioRepository.save(p);
        }



//        portfolioRepository.findAll().forEach(p -> {
//            List<ZuluPosition> newPos = zuluService.scanPositions(p.getId());
//            p.getPositionsMap().forEach((k,v) -> {
//                if(!newPos.contains(v)) {
//                    p.getPositionsMap().remove(k);
//                    try {
//                        onClosePosition(v, p.getId());
//                        tradeUnitService.removePositionFromCounter();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            });
//
//            newPos.forEach(pos -> {
//                if(!p.getPositionsMap().containsKey(pos.getId()) && tradeUnitService.canAddPosition()) {
//                    p.getPositionsMap().put(pos.getId(), pos);
//                    try {
//                        onOpenNewPosition(pos, p.getId());
//                        tradeUnitService.addPositionToCounter();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

////            log.info("saved portfolio from: " + p.getId());
//        });

    }

    @Scheduled(fixedRate = 15000, initialDelay = 5000)
    public void scan() {
        getTraderPositions();
    }

    //TODO remove mocked position name
    @Override
    public void onOpenNewPosition(Position pos, String trader) throws InterruptedException {
        ZuluPosition p = (ZuluPosition) pos;
        log.info("Opening new position: tr{} {} {} {} {}", trader, p.getId(), p.getCurrencyName(), p.getDateTime(), p.getStdLotds());
        //if(true) {
        if((new Date().getTime() - p.getDateTime().getTime()) < 50*20 * 600000 && p.getEtoroRef() == null) {
            EtoroPosition etoroP = executer.doOrder(transformToOrder(p));
            p.setEtoroRef(etoroP.getPosId());
            log.info("Opened " + p.getId());
        } else {
            log.info("Position: tr{} {} {} {} {} is too old to be open", trader, p.getId(), p.getCurrencyName(), p.getDateTime(), p.getStdLotds());
        }

    }

    //TODO remove mocked position name
    @Override
    public void onClosePosition(Position pos, String trader) throws InterruptedException {
        ZuluPosition p = (ZuluPosition) pos;
        log.info("Closing position: tr{} {} {} {} {}", trader, p.getId(), p.getCurrencyName(), p.getDateTime(), p.getStdLotds());
        if (p.getEtoroRef() != null) {
            executer.closePositionById(p.getEtoroRef(), p.getCurrencyName().replace("/",""));
            log.info("deleted " + p.getId());
        } else {
            log.info("Position: tr{} {} {} {} {} was never opened on etoro",trader, p.getId(), p.getCurrencyName(), p.getDateTime(), p.getStdLotds());
        }
    }

    //TODO remove mocked position name
    public static Order transformToOrder(ZuluPosition zp) {
//        Order o = new Order();
//        o.setValue(new BigDecimal(1000d * zp.getStdLotds()));
//        o.setName(zp.getCurrencyName().replace("/", ""));
//        o.setLeverage(30);
//        o.setType(zp.getTradeType());
//        return o;
        Order o = new Order();
        o.setValue(new BigDecimal(140));
        o.setName(zp.getCurrencyName().replace("/",""));
        o.setLeverage(30);
        o.setType(zp.getTradeType());
        return o;

    }
}
