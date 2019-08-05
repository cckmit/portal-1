package ru.protei.portal.ui.common.client.widget.dndautoresizetextarea;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;

public class DndAutoResizeTextArea extends AutoResizeTextArea {
    private Element overlay;
    private HTMLPanel dropZonePanel;
    private String overlayText;

    public void setDropZonePanel(HTMLPanel dropZonePanel) {
        this.dropZonePanel = dropZonePanel;
        dropZonePanel.addStyleName("drop-zone");

        addHandlers();
        createOverlay();
    }

    public void setOverlayText(String text) {
        this.overlayText = text;
    }

    private void addHandlers() {
        dropZonePanel.addDomHandler(event -> {
            event.preventDefault();
            setOverlayVisible(true);
        }, DragOverEvent.getType());

        dropZonePanel.addDomHandler(event -> setOverlayVisible(true), DragEnterEvent.getType());

        dropZonePanel.addDomHandler(event -> setOverlayVisible(false), DragLeaveEvent.getType());

        dropZonePanel.addDomHandler(event -> {
            setOverlayVisible(false);

            event.preventDefault();
            event.stopPropagation();

            dropHandler(event.getDataTransfer(), this);
        }, DropEvent.getType());
    }

    private void createOverlay() {
        overlay = DOM.createDiv();
        overlay.setAttribute("class", "drag-overlay hide");

        Element overlayLabel = DOM.createLegend();
        overlayLabel.setInnerText(overlayText);

        overlay.appendChild(overlayLabel);
        dropZonePanel.getElement().appendChild(overlay);
    }

    private void setOverlayVisible(boolean isOverlayVisible) {
        if (isOverlayVisible) {
            dropZonePanel.addStyleName("drop-zone-active");
            dropZonePanel.removeStyleName("drop-zone");
            overlay.setAttribute("class", "drag-overlay");
        } else {
            dropZonePanel.removeStyleName("drop-zone-active");
            dropZonePanel.addStyleName("drop-zone");
            overlay.setAttribute("class", "drag-overlay hide");
        }
    }

    private native void dropHandler(JavaScriptObject dataTransfer, DndAutoResizeTextArea view) /*-{
        var files = dataTransfer.files;
        var dataList = [];
        for (var i = 0; i < files.length; i++) {
            uploadFile(files[i]);
        }

        function uploadFile(file) {
            var reader = new FileReader();
            reader.onload = function (evt) {
                var data = {
                    base64: evt.target.result,
                    name: file.name,
                    type: evt.target.result.slice(5, evt.target.result.indexOf(";")),
                    size: file.size
                };

                dataList.push(data);

                if (dataList.length == files.length) {
                    view.@ru.protei.portal.ui.common.client.widget.dndautoresizetextarea.DndAutoResizeTextArea::onPastedObjects(*)(dataList);
                }
            };
            reader.readAsDataURL(file);
        }
    }-*/;
}
