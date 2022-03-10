package ru.protei.portal.core.model.util;

import java.util.List;

import static java.util.Arrays.asList;
import static ru.protei.portal.core.model.util.CrmConstants.State.*;

public class ContractStateUtil {

    public static final List<Long> CLOSED_CONTRACT_STATES = asList(AGREEMENT, CLOSED);
}
