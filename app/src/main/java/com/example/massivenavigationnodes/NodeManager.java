package com.example.massivenavigationnodes;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.SystemClock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import android.speech.tts.TextToSpeech;

import massive_navigation.snapchat.MainActivity;

public class NodeManager {
    private static NodeManager instance = new NodeManager();

    public enum Direction{up, right, down, left};

    public String message = "";
    public boolean messageUpdate = false;
    public boolean isUpdating = false;
    private float ignoreTime;

    private int currentIndex;

    private float currentDistance;
    private ContentResolver resolver;
    private MainActivity mainActivity;

    public static Context context;

    private final double speed = 50;
    private ArrayList<Node> nodes;
    private ArrayList<Integer> shortestPath;

    private static TextToSpeech textToSpeech;

    public static NodeManager getInstance() {
        return instance;
    }

    private NodeManager() {
        reset();

    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setMessageUpdate(boolean bool) {
        this.messageUpdate = bool;
    }

    public void setContext(Context context) {
        NodeManager.context = context;
    }

    public void reset() {
        nodes = new ArrayList<>();
        /*List<Node> temp = nodes.stream().filter((n) -> n.getName().equals("elevator")).collect(Collectors.toList());

        for (Node n : temp) {

        }*/
    }

    public void testNodes() throws IOException {
        parseNodesFromFile();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void startRoute(String destination){
        Node start = nodes.get(8);
        Node end = null;
        boolean found = false;
        int i = 0;

        while(!found&&i<nodes.size()){
            if(nodes.get(i).getName().equals(destination)){
                found = true;
                end = nodes.get(i);
            }
            i++;
        }

        findShortestPath(start.getID(),end.getID());
        currentIndex = 0;
        currentDistance = 0;
        ignoreTime = SystemClock.elapsedRealtime();
        isUpdating = true;
    }
    private int count = 0;
    public void update(){
        count++;
        currentDistance += speed * (SystemClock.elapsedRealtime()-ignoreTime);
        ignoreTime = SystemClock.elapsedRealtime();

        message = "";
        if(currentIndex==shortestPath.size()-1){
            messageUpdate = true;
            message = "You have reached your destination";
            isUpdating = false;
        }
        else {
            int nextNode = shortestPath.get(currentIndex);
            HashMap<Node, Float> edges = nodes.get(nextNode)
                    .getEdges();
            if (edges.get(nodes.get(shortestPath.get(currentIndex + 1))) <= currentDistance) {

            messageUpdate = true;

                if (currentIndex < shortestPath.size() - 2) {
//                double hypoth = (double)Math.sqrt(Math.pow(nodes.get(currentIndex).getX()-nodes.get(currentIndex+2).getX(),2)+Math.pow(nodes.get(currentIndex).getY()-nodes.get(currentIndex+2).getY(),2));
//                double opposite = (double)Math.sqrt(Math.pow(nodes.get(currentIndex+1).getX()-nodes.get(currentIndex+2).getX(),2)+Math.pow(nodes.get(currentIndex+1).getY()-nodes.get(currentIndex+2).getY(),2));
//                double angle = asin(opposite/hypoth);
//
//                if(angle>10){
//                    message = "Take a left turn";
//                }
//                else if(angle<-10){
//                    message = "Take a right turn";
//                }\\

                    Direction first = getDirection(currentIndex, currentIndex + 1);
                    Direction second = getDirection(currentIndex + 1, currentIndex + 2);

                    if (first.equals(second)) {
                        message = "Continue Straight";
                    } else if (first.equals(Direction.down)) {
                        if (second.equals(Direction.right)) {
                            message = "Take a left turn";
                        } else {
                            message = "Take a right turn";
                        }
                    } else if (first.equals(Direction.up)) {
                        if (second.equals(Direction.right)) {
                            message = "Take a right turn";
                        } else {
                            message = "Take a left turn";
                        }
                    } else if (first.equals(Direction.right)) {
                        if (second.equals(Direction.down)) {
                            message = "Take a right turn";
                        } else {
                            message = "Take a left turn";
                        }
                    } else {
                        if (second.equals(Direction.up)) {
                            message = "Take a right turn";
                        } else {
                            message = "Take a left turn";
                        }
                    }

                } else {
                    Direction first = getDirection(currentIndex, currentIndex + 1);
                    if (first.equals(Direction.right)) {
                        message = "Take a right turn";
                    } else if (first.equals(Direction.left)) {
                        message = "Take a left turn";
                    } else {
                        message = "Continue straight";
                    }

                }

                currentIndex++;
                currentDistance = 0;

                mainActivity.tryListener();
            }
        }

        //int i = MainActivity.textToSpeech.speak(""+message, TextToSpeech.QUEUE_ADD, null);
    }

    public Direction getDirection(int first, int second){
        float deltaX = nodes.get(second).getX()-nodes.get(first).getX();
        float deltaY = nodes.get(second).getY()-nodes.get(first).getY();

        Direction direction = Direction.up;
        if(Math.abs(deltaX)>Math.abs(deltaY)){
            if(deltaX>0){
                direction = Direction.right;
            }
            else{
                direction = Direction.left;
            }
        }
        else{
            if(deltaY<0){
                direction = Direction.down;
            }
        }
        return direction;
    }

    public void findShortestPath(int startingId, int destinationId) {
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
        path.add(0,destinationId);
        while(id != startingId) {
            path.add(0,parents[id]);
            id = parents[id];
        }
        shortestPath = path;
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
        Scanner scanner;
        try {
            AssetManager am = context.getAssets();
            scanner = new Scanner(am.open("NodeList.csv"));
        } catch (Exception e) {
            System.exit(0);
            return;
        }

        scanner.nextLine();

        String[] data;
        ArrayList<String> tempEdges = new ArrayList<>();
        while(scanner.hasNext()) {
            data = scanner.nextLine().split(",");
            nodes.add(new Node(data[1], Float.parseFloat(data[5]), Float.parseFloat(data[6])));
            tempEdges.add(data[4]);
        }

//        StringBuilder stringBuilder = new StringBuilder();
//        BufferedReader reader;
//        try {
//            InputStream inputStream = resolver.openInputStream(Uri.fromFile(new File(this.context.getFilesDir(),"NodeList.csv")));
//            reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//
//        }
//
//
//
//        reader.readLine();
//
//        String[] data;
//        ArrayList<String> tempEdges = new ArrayList<>();
//        while(reader.ready()) {
//            data = reader.readLine().split(",");
//            nodes.add(new Node(data[1], Float.parseFloat(data[5]), Float.parseFloat(data[6])));
//            tempEdges.add(data[4]);
//        }

        for(int i = 0; i < tempEdges.size(); i++) {
            data = tempEdges.get(i).split(";");
            for(int j = 0; j < data.length; j++) {
                nodes.get(i).addEdge(nodes.get(Integer.parseInt(data[j])));
            }
        }

        /*Node node0 = new Node("entrance", 89, 78);
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
        nodes.get(8).addEdge(nodes.get(6), 6);*/

        // add edges
        /*for(int i = 0; i < nodes.size(); i++) {

        }*/
    }
}
