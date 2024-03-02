package net.seabears.funner.db;

public enum SelectionMethod
{
  BALLAST(1),
  MANUAL(5),
  HISTORICAL(2),
  RANDOM(4),
  INACTIVE(3);

  private final long id;

  SelectionMethod(long id)
  {
    this.id = id;
  }

  public long getId()
  {
    return id;
  }
}
