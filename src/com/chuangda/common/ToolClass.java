package com.chuangda.common;

public class ToolClass {
	public String ClrSpace(String sstr){
		String str = null;
		str=sstr.replaceAll(" ", "");
		return str;
	}
	/* Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串�?   
	* @param src byte[] data   
    * @return hex string   
	*/      
	public static String bytesToHexString(byte[] src){  
    StringBuilder stringBuilder = new StringBuilder("");  
    if (src == null || src.length <= 0) {   
       return null;   
    }   
	    for (int i = 0; i < src.length; i++) {  
        int v = src[i] & 0xFF;   
        String hv = Integer.toHexString(v);   
	        if (hv.length() < 2) {   
            stringBuilder.append(0);  
       }   
	        stringBuilder.append(hv);   
	    }   
	    return stringBuilder.toString();   
    }
	
	public static int[] bytesToInts(byte[] src, int size) {
		if (src == null || src.length <= 0) {
			return null;
		}
		int len = src.length;
		if(size > 0 && size < src.length){
			len = size;
		}
		int[] ret = new int[len];
		for (int i = 0; i < len; i++) {
			int v = src[i] & 0xFF;
			ret[i] = v;
		}
		return ret;
	}
	
	 public static double HexstrToDouble(String Hexstr){
	    	int l=0;
	    	double result = 0;
	    	
	    	l = Hexstr.length();
	    	for(int i=0;l>0;l--,i++){

	    		//result +=Math.pow(16, l-1)*Integer.valueOf(Hexstr.charAt(l)+"");
	    		result +=Math.pow(16, l-1)*Integer.parseInt(""+Hexstr.charAt(i), 16);
	    	}
	    	return result;
	    }
	//判断字符串里是否已经有小数点
   public static boolean Hasnotdot(String str){
			 boolean result=false;
			 for(int i=0;i<str.length();i++){
				 if(str.indexOf(".")!=-1)
					 result = true;
			 }
			 return result;
			 
		 }
   public static byte[] hexStringToBytes(String hexString) {   
       if (hexString == null || hexString.equals("")) {   
       return null;   
        }   
	    hexString = hexString.toUpperCase();   
	    int length = hexString.length() / 2;   
	    char[] hexChars = hexString.toCharArray();   
       byte[] d = new byte[length];  
       for (int i = 0; i < length; i++) {   
       int pos = i * 2;  
       d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));   
       }   
	    return d;   
	    }    
   private static byte charToByte(char c) {   
		return (byte) "0123456789ABCDEF".indexOf(c);   
    }  

}
