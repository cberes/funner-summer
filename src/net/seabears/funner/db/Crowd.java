package net.seabears.funner.db;

public enum Crowd
{
  SINGLE("S"),
  COUPLE("C"),
  GROUP("G");

  private final String code;

  private Crowd(String code)
  {
    this.code = code;
  }

  public String getCode()
  {
    return code;
  }

  public static Crowd fromString(String s)
  {
    for (Crowd crowd : values())
    {
      if (crowd.code.equalsIgnoreCase(s))
      {
        return crowd;
      }
    }
    throw new IllegalArgumentException("Value " + s + " is not a valid code.");
  }
}
