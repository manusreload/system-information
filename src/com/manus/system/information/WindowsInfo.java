package com.manus.system.information;

public class WindowsInfo
{
  public static String getInfo()
  {
    return ExecHelper.exec("bin\\dmidecode.exe");
  }

  public static String getHDDInfo(String version) {
    return ExecHelper.exec("cscript //NoLogo bin\\harddiskinfo" + version.replaceAll(" ", "-") + ".vbs");
  }
}