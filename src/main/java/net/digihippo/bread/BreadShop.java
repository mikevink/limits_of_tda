package net.digihippo.bread;

import java.util.HashMap;
import java.util.Map;

public class BreadShop
{
    private static final int PRICE_OF_BREAD = 12;

    private final OutboundEvents events;
    private final Map<Integer, Account> accounts = new HashMap<>();
    private final Orders orders;

    public BreadShop(OutboundEvents events)
    {
        this.events = events;
        orders = new Orders(events);
    }

    public void createAccount(int id)
    {
        accounts.put(id, new Account(id, events, orders));
        events.accountCreatedSuccessfully(id);
    }

    public void deposit(int accountId, int creditAmount)
    {
        final Account account = accounts.get(accountId);
        if (null != account)
        {
            account.deposit(creditAmount);
        }
        else
        {
            events.accountNotFound(accountId);
        }
    }

    public void placeOrder(int accountId, int orderId, int amount)
    {
        final Account account = accounts.get(accountId);
        if (null != account)
        {
            account.addOrder(orderId, amount, PRICE_OF_BREAD);
        }
        else
        {
            events.accountNotFound(accountId);
        }
    }

    public void cancelOrder(int accountId, int orderId)
    {
        final Account account = accounts.get(accountId);
        if (null != account)
        {
            orders.cancel(account, accountId, orderId, PRICE_OF_BREAD);
        }
        else
        {
            events.accountNotFound(accountId);
        }
    }

    public void placeWholesaleOrder()
    {
        orders.placeAsWholesale();
    }

    public void onWholesaleOrder(int quantity)
    {
        orders.onWholesaleOrder(quantity);
    }
}
