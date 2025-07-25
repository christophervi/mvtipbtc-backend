package edu.gatech.cc.scp.mvtipbtc.dto;

import java.util.HashSet;
import java.util.Set;

public class TransactionFlow {

	private Set<Node> nodes;
	private Set<Edge> edges;

	public TransactionFlow() {
		this.nodes = new HashSet<>();
		this.edges = new HashSet<>();
	}
	
	public Set<Node> getNodes() {
		return nodes;
	}

	public void setNodes(Set<Node> nodes) {
		this.nodes = nodes;
	}

	public Set<Edge> getEdges() {
		return edges;
	}

	public void setEdges(Set<Edge> edges) {
		this.edges = edges;
	}
}
