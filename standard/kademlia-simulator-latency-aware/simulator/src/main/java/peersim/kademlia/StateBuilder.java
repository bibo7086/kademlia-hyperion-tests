package peersim.kademlia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.transport.E2ETransport;
import peersim.transport.Transport;

/**
 * Initialization class that performs the bootsrap filling the k-buckets of all initial nodes.<br>
 * In particular every node is added to the routing table of every other node in the network. In the
 * end however the various nodes don't have the same k-buckets because when a k-bucket is full a
 * random node in it is deleted.
 *
 * @author Daniele Furlan, Maurizio Bonani
 * @version 1.0
 */
public class StateBuilder implements peersim.core.Control {

  private static final String PAR_PROT = "protocol";
  private static final String PAR_TRANSPORT = "transport";

  private String prefix;
  private int kademliaid;
  private int transportid;

  /** Transport object used for communication. */
  private E2ETransport transport;

  /**
   * Constructor method for the StateBuilder class. It performs the necessary initialization of the
   * prefix of the parameters, IDs of the KademliaProtocol and the transport protocol Protocol.
   *
   * @param prefix the prefix string of the parameters read from the configuration file
   */
  public StateBuilder(String prefix) {
    this.prefix = prefix;
    kademliaid = Configuration.getPid(this.prefix + "." + PAR_PROT);
    transportid = Configuration.getPid(this.prefix + "." + PAR_TRANSPORT);
  }

  /**
   * Returns the Kademlia protocol of a node at a given index in the network.
   *
   * @param i the index of the node in the network
   * @return the Kademlia protocol of the node
   */
  public final KademliaProtocol get(int i) {
    return ((KademliaProtocol) (Network.get(i)).getProtocol(kademliaid));
  }

  /**
   * Returns the transport protocol of a node at a given index in the network.
   *
   * @param i the index of the node in the network
   * @return the transport protocol of the node
   */
  public final Transport getTr(int i) {
    return ((Transport) (Network.get(i)).getProtocol(transportid));
  }

  /**
   * Prints the given object.
   *
   * @param o the object to print
   */
  public static void o(Object o) {
    System.out.println(o);
  }

  /**
   * Executes the Kademlia network by sorting the nodes in ascending order of nodeID, and randomly
   * adding 100 (not the 50 mentioned in the previous comment) nodes to each node's k-bucket. Then
   * adds 50 nearby nodes to each node's k-bucket.
   *
   * @return always false
   */
  public boolean execute() {
    // Path to where latency file will be saved
    String latencyFilePath = "latency_values.csv";

    // Sort the network by nodeId (Ascending)
    Network.sort(
        new Comparator<Node>() {
          /**
           * Compares the node IDs of two nodes.
           *
           * @param o1 the first node
           * @param o2 the second node
           * @return 0 if same, negative if o1 < 02, and positive if o1 > o2
           */
          public int compare(Node o1, Node o2) {
            Node n1 = (Node) o1;
            Node n2 = (Node) o2;
            KademliaProtocol p1 = (KademliaProtocol) (n1.getProtocol(kademliaid));
            KademliaProtocol p2 = (KademliaProtocol) (n2.getProtocol(kademliaid));
            return Util.put0(p1.getKademliaNode().getId())
                .compareTo(Util.put0(p2.getKademliaNode().getId()));
          }
        });

    // generatelatencies(latencyFilePath);
    int sz = Network.size();
    // For every node, add 100 random nodes to its k-bucket - not sure why this was 50 previously...
    for (int i = 0; i < sz; i++) {
      Node iNode = Network.get(i);
      KademliaProtocol iKad = (KademliaProtocol) (iNode.getProtocol(kademliaid));
      BigInteger iNodeId = iKad.getKademliaNode().getId();

      for (int k = 0; k < 100; k++) {
        Node jNode = Network.get(CommonState.r.nextInt(sz));
        KademliaProtocol jKad = (KademliaProtocol) (jNode.getProtocol(kademliaid));
        BigInteger jNodeId = jKad.getKademliaNode().getId();
        if (!jNodeId.equals(iNodeId)) {

          transport = (E2ETransport) (Network.prototype).getProtocol(transportid);
          iKad.getRoutingTable()
              .addNeighbour(transport, iNode, jNode, jKad.getKademliaNode().getId());
        }
      }
    }

    // Add 50 nearby nodes to each node's k-bucket based on proximity to ID
    for (int i = 0; i < sz; i++) {
      Node iNode = Network.get(i);
      KademliaProtocol iKad = (KademliaProtocol) (iNode.getProtocol(kademliaid));
      BigInteger iNodeId = iKad.getKademliaNode().getId();

      int start = i + 1;
      if (start > sz - 50) {
        start = sz - 25;
      }
      for (int k = 0; k < 50; k++) {
        start++;
        if (start < sz) {
          Node jNode = Network.get(start);
          KademliaProtocol jKad = (KademliaProtocol) (jNode.getProtocol(kademliaid));
          BigInteger jNodeId = jKad.getKademliaNode().getId();
          if (!jNodeId.equals(iNodeId)) {

            transport = (E2ETransport) (Network.prototype).getProtocol(transportid);
            iKad.getRoutingTable()
                .addNeighbour(transport, iNode, jNode, jKad.getKademliaNode().getId());
          }
        }
      }
    }

    return false;
  } // end execute()

  private void generatelatencies(String pairlatencies) {
    try (FileWriter fileWriter = new FileWriter(pairlatencies)) {
      fileWriter.write("sourceNode, Destination, latency\n");
      for (int i = 0; i < Network.size(); i++) {
        KademliaProtocol ikad = (KademliaProtocol) (Network.get(i).getProtocol(kademliaid));
        BigInteger sourceNodeId = ikad.getKademliaNode().getId();

        System.out.println("the source node id is : " + sourceNodeId);
        for (int j = 0; j < Network.size(); j++) {
          if (i != j) {
            KademliaProtocol jkad = (KademliaProtocol) (Network.get(j).getProtocol(kademliaid));
            BigInteger targetNodeId = jkad.getKademliaNode().getId();

            // Calculate the prefix length between the source node and the target node IDs
            int prefixLength = Util.prefixLen(sourceNodeId, targetNodeId);

            // Generate a random latency values based on prefix lenght
            double randomLatency = loadRandomLatencyValuesFromFile("random_latency_values.csv");
            fileWriter.write(sourceNodeId + "," + targetNodeId + "," + randomLatency + "\n");
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Double loadRandomLatencyValuesFromFile(String filePath) {
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String header = reader.readLine();
      if (header == null) {
        return -1.0;
      }

      // Number of lines in the file (excluding the header)
      long lineCount = reader.lines().count();

      if (lineCount > 0) {
        int randomLine = new Random().nextInt((int) lineCount);

        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
          Optional<String> selectedLine = lines.skip(1 + randomLine).findFirst(); // Skip the header
          if (selectedLine.isPresent()) {
            String[] parts = selectedLine.get().split(",");
            if (parts.length == 2) {
              return Double.parseDouble(parts[1].trim());
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Return -1 to signal the absence of a latency
    return -1.0;
  }
}
