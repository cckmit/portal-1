package ru.protei.portal.ui.common.client.render;

import com.google.gwt.text.client.LongRenderer;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Renderer;

public class LongNoSpaceRenderer extends AbstractRenderer<Long> {
    private static LongNoSpaceRenderer INSTANCE;

    public static Renderer<Long> instance() {
        if (INSTANCE == null) {
            INSTANCE = new LongNoSpaceRenderer();
        }
        return INSTANCE;
    }

    protected LongNoSpaceRenderer() {
    }

    public String render(Long object) {
        String rendered = LongRenderer.instance().render(object);
        return rendered.replaceAll("\\s+","");
    }
}
