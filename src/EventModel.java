import expressions.Expression;

import java.util.ArrayList;
import java.util.List;

public class EventModel {

    //public Node trueWorld;
    public List<Node> allNodes = new ArrayList<>();
    private int nPlayers;

    public EventModel(int nPlayers) {
        //trueWorld = new Node(nPlayers, trueWorldPre);
        //allNodes.add(trueWorld);
        this.nPlayers = nPlayers;
    }

    public Node createNode(Expression pre) {
        Node node = new Node(nPlayers, pre);
        allNodes.add(node);
        return node;
    }

    public static Node createNode(int nPlayers, Expression pre) {
        return new Node(nPlayers, pre);
    }

    public void createEdge(Node from, Node to, boolean symmetric) {
        int[] agents = new int[nPlayers];
        for (int i = 0; i < nPlayers; i++) {
            agents[i] = i;
        }
        createEdge(from, to, symmetric, agents);
    }

    public void createEdge(Node from, Node to, boolean symmetric, int[] agents) {
        for (int i = 0; i < agents.length; i++) {
            from.edges[agents[i]].add(to);
            if (symmetric) {
                to.edges[agents[i]].add(from);
            }
        }
    }

    public static class Node {

        public Expression pre; //Pre condition.
        public List<Node>[] edges;

        public Node(int nPlayers, Expression pre) {
            edges = new List[nPlayers];
            for (int i = 0; i < edges.length; i++) {
                edges[i] = new ArrayList<>();
            }
            this.pre = pre;
        }



    }

}
