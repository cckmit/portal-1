package ru.protei.portal.core.model.dict;

import java.util.ArrayList;
import java.util.List;

public enum En_CaseCommentPrivacyType {
    PUBLIC,
    PRIVATE_CUSTOMERS,
    PRIVATE;

    private static final List<En_CaseCommentPrivacyType> simplePrivacyType;
    private static final List<En_CaseCommentPrivacyType> extendPrivacyType;
    static {
        simplePrivacyType = new ArrayList<>();
        simplePrivacyType.add(PUBLIC);
        simplePrivacyType.add(PRIVATE);

        extendPrivacyType = new ArrayList<>();
        extendPrivacyType.add(PUBLIC);
        extendPrivacyType.add(PRIVATE_CUSTOMERS);
        extendPrivacyType.add(PRIVATE);
    }

    public static List<En_CaseCommentPrivacyType> simplePrivacyType() { return simplePrivacyType; }
    public static List<En_CaseCommentPrivacyType> extendPrivacyType() { return extendPrivacyType; }
}
