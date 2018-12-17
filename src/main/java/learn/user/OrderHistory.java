package learn.user;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class OrderHistory {

    private String orderId;
    private String traderId;
    private String copiedFrom;
    private String type;
    private Date open;
    private Date closed;
    private BigDecimal profitInUSD;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
}