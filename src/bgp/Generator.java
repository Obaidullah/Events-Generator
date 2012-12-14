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
			File events2= new File ("generated_evets_without_updates-txt");
			events2.delete();
			FileWriter eventwriter = new FileWriter("generated_events.txt", true);
			BufferedWriter bufferevent = new BufferedWriter(eventwriter);
			FileWriter eventwriter2 = new FileWriter("generated_evets_without_updates-txt", true);
			BufferedWriter bufferevent2 = new BufferedWriter(eventwriter2);
			
			double time = 0, temp, uniform;
			int coin, type;
			int count = 0, lambda = 1;
			int prefixes_per_node = 10;

			int wd_prob = 48;
			int new_pref_prob = wd_prob;
			int old_prefix_prob = 1000 - (2 * wd_prob);

			ArrayList<Table> topology = new ArrayList<Table>();
			ArrayList<Table> new_prefixex = new ArrayList<Table>();

			for (int i = 1; i <= 60; i++)
				topology.add(new Table(i, i, 1)); // topology.size()=60 -->
													// [0,59] ---> prefixes from
													// 1 to 60

			count = 61;

			for (int i = 1; i <= 60 * prefixes_per_node; i++) {
				new_prefixex.add(new Table(count, 0, 0)); // new_prefixes.size()=600
															// --> [0,599] --->
															// prefixes from 61
															// to 660
				count++;
			}

			do {
				uniform = r.nextDouble();
				temp = -Math.log(1 - uniform) / lambda;
				time = time + temp;
				coin = (r.nextInt(1000)) + 1; // uniform random number [1,1000]
												// for event_type generator
				if (coin <= wd_prob) {
					type = 1; // withdrawal with probability 0,048
					int position = r.nextInt(topology.size());
					String message = type+"\t" + topology.get(position).getId()
							+ "\t" + topology.get(position).getNode() + "\t"
							+ time + "\n";
					Table a = topology.get(position);
					a.setState(0);
					topology.set(position, a);
					new_prefixex.add(a);
					topology.remove(position);
					bufferevent.write(message);
					bufferevent.flush();
					bufferevent2.write(message);
					bufferevent2.flush();
				} else if (wd_prob < coin && coin <= new_pref_prob + old_prefix_prob) {
					type = 3; // old prefix update with probability 0,904
					int position = r.nextInt(topology.size());
					String message = "2\t" + topology.get(position).getId()
							+ "\t" + topology.get(position).getNode() + "\t"
							+ time + "\n";
					bufferevent.write(message);
					bufferevent.flush();
				} else {
					type = 2; // new prefix announcement with probability 0,048
					int position = r.nextInt(new_prefixex.size());
					int random_node= r.nextInt(topology.size());
					String message = type+"\t" + new_prefixex.get(position).getId()
							+ "\t" + random_node + "\t"
							+ time + "\n";
					Table a = new_prefixex.get(position);
					a.setState(1);
					a.setNode(random_node);
					new_prefixex.set(position, a);
					topology.add(a);
					new_prefixex.remove(position);
					bufferevent.write(message);
					bufferevent.flush();
					bufferevent2.write(message);
					bufferevent2.flush();
				}
			} while (time < 500);
			bufferevent.close();
			bufferevent2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
