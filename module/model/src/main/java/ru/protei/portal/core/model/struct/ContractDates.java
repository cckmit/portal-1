package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Период оплаты и поставки договора
 */
public class ContractDates implements Serializable {

    @JsonProperty("items")
    private List<ContractDate> itemList;

    public ContractDates() {
        itemList = new ArrayList<>();
    }

    @JsonIgnore
    public List<ContractDate> getItems() {
        return itemList;
    }
}
