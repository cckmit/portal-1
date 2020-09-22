package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.converter.MoneyJdbcConverter;
import ru.protei.portal.core.model.dict.En_ContractDatesType;
import ru.protei.portal.core.model.dict.En_Currency;
import ru.protei.portal.core.model.struct.Money;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

@JdbcEntity(table = "contract_date")
public class ContractDate implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "contract_id")
    private Long contractId;

    @JdbcColumn(name = "date")
    private Date date;

    @JdbcColumn(name = "comment")
    private String comment;

    @JdbcColumn(name = "type")
    @JdbcEnumerated(EnumType.ID)
    private En_ContractDatesType type;

    @JdbcColumn(name = "cost", converterType = ConverterType.CUSTOM, converter = MoneyJdbcConverter.class)
    private Money cost;

    @JdbcColumn(name = "cost_currency")
    @JdbcEnumerated(EnumType.ID)
    private En_Currency currency;

    @JdbcColumn(name = "notify")
    private boolean isNotify;

    public ContractDate() {}

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public En_ContractDatesType getType() {
        return type;
    }

    public void setType(En_ContractDatesType type) {
        this.type = type;
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

    public boolean isNotify() {
        return isNotify;
    }

    public void setNotify(boolean notify) {
        isNotify = notify;
    }

    @Override
    public String toString() {
        return "ContractDate{" +
                "id=" + id +
                ", contractId=" + contractId +
                ", date=" + date +
                ", comment='" + comment + '\'' +
                ", type=" + type +
                ", cost=" + cost +
                ", currency=" + currency +
                ", isNotify=" + isNotify +
                '}';
    }
}
