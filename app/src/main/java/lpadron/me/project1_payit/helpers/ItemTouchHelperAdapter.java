package lpadron.me.project1_payit.helpers;

/**
 * Project1-Payit
 * lpadron.me.project1_payit.helpers
 * Created by Luis Padron on 3/20/16, at 2:27 PM
 */
public interface ItemTouchHelperAdapter {

    /* Called when an item has been dragged */
    boolean onItemMove(int fromPosition, int toPosition);

    /* Called when an item has been swiped away */
    void onItemDismiss(int position);
}
