package log;


/** Use the following line at the top of each source code file:
import static commonfuncs.CommonFuncs.*;
<br><br>
This is more like functional programming than object oriented.
*/
public class Lg{
	private Lg(){}
	
	public static void lgNobr(String s){
		System.out.print(s);
	}
	
	public static void lg(Object o){
		lg(o.toString()); //could have ""+o but thats slower
	}
	
	public static void lg(String line){
		System.out.println(line);
	}
	
	public static void lgErr(Object o){
		lgErr(o.toString()); //could have ""+o but thats slower
	}
	
	public static void lgErr(String line){
		System.err.println(line);
	}
	
	public static void lgToUser(Object o){
		lgToUser(o.toString()); //could have ""+o but thats slower
	}
	
	public static void lgToUser(String line){
		System.err.println(line);
	}
	
	/** Since JSelfModify doesnt have a User system yet, just room for expansion,
	this function uses JSelfModify.rootUser as is normally done.
	WARNING: This kind of thing will need to be redesigned securely when we start having usernames.
	*
	public static Object jsmGet(String path){
		try{
			return JSelfModify.root.get(JSelfModify.rootUser, path);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	/** See WARNING in jsmGet *
	public static void jsmPut(String path, Object value){
		try{
			JSelfModify.root.put(JSelfModify.rootUser, path, value);
		}catch(Exception e){
			throw new RuntimeException(e);
		}	
	}
	
	/** See WARNING in jsmGet *
	public static boolean jsmExist(String path){
		try{
			return JSelfModify.root.exist(JSelfModify.rootUser, path);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}*/
	
	/** returns a debug string summarizing a pointer into an acyc as an object *
	public static String str(int pointerIntoAcyc){
		return XobUtil.describeGlobal(pointerIntoAcyc);
	}*/


}