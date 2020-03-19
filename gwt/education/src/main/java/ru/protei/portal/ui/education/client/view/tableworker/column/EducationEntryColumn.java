package ru.protei.portal.ui.education.client.view.tableworker.column;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;

public class EducationEntryColumn extends Composite {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setImage(String url) {
        this.image.setUrl(url);
    }

    public void setLink(String link) {
        this.link.setHref(link);
    }

    public void setTitle(String title) {
        this.title.setInnerText(title);
    }

    public void setType(String type) {
        this.type.setInnerText(type);
    }

    public void setCoins(Integer coins) {
        this.coins.setInnerText(String.valueOf(coins));
    }

    public void setAttendance(String attendance) {
        this.attendance.setInnerText(attendance);
    }

    public void setDateAndLocation(String dateAndLocation) {
        this.dateAndLocation.setInnerText(dateAndLocation);
    }

    @UiField
    Image image;
    @UiField
    Anchor link;
    @UiField
    SpanElement title;
    @UiField
    SpanElement type;
    @UiField
    SpanElement coins;
    @UiField
    SpanElement attendance;
    @UiField
    SpanElement dateAndLocation;

    interface EducationEntryColumnBinder extends UiBinder<HTMLPanel, EducationEntryColumn> {}
    private static EducationEntryColumnBinder ourUiBinder = GWT.create(EducationEntryColumnBinder.class);
}
