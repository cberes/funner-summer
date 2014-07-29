package net.seabears.funner.cache;

public interface ICache<K, V>
{
  CachedValue<V> read(K key);

  void write(K key, CachedValue<V> value);
}
