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
        DepartmentTree tree = new DepartmentTree();
        NameWithId dep1 = new NameWithId("dep1", 1L);

        tree.addNode(PersonInfo.nullDepartmentParentName, dep1);

        List<DepartmentTree.Node> data = new ArrayList<>();
        tree.deepFirstSearchTraversal(data::add);

        Assert.assertEquals(1, data.size());

        DepartmentTree.Node node = data.get(0);
        Assert.assertEquals(DepartmentTree.rootName, node.getParent().getNameWithId());
        Assert.assertEquals(dep1, node.getNameWithId());
        Assert.assertEquals(0, node.getLevel());
    }

    @Test
    public void twoNodeWithOutParent() {
        DepartmentTree tree = new DepartmentTree();
        NameWithId dep1 = new NameWithId("dep1", 1L);
        NameWithId dep2 = new NameWithId("dep2", 2L);

        tree.addNode(PersonInfo.nullDepartmentParentName, dep1);
        tree.addNode(PersonInfo.nullDepartmentParentName, dep2);

        Map<NameWithId, DepartmentTree.Node> data = new HashMap<>();
        tree.deepFirstSearchTraversal(node -> data.put(node.getNameWithId(), node));

        Assert.assertEquals(2, data.size());

        DepartmentTree.Node node = data.get(dep1);
        Assert.assertEquals(DepartmentTree.rootName, node.getParent().getNameWithId());
        Assert.assertEquals(dep1, node.getNameWithId());
        Assert.assertEquals(0, node.getLevel());

        node = data.get(dep2);
        Assert.assertEquals(DepartmentTree.rootName, node.getParent().getNameWithId());
        Assert.assertEquals(dep2, node.getNameWithId());
        Assert.assertEquals(0, node.getLevel());
    }

    @Test
    public void oneNodeWithParent() {
        DepartmentTree tree = new DepartmentTree();
        NameWithId depParent1 = new NameWithId("depParent1", 1L);
        NameWithId dep1 = new NameWithId("dep1", 11L);

        tree.addNode(depParent1, dep1);

        Map<NameWithId, DepartmentTree.Node> data = new HashMap<>();
        tree.deepFirstSearchTraversal(node -> data.put(node.getNameWithId(), node));

        Assert.assertEquals(2, data.size());

        DepartmentTree.Node node = data.get(depParent1);
        Assert.assertEquals(DepartmentTree.rootName, node.getParent().getNameWithId());
        Assert.assertEquals(depParent1, node.getNameWithId());
        Assert.assertEquals(0, node.getLevel());

        node = data.get(dep1);
        Assert.assertEquals(depParent1, node.getParent().getNameWithId());
        Assert.assertEquals(dep1, node.getNameWithId());
        Assert.assertEquals(1, node.getLevel());
    }

    @Test
    public void twoNodeWithParent() {
        DepartmentTree tree = new DepartmentTree();
        NameWithId depParent1 = new NameWithId("depParent1", 1L);
        NameWithId dep1 = new NameWithId("dep1", 11L);
        NameWithId depParent2 = new NameWithId("depParent2", 2L);
        NameWithId dep2 = new NameWithId("dep2", 12L);

        tree.addNode(depParent1, dep1);
        tree.addNode(depParent2, dep2);

        Map<NameWithId, DepartmentTree.Node> data = new HashMap<>();
        tree.deepFirstSearchTraversal(node -> data.put(node.getNameWithId(), node));

        Assert.assertEquals(4, data.size());

        DepartmentTree.Node node = data.get(depParent1);
        Assert.assertEquals(DepartmentTree.rootName, node.getParent().getNameWithId());
        Assert.assertEquals(depParent1, node.getNameWithId());
        Assert.assertEquals(0, node.getLevel());

        node = data.get(dep1);
        Assert.assertEquals(depParent1, node.getParent().getNameWithId());
        Assert.assertEquals(dep1, node.getNameWithId());
        Assert.assertEquals(1, node.getLevel());

        node = data.get(depParent2);
        Assert.assertEquals(DepartmentTree.rootName, node.getParent().getNameWithId());
        Assert.assertEquals(depParent2, node.getNameWithId());
        Assert.assertEquals(0, node.getLevel());

        node = data.get(dep2);
        Assert.assertEquals(depParent2, node.getParent().getNameWithId());
        Assert.assertEquals(dep2, node.getNameWithId());
        Assert.assertEquals(1, node.getLevel());
    }

    @Test
    public void twoNodeWithOneParent() {
        DepartmentTree tree = new DepartmentTree();
        NameWithId depParent = new NameWithId("depParent1", 1L);
        NameWithId dep1 = new NameWithId("dep1", 11L);
        NameWithId dep2 = new NameWithId("dep2", 12L);

        tree.addNode(depParent, dep1);
        tree.addNode(depParent, dep2);

        Map<NameWithId, DepartmentTree.Node> data = new HashMap<>();
        tree.deepFirstSearchTraversal(node -> data.put(node.getNameWithId(), node));

        Assert.assertEquals(3, data.size());

        DepartmentTree.Node node = data.get(depParent);
        Assert.assertEquals(DepartmentTree.rootName, node.getParent().getNameWithId());
        Assert.assertEquals(depParent, node.getNameWithId());
        Assert.assertEquals(0, node.getLevel());

        node = data.get(dep1);
        Assert.assertEquals(depParent, node.getParent().getNameWithId());
        Assert.assertEquals(dep1, node.getNameWithId());
        Assert.assertEquals(1, node.getLevel());

        node = data.get(dep2);
        Assert.assertEquals(depParent, node.getParent().getNameWithId());
        Assert.assertEquals(dep2, node.getNameWithId());
        Assert.assertEquals(1, node.getLevel());
    }

    @Test
    public void twoNodeWithParentSeq1() {
        DepartmentTree tree = new DepartmentTree();
        NameWithId depParent = new NameWithId("depParent", 1L);
        NameWithId dep = new NameWithId("dep", 11L);
        NameWithId subDep = new NameWithId("subDep", 21L);

        tree.addNode(depParent, dep); // dep first
        tree.addNode(dep, subDep);

        Map<NameWithId, DepartmentTree.Node> data = new HashMap<>();
        tree.deepFirstSearchTraversal(node -> data.put(node.getNameWithId(), node));

        Assert.assertEquals(3, data.size());

        DepartmentTree.Node node = data.get(depParent);
        Assert.assertEquals(DepartmentTree.rootName, node.getParent().getNameWithId());
        Assert.assertEquals(depParent, node.getNameWithId());
        Assert.assertEquals(0, node.getLevel());

        node = data.get(dep);
        Assert.assertEquals(depParent, node.getParent().getNameWithId());
        Assert.assertEquals(dep, node.getNameWithId());
        Assert.assertEquals(1, node.getLevel());

        node = data.get(subDep);
        Assert.assertEquals(dep, node.getParent().getNameWithId());
        Assert.assertEquals(subDep, node.getNameWithId());
        Assert.assertEquals(2, node.getLevel());
    }

    @Test
    public void twoNodeWithParentSeq2() {
        DepartmentTree tree = new DepartmentTree();
        NameWithId depParent = new NameWithId("depParent", 1L);
        NameWithId dep = new NameWithId("dep", 11L);
        NameWithId subDep = new NameWithId("subDep", 21L);

        tree.addNode(dep, subDep);    // subDep first
        tree.addNode(depParent, dep);

        Map<NameWithId, DepartmentTree.Node> data = new HashMap<>();
        tree.deepFirstSearchTraversal(node -> data.put(node.getNameWithId(), node));

        Assert.assertEquals(3, data.size());

        DepartmentTree.Node node = data.get(depParent);
        Assert.assertEquals(DepartmentTree.rootName, node.getParent().getNameWithId());
        Assert.assertEquals(depParent, node.getNameWithId());
        Assert.assertEquals(0, node.getLevel());

        node = data.get(dep);
        Assert.assertEquals(depParent, node.getParent().getNameWithId());
        Assert.assertEquals(dep, node.getNameWithId());
        Assert.assertEquals(1, node.getLevel());

        node = data.get(subDep);
        Assert.assertEquals(dep, node.getParent().getNameWithId());
        Assert.assertEquals(subDep, node.getNameWithId());
        Assert.assertEquals(2, node.getLevel());
    }

    @Test
    public void fourNode() {
        //                            root           root               root
        //                            /               /\                /
        // seq                       1    =>        1   3   =>         1
        //                          /              /     \            / \
        //                         2              2       4          2   3
        //                                                                \
        //                                                                 4
        DepartmentTree tree = new DepartmentTree();
        NameWithId dep1 = new NameWithId("dep1", 1L);
        NameWithId dep2 = new NameWithId("dep2", 2L);
        NameWithId dep3 = new NameWithId("dep3", 3L);
        NameWithId dep4 = new NameWithId("dep4", 4L);

        tree.addNode(dep1, dep2);
        tree.addNode(dep3, dep4);
        tree.addNode(dep1, dep3);

        Map<NameWithId, DepartmentTree.Node> data = new HashMap<>();
        tree.deepFirstSearchTraversal(node -> data.put(node.getNameWithId(), node));

        Assert.assertEquals(4, data.size());

        DepartmentTree.Node node = data.get(dep1);
        Assert.assertEquals(DepartmentTree.rootName, node.getParent().getNameWithId());
        Assert.assertEquals(dep1, node.getNameWithId());
        Assert.assertEquals(0, node.getLevel());

        node = data.get(dep2);
        Assert.assertEquals(dep1, node.getParent().getNameWithId());
        Assert.assertEquals(dep2, node.getNameWithId());
        Assert.assertEquals(1, node.getLevel());

        node = data.get(dep3);
        Assert.assertEquals(dep1, node.getParent().getNameWithId());
        Assert.assertEquals(dep3, node.getNameWithId());
        Assert.assertEquals(1, node.getLevel());

        node = data.get(dep4);
        Assert.assertEquals(dep3, node.getParent().getNameWithId());
        Assert.assertEquals(dep4, node.getNameWithId());
        Assert.assertEquals(2, node.getLevel());
    }
}
