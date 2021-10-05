package ru.protei.portal.ui.delivery.client.activity.cardbatch.common;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.EntityOption;

public interface AbstractCardBatchCommonInfoView extends IsWidget {

    HasValue<EntityOption> type();

    HasEnabled typeEnabled();

    HasValue<String> number();

    HasValue<String> article();

    HasValue<Integer> amount();

    void setAmountValid(boolean isValid);

    HasValue<String> params();

    boolean isNumberValid();

    boolean isArticleValid();

    void hidePrevCardBatchInfo();

    void setPrevCardBatchInfo(String number, int amount, String state);

    void setActivity(AbstractCardBatchCommonInfoActivity activity);
}
