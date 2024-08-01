import java.util.LinkedList;

public class Queue<E>  {
	private LinkedList<E> list;

	public Queue() {
		list = new LinkedList<E>();
	}


	public boolean enqueue(E x) {
		list.addLast(x);
		return true;
	}

	public E dequeue() {
		return list.removeFirst();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public boolean isFull() {
		return false;
	}

	public E peek() {
		return list.getFirst();
	}

	public int size() {
		return list.size();
	}

	public String toString() {
		return list.toString();
	}

}
