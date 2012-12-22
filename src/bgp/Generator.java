package bgp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Generator {

	static Random r = new Random(); // or use Random(seed)

	public static void main(String[] args) {
		try {

			File events = new File("generated_events.txt");
			events.delete();
			File events2 = new File("generated_evets_without_updates-txt");
			events2.delete();
			FileWriter eventwriter = new FileWriter("generated_events.txt",
					true);
			BufferedWriter bufferevent = new BufferedWriter(eventwriter);
			FileWriter eventwriter2 = new FileWriter(
					"generated_evets_without_updates-txt", true);
			BufferedWriter bufferevent2 = new BufferedWriter(eventwriter2);

			double time = 0, temp, uniform;
			int intime;
			int coin, type;
			int count = 0, lambda = 5;
			int stoptime = 5000;
			int prefixes_per_node = 20;
			int initalized_prefixesXnode_at_start = 4;
			int numberofnodes = 2500;

			int wd_prob = 72;
			int new_pref_prob = 928;
			int old_prefix_prob = 1000 - wd_prob - new_pref_prob;

			ArrayList<Table> topology = new ArrayList<Table>();
			ArrayList<Table> new_prefixes = new ArrayList<Table>();

			for (int i = 1; i <= numberofnodes; i++) {
				for (int k = 0; k < initalized_prefixesXnode_at_start; k++)
					topology.add(new Table(i + numberofnodes * k, i, 1)); // topology.size()=60
				// -->
				// [0,59] --->
				// prefixes from
				// 1 to 60
			}
			count = topology.get(topology.size() - 1).getId();

			do {
				new_prefixes.add(new Table(count, 0, 0)); // new_prefixes.size()=600
															// --> [0,599] --->
															// prefixes from 61
															// to 660
				count++;
			} while (count <= numberofnodes * prefixes_per_node);

			do {
				uniform = r.nextDouble();
				temp = -Math.log(1 - uniform) / lambda;
				time = time + temp;
				intime = (int) time;
				coin = (r.nextInt(1000)) + 1; // uniform random number [1,1000]
												// for event_type generator
				if (coin <= wd_prob) {
					type = 1; // withdrawal with probability 0,072
					int position = r.nextInt(topology.size());
					String message = type + "\t"
							+ topology.get(position).getId() + "\t"
							+ topology.get(position).getNode() + "\t" + intime
							+ "\n";

					Table a = topology.get(position);
					a.setState(0);
					topology.set(position, a);
					new_prefixes.add(a);
					topology.remove(position);

					bufferevent.write(message);
					bufferevent.flush();
					bufferevent2.write(message);
					bufferevent2.flush();

					// time = time + temp;
				} else if (wd_prob < coin && coin <= new_pref_prob + wd_prob) {
					type = 2; // new prefix announcement with probability 0,928
					int position = r.nextInt(new_prefixes.size());
					int random_node = topology.get(r.nextInt(topology.size()))
							.getNode();
					String message = type + "\t"
							+ new_prefixes.get(position).getId() + "\t"
							+ random_node + "\t" + intime + "\n";

					Table a = new_prefixes.get(position);
					a.setState(1);
					a.setNode(random_node);
					new_prefixes.set(position, a);
					topology.add(a);
					new_prefixes.remove(position);

					bufferevent.write(message);
					bufferevent.flush();
					bufferevent2.write(message);
					bufferevent2.flush();

					// time = time + temp;
				} else {
					type = 3; // old prefix update with probability 0
					int position = r.nextInt(topology.size());
					String message = "2\t" + topology.get(position).getId()
							+ "\t" + topology.get(position).getNode() + "\t"
							+ intime + "---------------\n";

					bufferevent.write(message);
					bufferevent.flush();
				}
			} while (time < stoptime);
			bufferevent.close();
			bufferevent2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
