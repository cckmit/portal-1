package ru.protei.portal.ui.common.client.widget.uploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;

/**
 * Created by bondarenko on 22.12.16.
 */
public class FileUploader extends Composite  {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        //form.setAction();
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);
        fileUpload.setName("file");

        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                //get the filename to be uploaded
                String filename = fileUpload.getFilename();
                if (filename.length() == 0) {
                    Window.alert("No File Specified!");
                } else {
                    //submit the form
                    form.submit();
                }
            }
        });

        form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
                // When the form submission is successfully completed, this
                //event is fired. Assuming the service returned a response
                //of type text/html, we can get the result text here
                Window.alert(event.getResults());
            }
        });

        fileUpload.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                Window.alert(event.toString());
            }
        });
    }


//    public void onModuleLoad() {
//        VerticalPanel panel = new VerticalPanel();
//        //create a FormPanel
//        final FormPanel form = new FormPanel();
//        //create a file upload widget
//        final FileUpload fileUpload = new FileUpload();
//        //create labels
//        Label selectLabel = new Label("Select a file:");
//        //create upload button
//        Button uploadButton = new Button("Upload File");
//        //pass action to the form to point to service handling file
//        //receiving operation.
//        form.setAction("http://www.tutorialspoint.com/gwt/myFormHandler");
//        // set form to use the POST method, and multipart MIME encoding.
//        form.setEncoding(FormPanel.ENCODING_MULTIPART);
//        form.setMethod(FormPanel.METHOD_POST);
//
//        //add a label
//        panel.add(selectLabel);
//        //add fileUpload widget
//        panel.add(fileUpload);
//        //add a button to upload the file
//        panel.add(uploadButton);
//        uploadButton.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                //get the filename to be uploaded
//                String filename = fileUpload.getFilename();
//                if (filename.length() == 0) {
//                    Window.alert("No File Specified!");
//                } else {
//                    //submit the form
//                    form.submit();
//                }
//            }
//        });
//
//        form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
//            @Override
//            public void onSubmitComplete(SubmitCompleteEvent event) {
//                // When the form submission is successfully completed, this
//                //event is fired. Assuming the service returned a response
//                //of type text/html, we can get the result text here
//                Window.alert(event.getResults());
//            }
//        });
//        panel.setSpacing(10);
//
//        // Add form to the root panel.
//        form.add(panel);
//
//        RootPanel.get("gwtContainer").add(form);
//    }


    @UiField
    FormPanel form;
    @UiField
    FileUpload fileUpload;
    @UiField
    Button button;

    private static FileUploaderUiBinder ourUiBinder = GWT.create(FileUploaderUiBinder.class);
    interface FileUploaderUiBinder extends UiBinder<HTMLPanel, FileUploader> {
    }
}