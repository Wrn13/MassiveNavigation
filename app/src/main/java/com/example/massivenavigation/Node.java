package com.example.massivenavigation;

import java.util.HashMap;

public class Node {
    private static int maxID = 0;

    private int id;
    private String name;
    private float x;
    private float y;
    private HashMap<Node, Float> edges;

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
