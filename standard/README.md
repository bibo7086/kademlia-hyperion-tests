## Standard Implementation: Kademlia Simulator

This directory contains simulation files for the standard implementation of the Kademlia network. The simulation focuses on the fundamental approach of retrieving the closest neighbors to a given key using the XOR metric, a core principle of Kademlia's distributed hash table (DHT).


Simulation Files:
1. Optimal Distribution
Folder: kademlia-simulator-optimal
Description: This simulation checks the performance of the Kademlia DHT with an optimal distribution of nodes. 

2. Pareto Distribution
Folder:  kademlia-simulator-pareto
Description: This simulation checks the performance of the Kademlia DHT under a Pareto distribution of nodes. Similar to the optimal distribution, the routing table iteratively checks all k-buckets when the required number of nodes is not found within a specific k-bucket.

3. Random Distribution
Folder:  kademlia-simulator-optimal
Description: This simulation checks the performance of the kademlia DHT performance when nodes are added randomly during the bootstrap process. The routing table implements the standard approach of iteratively checking all k-buckets for missing nodes in contrast to the vanilla Kademlia.
### Key Method: `getNeighboursXor_original`

The heart of the standard implementation lies in the method `getNeighboursXor_original`. This method retrieves the closest neighbors to a specified key from the appropriate k-bucket using the XOR metric. The process involves:



1. **Common Prefix Length:**
   - Calculate the length of the longest common prefix between the source node ID (`src`) and the target key.

2. **Full K-Bucket Check:**
   - Check if the k-bucket at the calculated XOR distance (`prefixLen`) is full.
   - If full, return the nodes from that k-bucket.

3. **Retrieve Neighbors from K-Buckets:**
   - If not full, collect neighbor candidates from all k-buckets by iterating through prefix lengths.
   - Remove the source ID from the candidate list.

4. **Sort and Select Best Neighbors:**
   - Utilize a TreeMap to organize neighbors based on XOR distance from the target key.
   - Select the best neighbors, ensuring that the result array contains the closest ones up to the required number (`KademliaCommonConfig.K`).

This standard implementation follows the conventional Kademlia approach for neighbor retrieval, using XOR distance and k-bucket organization. 