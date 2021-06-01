package deviceHandler;

public interface HandlerRouter<T> {
    T getHandler(Integer env,Object... args);
}
