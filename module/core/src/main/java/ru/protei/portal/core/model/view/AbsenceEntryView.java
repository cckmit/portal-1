package ru.protei.portal.core.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.utils.HelperFunc;

/**
 * Created by michael on 06.07.16.
 */
public class AbsenceEntryView {

    @JsonProperty
    private String creator;

    @JsonProperty
    private Long creatorId;

    @JsonProperty
    private Long dtCreation;

    @JsonProperty
    private Long dtUpdate;



    @JsonProperty
    private Long dateFrom;

    @JsonProperty
    private Long dateTill;

    @JsonProperty
    private int reason;

    @JsonProperty
    private String comment;



    @Autowired
    PersonDAO personDAO;


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

    public AbsenceEntryView fullFill(PersonAbsence a){
        this.creator = personDAO.partialGet(a.getCreatorId(), "displayShortName").getDisplayShortName();
        this.creatorId = a.getCreatorId();
        this.dtCreation = HelperFunc.toTime(a.getCreated(), null);
//        this.dtUpdate = HelperFunc.toTime(a.getUpdated(), null);
        this.reason = a.getReasonId();
        return fill(a);
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
