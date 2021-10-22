package ru.protei.portal.ui.delivery.client.view.card.edit.group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.widget.markdown.MarkdownAreaWithPreview;
import ru.protei.portal.ui.common.client.widget.selector.card.state.CardStateFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeFormSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.activity.card.edit.group.AbstractCardGroupEditView;

import java.util.Date;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.dict.En_TextMarkup.MARKDOWN;
import static ru.protei.portal.core.model.util.CrmConstants.Masks.CARD_BATCH_ARTICLE_PATTERN;

public class CardGroupEditView extends Composite implements AbstractCardGroupEditView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        article.setRegexp(CARD_BATCH_ARTICLE_PATTERN);
        note.setDisplayPreviewHandler( isDisplay -> onDisplayPreviewChanged( NOTE, isDisplay ) );
        note.setRenderer(this::renderMarkupText);
        comment.setDisplayPreviewHandler( isDisplay -> onDisplayPreviewChanged( COMMENT, isDisplay ) );
        comment.setRenderer(this::renderMarkupText);
    }

    @Override
    public HasValue<CaseState> state() {
        return state;
    }

    @Override
    public HasValue<String> article() {
        return article;
    }

    @Override
    public boolean articleIsValid() {
        return article.getValue().isEmpty() || article.isValid();
    }

    @Override
    public HasValue<PersonShortView> manager() {
        return manager;
    }

    @Override
    public HasValue<Date> testDate() {
        return testDate;
    }

    @Override
    public HasValue<String> note() {
        return note;
    }

    @Override
    public HasValue<String> comment(){
        return comment;
    }

    @Override
    public void setTestDateValid(boolean isValid) {
        testDate.markInputValid(isValid);
    }

    @Override
    public void setStateWarning(boolean isWarning) {
        setWarning(isWarning, stateContainer);
    }

    @Override
    public void setArticleWarning(boolean isWarning) {
        setWarning(isWarning, articleContainer);
    }

    @Override
    public void setManagerWarning(boolean isWarning) {
        setWarning(isWarning, managerContainer);
    }

    @Override
    public void setTestDateWarning(boolean isWarning) {
        setWarning(isWarning, testDateContainer);
    }

    @Override
    public void setNoteWarning(boolean isWarning) {
        setWarning(isWarning, noteContainer);
    }

    @Override
    public void setCommentWarning(boolean isWarning) {
        setWarning(isWarning, commentContainer);
    }

    private void setWarning(boolean isWarning, HTMLPanel container) {
        if (isWarning) {
            container.removeStyleName("no-warning");
        } else {
            container.addStyleName("no-warning");
        }
    }

    private void renderMarkupText(String text, Consumer<String> consumer ) {
        textRenderController.render( text, MARKDOWN, new FluentCallback<String>()
                .withError( throwable -> consumer.accept( null ) )
                .withSuccess( consumer ) );
    }

    private void onDisplayPreviewChanged( String key, boolean isDisplay ) {
        localStorageService.set( this.getClass().getSimpleName() + "_" + key, String.valueOf( isDisplay ) );
    }

    public boolean isTestDateFieldValid() {
        Date date = testDate().getValue();
        if (date == null) {
            return false;
        }

        return date.getTime() > System.currentTimeMillis();
    }

    @UiHandler("testDate")
    public void onTestDateChanged(ValueChangeEvent<Date> event) {
        setTestDateValid(isTestDateFieldValid());
    }

    @Inject
    @UiField( provided = true )
    CardStateFormSelector state;
    @UiField
    ValidableTextBox article;
    @Inject
    @UiField(provided = true)
    EmployeeFormSelector manager;
    @Inject
    @UiField(provided = true)
    SinglePicker testDate;
    @UiField
    MarkdownAreaWithPreview note;
    @UiField
    MarkdownAreaWithPreview comment;

    @UiField
    HTMLPanel stateContainer;
    @UiField
    HTMLPanel articleContainer;
    @UiField
    HTMLPanel managerContainer;
    @UiField
    HTMLPanel testDateContainer;
    @UiField
    HTMLPanel noteContainer;
    @UiField
    HTMLPanel commentContainer;

    @Inject
    TextRenderControllerAsync textRenderController;
    @Inject
    LocalStorageService localStorageService;

    static private final String NOTE = "Note";
    static private final String COMMENT = "Comment";

    private static CardGroupEditView.ViewUiBinder ourUiBinder = GWT.create(CardGroupEditView.ViewUiBinder.class);
    interface ViewUiBinder extends UiBinder<HTMLPanel, CardGroupEditView> {}
}
