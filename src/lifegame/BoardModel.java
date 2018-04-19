package lifegame;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * ライフゲームの盤面そのものを操作するクラス．
 */
public class BoardModel {
	private int cols;
	private int rows;
	private boolean[][] cells;

	private ArrayList<BoardListener> listeners;
	private ArrayDeque<boolean[][]> cellsHistory;

	private static final int numMaxUndoable = 32;

	/**
	 * コンストラクタ．
	 * 盤面は指定したサイズで作成される．
	 * @param  cols 盤面の横サイズ
	 * @param  rows 盤面の縦サイズ
	 */
	public BoardModel(final int cols, final int rows) {
		this.listeners = new ArrayList<>();
		initialize(cols, rows);
	}

	/**
	 * リスナーを登録する．
	 * リスナーは changeCellState，initialize，next，undo を呼び出したとき，
	 * 登録された順に呼び出される．
	 * @param listener [description]
	 */
	public void addListener(final BoardListener listener) {
		listeners.add(listener);
	}

	// 盤面を出力し，リスナーを呼び出す．
	private void fireUpdate() {
		printForDebug();
		for (final BoardListener listener: listeners) {
			listener.updated(this);
		}
	}

	// クラスの状態を変更するメソッド群

	/**
	 * 座標 (x,y) にあるセルの生死を反転させる．
	 * 反転前の盤面は巻き戻し用履歴に追加され，
	 * 反転後にはリスナーが呼び出される．
	 * @param x x 座標
	 * @param y y 座標
	 */
	public void changeCellState(final int x, final int y) {
		if (isInField(x, y)) {
			addHistory(copyCells(cells));
			changeCellState(x, y, cells);

			fireUpdate();
		}
	}

	/* 盤面 cs 上の座標 (x,y) にあるセルの生死を反転させる．
	 * 呼び出し前に (x,y) が盤面内にあるか確認しておくこと．
	 */
	private static void changeCellState(final int x, final int y, final boolean[][] cs) {
		cs[y][x] = !cs[y][x];
	}

	/**
	 * 盤面を指定したサイズで初期化する．
	 * 同時に盤面巻き戻しの履歴も削除されるが，リスナーは初期化されない．
	 * @param cols 新しい盤面の横サイズ
	 * @param rows 新しい盤面の縦サイズ
	 */
	public void initialize(final int cols, final int rows) {
		this.cols = cols;
		this.rows = rows;
		this.cells = new boolean[getRows()][getCols()];
		this.cellsHistory = new ArrayDeque<>();
		fireUpdate();
	}

	/**
	 * ゲームの世代を進める．
	 * 進行前の盤面は巻き戻し用履歴に追加され，
	 * 進行後にはリスナーが呼び出される．
	 */
	public void next() {
		final boolean[][] newCells = copyCells(cells);

		for (int y = 0; y < getRows(); ++y) {
			for (int x = 0; x < getCols(); ++x) {
				if (isCellToChange(x, y)) {
					changeCellState(x, y, newCells);
				}
			}
		}

		addHistory(cells);
		cells = newCells;

		fireUpdate();
	}

	// 盤面 cs (2次元配列) をディープコピーして返す．
	private static boolean[][] copyCells(final boolean[][] cs) {
		final boolean[][] newCells = new boolean[cs.length][];

		for (int i = 0; i < cs.length; ++i) {
			newCells[i] = cs[i].clone();
		}
		return newCells;
	}

	// 盤面 cs を巻き戻し用履歴に追加する．
	private void addHistory(final boolean[][] cs) {
		cellsHistory.addLast(cs);
		if (cellsHistory.size() > numMaxUndoable) {
			cellsHistory.pollFirst();
		}
	}

	/**
	 * 盤面の状態を巻き戻す．巻き戻せないなら何もしない．
	 * 巻き戻し後にはリスナーが呼び出される．
	 */
	public void undo() {
		if (isUndoable()) {
			cells = cellsHistory.removeLast();
			fireUpdate();
		}
	}

	// クラスの状態を変更しないメソッド群


	/**
	 * 盤面の横サイズを返す．
	 * @return 盤面の横サイズ
	 */
	public int getCols() {
		return cols;
	}

	/**
	 * 盤面の縦サイズを返す．
	 * @return 盤面の縦サイズ
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * 座標 (x,y) にあるセルが生きたセルかどうかを返す．
	 * 盤面外のセルは全て死んだセルとみなす．
	 * @param  x x 座標
	 * @param  y y 座標
	 * @return   座標 (x,y) のセルが生きたセルなら true．
	 */
	public boolean isAlive(final int x, final int y) {
		return (isInField(x, y) && cells[y][x]);
	}

	// 座標 (x,y) が盤面内にあるか判定する．
	private boolean isInField(final int x, final int y) {
		return (x >= 0 && x < getCols()) && (y >= 0 && y < getRows());
	}

	/**
	 * 盤面の状態を巻き戻せるかどうかを返す．
	 * @return 盤面の状態を巻き戻せるなら true．
	 */
	public boolean isUndoable() {
		return !cellsHistory.isEmpty();
	}

	// 座標 (x,y) にあるセルの生死が次の世代で変化するかを判定する．
	private boolean isCellToChange(final int x, final int y) {
		// 周囲8セルの生きたセルを数える．
		final int liveCount = (isAlive(x - 1, y - 1) ? 1 : 0)
		                    + (isAlive(x - 1, y    ) ? 1 : 0)
		                    + (isAlive(x - 1, y + 1) ? 1 : 0)
		                    + (isAlive(x,     y - 1) ? 1 : 0)
		                    + (isAlive(x,     y + 1) ? 1 : 0)
		                    + (isAlive(x + 1, y - 1) ? 1 : 0)
		                    + (isAlive(x + 1, y    ) ? 1 : 0)
		                    + (isAlive(x + 1, y + 1) ? 1 : 0);

		if (isAlive(x, y)) {
			return (liveCount != 2 && liveCount != 3);
		} else {
			return (liveCount == 3);
		}
	}

	// 盤面出力
	private void printForDebug() {
		for (boolean[] bs : cells) {
			for (boolean b : bs) {
				System.out.print(b ? '*' : '.');
			}
			System.out.println();
		}
		System.out.println();
		System.out.println(
			"numUndoable: " + cellsHistory.size()
		);
	}
}




