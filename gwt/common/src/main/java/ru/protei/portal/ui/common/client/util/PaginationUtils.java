package ru.protei.portal.ui.common.client.util;

public class PaginationUtils {

    public static int getTotalPages( int count ) {
        int fullPages = count / PAGE_SIZE;
        int rowsOnLastPage = count - fullPages * PAGE_SIZE;
        int halfPages = rowsOnLastPage > 0 ? 1 : 0;
        return fullPages + halfPages;
    }

    public static int PAGE_SIZE = 50;
}
