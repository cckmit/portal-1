package ru.protei.portal.test.utils;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkClassificationError;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkInfo;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem;
import ru.protei.portal.core.report.ytwork.ReportYtWorkCollector;

import java.util.*;
import java.util.stream.Stream;

public class ReportYoutrackWorkCollectorTest {
    static private final String HOME_COMPANY_NAME = "homeCompany";
    static private final String CUSTOMER_COMPANY_NAME = "customerCompany";

    @Test
    public void testEmpty() {
        Map<String, List<String>> niokrs = new HashMap<>();
        String niokrProject = "niokrProject1";
        niokrs.put(niokrProject, Arrays.asList("niokr1", "niokr2"));

        Map<String, List<String>> nmas = new HashMap<>();
        String nmaProject = "nmaProject1";
        nmas.put(nmaProject, Arrays.asList("nma1", "nma2"));

        Set<String> homeCompany = new HashSet<>(Arrays.asList(HOME_COMPANY_NAME));

        ReportYtWorkCollector collector = new ReportYtWorkCollector(
                niokrs, nmas,
                name -> new ArrayList<>(),
                new Date(), homeCompany
        );

        ReportYtWorkCollector.ErrorsAndItems result = Stream.<ReportYtWorkInfo>empty().collect(collector);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getItems().size());
        Assert.assertEquals(0, result.getErrors().size());
    }

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

        Set<String> homeCompany = new HashSet<>(Arrays.asList(HOME_COMPANY_NAME));

        ReportYtWorkInfo info1 = new ReportYtWorkInfo(userEmail1, "niokrIssue1", HOME_COMPANY_NAME, niokrTime, niokrProject);
        ReportYtWorkInfo info2 = new ReportYtWorkInfo(userEmail1, "nmaIssue1", HOME_COMPANY_NAME, nmaTime, nmaProject);

        ReportYtWorkCollector collector = new ReportYtWorkCollector(
                niokrs, nmas,
                name -> new ArrayList<>(),
                new Date(), homeCompany
        );

        Map<String, ReportYtWorkRowItem> data = Stream.of(info1, info2).collect(collector).getItems();

        Assert.assertEquals(1, data.size());
        ReportYtWorkRowItem ytWorkItem = data.get(userEmail1);

        Assert.assertNotNull(ytWorkItem);
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

        Set<String> homeCompany = new HashSet<>(Arrays.asList(HOME_COMPANY_NAME));

        ReportYtWorkInfo info1 = new ReportYtWorkInfo(userEmail1, "niokrIssue1", HOME_COMPANY_NAME, niokrTime1, niokrProject1);
        ReportYtWorkInfo info2 = new ReportYtWorkInfo(userEmail1, "niokrIssue2", HOME_COMPANY_NAME, niokrTime2, niokrProject2);

        ReportYtWorkCollector collector = new ReportYtWorkCollector(
                niokrs, nmas,
                name -> new ArrayList<>(),
                new Date(), homeCompany
        );

        Map<String, ReportYtWorkRowItem> data = Stream.of(info1, info2).collect(collector).getItems();

        Assert.assertEquals(1, data.size());
        ReportYtWorkRowItem ytWorkItem = data.get(userEmail1);

        Assert.assertNotNull(ytWorkItem);
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

        Set<String> homeCompany = new HashSet<>(Arrays.asList(HOME_COMPANY_NAME));

        String contractProject = "contractProject1";
        String guaranteeProject = "guaranteeProject1";

        Contract contract = new Contract();
        contract.setNumber(contractProject);
        contract.setDateValid(new GregorianCalendar(2021, Calendar.JULY, 20, 0, 0, 0).getTime());

        Contract guarantee = new Contract();
        guarantee.setNumber(guaranteeProject);
        guarantee.setDateValid(new GregorianCalendar(2021, Calendar.JULY, 18, 0, 0, 0).getTime());

        Map<String, List<Contract>> contracts = new HashMap<>();
        contracts.put(CUSTOMER_COMPANY_NAME, Arrays.asList(contract, guarantee));

        ReportYtWorkInfo info1 = new ReportYtWorkInfo(userEmail1, "contractIssue1", CUSTOMER_COMPANY_NAME, contractTime, contractProject);
        ReportYtWorkInfo info2 = new ReportYtWorkInfo(userEmail1, "guaranteeIssue1", CUSTOMER_COMPANY_NAME, guaranteeTime, guaranteeProject);

        ReportYtWorkCollector collector = new ReportYtWorkCollector(
                niokrs, nmas,
                contracts::get,
                new GregorianCalendar(2021, Calendar.JULY, 19, 0, 0, 0).getTime(),
                homeCompany
        );

        Map<String, ReportYtWorkRowItem> data = Stream.of(info1, info2).collect(collector).getItems();

        Assert.assertEquals(1, data.size());
        ReportYtWorkRowItem ytWorkItem = data.get(userEmail1);

        Assert.assertNotNull(ytWorkItem);
        Assert.assertEquals(contractTime + guaranteeTime, ytWorkItem.getAllTimeSpent().longValue());

        Assert.assertEquals(0, ytWorkItem.getNiokrSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getNmaSpentTime().size());

        Assert.assertEquals(1, ytWorkItem.getContractSpentTime().size());
        Assert.assertEquals(contractTime + guaranteeTime, ytWorkItem.getContractSpentTime().get(contractProject).longValue());

        Assert.assertEquals(0, ytWorkItem.getGuaranteeSpentTime().size());
    }

    @Test
    public void testGuarantyReport() {
        Map<String, List<String>> niokrs = new HashMap<>();
        Map<String, List<String>> nmas = new HashMap<>();

        String userEmail1 = "user1@protei.ru";
        long guaranteeTime1 = 30L;
        long guaranteeTime2 = 43L;

        Set<String> homeCompany = new HashSet<>(Arrays.asList(HOME_COMPANY_NAME));

        String guaranteeProject1 = "guaranteeProject1";

        Contract guarantee1 = new Contract();
        guarantee1.setNumber("guarantee1");
        guarantee1.setDateValid(new GregorianCalendar(2021, Calendar.JULY, 17, 1, 0, 0).getTime());

        Contract guarantee2 = new Contract();
        guarantee2.setNumber("guarantee2");
        guarantee2.setDateValid(new GregorianCalendar(2021, Calendar.JULY, 18, 3, 0, 0).getTime());

        Map<String, List<Contract>> contracts = new HashMap<>();
        contracts.put(CUSTOMER_COMPANY_NAME, Arrays.asList(guarantee1, guarantee2));

        ReportYtWorkInfo info1 = new ReportYtWorkInfo(userEmail1, "guaranteeIssue1", CUSTOMER_COMPANY_NAME, guaranteeTime1, guaranteeProject1);
        ReportYtWorkInfo info3 = new ReportYtWorkInfo(userEmail1, "guaranteeIssue2", CUSTOMER_COMPANY_NAME, guaranteeTime2, guaranteeProject1);

        ReportYtWorkCollector collector = new ReportYtWorkCollector(
                niokrs, nmas,
                contracts::get,
                new GregorianCalendar(2021, Calendar.JULY, 19, 0, 0, 0).getTime(),
                homeCompany
        );

        Map<String, ReportYtWorkRowItem> data = Stream.of(info1, info3).collect(collector).getItems();

        Assert.assertEquals(1, data.size());
        ReportYtWorkRowItem ytWorkItem = data.get(userEmail1);

        Assert.assertNotNull(ytWorkItem);
        Assert.assertEquals(guaranteeTime1 + guaranteeTime2, ytWorkItem.getAllTimeSpent().longValue());

        Assert.assertEquals(0, ytWorkItem.getNiokrSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getNmaSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getContractSpentTime().size());

        Assert.assertEquals(2, ytWorkItem.getGuaranteeSpentTime().size());
        Assert.assertEquals((guaranteeTime1+guaranteeTime2) / 2, ytWorkItem.getGuaranteeSpentTime().get("guarantee1").longValue());
        Assert.assertEquals((guaranteeTime1+guaranteeTime2) / 2, ytWorkItem.getGuaranteeSpentTime().get("guarantee2").longValue());
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

        Set<String> homeCompany = new HashSet<>(Arrays.asList(HOME_COMPANY_NAME));

        ReportYtWorkInfo info1 = new ReportYtWorkInfo(userEmail1, "niokrIssue1", HOME_COMPANY_NAME, niokrTime, niokrProject);
        ReportYtWorkInfo info2 = new ReportYtWorkInfo(userEmail2, "nmaIssue1", HOME_COMPANY_NAME, nmaTime, nmaProject);

        ReportYtWorkCollector collector = new ReportYtWorkCollector(
                niokrs, nmas,
                name -> new ArrayList<>(),
                new Date(), homeCompany
        );

        Map<String, ReportYtWorkRowItem> data = Stream.of(info1, info2).collect(collector).getItems();

        Assert.assertEquals(2, data.size());
        ReportYtWorkRowItem ytWorkItem1 = data.get(userEmail1);

        Assert.assertNotNull(ytWorkItem1);
        Assert.assertEquals(niokrTime, ytWorkItem1.getAllTimeSpent().longValue());
        int niokrSize = niokrs.get(niokrProject).size();
        Assert.assertEquals(2, ytWorkItem1.getNiokrSpentTime().size());
        Assert.assertEquals(niokrTime / niokrSize, ytWorkItem1.getNiokrSpentTime().get("niokr1").longValue());
        Assert.assertEquals(niokrTime / niokrSize, ytWorkItem1.getNiokrSpentTime().get("niokr2").longValue());
        Assert.assertEquals(0, ytWorkItem1.getNmaSpentTime().size());
        Assert.assertEquals(0, ytWorkItem1.getContractSpentTime().size());
        Assert.assertEquals(0, ytWorkItem1.getGuaranteeSpentTime().size());

        ReportYtWorkRowItem ytWorkItem2 = data.get(userEmail2);
        Assert.assertNotNull(ytWorkItem1);
        Assert.assertEquals(nmaTime, ytWorkItem2.getAllTimeSpent().longValue());
        int nmaSize = niokrs.get(niokrProject).size();
        Assert.assertEquals(0, ytWorkItem2.getNiokrSpentTime().size());
        Assert.assertEquals(2, ytWorkItem2.getNmaSpentTime().size());
        Assert.assertEquals(nmaTime / nmaSize, ytWorkItem2.getNmaSpentTime().get("nma1").longValue());
        Assert.assertEquals(nmaTime / nmaSize, ytWorkItem2.getNmaSpentTime().get("nma2").longValue());
        Assert.assertEquals(0, ytWorkItem2.getContractSpentTime().size());
        Assert.assertEquals(0, ytWorkItem2.getGuaranteeSpentTime().size());
    }

    @Test
    public void testErrorClassificationReport() {
        Map<String, List<String>> niokrs = new HashMap<>();
        Map<String, List<String>> nmas = new HashMap<>();

        String userEmail1 = "user1@protei.ru";
        String niokrIssue = "niokrIssue1";
        long niokrTime = 30L;
        String nmaIssue = "nmaIssue1";
        long contractTime = 48L;

        Set<String> homeCompany = new HashSet<>(Arrays.asList(HOME_COMPANY_NAME));
        ReportYtWorkInfo info1 = new ReportYtWorkInfo(userEmail1, niokrIssue, HOME_COMPANY_NAME, niokrTime, "niokrProject");
        ReportYtWorkInfo info2 = new ReportYtWorkInfo(userEmail1, nmaIssue, HOME_COMPANY_NAME, contractTime, "contractProject");

        ReportYtWorkCollector collector = new ReportYtWorkCollector(
                niokrs, nmas,
                name -> new ArrayList<>(),
                new Date(), homeCompany
        );

        Set<ReportYtWorkClassificationError> errors = Stream.of(info1, info2).collect(collector).getErrors();

        Assert.assertEquals(2, errors.size());
        Assert.assertTrue(errors.contains(new ReportYtWorkClassificationError(niokrIssue)));
        Assert.assertTrue(errors.contains(new ReportYtWorkClassificationError(nmaIssue)));
    }

}
