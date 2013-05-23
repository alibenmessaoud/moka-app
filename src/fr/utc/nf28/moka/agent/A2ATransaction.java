package fr.utc.nf28.moka.agent;

/**
 * common Transaction between two agents
 */
public class A2ATransaction {
	public final String type;
	public Object content;

	public A2ATransaction(String type) {
		this.type = type;
	}

	public A2ATransaction(String type, Object content) {
		this(type);
		this.content = content;
	}
}
