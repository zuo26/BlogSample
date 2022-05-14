package com.mjzuo.location.util;

public class CommonUtil {

    /**
     *  来自stackoverflow的MD5计算方法，调用了MessageDigest库函数，并把byte数组结果转换成16进制
     * @param md5
     * @return
     */
    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    /**
     * 将String转换成bytes
     */
    public static byte[] stringToBytes(String s) {
        if (s != null) {
            try {
                return s.getBytes("UTF-8");
            } catch (Exception var2) {
                return new byte[0];
            }
        }
        return new byte[0];
    }
}
