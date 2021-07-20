package ru.protei.portal.test.utils;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.ReportYtWorkInfo;
import ru.protei.portal.core.model.struct.ReportYtWorkItem;
import ru.protei.portal.core.report.ytwork.ReportYtWorkCollector;

import java.util.*;
import java.util.stream.Stream;

public class ReportYtWorkCollectorTest {
    static private final String homeCompanyName = "homeCompany";
    static private final String customerCompanyName = "customerCompany";

    @Test
    public void testNiokrNmaReport() {
        Map<String, List<String>> niokrs = new HashMap<>();
        String niokrProject = "niokrProject1";
        niokrs.put(niokrProject, Arrays.asList("niokr1", "niokr2"));

        Map<String, List<String>> nmas = new HashMap<>();
        String nmaProject = "nmaProject1";
        nmas.put(nmaProject, Arrays.asList("nma1", "nma2"));

        String userEmail1 = "user1@protei.ru";
        long niokrTime = 30L;
        long nmaTime = 48L;

        Person user1 = new Person();
        user1.setDisplayName(userEmail1);
        HashMap<String, Person> persons = new HashMap<>();
        persons.put(userEmail1, user1);

        Set<String> homeCompany = new HashSet<>(Arrays.asList(homeCompanyName));

        ReportYtWorkInfo info1 = new ReportYtWorkInfo(userEmail1, "niokrIssue1", homeCompanyName, niokrTime, niokrProject);
        ReportYtWorkInfo info2 = new ReportYtWorkInfo(userEmail1, "nmaIssue1", homeCompanyName, nmaTime, nmaProject);

        ReportYtWorkCollector collector = new ReportYtWorkCollector(
                niokrs, nmas,
                (name, project) -> new ArrayList<>(),
                persons::get,
                new Date(), homeCompany
        );

        List<ReportYtWorkItem> data = Stream.of(info1, info2).collect(collector);

        Assert.assertEquals(1, data.size());
        ReportYtWorkItem ytWorkItem = data.get(0);

        Assert.assertEquals(userEmail1, ytWorkItem.getPerson().getDisplayName());
        Assert.assertEquals(niokrTime + nmaTime, ytWorkItem.getAllTimeSpent().longValue());
        int niokrSize = niokrs.get(niokrProject).size();
        Assert.assertEquals(2, ytWorkItem.getNiokrSpentTime().size());
        Assert.assertEquals(niokrTime / niokrSize, ytWorkItem.getNiokrSpentTime().get("niokr1").longValue());
        Assert.assertEquals(niokrTime / niokrSize, ytWorkItem.getNiokrSpentTime().get("niokr2").longValue());
        int nmaSize = nmas.get(nmaProject).size();
        Assert.assertEquals(2, ytWorkItem.getNmaSpentTime().size());
        Assert.assertEquals(nmaTime / nmaSize, ytWorkItem.getNmaSpentTime().get("nma1").longValue());
        Assert.assertEquals(nmaTime / nmaSize, ytWorkItem.getNmaSpentTime().get("nma2").longValue());
        Assert.assertEquals(0, ytWorkItem.getContractSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getGuaranteeSpentTime().size());
    }

    @Test
    public void testNiokrsReport() {
        Map<String, List<String>> niokrs = new HashMap<>();
        String niokrProject1 = "niokrProject1";
        niokrs.put(niokrProject1, Arrays.asList("niokr1", "niokr2"));

        String niokrProject2 = "niokrProject2";
        niokrs.put(niokrProject2, Arrays.asList("niokr1", "niokr3"));

        Map<String, List<String>> nmas = new HashMap<>();

        String userEmail1 = "user1@protei.ru";
        long niokrTime1 = 30L;
        long niokrTime2 = 48L;

        Person user1 = new Person();
        user1.setDisplayName(userEmail1);
        HashMap<String, Person> persons = new HashMap<>();
        persons.put(userEmail1, user1);

        Set<String> homeCompany = new HashSet<>(Arrays.asList(homeCompanyName));

        ReportYtWorkInfo info1 = new ReportYtWorkInfo(userEmail1, "niokrIssue1", homeCompanyName, niokrTime1, niokrProject1);
        ReportYtWorkInfo info2 = new ReportYtWorkInfo(userEmail1, "niokrIssue2", homeCompanyName, niokrTime2, niokrProject2);

        ReportYtWorkCollector collector = new ReportYtWorkCollector(
                niokrs, nmas,
                (name, project) -> new ArrayList<>(),
                persons::get,
                new Date(), homeCompany
        );

        List<ReportYtWorkItem> data = Stream.of(info1, info2).collect(collector);

        Assert.assertEquals(1, data.size());
        ReportYtWorkItem ytWorkItem = data.get(0);

        Assert.assertEquals(userEmail1, ytWorkItem.getPerson().getDisplayName());
        Assert.assertEquals(niokrTime1 + niokrTime2, ytWorkItem.getAllTimeSpent().longValue());
        int niokrSize1 = niokrs.get(niokrProject1).size();
        int niokrSize2 = niokrs.get(niokrProject2).size();
        Assert.assertEquals(3, ytWorkItem.getNiokrSpentTime().size());
        Assert.assertEquals(niokrTime1 / niokrSize1 + niokrTime2 / niokrSize2, ytWorkItem.getNiokrSpentTime().get("niokr1").longValue());
        Assert.assertEquals(niokrTime1 / niokrSize1, ytWorkItem.getNiokrSpentTime().get("niokr2").longValue());
        Assert.assertEquals(niokrTime2 / niokrSize2, ytWorkItem.getNiokrSpentTime().get("niokr3").longValue());
        Assert.assertEquals(0, ytWorkItem.getNmaSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getContractSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getGuaranteeSpentTime().size());
    }

    @Test
    public void testContractGuaranteeReport() {
        Map<String, List<String>> niokrs = new HashMap<>();
        Map<String, List<String>> nmas = new HashMap<>();

        String userEmail1 = "user1@protei.ru";
        long contractTime = 30L;
        long guaranteeTime = 48L;

        Person user1 = new Person();
        user1.setDisplayName(userEmail1);
        HashMap<String, Person> persons = new HashMap<>();
        persons.put(userEmail1, user1);

        Set<String> homeCompany = new HashSet<>(Arrays.asList(homeCompanyName));

        String contractProject = "contractProject1";
        String guaranteeProject = "guaranteeProject1";

        Contract contract = new Contract();
        contract.setNumber(contractProject);
        contract.setDateValid(new GregorianCalendar(2021, Calendar.JULY, 20, 0, 0, 0).getTime());

        Contract guarantee = new Contract();
        guarantee.setNumber(guaranteeProject);
        guarantee.setDateValid(new GregorianCalendar(2021, Calendar.JULY, 18, 0, 0, 0).getTime());

        Map<String, List<Contract>> contracts = new HashMap<>();
        contracts.put(customerCompanyName + "#" + contractProject, Arrays.asList(contract));
        contracts.put(customerCompanyName + "#" + guaranteeProject, Arrays.asList(guarantee));

        ReportYtWorkInfo info1 = new ReportYtWorkInfo(userEmail1, "contractIssue1", customerCompanyName, contractTime, contractProject);
        ReportYtWorkInfo info2 = new ReportYtWorkInfo(userEmail1, "guaranteeIssue1", customerCompanyName, guaranteeTime, guaranteeProject);

        ReportYtWorkCollector collector = new ReportYtWorkCollector(
                niokrs, nmas,
                (name, project) -> contracts.get(name + "#" + project),
                persons::get,
                new GregorianCalendar(2021, Calendar.JULY, 19, 0, 0, 0).getTime(),
                homeCompany
        );

        List<ReportYtWorkItem> data = Stream.of(info1, info2).collect(collector);

        Assert.assertEquals(1, data.size());
        ReportYtWorkItem ytWorkItem = data.get(0);

        Assert.assertEquals(userEmail1, ytWorkItem.getPerson().getDisplayName());
        Assert.assertEquals(contractTime + guaranteeTime, ytWorkItem.getAllTimeSpent().longValue());

        Assert.assertEquals(0, ytWorkItem.getNiokrSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getNmaSpentTime().size());

        Assert.assertEquals(1, ytWorkItem.getContractSpentTime().size());
        Assert.assertEquals(contractTime, ytWorkItem.getContractSpentTime().get(contractProject).longValue());

        Assert.assertEquals(1, ytWorkItem.getGuaranteeSpentTime().size());
        Assert.assertEquals(guaranteeTime, ytWorkItem.getGuaranteeSpentTime().get(guaranteeProject).longValue());
    }

    @Test
    public void testContractsReport() {
        Map<String, List<String>> niokrs = new HashMap<>();
        Map<String, List<String>> nmas = new HashMap<>();

        String userEmail1 = "user1@protei.ru";
        long contractTime1 = 30L;
        long contractTime2 = 36L;
        long contractTime3 = 43L;

        Person user1 = new Person();
        user1.setDisplayName(userEmail1);
        HashMap<String, Person> persons = new HashMap<>();
        persons.put(userEmail1, user1);

        Set<String> homeCompany = new HashSet<>(Arrays.asList(homeCompanyName));

        String contractProject1 = "contractProject1";
        String contractProject2 = "contractProject2";

        Contract contract1 = new Contract();
        contract1.setNumber("contract1");
        contract1.setDateValid(new GregorianCalendar(2021, Calendar.JULY, 20, 1, 0, 0).getTime());

        Contract contract2 = new Contract();
        contract2.setNumber("contract2");
        contract2.setDateValid(new GregorianCalendar(2021, Calendar.JULY, 20, 2, 0, 0).getTime());

        Contract guarantee = new Contract();
        guarantee.setNumber("guarantee");
        guarantee.setDateValid(new GregorianCalendar(2021, Calendar.JULY, 18, 3, 0, 0).getTime());

        Map<String, List<Contract>> contracts = new HashMap<>();
        contracts.put(customerCompanyName + "#" + contractProject1, Arrays.asList(contract1));
        contracts.put(customerCompanyName + "#" + contractProject2, Arrays.asList(contract2, guarantee));

        ReportYtWorkInfo info1 = new ReportYtWorkInfo(userEmail1, "contractIssue1", customerCompanyName, contractTime1, contractProject1);
        ReportYtWorkInfo info2 = new ReportYtWorkInfo(userEmail1, "contractIssue2", customerCompanyName, contractTime2, contractProject2);
        ReportYtWorkInfo info3 = new ReportYtWorkInfo(userEmail1, "contractIssue3", customerCompanyName, contractTime3, contractProject1);

        ReportYtWorkCollector collector = new ReportYtWorkCollector(
                niokrs, nmas,
                (name, project) -> contracts.get(name + "#" + project),
                persons::get,
                new GregorianCalendar(2021, Calendar.JULY, 19, 0, 0, 0).getTime(),
                homeCompany
        );

        List<ReportYtWorkItem> data = Stream.of(info1, info2, info3).collect(collector);

        Assert.assertEquals(1, data.size());
        ReportYtWorkItem ytWorkItem = data.get(0);

        Assert.assertEquals(userEmail1, ytWorkItem.getPerson().getDisplayName());
        Assert.assertEquals(contractTime1 + contractTime2 + contractTime3, ytWorkItem.getAllTimeSpent().longValue());

        Assert.assertEquals(0, ytWorkItem.getNiokrSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getNmaSpentTime().size());

        Assert.assertEquals(2, ytWorkItem.getContractSpentTime().size());
        Assert.assertEquals(contractTime1 + contractTime3, ytWorkItem.getContractSpentTime().get("contract1").longValue());
        Assert.assertEquals(contractTime2 , ytWorkItem.getContractSpentTime().get("contract2").longValue());

        Assert.assertEquals(0, ytWorkItem.getGuaranteeSpentTime().size());
    }

    @Test
    public void testUsersReport() {
        Map<String, List<String>> niokrs = new HashMap<>();
        String niokrProject = "niokrProject1";
        niokrs.put(niokrProject, Arrays.asList("niokr1", "niokr2"));

        Map<String, List<String>> nmas = new HashMap<>();
        String nmaProject = "nmaProject1";
        nmas.put(nmaProject, Arrays.asList("nma1", "nma2"));

        String userEmail1 = "user1@protei.ru";
        String userEmail2 = "user2@protei.ru";
        long niokrTime = 30L;
        long nmaTime = 48L;

        Person user1 = new Person();
        user1.setId(1L);
        user1.setDisplayName(userEmail1);

        Person user2 = new Person();
        user2.setId(2L);
        user2.setDisplayName(userEmail2);

        HashMap<String, Person> persons = new HashMap<>();
        persons.put(userEmail1, user1);
        persons.put(userEmail2, user2);

        Set<String> homeCompany = new HashSet<>(Arrays.asList(homeCompanyName));

        ReportYtWorkInfo info1 = new ReportYtWorkInfo(userEmail1, "niokrIssue1", homeCompanyName, niokrTime, niokrProject);
        ReportYtWorkInfo info2 = new ReportYtWorkInfo(userEmail2, "nmaIssue1", homeCompanyName, nmaTime, nmaProject);

        ReportYtWorkCollector collector = new ReportYtWorkCollector(
                niokrs, nmas,
                (name, project) -> new ArrayList<>(),
                persons::get,
                new Date(), homeCompany
        );

        List<ReportYtWorkItem> data = Stream.of(info1, info2).collect(collector);
        data.sort(Comparator.comparing((ReportYtWorkItem item) -> item.getPerson().getId()));

        Assert.assertEquals(2, data.size());
        ReportYtWorkItem ytWorkItem1 = data.get(0);

        Assert.assertEquals(userEmail1, ytWorkItem1.getPerson().getDisplayName());
        Assert.assertEquals(niokrTime, ytWorkItem1.getAllTimeSpent().longValue());
        int niokrSize = niokrs.get(niokrProject).size();
        Assert.assertEquals(2, ytWorkItem1.getNiokrSpentTime().size());
        Assert.assertEquals(niokrTime / niokrSize, ytWorkItem1.getNiokrSpentTime().get("niokr1").longValue());
        Assert.assertEquals(niokrTime / niokrSize, ytWorkItem1.getNiokrSpentTime().get("niokr2").longValue());
        Assert.assertEquals(0, ytWorkItem1.getNmaSpentTime().size());
        Assert.assertEquals(0, ytWorkItem1.getContractSpentTime().size());
        Assert.assertEquals(0, ytWorkItem1.getGuaranteeSpentTime().size());

        ReportYtWorkItem ytWorkItem2 = data.get(1);
        Assert.assertEquals(userEmail2, ytWorkItem2.getPerson().getDisplayName());
        Assert.assertEquals(nmaTime, ytWorkItem2.getAllTimeSpent().longValue());
        int nmaSize = niokrs.get(niokrProject).size();
        Assert.assertEquals(0, ytWorkItem2.getNiokrSpentTime().size());
        Assert.assertEquals(2, ytWorkItem2.getNmaSpentTime().size());
        Assert.assertEquals(nmaTime / nmaSize, ytWorkItem2.getNmaSpentTime().get("nma1").longValue());
        Assert.assertEquals(nmaTime / nmaSize, ytWorkItem2.getNmaSpentTime().get("nma2").longValue());
        Assert.assertEquals(0, ytWorkItem2.getContractSpentTime().size());
        Assert.assertEquals(0, ytWorkItem2.getGuaranteeSpentTime().size());
    }
}
