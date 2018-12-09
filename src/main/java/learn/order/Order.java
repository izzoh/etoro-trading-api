package learn.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private String name;
    private String type;
    private BigDecimal value;
    private BigDecimal sl;
    private BigDecimal tp;
    private Integer leverage;

}