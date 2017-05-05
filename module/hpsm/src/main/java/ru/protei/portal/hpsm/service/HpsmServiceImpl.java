package ru.protei.portal.hpsm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.hpsm.config.HpsmEnvConfig;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * Created by michael on 27.04.17.
 */
public class HpsmServiceImpl implements HpsmService {

    private static Logger logger = LoggerFactory.getLogger(HpsmService.class);

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    HpsmEnvConfig config;

    private HashMap<String,Company> branchToCompanyIdMap;

    public HpsmServiceImpl() {
        this.branchToCompanyIdMap = new HashMap<>();
    }

    @PostConstruct
    private void postConstruct () {

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

    @Override
    public Company getCompanyByBranchName(String branchName) {
        return branchName == null ? null : branchToCompanyIdMap.get(branchKey(branchName));
    }
}
