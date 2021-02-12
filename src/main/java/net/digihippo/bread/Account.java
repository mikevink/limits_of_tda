package net.digihippo.bread;

public class Account
{
    private final int accountId;
    private final OutboundEvents events;
    private final Orders orders;
    private int balance = 0;

    public Account(
            final int id,
            final OutboundEvents events,
            final Orders orders)
    {
        accountId = id;

        this.events = events;
        this.orders = orders;
    }

    public void deposit(int creditAmount)
    {
        balance += creditAmount;
        events.newAccountBalance(accountId, balance);
    }

    public void addOrder(int orderId, int amount, final int unitPrice)
    {
        int cost = amount * unitPrice;
        if (cost <= balance)
        {
            deposit(-cost);
            orders.put(accountId, orderId, amount);
        }
        else
        {
            events.orderRejected(accountId);
        }
    }
}
