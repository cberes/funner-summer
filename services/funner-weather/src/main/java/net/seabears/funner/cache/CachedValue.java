package net.seabears.funner.cache;

import java.util.concurrent.TimeUnit;

public class CachedValue<T>
{
  private final T value;
  private final long created;
  private final long expires;

  public CachedValue(T value, long ttl, TimeUnit unit)
  {
    this.value = value;
    this.created = System.currentTimeMillis();
    this.expires = this.created + unit.toMillis(ttl);
  }

  public T getValue()
  {
    return value;
  }

  public long getCreated()
  {
    return created;
  }

  public boolean isAlive()
  {
    return System.currentTimeMillis() <= expires;
  }
}
