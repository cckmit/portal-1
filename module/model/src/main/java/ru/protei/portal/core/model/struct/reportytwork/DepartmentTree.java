package ru.protei.portal.core.model.struct.reportytwork;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem.NameWithId;
import static ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem.PersonInfo.nullDepartmentParentName;

public class DepartmentTree {
    static public final NameWithId rootName = new NameWithId("root", -1L);
    private final Node root = new Node(null, new HashMap<>(), rootName);
    private final Map<NameWithId, Node> map = new HashMap<>();

    public void addNode(NameWithId parentName, NameWithId childName) {
        if (parentName == nullDepartmentParentName) {
            Node child = findNode(childName);
            if (child == null) {
                appendNode(root, childName);
            }

            return;
        }

        Node parent = findNode(parentName);
        if (parent != null) {
            Node child = parent.getChildrenByName(childName);
            if (child == null) {
                appendNode(parent, childName);
            }
        } else {
            parent = findNode(childName);
            if (parent != null) {
                Node oldParent = parent.parent;
                parent.parent = appendNode(oldParent, setOf(parent), parentName);
                oldParent.children.remove(parent.nameWithId);
            } else {
                Node newParent = appendNode(root, new HashSet<>(), parentName);
                appendNode(newParent, childName);
            }
        }
    }

    public void deepFirstSearchTraversal(Consumer<Node> consumer) {
        root.children.values().forEach(item -> item.level = 0);
        Deque<Node> deque = new ArrayDeque<>(root.children.values());
        while (!deque.isEmpty()) {
            Node cur = deque.pollLast();
            cur.children.values().forEach(item -> item.level = cur.level+1);
            deque.addAll(stream(cur.children.values()).sorted().collect(Collectors.toList()));
            consumer.accept(cur);
        }
    }

    private Node appendNode(Node parent, NameWithId childName) {
        return appendNode(parent, new HashSet<>(), childName);
    }

    private Node appendNode(Node parent, Set<Node> children, NameWithId childName) {
        Node node = map.compute(childName, (k, v) -> {
            if (v != null) {
                v.parent.children.remove(v.nameWithId);
                v.parent = parent;
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
                    childName);
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
        private int level;

        public Node(Node parent, Map<NameWithId, Node> children, NameWithId nameWithId) {
            this.parent = parent;
            this.children = children;
            this.nameWithId = nameWithId;
        }

        public Node getParent() {
            return parent;
        }

        public NameWithId getNameWithId() {
            return nameWithId;
        }

        public int getLevel() {
            return level;
        }

        public Node merge(Node other) {
            other.parent.children.remove(other.nameWithId);

            other.children.forEach((otherChildName, otherChildNode) ->
                    this.children.merge(otherChildName, otherChildNode, (oldChildNode, newChildNode) -> {
                        oldChildNode.children.putAll(newChildNode.children);
                        return newChildNode;
                    }));

            return new Node(this.parent, new HashMap<>(this.children), this.nameWithId);
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
