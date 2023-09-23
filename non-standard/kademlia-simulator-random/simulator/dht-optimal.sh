#!/bin/bash
#SBATCH --job-name=dht-ns-optimal
#SBATCH --output=output.log
#SBATCH --error=error.log
#SBATCH --nodes=1
#SBATCH --ntasks-per-node=18
#SBATCH --cpus-per-task=2
#SBATCH --mem-per-cpu=10G
#SBATCH --partition=week
#SBATCH --time=168:00:00

# Remove or truncate the output and error log files
> output.log
> error.log

python3 -u "../scripts/unix_multi_lookup.py"

