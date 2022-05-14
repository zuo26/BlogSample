package com.mjzuo.location.net;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

    private static final int BUF_SIZE = 4096;

    public static byte[] toBytes(InputStream is) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            byte[] buf = new byte[BUF_SIZE];
            int count;
            while ((count = is.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, count);
            }
            baos.flush();
            return baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            safeClose(baos);
        }
        return null;
    }

    private static void safeClose(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
