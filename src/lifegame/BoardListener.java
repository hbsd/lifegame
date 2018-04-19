package lifegame;

/**
 * BoardModel クラスの変化の通知を受け取るためのインターフェイス．
 */
public interface BoardListener {
	public void updated(final BoardModel m);
}
