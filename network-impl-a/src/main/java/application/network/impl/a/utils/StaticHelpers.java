package application.network.impl.a.utils;

import java.util.function.Function;

/**
 * Class supplying some static helper methods
 */
public class StaticHelpers
{
  public interface ThrowingCallable
  {
    Object run() throws Throwable;
  }

  public interface ThrowingRunnable
  {
    void run() throws Throwable;
  }

  @SuppressWarnings("unchecked")
  public static <T> T callSilent(ThrowingCallable runnable)
  {
    try
    {
      return (T) runnable.run();
    } catch (Throwable e)
    {
      if (e instanceof RuntimeException) throw (RuntimeException) e;
      else throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T suppress(ThrowingCallable runnable)
  {
    try
    {
      return (T) runnable.run();
    } catch (Throwable e)
    {
      e.printStackTrace();
      return null;
    }
  }

  public static void runSilent(ThrowingRunnable runnable)
  {
    callSilent(() ->
    {
      runnable.run();
      return null;
    });
  }

  /**
   * {@link Thread#sleep(long)} where the exceptions is being transformed
   * to a {@link RuntimeException}
   *
   * @param millis the amount of millis to sleep
   */
  public static void sleep(long millis)
  {

    runSilent(() -> Thread.sleep(millis));
  }

  /**
   * Helper to cast an object and do something with the casted version
   *
   * @param object   the source object
   * @param clazz    the target class into which the object will get casted
   * @param consumer a consumer to do something with the casted object
   * @param <T>      the cast type
   * @return the result of the consumer function or null if the cast failed
   */
  public static <T, R> R cast(Object object, Class<T> clazz, Function<T, R> consumer)
  {
    return cast(object, clazz, consumer, null);
  }

  /**
   * Helper to cast an object and do something with the casted version
   *
   * @param object       the source object
   * @param clazz        the target class into which the object will get casted
   * @param consumer     a consumer to do something with the casted object
   * @param defaultValue the name to return if the cast failed
   * @param <T>          the cast type
   * @return the result of the consumer function or the defaultvalue if the cast failed
   */
  public static <T, R> R cast(Object object, Class<T> clazz, Function<T, R> consumer, R defaultValue)
  {
    if (clazz.isInstance(object))
    {
      return consumer.apply(clazz.cast(object));
    }
    return defaultValue;
  }
}
