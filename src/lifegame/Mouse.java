package lifegame;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * 盤面のセルをマウスで操作するためのクラス．
 */
class Mouse implements MouseListener, MouseMotionListener {

	private final BoardView view;
	private int prevX;
	private int prevY;
	private boolean isPrevPressedOrDragged;

	Mouse(final BoardView view) {
		this.view = view;
		prevX = -1;
		prevY = -1;
		isPrevPressedOrDragged = false;
	}

	private void mousePressedOrDragged(MouseEvent e) {
		final int x = view.convPositionToIndex(e.getX());
		final int y = view.convPositionToIndex(e.getY());

		if (x != prevX || y != prevY || !isPrevPressedOrDragged) {
			view.changeCellState(x, y);
		}

		prevX = x;
		prevY = y;
		isPrevPressedOrDragged = true;
	}

	private void mouseOtherwise(MouseEvent e) {
		prevX = view.convPositionToIndex(e.getX());
		prevY = view.convPositionToIndex(e.getY());
		isPrevPressedOrDragged = false;
	}

	@Override
	public void mousePressed(MouseEvent e) { mousePressedOrDragged(e); }
	@Override
	public void mouseDragged(MouseEvent e) { mousePressedOrDragged(e); }
	@Override
	public void mouseClicked(MouseEvent e) { mouseOtherwise(e); }
	@Override
	public void mouseEntered(MouseEvent e) { mouseOtherwise(e); }
	@Override
	public void mouseExited(MouseEvent e) { mouseOtherwise(e); }
	@Override
	public void mouseReleased(MouseEvent e) { mouseOtherwise(e); }
	@Override
	public void mouseMoved(MouseEvent e) { mouseOtherwise(e); }
}