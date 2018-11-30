package model.portfolio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Portfolio {

    private String type = "real";
    private List<CopiedPortfolio> copiedPortfolios = new ArrayList<>();
    private List<PositionGroup> positionGroups = new ArrayList<>();

    public void addCopiedPortfolio(CopiedPortfolio a) {
        copiedPortfolios.add(a);
    }

    public void addPositionGroup(PositionGroup p) {
        positionGroups.add(p);
    }
    @Override
    public String toString() {
        StringBuilder presentation = new StringBuilder();
        presentation.append("-----Portfolio------\n");
        //copiedPortfolios.forEach(presentation::append) ;
        positionGroups.forEach(presentation::append);
        presentation.append("---------------------\n");
        return presentation.toString();
    }
}
