import expressions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PossibleWorldModel {

    public List<Node> allNodes = new ArrayList<>();
    public Node trueWorld;

    public PossibleWorldModel(int nPlayers, int nRooms, int nWeapons, int nPeople) {
        //Create all possible worlds.
        for (int i = 0; i < nRooms; i++) {
            for (int j = 0; j < nWeapons; j++) {
                for (int k = 0; k < nPeople; k++) {
                    boolean[][] predicates = new boolean[][] {new boolean[nRooms], new boolean[nWeapons], new boolean[nPeople]};
                    predicates[0][i] = true;
                    predicates[1][j] = true;
                    predicates[2][k] = true;
                    allNodes.add(new Node(nPlayers, predicates));
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
        //Pick a random world as the true world.
        trueWorld = allNodes.get(new Random().nextInt(allNodes.size()));
    }

    public boolean evaluateExpression(Expression expression) {
        return evaluateExpression(expression, trueWorld);
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
            return !evaluateExpression(expression.operands[0]);
        }
        else if (expression instanceof Predicate p) {
            return node.predicateValues[p.type][p.number];
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


    public static class Node {
        private List<Node>[] edges;
        private boolean[][] predicateValues;
        private boolean markedForRemoval = false;

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
