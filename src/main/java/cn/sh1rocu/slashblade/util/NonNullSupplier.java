package cn.sh1rocu.slashblade.util;

@FunctionalInterface
public interface NonNullSupplier<T> {
    T get();
}