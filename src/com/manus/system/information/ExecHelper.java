package com.manus.system.information;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecHelper
{
  public static String exec(String cmd)
  {
    try
    {
     String data = "";
       Process p = Runtime.getRuntime().exec(cmd);
      InputStream in = p.getInputStream();
      byte[] buff = new byte[1024];
      int leng = 0;
      while ((leng = in.read(buff)) >= 0) {
        data = data + new String(buff, 0, leng);
      }
      InputStream in2 = p.getErrorStream();
      while ((leng = in2.read(buff)) >= 0) {
        data = data + new String(buff, 0, leng);
      }
      return data;
    } catch (IOException ex) {
      Logger.getLogger(LinuxInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
    return "";
  }
}

/* Location:           /home/manus/Descargas/system kinformation/build/classes/
 * Qualified Name:     manus.system.information.ExecHelper
 * JD-Core Version:    0.6.2
 */
