package cool;
import java.util.*;

public class ClassIR {
	public String parent = null;
	public int depth =0;
	public List<String> aList;
	public HashMap<String, AST.attr> atrs;
	public HashMap <String, AST.method> mtds;
	public HashMap <String, String> methodIR;
	public int size = 0;

	ClassIR(String par,HashMap<String,AST.attr> a,HashMap<String,AST.method> m,HashMap<String,String> mIR,int d,int s){
		parent=par;
		depth=d;
		size=s;
		aList=new ArrayList<String>();
		if(par!=null){
			aList.add("_Par");
		}
		atrs=new HashMap<String,AST.attr>();
		atrs.putAll(a);
		mtds=new HashMap<String,AST.method>();
		mtds.putAll(m);
		methodIR=new HashMap<String,String>();
		methodIR.putAll(mIR);
	}
}