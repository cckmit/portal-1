package ru.protei.portal.ui.common.client.widget.imagepastetextarea;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.TextArea;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.Base64Facade;
import ru.protei.portal.ui.common.client.widget.imagepastetextarea.event.HasPasteHandlers;
import ru.protei.portal.ui.common.client.widget.imagepastetextarea.event.PasteEvent;
import ru.protei.portal.ui.common.client.widget.imagepastetextarea.event.PasteHandler;

import java.util.HashMap;

public class ImagePasteTextArea extends TextArea implements HasPasteHandlers {

    public ImagePasteTextArea() {
        addPasteHandler(this.getElement(), this);
    }

    @Override
    public HandlerRegistration addPasteHandler(PasteHandler handler) {
        return addHandler(handler, PasteEvent.getType());
    }

    private void onPastedObject(JavaScriptObject object) {
        Base64Image base64Image = object.cast();
        String json = JsonUtils.stringify(base64Image);
        PasteEvent.fire(this, json);
    }

    private native void addPasteHandler(Element element, ImagePasteTextArea view) /*-{
        element.addEventListener("paste", function(event) {
            var matchType = new RegExp("image.*");
            var clipboardData = event.clipboardData;
            var found = false;
            return Array.prototype.forEach.call(clipboardData.types, function(type, i) {
                var file, reader;
                if (found) {
                    return;
                }
                if (type.match(matchType) || clipboardData.items[i].type.match(matchType)) {
                    file = clipboardData.items[i].getAsFile();
                    reader = new FileReader();
                    reader.onload = function(evt) {
                        var data = {
                            base64: evt.target.result,
                            name: file.name,
                            type: file.type,
                            size: file.size
                        };
                        view.@ru.protei.portal.ui.common.client.widget.imagepastetextarea.ImagePasteTextArea::onPastedObject(*)(data);
                    };
                    reader.readAsDataURL(file);
                    return (found = true);
                }
            });
        });
    }-*/;

    private static class Base64Image extends JavaScriptObject {

        protected Base64Image() {}

        public final native String base64()/*-{
            return this.base64;
        }-*/;

        public final native String name()/*-{
            return this.name;
        }-*/;

        public final native String type()/*-{
            return this.type;
        }-*/;

        public final native Long size()/*-{
            return this.size;
        }-*/;
    }
}
