package observer;

/**
 * 서브젝트 인터페이스 (옵저버 패턴)
 */
public interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String event, String message);
}