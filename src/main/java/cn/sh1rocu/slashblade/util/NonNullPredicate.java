package cn.sh1rocu.slashblade.util;

@FunctionalInterface
public interface NonNullPredicate<T> {
    boolean test(T var1);
}
