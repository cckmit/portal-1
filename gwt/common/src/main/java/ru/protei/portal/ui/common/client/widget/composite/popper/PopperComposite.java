package ru.protei.portal.ui.common.client.widget.composite.popper;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import ru.protei.portal.ui.common.client.widget.composite.PopupLikeComposite;

public abstract class PopperComposite extends PopupLikeComposite {
    public enum Placement {
        TOP {
            @Override
            public String getCode() {
                return "top";
            }
        },

        RIGHT {
            @Override
            public String getCode() {
                return "right";
            }
        },

        BOTTOM {
            @Override
            public String getCode() {
                return "bottom";
            }
        },

        LEFT {
            @Override
            public String getCode() {
                return "left";
            }
        };

        public abstract String getCode();
    }

    public PopperComposite() {
        addCloseHandler(event -> destroyPopper(popper));
    }

    public void show(Element relative) {
        show(relative, Placement.BOTTOM);
    }

    public void show(Element relative, Placement placement) {
        show(relative, placement, 0, 0);
    }

    public void show(Element relative, Placement placement, int skidding, int distance) {
        setVisible(true);

        if (isAutoResize) {
            if (resizeHandlerReg != null) {
                addResizeHandler(relative, getElement());
            }

            resizeWidth(relative, getElement());
        }

        if (popper != null) {
            destroyPopper(popper);
        }

        popper = createPopper(relative, getElement(), placement.getCode(), skidding, distance);
    }

    public void hide() {
        setVisible(false);
        destroyPopper(popper);

        if (isAutoResize) {
            removeResizeHandler();
        }
    }

    public void setAutoResize(boolean isAutoResize) {
        this.isAutoResize = isAutoResize;
    }

    private void addResizeHandler(Element relative, Element popup) {
        resizeHandlerReg = Window.addResizeHandler(event -> {
            if (isVisible()) {
                resizeWidth(relative, popup);
            }
        });
    }

    private void removeResizeHandler() {
        if (resizeHandlerReg != null) {
            resizeHandlerReg.removeHandler();
        }

        resizeHandlerReg = null;
    }

    private void resizeWidth(Element relative, Element popup) {
        int offsetWidth = relative.getOffsetWidth();
        popup.getStyle().setWidth( offsetWidth < 100 ? 150 : offsetWidth, Style.Unit.PX );
    }

    private native JavaScriptObject createPopper(Element button, Element popup, String placement, int skidding, int distance) /*-{
        return $wnd.Popper.createPopper(button, popup, {
            placement: placement,
            modifiers: [
                {
                    name: 'offset',
                    options: {
                        offset: [skidding, distance]
                    }
                }
            ]
        });
    }-*/;

    private native void destroyPopper(JavaScriptObject popper) /*-{
        if (popper) {
            popper.destroy();
            popper = null;
        }
    }-*/;

    private JavaScriptObject popper;
    private boolean isAutoResize;

    private HandlerRegistration resizeHandlerReg;
}
