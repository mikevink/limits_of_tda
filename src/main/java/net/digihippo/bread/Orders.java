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

    public void cancel(
            final Account account,
            final int accountId,
            final int orderId,
            final int priceOfBread)
    {
        final int inx = find(accountId, orderId);
        if (-1 != inx)
        {
            final Order order = orders.remove(inx);
            account.deposit(order.qty * priceOfBread);
            events.orderCancelled(accountId, orderId);
        }
        else
        {
            events.orderNotFound(accountId, orderId);
        }
    }

    public void placeAsWholesale()
    {
        int total = 0;
        for (final Order order : orders)
        {
            total += order.qty;
        }
        events.placeWholesaleOrder(total);
    }

    public void onWholesaleOrder(final int quantity)
    {
        int remaining = quantity;
        while (0 < remaining && 0 < orders.size())
        {
            final Order order = orders.get(0);
            if (order.qty <= remaining)
            {
                remaining = remaining - order.qty;
                events.orderFilled(order.accountId, order.orderId, order.qty);
                order.qty = 0;
            }
            else
            {
                order.qty -= remaining;
                events.orderFilled(order.accountId, order.orderId, remaining);
                remaining = 0;
            }
            if(0 == order.qty) {
                orders.remove(0);
            }
        }
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

    // this is a data object, TDA doesn't apply
    private class Order
    {
        int accountId;
        int orderId;
        int qty;

        public Order(final int accountId, final int orderId, final int qty)
        {
            this.accountId = accountId;
            this.orderId = orderId;
            this.qty = qty;
        }

        boolean matches(int accountId, int orderId)
        {
            return this.accountId == accountId && this.orderId == orderId;
        }
    }
}
