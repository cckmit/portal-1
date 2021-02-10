package ru.protei.portal.ui.common.client.util;

import ru.protei.portal.core.model.dict.En_CommentOrHistoryType;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.common.UiConstants;

import java.util.ArrayList;
import java.util.List;

import static ru.protei.portal.core.model.dict.En_CommentOrHistoryType.COMMENT;
import static ru.protei.portal.core.model.dict.En_CommentOrHistoryType.HISTORY;
import static ru.protei.portal.ui.common.client.common.UiConstants.ISSUE_COMMENTS_TAB_SELECTED;
import static ru.protei.portal.ui.common.client.common.UiConstants.ISSUE_HISTORIES_TAB_SELECTED;
import static ru.protei.portal.ui.common.client.common.UiConstants.ISSUE_TABS.SELECTED_BY_DEFAULT;

public class MultiTabWidgetUtils {
    public static void saveCommentAndHistorySelectedTabs(LocalStorageService localStorageService,
                                                         List<En_CommentOrHistoryType> selectedTabs) {

        localStorageService.set(ISSUE_COMMENTS_TAB_SELECTED, String.valueOf(selectedTabs.contains(COMMENT)));
        localStorageService.set(ISSUE_HISTORIES_TAB_SELECTED, String.valueOf(selectedTabs.contains(HISTORY)));
    }

    public static List<En_CommentOrHistoryType> getCommentAndHistorySelectedTabs(LocalStorageService localStorageService) {
        List<En_CommentOrHistoryType> selectedTabs = new ArrayList<>();

        if (localStorageService.getBooleanOrDefault(ISSUE_COMMENTS_TAB_SELECTED, false)) {
            selectedTabs.add(COMMENT);
        }

        if (localStorageService.getBooleanOrDefault(UiConstants.ISSUE_HISTORIES_TAB_SELECTED, false)) {
            selectedTabs.add(HISTORY);
        }

        return selectedTabs.isEmpty() ? SELECTED_BY_DEFAULT : selectedTabs;
    }
}
