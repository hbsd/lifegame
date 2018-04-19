package lifegame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * ライフゲームのウインドウを作成するクラス．
 */
public class Main implements Runnable {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Main());
	}

	@Override
	public void run() {
		// まず最初は，縦横サイズ 10 の盤面のウインドウを作成する．
		makeWindow(10, 10);
	}

	// 新しいウインドウを作成する．
	private static void makeWindow(final int cols, final int rows) {

		final JFrame frame = new JFrame();
		frame.setTitle("Lifegame");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setMinimumSize(new Dimension(500, 400));

		final JPanel base = new JPanel();
		base.setPreferredSize(new Dimension(500, 400));
		base.setLayout(new BorderLayout());
		frame.setContentPane(base);

		final BoardModel model = new BoardModel(cols, rows);
		final BoardView view = new BoardView(model, frame);
		model.addListener(view);
		base.add(view, BorderLayout.CENTER);

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		addButtons(cols, rows, model, buttonPanel);
		base.add(buttonPanel, BorderLayout.SOUTH);


		frame.pack();
		frame.setVisible(true);
	}

	// 各種ボタン，スピナーを buttonPanel に追加する．
	private static void addButtons(final int cols, final int rows, final BoardModel model, final JPanel buttonPanel) {
		// undo ボタン
		final JButton undoBtn = new JButton("undo");
		undoBtn.setEnabled(model.isUndoable());

		undoBtn.addActionListener(
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// ボタンが押されると盤面を巻き戻す．
					model.undo();
				}
			}
		);

		model.addListener(
			new BoardListener() {
				@Override
				public void updated(BoardModel m) {
					// 巻き戻せなくなったらボタンを無効にする．
					undoBtn.setEnabled(m.isUndoable());
				}
			}
		);
		buttonPanel.add(undoBtn);

		// next ボタン
		final JButton nextBtn = new JButton("next");
		nextBtn.addActionListener(
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// ボタンが押されると盤面の世代を進める．
					model.next();
				}
			}
		);
		buttonPanel.add(nextBtn);

		// auto トグルボタン
		final Timer timer = new Timer(
			500, // 間隔は 500 ミリ秒
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// 一定間隔で盤面の世代を進める．
					model.next();
				}
			}
		);

		final JToggleButton autoToggle = new JToggleButton("auto");
		autoToggle.addChangeListener(
			new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					// トグルがオンのときはタイマーを有効にする．
					// オフのときは停止する．
					if (autoToggle.isSelected()) {
						timer.start();
					} else {
						timer.stop();
					}
				}
			}
		);
		buttonPanel.add(autoToggle);

		final int maxBoardSize = 20; // 指定できる盤面サイズの最大値
		final int minBoardSize = 10; // 指定できる盤面サイズの最小値
		// columns スピナ
		final JSpinner numCols = new JSpinner(
			new SpinnerNumberModel(cols, minBoardSize, maxBoardSize, 1)
		);
		buttonPanel.add(numCols);

		// rows スピナ
		final JSpinner numRows = new JSpinner(
			new SpinnerNumberModel(rows, minBoardSize, maxBoardSize, 1)
		);
		buttonPanel.add(numRows);

		// reset ボタン
		final JButton resetBtn = new JButton("reset");
		resetBtn.addActionListener(
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// ボタンが押されると，
					// 盤面をスピナーの値で初期化し，
					model.initialize((int)numCols.getValue(), (int)numRows.getValue());
					// 自動世代進行を停止する．
					autoToggle.setSelected(false);
					timer.stop();
				}
			}
		);
		buttonPanel.add(resetBtn);

		// new-game ボタン
		final JButton newgameBtn = new JButton("new game");
		newgameBtn.addActionListener(
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// ボタンが押されると新しいウインドウでゲームを始める．
					makeWindow((int)numCols.getValue(), (int)numRows.getValue());
				}
			}
		);
		buttonPanel.add(newgameBtn);
	}
}
