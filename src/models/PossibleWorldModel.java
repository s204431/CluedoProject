package models;

import expressions.*;
import models.EventModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PossibleWorldModel {

    public List<Node> allNodes = new ArrayList<>();
    //public Node trueWorld;
    private int nPlayers;

    public PossibleWorldModel(int nPlayers, int nRooms, int nWeapons, int nPeople) {
        //Create all possible worlds.
        for (int i = 0; i < nRooms; i++) {
            for (int j = 0; j < nWeapons; j++) {
                for (int k = 0; k < nPeople; k++) {
                    boolean[][] predicates = new boolean[][] {new boolean[nRooms], new boolean[nWeapons], new boolean[nPeople]};
                    predicates[0][i] = true;
                    predicates[1][j] = true;
                    predicates[2][k] = true;
                    Node node = new Node(nPlayers, predicates);
                    allNodes.add(node);
                    /*if (i == trueWorld[0] && j == trueWorld[1] && k == trueWorld[2]) {
                        this.trueWorld = node;
                    }*/
                }
            }
        }
        //Create all edges.
        for (Node node : allNodes) {
            for (Node node2 : allNodes) {
                for (int i = 0; i < node.edges.length; i++) {
                    node.edges[i].add(node2);
                }
            }
        }
        this.nPlayers = nPlayers;
        //Pick a random world as the true world.
        //trueWorld = allNodes.get(new Random().nextInt(allNodes.size()));
    }

    private PossibleWorldModel(List<Node> allNodes, int nPlayers) {
        this.allNodes = allNodes;
        this.nPlayers = nPlayers;
    }

    public boolean evaluateExpression(Expression expression, int[] trueWorld) {
        for (Node node : allNodes) {
            boolean[][] pred = node.predicateValues;
            if (pred[0][trueWorld[0]] && pred[1][trueWorld[1]] && pred[2][trueWorld[2]]) {
                if (evaluateExpression(expression, node)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean evaluateExpression(Expression expression, Node node) {
        if (expression instanceof Or) {
            for (Expression e : expression.operands) {
                if (evaluateExpression(e, node)) {
                    return true;
                }
            }
            return false;
        }
        else if (expression instanceof And) {
            for (Expression e : expression.operands) {
                if (!evaluateExpression(e, node)) {
                    return false;
                }
            }
            return true;
        }
        else if (expression instanceof Not) {
            return !evaluateExpression(expression.operands[0], node);
        }
        else if (expression instanceof Predicate p) {
            return node.predicateValues[p.type][p.number];
        }
        else if (expression instanceof Value v) {
            return v.value;
        }
        else if (expression instanceof Knows k) {
            for (Node node1 : node.edges[k.agentIndex]) {
                if (!evaluateExpression(k.operands[0], node1)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void publicAnnouncement(Expression expression) {
        //Find nodes where expression is false.
        boolean removeOne = false;
        for (Node node : allNodes) {
            if (!evaluateExpression(expression, node)) {
                node.markedForRemoval = true;
                removeOne = true;
            }
        }
        if (!removeOne) {
            //Return since no worlds have been removed.
            return;
        }
        //Remove edges.
        for (Node node : allNodes) {
            for (int i = 0; i < node.edges.length; i++) {
                List<Node> newList = new ArrayList<>();
                for (int j = 0; j < node.edges[i].size(); j++) {
                    if (!node.edges[i].get(j).markedForRemoval) {
                        newList.add(node.edges[i].get(j));
                    }
                }
                node.edges[i] = newList;
            }
        }
        //Remove nodes.
        allNodes.removeIf(node -> node.markedForRemoval);
    }

    public void privateAnnouncement(Expression expression, int agentIndex) {

    }

    //Perform a product update with a given event.
    public void productUpdate(EventModel event, int[] trueWorld) {
        List<Node> newNodes = new ArrayList<>();
        for (int i = 0; i < allNodes.size(); i++) {
            for (int j = 0; j < event.allNodes.size(); j++) {
                Node node = allNodes.get(i);
                EventModel.Node eventNode = event.allNodes.get(j);
                if (evaluateExpression(event.allNodes.get(j).pre, allNodes.get(i))) {
                    Node newNode = new Node(nPlayers, node.predicateValues);
                    newNode.eventNode = eventNode;
                    newNode.oldNode = node;
                    newNodes.add(newNode);
                    /*if (trueWorld == node && event.trueWorld == eventNode) {
                        trueWorld = newNode;
                    }*/
                }
            }
        }
        boolean[] hasEdge = new boolean[newNodes.size()];
        for (int i = 0; i < newNodes.size(); i++) {
            Node node1 = newNodes.get(i);
            for (int j = 0; j < newNodes.size(); j++) {
                Node node2 = newNodes.get(j);
                for (int k = 0; k < nPlayers; k++) {
                    if (node1.eventNode.edges[k].contains(node2.eventNode) && node1.oldNode.edges[k].contains(node2.oldNode)) {
                        node1.edges[k].add(node2);
                        hasEdge[i] = true;
                        hasEdge[j] = true;
                    }
                }
            }
        }
        allNodes = new ArrayList<>();
        for (int i = 0; i < newNodes.size(); i++) {
            Node node = newNodes.get(i);
            if (hasEdge[i] || Arrays.equals(trueWorld, node.predicatesAsInts())) {
                allNodes.add(newNodes.get(i));
            }
        }
    }

    public PossibleWorldModel copy() {
        List<Node> allNodesCopy = new ArrayList<>();
        for (int i = 0; i < allNodes.size(); i++) {
            Node oldNode = allNodes.get(i);
            boolean[][] predicateValues = new boolean[oldNode.predicateValues.length][];
            for (int j = 0; j < predicateValues.length; j++) {
                predicateValues[j] = new boolean[oldNode.predicateValues[j].length];
                for (int k = 0; k < predicateValues[j].length; k++) {
                    predicateValues[j][k] = oldNode.predicateValues[j][k];
                }
            }
            Node node = new Node(nPlayers, predicateValues);
            allNodesCopy.add(node);
        }
        for (int i = 0; i < allNodes.size(); i++) {
            List<Node>[] edges = new List[nPlayers];
            List<Node>[] oldEdges = allNodes.get(i).edges;
            for (int j = 0; j < nPlayers; j++) {
                edges[j] = new ArrayList<>();
                for (Node node : oldEdges[j]) {
                    int nodeIndex = allNodes.indexOf(node);
                    edges[j].add(allNodesCopy.get(nodeIndex));
                }
            }
            allNodesCopy.get(i).edges = edges;
        }
        return new PossibleWorldModel(allNodesCopy, nPlayers);
    }

    public static class Node {
        private List<Node>[] edges;
        private boolean[][] predicateValues;
        private boolean markedForRemoval = false;

        public EventModel.Node eventNode; //Used for product updates.
        public Node oldNode; //Used for product updates.

        public Node(int nPlayers, boolean[][] predicateValues) {
            edges = new List[nPlayers];
            for (int i = 0; i < edges.length; i++) {
                edges[i] = new ArrayList<>();
            }
            this.predicateValues = predicateValues;
        }

        public int[] predicatesAsInts() {
            int[] result = new int[predicateValues.length];
            for (int i = 0; i < predicateValues.length; i++) {
                for (int j = 0; j < predicateValues[i].length; j++) {
                    if (predicateValues[i][j]) {
                        result[i] = j;
                        break;
                    }
                }
            }
            return result;
        }
    }
}
