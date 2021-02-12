package net.digihippo.bread;

import java.util.ArrayList;
import java.util.List;

public class Orders
{
    private final OutboundEvents events;
    private List<Order> orders;

    public Orders(final OutboundEvents events)
    {
        this.events = events;
        orders = new ArrayList<>();
    }

    public void put(final int accountId, final int orderId, final int amount)
    {
        orders.add(new Order(accountId, orderId, amount));
        events.orderPlaced(accountId, amount);
    }

    public void remove(final int accountId, final int orderId)
    {

    }

    public void cancel(
            final Account account,
            final int accountId,
            final int orderId,
            final int priceOfBread)
    {
        final int inx = find(accountId, orderId);
        if (-1 != inx)
        {
            orders.remove(inx).refund(account, priceOfBread);
            events.orderCancelled(accountId, orderId);
        }
        else
        {
            events.orderNotFound(accountId, orderId);
        }
    }

    public void placeAsWholesale()
    {
        final TotUp totUp = new TotUp(events);
        orders.forEach(o -> o.toWholesale(totUp));
        totUp.publish();
    }

    private int find(final int accountId, final int orderId)
    {
        for (int i = 0; i < orders.size(); i++)
        {
            if (orders.get(i).matches(accountId, orderId))
            {
                return i;
            }
        }
        return -1;
    }

    private class Order
    {
        private final int accountId;
        private final int orderId;
        private final int qty;

        public Order(final int accountId, final int orderId, final int qty)
        {
            this.accountId = accountId;
            this.orderId = orderId;
            this.qty = qty;
        }

        public void refund(final Account account, final int priceOfBread)
        {
            account.deposit(qty * priceOfBread);
        }

        public void toWholesale(final TotUp totUp)
        {
            totUp.add(qty);
        }

        boolean matches(int accountId, int orderId)
        {
            return this.accountId == accountId && this.orderId == orderId;
        }
    }
}
