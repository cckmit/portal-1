package ru.protei.portal.ui.common.client.widget.dndautoresizetextarea;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.DOM;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;

public class DndAutoResizeTextArea extends AutoResizeTextArea {
    private Element overlayElement;
    private boolean flag = true;
    private String overlayText;

    public DndAutoResizeTextArea() {
        addDomHandler(DomEvent::preventDefault, DragOverEvent.getType());

        addDomHandler(event -> {
            if (overlayElement != null) {
                setOverlayVisible(true);
            }
        }, DragEnterEvent.getType());

        addDomHandler(event -> {
            if (overlayElement != null) {
                setOverlayVisible(false);
            }
        }, DragLeaveEvent.getType());

        addDomHandler(event -> {
            if (overlayElement != null) {
                setOverlayVisible(false);
            }

            event.preventDefault();
            event.stopPropagation();

            dropHandler(event.getDataTransfer(), this);
        }, DropEvent.getType());
    }

    @Override
    protected void onAttach() {
        if (flag) {
            overlayElement = DOM.createDiv();
            overlayElement.setAttribute("class", "drag-overlay hide");

            Element overlayLabel = DOM.createLegend();
            overlayLabel.setInnerText(overlayText);

            overlayElement.appendChild(overlayLabel);

            DOM.getParent(getElement()).appendChild(overlayElement);

            flag = false;
        }

        super.onAttach();
    }

    public void setOverlayText(String text) {
        this.overlayText = text;
    }

    private native void dropHandler(JavaScriptObject dataTransfer, DndAutoResizeTextArea view) /*-{
            var files = dataTransfer.files;
            for (var i = 0; i < files.length; i++) {
                var file = files[i];
                var reader = new FileReader();
                reader.onload = function(evt) {
                    var data = {
                        base64: evt.target.result,
                        name: file.name,
                        type: evt.target.result.slice(5, evt.target.result.indexOf(";")),
                        size: file.size
                    };
                    view.@ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea::onPastedObject(*)(data);
                };
                reader.readAsDataURL(file);
            }
    }-*/;

    private void setOverlayVisible(boolean isOverlayVisible) {
        if (isOverlayVisible) {
            overlayElement.setAttribute("class", "drag-overlay");
        } else {
            overlayElement.setAttribute("class", "drag-overlay hide");
        }
    }
}
