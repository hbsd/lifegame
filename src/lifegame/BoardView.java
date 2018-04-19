package lifegame;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
class BoardView extends JPanel implements BoardListener {
	private static final int lineWidth  = 1;

	private final BoardModel model;
	private final JFrame frame;

	public BoardView(final BoardModel model, final JFrame frame) {
		this.model = model;
		this.frame = frame;
		final Mouse mouse = new Mouse(this);
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
	}

	@Override
	public void paint(final Graphics g) {
		super.paint(g);

		final int margin = getMargin();
		final int cellSize = getCellSize();

		g.setColor(Color.black);
		// vertical line
		for (int x = 0; x < model.getCols() + 1; ++x) {
			g.drawLine(
				margin + (cellSize + lineWidth) * x,
				margin,
				margin + (cellSize + lineWidth) * x,
				margin + (cellSize + lineWidth) * model.getRows()
			);
		}

		// horizontal line
		for (int y = 0; y < model.getRows() + 1; ++y) {
			g.drawLine(
				margin,
				margin + (cellSize + lineWidth) * y,
				margin + (cellSize + lineWidth) * model.getCols(),
				margin + (cellSize + lineWidth) * y
			);
		}

		// cells
		for (int x = 0; x < model.getCols(); ++x) {
			for (int y = 0; y < model.getRows(); ++y) {
				g.setColor(
					model.isAlive(x, y) ?
						Color.green : Color.gray
				);
				g.fillRect(
					convIndexToPosition(x), convIndexToPosition(y),
					cellSize, cellSize
				);
			}
		}
	}

	private int convIndexToPosition(final int index) {
		return getMargin() + lineWidth + index * (getCellSize() + lineWidth);
	}

	public int convPositionToIndex(final int position) {
		// Without '(double)', -1 is confused with 0.
		return (int)Math.floor(
			(double)(position - getMargin()) / (getCellSize() + lineWidth)
		);
	}

	// the margin of top or left end
	private int getMargin() {
		return (int)calcCellSize() / 4;
	}

	private int getCellSize() {
		return (int)calcCellSize();
	}

	private double calcCellSize() {
		final double frameSize = Math.min(frame.getHeight(), frame.getWidth());
		final double cellNum = Math.max(model.getRows(), model.getCols());
		final double ext_margin = 40; // margin of bottom and right end

		return ((frameSize - ext_margin) - (cellNum + 1) * lineWidth)
		     / (cellNum + 1);
	}

	public void changeCellState(final int x, final int y) {
		model.changeCellState(x, y);
	}

	@Override
	public void updated(BoardModel m) {
		repaint();
	}
}


