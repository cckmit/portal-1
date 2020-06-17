package ru.protei.portal.app.portal.client.activity.profile.subscription;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Set;

public interface AbstractProfileSubscriptionView extends IsWidget {
    void setActivity(AbstractProfileSubscriptionActivity activity);
    HasValue<Set<PersonShortView>> persons();
}
