package edu.gatech.cc.scp.mvtipbtc.dto;

import java.util.Objects;

public class Node {

    private String id; // The Bitcoin address will be ID
    private String label;

    public Node() {
    }

    public Node(String id, String label) {
        this.id = id;
        this.label = label;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    // Implement equals and hashCode to allow storing in a Set
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
