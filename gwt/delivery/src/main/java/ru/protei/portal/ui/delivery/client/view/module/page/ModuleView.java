package ru.protei.portal.ui.delivery.client.view.module.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.kit.page.AbstractKitActivity;
import ru.protei.portal.ui.delivery.client.activity.kit.page.AbstractModuleView;

import java.util.List;

public class ModuleView extends Composite implements AbstractModuleView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    private void initTable() {
        ClickColumn<Module> checkbox = new ClickColumn<Module>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) { }

            @Override
            protected void fillColumnValue(Element cell, Module value) {
                cell.addClassName("module-checkbox");
                Element checkBox = new CheckBox().getElement();
                checkBox.addClassName("checkbox");
                cell.appendChild(checkBox);
            }
        };

        ClickColumn<Module> info = new ClickColumn<Module>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {

            }

            @Override
            protected void fillColumnValue(Element cell, Module value) {
                cell.appendChild(makeContent(value));
            }
        };
        table.addColumn(checkbox.header, checkbox.values);
        table.addColumn(info.header, info.values);
    }

    private Node makeContent(Module value) {
        DivElement div = Document.get().createDivElement();
        div.addClassName("module-row");
        div.appendChild(makePrimaryBlock(value));
        div.appendChild(makeSpan("", value.getDescription()));
        return div;
    }

    private Node makePrimaryBlock(Module value) {
        DivElement div = Document.get().createDivElement();
        div.appendChild(makeIcon(value));
        div.appendChild(makeSpan("bold", value.getSerialNumber()));
        div.appendChild(makeSpan("float-right manager", value.getManagerName()));
        return div;
    }

    private SpanElement makeIcon(Module value) {
        SpanElement span = Document.get().createSpanElement();
        span.setClassName("module-state");
        span.getStyle().setColor(value.getState() == null ? "" : value.getState().getColor());
        span.setInnerHTML("<i class = 'fas fa-circle'/>");
        return span;
    }

    private SpanElement makeSpan(String className, String text) {
        SpanElement span = Document.get().createSpanElement();
        span.setClassName(className);
        span.setInnerText(text);
        return span;
    }

    @Override
    public void setActivity(AbstractKitActivity activity) {
        this.activity = activity;
    }

    @Override
    public void clearModules() {
        table.clearRows();
    }

    @Override
    public void putModules(List<Module> modules) {
        modules.forEach(table::addRow);

//            Module parent = new Module();
//            parent.setName("DDDDD");
//            parent.setDescription("FFFFF");
//        table.addRow(parent);
//            Module childRow = new Module();
//            childRow.setName("ddddd");
//            childRow.setDescription("fffff");
//            table.addChildRow(parent, childRow);

    }

    @UiField
    Lang lang;


    @UiField
    TableWidget<Module> table;

    private AbstractKitActivity activity;

    private static ModuleView.ModuleViewUiBinder ourUiBinder = GWT.create(ModuleView.ModuleViewUiBinder.class);
    interface ModuleViewUiBinder extends UiBinder<HTMLPanel, ModuleView> {}
}
