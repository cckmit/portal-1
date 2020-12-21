package ru.protei.portal.ui.common.client.common;

import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.ProjectSla;

import java.util.Arrays;
import java.util.List;

public class DefaultSlaValues {

    private static final List<ProjectSla> list;

    static {
        ProjectSla emergencySla = new ProjectSla(
                En_ImportanceLevel.EMERGENCY.getId(),
                hoursToMinutes(1),
                hoursToMinutes(4),
                daysToMinutes(3)
        );

        ProjectSla criticalSla = new ProjectSla(
                En_ImportanceLevel.CRITICAL.getId(),
                hoursToMinutes(1),
                hoursToMinutes(4),
                daysToMinutes(3)
        );

        ProjectSla importantSla = new ProjectSla(
                En_ImportanceLevel.IMPORTANT.getId(),
                hoursToMinutes(2),
                daysToMinutes(1),
                daysToMinutes(3)
        );

        ProjectSla basicSla = new ProjectSla(
                En_ImportanceLevel.BASIC.getId(),
                daysToMinutes(1),
                daysToMinutes(3),
                daysToMinutes(30)
        );

        ProjectSla cosmeticSla = new ProjectSla(
                En_ImportanceLevel.COSMETIC.getId(),
                daysToMinutes(1),
                weeksToMinutes(2),
                daysToMinutes(90)
        );

        list = Arrays.asList(emergencySla, criticalSla, importantSla, basicSla, cosmeticSla);
    }

    private static Long hoursToMinutes(int hours) {
        return hours * 60L;
    }
    private static Long daysToMinutes(int days) {
        return hoursToMinutes(days * 24);
    }
    private static Long weeksToMinutes(int weeks) {
        return daysToMinutes(weeks * 7);
    }

    public static List<ProjectSla> getList() {
        return list;
    }
}
