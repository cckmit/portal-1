package ru.protei.winter.web.common.client.activity.menu;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.winter.web.common.client.activity.section.AbstractSectionItemActivity;
import ru.protei.winter.web.common.client.activity.section.AbstractSectionItemView;
import ru.protei.winter.web.common.client.events.MenuEvents;

import java.util.*;

public abstract class MenuActivity implements Activity, AbstractSectionItemActivity {

    @Event
    public void onInit(MenuEvents.Init event) {
        init = event;
    }

    @Event
    public void onShowSectionItem(MenuEvents.Add event) {
        addSection(event.parent, event.header, event.icon,  event.title, event.href, event.ensureDebugId);
    }

    @Event
    public void onClearAllSection(MenuEvents.Clear event) {
        init.parent.clear();
        sectionItems.clear();
        itemViewToIdentity.clear();
        identityToItemView.clear();
        identityToHasSubSections.clear();
    }

    @Event
    public void onSelectSectionItem(MenuEvents.Select event) {
        if (identityToItemView == null) {
            return;
        }

        for (AbstractSectionItemView item : sectionItems) {
            item.setActive(false);
        }

        if (event.parents != null) {
            for (String parent : event.parents) {
                AbstractSectionItemView parentView = identityToItemView.get(parent);
                if (parentView == null) {
                    continue;
                }
                parentView.setActive(true);
                parentView.toggleSubSection(true);
            }
        }
        identityToItemView.get(event.identity).setActive(true);
    }

    @Event
    public void onCloseAllSections(MenuEvents.CloseAll event) {
        for (AbstractSectionItemView item : sectionItems) {
            String identity = itemViewToIdentity.get(item);
            if (identityToHasSubSections.get(identity)) {
                item.toggleSubSection(false);
            }
        }
    }

    @Override
    public void onSectionClicked(AbstractSectionItemView itemView) {
        if (itemViewToIdentity == null) {
            return;
        }

        String identity = itemViewToIdentity.get(itemView);

        for (AbstractSectionItemView item : sectionItems) {
            String identity1 = itemViewToIdentity.get(item);
            if (identityToHasSubSections.get(identity1) && !Objects.equals(identity, identity1)) {
                item.toggleSubSection(false);
            }
        }

        if (identityToHasSubSections.get(identity)) {
            boolean subSectionVisible = itemView.isSubSectionVisible();
            itemView.toggleSubSection(null);
            // если подменю уже открыто – событие по клику на секцию не рассылаем
            if (subSectionVisible) {
                return;
            }
        }

        fireEvent(new MenuEvents.Clicked(identity));
    }

    private void addSection(String parent, String header, String icon, String title, String href, String ensureDebugId) {
        AbstractSectionItemView itemView = factory.get();
        itemView.setActivity(this);
        itemView.setText(header);
        itemView.setIcon(icon);
        itemView.setSectionTitle(title);
        if (href != null) {
            itemView.setHref(href);
        }

        if (ensureDebugId != null) {
            itemView.setEnsureDebugId(ensureDebugId);
        }

        if (parent == null || !identityToItemView.containsKey(parent)) {
            init.parent.add(itemView.asWidget());
        } else {
            AbstractSectionItemView parentView = identityToItemView.get(parent);
            parentView.getChildContainer().add(itemView.asWidget());
            parentView.setSubSectionVisible(true);
            identityToHasSubSections.put(parent, true);
        }

        itemViewToIdentity.put(itemView, header);
        identityToItemView.put(header, itemView);
        identityToHasSubSections.put(header, false);
        sectionItems.add(itemView);
    }

    @Inject
    Provider<AbstractSectionItemView> factory;

    MenuEvents.Init init;

    List<AbstractSectionItemView> sectionItems = new ArrayList<>();

    Map<AbstractSectionItemView, String> itemViewToIdentity = new HashMap<>();
    Map<String, AbstractSectionItemView> identityToItemView = new HashMap<>();
    Map<String, Boolean> identityToHasSubSections = new HashMap<>();
}