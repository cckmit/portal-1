package ru.protei.portal.core.model.util;

import java.util.Arrays;
import java.util.List;

public class CrmConstants {

    public static final int DEFAULT_SELECTOR_PAGE_SIZE = 20;
    public static final int DEFAULT_SELECTOR_CHUNK_SIZE = 100;
    public static final int DEFAULT_SELECTOR_SAVED_CHUNKS = 100;
    public static final int EMAIL_MAX_SIZE = 254;
    public static final int NAME_MAX_SIZE = 1024;

    public static final float BYTES_IN_MEGABYTE = 1024 * 1024;

    public static final String SOME_LINKS_NOT_SAVED = "some links not saved";

    public static final String DEFAULT_LOCALE = "ru";

    public static final List<String> CONFIG_EXTENSIONS = Arrays.asList(".config", ".cfg", ".properties", ".xml", ".json");

    public static final List<String> PROTEI_DOMAINS = Arrays.asList("@protei.ru", "@sigurd-it.ru", "@neo-s.com");

    public interface Session {
        String AUTH_TOKEN = "session-auth-token";
        String FILE_ITEM = "file-item";
        String FILE_ITEM_PDF = "file-item-pdf";
        String FILE_ITEM_DOC = "file-item-doc";
        String FILE_ITEM_APPROVAL_SHEET = "file-item-approval-sheet";
    }

    public interface Header {
        String USER_AGENT = "User-Agent";
        String X_REAL_IP = "X-Real-IP";
    }

    public interface Product {
        Long UNDEFINED = -1L;
    }

    public interface Employee {
        Long UNDEFINED = -1L;
    }

    public interface Region {
        Long UNDEFINED = -1L;
    }

    public interface EmployeeRegistration {
        int ADDITIONAL_SOFT_MAX_LENGTH = 512;
        int RESOURCE_COMMENT_MAX_LENGTH = 512;
        int OPERATING_SYSTEM_MAX_LENGTH = 64;
        int POSITION_MAX_LENGTH = 128;
        int WORKPLACE_MAX_LENGTH = 256;
    }

    public interface Person {
        Long SYSTEM_USER_ID = 1L;
    }

    public interface TopBrassPerson {
        long PINCHUK_PERSON_ID = 29;
        long APOSTOLOVA_PERSON_ID = 4;
        long KOLOBKOV_PERSON_ID= 20;
        long FREYKMAN_PERSON_ID = 45;
        long MASLOV_PERSON_ID = 25;
    }

    public interface Issue {
        String CREATE_CONTACT_IDENTITY = "issue-edit";
        int MIN_LENGTH_FOR_SEARCH_BY_COMMENTS = 3;
    }

    public interface CaseTag {
        Long NOT_SPECIFIED = -1L;
        int NAME_MAX_LENGTH = 64;
        String DEFAULT_COLOR = "#e9edef";
        String HEX_COLOR_MASK = "^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$";
    }

    public interface Masks {
        String ALL_CHARACTERS = ".*";
        String EMAIL = "^[-a-zA-Z0-9_\\.]+@[-a-zA-Z0-9_\\.]+\\.\\w{2,4}$";
        String ONLY_DIGITS = "^\\d*$";
        String MONEY = "^(\\d+(\\s*\\d+)*([\\.,]\\d{1,2})?|.{0})$";
        String ONE_OR_MORE_SPACES = "\\s+";
        String ROUND_AND_SQUARE_BRACKETS = "[()\\[\\]]+";
        String IP = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        String COMPANY_NAME_ILLEGAL_CHARS = ".*[<>/]+.*";
        String CONTRACT_SPECIFICATION_CLAUSE = "^\\d{1,3}(\\.\\d{1,3})*$";
        String CONTRACT_NUMBER = "^.{1,50}";
        String CONTRACTOR_INN = "^(\\d{10}|\\d{12})$";
        String CONTRACTOR_KPP = "^\\d{9}$";
        String CONTRACTOR_NAME = "^.{1,100}$";
        String CONTRACTOR_FULL_NAME = "^.{1,250}$";
        String RUS_PHONE_NUMBER_PATTERN = "^((\\+7|8)[0-9]{9,10}|[0-9]{6,7})$"; // [+7 или 8] + [3 код региона] + [6-7 номер] ИЛИ [6-7 номер]
        String WORK_PHONE_NUMBER_PATTERN = "^\\d*#?\\d+$";
        String DELIVERY_KIT_SERIAL_NUMBER_PATTERN = "^\\d{3}\\.\\d{3}$";
        String CARD_BATCH_NUMBER_PATTERN = "^\\d{3}$";
        String CARD_BATCH_ARTICLE_PATTERN = "^\\d{3}\\w-\\d{2}-\\d{2}$";  // "XXXXA-XX-XX" (X-цифра, A-буква)
    }

    public interface Time {
        long SEC = 1000L;
        long MINUTE = 60 * SEC;
        long HOUR = 60 * MINUTE;
        long DAY = 24 * HOUR;
    }

    public interface ContactConstants {
        int FIRST_NAME_SIZE = 80;
        int SECOND_NAME_SIZE = 80;
        int LAST_NAME_SIZE = 80;
        int SHORT_NAME_SIZE = 128;
        int LOGIN_SIZE = 64;
    }

    public interface EquipmentConstants {
        int NAME_SIZE = 128;
    }

    public interface Comment {
        String TIME_ELAPSED_DEFAULT_COMMENT = "затраченное время";
    }

    public interface ClassifierCode {
        int MAX_SIZE = 6;
    }

    public interface RegistrationNumber {
        int MAX_SIZE = 3;
    }

    public interface Jira {
        String INFO_LINK = "jiraInfo";
        String WORKFLOW_IMAGE = "./images/jira-workflow.jpg";
        String NO_EXTENDED_PRIVACY_PROJECT = "CLM";
    }

    public interface IssueCommentHelp {
        String LINK = "#issueCommentHelp";
    }

    public interface ImportanceLevel {
        String BASIC_NAME = "basic";

        Integer CRITICAL = 1;
        Integer IMPORTANT = 2;
        Integer BASIC = 3;
        Integer COSMETIC = 4;

        List<Integer> commonImportanceLevelIds = Arrays.asList(CRITICAL,IMPORTANT,BASIC,COSMETIC);
    }

    public interface Company {
        long HOME_COMPANY_ID = 1L;
        long MAIN_HOME_COMPANY_ID = 3084L;
        String MAIN_HOME_COMPANY_NAME = "Протей";
        String PROTEI_ST_HOME_COMPANY_NAME = "Протей СТ";
        String HOME_COUNTRY_NAME = "Российская Федерация";
        String HOME_COUNTRY_SHORT_NAME = "Россия";
    }

    public interface LocaleTags {
        String RU = "ru";
        String EN = "en";
    }

    public interface LinkStart {
        String HTTP = "http://";
        String HTTPS = "https://";
    }

    public interface Redmine {
        String NO_CONTENT_TYPE = "application/octet-stream";
    }

    public interface IpReservation {
        String SUBNET_ADDRESS = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
        String IP_ADDRESS = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
        String MAC_ADDRESS = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
        String NUMBER = "^\\d{1,3}$";

        String SUBNET_MASK = "0/24";

        int MIN_IPS_COUNT = 1;
        int MAX_IPS_COUNT = 255;

        int RELEASE_DATE_EXPIRES_IN_DAYS =  3;
    }

    public interface Youtrack {
        String REQUEST_TYPE_VALUE = "Удаление пользователей";
    }

    public interface State {
        String CREATED_NAME = "created";
        long CREATED = 1;
        long OPENED = 2;
        long WORKAROUND = 30;
        long TEST_CUST = 20;
        long DONE = 17;
        long VERIFIED = 5;
        long PAUSED = 4;
        long CANCELED = 33;
        long ACTIVE = 16;
        long TEST_LOCAL = 19;
        long INFO_REQUEST = 31;
        long NX_REQUEST = 35;
        long CUST_REQUEST = 36;
        long CUST_PENDING = 34;
        long CLOSED = 3;
        long IGNORED = 10;
        long SOLVED_NOT_A_PROBLEM = 7;
        long SOLVED_FIXED = 8;
        long SOLVED_DUPLICATED = 9;
        long BLOCKED = 38;
        long DEVELOPMENT = 26;
        long UNKNOWN = 22;
        long PRESALE = 24;
        long FINISHED = 32;
        long PRELIMINARY = 39;
        long COMMERCIAL_NEGOTIATIONS = 51;
    }

    public interface Department {
        Long CONTRACT = 384L;
    }

    public interface Style {
        String ADDED = "added";
        String UPDATED = "updated";
        String HIDE = "hide";
    }

    public interface Platform {
        int PARAMETERS_MAX_LENGTH = 256;
    }

    public interface AutoOpen {
        long NO_DELAY = 0;
        long DELAY_STARTUP = 60;
        long DELAY_RUNTIME = 180;
        int DELAY_RANDOM = 120;
    }

    public interface NightWork {
        Integer START_NIGHT = 21;
    }

    public interface UitsState {
        String NEW = "NEW"; //Новый (не рассмотрен)
        String EXECUTING = "EXECUTING"; //В работе
        String REPORT_PREPARE = "1"; //Подготовка отчета
        String FEEDBACK_EXPECT = "2"; //Ожидание обратной связи
        String WON = "WON"; //Решено (Услуги оказаны)
        String LOSE = "LOSE"; //Отклонено (отказ)
    }
}
