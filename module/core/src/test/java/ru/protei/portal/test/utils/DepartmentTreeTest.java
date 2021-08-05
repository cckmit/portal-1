package ru.protei.portal.test.utils;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.model.struct.reportytwork.DepartmentTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem.NameWithId;
import static ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem.PersonInfo;

public class DepartmentTreeTest {
    @Test
    public void oneNodeWithOutParent() {
        DepartmentTree<Integer> tree = new DepartmentTree<>();
        NameWithId dep1 = new NameWithId("dep1", 1L);

        tree.addNode(PersonInfo.nullDepartmentParentName, dep1, 1);

        List<DepartmentTree.Node<Integer>> data = new ArrayList<>();
        tree.deepFirstSearchTraversal(data::add);

        Assert.assertEquals(1, data.size());

        DepartmentTree.Node<Integer> node = data.get(0);
        Assert.assertEquals(DepartmentTree.rootName, node.getParent().getNameWithId());
        Assert.assertEquals(dep1, node.getNameWithId());
        Assert.assertEquals(0 , node.getLevel());

        List<Integer> value = node.getValue();
        Assert.assertEquals(1, value.size());
        Assert.assertEquals(1, (int)value.get(0));
    }

    @Test
    public void twoNodeWithOutParent() {
        DepartmentTree<Integer> tree = new DepartmentTree<>();
        NameWithId dep1 = new NameWithId("dep1", 1L);
        NameWithId dep2 = new NameWithId("dep2", 2L);

        tree.addNode(PersonInfo.nullDepartmentParentName, dep1, 1);
        tree.addNode(PersonInfo.nullDepartmentParentName, dep2, 2);

        Map<NameWithId, DepartmentTree.Node<Integer>> data = new HashMap<>();
        tree.deepFirstSearchTraversal(node -> data.put(node.getNameWithId(), node));

        Assert.assertEquals(2, data.size());

        DepartmentTree.Node<Integer> node = data.get(dep1);
        Assert.assertEquals(node.getParent().getNameWithId(), DepartmentTree.rootName);
        Assert.assertEquals(node.getNameWithId(), dep1);
        Assert.assertEquals(node.getLevel(), 0);

        List<Integer> value = node.getValue();
        Assert.assertEquals(1, value.size());
        Assert.assertEquals(1, (int)value.get(0));

        node = data.get(dep2);
        Assert.assertEquals(node.getParent().getNameWithId(), DepartmentTree.rootName);
        Assert.assertEquals(node.getNameWithId(), dep2);
        Assert.assertEquals(node.getLevel(), 0);

        value = node.getValue();
        Assert.assertEquals(1, value.size());
        Assert.assertEquals(2, (int)value.get(0));
    }

    @Test
    public void oneNodeWithParent() {
        DepartmentTree<Integer> tree = new DepartmentTree<>();
        NameWithId depParent1 = new NameWithId("depParent1", 1L);
        NameWithId dep1 = new NameWithId("dep1", 11L);

        tree.addNode(depParent1, dep1, 1);

        Map<NameWithId, DepartmentTree.Node<Integer>> data = new HashMap<>();
        tree.deepFirstSearchTraversal(node -> data.put(node.getNameWithId(), node));

        Assert.assertEquals(2, data.size());

        DepartmentTree.Node<Integer> node = data.get(depParent1);
        Assert.assertEquals(node.getParent().getNameWithId(), DepartmentTree.rootName);
        Assert.assertEquals(node.getNameWithId(), depParent1);
        Assert.assertEquals(node.getLevel(), 0);

        node = data.get(dep1);
        Assert.assertEquals(node.getParent().getNameWithId(), depParent1);
        Assert.assertEquals(node.getNameWithId(), dep1);
        Assert.assertEquals(node.getLevel(), 1);

        List<Integer> value = node.getValue();
        Assert.assertEquals(1, value.size());
        Assert.assertEquals(1, (int)value.get(0));
    }

    @Test
    public void twoNodeWithParent() {
        DepartmentTree<Integer> tree = new DepartmentTree<>();
        NameWithId depParent1 = new NameWithId("depParent1", 1L);
        NameWithId dep1 = new NameWithId("dep1", 11L);
        NameWithId depParent2 = new NameWithId("depParent2", 2L);
        NameWithId dep2 = new NameWithId("dep2", 12L);

        tree.addNode(depParent1, dep1, 1);
        tree.addNode(depParent2, dep2, 2);

        Map<NameWithId, DepartmentTree.Node<Integer>> data = new HashMap<>();
        tree.deepFirstSearchTraversal(node -> data.put(node.getNameWithId(), node));

        Assert.assertEquals(4, data.size());

        DepartmentTree.Node<Integer> node = data.get(depParent1);
        Assert.assertEquals(node.getParent().getNameWithId(), DepartmentTree.rootName);
        Assert.assertEquals(node.getNameWithId(), depParent1);
        Assert.assertEquals(node.getLevel(), 0);

        List<Integer> value = node.getValue();
        Assert.assertEquals(0, value.size());

        node = data.get(dep1);
        Assert.assertEquals(node.getParent().getNameWithId(), depParent1);
        Assert.assertEquals(node.getNameWithId(), dep1);
        Assert.assertEquals(node.getLevel(), 1);

        value = node.getValue();
        Assert.assertEquals(1, value.size());
        Assert.assertEquals(1, (int)value.get(0));

        node = data.get(depParent2);
        Assert.assertEquals(node.getParent().getNameWithId(), DepartmentTree.rootName);
        Assert.assertEquals(node.getNameWithId(), depParent2);
        Assert.assertEquals(node.getLevel(), 0);

        value = node.getValue();
        Assert.assertEquals(0, value.size());

        node = data.get(dep2);
        Assert.assertEquals(node.getParent().getNameWithId(), depParent2);
        Assert.assertEquals(node.getNameWithId(), dep2);
        Assert.assertEquals(node.getLevel(), 1);

        value = node.getValue();
        Assert.assertEquals(1, value.size());
        Assert.assertEquals(2, (int)value.get(0));
    }

    @Test
    public void twoNodeWithOneParent() {
        DepartmentTree<Integer> tree = new DepartmentTree<>();
        NameWithId depParent = new NameWithId("depParent1", 1L);
        NameWithId dep1 = new NameWithId("dep1", 11L);
        NameWithId dep2 = new NameWithId("dep2", 12L);

        tree.addNode(depParent, dep1, null);
        tree.addNode(depParent, dep2, null);

        Map<NameWithId, DepartmentTree.Node<Integer>> data = new HashMap<>();
        tree.deepFirstSearchTraversal(node -> data.put(node.getNameWithId(), node));

        Assert.assertEquals(3, data.size());

        DepartmentTree.Node<Integer> node = data.get(depParent);
        Assert.assertEquals(node.getParent().getNameWithId(), DepartmentTree.rootName);
        Assert.assertEquals(node.getNameWithId(), depParent);
        Assert.assertEquals(node.getLevel(), 0);

        node = data.get(dep1);
        Assert.assertEquals(node.getParent().getNameWithId(), depParent);
        Assert.assertEquals(node.getNameWithId(), dep1);
        Assert.assertEquals(node.getLevel(), 1);

        node = data.get(dep2);
        Assert.assertEquals(node.getParent().getNameWithId(), depParent);
        Assert.assertEquals(node.getNameWithId(), dep2);
        Assert.assertEquals(node.getLevel(), 1);
    }

    @Test
    public void twoNodeWithParentSeq1() {
        DepartmentTree<Integer> tree = new DepartmentTree<>();
        NameWithId depParent = new NameWithId("depParent", 1L);
        NameWithId dep = new NameWithId("dep", 11L);
        NameWithId subDep = new NameWithId("subDep", 21L);

        tree.addNode(depParent, dep, 1); // dep first
        tree.addNode(dep, subDep, 2);

        Map<NameWithId, DepartmentTree.Node<Integer>> data = new HashMap<>();
        tree.deepFirstSearchTraversal(node -> data.put(node.getNameWithId(), node));

        Assert.assertEquals(3, data.size());

        DepartmentTree.Node<Integer> node = data.get(depParent);
        Assert.assertEquals(node.getParent().getNameWithId(), DepartmentTree.rootName);
        Assert.assertEquals(node.getNameWithId(), depParent);
        Assert.assertEquals(node.getLevel(), 0);

        List<Integer> value = node.getValue();
        Assert.assertEquals(0, value.size());

        node = data.get(dep);
        Assert.assertEquals(node.getParent().getNameWithId(), depParent);
        Assert.assertEquals(node.getNameWithId(), dep);
        Assert.assertEquals(node.getLevel(), 1);

        value = node.getValue();
        Assert.assertEquals(1, value.size());
        Assert.assertEquals(1, (int)value.get(0));

        node = data.get(subDep);
        Assert.assertEquals(node.getParent().getNameWithId(), dep);
        Assert.assertEquals(node.getNameWithId(), subDep);
        Assert.assertEquals(node.getLevel(), 2);

        value = node.getValue();
        Assert.assertEquals(1, value.size());
        Assert.assertEquals(2, (int)value.get(0));
    }

    @Test
    public void twoNodeWithParentSeq2() {
        DepartmentTree<Integer> tree = new DepartmentTree<>();
        NameWithId depParent = new NameWithId("depParent", 1L);
        NameWithId dep = new NameWithId("dep", 11L);
        NameWithId subDep = new NameWithId("subDep", 21L);

        tree.addNode(dep, subDep, 2);    // subDep first
        tree.addNode(depParent, dep, 1);

        Map<NameWithId, DepartmentTree.Node<Integer>> data = new HashMap<>();
        tree.deepFirstSearchTraversal(node -> data.put(node.getNameWithId(), node));

        Assert.assertEquals(3, data.size());

        DepartmentTree.Node<Integer> node = data.get(depParent);
        Assert.assertEquals(node.getParent().getNameWithId(), DepartmentTree.rootName);
        Assert.assertEquals(node.getNameWithId(), depParent);
        Assert.assertEquals(node.getLevel(), 0);

        List<Integer> value = node.getValue();
        Assert.assertEquals(0, value.size());

        node = data.get(dep);
        Assert.assertEquals(node.getParent().getNameWithId(), depParent);
        Assert.assertEquals(node.getNameWithId(), dep);
        Assert.assertEquals(node.getLevel(), 1);

        value = node.getValue();
        Assert.assertEquals(1, value.size());
        Assert.assertEquals(1, (int)value.get(0));

        node = data.get(subDep);
        Assert.assertEquals(node.getParent().getNameWithId(), dep);
        Assert.assertEquals(node.getNameWithId(), subDep);
        Assert.assertEquals(node.getLevel(), 2);

        value = node.getValue();
        Assert.assertEquals(1, value.size());
        Assert.assertEquals(2, (int)value.get(0));
    }

    @Test
    public void fourNode() {
        // seq                       1    =>   1  и  3   =>   1
        //                          /         /     /        / \
        //                         2         2     4        2   3
        //                                                      \
        //                                                       4
        DepartmentTree<Integer> tree = new DepartmentTree<>();
        NameWithId dep1 = new NameWithId("dep1", 1L);
        NameWithId dep2 = new NameWithId("dep2", 2L);
        NameWithId dep3 = new NameWithId("dep3", 3L);
        NameWithId dep4 = new NameWithId("dep4", 4L);

        tree.addNode(PersonInfo.nullDepartmentParentName, dep1, 1);
        tree.addNode(dep1, dep2, 2);
        tree.addNode(dep3, dep4, 4);
        tree.addNode(dep1, dep3, 3);

        Map<NameWithId, DepartmentTree.Node<Integer>> data = new HashMap<>();
        tree.deepFirstSearchTraversal(node -> data.put(node.getNameWithId(), node));

        Assert.assertEquals(4, data.size());

        DepartmentTree.Node<Integer> node = data.get(dep1);
        Assert.assertEquals(node.getParent().getNameWithId(), DepartmentTree.rootName);
        Assert.assertEquals(node.getNameWithId(), dep1);
        Assert.assertEquals(node.getLevel(), 0);

        List<Integer> value = node.getValue();
        Assert.assertEquals(1, value.size());

        node = data.get(dep2);
        Assert.assertEquals(node.getParent().getNameWithId(), dep1);
        Assert.assertEquals(node.getNameWithId(), dep2);
        Assert.assertEquals(node.getLevel(), 1);

        value = node.getValue();
        Assert.assertEquals(1, value.size());
        Assert.assertEquals(2, (int)value.get(0));

        node = data.get(dep3);
        Assert.assertEquals(node.getParent().getNameWithId(), dep1);
        Assert.assertEquals(node.getNameWithId(), dep3);
        Assert.assertEquals(node.getLevel(), 1);

        value = node.getValue();
        Assert.assertEquals(1, value.size());
        Assert.assertEquals(3, (int)value.get(0));

        node = data.get(dep4);
        Assert.assertEquals(node.getParent().getNameWithId(), dep3);
        Assert.assertEquals(node.getNameWithId(), dep4);
        Assert.assertEquals(node.getLevel(), 2);

        value = node.getValue();
        Assert.assertEquals(1, value.size());
        Assert.assertEquals(4, (int)value.get(0));
    }
}
