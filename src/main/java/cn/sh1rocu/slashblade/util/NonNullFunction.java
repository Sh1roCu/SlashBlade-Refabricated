package cn.sh1rocu.slashblade.util;

@FunctionalInterface
public interface NonNullFunction<T, R> {
    R apply(T var1);
}