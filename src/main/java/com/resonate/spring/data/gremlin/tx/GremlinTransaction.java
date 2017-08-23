package com.resonate.spring.data.gremlin.tx;

import com.tinkerpop.blueprints.Graph;

/**
 * Created by gman on 4/05/15.
 */
public class GremlinTransaction {

    private Graph graph;

    public GremlinTransaction(Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }
}
