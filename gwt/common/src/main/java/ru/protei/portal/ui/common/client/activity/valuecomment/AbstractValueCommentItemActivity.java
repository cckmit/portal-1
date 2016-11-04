package ru.protei.portal.ui.common.client.activity.valuecomment;

/**
 * Абстракция активити элемента списка
 */
public interface AbstractValueCommentItemActivity {
    void onChangeComment( AbstractValueCommentItemView item );
    void onChangeValue( AbstractValueCommentItemView item );
}
