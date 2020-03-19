package ru.protei.portal.ui.education.client.view.entry.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.EducationEntryType;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.imagepicker.ImageBase64Picker;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.education.client.activity.entry.edit.AbstractEducationEntryEditActivity;
import ru.protei.portal.ui.education.client.activity.entry.edit.AbstractEducationEntryEditView;
import ru.protei.portal.ui.education.client.view.widget.entry.EducationEntryTypeButtonSelector;

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
            imagePickerButton.addClassName("required");
        } else {
            imagePreview.setVisible(true);
            imagePreview.setUrl(image);
        }
    }

    @Override
    public void onImageFailed() {
        onImagePicked(null);
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
    TextBox link;
    @UiField
    TextBox location;
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
    @UiField
    Button declineButton;
    @UiField
    Button approveButton;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;

    private String image;
    private AbstractEducationEntryEditActivity activity;

    interface EducationEntryEditViewBinder extends UiBinder<HTMLPanel, EducationEntryEditView> {}
    private static EducationEntryEditViewBinder ourUiBinder = GWT.create(EducationEntryEditViewBinder.class);
}
