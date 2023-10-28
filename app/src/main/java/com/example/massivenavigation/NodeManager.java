package com.example.massivenavigation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NodeManager {
    private static NodeManager instance = new NodeManager();

    private ArrayList<Node> nodes;

    public static NodeManager getInstance() {
        return instance;
    }

    private NodeManager() {
        reset();
        parseNodesFromFile();
    }

    public void reset() {
        nodes = new ArrayList<>();
        /*List<Node> temp = nodes.stream().filter((n) -> n.getName().equals("elevator")).collect(Collectors.toList());

        for (Node n : temp) {

        }*/
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public String findShortestPath(int startingId, int destinationId) {
        float[] list = new float[nodes.size()];
        boolean[] boolList = new boolean[nodes.size()];
        int[] parents = new int[nodes.size()];
        for(int i = 0; i < list.length; i++) {
            list[i] = Float.MAX_VALUE;
        }
        list[startingId] = 0;

        while(!boolList[destinationId]) {
            int id = minDistance(list, boolList);
            boolList[id] = true;
            HashMap<Node, Float> edges = nodes.get(id).getEdges();

            for(Map.Entry<Node, Float> entry: edges.entrySet()) {
                int tempID = entry.getKey().getID();
                if(!boolList[tempID]) {
                    if(list[tempID] > entry.getValue() + list[id]) {
                        list[tempID] = entry.getValue() + list[id];
                        parents[tempID] = id;
                    }
                }
            }
        }

        ArrayList<Integer> path = new ArrayList<>();
        int id = destinationId;
        path.add(destinationId);
        while(id != startingId) {
            path.add(parents[id]);
            id = parents[id];
        }
        return Arrays.toString(path.toArray());
    }

    private int minDistance(float[] list, boolean[] boolList) {
        float minDistance = Float.MAX_VALUE;
        int index = 0;
        for(int i = 0; i < list.length; i++) {
            if(!boolList[i]&&list[i]<minDistance){
                minDistance = list[i];
                index = i;
            }
        }
        return index;
    }

    public void parseNodesFromFile() {
        Node node0 = new Node("entrance", 89, 78);
        Node node1 = new Node("", 42, 86);
        Node node2 = new Node("", 90, 80);
        Node node3 = new Node("", 90, 80);
        Node node4 = new Node("", 90, 80);
        Node node5 = new Node("elevator", 90, 80);
        Node node6 = new Node("", 90, 80);
        Node node7 = new Node("", 90, 80);
        Node node8 = new Node("destination", 90, 80);
        addNode(node0);
        addNode(node1);
        addNode(node2);
        addNode(node3);
        addNode(node4);
        addNode(node5);
        addNode(node6);
        addNode(node7);
        addNode(node8);

        nodes.get(0).addEdge(nodes.get(1), 4);
        nodes.get(0).addEdge(nodes.get(7), 8);

        nodes.get(1).addEdge(nodes.get(0), 4);
        nodes.get(1).addEdge(nodes.get(7), 11);
        nodes.get(1).addEdge(nodes.get(2), 8);

        nodes.get(2).addEdge(nodes.get(1), 8);
        nodes.get(2).addEdge(nodes.get(8), 2);
        nodes.get(2).addEdge(nodes.get(5), 4);
        nodes.get(2).addEdge(nodes.get(3), 7);

        nodes.get(3).addEdge(nodes.get(2), 7);
        nodes.get(3).addEdge(nodes.get(5), 14);
        nodes.get(3).addEdge(nodes.get(4), 9);

        nodes.get(4).addEdge(nodes.get(3), 9);
        nodes.get(4).addEdge(nodes.get(5), 10);

        nodes.get(5).addEdge(nodes.get(6), 2);
        nodes.get(5).addEdge(nodes.get(2), 4);
        nodes.get(5).addEdge(nodes.get(3), 14);
        nodes.get(5).addEdge(nodes.get(4), 10);

        nodes.get(6).addEdge(nodes.get(7), 1);
        nodes.get(6).addEdge(nodes.get(8), 6);
        nodes.get(6).addEdge(nodes.get(5), 2);

        nodes.get(7).addEdge(nodes.get(0), 8);
        nodes.get(7).addEdge(nodes.get(1), 11);
        nodes.get(7).addEdge(nodes.get(8), 7);
        nodes.get(7).addEdge(nodes.get(6), 1);

        nodes.get(8).addEdge(nodes.get(2), 2);
        nodes.get(8).addEdge(nodes.get(7), 7);
        nodes.get(8).addEdge(nodes.get(6), 6);

        // add edges
        /*for(int i = 0; i < nodes.size(); i++) {

        }*/
    }
}
