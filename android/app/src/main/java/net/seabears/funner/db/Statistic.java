package net.seabears.funner.db;

public enum Statistic
{
  CROWD(3),
  TEMPERATURE(1),
  WEATHER(2);

  private final long id;

  Statistic(long id)
  {
    this.id = id;
  }

  public long getId()
  {
    return id;
  }
}
