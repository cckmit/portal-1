package ru.protei.portal.core.model.util;

import java.util.List;

import static java.util.Arrays.asList;
import static ru.protei.portal.core.model.util.CrmConstants.State.*;

public class ContractStateUtil {

    public static final List<Long> OPENED_CONTRACT_STATES = asList(
            HAVE_AN_ORIGINAL,
            COPIES_SEND_TO_CUSTOMER,
            WAITING_FOR_COPIES_FROM_CUSTOMER,
            WAITING_FOR_ORIGINAL,
            SIGNED_ON_SITE,
            EDS_SIGNED
    );
}
