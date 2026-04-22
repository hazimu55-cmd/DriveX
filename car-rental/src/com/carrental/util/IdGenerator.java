package com.carrental.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe ID generator for all domain objects.
 * Uses separate counters per entity type.
 */
public final class IdGenerator {

    private IdGenerator() {}

    private static final AtomicLong userCounter    = new AtomicLong(1);
    private static final AtomicLong carCounter     = new AtomicLong(1);
    private static final AtomicLong bookingCounter = new AtomicLong(1);
    private static final AtomicLong paymentCounter = new AtomicLong(1);

    public static Long nextUserId()    { return userCounter.getAndIncrement(); }
    public static Long nextCarId()     { return carCounter.getAndIncrement(); }
    public static Long nextBookingId() { return bookingCounter.getAndIncrement(); }
    public static Long nextPaymentId() { return paymentCounter.getAndIncrement(); }
}
