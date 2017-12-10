import java.util.HashMap;

public class LSPacket{
	private int seq;
	private int id;
	private int last;
	private HashMap<Integer, HashMap<Integer,Integer>> map;
	private int ttl;
	public LSPacket(int id,int seq,HashMap<Integer, HashMap<Integer,Integer>> map){
		this.setId(id);
		this.setSeq(seq);
		this.map=map;
		this.ttl=10;
		this.setLast(id);
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public HashMap<Integer, HashMap<Integer,Integer>> getMap() {
		return map;
	}
	
	public boolean forward(){
		this.ttl--;
		if(this.ttl==0) return false;
		else return true;
	}
	public int getLast() {
		return last;
	}
	public void setLast(int last) {
		this.last = last;
	}
	
}
