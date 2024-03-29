package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Book;
import ru.protei.portal.core.model.struct.Pair;

import java.util.List;

public interface BookDAO extends PortalBaseDAO<Book>{

    List<Pair<String, Integer>> getCountBookByAuthor();
}
