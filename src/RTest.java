import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class RTest {
	HashMap<Integer,Router> rlist;
	
	public RTest(){
		rlist=new HashMap<Integer,Router>();
		LinkedList<String> indata=new LinkedList<String>();
		try{
			BufferedReader br=new BufferedReader(new FileReader(new File("infile.dat")));
			String line;
			while((line=br.readLine())!=null){
				indata.add(line);
				StringTokenizer st = new StringTokenizer(line);
				if(!line.startsWith(" ")){
					Router router=new Router(Integer.parseInt(st.nextToken()),st.nextToken());
					rlist.put(router.getId(), router);
				}
			}
			int idtmp=999;
			for(int i=0;i<indata.size();i++){
				StringTokenizer st = new StringTokenizer(indata.get(i));
				if(!indata.get(i).startsWith(" ")) idtmp=Integer.parseInt(st.nextToken());
				else if(st.countTokens()==2) rlist.get(idtmp).addDCR(rlist.get(Integer.parseInt(st.nextToken())),Integer.parseInt(st.nextToken()));
				else rlist.get(idtmp).addDCR(rlist.get(Integer.parseInt(st.nextToken())),1);
			}
			for(int i:rlist.keySet()) rlist.get(i).init();
			br.close();
		}catch(IOException e){
			System.out.println(e.toString());
		}
	}
	
	public static void main(String args[]){
		RTest rt=new RTest();
		System.out.println(rt.rlist.keySet().toString());
		Scanner in = new Scanner(System.in);
		String command;
		while(true){
			System.out.println("what do you want to do?");
			command=in.nextLine();
			StringTokenizer st = new StringTokenizer(command);
			if(command.equals("Q")){
				System.out.println("bye");
				break;
			}else if(command.equals("C")){
				for(int i:rt.rlist.keySet()) rt.rlist.get(i).sendPacket();
			}else if(command.equals("P")){
				System.out.println("which router you want?");
				int i=Integer.parseInt(in.nextLine());
				rt.rlist.get(i).printPath();
			}else if(command.equals("S")){
				System.out.println("which router you want?");
				int i=Integer.parseInt(in.nextLine());
				rt.rlist.get(i).shutdown();
			}else if(command.equals("T")){
				System.out.println("which router you want?");
				int i=Integer.parseInt(in.nextLine());
				rt.rlist.get(i).start();
			}else System.out.println("invalid command");
		}
	}
}
