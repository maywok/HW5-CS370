package src;

import java.util.Random;

public class VirtualNetworkSimulation {

    private Node[] threadPoolOfNodes;
    private Random mg;
    private int[][] overlay;

    public VirtualNetworkSimulation(long seed, int N, int K, int bufferSize, long totalMessages) {
        mg = new Random(seed);
        threadPoolOfNodes = new Node[N];
        overlay = generateOverlay(N, K);

        for (int i = 0; i < N; i++) {
            threadPoolOfNodes[i] = new Node(String.valueOf(i), totalMessages / N, seed, K, bufferSize);
        }

        // Set neighbors
        for (int i = 0; i < N; i++) {
            Node[] neighbors = new Node[K];
            for (int j = 0; j < K; j++) {
                neighbors[j] = threadPoolOfNodes[overlay[i][j]];
            }
            threadPoolOfNodes[i].setNeighbors(neighbors);
        }
    }

    private int[][] generateOverlay(int N, int K) {
        int[][] overlay = new int[N][K];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < K; j++) {
                overlay[i][j] = (i + j + 1) % N;  // Example logic for neighbor selection
            }
        }
        return overlay;
    }

    public void startSimulation() {
        for (Node node : threadPoolOfNodes) {
            node.start();
        }

        for (Node node : threadPoolOfNodes) {
            try {
                node.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        reportResults();
    }

    private void reportResults() {
        long globalSumSent = 0, globalSumReceived = 0;
        long totalMessagesSent = 0, totalMessagesReceived = 0;

        for (Node node : threadPoolOfNodes) {
            globalSumSent += node.reportSumSent();
            globalSumReceived += node.reportSumReceived();
            totalMessagesSent += node.reportTotalSent();
            totalMessagesReceived += node.reportTotalReceived();

            System.out.println("Node " + node.getNodeID() + " statistics:");
            System.out.println("Sum Sent: " + node.reportSumSent() + " Sum Received: " + node.reportSumReceived());
            System.out.println("Total Sent: " + node.reportTotalSent() + " Total Received: " + node.reportTotalReceived());
        }

        System.out.println("Final Results:");
        System.out.println("Total Messages Sent -> " + totalMessagesSent);
        System.out.println("Total Messages Received -> " + totalMessagesReceived);
        System.out.println("Global Sum Sent -> " + globalSumSent);
        System.out.println("Global Sum Received -> " + globalSumReceived);
    }

    public static void main(String[] args) {
        long seed = Long.parseLong(args[0]);
        int N = Integer.parseInt(args[1]);
        int K = Integer.parseInt(args[2]);
        int bufferSize = Integer.parseInt(args[3]);
        long totalMessages = Long.parseLong(args[4]);

        VirtualNetworkSimulation simulation = new VirtualNetworkSimulation(seed, N, K, bufferSize, totalMessages);
        simulation.startSimulation();
    }
}
