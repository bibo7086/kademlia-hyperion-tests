## Non-Standard Experiments: Kademlia Simulator

This folder contains simulation files for non-standard experiments in the Kademlia network, specifically focusing on variations in the routing table behavior. In these simulations, we introduce make use of a deviation from the standard approach for handling cases where the desired number of nodes is not found within a specific k-bucket. Instead of the conventional approache of starting from the 0th prefix length and checking all k-buckets, we check adjacent k-buckets and retrieve nodes from there.

Simulation Files:
1. Optimal Distribution
Folder: kademlia-simulator-optimal
Description: This simulation checks the performance of the Kademlia DHT with an optimal distribution of nodes. 

2. Pareto Distribution
Folder:  kademlia-simulator-pareto
Description: This simulation checks the performance of the Kademlia DHT under a Pareto distribution of nodes. Similar to the optimal distribution, the routing table adapts by checking adjacent k-buckets when the required number of nodes is not found within a specific k-bucket.

3. Random Distribution
Folder:  kademlia-simulator-optimal
Description: This simulation checks the performance of the kademlia DHT performance when nodes are added randomly during the bootstrap process. The routing table implements our non-standard approach of checking adjacent k-buckets for missing nodes in contrast to the vanilla Kademlia. 

Routing Table Modification:
In the non-standard experiments, the modification to the routing table occurs when the desired number of nodes is not present in a specific k-bucket. Instead of stopping the search within that bucket or iteratively checking all k-buckets (0-255), we check neighboring/adjacent k-buckets to supplement the node retrieval process.