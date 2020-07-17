package ru.protei.portal.ui.common.client.widget.imagepicker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.widget.uploader.FileUploader;

public class ImageBase64Picker extends FileUploader {

    public interface Handler {
        void onImagePicked(String base64image);
        void onImageFailed();
    }

    @Inject
    public void init() {
        fileUpload.getElement().setAttribute("accept", "image/*");
    }

    public void setHandler(Handler uploadHandler) {
        this.uploadHandler = uploadHandler;
    }

    @Override
    protected void submitCompleteHandler(FormPanel.SubmitCompleteEvent event) {
        form.removeStyleName(UPLOADING_CLASS_NAME);
        form.reset();
        fileUpload.setEnabled(true);
        if (uploadHandler == null) {
            return;
        }
        String base64image = event.getResults();
        if (StringUtils.isBlank(base64image)) {
            uploadHandler.onImageFailed();
        } else {
            uploadHandler.onImagePicked(base64image);
        }
    }

    @Override
    protected void changeHandler(ChangeEvent event) {
        String filename = fileUpload.getFilename();
        if (StringUtils.isEmpty(filename)) {
            return;
        }
        if (form.getElement().hasClassName(UPLOADING_CLASS_NAME)) {
            return;
        }
        form.addStyleName(UPLOADING_CLASS_NAME);
        form.setAction(UPLOAD_URL);
        form.submit();
        fileUpload.setEnabled(false);
    }

    private Handler uploadHandler;

    private static final String UPLOADING_CLASS_NAME = "image-uploading";
    private static final String UPLOAD_URL = GWT.getModuleBaseURL() + "springApi/convertImageToBase64";
}
