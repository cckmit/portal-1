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
        title.setInnerText( value );
    }

    @Override
    public void setIcon( String value ) {
        if ( value == null ) return;

        iconTrumbnail.removeClassName( "hide" );
        icon.setClassName( value );
    }

    @Override
    public void setActive( boolean value ) {
        if ( value ) {
            root.setStyleName( "active" );
            iconTrumbnail.addClassName( "bg-complete" );
        }
        else {
            root.removeStyleName( "active" );
            iconTrumbnail.removeClassName( "bg-complete" );
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
    public void setSubSectionVisible(boolean isVisible) {
        if (isVisible != subSection.isVisible()) {
            subSection.setVisible(isVisible);
            arrow.removeClassName("hide");
            closeSubSection();
        }
    }

    @Override
    public boolean isSubSectionVisible() {
        return isSubSectionVisible;
    }

    @Override
    public HasWidgets getChildContainer() {
        return subSection;
    }

    @Override
    public void toggleSubSection(Boolean force) {
        isSubSectionVisible = force == null ? !isSubSectionVisible : force;
        if (isSubSectionVisible) {
            arrow.addClassName("open active");
            openSubSection();
        } else {
            arrow.removeClassName("open active");
            closeSubSection();
        }
    }

    @Override
    public void setSectionTitle( String title ) {
        anchor.setTitle( title );
    }

    @Override
    public void setEnsureDebugId( String ensureDebugId ) {
        anchor.ensureDebugId( ensureDebugId );
        icon.setId( ensureDebugId + ICON_SUFFIX );
    }

    @Override
    public void setHref( String href ) {
        anchor.setHref(href);
    }

    @Override
    public void addClickHandler() {
        root.addDomHandler(event -> onSectionClicked(), ClickEvent.getType());
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        if (isEnabled) {
            root.removeStyleName("disabled");
        } else {
            root.addStyleName("disabled");
        }
    }

    @UiHandler("anchor")
    public void onAnchorClicked( ClickEvent event ) {
        if ( anchor.getHref().endsWith("#") ) {
            event.preventDefault();
            closeExternalSections(root.getElement().getParentElement());
            onSectionClicked();
        }
    }

    private void onSectionClicked() {
        if ( activity != null ) {
            activity.onSectionClicked( this );
        }
    }

    private void openSubSection() {
        subSection.getElement().getStyle().setPaddingTop(18, Style.Unit.PX);
        subSection.getElement().getStyle().setPaddingBottom(10, Style.Unit.PX);
        subSection.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        int height = subSection.getElement().getChildCount() * (subSection.getElement().getFirstChildElement().getClientHeight() + 1) + 28;
        subSection.getElement().getStyle().setHeight(height, Style.Unit.PX);
    }

    private void closeSubSection() {
        subSection.getElement().getStyle().setHeight(0, Style.Unit.PX);
        subSection.getElement().getStyle().setPadding(0, Style.Unit.PX);
        subSection.getElement().getStyle().setMargin(0, Style.Unit.PX);
    }

    private native void closeExternalSections(Element menu) /*-{
        var sections = menu.getElementsByClassName("external");
        for (i = 0; i < sections.length; i++) {
            var anchor = sections[i].firstElementChild;
            var submenu = sections[i].lastElementChild;
            if (submenu) {
                anchor.getElementsByClassName("arrow").item(0).classList.remove("open");
                submenu.style.cssText = 'margin:0px;padding:0;height:0;';
            }
        }
    }-*/;

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
    HTMLPanel subSection;

    Boolean isSubSectionVisible = false;

    interface SectionItemViewUiBinder extends UiBinder<HTMLPanel, SectionItemView> {}
    private static SectionItemViewUiBinder ourUiBinder = GWT.create( SectionItemViewUiBinder.class );

}