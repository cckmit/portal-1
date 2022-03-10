package ru.protei.portal.ui.delivery.client.view.delivery.module.column;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;

public class ModuleColumn extends ClickColumn<Module> {

    @Inject
    public ModuleColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) { }

    @Override
    protected void fillColumnValue(Element cell, Module value) {
        cell.appendChild(makeContent(value));
    }

    private Node makeContent(Module value) {
        DivElement div = Document.get().createDivElement();
        div.addClassName("module-row");
        div.appendChild(makeBlock(value));
        return div;
    }

    private Node makeBlock(Module value) {
        DivElement div = Document.get().createDivElement();
        div.addClassName("module-info");
        div.appendChild(makeTopBlock(value));
        div.appendChild(makeBottomBlock(value));
        return div;
    }

    private Node makeTopBlock(Module value) {
        DivElement div = Document.get().createDivElement();
        div.appendChild(makeIcon(value));
        div.appendChild(makeSpan("bold", value.getSerialNumber()));
        div.appendChild(makeSpan("float-right manager", DateFormatter.formatDateOnly(value.getDepartureDate()), lang.moduleDepartureDate()));
        return div;
    }

    private Node makeBottomBlock(Module value) {
        DivElement div = Document.get().createDivElement();
        div.appendChild(makeSpan("", value.getName()));
        return div;
    }

    private SpanElement makeIcon(Module value) {
        SpanElement span = Document.get().createSpanElement();
        span.setClassName("module-state");
        span.getStyle().setColor(value.getState() == null ? "" : value.getState().getColor());
        span.setInnerHTML("<i class = 'fas fa-circle'/>");
        return span;
    }

    private SpanElement makeSpan(String className, String text, String title) {
        SpanElement span = Document.get().createSpanElement();
        span.setClassName(className);
        span.setInnerText(text);
        if (StringUtils.isNotBlank(title)) {
            span.setTitle(title);
        }
        return span;
    }
    private SpanElement makeSpan(String className, String text) {
        return makeSpan(className, text, null);
    }

    Lang lang;
}
