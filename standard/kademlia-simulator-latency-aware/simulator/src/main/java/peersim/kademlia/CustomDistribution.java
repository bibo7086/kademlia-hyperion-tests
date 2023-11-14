package peersim.kademlia;

import java.io.FileWriter;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;

public class CustomDistribution implements peersim.core.Control {

  private static final String PAR_PROT = "protocol";
  private int protocolID;
  private UniformRandomGenerator urg;
  private Connection dbConnection;

  public CustomDistribution(String prefix) {
    protocolID = Configuration.getPid(prefix + "." + PAR_PROT);
    urg = new UniformRandomGenerator(KademliaCommonConfig.BITS, CommonState.r);

    //   // Initialize the database connection
    //   try {
    //     Class.forName("org.postgresql.Driver"); // PostgreSQL JDBC driver
    //     dbConnection =
    //         DriverManager.getConnection(
    //             "jdbc:postgresql://localhost:5432/punchr", "punchr", "password");
    //   } catch (Exception e) {
    //     e.printStackTrace();
    //   }
  }

  public boolean execute() {
    List<String> nodeIds = retrieveNodeIdFromDatabase(Network.size());

    // Path to where latency file will be saved
    String latencyFilePath = "latency_vales.csv";

    // BigInteger tmp;
    for (int i = 0; i < Network.size(); ++i) {
      Node generalNode = Network.get(i);
      BigInteger id;
      // BigInteger attackerID = null;
      KademliaNode node;
      // String ip_address;
      id = urg.generate();
      System.out.println(id);
      // node = new KademliaNode(id, randomIpAddress(r), 0);
      node = new KademliaNode(id, "0.0.0.0", 0);

      KademliaProtocol kadProt = ((KademliaProtocol) (Network.get(i).getProtocol(protocolID)));

      generalNode.setKademliaProtocol(kadProt);
      kadProt.setNode(node);
      kadProt.setProtocolID(protocolID);
    }

    // generatelatencies(latencyFilePath)
    String outputFilePath = "random_latency_values.csv";

    // // Fetch node information (random ID and latency) from the database
    // retrieveLatencyFromDatabase(outputFilePath);

    // generatelatencies(latencyFilePath);
    // Close the database connection
    try {
      dbConnection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return false;
  }

  // Retrieve node ID and latency from the database
  private List<String> retrieveNodeIdFromDatabase(int limit) {

    List<String> nodeIds = new ArrayList<>();

    String query = "SELECT multi_hash FROM peers LIMIT ?";

    try {
      PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
      preparedStatement.setInt(1, limit);
      ResultSet resultSet = preparedStatement.executeQuery();

      while (resultSet.next()) {
        String nodeId = new String(resultSet.getString("multi_hash"));
        nodeIds.add(nodeId);
      }
      resultSet.close();
      preparedStatement.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return nodeIds;
  }

  // // Generate random latency based on prefix length
  // private double generateRandomLatency(int prefixLenght) {
  //   // Load the random latency values from the file
  //   Double randomLatencyValue = loadRandomLatencyValuesFromFile("random_latency_values.txt");

  //   return randomLatencyValue; // 0.0 is the default value if not found
  // }

  private void retrieveLatencyFromDatabase(String outputFilePath) {
    try {
      Statement statement = dbConnection.createStatement();
      String query =
          "SELECT remote_id, rtt_avg FROM latency_measurements\n"
              + //
              "WHERE (mtype = 'TO_RELAY' OR mtype = 'TO_REMOTE_THROUGH_RELAY') AND rtt_avg <> -1;\n"
              + //
              "";
      ResultSet result = statement.executeQuery(query);

      // Generate random latency values for peers and save them to a file
      FileWriter fileWriter = new FileWriter(outputFilePath);
      fileWriter.write("peerID, latency\n");

      while (result.next()) {
        long remoteID = result.getLong("remote_id");
        double rttAvg = result.getDouble("rtt_avg");
        // double randomLatency = Math.random() * 9.9 + 0.1;

        fileWriter.write(remoteID + ", " + rttAvg + "\n");
      }
      // Close resources
      result.close();
      statement.close();
      fileWriter.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
