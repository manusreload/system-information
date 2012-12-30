package com.manus.system.information;

import com.sun.management.OperatingSystemMXBean;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class SystemInformation
{
  public static boolean DEBUG = false;
  static double totalLoops = 0.0D;

  public static void main(String[] args)
  {
    try
    {
      boolean do_benchmark_cpu = true;
      boolean do_benchmark_hdd = true;
      boolean do_benchmark_ram = true;

      if (contains(args, "-h"))
      {
        println("-c\tCalcular fuerza bruta de la CPU");
        println("-j\tNumero de hilos para calcular la fuerza bruta");
        println("-d\tCalcular velocidad de E/S del disco en el que este el programa");
        println("-r\tCalcular velocidad de E/S de la RAM");
        println("-l\tEstablecer la ruta de la librería");
        println("-v\tEstablecer mode verbose 0|1");
        return;
      }

      if ((contains(args, "-c")) || (contains(args, "-d")) || (contains(args, "-r")))
      {
        do_benchmark_cpu = false;
        do_benchmark_hdd = false;
        do_benchmark_ram = false;
      }
      if (contains(args, "-c"))
      {
        do_benchmark_cpu = true;
      }
      if (contains(args, "-d"))
      {
        do_benchmark_hdd = true;
      }
      if (contains(args, "-r"))
      {
        do_benchmark_ram = true;
      }
      String[] param = new String[2];
      if (getParameter(args, "-v", param))
      {
        if (param[1].equals("1"))
        {
          DEBUG = true;
        }
      }
      Properties properties;
      Iterator i$;
      if (DEBUG) {
        properties = System.getProperties();
        Set keys = properties.keySet();
        for (i$ = keys.iterator(); i$.hasNext(); ) { Object key = i$.next();
          System.out.println(new StringBuilder().append(key).append(":\t\t\t ").append(properties.get(key)).toString());
        }
      }
      String library = System.getProperty("java.library.path");
      String osname = System.getProperty("os.name");
      if (getParameter(args, "-l", param))
      {
        System.setProperty("java.library.path", param[1]);
      }
      else if (osname.equals("Linux"))
      {
        System.setProperty("java.library.path", new StringBuilder().append(library).append(":./lib:../lib").toString());
      }
      else
      {
        System.setProperty("java.library.path", new StringBuilder().append(library).append(";.\\lib").toString());
      }

      String arch = System.getProperty("os.arch");
      String version = System.getProperty("os.version");
      OperatingSystemMXBean bean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();

      long maxMem = bean.getTotalPhysicalMemorySize();
      long freeMem = bean.getFreePhysicalMemorySize();
      Sigar sigar = new Sigar();
      if (osname.contains("Linux"))
      {
        println(LinuxInfo.getInfo());
        FileSystem[] fsys = sigar.getFileSystemList();
        String name = fsys[0].getDevName();
        println(LinuxInfo.getHDDInfo(name.substring(0, name.length() - 1)));
        for (FileSystem fileSystem : fsys) {
          println(fileSystem.getDevName());
          println(LinuxInfo.getHDDInfo(fileSystem.getDevName()));
        }
      }
      else if (osname.contains("Windows"))
      {
        println(WindowsInfo.getInfo());
        FileSystem[] fsys = sigar.getFileSystemList();
        for (FileSystem fileSystem : fsys) {
          println(fileSystem.getDirName());
        }
        println(WindowsInfo.getHDDInfo(osname));
      }
      println("Info. Sistema:");
      int processors = bean.getAvailableProcessors();
      CpuInfo[] cpus = sigar.getCpuInfoList();
      if (cpus.length > 0) {
        CpuInfo cpuInfo = cpus[0];
        println(new StringBuilder().append("\t").append(cpuInfo.getVendor()).append(" ").append(cpuInfo.getModel()).append(" ").append(cpuInfo.getMhz()).append("MHz").toString());
        if ((cpuInfo.getTotalCores() != cpuInfo.getTotalSockets()) || (cpuInfo.getCoresPerSocket() > cpuInfo.getTotalCores()))
        {
          System.out.println(new StringBuilder().append("\tCPUs fisiscas\t\t").append(cpuInfo.getTotalSockets()).toString());
          System.out.println(new StringBuilder().append("\tNucleos por CPU\t\t").append(cpuInfo.getCoresPerSocket()).toString());
        }
      } else {
        println("\tNo se pudo recuperar la informacion de los núcleos");
      }
      println(new StringBuilder().append("\tProcesadores (Logicos): ").append(processors).toString());
      println(new StringBuilder().append("\tMemoria (Max/Free): ").append(humanReadableByteCount(maxMem)).append("/").append(humanReadableByteCount(freeMem)).toString());
      println(new StringBuilder().append("\tOS:            ").append(osname).toString());
      println(new StringBuilder().append("\tVersion:       ").append(version).toString());
      println(new StringBuilder().append("\tArquitectura:  ").append(arch).toString());
      println("Info. Disco duro:");

      println("Info. Conexiones de Red:");
      String[] netinterfaces = sigar.getNetInterfaceList();
      println(new StringBuilder().append("\tTotal:            ").append(netinterfaces.length).toString());
      for (String string : netinterfaces) {
        NetInterfaceConfig conf = sigar.getNetInterfaceConfig(string);
        println(new StringBuilder().append("\tInterfaz: ").append(string).toString());
        println(new StringBuilder().append("\t\tNombre: ").append(conf.getName()).toString());
        println(new StringBuilder().append("\t\tDescripcion: ").append(conf.getDescription()).toString());
        println(new StringBuilder().append("\t\tDestino: ").append(conf.getDestination()).toString());
        println(new StringBuilder().append("\t\tDireccion: ").append(conf.getAddress()).toString());
      }
      int jobs = 0;
      if ((contains(args, "-j")) && (getParameter(args, "-j", param)))
      {
        jobs = Integer.parseInt(param[1]);
      }
      else
      {
        jobs = processors == 1 ? 1 : processors - 1;
      }
      long loops = 1048576L;
      int fors = 64;
      final List list = new ArrayList();
      if (do_benchmark_cpu)
      {
        println(new StringBuilder().append("Haciendo benckmarck con ").append(humanReadable(1048576.0D, false, " loops")).append(" durante ").append(64).append(" bucles exponenciales en ").append(jobs).append(" hilos ...").toString());

        long nanotime = System.nanoTime();
        for (int m = 0; m < jobs; m++)
        {
          list.add(m, Boolean.valueOf(false));
          final int id = m;
          new Thread(new Runnable() {
            int mId = id;

            public void run() {
              float d = 0.0F;
              for (int j = 0; j < 64; j++)
              {
                double mLoops = 1048576L * (0x2 ^ j);

                for (double i = 0.0D; i < mLoops; i += 1.0D) {
                  d += 1.0F;
                }
              }
              list.set(this.mId, Boolean.valueOf(true));
              synchronized (list)
              {
                list.notifyAll();
              }
            }
          }).start();
        }

        while (true)
        {
          boolean wait = false;
          for (int i = 0; i < list.size(); i++)
          {
            if (!((Boolean)list.get(i)).booleanValue())
            {
              wait = true;
            }
          }
          if (!wait)
            break;
          synchronized (list)
          {
            try {
              list.wait();
            } catch (InterruptedException ex) {
              Logger.getLogger(SystemInformation.class.getName()).log(Level.SEVERE, null, ex);
              break;
            }

          }

        }

        long result = System.nanoTime() - nanotime;
        double d = 0.0D;
        for (int j = 0; j < jobs; j++)
        {
          for (int i = 0; i < 64; i++)
          {
            d += 1048576L * (0x2 ^ i);
          }

        }

        totalLoops = d;
        println(new StringBuilder().append("\tBucles Realizados: ").append(humanReadable(totalLoops, true, " loops")).append("(").append(totalLoops).append(")").toString());
        println(new StringBuilder().append("\tTiempo empleado: ").append(humanReadable(result, true, "nanoS")).toString());
        println(new StringBuilder().append("\tMFLOPS: ").append((float)totalLoops / (float)result / 0.0001D).toString());
        println(new StringBuilder().append("\tPuntuación: ").append((float)totalLoops / (float)result * 100.0F).toString());
      }
      if (do_benchmark_hdd)
      {
        long totalSize = 1073741824L;
        long write_size = 0L;
        println(new StringBuilder().append("Benckmarck del HDD. Escribiendo ").append(humanReadableByteCount(totalSize)).toString());

        File f = new File("temp.dat");
        try {
          long time = System.currentTimeMillis();

          FileOutputStream out = new FileOutputStream(f, false);
          byte[] buf = { 11, 61, 10, -4, 0, -39, 30, 1, 27, 24, 94, 84, -42, 115, 102, 47, -9, 89, 66, 45, -103, -12, 75, 49, 60, 57, -7, -83, -41, 40, 84, 64, -119, -10, 95, -112, 100, 80, 87, -44, -105, -12, 121, -116, 80, -118, -6, -111, -106, -115, -120, -70, 49, 109, 113, 92, 13, 107, -61, 12, -18, 39, -55, 1, 16, 8, 120, 121, -1, -113, 117, 39, 15, 45, 14, -41, -112, -107, 12, 76 };

          for (long l = 0L; l < totalSize; l += buf.length)
            try
            {
              out.write(buf);
              write_size += buf.length;
            } catch (IOException ex) {
              Logger.getLogger(SystemInformation.class.getName()).log(Level.SEVERE, null, ex);
            }
          try
          {
            out.close();
            println(new StringBuilder().append("\tTasa de escritura: ").append(humanReadableByteCount(write_size / ((System.currentTimeMillis() - time) / 1000L))).append("/s").toString());
          }
          catch (IOException ex) {
            Logger.getLogger(SystemInformation.class.getName()).log(Level.SEVERE, null, ex);
          }
        } catch (FileNotFoundException ex) {
          Logger.getLogger(SystemInformation.class.getName()).log(Level.SEVERE, null, ex);
        }
        File f2 = new File("temp.dat");

        println("Testeando la lectura...");
        long time = System.currentTimeMillis();
        totalSize = 0L;
        try
        {
          FileInputStream in = new FileInputStream(f2);
          int leng = 0;
          byte[] buf = new byte[1024];
          try {
            while ((leng = in.read(buf, 0, buf.length)) >= 0)
            {
              totalSize += leng;
            }
            println("\tTasa de lectura: " + humanReadableByteCount((int)((float)totalSize / (((float)(System.currentTimeMillis() - time) + 1.0F) / 1000.0F))) + "/s");

            in.close();
          } catch (IOException ex) {
            Logger.getLogger(SystemInformation.class.getName()).log(Level.SEVERE, null, ex);
          }
        } catch (FileNotFoundException ex) {
          Logger.getLogger(SystemInformation.class.getName()).log(Level.SEVERE, null, ex);
        }

        File f3 = new File("temp.dat");
        f3.delete();
      }

      if (do_benchmark_ram)
      {
        if (osname.equals("Linux"))
        {
          println("Calcuando velocidad de la RAM...");
          println(LinuxInfo.benchmarkRAM());
        }
        else
        {
          println(new StringBuilder().append(osname).append(" no esta soportado para el benchmark de RAM").toString());
        }
      }
      println("=====================================");
    } catch (SigarException ex) {
      Logger.getLogger(SystemInformation.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void println(String line)
  {
    System.out.println(line);
  }

  public static String humanReadableByteCount(long bytes) {
    return humanReadableByteCount(bytes, false);
  }

  public static String humanReadableByteCount(long bytes, boolean si) {
    return humanReadable(bytes, si, "B");
  }

  public static String humanReadable(double num, boolean si, String unit) {
    int _unit = si ? 1000 : 1024;
    if (num < _unit) {
      return new StringBuilder().append(num).append(" ").append(unit).toString();
    }
    int exp = (int)(Math.log(num) / Math.log(_unit));
    String pre = new StringBuilder().append((si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)).append(si ? "" : "i").toString();
    return String.format(new StringBuilder().append("%.1f %s").append(unit).toString(), new Object[] { Double.valueOf(num / Math.pow(_unit, exp)), pre });
  }

  public static boolean getParameter(String[] args, String param, String[] ret)
  {
    for (int i = 0; i < args.length; i++)
    {
      String str = args[i];

      if (str.equals(param))
      {
        ret[0] = param;
        if (i + 1 >= args.length)
        {
          return false;
        }

        ret[1] = args[(i + 1)];
        return true;
      }

    }

    return false;
  }

  public static boolean contains(String[] args, String param) {
    for (int i = 0; i < args.length; i++)
    {
      String str = args[i];

      if (str.equals(param))
      {
        return true;
      }
    }
    return false;
  }
}

/* Location:           /home/manus/Descargas/system kinformation/build/classes/
 * Qualified Name:     manus.system.information.SystemInformation
 * JD-Core Version:    0.6.2
 */