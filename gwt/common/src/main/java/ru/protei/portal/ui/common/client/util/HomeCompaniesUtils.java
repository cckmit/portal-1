package ru.protei.portal.ui.common.client.util;

import java.util.LinkedList;
import java.util.List;

public class HomeCompaniesUtils {
    private static List<Long> homeCompanyIds = new LinkedList<>();

    public static boolean isHomeCompany(Long companyId) {
        return homeCompanyIds.contains(companyId);
    }

    public static void setHomeCompanyIds(List<Long> homeCompanyIds) {
        if (homeCompanyIds == null) {
            return;
        }

        HomeCompaniesUtils.homeCompanyIds.clear();
        HomeCompaniesUtils.homeCompanyIds.addAll(homeCompanyIds);
    }
}
