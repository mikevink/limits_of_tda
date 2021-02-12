package net.digihippo.bread;

public interface OrderEvents
{
    void place(int accountId, int orderId, int amount);
}
