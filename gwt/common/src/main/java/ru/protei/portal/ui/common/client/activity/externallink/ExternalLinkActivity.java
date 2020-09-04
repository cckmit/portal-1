package ru.protei.portal.ui.common.client.activity.externallink;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.HTML;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.winter.web.common.client.events.MenuEvents;

public abstract class ExternalLinkActivity implements Activity {

    @Event
    public void onInit(MenuEvents.Init event) {
        init = event;
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {

        init.parent.add(html.asWidget());

        NodeList<Element> anchors = html.getElement().getElementsByTagName("a");
        for (int i = 0; i < anchors.getLength(); i++) {
            if (anchors.getItem(i).getPropertyString("href").endsWith("#")) {
                Element anchor = Document.get().getElementById(anchors.getItem(i).getId());
                Element submenu = Document.get().getElementById(anchor.getParentElement().getElementsByTagName("ul").getItem(0).getId());
                addOnAnchorClickListener(anchor, submenu);
            }
        }
    }

    private native void addOnAnchorClickListener(Element anchor, Element submenu) /*-{
        anchor.addEventListener("click", function (event) {
            event.preventDefault();
            event.stopPropagation();
            var arrow = anchor.getElementsByClassName("arrow").item(0);
            var opened = arrow.classList.contains("open");
            var height = submenu.childElementCount * 38 + 30;
            console.log(submenu.firstChild.height);
            if (opened) {
                arrow.classList.remove("open");
                submenu.style.cssText = 'margin:0px;padding:0;height:0;';
            } else {
                arrow.classList.add("open");
                submenu.style.cssText = 'margin:0px;padding-top:18px;padding-bottom:10px;margin-bottom:10px;height:' + height + 'px';
            }
        })
    }-*/;

    public HTML html = new HTML("<li>" +
            "<a href=\"#\" title=\"Test\" id=\"debug-sidebar-menu-store-delivery\">" +
            "<span class=\"title\">Test</span>" +
            "<span class=\"arrow\"></span>" +
            "<span class=\"icon-thumbnail\">" +
            "<i class=\"fas fa-warehouse\" id=\"debug-sidebar-menu-store-delivery-icon\"></i></span></a>" +
            "<ul class=\"sub-menu\" id=\"debug-sidebar-menu-store-delivery-submenu\" style=\"padding:0;margin:0;height:0\">" +
            "<li>" +
            "<a href=\"https://oldportal.protei.ru/sd/index.jsp\" target=\"_blank\" title=\"Test sub\" id=\"gwt-debug-sidebar-menu-store-delivery-store\">" +
            "<span class=\"title\">Test sub</span>" +
            "<span class=\"icon-thumbnail\">" +
            "<i class=\"fas fa-memory\" id=\"sidebar-menu-store-delivery-store-icon\"></i></span></a></li>" +
            "<li>" +
            "<a href=\"https://oldportal.protei.ru/sd/delivery/delivery.jsp\" target=\"_blank\" title=\"Test sub 2\" id=\"gwt-debug-sidebar-menu-store-delivery-delivery\">" +
            "<span class=\"title\">Test sub 2</span>" +
            "<span class=\"icon-thumbnail\">" +
            "<i class=\"fas fa-truck\" id=\"sidebar-menu-store-delivery-delivery-icon\"></i></span></a></li>" +
            "<li>" +
            "<a href=\"https://oldportal.protei.ru/sd/store/card_info.jsp\" target=\"_blank\" title=\"Test sub 3\" id=\"gwt-debug-sidebar-menu-store-delivery-card-search\">" +
            "<span class=\"title\">Test sub 3</span>" +
            "<span class=\"icon-thumbnail\">" +
            "<i class=\"fas fa-search\" id=\"sidebar-menu-store-delivery-card-search-icon\"></i></span></a></li></ul>" +
            "</li>");

    MenuEvents.Init init;
}
