package net.seabears.funner.cache;

public interface ILocalCache<K, V>
{
  V read(K key);

  void write(K key, V value);
}
