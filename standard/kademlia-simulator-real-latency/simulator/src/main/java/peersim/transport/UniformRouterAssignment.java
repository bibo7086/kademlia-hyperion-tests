/*
 * Copyright (c) 2003-2005 The BISON Project
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package peersim.transport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import peersim.config.*;
import peersim.core.*;

/**
 * Initializes {@link RouterInfo} protocols by assigning routers to them. The number of routers is
 * defined by static singleton {@link E2ENetwork}.
 *
 * @author Alberto Montresor
 * @version $Revision: 1.6 $
 */
public class UniformRouterAssignment implements Control {

  // ---------------------------------------------------------------------
  // Parameters
  // ---------------------------------------------------------------------

  /**
   * Parameter name used to configure the {@link RouterInfo} protocol that should be initialized.
   *
   * @config
   */
  private static final String PAR_PROT = "protocol";

  private static final String PAR_FILE = "file";

  /** Prefix for reading parameters */
  private String prefix;

  /**
   * Ratio between the time units used in the configuration file and the time units used in the
   * Peersim simulator.
   */
  private double ratio;

  /** Name of the file containing the King measurements. */
  private String filename;

  private static final String PAR_RATIO = "ratio";

  // ---------------------------------------------------------------------
  // Methods
  // ---------------------------------------------------------------------

  /** Protocol identifier */
  private int pid;

  // ---------------------------------------------------------------------
  // Initialization
  // ---------------------------------------------------------------------

  /** Reads configuration parameters. */
  public UniformRouterAssignment(String prefix) {
    this.prefix = prefix;
    pid = Configuration.getPid(prefix + "." + PAR_PROT);
    filename = Configuration.getString(prefix + "." + PAR_FILE, null);
    ratio = Configuration.getDouble(prefix + "." + PAR_RATIO, 1);
  }

  // ---------------------------------------------------------------------
  // Methods
  // ---------------------------------------------------------------------

  public void initializeKingParser() {
    // try {
    //   BufferedReader in = null;

    //   if (filename != null) {
    //     try {
    //       in = new BufferedReader(new FileReader(filename));
    //     } catch (FileNotFoundException e) {
    //       throw new IllegalParameterException(
    //           prefix + "." + PAR_FILE, filename + " does not exist");
    //     }
    //   } else {
    //     in =
    //         new BufferedReader(
    //             new InputStreamReader(ClassLoader.getSystemResourceAsStream("t-king.map")));
    //   }

    //   // XXX If the file format is not correct, we will get quite obscure
    //   // exceptions. To be improved.

    //   String line = null;
    //   int size = 0;
    //   int lc = 1;

    //   try {
    //     while ((line = in.readLine()) != null && !line.startsWith("node")) {
    //       lc++;
    //     }

    //     while (line != null && line.startsWith("node")) {
    //       size++;
    //       lc++;
    //       line = in.readLine();
    //     }
    //   } catch (IOException e) {
    //     System.err.println("KingParser: " + filename + ", line " + lc + ":");
    //     e.printStackTrace();
    //     try {
    //       in.close();
    //     } catch (IOException e1) {
    //     }
    //     System.exit(1);
    //   }

    //   E2ENetwork.reset(size, false);

    //   if (line == null) {
    //     System.err.println("KingParser: " + filename + ", line " + lc + ":");
    //     System.err.println("No latency matrix contained in the specified file");
    //     try {
    //       in.close();
    //     } catch (IOException e1) {
    //       System.exit(1);
    //     }
    //   }

    //   System.err.println("KingParser: read " + size + " entries");

    //   try {
    //     in = new BufferedReader(new FileReader(filename));
    //     line = in.readLine(); // Read the first line
    //     line = in.readLine(); // Read the first line
    //   } catch (FileNotFoundException e) {
    //     System.exit(1);
    //   } catch (IOException e) {
    //     e.printStackTrace();
    //   }

    //   try {
    //     do {
    //       StringTokenizer tok = new StringTokenizer(line, ", ");
    //       if (tok.countTokens() != 3) {
    //         System.err.println("KingParser: " + filename + ", line " + lc + ":");
    //         System.err.println("Specified line does not contain a <node1, node2, latency>
    // triple");
    //         try {
    //           in.close();
    //         } catch (IOException e1) {
    //         }
    //         System.exit(1);
    //       }
    //       String nodeToken = tok.nextToken(); // Get the token, e.g., "node0"
    //       int n1 = Integer.parseInt(nodeToken.replace("node", ""));
    //       nodeToken = tok.nextToken(); // Get the token, e.g., "node0"
    //       int n2 = Integer.parseInt(nodeToken.replace("node", ""));
    //       // int latency = (int) (Double.parseDouble(tok.nextToken()) * ratio);
    //       int latency = (int) Math.abs(Double.parseDouble(tok.nextToken()) * ratio);
    //       Node node1 = Network.get(n1);
    //       Node node2 = Network.get(n2);

    //       E2ETransport t1 = (E2ETransport) node1.getProtocol(pid);
    //       E2ETransport t2 = (E2ETransport) node2.getProtocol(pid);

    //       int router1 = t1.getRouter();
    //       int router2 = t2.getRouter();
    //       // System.out.println(
    //       //     "router1: "
    //       //         + router1
    //       //         + " router2 "
    //       //         + router2
    //       //         + ": "
    //       //         + latency
    //       //         + " for porotocl "
    //       //         + node1.getProtocol(pid));

    //       E2ENetwork.setLatency(router1, router2, latency);
    //       lc++;
    //       line = in.readLine();
    //     } while (line != null);

    //     in.close();
    //   } catch (IOException e) {
    //     System.err.println("KingParser: " + filename + ", line " + lc + ":");
    //     e.printStackTrace();
    //     try {
    //       in.close();
    //     } catch (IOException e1) {
    //     }
    //     System.exit(1);
    //   }
    // } catch (Exception ex) {
    //   // Handle exceptions or errors appropriately
    // }
    try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
      String line;
      // Skip the header line
      in.readLine();
      int sz = Network.size();
      boolean resetFile = false;

      BufferedReader resetIn = null; // Separate variable for resetting the file

      for (int i = 0; i < sz; i++) {
        Node node1 = Network.get(i);
        E2ETransport t1 = (E2ETransport) node1.getProtocol(pid);

        for (int j = 0; j < sz; j++) {
          Node node2 = Network.get(j);
          E2ETransport t2 = (E2ETransport) node2.getProtocol(pid);

          if (node1 != node2) {
            if (resetFile) {
              if (resetIn != null) {
                resetIn.close();
              }
              resetIn = new BufferedReader(new FileReader(filename));
              resetIn.readLine(); // Skip the header line
              resetFile = false;
            }

            if (resetIn != null) {
              line = resetIn.readLine();
            } else {
              line = in.readLine();
            }

            if (line == null) {
              resetFile = true; // Set the flag to reset the file on the next iteration
              break;
            }

            StringTokenizer tok = new StringTokenizer(line, ", ");
            if (tok.countTokens() == 2) {
              // Extract latency from the second column of the file
              tok.nextToken(); // Skip the first token
              int latency = (int) Math.abs(Double.parseDouble(tok.nextToken()) * ratio);
              int router1 = t1.getRouter();
              int router2 = t2.getRouter();

              // System.out.println(
              //     "router1: "
              //         + router1
              //         + " router2 "
              //         + router2
              //         + ": "
              //         + latency
              //         + " for protocol "
              //         + node1.getProtocol(pid));

              E2ENetwork.setLatency(router1, router2, latency);
            }
          }
        }
      }

      if (resetIn != null) {
        resetIn.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Initializes given {@link RouterInfo} protocol layer by assigning routers randomly. The number
   * of routers is defined by static singleton {@link E2ENetwork}.
   *
   * @return always false
   */
  public boolean execute() {
    int nsize = Network.size();
    int nrouters = E2ENetwork.getSize();

    for (int i = 0; i < nsize; i++) {
      Node node = Network.get(i);
      E2ETransport t = (E2ETransport) node.getProtocol(pid);
      // int r = CommonState.r.nextInt(nsize);
      t.setRouter(i);
    }

    initializeKingParser();
    return false;
  }
}
