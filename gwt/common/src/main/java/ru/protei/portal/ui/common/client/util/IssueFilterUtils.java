package ru.protei.portal.ui.common.client.util;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;

/**
 * Утилита по работе с пользовательскими фильтрами
 */
public class IssueFilterUtils {

    public static final RegExp caseNumbersPattern = RegExp.compile("(\\d+,?\\s?)+");

    public static List<Long> searchCaseNumber( String searchString, boolean searchByComments ) {
        if (isBlank( searchString ) || searchByComments) {
            return null;
        }

        MatchResult result = caseNumbersPattern.exec( searchString );
        if (result != null && result.getGroup( 0 ).equals( searchString )) {
            return Arrays.stream( searchString.split( "," ) )
                    .map( cn -> Long.parseLong( cn.trim() ) )
                    .collect( Collectors.toList() );
        }

        return null;

    }
}
