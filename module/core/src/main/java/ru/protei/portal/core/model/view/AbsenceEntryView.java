package ru.protei.portal.core.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.utils.HelperFunc;

/**
 * Created by michael on 06.07.16.
 */
public class AbsenceEntryView {

    @JsonProperty
    private Long dateFrom;

    @JsonProperty
    private Long dateTill;

    @JsonProperty
    private int reason;

    @JsonProperty
    private String comment;


    public AbsenceEntryView() {

    }

    public AbsenceEntryView(PersonAbsence a) {
        fill(a);
    }

    public AbsenceEntryView fill (PersonAbsence a) {
        this.comment = a.getUserComment();
        this.dateFrom = HelperFunc.toTime(a.getFromTime(), null);
        this.dateTill = HelperFunc.toTime(a.getTillTime(), null);
        this.reason = a.getReasonId();
        return this;
    }


    public Long getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Long dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Long getDateTill() {
        return dateTill;
    }

    public void setDateTill(Long dateTill) {
        this.dateTill = dateTill;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
