package ru.protei.portal.ui.common.client.util;

import ru.protei.portal.core.model.dict.En_MultiTabWidgetTabs;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.common.UiConstants;

import java.util.ArrayList;
import java.util.List;

import static ru.protei.portal.core.model.dict.En_MultiTabWidgetTabs.COMMENT;
import static ru.protei.portal.core.model.dict.En_MultiTabWidgetTabs.HISTORY;
import static ru.protei.portal.ui.common.client.common.UiConstants.MULTI_TAB_COMMENTS_SELECTED;
import static ru.protei.portal.ui.common.client.common.UiConstants.MULTI_TAB_HISTORY_SELECTED;
import static ru.protei.portal.ui.common.client.common.UiConstants.MULTI_TAB_TABS.SELECTED_BY_DEFAULT;

public class MultiTabWidgetUtils {
    public static void saveCommentAndHistorySelectedTabs(LocalStorageService localStorageService,
                                                         List<En_MultiTabWidgetTabs> selectedTabs) {

        localStorageService.set(MULTI_TAB_COMMENTS_SELECTED, String.valueOf(selectedTabs.contains(COMMENT)));
        localStorageService.set(MULTI_TAB_HISTORY_SELECTED, String.valueOf(selectedTabs.contains(HISTORY)));
    }

    public static List<En_MultiTabWidgetTabs> getCommentAndHistorySelectedTabs(LocalStorageService localStorageService) {
        List<En_MultiTabWidgetTabs> selectedTabs = new ArrayList<>();

        if (localStorageService.getBooleanOrDefault(MULTI_TAB_COMMENTS_SELECTED, false)) {
            selectedTabs.add(COMMENT);
        }

        if (localStorageService.getBooleanOrDefault(UiConstants.MULTI_TAB_HISTORY_SELECTED, false)) {
            selectedTabs.add(HISTORY);
        }

        return selectedTabs.isEmpty() ? SELECTED_BY_DEFAULT : selectedTabs;
    }
}
