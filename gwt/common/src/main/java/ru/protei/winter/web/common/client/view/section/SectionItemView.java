package ru.protei.winter.web.common.client.view.section;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.winter.web.common.client.activity.section.AbstractSectionItemActivity;
import ru.protei.winter.web.common.client.activity.section.AbstractSectionItemView;
import ru.protei.winter.web.common.client.common.DisplayStyle;

import static ru.protei.portal.test.client.DebugIds.SIDEBAR_MENU.ICON_SUFFIX;

/**
 * Вид одного элемента раздела навигации
 */
public class SectionItemView extends Composite implements AbstractSectionItemView {

    public SectionItemView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractSectionItemActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setText( String value ) {
        title.setInnerText(value);
    }

    @Override
    public void setIcon( String value ) {
        if ( value == null ) return;

        iconTrumbnail.removeClassName("hide");
        icon.setClassName(value);
    }

    @Override
    public void setActive( boolean value ) {
        if ( value ) {
            root.setStyleName( "active" );
            iconTrumbnail.addClassName("bg-complete");
        }
        else {
            root.removeStyleName( "active" );
            iconTrumbnail.removeClassName("bg-complete");
        }
    }

    @Override
    public void setCaret( Boolean isDropdown ) {
        // not implemented
    }

    @Override
    public void setBadge( Integer count ) {
        // not implemented
    }

    @Override
    public void setBadgeStyle( DisplayStyle style ) {
       // not implemented
    }

    @Override
    public void setSubMenuVisible(boolean isVisible) {
        if (isVisible != subSection.isVisible()) {
            subSection.setVisible(isVisible);
            subSectionIcon.setVisible(isVisible);
            arrow.removeClassName("hide");
            hideSubSection();
        }
    }

    @Override
    public HasWidgets getChildContainer() {
        return subSection;
    }

    @Override
    public void toggleSubSections(boolean forceVisible) {
        isSubSectionVisible = forceVisible || !isSubSectionVisible;
        if (isSubSectionVisible) {
            arrow.addClassName("open active");
            showSubSection();
        } else {
            arrow.removeClassName("open active");
            hideSubSection();
        }
    }

    @Override
    public void setSectionTitle(String title) {
        anchor.setTitle( title );
    }

    @Override
    public void setEnsureDebugId( String ensureDebugId ) {
        anchor.ensureDebugId( ensureDebugId );
        icon.setId( ensureDebugId + ICON_SUFFIX );
    }

    @UiHandler("anchor")
    public void onAnchorClicked( ClickEvent event ) {
        if ( anchor.getHref().endsWith("#") ) {
            event.preventDefault();
            onSectionClicked();
        }
    }

    private void onSectionClicked() {
        if ( activity != null ) {
            activity.onSectionClicked( this );
        }
    }

    private void hideSubSection() {
        subSection.getElement().getStyle().setHeight(0, Style.Unit.PX);
        subSection.getElement().getStyle().setPadding(0, Style.Unit.PX);
        subSection.getElement().getStyle().setMargin(0, Style.Unit.PX);
    }

    private void showSubSection() {
        int height = subSection.getElement().getChildCount() * 38 + 30;
        subSection.getElement().getStyle().setHeight(height, Style.Unit.PX);
        subSection.getElement().getStyle().setPaddingTop(18, Style.Unit.PX);
        subSection.getElement().getStyle().setPaddingBottom(10, Style.Unit.PX);
        subSection.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
    }

    AbstractSectionItemActivity activity;

    @UiField
    HTMLPanel root;
    @UiField
    Anchor anchor;
    @UiField
    SpanElement title;
    @UiField
    Element icon;
    @UiField
    SpanElement iconTrumbnail;
    @UiField
    SpanElement arrow;
    @UiField
    Label subSectionIcon;
    @UiField
    HTMLPanel subSection;

    Boolean isSubSectionVisible = false;

    interface SectionItemViewUiBinder extends UiBinder<HTMLPanel, SectionItemView> {}
    private static SectionItemViewUiBinder ourUiBinder = GWT.create( SectionItemViewUiBinder.class );

}