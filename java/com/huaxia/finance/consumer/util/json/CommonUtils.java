package com.huaxia.finance.consumer.util.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 常用方法工具类
 * 
 * @author shilei
 *
 */
public class CommonUtils {

	public static <T> boolean isNumber(T num) {
		if ((num instanceof Number)) {
			return true;
		}
		if ((num instanceof String)) {
			try {
				new BigDecimal((String) num);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public static <T> boolean isNull(T obj) {
		return null == obj;
	}
	
	public static <T> boolean isNotNull(T obj) {
		return null != obj;
	}
	
	public static <T> boolean isNullEmpty(T obj) {
		if (isNull(obj) || isEmpty(obj)){
			return true;
		}
		return false;
	}
	
	public static <T> boolean isNotNullEmpty(T obj) {
		return !isNullEmpty(obj);
	}
	
	public static <T> boolean isEmpty(T obj) {
		if (obj == null) {
			return true;
		}
		if ((obj instanceof String)) {
			String strObj = (String) obj;
			strObj = strObj.trim();
			return strObj.length() == 0 ? true : strObj.isEmpty() ? true : false;
		}
		if ((obj instanceof Collection)) {
			Collection<?> collectionObj = (Collection<?>) obj;
			return (collectionObj.size() <= 0)
					|| (collectionObj.isEmpty());
		}
		if ((obj instanceof Map)) {
			Map<?, ?> mapObje = (Map<?, ?>) obj;
			return (mapObje.size() <= 0)
					|| (mapObje.isEmpty());
		}
		if (obj.getClass().isArray()) {
			return (Array.getLength(obj) <= 0);
		}
		return false;
	}
	
//	public static <T> boolean isEmpty(T obj) {
//		if (obj == null) {
//			return true;
//		}
//		if ((obj instanceof String)) {
//			String strObj = (String) obj;
//			return Strings.isNullOrEmpty(strObj) ? true : Strings
//					.isNullOrEmpty(strObj.trim()) ? true : false;
//		}
//		if ((obj instanceof Collection)) {
//			Collection<?> collectionObj = (Collection<?>) obj;
//			return (collectionObj == null) || (collectionObj.size() <= 0)
//					|| (collectionObj.isEmpty());
//		}
//		if ((obj instanceof Map)) {
//			Map<?, ?> mapObje = (Map<?, ?>) obj;
//			return (mapObje == null) || (mapObje.size() <= 0)
//					|| (mapObje.isEmpty());
//		}
//		if (obj.getClass().isArray()) {
//			return (obj == null) || (Array.getLength(obj) <= 0);
//		}
//		return obj == null;
//	}

	public static <T> boolean isNotEmpty(T obj){
		return !isEmpty(obj);
	}
	
	public static byte[] InputStreamTOByte(InputStream in, int bufferSize)
			throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[bufferSize];
		int count = -1;
		while ((count = in.read(data, 0, bufferSize)) != -1) {
			outStream.write(data, 0, count);
		}
		data = null;
		return outStream.toByteArray();
	}

	public static boolean isTime(int sqlType) {
		return (92 == sqlType) || (93 == sqlType);
	}

	public static boolean isDate(int sqlType) {
		return 91 == sqlType;
	}



	public static Class<?> getJavaClassInner(String type) {
		if (type.equals("String")) {
			return String.class;
		}
		if (type.equals("Short")) {
			return Short.class;
		}
		if (type.equals("Integer")) {
			return Integer.class;
		}
		if (type.equals("Long")) {
			return Long.class;
		}
		if (type.equals("Double")) {
			return Double.class;
		}
		if (type.equals("Float")) {
			return Float.class;
		}
		if (type.equals("Byte")) {
			return Byte.class;
		}
		if ((type.equals("Char")) || (type.equals("Character"))) {
			return Character.class;
		}
		if (type.equals("Boolean")) {
			return Boolean.class;
		}
		if (type.equals("Date")) {
			return java.sql.Date.class;
		}
		if (type.equals("Time")) {
			return Time.class;
		}
		if (type.equals("DateTime")) {
			return Timestamp.class;
		}
		if (type.equals("Object")) {
			return Object.class;
		}
		if (type.equals("short")) {
			return Short.TYPE;
		}
		if (type.equals("int")) {
			return Integer.TYPE;
		}
		if (type.equals("long")) {
			return Long.TYPE;
		}
		if (type.equals("double")) {
			return Double.TYPE;
		}
		if (type.equals("float")) {
			return Float.TYPE;
		}
		if (type.equals("byte")) {
			return Byte.TYPE;
		}
		if (type.equals("char")) {
			return Character.TYPE;
		}
		if (type.equals("boolean")) {
			return Boolean.TYPE;
		}
		try {
			return loadClass(type);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Class<?> getJavaClass(String type) {
		int index = type.indexOf("[]");
		if (index < 0) {
			return getJavaClassInner(type);
		}
		String arrayString = "[";
		String baseType = type.substring(0, index);
		while ((index = type.indexOf("[]", index + 2)) >= 0) {
			arrayString = arrayString + "[";
		}
		Class<?> baseClass = getJavaClassInner(baseType);
		try {
			String baseName = "";
			if (!baseClass.isPrimitive()) {
				return loadClass(arrayString + "L" + baseClass.getName() + ";");
			}
			if ((baseClass.equals(Boolean.class))
					|| (baseClass.equals(Boolean.TYPE))) {
				baseName = "Z";
			} else if ((baseClass.equals(Byte.class))
					|| (baseClass.equals(Byte.TYPE))) {
				baseName = "B";
			} else if ((baseClass.equals(Character.class))
					|| (baseClass.equals(Character.TYPE))) {
				baseName = "C";
			} else if ((baseClass.equals(Double.class))
					|| (baseClass.equals(Double.TYPE))) {
				baseName = "D";
			} else if ((baseClass.equals(Float.class))
					|| (baseClass.equals(Float.TYPE))) {
				baseName = "F";
			} else if ((baseClass.equals(Integer.class))
					|| (baseClass.equals(Integer.TYPE))) {
				baseName = "I";
			} else if ((baseClass.equals(Long.class))
					|| (baseClass.equals(Long.TYPE))) {
				baseName = "J";
			} else if ((baseClass.equals(Short.class))
					|| (baseClass.equals(Short.TYPE))) {
				baseName = "S";
			}
			return loadClass(arrayString + baseName);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> result = null;
		try {
			result = Thread.currentThread().getContextClassLoader()
					.loadClass(name);
		} catch (ClassNotFoundException localClassNotFoundException) {
		}
		if (result == null) {
			result = Class.forName(name);
		}
		return result;
	}

	public static boolean isLow(char chr) {
		if ((chr > 'Z') || (chr < 'A')) {
			return true;
		}
		return false;
	}

	public static boolean isUpper(char chr) {
		if ((chr <= 'Z') && (chr >= 'A')) {
			return true;
		}
		return false;
	}

	public static boolean isUpperSecondChar(String chrs) {
		char chr = chrs.charAt(1);
		return isUpper(chr);
	}

	public static String underscoreName(String name) {
		StringBuilder result = new StringBuilder();
		if ((name != null) && (name.length() > 0)) {
			int idx = 0;
			if (isUpperSecondChar(name)) {
				result.append(name.substring(0, 2));
				idx = 2;
			} else {
				result.append(name.substring(0, 1).toLowerCase());
				idx = 1;
			}
			for (int i = idx; i < name.length(); i++) {
				String s = name.substring(i, i + 1);
				if (s.equals(s.toUpperCase())) {
					result.append("_");
					result.append(s.toLowerCase());
				} else {
					result.append(s);
				}
			}
		}
		return result.toString();
	}

	public static String derscoreColumnName(String columnName) {
		String[] colnames = columnName.split("_");
		StringBuilder result = new StringBuilder();
		result.append(colnames[0].trim().toLowerCase());
		if (colnames.length > 1) {
			for (int idx = 1; idx < colnames.length; idx++) {
				result.append(colnames[idx].substring(0, 1).toUpperCase());
				result.append(colnames[idx].substring(1).toLowerCase());
			}
		}
		return result.toString();
	}
	

	
	public static String printException(Throwable thr){
		if (thr == null) return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			thr.printStackTrace(new PrintStream(baos));
			baos.flush();
		} catch (Exception e){
			e.printStackTrace();
		}finally {
			if (baos != null)
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return baos.toString();
	}
	
	public static void main(String[] args) {
		String abc = null;
		String abcd = "";
		List<String> lstb = null;

		String[] arrya = null;
		String[] arrab = new String[0];
		List<String> arrayB = Arrays.asList(arrab);
		
	}
	
	private static long _flowID=1;	
	
	private static Lock lock = new ReentrantLock();//默认使用非公平锁
	
	public static String getFlowID()
	{
		 lock.lock();
		 String ordId ="";
		 try{
			Date date = new Date();
			SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMddHHmmss");	
			ordId =formatter.format(date)+  String.format("%0$05d", _flowID);
			_flowID++;
			 return ordId;
	 		}
		 catch(Exception e)	 
		 {
			 e.printStackTrace();
			 return ordId;
		 }
		 finally {
	        lock.unlock();        //锁必须在finally块中释放       
	 	}
	}
	

	
	private static String localIP="";
	/**
	 * 获取本机IP
	 * @return
	 */
	public static String getLocalIP()
	{
		if(localIP=="")
		{
			List<String> lstLocalIPs=getLocalIPList();
			if(lstLocalIPs.size()>0)
			{
				localIP=lstLocalIPs.get(0);
			}
		}
		return localIP;
	}
	
	/**
	 * 获取本机IP地址列表
	 * @return
	 */
	public static List<String> getLocalIPList() {
        List<String> ipList = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            String ip;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress != null && inetAddress instanceof Inet4Address) { // IPV4
                        ip = inetAddress.getHostAddress();
                        ipList.add(ip);
                    }
                }
            }
        } catch (SocketException e) {        	
            e.printStackTrace();
        }
        return ipList;
    }


}
