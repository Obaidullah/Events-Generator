package bgp;

public class Table {
	public int prefix;
	public int node;
	public int state;
	
	public Table()
	{
		prefix=0;
		node=0;
		state=0;
	}
	
	public Table (int PREFIX, int NODE, int STATE)
	{
		prefix = PREFIX;
		node = NODE;
		state = STATE;
	}

	public int getId() {
		return prefix;
	}

	public void setId(int id) {
		this.prefix = id;
	}

	public int getNode() {
		return node;
	}

	public void setNode(int node) {
		this.node = node;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
}
