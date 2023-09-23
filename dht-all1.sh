#!/bin/bash
#SBATCH --job-name=dht-all1
#SBATCH --nodes=1
#SBATCH --ntasks-per-node=18
#SBATCH --cpus-per-task=2
#SBATCH --mem-per-cpu=10G
#SBATCH --partition=month
#SBATCH --time=700:00:00

# Run the standard random script with its own output log
python3 -u "/users/addr777/archive/development/kademlia-tests/standard/kademlia-simulator-random/scripts/unix_multi_lookup.py" > "/users/addr777/archive/development/kademlia-tests/standard/kademlia-simulator-random/simulator/output.log" 2> "/users/addr777/archive/development/kademlia-tests/standard/kademlia-simulator-random/simulator/error.log" &

# Run the standard pareto Python script with its own output log
python3 -u "/users/addr777/archive/development/kademlia-tests/standard/kademlia-simulator-pareto/scripts/unix_multi_lookup.py" > "/users/addr777/archive/development/kademlia-tests/standard/kademlia-simulator-pareto/simulator/output.log" 2> "/users/addr777/archive/development/kademlia-tests/standard/kademlia-simulator-pareto/simulator/error.log" &

# Wait for all background jobs to finish
wait
