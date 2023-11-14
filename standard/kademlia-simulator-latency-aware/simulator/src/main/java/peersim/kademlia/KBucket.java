package peersim.kademlia;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.transport.E2ETransport;

/**
 * import java.util.Map;
 *
 * <p>This class implements a kademlia k-bucket. Functions for the management of the neighbours
 * update are also implemented.
 *
 * @author Daniele Furlan, Maurizio Bonani
 * @version 1.0
 */
public class KBucket implements Cloneable {

  // k-bucket array
  protected TreeMap<BigInteger, Long> neighbours = null;

  /** Empty constructor for initializing the k-bucket TreeMap. */
  public KBucket() {
    neighbours = new TreeMap<BigInteger, Long>();
  }

  /**
   * Add a neighbour to this k-bucket.
   *
   * @param node the neighbor to be added.
   * @return true if the neighbor is successfully added; false if the k-bucket is already full.
   */
  public boolean addNeighbour(BigInteger node) {
    long time = CommonState.getTime();
    if (neighbours.size() < KademliaCommonConfig.K) { // k-bucket isn't full
      neighbours.put(node, time); // add neighbor to the tail of the list
      return true;
    }
    return false;
  }

  public boolean addNeighbour(E2ETransport transport, Node src, Node dest, BigInteger node) {
    long newLatency = transport.getLatency(src, dest);
    Long existingLatency = null;

    if (neighbours.size() < KademliaCommonConfig.K) { // k-bucket isn't full
      neighbours.put(node, newLatency); // add neighbor to the tail of the list
      return true;
    } else {
      // Check if the new node has a better latency than any existing node
      Map.Entry<BigInteger, Long> maxLatencyEntry = null;

      for (Map.Entry<BigInteger, Long> neighbour : neighbours.entrySet()) {
        existingLatency = neighbour.getValue();

        if (maxLatencyEntry == null || existingLatency > maxLatencyEntry.getValue()) {
          maxLatencyEntry = neighbour;
        }
      }

      if (newLatency < maxLatencyEntry.getValue()) {
        System.out.println(
            "Removing "
                + maxLatencyEntry.getKey()
                + " with latency of "
                + maxLatencyEntry.getValue());
        removeNeighbour(maxLatencyEntry.getKey());
        System.out.println("Adding " + node + " with latency of " + newLatency);
        neighbours.put(node, newLatency);
        return true;
      }
    }
    return false;
  }
  /**
   * Remove a neighbour from this k-bucket.
   *
   * @param node the neighbour to be removed.
   */
  public void removeNeighbour(BigInteger node) {
    neighbours.remove(node);
  }

  /**
   * Returns the count of the neigbors of the k-bucket object (this k-bucket).
   *
   * @return the number of neighbors in the k-bucket.
   */
  public int getNeighborCount() {
    return neighbours.size();
  }

  /**
   * Returns a deep copy of the k-bucket object.
   *
   * @return a cloned k-bucket object.
   */
  public Object clone() {
    KBucket dolly = new KBucket();
    for (BigInteger node : neighbours.keySet()) {
      dolly.neighbours.put(new BigInteger(node.toByteArray()), 0l);
    }
    return dolly;
  }

  /**
   * Returns a string representation of the k-bucket object.
   *
   * @return a string representation of the k-bucket object.
   */
  public String toString() {
    String res = "{\n";

    for (BigInteger node : neighbours.keySet()) {
      res += node + "\n";
    }

    return res + "}";
  }
}
