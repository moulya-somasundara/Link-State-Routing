import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Router{
	private int id;
	private String address;
	private HashMap<Integer,HashMap<Integer,Integer>> map;
	private LinkedList<Router> dcr;
	private int seq;
	private HashMap<Integer,Integer> routingRecord;
	private String state;
	private LinkedList<RouterConnecter> rclist;
	private HashMap<Integer,int[]> paths;
	private HashMap<Integer,Integer> minds;
	private HashMap<Integer,Integer> ongoings;
	private HashSet<Integer> open;
	private HashSet<Integer> close;
	public Router(int id, String address){
		this.setId(id);
		this.setAddress(address);
		this.dcr=new LinkedList<Router>();
		this.map=new HashMap<Integer,HashMap<Integer,Integer>>();
		this.routingRecord=new HashMap<Integer,Integer>();
		this.state="running";
		this.rclist=new LinkedList<RouterConnecter>();
		this.seq=0;
		this.paths=new HashMap<Integer,int[]>();
		minds=new HashMap<Integer,Integer>();
		ongoings=new HashMap<Integer,Integer>();
	}

	public void init(){
		for(Router r:dcr) rclist.add(new RouterConnecter(r));
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public LinkedList<Router> getDcr() {
		return dcr;
	}

	public HashMap<Integer,HashMap<Integer,Integer>> getMap() {
		return map;
	}
	
	public void addDCR(Router router,int cost){
			dcr.add(router);
			if(map.containsKey(id)){
				map.get(id).put(router.getId(), cost);
			}
			else{
				HashMap<Integer,Integer> hm=new HashMap<Integer,Integer>();
				hm.put(router.getId(), cost);
				map.put(id, hm);
			}
	}
	private class RouterConnecter{
		private Router dest;
		private int tick;
		
		public RouterConnecter(Router d){
			this.dest=d;
			tick=0;
		}
		public int getDest(){
			return this.dest.getId();
		}
		public int sent(LSPacket packet){
			int reply=dest.recievePacket(packet);
			if(reply==401){
				if(dest.recievePacket(packet)==401) return 999;
				else return 1;
			}else{
				return 1;
			}
		}
	}
	
	public int recievePacket(LSPacket packet){
		if(this.state.equals("stopped")) return 401;
		Integer rid=new Integer(packet.getId());
		if(packet.forward()){
			if(routingRecord.containsKey(rid)){
				if(routingRecord.get(rid)>=packet.getSeq()) return 302;
				else{
					updateMap(packet.getMap());
					routingRecord.put(rid, packet.getSeq());
					pushforward(packet);
					return 201;
				}
			}else{
				routingRecord.put(rid,packet.getSeq());
				updateMap(packet.getMap());
				pushforward(packet);
				return 201;
			}
		}
		else return 301;
	}
	
	private void updateMap(HashMap<Integer, HashMap<Integer,Integer>> nm){
		for(Integer i:nm.keySet()){
			if(map.containsKey(i)){
				for(Integer j:nm.get(i).keySet()) map.get(i).put(j, nm.get(i).get(j));
			}else map.put(i, nm.get(i));
		}
	}
	
	public void sendPacket(){
		if(state.equals("running")){
			LSPacket packet=new LSPacket(id,seq,map);
			seq++;
			for(RouterConnecter rc:rclist){
				System.out.println("sending packet from "+id+" to "+rc.dest.getId());
				if(rc.sent(packet)==999) map.get(id).put(rc.dest.getId(),999);
			}
		}
	}
	
	public void pushforward(LSPacket packet){
		for(RouterConnecter rc:rclist){
			if(rc.dest.getId()!=packet.getId()&&rc.dest.getId()!=packet.getLast()){
				System.out.println("send from "+id+" to "+rc.dest.getId());
				packet.setLast(id);
				if(rc.sent(packet)==999) map.get(id).put(rc.dest.getId(),999);
			}
		}
	}
	
	public void routing(){
		System.out.println(map.toString());
		paths.clear();
		minds.clear();
		ongoings.clear();
		System.out.println(minds.toString());
		for(int i:map.keySet()){
			if(i!=id){
				ongoings.put(i, i);
				if(!map.get(id).containsKey(i)) minds.put(i, 999);
				else minds.put(i, map.get(id).get(i).intValue());
			}
		}
		System.out.println(map.keySet().toString());
		System.out.println(minds.keySet().toString());
		while(!minds.isEmpty()){
			for(int i:minds.keySet()){
				if(!paths.isEmpty()) 
					for(int j: paths.keySet()){
						System.out.println(paths.keySet().toString());
						System.out.println(j);
						if(map.get(j).containsKey(i)) 
							if((map.get(j).get(i)+paths.get(j)[0])<minds.get(i)){
								minds.put(i, map.get(j).get(i)+paths.get(j)[0]);
								ongoings.put(i,paths.get(j)[1]);
							}
					}
			}
			int d=0;
			int[] path = new int[2];
			path[0]=9999;
			path[1]=0;
			for(int i:minds.keySet()){
				if(minds.get(i)<path[0]){
					d=i;
					path[0]=minds.get(i);
					path[1]=ongoings.get(i);
				}
			}
			paths.put(d,path);
			//System.out.println(d);
			minds.remove(d);
			ongoings.remove(d);
		}
	}
	
	public void shutdown(){
		this.state="stopped";
	}
	
	public void start(){
		this.state="running";
	}
	
	public void printPath(){
		routing();
		for(int i:paths.keySet()){
			System.out.println("routing path:"+i+" "+paths.get(i)[0]+" "+paths.get(i)[1]);
		}
	}
}
