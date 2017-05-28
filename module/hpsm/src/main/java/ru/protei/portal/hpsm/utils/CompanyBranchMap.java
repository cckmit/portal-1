package ru.protei.portal.hpsm.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.hpsm.config.HpsmEnvConfig;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * Created by michael on 15.05.17.
 */
public class CompanyBranchMap {

    private static Logger logger = LoggerFactory.getLogger(CompanyBranchMap.class);

    @Autowired
    private HpsmEnvConfig config;

    @Autowired
    CompanyDAO companyDAO;

    private HashMap<String,Company> branchToCompanyIdMap;



    public CompanyBranchMap () {
        this.branchToCompanyIdMap = new HashMap<>();
    }

    @PostConstruct
    private void init () {
        config.getCompanyMapEntries().forEach(e -> addCompany(e.getBranchName(), e.getCompanyId()));
//        addCompany("TT Mobile", 230L);
//        addCompany("Аквафон GSM", 766L);
//        addCompany("Дальневосточный Филиал", 546L);
//        addCompany("Кавказский Филиал", 2366L);
//        addCompany("Поволжский Филиал", 151L);
//
//        addCompany("Северо-Западный Филиал", 231L);
//        addCompany("Столичный Филиал", 550L);
//        addCompany("Сибирский Филиал", 553L);
//        addCompany("Уральский Филиал", 549L);
//        addCompany("Центральный Филиал", 260L);
    }

    private String branchKey (String branchName) {
        return branchName.toUpperCase();
    }

    private void addCompany (String branchName, Long companyId) {

        Company company = companyDAO.get(companyId);
        if (company == null) {
            logger.error("Company with id {} not found", companyId);
            return;
        }

        this.branchToCompanyIdMap.put(branchKey(branchName), company);
    }


    public Company getCompanyByBranch (String branchName) {
        return branchName == null ? null : branchToCompanyIdMap.get(branchKey(branchName));
    }

}
