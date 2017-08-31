package ru.protei.portal.ui.official.client;

import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.core.model.ent.OfficialMember;

import java.util.*;

/**
 * Утилиты для работы с должностными лицами
 */
public class OfficialUtils {

    public static Map<String, List<OfficialMember>> createMembersByRegionsMap(Official official) {
        Map<String, List<OfficialMember>> membersByRegionsMap = new HashMap<>();

        List<OfficialMember> members = official.getMembers();
        Set<String> companies = new HashSet<>();
        for (OfficialMember member : members) {
            companies.add(member.getCompany().getDisplayText());
        }
        for (String company : companies) {
            List<OfficialMember> newMembers = new ArrayList<>();
            for (OfficialMember member : members) {
                if (member.getCompany().getDisplayText().equals(company)) {
                    newMembers.add(member);
                }
            }
            membersByRegionsMap.put(company, newMembers);
        }

        return membersByRegionsMap;
    }
}
