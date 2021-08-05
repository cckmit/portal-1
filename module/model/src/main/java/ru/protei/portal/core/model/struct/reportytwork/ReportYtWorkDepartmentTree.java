package ru.protei.portal.core.model.struct.reportytwork;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem.NameWithId;
import static ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem.PersonInfo.nullDepartmentParentName;

public class ReportYtWorkDepartmentTree {
    private final Node root = new Node(null, new HashMap<>(), new NameWithId("root", -1L), new ArrayList<>());
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
                parent.parent = appendNode(oldParent, setOf(parent), parentName, new ArrayList<>());
                oldParent.children.remove(parent.nameWithId);
            } else {
                Node newParent = appendNode(root, new HashSet<>(), parentName, new ArrayList<>());
                appendNode(newParent, childName, value);
            }
        }
    }

    public void breadthFirstSearchTraversal(Consumer<Node> consumer) {
        root.children.values().forEach(i -> i.level = 0);
        Deque<Node> deque = new ArrayDeque<>(root.children.values());
        while (!deque.isEmpty()) {
            Node cur = deque.pollLast();
            cur.children.values().forEach(i -> i.level = cur.level+1);
            deque.addAll(stream(cur.children.values()).sorted().collect(Collectors.toList()));
            consumer.accept(cur);
        }
    }

    private Node appendNode(Node parent, NameWithId childName, ReportYtWorkRowItem value) {
        return appendNode(parent, new HashSet<>(), childName, listOf(value));
    }

    private Node appendNode(Node parent, Set<Node> children, NameWithId childName, List<ReportYtWorkRowItem> values) {
        Node node = map.compute(childName, (k, v) -> {
            if (v != null) {
                v.parent.children.remove(v.nameWithId);
                v.parent = parent;
                v.value.addAll(values);
                children.forEach(child -> v.children.compute(child.nameWithId, (key, oldChild) -> {
                    if (oldChild != null) {
                        oldChild.merge(child);
                        return oldChild;
                    } else {
                        return child;
                    }
                }));
                return v;
            }
            return new Node(parent,
                    children.stream().collect(Collectors.toMap(Node::getNameWithId, Function.identity())),
                    childName, values);
        });
        parent.children.put(node.nameWithId, node);
        return node;
    }

    private Node findNode(NameWithId nameWithId) {
        return map.get(nameWithId);
    }

    public static class Node implements Comparable<Node> {
        private Node parent;
        private final Map<NameWithId, Node> children;
        private final NameWithId nameWithId;
        private final List<ReportYtWorkRowItem> value;
        private int level;

        public Node(Node parent, Map<NameWithId, Node> children, NameWithId nameWithId, List<ReportYtWorkRowItem> value) {
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

        public Node merge(Node other) {
            other.parent.children.remove(other.nameWithId);

            other.children.forEach((k, v) -> {
                this.children.merge(k, v, (v1, v2) -> {
                    if (v1 != null) {
                        v1.value.addAll(v2.value);
                        v1.children.putAll(v2.children);
                    }
                    return v2;
                });
            });

            List<ReportYtWorkRowItem> mergedValue =  new ArrayList<>(this.value);
            mergedValue.addAll(other.value);
            return new Node(this.parent, new HashMap<>(this.children), this.nameWithId, mergedValue);
        }

        private Node getChildrenByName(NameWithId nameWithId) {
            return children.get(nameWithId);
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
