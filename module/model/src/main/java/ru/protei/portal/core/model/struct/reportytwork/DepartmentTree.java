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
    private final Map<NameWithId, Node> mapNameToNode = new HashMap<>();

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
            Node child = parent.getChildByName(childName);
            if (child == null) {
                appendNode(parent, childName);
            }
        } else {
            parent = findNode(childName);
            if (parent != null) {
                Node oldParent = parent.getParent();
                parent.setParent(appendNode(oldParent, setOf(parent), parentName));
                oldParent.getChildren().remove(parent.getNameWithId());
            } else {
                Node newParent = appendNode(root, new HashSet<>(), parentName);
                appendNode(newParent, childName);
            }
        }
    }

    public void deepFirstSearchTraversal(Consumer<Node> consumer) {
        Collection<Node> rootChildrenValues = root.getChildren().values();
        rootChildrenValues.forEach(item -> item.setLevel(0));
        Deque<Node> deque = new ArrayDeque<>(rootChildrenValues);

        while (!deque.isEmpty()) {
            Node cur = deque.pollLast();

            Collection<Node> curChildrenValues = cur.getChildren().values();
            curChildrenValues.forEach(item -> item.setLevel(cur.getLevel() + 1));
            deque.addAll(stream(curChildrenValues).sorted().collect(Collectors.toList()));

            consumer.accept(cur);
        }
    }

    private Node appendNode(Node parent, NameWithId childName) {
        return appendNode(parent, new HashSet<>(), childName);
    }

    private Node appendNode(Node parent, Set<Node> children, NameWithId childName) {
        Node node = mapNameToNode.compute(childName, (k, v) -> {
            if (v != null) {
                v.getParent().getChildren().remove(v.getNameWithId());
                v.setParent(parent);
                children.forEach(child -> v.getChildren().compute(child.getNameWithId(), (key, oldChild) -> {
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
        parent.getChildren().put(node.getNameWithId(), node);
        return node;
    }

    private Node findNode(NameWithId nameWithId) {
        return mapNameToNode.get(nameWithId);
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

        private void setParent(Node parent) {
            this.parent = parent;
        }

        public Node getParent() {
            return parent;
        }

        private Map<NameWithId, Node> getChildren() {
            return children;
        }

        public NameWithId getNameWithId() {
            return nameWithId;
        }

        private void setLevel(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }

        public Node merge(Node other) {
            other.getParent().getChildren().remove(other.getNameWithId());

            other.getChildren().forEach((otherChildName, otherChildNode) ->
                    this.children.merge(otherChildName, otherChildNode, (oldChildNode, newChildNode) -> {
                        oldChildNode.children.putAll(newChildNode.children);
                        return newChildNode;
                    }));

            return new Node(this.parent, new HashMap<>(this.children), this.nameWithId);
        }

        private Node getChildByName(NameWithId nameWithId) {
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
