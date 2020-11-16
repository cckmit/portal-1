package ru.protei.portal.ui.issue.client.view.create.subtask;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.makdown.MarkdownAreaWithPreview;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.company.SubcontractorCompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonModel;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.issue.client.activity.create.subtask.AbstractSubtaskCreateActivity;
import ru.protei.portal.ui.issue.client.activity.create.subtask.AbstractSubtaskCreateView;

import java.util.Collections;
import java.util.HashSet;

import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;
import static ru.protei.portal.core.model.util.CrmConstants.NAME_MAX_SIZE;

public class SubtaskCreateView extends Composite implements AbstractSubtaskCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        description.setRenderer((text, consumer) -> activity.renderMarkupText(text, consumer));
        description.setDisplayPreviewHandler(isDisplay -> activity.onDisplayPreviewChanged(DESCRIPTION, isDisplay));
        name.setMaxLength(NAME_MAX_SIZE);
        manager.setAsyncModel(managerModel);
        initView();
        ensureDebugIds();
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

    @Override
    public HasValue<EntityOption> managerCompany() {
        return managerCompany;
    }

    @Override
    public HasValidable managerCompanyValidator() {
        return managerCompany;
    }

    @Override
    public HasValue<PersonShortView> manager() {
        return manager;
    }

    @Override
    public void setManagerCompanyModel(SubcontractorCompanyModel subcontractorCompanyModel) {
        managerCompany.setAsyncModel(subcontractorCompanyModel);
    }

    @Override
    public void updateManagersCompanyFilter(Long managerCompanyId) {
        managerModel.updateCompanies(null, setOf(managerCompanyId));
    }

    @UiHandler("managerCompany")
    public void onManagerCompanyChanged(ValueChangeEvent<EntityOption> event) {
        activity.onManagerCompanyChanged();
    }

    @Override
    public void setFocusName() {
        name.setFocus(true);
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
        nameLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NAME);
        description.setEnsureDebugId(DebugIds.ISSUE.DESCRIPTION_INPUT);
        descriptionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.INFO);
        manager.setEnsureDebugId(DebugIds.ISSUE.MANAGER_SELECTOR);
        manager.ensureLabelDebugId(DebugIds.ISSUE.LABEL.MANAGER);
    }

    private void initView() {
        managerCompany.setDefaultValue(lang.selectIssueCompany());
        manager.setDefaultValue(lang.selectIssueManager());
    }

    @UiField
    @Inject
    Lang lang;
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
    @Inject
    @UiField(provided = true)
    CompanyFormSelector managerCompany;
    @Inject
    @UiField(provided = true)
    PersonFormSelector manager;

    @Inject
    PersonModel managerModel;

    private AbstractSubtaskCreateActivity activity;

    interface SubtaskCreateViewUiBinder extends UiBinder<HTMLPanel, SubtaskCreateView> {}
    private static SubtaskCreateViewUiBinder ourUiBinder = GWT.create(SubtaskCreateViewUiBinder.class);
}