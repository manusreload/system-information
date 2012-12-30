package com.manus.system.information;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LinuxInfo
{
  static final Object stoper = new Object();
  static String result = "";

  public static String getInfo() {
    try {
      Process p = Runtime.getRuntime().exec("dmidecode");
      InputStream in = p.getInputStream();
      byte[] buff = new byte[1024];
      int leng = 0;
      String data = "";
      while ((leng = in.read(buff)) >= 0) {
        data = data + new String(buff, 0, leng);
      }
      return normalize(data);
    } catch (IOException ex) {
      Logger.getLogger(LinuxInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
    return "";
  }

  public static String getHDDInfo(String hdd) {
    try {
      Process p = Runtime.getRuntime().exec("fdisk -l " + hdd);
      InputStream in = p.getInputStream();
      byte[] buff = new byte[1024];
      int leng = 0;
      String data = "";
      while ((leng = in.read(buff)) >= 0) {
        data = data + new String(buff, 0, leng);
      }
      return data;
    } catch (IOException ex) {
      Logger.getLogger(LinuxInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
    return "";
  }

  public static String normalize(String input) {
    String result = "";
    int leng = input.indexOf("Processor Information");
    if (leng >= 0) {
      result = result + "# dmidecode\n";
      result = result + input + "\n";
      String proc = input.substring(leng + "Processor Information".length());
      result = result + "Informacion Procesador:\n";

      result = result + "\tVelocidad: " + getLine(proc, "Current Speed: ") + "\n";

      result = result + "\tVoltaje: " + getLine(proc, "Voltage: ") + "\n";

      leng = proc.indexOf("Socket Designation: L1 Cache");
      if (leng >= 0)
      {
        proc = proc.substring(leng);
        result = result + "\tTamaño cache L1: " + getLine(proc, "Maximum Size: ") + "\n";
      }

      leng = proc.indexOf("Socket Designation: L2 Cache");
      if (leng >= 0)
      {
        proc = proc.substring(leng);
        result = result + "\tTamaño cache L2: " + getLine(proc, "Maximum Size: ") + "\n";
      }

    }

    leng = input.indexOf("Base Board Information");
    if (leng >= 0)
    {
      result = result + "Informacion Placa Base:\n";
      String base = input.substring(leng + "Base Board Information".length());
      result = result + "\tFabricante: " + getLine(base, "Manufacturer: ") + "\n";
      result = result + "\tModelo: " + getLine(base, "Product Name: ") + "\n";
    }

    leng = input.indexOf("BIOS Information");
    if (leng >= 0)
    {
      result = result + "Informacion BIOS:\n";
      String bios = input.substring(leng + "BIOS Information".length());
      leng = bios.indexOf("Vendor: ");
      if (leng < 0) {
        return "";
      }
      String fabricante = getLine(bios, "Vendor: ");
      result = result + "\tFabricante: " + fabricante + "\n";
      result = result + "\tVersion: " + getLine(bios, "Version: ") + "\n";
      String sup = getLine(bios, "Boot from CD is ");
      if (sup.equals("supported"))
      {
        result = result + "\tArranque desde CD esta soportado\n";
      }
      else
      {
        result = result + "\tArranque desde CD no esta soportado\n";
      }
      sup = getLine(bios, "BIOS is ");
      if (sup.equals("upgradeable"))
      {
        result = result + "\tBIOS es actualizable\n";
      }
      else
      {
        result = result + "\tBIOS no es actualizable\n";
      }
    }

    leng = input.indexOf("Memory Device\n");
    if (leng >= 0)
    {
      result = result + "Memoria principal:\n";
      String ram = input.substring(leng);
      int numSlots = 0;
      int usedSlots = 0;
      while ((leng = ram.indexOf("Memory Device\n")) >= 0)
      {
        ram = ram.substring(leng + "Memory Device\n".length());
        numSlots++;
        String size = getLine(ram, "Size: ");
        if (!size.equals("No Module Installed"))
        {
          usedSlots++;
          result = result + "\tRanura: " + numSlots + "\n";
          ram = ram.substring(leng + "Memory Device".length());
          result = result + "\t\tTamaño: " + size + "\n";
          result = result + "\t\tVelocidad de reloj: " + getLine(ram, "Speed: ") + "\n";
        }
      }
      result = result + "\tNumero de módulos de memoria/usados: " + numSlots + "/" + usedSlots + "\n";
    }

    return result;
  }

  public static String getLine(String input, String prop) {
    int leng = input.indexOf(prop);
    if (leng < 0) {
      return "";
    }
    String res = input.substring(leng + prop.length());
    leng = res.indexOf("\n");
    res = res.substring(0, leng);
    return res;
  }

  static String benchmarkRAM() {
    String r = ExecHelper.exec("rm /dev/ram");
    r = r + ExecHelper.exec("mknod -m 660 /dev/ram b 1 16");
    r = r + ExecHelper.exec("dd if=/dev/zero of=/dev/ram bs=1K count=16K");

    return r;
  }
}

/* Location:           /home/manus/Descargas/system kinformation/build/classes/
 * Qualified Name:     manus.system.information.LinuxInfo
 * JD-Core Version:    0.6.2
 */