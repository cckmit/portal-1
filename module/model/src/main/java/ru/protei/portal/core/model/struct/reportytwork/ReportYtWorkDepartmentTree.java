package ru.protei.portal.core.model.struct.reportytwork;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem.NameWithId;
import static ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem.PersonInfo.nullDepartmentParentName;

public class ReportYtWorkDepartmentTree {
    private final Node root = new Node(null, new ArrayList<>(), new NameWithId("root", -1L), new ArrayList<>());
    private final Map<NameWithId, Node> map = new HashMap<>();

    public void addNode(NameWithId parentName, NameWithId childName, ReportYtWorkRowItem value) {
        if (parentName == nullDepartmentParentName) {
            Node child = findNode(childName);
            if (child != null) {
                child.value.add(value);
            } else {
                appendNode(root, childName, value);
            }

            return;
        }

        Node parent = findNode(parentName);
        if (parent != null) {
            Node child = parent.getChildrenByName(childName);
            if (child != null) {
                child.value.add(value);
            } else {
                appendNode(parent, childName, value);
            }
        } else {
            parent = findNode(childName);
            if (parent != null) {
                parent.value.add(value);
                Node oldParent = parent.parent;
                parent.parent = appendNode(oldParent, listOf(parent), parentName, new ArrayList<>());
                oldParent.children.remove(parent);
            } else {
                Node newParent = appendNode(root, new ArrayList<>(), parentName, new ArrayList<>());
                appendNode(newParent, childName, value);
            }
        }
    }

    public void traversal(Consumer<Node> consumer) {
        root.children.forEach(i -> i.level = 0);
        Deque<Node> deque = new ArrayDeque<>(root.children);
        while (!deque.isEmpty()) {
            Node cur = deque.pollLast();
            cur.children.forEach(i -> i.level = cur.level+1);
            deque.addAll(stream(cur.children).sorted().collect(Collectors.toList()));
            consumer.accept(cur);
        }
    }

    private Node appendNode(Node parent, NameWithId childName, ReportYtWorkRowItem value) {
        return appendNode(parent, new ArrayList<>(), childName, listOf(value));
    }

    private Node appendNode(Node parent, List<Node> children, NameWithId childName, List<ReportYtWorkRowItem> values) {
        Node node = map.compute(childName, (k, v) -> {
            if (v != null) {
                v.parent.children.remove(v);
                v.parent = parent;
                v.value.addAll(values);
                v.children.addAll(children);
                return v;
            }
            return new Node(parent, children, childName, values);
        });
        parent.children.add(node);
        return node;
    }

    private Node findNode(NameWithId nameWithId) {
        return map.get(nameWithId);
    }

    public static class Node implements Comparable<Node> {
        private Node parent;
        private final List<Node> children;
        private final NameWithId nameWithId;
        private final List<ReportYtWorkRowItem> value;
        private int level;

        public Node(Node parent, List<Node> children, NameWithId nameWithId, List<ReportYtWorkRowItem> value) {
            this.parent = parent;
            this.children = children;
            this.nameWithId = nameWithId;
            this.value = value;
        }

        public NameWithId getNameWithId() {
            return nameWithId;
        }

        public List<ReportYtWorkRowItem> getValue() {
            return value;
        }

        public int getLevel() {
            return level;
        }

        private Node getChildrenByName(NameWithId nameWithId) {
            for (Node node : children) {
                if (node.nameWithId.equals(nameWithId)) {
                    return node;
                }
            }
            return null;
        }

        @Override
        public int compareTo(Node o) {
            return (int) (nameWithId.getId() - o.nameWithId.getId());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            Node node = (Node) o;
            return nameWithId.equals(node.nameWithId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nameWithId);
        }
    }
}
