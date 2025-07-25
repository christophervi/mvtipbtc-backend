package edu.gatech.cc.scp.mvtipbtc.dto;

import java.util.Objects;

public class Edge {

    private String id;
    private String source;
    private String target;

    public Edge() {
    }

    public Edge(String id, String source, String target) {
        this.id = id;
        this.source = source;
        this.target = target;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    // Implement equals and hashCode for proper collection handling
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(id, edge.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
