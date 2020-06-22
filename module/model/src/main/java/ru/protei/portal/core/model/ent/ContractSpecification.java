package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.helper.NumberUtils;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JdbcEntity(table = "contract_specification")
public class ContractSpecification implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "contract_id")
    private Long contractId;

    @JdbcColumn(name = "clause")
    private String clause;

    @JdbcColumn(name = "text")
    private String text;

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
        return clauseNumbers.size();
    }

    @Override
    public String toString() {
        return "ContractDate{" +
                "id=" + id +
                ", contractId=" + contractId +
                ", clause=" + clause +
                ", text=" + text +
                '}';
    }
}
