package ru.protei.portal.api.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_ResultStatus;

import java.util.Collection;

/**
 * Created by michael on 27.06.16.
 */
@JsonAutoDetect
public class CoreResponse<T> {

    @JsonProperty
    private En_ResultStatus status;

    @JsonProperty
    private T data;

    /**
     * Используется для идентификации размера/объема данных
     * В частности, для списочных структур может возвращать
     * общее количество записей, которое возможно выбрать по запросу
     *
     * Для единичных структур данных в овете всегда равно 1
     */
    @JsonProperty
    private int dataAmountTotal;

    public boolean isOk () {
        return status == En_ResultStatus.OK;
    }

    public boolean isError () {
        return status != En_ResultStatus.OK;
    }

    public En_ResultStatus getStatus () {
        return status;
    }

    public T getData () {
        return data;
    }

    public int getDataAmountTotal() {
        return dataAmountTotal;
    }

    public CoreResponse<T> error (En_ResultStatus status) {
        this.status = status;
        return this;
    }

    public CoreResponse<T> error (En_ResultStatus status, T errData) {
        this.status = status;
        this.data = errData;
        return this;
    }

    public CoreResponse<T> redirect (String to) {
        return this;
    }

    public CoreResponse<T> success (T data) {
        return this.success(data, data instanceof Collection ? ((Collection)data).size() : 1);
    }

    public CoreResponse<T> success (T data, int dataAmount) {
        this.data = data;
        this.status = En_ResultStatus.OK;
        this.dataAmountTotal = dataAmount;
        return this;
    }
}
