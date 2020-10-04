package ru.protei.portal.ui.issue.client.view.create.subtask;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.widget.makdown.MarkdownAreaWithPreview;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.issue.client.activity.create.subtask.AbstractSubtaskCreateActivity;
import ru.protei.portal.ui.issue.client.activity.create.subtask.AbstractSubtaskCreateView;

import static ru.protei.portal.core.model.util.CrmConstants.NAME_MAX_SIZE;

public class SubtaskCreateView extends Composite implements AbstractSubtaskCreateView {

    public SubtaskCreateView() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        description.setRenderer((text, consumer) -> activity.renderMarkupText(text, consumer));
        description.setDisplayPreviewHandler(isDisplay -> activity.onDisplayPreviewChanged(DESCRIPTION, isDisplay));
        name.setMaxLength(NAME_MAX_SIZE);
    }

    @Override
    public void setActivity(AbstractSubtaskCreateActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<String> description() {
        return description;
    }

    @Override
    public HasValidable nameValidator() {
        return nameValidator;
    }

    private HasValidable nameValidator = new HasValidable() {
        @Override
        public void setValid(boolean isValid) {
            if ( isValid ) {
                nameContainer.removeStyleName("has-error");
            } else {
                nameContainer.addStyleName("has-error");
            }
        }

        @Override
        public boolean isValid() {
            return HelperFunc.isNotEmpty(name.getValue());
        }
    };

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        name.ensureDebugId(DebugIds.ISSUE.NAME_INPUT);
        description.setEnsureDebugId(DebugIds.ISSUE.DESCRIPTION_INPUT);
        nameLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NAME);
        descriptionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.INFO);
    }

    @UiField
    LabelElement nameLabel;
    @UiField
    ValidableTextBox name;
    @UiField
    LabelElement descriptionLabel;
    @UiField
    MarkdownAreaWithPreview description;
    @UiField
    HTMLPanel nameContainer;

    private AbstractSubtaskCreateActivity activity;

    interface SubtaskCreateViewUiBinder extends UiBinder<HTMLPanel, SubtaskCreateView> {}
    private static SubtaskCreateViewUiBinder ourUiBinder = GWT.create(SubtaskCreateViewUiBinder.class);
}