package ru.protei.portal.core.model.dict;

import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.core.model.util.CrmConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by michael on 17.05.16.
 */
public enum En_ImportanceLevel  {
    CRITICAL(1, "critical"),
    IMPORTANT (2, "important"),
    BASIC (3, "basic"),
    COSMETIC (4, "cosmetic"),
    MEDIUM (5, "medium");


    En_ImportanceLevel (int id, String code) {
        this.id = id;
        this.code = code;
    }

    private final int id;
    private final String code;

    private static List<ProjectSla> makeDefaultValues() {
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

        return Arrays.asList(criticalSla, importantSla, basicSla, cosmeticSla);
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

    public static En_ImportanceLevel getById(Integer id) {
        if(id == null)
            return null;

        for (En_ImportanceLevel imp : En_ImportanceLevel.values())
            if (imp.id == id)
                return imp;

        return null;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public static En_ImportanceLevel find (int id) {
        for (En_ImportanceLevel il : En_ImportanceLevel.values())
            if (il.id == id)
                return il;

        return null;
    }

    public static En_ImportanceLevel[] values(boolean onlyCommon){
        if (!onlyCommon){
            return En_ImportanceLevel.values();
        } else {
            List<En_ImportanceLevel> commonValues = new ArrayList<>();
            for (En_ImportanceLevel level : En_ImportanceLevel.values()) {
                if (CrmConstants.ImportanceLevel.commonImportanceLevelIds.contains(level.getId())){
                    commonValues.add(level);
                }
            }
            En_ImportanceLevel[] result = new En_ImportanceLevel[commonValues.size()];
            return commonValues.toArray(result);
        }
    }

    public static final List<ProjectSla> DEFAULT_SLA_VALUES = makeDefaultValues();
}
