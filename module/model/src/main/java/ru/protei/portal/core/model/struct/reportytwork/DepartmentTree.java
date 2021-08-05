package ru.protei.portal.core.model.struct.reportytwork;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem.NameWithId;
import static ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem.PersonInfo.nullDepartmentParentName;

public class DepartmentTree<T> {
    static public final NameWithId rootName = new NameWithId("root", -1L);
    private final Node<T> root = new Node<>(null, new HashMap<>(), rootName, new ArrayList<>());
    private final Map<NameWithId, Node<T>> map = new HashMap<>();

    public void addNode(NameWithId parentName, NameWithId childName, T value) {
        if (parentName == nullDepartmentParentName) {
            Node<T> child = findNode(childName);
            if (child != null) {
                child.value.add(value);
            } else {
                appendNode(root, childName, value);
            }

            return;
        }

        Node<T> parent = findNode(parentName);
        if (parent != null) {
            Node<T> child = parent.getChildrenByName(childName);
            if (child != null) {
                child.value.add(value);
            } else {
                appendNode(parent, childName, value);
            }
        } else {
            parent = findNode(childName);
            if (parent != null) {
                parent.value.add(value);
                Node<T> oldParent = parent.parent;
                parent.parent = appendNode(oldParent, setOf(parent), parentName, new ArrayList<>());
                oldParent.children.remove(parent.nameWithId);
            } else {
                Node<T> newParent = appendNode(root, new HashSet<>(), parentName, new ArrayList<>());
                appendNode(newParent, childName, value);
            }
        }
    }

    public void deepFirstSearchTraversal(Consumer<Node<T>> consumer) {
        root.children.values().forEach(item -> item.level = 0);
        Deque<Node<T>> deque = new ArrayDeque<>(root.children.values());
        while (!deque.isEmpty()) {
            Node<T> cur = deque.pollLast();
            cur.children.values().forEach(item -> item.level = cur.level+1);
            deque.addAll(stream(cur.children.values()).sorted().collect(Collectors.toList()));
            consumer.accept(cur);
        }
    }

    private Node<T> appendNode(Node<T> parent, NameWithId childName, T value) {
        return appendNode(parent, new HashSet<>(), childName, listOf(value));
    }

    private Node<T> appendNode(Node<T> parent, Set<Node<T>> children, NameWithId childName, List<T> values) {
        Node<T> node = map.compute(childName, (k, v) -> {
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
            return new Node<>(parent,
                    children.stream().collect(Collectors.toMap(Node::getNameWithId, Function.identity())),
                    childName, values);
        });
        parent.children.put(node.nameWithId, node);
        return node;
    }

    private Node<T> findNode(NameWithId nameWithId) {
        return map.get(nameWithId);
    }

    public static class Node<T> implements Comparable<Node<T>> {
        private Node<T> parent;
        private final Map<NameWithId, Node<T>> children;
        private final NameWithId nameWithId;
        private final List<T> value;
        private int level;

        public Node(Node<T> parent, Map<NameWithId, Node<T>> children, NameWithId nameWithId, List<T> value) {
            this.parent = parent;
            this.children = children;
            this.nameWithId = nameWithId;
            this.value = value;
        }

        public Node<T> getParent() {
            return parent;
        }

        public NameWithId getNameWithId() {
            return nameWithId;
        }

        public List<T> getValue() {
            return value;
        }

        public int getLevel() {
            return level;
        }

        public Node<T> merge(Node<T> other) {
            other.parent.children.remove(other.nameWithId);

            other.children.forEach((otherChildName, otherChildNode) ->
                    this.children.merge(otherChildName, otherChildNode, (oldChildNode, newChildNode) -> {
                        oldChildNode.value.addAll(newChildNode.value);
                        oldChildNode.children.putAll(newChildNode.children);
                        return newChildNode;
                    }));

            List<T> mergedValue =  new ArrayList<>(this.value);
            mergedValue.addAll(other.value);
            return new Node<>(this.parent, new HashMap<>(this.children), this.nameWithId, mergedValue);
        }

        private Node<T> getChildrenByName(NameWithId nameWithId) {
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
            Node<?> node = (Node<?>) o;
            return nameWithId.equals(node.nameWithId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nameWithId);
        }
    }
}
