import os
import shutil
import multiprocessing as mp
from common_code import *

# Configuration parameters
node_sizes = {
    128: 123456789,
    # 256: 67890,
    # 512: 45678,
    # 1024: 98765,
    # 2048: 54321,
    # 4096: 24680,
    # 8192: 13579,
    # 16384: 55555,
    # 32768: 88888,
    # 65536: 22222, 
    # 5000: 654654, 
    # 10000: 319132, 
}

find_modes = [0, 3]  # List of find modes

config_files = unix_config_files # List of config file paths
output_dir = unix_output_dir # Output directory path
log_dir = unix_log_dir  # Log directory path
base_path = unix_base_path
jar_paths = unix_jar_paths
target_path = target_path
classpath = unix_classpath

def run_sim(config_file, size, seed, find_mode, lock):
    try:
        # Get the base name of the config file 
        config_basename = os.path.basename(config_file)

        # Create a separate directory for each configuration
        config_name = os.path.splitext(os.path.basename(config_file))[0]
        output_dir_config = os.path.join(output_dir, config_name)
        os.makedirs(output_dir_config, exist_ok=True)

        # Generate a unique name for the to be copied config file 
        unique_name = f"{size}_{find_mode}"
        # config_copy = os.path.join(os.path.dirname(output_dir_config), f"{os.path.splitext(config_basename)[0]}_{unique_name}.cfg")
        config_copy = os.path.join(output_dir_config, f"{os.path.splitext(config_basename)[0]}_{unique_name}.cfg")

        # Create a separate copy of the config file for this simulation
        shutil.copy(config_file, config_copy)

        # Modify the parameters in the config file based on the size, seed, and find mode
        change_key(config_copy, "SIZE", size)
        change_key(config_copy, "random.seed", seed)
        change_key(config_copy, "FINDMODE", find_mode)

        # # Move the config file to the output directory
        # shutil.move(config_copy, output_dir_config)


        # Run the simulation
        os.system(f"java -Xmx200000m -cp ../simulator/lib/djep-1.0.0.jar:../simulator/lib/jep-2.3.0.jar:../simulator/target/service-discovery-1.0-SNAPSHOT.jar:../simulator/lib/gs-core-2.0.jar:../simulator/lib/pherd-1.0.jar:../simulator/lib/mbox2-1.0.jar:../simulator/lib/gs-ui-swing-2.0.jar -ea peersim.Simulator {config_copy} > /dev/null 2> /dev/null")
       
        # Acquire lock to ensure exclusive access to the log folder
        # lock.acquire()
        # Move the generated CSV files to the appropriate log directory
        log_dir_config = os.path.join(log_dir, f"log_{size}_{find_mode}")
        os.makedirs(log_dir_config, exist_ok=True)
        shutil.move("../simulator/logs/count.csv", os.path.join(log_dir_config, f"count_{size}_{find_mode}.csv"))
        shutil.move("../simulator/logs/messages.csv", os.path.join(log_dir_config, f"messages_{size}_{find_mode}.csv"))
        shutil.move("../simulator/logs/operation.csv", os.path.join(log_dir_config, f"operation_{size}_{find_mode}.csv"))
        shutil.move("../simulator/logs/routingtable.csv", os.path.join(log_dir_config, f"routing_table_{size}_{find_mode}.csv"))

        # Move the generated log files to the appropriate log folder/directory
        log_dir_config = os.path.join(log_dir, f"log_{size}_{find_mode}")
        os.makedirs(log_dir_config, exist_ok=True)
       
        print("Simulation completed:", config_file, "with size", size, "seed", seed, "find mode", find_mode)
        

        # Release the lock
        # lock.release()

    except Exception as e:
        print("Error occurred during simulation:", e)

def main():
    os.system(f"rm -rf {output_dir}")
    os.system(f"rm -rf {log_dir}")

    os.makedirs(output_dir, exist_ok=True)
    os.makedirs(log_dir, exist_ok=True)

    # Create a multiprocessing Pool
    # pool = mp.Pool() # Asynchronous calls will open a can of worms 
    pool = mp.Pool(mp.cpu_count())
    
    # Create a lock for synchronizing access to the log folder
    manager = mp.Manager()
    lock = manager.Lock()

    for config_file in config_files:
        for find_mode in find_modes:
            for size, seed in node_sizes.items():
                print("Running", config_file, "with size", size, "seed", seed, "find mode", find_mode)
                pool.apply(run_sim, (config_file, size, seed, find_mode, lock))

    # Close the pool and wait for all processes to complete
    pool.close()
    pool.join()

if __name__ == '__main__':
    main()
