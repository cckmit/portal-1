package ru.protei.portal.ui.common.client.view.valuecomment;

import ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.ValueCommentPair;

import java.util.List;

/**
 * Created by bondarenko on 28.10.16.
 */
public interface ValueCommentDataList {
    List<ValueCommentPair> getDataList();

    void setDataList(List<ValueCommentPair> dataList);

}
