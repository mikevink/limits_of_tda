package net.digihippo.bread;

import java.util.function.Consumer;

public class TotUp
{
    private final OutboundEvents events;
    private int sum;

    public TotUp(final OutboundEvents events)
    {

        this.events = events;
    }

    void add(final int qty) {
        sum += qty;
    }

    void publish() {
        events.placeWholesaleOrder(sum);
    }
}
