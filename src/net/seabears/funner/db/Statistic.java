package net.seabears.funner.db;

public enum Statistic
{
  GROUP(3),
  SINGLE(4),
  TEMPERATURE(1),
  WEATHER(2);

  private final long id;

  private Statistic(long id)
  {
    this.id = id;
  }

  public long getId()
  {
    return id;
  }
}
