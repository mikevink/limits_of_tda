package net.digihippo.bread;

import java.util.HashMap;
import java.util.Map;

public class Account {
    private final int accountId;
    private final OutboundEvents events;
    private int balance = 0;
    private final Map<Integer, Integer> orders = new HashMap<>();

    public Account(final int id, final OutboundEvents events)
    {
        accountId = id;

        this.events = events;
    }

    public void deposit(int creditAmount) {
        balance += creditAmount;
        events.newAccountBalance(accountId, balance);
    }

    public void addOrder(int orderId, int amount, final int unitPrice) {
        int cost = amount * unitPrice;
        if (cost <= balance) {
            orders.put(orderId, amount);
            deposit(-cost);
            events.orderPlaced(accountId, amount);
        } else {
            events.orderRejected(accountId);
        }
    }

    public void cancelOrder(int orderId, final int unitPrice) {
        final Integer qty = orders.remove(orderId);
        if (null != qty) {
            deposit(qty * unitPrice);
            events.orderCancelled(accountId, orderId);
        } else {
            events.orderNotFound(accountId, orderId);
        }
    }

    public void totUpOrders(final TotUp beanCounter)
    {
        orders.values().forEach(beanCounter::add);
    }
}
