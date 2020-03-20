package ru.protei.portal.ui.education.client.view.entry.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import protei.utils.common.DateRange;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.EducationEntryType;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.imagepicker.ImageBase64Picker;
import ru.protei.portal.ui.common.client.widget.selector.worker.entry.WorkerEntryMultiSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.education.client.activity.entry.edit.AbstractEducationEntryEditActivity;
import ru.protei.portal.ui.education.client.activity.entry.edit.AbstractEducationEntryEditView;
import ru.protei.portal.ui.education.client.view.widget.entry.EducationEntryTypeButtonSelector;

import java.util.Set;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

public class EducationEntryEditView extends Composite implements AbstractEducationEntryEditView, ImageBase64Picker.Handler {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        imagePicker.setHandler(this);
    }

    @Override
    public void setActivity(AbstractEducationEntryEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> title() {
        return title;
    }

    @Override
    public HasValue<EducationEntryType> type() {
        return type;
    }

    @Override
    public HasValue<String> coins() {
        return coins;
    }

    @Override
    public HasValue<String> link() {
        return link;
    }

    @Override
    public HasValue<String> location() {
        return location;
    }

    @Override
    public HasValue<DateInterval> dates() {
        return dates;
    }

    @Override
    public HasValue<String> description() {
        return description;
    }

    @Override
    public TakesValue<String> image() {
        return new TakesValue<String>() {
            public void setValue(String value) { onImagePicked(value); }
            public String getValue() { return image; }
        };
    }

    @Override
    public HasValue<Set<WorkerEntryShortView>> participants() {
        return participants;
    }

    @Override
    public void setTitleRequired(boolean isRequired) {
        title.setNotNull(isRequired);
        title.setRegexp(isRequired ? "^\\S+$" : "^.*$");
        title.setValue(title.getValue());
    }

    @Override
    public void setTypeRequired(boolean isRequired) {
        type.setValidation(isRequired);
        type.checkValueIsValid();
    }

    @Override
    public void setCoinsRequired(boolean isRequired) {
        coins.setNotNull(isRequired);
        coins.setRegexp(isRequired ? "^\\d+$" : "^.*$");
        coins.setValue(coins.getValue());
    }

    @Override
    public void setLinkRequired(boolean isRequired) {
        link.setNotNull(isRequired);
        link.setRegexp(isRequired ? "^\\S+$" : "^.*$");
        link.setValue(link.getValue());
    }

    @Override
    public void setLocationRequired(boolean isRequired) {
        location.setNotNull(isRequired);
        location.setRegexp(isRequired ? "^\\S+$" : "^.*$");
        location.setValue(location.getValue());
    }

    @Override
    public void setDatesRequired(boolean isRequired) {
        datesRequired = isRequired;
        dates.markInputValid(dates.getValue() == null || !dates.getValue().isValid());
        dates.setMandatory(isRequired);
        if (isRequired && (dates.getValue() == null || !dates.getValue().isValid())) {
            dates.markInputValid(false);
        }
    }

    @Override
    public void setDescriptionRequired(boolean isRequired) {
        descriptionRequired = isRequired;
        description.removeStyleName("required");
        if (isRequired && isEmpty(description.getValue())) description.addStyleName("required");
    }

    @Override
    public void setImageRequired(boolean isRequired) {
        imageRequired = isRequired;
        imagePickerButton.removeClassName("required");
        if (isRequired && isEmpty(image)) imagePickerButton.addClassName("required");
    }

    @Override
    public void setParticipantsRequired(boolean isRequired) {
        participantsRequired = isRequired;
        participants.removeStyleName("required");
        if (isRequired && CollectionUtils.isEmpty(participants.getValue())) participants.addStyleName("required");
    }

    @Override
    public HasEnabled typeEnabled() {
        return type;
    }

    @Override
    public HasVisibility participantsVisibility() {
        return participants;
    }

    @Override
    public HasVisibility declineButtonVisibility() {
        return declineButton;
    }

    @Override
    public HasVisibility approveButtonVisibility() {
        return approveButton;
    }

    @Override
    public HasVisibility saveButtonVisibility() {
        return saveButton;
    }

    @Override
    public HasEnabled approveButtonEnabled() {
        return approveButton;
    }

    @Override
    public void onImagePicked(String base64image) {
        image = base64image;
        imagePickerButton.removeClassName("required");
        if (isEmpty(image)) {
            imagePreview.setVisible(false);
            if (imageRequired) imagePickerButton.addClassName("required");
        } else {
            imagePreview.setVisible(true);
            imagePreview.setUrl(image);
        }
    }

    @Override
    public void onImageFailed() {
        onImagePicked(null);
    }

    @UiHandler("type")
    public void typeChanged(ValueChangeEvent<EducationEntryType> event) {
        if (activity != null) {
            activity.onTypeChanged(event.getValue());
        }
    }

    @UiHandler("dates")
    public void datesChanged(ValueChangeEvent<DateInterval> event) {
        setDatesRequired(datesRequired);
    }

    @UiHandler("description")
    public void descriptionChanged(ValueChangeEvent<String> event) {
        setDescriptionRequired(descriptionRequired);
    }

    @UiHandler("participants")
    public void participantsChanged(ValueChangeEvent<Set<WorkerEntryShortView>> event) {
        setParticipantsRequired(participantsRequired);
    }

    @UiHandler("declineButton")
    public void declineButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onDeclineClicked();
        }
    }

    @UiHandler("approveButton")
    public void approveButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onApproveClicked();
        }
    }

    @UiHandler("saveButton")
    public void saveButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void cancelButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onCloseClicked();
        }
    }

    @UiField
    ValidableTextBox title;
    @Inject
    @UiField(provided = true)
    EducationEntryTypeButtonSelector type;
    @UiField
    ValidableTextBox coins;
    @UiField
    ValidableTextBox link;
    @UiField
    ValidableTextBox location;
    @Inject
    @UiField(provided = true)
    RangePicker dates;
    @UiField
    AutoResizeTextArea description;
    @UiField
    ImageBase64Picker imagePicker;
    @UiField
    SpanElement imagePickerButton;
    @UiField
    Image imagePreview;
    @Inject
    @UiField(provided = true)
    WorkerEntryMultiSelector participants;
    @UiField
    Button declineButton;
    @UiField
    Button approveButton;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;

    private String image;
    private boolean imageRequired;
    private boolean datesRequired;
    private boolean descriptionRequired;
    private boolean participantsRequired;
    private AbstractEducationEntryEditActivity activity;

    interface EducationEntryEditViewBinder extends UiBinder<HTMLPanel, EducationEntryEditView> {}
    private static EducationEntryEditViewBinder ourUiBinder = GWT.create(EducationEntryEditViewBinder.class);
}
