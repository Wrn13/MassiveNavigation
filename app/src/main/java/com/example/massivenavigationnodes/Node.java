package com.example.massivenavigationnodes;

import java.util.HashMap;

public class Node {
    private static int maxID = 0;

    private final int id;
    private final String name;
    private final float x;
    private final float y;
    private final HashMap<Node, Float> edges;

    public Node(String name, float x, float y) {
        this.name = name;
        this.x = x;
        this.y = y;
        edges = new HashMap<>();
        id = maxID;
        maxID++;
    }

    public void addEdge(Node node, float dist) {
        //edges.put(node, (float)Math.sqrt(Math.pow(x - node.getX(), 2) + Math.pow(y - node.getY(), 2)));
        edges.put(node, dist);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }

    public HashMap<Node, Float> getEdges() {
        return edges;
    }
}
