package sjmhrp.io;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	
	static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public static void print(Object o) {
		System.out.print("["+getDate()+"] [INFO]: "+ (o==null?"null":(o.toString())));
	}

	public static void println(Object... s) {
		String out = s[0]==null?"null":(s[0].toString());
		for(int i = 1; i < s.length; i++) {
			out+=";"+(s[i]==null?"null":(s[i].toString()));
		}
		Log.print(out+"\n");
	}

	public static void warn(Object... s) {
		System.out.println("["+getDate()+"] [WARN]: " + s[0].toString());
	}

	public static void done() {
		System.out.print(" - Done\n");
	}

	public static void printError(Exception e) {
		e.printStackTrace();
	}

	static String getDate() {
		return dateFormat.format(new Date());
	}
}