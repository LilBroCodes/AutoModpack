package org.lilbrocodes.automodpack.utils;

import javax.annotation.Nullable;
import java.util.*;

public class TreeIO {
    public static String printTree(List<String> paths, boolean useAscii, @Nullable String rootName) {
        TreeNode root = new TreeNode("");

        Set<String> prefixes = new HashSet<>();
        for (String path : paths) {
            String[] parts = path.split("\\\\");
            StringBuilder current = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                if (!current.isEmpty()) current.append("\\");
                current.append(parts[i]);
                prefixes.add(current.toString());
            }
        }

        for (String path : paths) {
            String[] parts = path.split("\\\\");
            TreeNode current = root;
            StringBuilder fullPath = new StringBuilder();

            for (int i = 0; i < parts.length; i++) {
                if (!fullPath.isEmpty()) fullPath.append("\\");
                fullPath.append(parts[i]);

                boolean isFile = (i == parts.length - 1) && !prefixes.contains(fullPath.toString());
                current = current.children.computeIfAbsent(parts[i], name -> new TreeNode(name, isFile));
            }
        }

        String branch = useAscii ? "|-- " : "├── ";
        String lastBranch = useAscii ? "\\-- " : "└── ";
        String vertical = useAscii ? "|   " : "│   ";

        StringBuilder output = new StringBuilder();
        printRecursive(root, "", output, branch, lastBranch, vertical, "");
        int index = output.indexOf("\\--");
        if (index != -1) {
            output.replace(index, index + 3, rootName == null ? "" : rootName);
        }
        return output.toString();
    }

    private static void printRecursive(TreeNode node, String prefix, StringBuilder output,
                                       String branch, String lastBranch, String vertical, String empty) {
        List<TreeNode> children = new ArrayList<>(node.children.values());

        children.sort(Comparator
                .comparing((TreeNode n) -> n.isFile)
                .thenComparing(n -> n.name.toLowerCase()));

        for (int i = 0; i < children.size(); i++) {
            TreeNode child = children.get(i);
            boolean isLast = (i == children.size() - 1);
            output.append(prefix)
                    .append(isLast ? lastBranch : branch)
                    .append(child.name)
                    .append("\n");

            if (!child.isFile) {
                printRecursive(child, prefix + (isLast ? empty : vertical), output, branch, lastBranch, vertical, empty);
            }
        }
    }

    private static class TreeNode {
        String name;
        boolean isFile;
        Map<String, TreeNode> children = new HashMap<>();

        TreeNode(String name) {
            this(name, false);
        }

        TreeNode(String name, boolean isFile) {
            this.name = name;
            this.isFile = isFile;
        }
    }

}
