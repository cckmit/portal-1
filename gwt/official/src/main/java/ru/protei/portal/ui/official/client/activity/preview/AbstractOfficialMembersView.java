package ru.protei.portal.ui.official.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.OfficialMember;

/**
 * Created by serebryakov on 25/08/17.
 */
public interface AbstractOfficialMembersView extends IsWidget{
    void setCompanyName(String key);

    void addOfficialMember(OfficialMember member);
}
