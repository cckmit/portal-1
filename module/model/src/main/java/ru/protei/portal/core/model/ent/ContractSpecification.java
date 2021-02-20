package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.converter.MoneyJdbcConverter;
import ru.protei.portal.core.model.dict.En_Currency;
import ru.protei.portal.core.model.helper.NumberUtils;
import ru.protei.portal.core.model.struct.Money;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;

@JdbcEntity(table = "contract_specification")
public class ContractSpecification implements Serializable, Comparable<ContractSpecification> {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "contract_id")
    private Long contractId;

    @JdbcColumn(name = "clause")
    private String clause;

    @JdbcColumn(name = "text")
    private String text;

    @JdbcColumn(name = "quantity")
    private Long quantity;

    @JdbcColumn(name = "cost", converterType = ConverterType.CUSTOM, converter = MoneyJdbcConverter.class)
    private Money cost;

    @JdbcColumn(name = "cost_currency")
    @JdbcEnumerated(EnumType.ID)
    private En_Currency currency;

    @JdbcPostGet
    void parseClause() {
        clauseNumbers = Stream.of(clause.split("\\.")).map(NumberUtils::parseInteger).collect(Collectors.toList());
    }

    private List<Integer> clauseNumbers;

    public ContractSpecification() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getClause() {
        return clause;
    }

    public void setClause(String clause) {
        this.clause = clause;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Integer> getClauseNumbers() {
        return clauseNumbers;
    }

    public int getClauseNesting() {
        return emptyIfNull(clauseNumbers).size();
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Money getCost() {
        return cost;
    }

    public void setCost(Money cost) {
        this.cost = cost;
    }

    public En_Currency getCurrency() {
        return currency;
    }

    public void setCurrency(En_Currency currency) {
        this.currency = currency;
    }

    @Override
    public int compareTo(ContractSpecification o) {
        List<Integer> o1 = this.getClauseNumbers();
        List<Integer> o2 = o.getClauseNumbers();

        for (int i = 0; i < Math.min(o1.size(), o2.size()); i++) {
            int c = o1.get(i).compareTo(o2.get(i));
            if (c != 0) {
                return c;
            }
        }
        return Integer.compare(o1.size(), o2.size());
    }

    @Override
    public String toString() {
        return "ContractSpecification{" +
                "id=" + id +
                ", contractId=" + contractId +
                ", clause='" + clause + '\'' +
                ", text='" + text + '\'' +
                ", quantity=" + quantity +
                ", cost=" + cost +
                ", currency=" + currency +
                ", clauseNumbers=" + clauseNumbers +
                '}';
    }
}
